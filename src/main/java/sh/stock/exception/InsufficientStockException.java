package sh.stock.exception;

public class InsufficientStockException extends BusinessException {

    public InsufficientStockException(long current, long requested) {
        super(ErrorCode.INSUFFICIENT_STOCK,
                "재고가 부족합니다. 현재: %d, 요청: %d".formatted(current, requested));
    }
}
