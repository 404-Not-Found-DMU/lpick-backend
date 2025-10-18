import gzip
import psycopg2
import xml.etree.ElementTree as ET
from datetime import datetime

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

XML_PATH = "discogs_20250801_releases.xml.gz"

# 배치/제한/로그 간격
BATCH_SIZE = 1000
MAX_COUNT = 100_000          # 테스트용 상한. 전량 적재 시 None 또는 크게
SKIP_LOG_INTERVAL = 10_000   # profile/label 없어서 스킵된 누적이 이 값의 배수일 때 로그

# ----------------------------
# 유틸
# ----------------------------
def trunc(s: str | None, limit: int) -> str | None:
    if s is None:
        return None
    return s[:limit]

def parse_released(released_text: str | None) -> datetime | None:
    if not released_text:
        return None
    t = released_text.strip()
    for fmt in ("%Y-%m-%d", "%Y-%m", "%Y"):
        try:
            dt = datetime.strptime(t, fmt)
            if fmt == "%Y":
                dt = datetime(dt.year, 1, 1)
            elif fmt == "%Y-%m":
                dt = datetime(dt.year, dt.month, 1)
            return dt
        except ValueError:
            continue
    return None

def extract_images(elem: ET.Element) -> tuple[str | None, str | None]:
    """
    returns (primary_uri, primary_uri150)
    fallback: 첫 이미지의 uri/uri150
    """
    images = elem.find("images")
    if images is None:
        return (None, None)

    first_uri = first_uri150 = None
    primary_uri = primary_uri150 = None

    for img in images.findall("image"):
        uri = img.attrib.get("uri")
        uri150 = img.attrib.get("uri150")
        if first_uri is None:
            first_uri = uri
            first_uri150 = uri150
        if img.attrib.get("type") == "primary":
            primary_uri = uri or primary_uri
            primary_uri150 = uri150 or primary_uri150

    if primary_uri or primary_uri150:
        return (primary_uri, primary_uri150)
    return (first_uri, first_uri150)

def insert_batch(cur, batch):
    cur.executemany(
        """
        INSERT INTO album (album_id, name, profile, release_date, release_country, label, wiki_id)
        VALUES (%s, %s, %s, %s, %s, %s, NULL)
        ON CONFLICT (album_id) DO NOTHING;
        """,
        batch,
    )

# ----------------------------
# 실행
# ----------------------------
def run():
    conn = psycopg2.connect(**DB)
    cur = conn.cursor()

    batch = []
    total_inserted = 0
    total_seen = 0
    skipped_missing_media = 0  # profile/label 누락 스킵 카운터

    print("⏳ Parsing and inserting from:", XML_PATH)

    with gzip.open(XML_PATH, "rb") as f:
        context = ET.iterparse(f, events=("end",))
        for event, elem in context:
            if elem.tag != "release":
                continue

            total_seen += 1

            album_id = elem.attrib.get("id")
            if not album_id:
                elem.clear()
                continue

            title_el = elem.find("title")
            name = (title_el.text or "").strip() if title_el is not None else None
            if not name:
                elem.clear()
                continue  # name은 NOT NULL

            released_el = elem.find("released")
            release_date = parse_released(released_el.text if released_el is not None else None)

            country_el = elem.find("country")
            release_country = (country_el.text or "").strip() if country_el is not None else None
            release_country = trunc(release_country, 50)

            # 이미지 추출
            img_uri, img_uri150 = extract_images(elem)
            profile = trunc(img_uri, 200) if img_uri else None                 # 대표사진
            label_cover = trunc(img_uri150 or img_uri, 50) if (img_uri150 or img_uri) else None  # 표지(썸네일 우선)

            # ✅ profile과 label 둘 중 하나라도 값이 있는 경우에만 삽입
            if not (profile or label_cover):
                skipped_missing_media += 1
                # 10,000개 단위로 스킵 로그
                if skipped_missing_media % SKIP_LOG_INTERVAL == 0:
                    print(f"⚠️ Skipped due to missing profile/label: {skipped_missing_media:,} (processed: {total_seen:,}, inserted: {total_inserted:,})")
                elem.clear()
                continue

            batch.append((
                str(album_id),
                trunc(name, 100),
                profile,
                release_date,
                release_country,
                label_cover,
            ))

            if len(batch) >= BATCH_SIZE:
                insert_batch(cur, batch)
                conn.commit()
                total_inserted += len(batch)
                print(f"✅ {total_inserted:,} rows inserted (processed: {total_seen:,}, skipped: {skipped_missing_media:,})")
                batch.clear()

                if MAX_COUNT and total_inserted >= MAX_COUNT:
                    print(f"🛑 Max limit {MAX_COUNT:,} reached. Stopping early.")
                    break

            elem.clear()

    # 남은 배치 처리 (MAX_COUNT 초과 방지)
    if batch and (not MAX_COUNT or total_inserted < MAX_COUNT):
        if MAX_COUNT:
            remain = MAX_COUNT - total_inserted
            batch = batch[:max(remain, 0)]
        if batch:
            insert_batch(cur, batch)
            conn.commit()
            total_inserted += len(batch)
            print(f"✅ {total_inserted:,} rows inserted (final batch).")

    cur.close()
    conn.close()
    print(f"🎉 Done. processed={total_seen:,}, inserted={total_inserted:,}, skipped_missing_media={skipped_missing_media:,}")

if __name__ == "__main__":
    run()
