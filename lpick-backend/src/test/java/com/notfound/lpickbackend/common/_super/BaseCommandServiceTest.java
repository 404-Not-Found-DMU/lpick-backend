package com.notfound.lpickbackend.common._super;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.repository.JpaRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class BaseCommandServiceTest {

    // 1) 테스트용 엔티티: ID만 갖는 아주 단순한 자식 클래스
    static class DummyEntity extends BaseEntity {
        public DummyEntity(String id) { setId(id); }
        public void setId(String id) { super.setId(id); }
    }

    // 2) BaseCommandService 를 구현한 아주 단순한 서브클래스
    static class DummyService extends BaseCommandService<DummyEntity, String> {
        private final JpaRepository<DummyEntity, String> repo;
        public DummyService(JpaRepository<DummyEntity, String> repo) {
            this.repo = repo;
        }
        @Override protected JpaRepository<DummyEntity, String> getRepository() {
            return repo;
        }
    }

    @Mock
    private JpaRepository<DummyEntity, String> mockRepo;

    private DummyService service;

    @BeforeEach
    void setUp() {
        service = new DummyService(mockRepo);
    }

    @Test
    void saveEntity_shouldDelegateToRepoSave() {
        DummyEntity in = new DummyEntity(null);
        DummyEntity out = new DummyEntity("ID1");
        when(mockRepo.save(in)).thenReturn(out);

        DummyEntity result = service.saveEntity(in);

        assertThat(result).isSameAs(out);
        verify(mockRepo).save(in);
    }

    @Test
    void deleteById_shouldDelegateToRepoDelete() {
        doNothing().when(mockRepo).deleteById("ID2");

        service.deleteById("ID2");

        verify(mockRepo).deleteById("ID2");
    }
}