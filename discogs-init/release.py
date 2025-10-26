import gzip
import threading
import queue
import xml.etree.ElementTree as ET
from datetime import datetime
from typing import Optional, List, Tuple

import psycopg2
from psycopg2.extras import execute_values

# ----------------------------
# DB 접속 정보
# ----------------------------
DB = dict(
    host="localhost",
    port=5432,
    dbname="lpick",
    user="postgres",
    password="root",
)

XML_PATH = "discogs_20251001_releases.xml.gz"

# 튜닝 포인트
BATCH_SIZE = 5000       # Writer가 모아서 넣는 건수(크게 갈수록 빠름)
PAGE_SIZE = 5000        # execute_values page_size
QUEUE_MAXSIZE = 20000   # 큐 버퍼
LOG_EVERY = 20000

# ----------------------------
# LPTI 규칙(간단 스타터)
# ----------------------------
TAG_WEIGHTS = {
    "ambient": {"C": 2, "I": 2, "X": 1},
    "idm": {"C": 2, "M": 2, "I": 1, "X": 2},
    "minimal": {"C": 1, "I": 1, "X": 1},
    "prog rock": {"C": 1, "A": 1, "X": 2},
    "fusion": {"C": 1, "X": 1},
    "jazz": {"C": 1, "S": 1},
    "soul": {"E": 2, "A": 1, "V": 1, "S": 1},
    "r&b": {"E": 2, "M": 1, "V": 1, "S": 1},
    "pop": {"E": 1, "M": 1, "V": 2, "S": 1},
    "ballad": {"E": 2, "S": 2},
    "folk": {"E": 2, "A": 1, "I": 1, "S": 1},
    "city pop": {"E": 1, "A": 1, "V": 1, "S": 1},
    "lo-fi": {"E": 1, "A": 1, "I": 1},
    "house": {"M": 2, "V": 2},
    "edm": {"M": 2, "V": 2},
    "hyperpop": {"M": 2, "V": 2, "X": 1},
    "dream pop": {"E": 1, "I": 1, "X": 1},
    "new age": {"C": 1, "I": 1, "S": 1},
    "classical": {"C": 2, "I": 1, "S": 1},
    "indie rock": {"E": 1, "X": 1},
}

# ----------------------------
# 유틸
# ----------------------------
def trunc(s: Optional[str], limit: int) -> Optional[str]:
    return s[:limit] if s else None

def parse_released(t: Optional[str]) -> Optional[datetime]:
    if not t:
        return None
    t = t.strip()
    for fmt in ("%Y-%m-%d", "%Y-%m", "%Y"):
        try:
            d = datetime.strptime(t, fmt)
            if fmt == "%Y-%m-%d":
                return d
            if fmt == "%Y-%m":
                return datetime(d.year, d.month, 1)
            return datetime(d.year, 1, 1)
        except ValueError:
            continue
    return None

def extract_images(elem: ET.Element) -> Tuple[Optional[str], Optional[str]]:
    images = elem.find("images")
    if images is None:
        return None, None
    first_uri = first_uri150 = None
    primary_uri = primary_uri150 = None
    for img in images.findall("image"):
        uri = img.attrib.get("uri")
        uri150 = img.attrib.get("uri150")
        if first_uri is None:
            first_uri, first_uri150 = uri, uri150
        if img.attrib.get("type") == "primary":
            primary_uri = uri or primary_uri
            primary_uri150 = uri150 or primary_uri150
    return (primary_uri or first_uri, primary_uri150 or first_uri150)

def extract_label_name(elem: ET.Element) -> Optional[str]:
    labels = elem.find("labels")
    if labels is None:
        return None
    first = labels.find("label")
    if first is not None:
        name = first.attrib.get("name")
        if name:
            return name.strip()
    return None

def collect_genres_styles(elem: ET.Element) -> List[str]:
    tags: List[str] = []
    g = elem.find("genres")
    if g is not None:
        for x in g.findall("genre"):
            if x.text:
                tags.append(x.text.strip().lower())
    s = elem.find("styles")
    if s is not None:
        for x in s.findall("style"):
            if x.text:
                tags.append(x.text.strip().lower())
    return tags

def calc_lpti(tags: List[str], year: Optional[int]) -> str:
    sc = {"E": 0, "C": 0, "A": 0, "M": 0, "I": 0, "V": 0, "X": 0, "S": 0}
    for t in tags:
        if t in TAG_WEIGHTS:
            for k, v in TAG_WEIGHTS[t].items():
                sc[k] += v
    if year is not None:
        if year <= 1985:
            sc["A"] += 1
        elif year >= 2000:
            sc["M"] += 1
    def choose(pos: str, neg: str) -> str:
        return pos if sc[pos] >= sc[neg] else neg
    ec = choose("E", "C")
    am = choose("A", "M")
    iv = choose("I", "V")
    xs = choose("X", "S")
    last = "E" if xs == "X" else "S"
    return f"{ec}{am}{iv}{last}"

# ----------------------------
# DB Writer (consumer)
# ----------------------------
INSERT_SQL = """
INSERT INTO album
    (album_id, name, profile, release_date, release_country, label, wiki_id, lpti)
VALUES %s
ON CONFLICT (album_id) DO NOTHING;
"""

def writer_thread(q: "queue.Queue[Optional[tuple]]", stats: dict):
    conn = psycopg2.connect(**DB)
    try:
        conn.autocommit = False
        with conn.cursor() as cur:
            # 세션 튜닝: 동기 커밋 끄기 → 대량 삽입 가속
            cur.execute("SET LOCAL synchronous_commit TO off;")

            batch = []
            while True:
                item = q.get()
                if item is None:  # sentinel
                    break
                batch.append(item)

                if len(batch) >= BATCH_SIZE:
                    execute_values(cur, INSERT_SQL, batch, page_size=PAGE_SIZE)
                    conn.commit()
                    stats["inserted"] += len(batch)
                    if stats["inserted"] % LOG_EVERY == 0:
                        print(f"✅ inserted={stats['inserted']:,} processed={stats['seen']:,}")
                    batch.clear()

            # drain
            if batch:
                execute_values(cur, INSERT_SQL, batch, page_size=PAGE_SIZE)
                conn.commit()
                stats["inserted"] += len(batch)
                print(f"✅ inserted(final)={stats['inserted']:,} processed={stats['seen']:,}")
    finally:
        conn.close()

# ----------------------------
# Producer (parser)
# ----------------------------
def run():
    qrows: "queue.Queue[Optional[tuple]]" = queue.Queue(maxsize=QUEUE_MAXSIZE)
    stats = {"seen": 0, "inserted": 0}

    wt = threading.Thread(target=writer_thread, args=(qrows, stats), daemon=True)
    wt.start()

    print("⏳ Parsing and queuing from:", XML_PATH)
    with gzip.open(XML_PATH, "rb") as f:
        context = ET.iterparse(f, events=("end",))
        for event, elem in context:
            if elem.tag != "release":
                continue

            stats["seen"] += 1

            album_id = elem.attrib.get("id")
            if not album_id:
                elem.clear(); continue

            title_el = elem.find("title")
            name = (title_el.text or "").strip() if title_el is not None else None
            if not name:
                elem.clear(); continue

            released_el = elem.find("released")
            release_dt = parse_released(released_el.text if released_el is not None else None)
            year = release_dt.year if release_dt else None

            country_el = elem.find("country")
            release_country = trunc(country_el.text.strip(), 50) if (country_el is not None and country_el.text) else None

            img_uri, _ = extract_images(elem)
            profile = trunc(img_uri, 200) if img_uri else None

            label_name = trunc(extract_label_name(elem), 50)

            tags = collect_genres_styles(elem)
            lpti_code = calc_lpti(tags, year)

            row = (
                str(album_id),
                trunc(name, 100),
                profile,
                release_dt,
                release_country,
                label_name,
                None,           # wiki_id = NULL
                lpti_code,
            )
            qrows.put(row)  # 큐가 가득 차면 back-pressure로 자연스러운 스로틀링

            if stats["seen"] % LOG_EVERY == 0:
                print(f"… processed={stats['seen']:,} queued")

            elem.clear()  # 메모리 누수 방지

    # 종료 신호
    qrows.put(None)
    wt.join()

    print(f"🎉 Done. processed={stats['seen']:,}, inserted={stats['inserted']:,}")

if __name__ == "__main__":
    run()