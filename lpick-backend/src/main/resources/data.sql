INSERT INTO tier(tier_id, name, point_scope) VALUES
                        ('1', 'dummy_tier', 300) ON CONFLICT (tier_id) DO NOTHING;
INSERT INTO user_info(oauth_id, nickname, profile, point, stack_point, about, lpti, tier_id) VALUES
                        ('1', 'mock_user', '', 0, 0, '자기소개', null, '1') ON CONFLICT (oauth_id) DO NOTHING;
INSERT INTO wiki_page(wiki_id, title, current_revision, status) VALUES
                        ('wiki-1', 'dummy_wikipage', 'r3', 'OPEN') ON CONFLICT (wiki_id) DO NOTHING;
INSERT INTO wiki_page(wiki_id, title, current_revision, status) VALUES
                        ('wiki-2', 'dummy_wikipage2', null, 'OPEN') ON CONFLICT (wiki_id) DO NOTHING;
INSERT INTO wiki_page(wiki_id, title, current_revision, status) VALUES
                        ('wiki-3', 'dummy_wikipage3', null, 'OPEN') ON CONFLICT (wiki_id) DO NOTHING;

-- PageRevision 기입
INSERT INTO page_revision(revision_id, content, revision_number, created_at, wiki_id, oauth_id) VALUES
    ('revision-6', '위키내용입니다.\n이거저거많이추가됐습니다', 'r1', '2025-06-05 00:21:12', 'wiki-3', '1') ON CONFLICT (revision_id) DO NOTHING;
INSERT INTO page_revision(revision_id, content, revision_number, created_at, wiki_id, oauth_id) VALUES
    ('revision-5', '위키내용입니다.\n그럭저럭추가', 'r2', '2025-06-05 09:34:12', 'wiki-2', '1') ON CONFLICT (revision_id) DO NOTHING;
INSERT INTO page_revision(revision_id, content, revision_number, created_at, wiki_id, oauth_id) VALUES
    ('revision-4', '위키내용입니다.', 'r3', '2025-06-04 14:34:32', 'wiki-1', '1') ON CONFLICT (revision_id) DO NOTHING;

INSERT INTO page_revision(revision_id, content, revision_number, created_at, wiki_id, oauth_id) VALUES
    ('revision-3', '위키내용입니다.\n이거저거많이추가됐습니다', 'r2', '2025-06-04 12:34:32', 'wiki-1', '1') ON CONFLICT (revision_id) DO NOTHING;
INSERT INTO page_revision(revision_id, content, revision_number, created_at, wiki_id, oauth_id) VALUES
    ('revision-2', '위키내용입니다.\n그럭저럭추가', 'r1', '2025-06-05 09:32:12', 'wiki-2', '1') ON CONFLICT (revision_id) DO NOTHING;
INSERT INTO page_revision(revision_id, content, revision_number, created_at, wiki_id, oauth_id) VALUES
    ('revision-1', '위키내용입니다.', 'r1', '2025-06-03 12:20:23', 'wiki-1', '1') ON CONFLICT (revision_id) DO NOTHING;




-- Role 기입
-- 중재자, 매니저, 어드민(본 서비스 개발자들)
INSERT INTO auth(auth_id, name) VALUES
                        ('1', 'MEDIATOR') ON CONFLICT (auth_id) DO NOTHING ;
INSERT INTO auth(auth_id, name) VALUES
                        ('2', 'MANAGER') ON CONFLICT (auth_id) DO NOTHING ;
INSERT INTO auth(auth_id, name) VALUES
                        ('3', 'ADMIN') ON CONFLICT (auth_id) DO NOTHING ;
