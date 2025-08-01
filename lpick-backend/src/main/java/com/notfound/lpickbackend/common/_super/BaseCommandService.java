package com.notfound.lpickbackend.common._super;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

public abstract class BaseCommandService<T extends BaseEntity, String> {
    protected abstract JpaRepository<T, String> getRepository();

    @Transactional
    public T saveEntity(T entity) {
        return getRepository().save(entity);
    }

    @Transactional
    public void deleteById(String id) {
        getRepository().deleteById(id);
    }
}
