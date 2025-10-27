INSERT INTO tier(tier_id, name, point_scope) VALUES
                        ('1', 'dummy_tier', 300) ON CONFLICT (tier_id) DO NOTHING;

INSERT INTO wiki_page(wiki_id, title, current_revision, status, class) VALUES
                        ('wiki-1', 'dummy_wikipage', 'r3', 'OPEN', 'ARTIST') ON CONFLICT (wiki_id) DO NOTHING;
INSERT INTO wiki_page(wiki_id, title, current_revision, status, class) VALUES
                        ('wiki-2', 'dummy_wikipage2', null, 'OPEN', 'ALBUM') ON CONFLICT (wiki_id) DO NOTHING;
INSERT INTO wiki_page(wiki_id, title, current_revision, status, class) VALUES
                        ('wiki-3', 'dummy_wikipage3', null, 'OPEN', 'GEAR') ON CONFLICT (wiki_id) DO NOTHING;

-- mockUser 기입
INSERT INTO user_info(oauth_id, nickname, profile, point, stack_point, about, lpti, tier_id) VALUES
    ('1', 'mock_user', '', 0, 0, '자기소개', null, '1') ON CONFLICT (oauth_id) DO NOTHING;
INSERT INTO user_setting(oauth_id, allow_view_act_count, allow_view_recent_act, allow_view_gear, allow_view_collection, page_theme_setting, is_alarm_wiki_edit, is_alarm_new_debate_answer, is_alarm_commented, is_alarm_event) VALUES
                        ('1', true, true, true, true, 'LIGHT', true, true, true, true) ON CONFLICT (oauth_id) DO NOTHING;

-- wikiBookmark 기입
INSERT INTO wiki_bookmark(wiki_bookmark_id, oauth_id, wiki_id) VALUES
 ('wiki-bookmark-1', '1', 'wiki-1') ON CONFLICT (wiki_bookmark_id) DO NOTHING ;
INSERT INTO wiki_bookmark(wiki_bookmark_id, oauth_id, wiki_id) VALUES
    ('wiki-bookmark-2', '1', 'wiki-2') ON CONFLICT (wiki_bookmark_id) DO NOTHING ;
INSERT INTO wiki_bookmark(wiki_bookmark_id, oauth_id, wiki_id) VALUES
    ('wiki-bookmark-3', '1', 'wiki-3') ON CONFLICT (wiki_bookmark_id) DO NOTHING ;


-- PageRevision 기입
-- INSERT INTO page_revision(revision_id, content, revision_number, created_at, wiki_id, oauth_id) VALUES
--     ('revision-6', '위키내용입니다.\n이거저거많이추가됐습니다', 'r1', '2025-06-05 00:21:12', 'wiki-3', '1') ON CONFLICT (revision_id) DO NOTHING;
-- INSERT INTO page_revision(revision_id, content, revision_number, created_at, wiki_id, oauth_id) VALUES
--     ('revision-5', '위키내용입니다.\n그럭저럭추가', 'r2', '2025-06-05 09:34:12', 'wiki-2', '1') ON CONFLICT (revision_id) DO NOTHING;
-- INSERT INTO page_revision(revision_id, content, revision_number, created_at, wiki_id, oauth_id) VALUES
--     ('revision-4', '위키내용입니다.', 'r3', '2025-06-04 14:34:32', 'wiki-1', '1') ON CONFLICT (revision_id) DO NOTHING;
--
-- INSERT INTO page_revision(revision_id, content, revision_number, created_at, wiki_id, oauth_id) VALUES
--     ('revision-3', '위키내용입니다.\n이거저거많이추가됐습니다', 'r2', '2025-06-04 12:34:32', 'wiki-1', '1') ON CONFLICT (revision_id) DO NOTHING;
-- INSERT INTO page_revision(revision_id, content, revision_number, created_at, wiki_id, oauth_id) VALUES
--     ('revision-2', '위키내용입니다.\n그럭저럭추가', 'r1', '2025-06-05 09:32:12', 'wiki-2', '1') ON CONFLICT (revision_id) DO NOTHING;
-- INSERT INTO page_revision(revision_id, content, revision_number, created_at, wiki_id, oauth_id) VALUES
--     ('revision-1', '위키내용입니다.', 'r1', '2025-06-03 12:20:23', 'wiki-1', '1') ON CONFLICT (revision_id) DO NOTHING;

-- # service_data
-- GearClass 기입
INSERT INTO gear_class(class_name) VALUES ('TURNTABLE') ON CONFLICT (class_name) DO NOTHING;
INSERT INTO gear_class(class_name) VALUES ('SPEAKER') ON CONFLICT (class_name) DO NOTHING;
INSERT INTO gear_class(class_name) VALUES ('HEADPHONE') ON CONFLICT (class_name) DO NOTHING;

-- Album 기입
INSERT INTO album(album_id, name, profile, release_date, release_country, label, lpti, wiki_id) VALUES
    ('album-1', '앨범명칭', null, '2025-06-05 00:21:12', 'KR', '으랏차차레이블', 'CMVS',  null) ON CONFLICT (album_id) DO NOTHING;
INSERT INTO album(album_id, name, profile, release_date, release_country, label, lpti, wiki_id) VALUES
    ('album-2', '푸른밤의 멜로디', 'profile_blue.jpg', '2024-11-10 10:00:00', 'US', 'Blue Note', 'JAZZ',  null) ON CONFLICT (album_id) DO NOTHING;
INSERT INTO album(album_id, name, profile, release_date, release_country, label, lpti, wiki_id) VALUES
    ('album-3', '도시의 불빛', 'city_lights.png', '2023-01-15 18:30:00', 'UK', 'Electric Records', 'ELEC',  null) ON CONFLICT (album_id) DO NOTHING;
INSERT INTO album(album_id, name, profile, release_date, release_country, label, lpti, wiki_id) VALUES
    ('album-4', '숲속의 속삭임', null, '2025-02-20 05:00:00', 'KR', '어쿠스틱뮤직', 'FOLK',  null) ON CONFLICT (album_id) DO NOTHING;

-- Gear 기입
INSERT INTO gear(id, name, model_name, brand, eq_class, wiki_id) VALUES
    ('gear-1', '명칭', 'dp-300f', 'Denon', 'TURNTABLE', null) ON CONFLICT (id) DO NOTHING;



-- Role 기입
-- 중재자, 매니저, 어드민(본 서비스 개발자들)
INSERT INTO auth(auth_id, name) VALUES
                        ('1', 'MEDIATOR') ON CONFLICT (auth_id) DO NOTHING ;
INSERT INTO auth(auth_id, name) VALUES
                        ('2', 'MANAGER') ON CONFLICT (auth_id) DO NOTHING ;
INSERT INTO auth(auth_id, name) VALUES
                        ('3', 'ADMIN') ON CONFLICT (auth_id) DO NOTHING ;


-- UserAlbum 기입 (oauth_id = '1' 사용)
INSERT INTO user_album(user_album_id, record_file, is_favorite, album_id, oauth_id) VALUES
    ('user_album_1', 'my_record_file.mp3', true, 'album-1', '1');
INSERT INTO user_album(user_album_id, record_file, is_favorite, album_id, oauth_id) VALUES
    ('user_album_2', 'my_fav_song.wav', true, 'album-3', '1');
INSERT INTO user_album(user_album_id, record_file, is_favorite, album_id, oauth_id) VALUES
    ('user_album_3', null, false, 'album-4', '1');




-- 위키 더미데이터 목적
-- ============================================================
-- 15개 위키 더미데이터 (JSONB) - 기존 data.sql에 병합용
-- PostgreSQL / UTF-8 저장 필수
-- ============================================================

-- =========================================
-- 위키 더미데이터 (JSONB 정정 버전) - data.sql 병합용
-- =========================================

-- 0) content 컬럼이 text 라면 jsonb로 변환(이미 jsonb면 에러 없이 통과)
-- DO $$
--     BEGIN
--         BEGIN
--             ALTER TABLE page_revision
--                 ALTER COLUMN content TYPE jsonb USING content::jsonb;
--         EXCEPTION WHEN others THEN
--             -- 이미 jsonb 이거나 타입 불일치가 아닌 경우 등은 무시
--             NULL;
--         END;
--     END $$;

-- 1) wiki_page 생성 (존재하면 무시)
INSERT INTO wiki_page (wiki_id, title, current_revision, status, class) VALUES
    ('wiki-eq-1', 'Gibson Les Paul',          NULL, 'OPEN', 'GEAR')   ON CONFLICT (wiki_id) DO NOTHING;
INSERT INTO wiki_page (wiki_id, title, current_revision, status, class) VALUES
    ('wiki-eq-2', 'Roland TR-808',             NULL, 'OPEN', 'GEAR')   ON CONFLICT (wiki_id) DO NOTHING;
INSERT INTO wiki_page (wiki_id, title, current_revision, status, class) VALUES
    ('wiki-eq-3', 'Moog Minimoog',             NULL, 'OPEN', 'GEAR')   ON CONFLICT (wiki_id) DO NOTHING;
INSERT INTO wiki_page (wiki_id, title, current_revision, status, class) VALUES
    ('wiki-eq-4', 'Marshall JCM800',           NULL, 'OPEN', 'GEAR')   ON CONFLICT (wiki_id) DO NOTHING;
INSERT INTO wiki_page (wiki_id, title, current_revision, status, class) VALUES
    ('wiki-eq-5', 'Shure SM58',                NULL, 'OPEN', 'GEAR')   ON CONFLICT (wiki_id) DO NOTHING;

INSERT INTO wiki_page (wiki_id, title, current_revision, status, class) VALUES
    ('wiki-art-1', 'Elvis Presley',            NULL, 'OPEN', 'ARTIST') ON CONFLICT (wiki_id) DO NOTHING;
INSERT INTO wiki_page (wiki_id, title, current_revision, status, class) VALUES
    ('wiki-art-2', 'Michael Jackson',          NULL, 'OPEN', 'ARTIST') ON CONFLICT (wiki_id) DO NOTHING;
INSERT INTO wiki_page (wiki_id, title, current_revision, status, class) VALUES
    ('wiki-art-3', 'Madonna',                  NULL, 'OPEN', 'ARTIST') ON CONFLICT (wiki_id) DO NOTHING;
INSERT INTO wiki_page (wiki_id, title, current_revision, status, class) VALUES
    ('wiki-art-4', 'Freddie Mercury',          NULL, 'OPEN', 'ARTIST') ON CONFLICT (wiki_id) DO NOTHING;
INSERT INTO wiki_page (wiki_id, title, current_revision, status, class) VALUES
    ('wiki-art-5', '서태지',                   NULL, 'OPEN', 'ARTIST') ON CONFLICT (wiki_id) DO NOTHING;

INSERT INTO wiki_page (wiki_id, title, current_revision, status, class) VALUES
    ('wiki-lp-1',  'Sgt. Pepper''s Lonely Hearts Club Band', NULL, 'OPEN', 'ALBUM') ON CONFLICT (wiki_id) DO NOTHING;
INSERT INTO wiki_page (wiki_id, title, current_revision, status, class) VALUES
    ('wiki-lp-2',  'Thriller',                 NULL, 'OPEN', 'ALBUM')  ON CONFLICT (wiki_id) DO NOTHING;
INSERT INTO wiki_page (wiki_id, title, current_revision, status, class) VALUES
    ('wiki-lp-3',  'Nevermind',                NULL, 'OPEN', 'ALBUM')  ON CONFLICT (wiki_id) DO NOTHING;
INSERT INTO wiki_page (wiki_id, title, current_revision, status, class) VALUES
    ('wiki-lp-4',  'Kind of Blue',             NULL, 'OPEN', 'ALBUM')  ON CONFLICT (wiki_id) DO NOTHING;
INSERT INTO wiki_page (wiki_id, title, current_revision, status, class) VALUES
    ('wiki-lp-5',  '서태지와 아이들 I',         NULL, 'OPEN', 'ALBUM')  ON CONFLICT (wiki_id) DO NOTHING;

-- 2) page_revision upsert (달러인용 + ::jsonb)
-- equipment 5
INSERT INTO page_revision (revision_id, content, revision_number, created_at, wiki_id, oauth_id) VALUES
    ('rev-eq-1', $$
    {
      "categoryData": {
        "type": "equipment",
        "data": {
          "name": "Gibson Les Paul",
          "brand": "Gibson",
          "releaseYear": "1952",
          "description": "록 역사상 가장 영향력 있는 전자 기타 중 하나로, 강력한 사운드와 긴 서스테인으로 유명합니다.",
          "equipmentType": "other",
          "imageUrl": "https://upload.wikimedia.org/wikipedia/commons/6/6b/Elvis_Presley.jpg"
        }
      },
      "textBlocks": [
        {"id": "1","title": "개발 배경","content": "깁슨 레스폴은 기타리스트 레스 폴과 깁슨사가 협업하여 1952년 처음 선보인 솔리드 바디 일렉트릭 기타입니다. 당시 혁신적인 전기 기타 디자인으로 큰 반향을 일으켰습니다.","depth": 1},
        {"id": "2","title": "특징","content": "마호가니 바디와 메이플 탑, 듀얼 험버커 픽업을 채택하여 두터운 음색과 긴 서스테인이 특징입니다.","depth": 1},
        {"id": "3","title": "사용자들","content": "Jimmy Page, Slash, 에이스 프레일리 등 많은 록 기타리스트들이 이 기타를 사용해 전설적인 사운드를 만들어냈습니다.","depth": 1}
      ]
    }
    $$::jsonb, 'r1', '2025-10-27 09:00:00', 'wiki-eq-1', '1')
ON CONFLICT (revision_id) DO UPDATE SET content = EXCLUDED.content;

INSERT INTO page_revision (revision_id, content, revision_number, created_at, wiki_id, oauth_id) VALUES
    ('rev-eq-2', $$
    {
      "categoryData": {
        "type": "equipment",
        "data": {
          "name": "Roland TR-808",
          "brand": "Roland",
          "releaseYear": "1980",
          "description": "초기의 프로그램 가능 드럼 머신으로, 독특한 아날로그 드럼음으로 힙합과 전자음악에 혁신을 가져왔습니다.",
          "equipmentType": "other",
          "imageUrl": "https://upload.wikimedia.org/wikipedia/commons/f/fa/Roland_TR-808_Rhythm_Composer.jpg"
        }
      },
      "textBlocks": [
        {"id": "1","title": "개발 배경","content": "TR-808은 Roland사가 1980년에 출시한 드럼 머신으로, 초기에는 큰 주목을 못 받았지만 저렴한 가격에 힘입어 언더그라운드 음악가들에게 채택되며 후에 힙합과 팝에 혁명을 가져왔습니다.","depth": 1},
        {"id": "2","title": "특징","content": "아날로그 회로 기반의 독특한 드럼 사운드를 내며, 묵직한 킥 드럼과 카우벨 등의 음색은 TR-808만의 시그니처로 자리잡았습니다.","depth": 1},
        {"id": "3","title": "영향","content": "마빈 게이의 \"Sexual Healing\"이나 Afrika Bambaataa의 \"Planet Rock\" 등 수많은 히트곡에 사용되었고, 현대 대중음악의 비트에 지대한 영향을 주었습니다.","depth": 1}
      ]
    }
    $$::jsonb, 'r1', '2025-10-27 09:01:00', 'wiki-eq-2', '1')
ON CONFLICT (revision_id) DO UPDATE SET content = EXCLUDED.content;

INSERT INTO page_revision (revision_id, content, revision_number, created_at, wiki_id, oauth_id) VALUES
    ('rev-eq-3', $$
    {
      "categoryData": {
        "type": "equipment",
        "data": {
          "name": "Moog Minimoog",
          "brand": "Moog",
          "releaseYear": "1970",
          "description": "세계 최초의 휴대 가능한 상업용 아날로그 신시사이저로, 따뜻하고 풍부한 톤으로 현대 전자음악의 기틀을 세웠습니다.",
          "equipmentType": "other",
          "imageUrl": "https://upload.wikimedia.org/wikipedia/commons/2/22/Minimoog.JPG"
        }
      },
      "textBlocks": [
        {"id": "1","title": "개발 배경","content": "Minimoog는 Bob Moog가 1970년에 선보인 소형 모듈러 신시사이저로, 거대한 초기 신디사이저를 휴대 가능한 크기로 줄여 현장 연주에 혁신을 일으켰습니다.","depth": 1},
        {"id": "2","title": "특징","content": "3개의 오실레이터와 저역통과 필터 등을 탑재한 모노포닉 아날로그 신스이며, 따뜻하고 두터운 리드와 베이스 사운드로 유명합니다.","depth": 1},
        {"id": "3","title": "영향","content": "Stevie Wonder, Keith Emerson, Kraftwerk 등 수많은 아티스트들이 Minimoog를 애용하였고, 펑크, 프로그레시브 록부터 전자음악까지 새로운 사운드의 지평을 열었습니다.","depth": 1}
      ]
    }
    $$::jsonb, 'r1', '2025-10-27 09:02:00', 'wiki-eq-3', '1')
ON CONFLICT (revision_id) DO UPDATE SET content = EXCLUDED.content;

INSERT INTO page_revision (revision_id, content, revision_number, created_at, wiki_id, oauth_id) VALUES
    ('rev-eq-4', $$
    {
      "categoryData": {
        "type": "equipment",
        "data": {
          "name": "Marshall JCM800",
          "brand": "Marshall",
          "releaseYear": "1981",
          "description": "1980년대 록 사운드를 상징하는 진공관 기타 앰프로, 강력한 오버드라이브 톤으로 헤비 메탈 음악가들에게 사랑받았습니다.",
          "equipmentType": "other",
          "imageUrl": "https://upload.wikimedia.org/wikipedia/commons/4/4f/Marshall_JCM800_amplifier.jpg"
        }
      },
      "textBlocks": [
        {"id": "1","title": "개발 배경","content": "Marshall JCM800 시리즈는 1981년 출시되었으며, 마스터 볼륨 기능을 채택한 최초의 마샬 앰프로서 시대를 대표하는 하드 록 사운드를 제공했습니다.","depth": 1},
        {"id": "2","title": "특징","content": "진공관 기반의 100와트 앰프로, 풍부한 미드레인지와 공격적인 디스토션 톤이 특징입니다. 마스터 볼륨 덕분에 낮은 볼륨에서도 자연스러운 드라이브를 얻을 수 있습니다.","depth": 1},
        {"id": "3","title": "사용자들","content": "Slash, Kerry King 등 많은 록 기타리스트들이 JCM800의 사운드를 활용하여 강렬한 톤을 구현했습니다.","depth": 1}
      ]
    }
    $$::jsonb, 'r1', '2025-10-27 09:03:00', 'wiki-eq-4', '1')
ON CONFLICT (revision_id) DO UPDATE SET content = EXCLUDED.content;

INSERT INTO page_revision (revision_id, content, revision_number, created_at, wiki_id, oauth_id) VALUES
    ('rev-eq-5', $$
    {
      "categoryData": {
        "type": "equipment",
        "data": {
          "name": "Shure SM58",
          "brand": "Shure",
          "releaseYear": "1966",
          "description": "라이브 공연에서 가장 널리 사용되는 다이내믹 마이크로폰으로, 견고한 내구성과 보컬에 최적화된 음질로 유명합니다.",
          "equipmentType": "other",
          "imageUrl": "https://upload.wikimedia.org/wikipedia/commons/2/2e/Microphone_(Shure_SM58)_(4900485245).jpg"
        }
      },
      "textBlocks": [
        {"id": "1","title": "개발 배경","content": "SM58은 1966년 슈어사가 선보인 마이크로폰으로, 이전 모델인 Unidyne III의 기술을 발전시켜 견고한 구조의 보컬용 마이크를 탄생시켰습니다.","depth": 1},
        {"id": "2","title": "특징","content": "카디오이드 패턴의 다이내믹 마이크로, 보컬 주파수 대역을 잘 살려주며, 내장된 망 형태의 팝 필터로 무대 환경에서도 노이즈를 최소화합니다.","depth": 1},
        {"id": "3","title": "활용","content": "수십 년간 라이브 공연 현장에서 표준으로 자리잡아 왔고, 수많은 아티스트들이 이 마이크를 통해 안정적인 보컬 사운드를 관객에게 전달했습니다.","depth": 1}
      ]
    }
    $$::jsonb, 'r1', '2025-10-27 09:04:00', 'wiki-eq-5', '1')
ON CONFLICT (revision_id) DO UPDATE SET content = EXCLUDED.content;

-- artist 5
INSERT INTO page_revision (revision_id, content, revision_number, created_at, wiki_id, oauth_id) VALUES
    ('rev-art-1', $$
    {
      "categoryData": {
        "type": "artist",
        "data": {
          "name": "Elvis Presley",
          "country": "미국",
          "activePeriod": "1954-1977",
          "roles": ["singer","actor"],
          "imageUrl": "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcR7L8ejItT31JXdDNanomGTJ6Fsmxj_Kou5HA&s",
          "discography": [
            {"id": "1","title": "Elvis Presley","releaseDate": "1956","type": "앨범","role": "보컬"},
            {"id": "2","title": "Elvis' Christmas Album","releaseDate": "1957","type": "앨범","role": "보컬"},
            {"id": "3","title": "From Elvis in Memphis","releaseDate": "1969","type": "앨범","role": "보컬"},
            {"id": "4","title": "Aloha from Hawaii via Satellite","releaseDate": "1973","type": "앨범","role": "보컬"}
          ],
          "activities": [
            {"id": "1","year": "1950s","title": "록앤롤 혁명","description": "록앤롤의 탄생과 함께 세계적 아이콘으로 부상","type": "데뷔"},
            {"id": "2","year": "1960s","title": "할리우드 진출","description": "다수의 영화에 출연하며 배우로 활약","type": "영화"},
            {"id": "3","year": "1970s","title": "라스베이거스 시대","description": "라스베이거스 상설 공연을 통해 성공적인 복귀","type": "콘서트"}
          ]
        }
      },
      "textBlocks": [
        {"id": "1","title": "생애와 경력","content": "Elvis Presley는 세계 음악계에서 독보적인 위치를 차지한 아티스트입니다.","depth": 1},
        {"id": "2","title": "음악적 스타일","content": "다양한 장르를 넘나들며 개성 있는 음악 세계를 구축하였습니다.","depth": 1},
        {"id": "3","title": "문화적 영향","content": "음악뿐 아니라 패션, 영상 등 여러 방면에서 대중문화에 큰 영향을 미쳤습니다.","depth": 1}
      ]
    }
    $$::jsonb, 'r1', '2025-10-27 09:05:00', 'wiki-art-1', '1')
ON CONFLICT (revision_id) DO UPDATE SET content = EXCLUDED.content;

INSERT INTO page_revision (revision_id, content, revision_number, created_at, wiki_id, oauth_id) VALUES
    ('rev-art-2', $$
    {
      "categoryData": {
        "type": "artist",
        "data": {
          "name": "Michael Jackson",
          "country": "미국",
          "activePeriod": "1964-2009",
          "roles": ["singer","songwriter","dancer"],
          "imageUrl": "https://upload.wikimedia.org/wikipedia/commons/0/04/Michael_Jackson_1984.jpg",
          "discography": [
            {"id": "1","title": "Thriller","releaseDate": "1982","type": "앨범","role": "작곡"},
            {"id": "2","title": "Bad","releaseDate": "1987","type": "앨범","role": "작곡"},
            {"id": "3","title": "Dangerous","releaseDate": "1991","type": "앨범","role": "작곡"},
            {"id": "4","title": "HIStory","releaseDate": "1995","type": "앨범","role": "작곡"}
          ],
          "activities": [
            {"id": "1","year": "1980s","title": "팝의 황제 등극","description": "'Thriller' 등 메가히트를 통해 세계 최고의 팝스타로 군림","type": "수상"},
            {"id": "2","year": "1990s","title": "새로운 시도와 논란","description": "음악, 뮤직비디오 분야에서 혁신을 계속하면서도 사생활 이슈로 화제","type": "활동"},
            {"id": "3","year": "2000s","title": "유산","description": "사망 이후에도 그의 음악과 영향력은 전 세계에 지속됨","type": "유산"}
          ]
        }
      },
      "textBlocks": [
        {"id": "1","title": "생애와 경력","content": "Michael Jackson는 세계 음악계에서 독보적인 위치를 차지한 아티스트입니다.","depth": 1},
        {"id": "2","title": "음악적 스타일","content": "다양한 장르를 넘나들며 혁신적인 음악을 만들어냈습니다.","depth": 1},
        {"id": "3","title": "문화적 영향","content": "음악뿐만 아니라 패션, 예술, 영상 등 다양한 분야에서 대중문화의 아이콘으로 자리잡았습니다.","depth": 1}
      ]
    }
    $$::jsonb, 'r1', '2025-10-27 09:06:00', 'wiki-art-2', '1')
ON CONFLICT (revision_id) DO UPDATE SET content = EXCLUDED.content;

INSERT INTO page_revision (revision_id, content, revision_number, created_at, wiki_id, oauth_id) VALUES
    ('rev-art-3', $$
    {
      "categoryData": {
        "type": "artist",
        "data": {
          "name": "Madonna",
          "country": "미국",
          "activePeriod": "1979-현재",
          "roles": ["singer","songwriter","actress"],
          "imageUrl": "https://upload.wikimedia.org/wikipedia/commons/1/19/Madonna_in_concert_wearing_fishnets_1987.jpg",
          "discography": [
            {"id": "1","title": "Like a Virgin","releaseDate": "1984","type": "앨범","role": "작곡"},
            {"id": "2","title": "True Blue","releaseDate": "1986","type": "앨범","role": "작곡"},
            {"id": "3","title": "Ray of Light","releaseDate": "1998","type": "앨범","role": "작곡"},
            {"id": "4","title": "Confessions on a Dance Floor","releaseDate": "2005","type": "앨범","role": "작곡"}
          ],
          "activities": [
            {"id": "1","year": "1980s","title": "팝 아이콘 탄생","description": "과감한 패션과 퍼포먼스로 세계적 여성 팝 아이콘으로 부상","type": "데뷔"},
            {"id": "2","year": "1990s","title": "장르 혁신","description": "전자음악, 댄스팝 등 다양한 스타일을 도입하며 음악적 변신","type": "활동"},
            {"id": "3","year": "2000s","title": "문화적 영향","description": "음악뿐만 아니라 영화, 패션 등 다방면에서 영향력을 행사","type": "수상"}
          ]
        }
      },
      "textBlocks": [
        {"id": "1","title": "생애와 경력","content": "Madonna는 세계 음악계에서 독보적인 위치를 차지한 아티스트입니다.","depth": 1},
        {"id": "2","title": "음악적 스타일","content": "다양한 장르를 넘나들며 개성 있는 음악 세계를 구축하였습니다.","depth": 1},
        {"id": "3","title": "문화적 영향","content": "음악뿐만 아니라 패션, 영상 등 여러 방면에서 대중문화에 큰 영향을 미쳤습니다.","depth": 1}
      ]
    }
    $$::jsonb, 'r1', '2025-10-27 09:07:00', 'wiki-art-3', '1')
ON CONFLICT (revision_id) DO UPDATE SET content = EXCLUDED.content;

INSERT INTO page_revision (revision_id, content, revision_number, created_at, wiki_id, oauth_id) VALUES
    ('rev-art-4', $$
    {
      "categoryData": {
        "type": "artist",
        "data": {
          "name": "Freddie Mercury",
          "country": "영국",
          "activePeriod": "1969-1991",
          "roles": ["singer","songwriter"],
          "imageUrl": "https://upload.wikimedia.org/wikipedia/commons/5/51/Freddie_Mercury_%28159442459%29.jpg",
          "discography": [
            {"id": "1","title": "A Night at the Opera (with Queen)","releaseDate": "1975","type": "앨범","role": "작곡"},
            {"id": "2","title": "The Game (with Queen)","releaseDate": "1980","type": "앨범","role": "작곡"},
            {"id": "3","title": "Mr. Bad Guy","releaseDate": "1985","type": "앨범","role": "작곡"},
            {"id": "4","title": "Innuendo (with Queen)","releaseDate": "1991","type": "앨범","role": "작곡"}
          ],
          "activities": [
            {"id": "1","year": "1970s","title": "퀸 결성 및 성공","description": "록 밴드 퀸의 리드보컬로서 세계적인 성공을 거둠","type": "데뷔"},
            {"id": "2","year": "1985","title": "라이브 에이드 전설","description": "1985년 라이브 에이드 공연에서 역사적인 무대를 선보임","type": "콘서트"},
            {"id": "3","year": "1990s","title": "유산","description": "사망 후에도 그의 음악과 무대 매너는 후대 아티스트들에게 큰 영향을 줌","type": "유산"}
          ]
        }
      },
      "textBlocks": [
        {"id": "1","title": "생애와 경력","content": "Freddie Mercury는 세계 음악계에서 독보적인 위치를 차지한 아티스트입니다.","depth": 1},
        {"id": "2","title": "음악적 스타일","content": "다양한 장르를 넘나들며 혁신적인 음악을 만들어냈습니다.","depth": 1},
        {"id": "3","title": "문화적 영향","content": "음악뿐만 아니라 패션, 예술, 영상 등 다양한 분야에서 대중문화의 아이콘으로 자리잡았습니다.","depth": 1}
      ]
    }
    $$::jsonb, 'r1', '2025-10-27 09:08:00', 'wiki-art-4', '1')
ON CONFLICT (revision_id) DO UPDATE SET content = EXCLUDED.content;

INSERT INTO page_revision (revision_id, content, revision_number, created_at, wiki_id, oauth_id) VALUES
    ('rev-art-5', $$
    {
      "categoryData": {
        "type": "artist",
        "data": {
          "name": "서태지",
          "country": "대한민국",
          "activePeriod": "1992-현재",
          "roles": ["singer","songwriter","producer"],
          "imageUrl": "https://upload.wikimedia.org/wikipedia/commons/7/78/141020_%EC%84%9C%ED%83%9C%EC%A7%80_03.jpg",
          "discography": [
            {"id": "1","title": "서태지와 아이들 I","releaseDate": "1992","type": "앨범","role": "작곡"},
            {"id": "2","title": "서태지와 아이들 II","releaseDate": "1993","type": "앨범","role": "작곡"},
            {"id": "3","title": "Seo Tai Ji (솔로 1집)","releaseDate": "1998","type": "앨범","role": "작곡"},
            {"id": "4","title": "Seotaiji 7th Issue","releaseDate": "2004","type": "앨범","role": "작곡"}
          ],
          "activities": [
            {"id": "1","year": "1990s","title": "아이들 신드롬","description": "서태지와 아이들로 한국 가요계에 혁명적인 변화를 일으킴","type": "데뷔"},
            {"id": "2","year": "2000s","title": "솔로 활동","description": "록, 일렉트로니카 등 다양한 장르를 시도하며 솔로 활동 전개","type": "활동"},
            {"id": "3","year": "2010s","title": "문화적 영향","description": "데뷔 20주년을 넘어서도 신세대 뮤지션들에게 막대한 영향력을 끼침","type": "유산"}
          ]
        }
      },
      "textBlocks": [
        {"id": "1","title": "생애와 경력","content": "서태지는 세계 음악계에서 독보적인 위치를 차지한 아티스트입니다.","depth": 1},
        {"id": "2","title": "음악적 스타일","content": "다양한 장르를 넘나들며 개성 있는 음악 세계를 구축하였습니다.","depth": 1},
        {"id": "3","title": "문화적 영향","content": "음악뿐만 아니라 패션, 영상 등 다방면에서 대중문화에 큰 영향을 미쳤습니다.","depth": 1}
      ]
    }
    $$::jsonb, 'r1', '2025-10-27 09:09:00', 'wiki-art-5', '1')
ON CONFLICT (revision_id) DO UPDATE SET content = EXCLUDED.content;

-- LP 5
INSERT INTO page_revision (revision_id, content, revision_number, created_at, wiki_id, oauth_id) VALUES
    ('rev-lp-1', $$
    {
      "categoryData": {
        "type": "lp",
        "data": {
          "infobox": {
            "title": "Sgt. Pepper's Lonely Hearts Club Band",
            "artist": "The Beatles",
            "coverUrl": "https://image.yes24.com/goods/57766390/XL",
            "releaseDate": "1967-06-01",
            "genre": "Rock",
            "label": "Parlophone",
            "tableColor": "#f0f0f0",
            "lpInfos": [
              {
                "id": "1",
                "alias": "50th Anniversary LP",
                "material": "Vinyl",
                "rpm": "33 1/3",
                "diameter": "12 inch",
                "weight": "180g",
                "pressingCountry": "UK",
                "pressingInfo": "50주년 기념반",
                "condition": "New",
                "isColored": false,
                "labelType": "Picture Disc",
                "format": "LP",
                "specialNotes": "Original album remastered"
              }
            ]
          },
          "tracklist": {
            "tracks": [
              {"id": "1","number": "1","title": "Sgt. Pepper's Lonely Hearts Club Band","length": "2:02"},
              {"id": "2","number": "2","title": "With a Little Help from My Friends","length": "2:44"},
              {"id": "3","number": "3","title": "Lucy in the Sky with Diamonds","length": "3:28"},
              {"id": "4","number": "4","title": "Getting Better","length": "2:47"},
              {"id": "5","number": "5","title": "Fixing a Hole","length": "2:37"},
              {"id": "6","number": "6","title": "She's Leaving Home","length": "3:25"},
              {"id": "7","number": "7","title": "Being for the Benefit of Mr. Kite!","length": "2:37"},
              {"id": "8","number": "8","title": "Within You Without You","length": "5:05"},
              {"id": "9","number": "9","title": "When I'm Sixty-Four","length": "2:37"},
              {"id": "10","number": "10","title": "Lovely Rita","length": "2:42"},
              {"id": "11","number": "11","title": "Good Morning Good Morning","length": "2:41"},
              {"id": "12","number": "12","title": "Sgt. Pepper's Lonely Hearts Club Band (Reprise)","length": "1:18"},
              {"id": "13","number": "13","title": "A Day in the Life","length": "5:33"}
            ]
          }
        }
      },
      "textBlocks": [
        {"id": "1","title": "앨범 개요","content": "Sgt. Pepper's Lonely Hearts Club Band는 The Beatles의 대표적인 음반으로, 발매 당시 큰 인기를 끌었습니다.","depth": 1},
        {"id": "2","title": "음악적 특징","content": "이 앨범은 독특한 음악적 시도와 완성도로 현재까지도 많은 사랑을 받고 있습니다.","depth": 1},
        {"id": "3","title": "영향과 평가","content": "평단과 대중으로부터 높은 평가를 받으며, 후대 작품들에 많은 영향을 미쳤습니다.","depth": 1}
      ]
    }
    $$::jsonb, 'r1', '2025-10-27 09:10:00', 'wiki-lp-1', '1')
ON CONFLICT (revision_id) DO UPDATE SET content = EXCLUDED.content;

INSERT INTO page_revision (revision_id, content, revision_number, created_at, wiki_id, oauth_id) VALUES
    ('rev-lp-2', $$
    {
      "categoryData": {
        "type": "lp",
        "data": {
          "infobox": {
            "title": "Thriller",
            "artist": "Michael Jackson",
            "coverUrl": "https://image.yes24.com/goods/18491063/XL",
            "releaseDate": "1982-11-30",
            "genre": "Pop, R&B",
            "label": "Epic",
            "tableColor": "#f8f8f8",
            "lpInfos": [
              {
                "id": "1",
                "alias": "Original US Pressing",
                "material": "Vinyl",
                "rpm": "33 1/3",
                "diameter": "12 inch",
                "weight": "140g",
                "pressingCountry": "USA",
                "pressingInfo": "First pressing",
                "condition": "Mint",
                "isColored": false,
                "labelType": "Standard",
                "format": "LP",
                "specialNotes": "Includes original poster"
              }
            ]
          },
          "tracklist": {
            "tracks": [
              {"id": "1","number": "1","title": "Wanna Be Startin' Somethin'","length": "6:03"},
              {"id": "2","number": "2","title": "Baby Be Mine","length": "4:20"},
              {"id": "3","number": "3","title": "The Girl Is Mine","length": "3:42"},
              {"id": "4","number": "4","title": "Thriller","length": "5:57"},
              {"id": "5","number": "5","title": "Beat It","length": "4:18"},
              {"id": "6","number": "6","title": "Billie Jean","length": "4:54"},
              {"id": "7","number": "7","title": "Human Nature","length": "4:06"},
              {"id": "8","number": "8","title": "P.Y.T. (Pretty Young Thing)","length": "3:59"},
              {"id": "9","number": "9","title": "The Lady in My Life","length": "4:59"}
            ]
          }
        }
      },
      "textBlocks": [
        {"id": "1","title": "앨범 개요","content": "Thriller는 Michael Jackson의 대표적인 음반으로, 발매 당시 큰 인기를 끌었습니다.","depth": 1},
        {"id": "2","title": "음악적 특징","content": "이 앨범은 독특한 음악적 시도와 완성도로 현재까지도 많은 사랑을 받고 있습니다.","depth": 1},
        {"id": "3","title": "영향과 평가","content": "평단과 대중으로부터 높은 평가를 받으며, 후대 작품들에 많은 영향을 미쳤습니다.","depth": 1}
      ]
    }
    $$::jsonb, 'r1', '2025-10-27 09:11:00', 'wiki-lp-2', '1')
ON CONFLICT (revision_id) DO UPDATE SET content = EXCLUDED.content;

INSERT INTO page_revision (revision_id, content, revision_number, created_at, wiki_id, oauth_id) VALUES
    ('rev-lp-3', $$
    {
      "categoryData": {
        "type": "lp",
        "data": {
          "infobox": {
            "title": "Nevermind",
            "artist": "Nirvana",
            "coverUrl": "https://image.yes24.com/goods/2828745/XL",
            "releaseDate": "1991-09-24",
            "genre": "Grunge, Alternative Rock",
            "label": "DGC",
            "tableColor": "#e8f0ff",
            "lpInfos": [
              {
                "id": "1",
                "alias": "30th Anniversary Edition",
                "material": "Vinyl",
                "rpm": "33 1/3",
                "diameter": "12 inch",
                "weight": "180g",
                "pressingCountry": "EU",
                "pressingInfo": "Remastered anniversary edition",
                "condition": "New",
                "isColored": true,
                "labelType": "Blue Vinyl",
                "format": "LP",
                "specialNotes": "Includes bonus 7-inch single"
              }
            ]
          },
          "tracklist": {
            "tracks": [
              {"id": "1","number": "1","title": "Smells Like Teen Spirit","length": "5:01"},
              {"id": "2","number": "2","title": "In Bloom","length": "4:14"},
              {"id": "3","number": "3","title": "Come as You Are","length": "3:39"},
              {"id": "4","number": "4","title": "Breed","length": "3:03"},
              {"id": "5","number": "5","title": "Lithium","length": "4:17"},
              {"id": "6","number": "6","title": "Polly","length": "2:57"},
              {"id": "7","number": "7","title": "Territorial Pissings","length": "2:22"},
              {"id": "8","number": "8","title": "Drain You","length": "3:44"},
              {"id": "9","number": "9","title": "Lounge Act","length": "2:37"},
              {"id": "10","number": "10","title": "Stay Away","length": "3:32"},
              {"id": "11","number": "11","title": "On a Plain","length": "3:16"},
              {"id": "12","number": "12","title": "Something in the Way","length": "3:52"},
              {"id": "13","number": "13","title": "Endless, Nameless","length": "6:44"}
            ]
          }
        }
      },
      "textBlocks": [
        {"id": "1","title": "앨범 개요","content": "Nevermind는 Nirvana의 대표적인 음반으로, 발매 당시 큰 인기를 끌었습니다.","depth": 1},
        {"id": "2","title": "음악적 특징","content": "이 앨범은 독특한 음악적 시도와 완성도로 현재까지도 많은 사랑을 받고 있습니다.","depth": 1},
        {"id": "3","title": "영향과 평가","content": "평단과 대중으로부터 높은 평가를 받으며, 후대 작품들에 많은 영향을 미쳤습니다.","depth": 1}
      ]
    }
    $$::jsonb, 'r1', '2025-10-27 09:12:00', 'wiki-lp-3', '1')
ON CONFLICT (revision_id) DO UPDATE SET content = EXCLUDED.content;

INSERT INTO page_revision (revision_id, content, revision_number, created_at, wiki_id, oauth_id) VALUES
    ('rev-lp-4', $$
    {
      "categoryData": {
        "type": "lp",
        "data": {
          "infobox": {
            "title": "Kind of Blue",
            "artist": "Miles Davis",
            "coverUrl": "https://image.yes24.com/goods/7450824/XL",
            "releaseDate": "1959-08-17",
            "genre": "Jazz",
            "label": "Columbia",
            "tableColor": "#f3e8ff",
            "lpInfos": [
              {
                "id": "1",
                "alias": "Legacy Edition LP",
                "material": "Vinyl",
                "rpm": "33 1/3",
                "diameter": "12 inch",
                "weight": "180g",
                "pressingCountry": "USA",
                "pressingInfo": "Legacy remaster",
                "condition": "New",
                "isColored": false,
                "labelType": "Standard",
                "format": "LP",
                "specialNotes": "Mono and stereo versions included"
              }
            ]
          },
          "tracklist": {
            "tracks": [
              {"id": "1","number": "1","title": "So What","length": "9:22"},
              {"id": "2","number": "2","title": "Freddie Freeloader","length": "9:46"},
              {"id": "3","number": "3","title": "Blue in Green","length": "5:37"},
              {"id": "4","number": "4","title": "All Blues","length": "11:33"},
              {"id": "5","number": "5","title": "Flamenco Sketches","length": "9:26"}
            ]
          }
        }
      },
      "textBlocks": [
        {"id": "1","title": "앨범 개요","content": "Kind of Blue는 Miles Davis의 대표적인 음반으로, 발매 당시 큰 인기를 끌었습니다.","depth": 1},
        {"id": "2","title": "음악적 특징","content": "이 앨범은 독특한 음악적 시도와 완성도로 현재까지도 많은 사랑을 받고 있습니다.","depth": 1},
        {"id": "3","title": "영향과 평가","content": "평단과 대중으로부터 높은 평가를 받으며, 후대 작품들에 많은 영향을 미쳤습니다.","depth": 1}
      ]
    }
    $$::jsonb, 'r1', '2025-10-27 09:13:00', 'wiki-lp-4', '1')
ON CONFLICT (revision_id) DO UPDATE SET content = EXCLUDED.content;

INSERT INTO page_revision (revision_id, content, revision_number, created_at, wiki_id, oauth_id) VALUES
    ('rev-lp-5', $$
    {
      "categoryData": {
        "type": "lp",
        "data": {
          "infobox": {
            "title": "서태지와 아이들 I",
            "artist": "서태지와 아이들",
            "coverUrl": "",
            "releaseDate": "1992-03-23",
            "genre": "Dance, Hip-Hop, Rock",
            "label": "반도음반",
            "tableColor": "#ffe8e8",
            "lpInfos": [
              {
                "id": "1",
                "alias": "Original 1992 LP",
                "material": "Vinyl",
                "rpm": "33 1/3",
                "diameter": "12 inch",
                "weight": "Unknown",
                "pressingCountry": "KOR",
                "pressingInfo": "First press",
                "condition": "VG+",
                "isColored": false,
                "labelType": "Standard",
                "format": "LP",
                "specialNotes": "Original insert included"
              }
            ]
          },
          "tracklist": {
            "tracks": [
              {"id": "1","number": "1","title": "Yo! Taiji!","length": "0:37"},
              {"id": "2","number": "2","title": "난 알아요 (Club Mix)","length": "3:50"},
              {"id": "3","number": "3","title": "환상 속의 그대","length": "3:25"},
              {"id": "4","number": "4","title": "너와 함께한 시간 속에서","length": "4:16"},
              {"id": "5","number": "5","title": "이 밤이 깊어가지만","length": "3:54"},
              {"id": "6","number": "6","title": "내 모든 것 (Live Mix)","length": "4:49"},
              {"id": "7","number": "7","title": "이제는","length": "4:18"},
              {"id": "8","number": "8","title": "Blind Love (English Version)","length": "3:58"},
              {"id": "9","number": "9","title": "Rock'N Roll Dance ('92 Heavy Mix)","length": "3:11"},
              {"id": "10","number": "10","title": "Missing...","length": "1:08"}
            ]
          }
        }
      },
      "textBlocks": [
        {"id": "1","title": "앨범 개요","content": "서태지와 아이들 I는 서태지와 아이들의 대표적인 음반으로, 발매 당시 큰 인기를 끌었습니다.","depth": 1},
        {"id": "2","title": "음악적 특징","content": "이 앨범은 독특한 음악적 시도와 완성도로 현재까지도 많은 사랑을 받고 있습니다.","depth": 1},
        {"id": "3","title": "영향과 평가","content": "평단과 대중으로부터 높은 평가를 받으며, 후대 작품들에 많은 영향을 미쳤습니다.","depth": 1}
      ]
    }
    $$::jsonb, 'r1', '2025-10-27 09:14:00', 'wiki-lp-5', '1')
ON CONFLICT (revision_id) DO UPDATE SET content = EXCLUDED.content;

-- 3) 각 페이지의 current_revision을 실제 리비전으로 설정
UPDATE wiki_page SET current_revision = 'rev-eq-1'  WHERE wiki_id='wiki-eq-1';
UPDATE wiki_page SET current_revision = 'rev-eq-2'  WHERE wiki_id='wiki-eq-2';
UPDATE wiki_page SET current_revision = 'rev-eq-3'  WHERE wiki_id='wiki-eq-3';
UPDATE wiki_page SET current_revision = 'rev-eq-4'  WHERE wiki_id='wiki-eq-4';
UPDATE wiki_page SET current_revision = 'rev-eq-5'  WHERE wiki_id='wiki-eq-5';

UPDATE wiki_page SET current_revision = 'rev-art-1' WHERE wiki_id='wiki-art-1';
UPDATE wiki_page SET current_revision = 'rev-art-2' WHERE wiki_id='wiki-art-2';
UPDATE wiki_page SET current_revision = 'rev-art-3' WHERE wiki_id='wiki-art-3';
UPDATE wiki_page SET current_revision = 'rev-art-4' WHERE wiki_id='wiki-art-4';
UPDATE wiki_page SET current_revision = 'rev-art-5' WHERE wiki_id='wiki-art-5';

UPDATE wiki_page SET current_revision = 'rev-lp-1'  WHERE wiki_id='wiki-lp-1';
UPDATE wiki_page SET current_revision = 'rev-lp-2'  WHERE wiki_id='wiki-lp-2';
UPDATE wiki_page SET current_revision = 'rev-lp-3'  WHERE wiki_id='wiki-lp-3';
UPDATE wiki_page SET current_revision = 'rev-lp-4'  WHERE wiki_id='wiki-lp-4';
UPDATE wiki_page SET current_revision = 'rev-lp-5'  WHERE wiki_id='wiki-lp-5';
