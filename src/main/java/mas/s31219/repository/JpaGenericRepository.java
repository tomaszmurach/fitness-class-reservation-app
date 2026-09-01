package mas.s31219.repository;

import jakarta.persistence.EntityManager;

import java.util.List;
import java.util.Optional;

public class JpaGenericRepository<T> implements GenericRepository<T> {

    protected final EntityManager entityManager;
    private final Class<T> entityClass;

    public JpaGenericRepository(EntityManager entityManager, Class<T> entityClass) {
        if (entityManager == null) {
            throw new IllegalArgumentException("EntityManager nie może być null.");
        }
        if (entityClass == null) {
            throw new IllegalArgumentException("Klasa encji nie może być null.");
        }

        this.entityManager = entityManager;
        this.entityClass = entityClass;
    }

    @Override
    public Optional<T> findById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Identyfikator nie może być null.");
        }

        return Optional.ofNullable(entityManager.find(entityClass, id));
    }

    @Override
    public List<T> findAll() {
        String jpql = "SELECT e FROM " + entityClass.getSimpleName() + " e";

        return entityManager
                .createQuery(jpql, entityClass)
                .getResultList();
    }

    @Override
    public T save(T entity) {
        if (entity == null) {
            throw new IllegalArgumentException("Encja nie może być null.");
        }

        entityManager.persist(entity);
        return entity;
    }

    @Override
    public T update(T entity) {
        if (entity == null) {
            throw new IllegalArgumentException("Encja nie może być null.");
        }

        return entityManager.merge(entity);
    }

    @Override
    public void delete(T entity) {
        if (entity == null) {
            throw new IllegalArgumentException("Encja nie może być null.");
        }

        T managedEntity = entityManager.contains(entity)
                ? entity
                : entityManager.merge(entity);

        entityManager.remove(managedEntity);
    }
}