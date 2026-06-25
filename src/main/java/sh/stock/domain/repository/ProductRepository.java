package sh.stock.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import sh.stock.domain.Product;

import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {

    Optional<Product> findByName(String name);

    boolean existsByName(String name);

    @Query(value = "select pg_advisory_xact_lock(hashtext(:name))", nativeQuery = true)
    void lockByName(@Param("name") String name);
}
