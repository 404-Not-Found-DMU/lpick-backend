import gzip
import xml.etree.ElementTree as ET
import os
from typing import Set, List

# ----------------------------
# 환경 설정
# ----------------------------
# 확인하려는 파일 경로를 여기에 지정합니다.
# 사용자가 올린 파일 또는 확인하고 싶은 파일의 경로를 입력해 주세요.
XML_PATH = "discogs_20251001_releases.xml.gz" 
# 최대 처리할 엘리먼트 개수 (이 개수를 넘으면 분석을 중단하고 결과를 출력합니다.)
MAX_ELEMENTS_TO_PROCESS = 300000000
# ----------------------------
# 메인 테스트 함수
# ----------------------------
def run_structure_check():
    """
    주어진 XML 파일(gzip 압축)을 파싱하여 파일 내부에 존재하는 모든 고유한 태그 이름 목록을 추출합니다.
    이를 통해 파일의 전체적인 구조를 단순하게 파악할 수 있습니다.
    """
    
    unique_tags: Set[str] = set()
    total_elements_processed = 0
    
    # MAX_ELEMENTS_TO_PROCESS 설정에 따라 분석 시작 메시지 출력
    if MAX_ELEMENTS_TO_PROCESS > 0:
        print(f"--- XML 파일 태그 구조 분석 시작 ({XML_PATH}) (최대 {MAX_ELEMENTS_TO_PROCESS:,}개 엘리먼트 처리) ---")
    else:
        print(f"--- XML 파일 태그 구조 분석 시작 ({XML_PATH}) (전체 파일 처리) ---")

    try:
        # 파일이 존재하는지 확인
        if not os.path.exists(XML_PATH):
            raise FileNotFoundError
            
        # Gzip 파일 열기
        with gzip.open(XML_PATH, "rb") as f:
            # iterparse를 사용하여 메모리 효율적으로 XML을 파싱합니다.
            # 'end' 이벤트를 사용하여 모든 엘리먼트가 닫힐 때 처리합니다.
            context = ET.iterparse(f, events=("end",))
            
            for event, elem in context:
                # 모든 엘리먼트의 태그 이름을 수집합니다.
                unique_tags.add(elem.tag)
                total_elements_processed += 1
                
                # 메모리 관리를 위해 처리 후 엘리먼트를 비웁니다.
                elem.clear() 

                # 대용량 파일 처리를 위해 중간 로그를 출력합니다.
                if total_elements_processed % 1000000 == 0:
                    print(f"--- 진행 상황: {total_elements_processed:,}개 엘리먼트 처리 완료 ---")

                # 개수 제한 확인
                if MAX_ELEMENTS_TO_PROCESS > 0 and total_elements_processed >= MAX_ELEMENTS_TO_PROCESS:
                    print("-" * 70)
                    print(f"🛑 설정된 최대 개수({MAX_ELEMENTS_TO_PROCESS:,}개)에 도달하여 분석을 중단합니다.")
                    break
        
        print("-" * 70)
        print("✅ 분석 완료.")
        
        # 태그 목록을 알파벳순으로 정렬
        sorted_tags: List[str] = sorted(list(unique_tags))
        
        print(f"\n--- 파일에 존재하는 고유 태그 ({len(sorted_tags)}개) 목록 ---")
        
        # 태그 목록을 보기 좋게 출력
        for tag in sorted_tags:
            print(f"  - <{tag}>")
        
        print("-" * 70)
        # 최종 통계 출력 시, 처리된 엘리먼트 수와 최대 제한 수를 함께 보여줍니다.
        limit_text = f" (최대 {MAX_ELEMENTS_TO_PROCESS:,}개)" if MAX_ELEMENTS_TO_PROCESS > 0 else ""
        print(f"  총 처리된 엘리먼트 수: {total_elements_processed:,}개{limit_text}")
        
    except FileNotFoundError:
        print(f"❌ 오류: 파일을 찾을 수 없습니다. 경로를 확인해 주세요: {XML_PATH}")
        print(f"💡 힌트: 코드 상단의 XML_PATH 변수를 사용하려는 파일 경로로 수정해 주세요.")
    except ET.ParseError as e:
        print(f"❌ XML 파싱 오류 발생: {e}")
        print(f"💡 힌트: XML 파일의 형식이 올바른지 확인해 주세요.")
    except Exception as e:
        print(f"❌ 예상치 못한 오류 발생: {e}")
        
    
if __name__ == "__main__":
    run_structure_check()
