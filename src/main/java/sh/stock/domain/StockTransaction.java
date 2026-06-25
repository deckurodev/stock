package sh.stock.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "stock_transaction")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StockTransaction extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "product_id", nullable = false, updatable = false)
    private Long productId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20, updatable = false)
    private TransactionType type;

    @Column(nullable = false, updatable = false)
    private long quantity;

    private StockTransaction(Long productId, TransactionType type, long quantity) {
        this.productId = productId;
        this.type = type;
        this.quantity = quantity;
    }

    public static StockTransaction inbound(Long productId, long quantity) {
        return new StockTransaction(productId, TransactionType.INBOUND, quantity);
    }

    public static StockTransaction outbound(Long productId, long quantity) {
        return new StockTransaction(productId, TransactionType.OUTBOUND, quantity);
    }
}
