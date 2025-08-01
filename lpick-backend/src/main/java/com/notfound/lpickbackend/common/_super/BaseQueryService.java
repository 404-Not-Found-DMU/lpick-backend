package com.notfound.lpickbackend.common._super;

import com.notfound.lpickbackend.common.exception.CustomException;
import com.notfound.lpickbackend.common.exception.ErrorCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public abstract class BaseQueryService<T extends BaseEntity, String> {
    protected abstract JpaRepository<T, String> getRepository();
    protected abstract ErrorCode notFoundErrorCode();

    @Transactional(readOnly = true)
    public T findById(String id) {
        return getRepository()
                .findById(id)
                .orElseThrow(() -> new CustomException(notFoundErrorCode()));
    }

    @Transactional(readOnly = true)
    public List<T> findAll() {
        return getRepository().findAll();
    }

}
