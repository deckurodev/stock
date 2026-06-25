package sh.stock.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sh.stock.exception.InsufficientStockException;

@Entity
@Table(name = "stock")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Stock extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "product_id", nullable = false, updatable = false)
    private Long productId;

    @Column(nullable = false)
    private long quantity;

    public Stock(Long productId, long quantity) {
        this.productId = productId;
        this.quantity = quantity;
    }

    public static Stock empty(Long productId) {
        return new Stock(productId, 0L);
    }

    public void increase(long amount) {
        validatePositive(amount);
        this.quantity += amount;
    }

    public void decrease(long amount) {
        validatePositive(amount);
        if (this.quantity < amount) {
            throw new InsufficientStockException(this.quantity, amount);
        }
        this.quantity -= amount;
    }

    private void validatePositive(long amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("수량은 1 이상이어야 합니다. 요청: %d".formatted(amount));
        }
    }
}
