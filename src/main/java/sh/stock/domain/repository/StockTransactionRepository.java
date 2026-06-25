package sh.stock.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sh.stock.domain.StockTransaction;

import java.util.List;

public interface StockTransactionRepository extends JpaRepository<StockTransaction, Long> {

    List<StockTransaction> findByProductIdOrderByCreatedAtDesc(Long productId);
}
