package sh.stock.exception;

public class ProductNotFoundException extends BusinessException {

    public ProductNotFoundException(Long productId) {
        super(ErrorCode.PRODUCT_NOT_FOUND,
                "상품을 찾을 수 없습니다. 상품ID: %d".formatted(productId));
    }
}
