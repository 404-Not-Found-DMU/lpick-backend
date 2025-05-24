INSERT INTO tier(tier_id, name, point_scope) VALUES
                        ('1', 'dummy_tier', 300) ON CONFLICT (tier_id) DO NOTHING;
INSERT INTO user_info(oauth_id, nickname, profile, point, stack_point, about, lpti, tier_id) VALUES
                        ('1', 'mock_user', '', 0, 0, '자기소개', null, '1') ON CONFLICT (oauth_id) DO NOTHING;
INSERT INTO wiki_page(wiki_id, title, current_revision, status) VALUES
                        ('1', 'dummy_wikipage', null, '') ON CONFLICT (wiki_id) DO NOTHING;

-- Role 기입
-- 중재자, 매니저, 어드민(본 서비스 개발자들)
INSERT INTO auth(auth_id, name) VALUES
                        ('1', 'MEDIATOR') ON CONFLICT (auth_id) DO NOTHING ;
INSERT INTO auth(auth_id, name) VALUES
                        ('2', 'MANAGER') ON CONFLICT (auth_id) DO NOTHING ;
INSERT INTO auth(auth_id, name) VALUES
                        ('3', 'ADMIN') ON CONFLICT (auth_id) DO NOTHING ;
