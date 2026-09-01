package mas.s31219.repository;

import java.util.List;
import java.util.Optional;

public interface GenericRepository<T> {

    Optional<T> findById(Long id);

    List<T> findAll();

    T save(T entity);

    T update(T entity);

    void delete(T entity);
}