package com.notfound.lpickbackend.common._super;

import com.notfound.lpickbackend.common.exception.CustomException;
import com.notfound.lpickbackend.common.exception.ErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BaseQueryServiceTest {

    // 1) 테스트용 엔티티: ID만 갖는 아주 단순한 자식 클래스
    static class DummyEntity extends BaseEntity {
        // BaseEntity 의 prePersist 로직을 피하려면
        public DummyEntity(String id) { setId(id); }
        public void setId(String id) { super.setId(id); }
    }

    // 2) BaseCommandService 를 구현한 아주 단순한 서브클래스
    static class DummyService extends BaseQueryService<DummyEntity, String> {
        private final JpaRepository<DummyEntity, String> repo;
        public DummyService(JpaRepository<DummyEntity, String> repo) {
            this.repo = repo;
        }
        @Override protected JpaRepository<DummyEntity, String> getRepository() {
            return repo;
        }
        @Override protected ErrorCode notFoundErrorCode() {
            return ErrorCode.NOT_FOUND_GEAR; // 실제로는 각 서비스에 맞는 코드로 사용
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
    void findById_whenExists_shouldReturn() {
        DummyEntity found = new DummyEntity("ID3");
        when(mockRepo.findById("ID3")).thenReturn(Optional.of(found));

        DummyEntity result = service.findById("ID3");

        assertThat(result).isSameAs(found);
    }

    @Test
    void findById_whenNotExists_shouldThrow() {
        when(mockRepo.findById("MISSING")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findById("MISSING"))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.NOT_FOUND_GEAR);
    }

    @Test
    void findAll_shouldDelegateToRepoFindAll() {
        List<DummyEntity> list = List.of(new DummyEntity("A"), new DummyEntity("B"));
        when(mockRepo.findAll()).thenReturn(list);

        List<DummyEntity> result = service.findAll();

        assertThat(result).isEqualTo(list);
    }
}