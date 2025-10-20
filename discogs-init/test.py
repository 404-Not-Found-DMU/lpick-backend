import gzip
import xml.etree.ElementTree as ET
from typing import Optional, List, Tuple

# ----------------------------
# 환경 설정
# ----------------------------
XML_PATH = "discogs_20250801_releases.xml.gz" # 확인하려는 파일 경로

# ----------------------------
# 유틸리티 함수 (기존 코드에서 복사)
# (구조 확인을 위해 현재 사용되진 않지만, 참조를 위해 유지합니다.)
# ----------------------------
def extract_images(elem: ET.Element) -> Tuple[Optional[str], Optional[str]]:
    """
    릴리스 엘리먼트에서 대표 이미지 URI (원본/큰 이미지, 150x150)를 추출합니다.
    Primary 이미지를 우선하고, 없으면 첫 번째 이미지를 사용합니다.
    """
    images = elem.find("images")
    if images is None:
        return None, None
    
    first_uri = first_uri150 = None
    primary_uri = primary_uri150 = None
    
    for img in images.findall("image"):
        uri = img.attrib.get("uri")
        uri150 = img.attrib.get("uri150")
        
        # 첫 번째 이미지 저장
        if first_uri is None:
            first_uri, first_uri150 = uri, uri150
            
        # Primary 이미지 저장 (대표 이미지)
        if img.attrib.get("type") == "primary":
            # URI가 실제로 존재하는지 확인하여 덮어씁니다.
            if uri:
                primary_uri = uri
            if uri150:
                primary_uri150 = uri150
                
    # Primary가 있으면 Primary를, 없으면 첫 번째 이미지를 반환
    return (primary_uri or first_uri, primary_uri150 or first_uri150)

def extract_title(elem: ET.Element) -> Optional[str]:
    """릴리스 제목을 추출합니다."""
    title_el = elem.find("title")
    return (title_el.text or "").strip() if title_el is not None else None

# ----------------------------
# 메인 테스트 함수
# ----------------------------
def run_sample_check():
    """XML 파일에서 샘플 데이터를 추출하고 원본 XML 구조를 확인합니다."""
    
    processed_count = 0
    MAX_SAMPLES = 5  # 확인을 위해 추출할 릴리스 샘플 수
    
    print(f"--- Discogs XML 데이터 샘플 구조 체크 시작 ({XML_PATH}) ---")

    try:
        with gzip.open(XML_PATH, "rb") as f:
            # iterparse는 메모리 효율적으로 XML을 파싱합니다.
            context = ET.iterparse(f, events=("end",))
            
            for event, elem in context:
                if elem.tag != "release":
                    # 메모리 관리를 위해 처리하지 않은 엘리먼트는 비웁니다.
                    elem.clear() 
                    continue
                
                # ----------------- XML 구조 추출 -----------------
                # ET.tostring을 사용하여 현재 <release> 엘리먼트의 전체 XML 구조를 문자열로 직렬화합니다.
                # encoding='utf-8', method='xml' 설정으로 XML 형태로 출력합니다.
                raw_xml_bytes = ET.tostring(elem, encoding='utf-8', method='xml')
                raw_xml_string = raw_xml_bytes.decode('utf-8')
                
                # ----------------- 로깅 -----------------
                processed_count += 1
                
                print("-" * 70)
                print(f"[{processed_count}번째 릴리스 XML 원본 구조 (ID: {elem.attrib.get('id')})]")
                print(raw_xml_string)
                    
                # ----------------- 종료 조건 -----------------
                # 설정된 샘플 수를 확인하면 바로 종료합니다.
                if processed_count >= MAX_SAMPLES:
                    print("-" * 70)
                    print(f"🎉 {MAX_SAMPLES}개 샘플 확인 완료. 테스트를 종료합니다.")
                    break

                elem.clear() # 메모리 누수 방지
                
    except FileNotFoundError:
        print(f"❌ 오류: 파일을 찾을 수 없습니다. 경로를 확인해 주세요: {XML_PATH}")
    except ET.ParseError as e:
        print(f"❌ XML 파싱 오류 발생: {e}")
    except Exception as e:
        print(f"❌ 예상치 못한 오류 발생: {e}")
        
    print(f"\n--- 최종 통계 ---")
    print(f"  총 처리된 릴리스: {processed_count:,}개")
    print("-" * 70)


if __name__ == "__main__":
    run_sample_check()
