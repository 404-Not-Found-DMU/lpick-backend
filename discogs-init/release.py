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
    host="",
    port=5432,
    dbname="lpick",
    user="postgres",
    password="",
)

XML_PATH = "discogs_20251001_releases.xml.gz"

# 튜닝 포인트
BATCH_SIZE = 5000      # Writer가 모아서 넣는 건수(크게 갈수록 빠름)
PAGE_SIZE = 5000       # execute_values page_size
QUEUE_MAXSIZE = 20000  # 큐 버퍼
LOG_EVERY = 20000

# ----------------------------
# 테이블 생성 SQL
# ----------------------------
CREATE_WIKI_TABLE_SQL = """
CREATE TABLE IF NOT EXISTS wiki_page (
    wiki_id            varchar(40)    NOT NULL PRIMARY KEY,
    title              varchar(50)    NOT NULL,
    current_revision   varchar(50)    NULL,
    status             varchar(10)    NOT NULL,
    class              varchar(10)    NOT NULL,
    -- ★ 추가된 부분: 랜덤 조회용 컬럼 (자동 생성)
    random_point       DOUBLE PRECISION DEFAULT random() NOT NULL
);
"""

CREATE_REVISION_TABLE_SQL = """
CREATE TABLE IF NOT EXISTS page_revision (
    revision_id        varchar(40)    NOT NULL PRIMARY KEY,
    content            jsonb          NOT NULL,
    revision_number    varchar(50)    NOT NULL,
    created_at         timestamp      NOT NULL,
    wiki_id            varchar(40)    NOT NULL,
    oauth_id           varchar(40)    NOT NULL
);
"""

# ----------------------------
# 데이터 삽입 SQL
# ----------------------------
WIKI_INSERT_SQL = """
INSERT INTO wiki_page
    (wiki_id, title, current_revision, status, class)
VALUES %s
ON CONFLICT (wiki_id) DO NOTHING;
"""

REVISION_INSERT_SQL = """
INSERT INTO page_revision
    (revision_id, content, revision_number, created_at, wiki_id, oauth_id)
VALUES %s
ON CONFLICT (revision_id) DO NOTHING;
"""

ALBUM_INSERT_SQL = """
INSERT INTO album
    (album_id, name, profile, release_date, release_country, label, wiki_id, lpti)
VALUES %s
ON CONFLICT (album_id) DO NOTHING;
"""


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
def writer_thread(q: "queue.Queue[Optional[tuple]]", stats: dict):
    conn = psycopg2.connect(**DB)
    try:
        conn.autocommit = False
        with conn.cursor() as cur:
            # 테이블 생성 (없으면)
            print("INFO: Checking/Creating tables...")
            cur.execute(CREATE_WIKI_TABLE_SQL)
            cur.execute(CREATE_REVISION_TABLE_SQL)
            conn.commit()
            
            # 세션 튜닝: 동기 커밋 끄기 → 대량 삽입 가속
            cur.execute("SET LOCAL synchronous_commit TO off;")

            batch_bundles = []
            while True:
                bundle = q.get()
                if bundle is None:  # sentinel
                    break
                
                batch_bundles.append(bundle)

                if len(batch_bundles) >= BATCH_SIZE:
                    # 묶음 풀기
                    wiki_batch = [b[0] for b in batch_bundles]
                    revision_batch = [b[1] for b in batch_bundles]
                    album_batch = [b[2] for b in batch_bundles]

                    # 순서대로 삽입 (Wiki -> Revision -> Album)
                    execute_values(cur, WIKI_INSERT_SQL, wiki_batch, page_size=PAGE_SIZE)
                    execute_values(cur, REVISION_INSERT_SQL, revision_batch, page_size=PAGE_SIZE)
                    execute_values(cur, ALBUM_INSERT_SQL, album_batch, page_size=PAGE_SIZE)
                    
                    conn.commit()
                    stats["inserted"] += len(batch_bundles)
                    if stats["inserted"] % LOG_EVERY == 0:
                        print(f"✅ inserted={stats['inserted']:,} processed={stats['seen']:,} queued={stats['queued']:,}")
                    batch_bundles.clear()

            # drain
            if batch_bundles:
                wiki_batch = [b[0] for b in batch_bundles]
                revision_batch = [b[1] for b in batch_bundles]
                album_batch = [b[2] for b in batch_bundles]

                execute_values(cur, WIKI_INSERT_SQL, wiki_batch, page_size=PAGE_SIZE)
                execute_values(cur, REVISION_INSERT_SQL, revision_batch, page_size=PAGE_SIZE)
                execute_values(cur, ALBUM_INSERT_SQL, album_batch, page_size=PAGE_SIZE)
                
                conn.commit()
                stats["inserted"] += len(batch_bundles)
                print(f"✅ inserted(final)={stats['inserted']:,} processed={stats['seen']:,} queued={stats['queued']:,}")
    finally:
        conn.close()

# ----------------------------
# Producer (parser)
# ----------------------------
def run():
    qrows: "queue.Queue[Optional[tuple]]" = queue.Queue(maxsize=QUEUE_MAXSIZE)
    stats = {"seen": 0, "inserted": 0, "queued": 0}

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

            # --- 유효한 데이터이므로 큐에 넣을 준비 ---
            stats["queued"] += 1
            current_index = stats["queued"]
            now = datetime.now()

            # 1. ID 생성
            wiki_id = f"wiki-lp-{current_index}"
            revision_id = f"rev-lp-{current_index}"

            # 2. 앨범 데이터 파싱
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

            # 3. 3개 테이블의 row 생성
            wiki_row = (
                wiki_id,
                trunc(name, 50),
                "r1",             # current_revision
                "OPEN",           # status
                "ALBUM",          # class
            )
            
            revision_row = (
                revision_id,
                "{}",             # content (jsonb empty object)
                "r1",             # revision_number
                now,              # created_at
                wiki_id,          # wiki_id (FK)
                "1",              # oauth_id
            )

            album_row = (
                str(album_id),
                trunc(name, 100),
                profile,
                release_dt,
                release_country,
                label_name,
                wiki_id,          # wiki_id (FK)
                lpti_code,
            )
            
            # 4. 큐에 (번들로) 삽입
            bundle = (wiki_row, revision_row, album_row)
            qrows.put(bundle)  # 큐가 가득 차면 back-pressure로 자연스러운 스로틀링

            if stats["queued"] % LOG_EVERY == 0:
                print(f"… processed={stats['seen']:,} queued={stats['queued']:,}")

            elem.clear()  # 메모리 누수 방지

    # 종료 신호
    qrows.put(None)
    wt.join()

    print(f"🎉 Done. processed={stats['seen']:,}, queued={stats['queued']:,}, inserted={stats['inserted']:,}")

if __name__ == "__main__":
    run()
