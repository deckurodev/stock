package sh.stock.service;

import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sh.stock.service.dto.ProductListResponse;
import sh.stock.service.dto.ProductResponse;
import sh.stock.exception.ProductNotFoundException;

import java.util.List;

import static sh.stock.jooq.Tables.PRODUCT;
import static sh.stock.jooq.Tables.STOCK;

@Service
@RequiredArgsConstructor
public class StockQueryService {
    private final DSLContext dsl;

    @Transactional(readOnly = true)
    public ProductResponse getStock(Long productId) {
        return dsl.select(PRODUCT.ID, PRODUCT.NAME, STOCK.QUANTITY)
                .from(STOCK)
                .join(PRODUCT).on(STOCK.PRODUCT_ID.eq(PRODUCT.ID))
                .where(PRODUCT.ID.eq(productId))
                .fetchOptional()
                .map(record -> new ProductResponse(
                        record.get(PRODUCT.ID),
                        record.get(PRODUCT.NAME),
                        record.get(STOCK.QUANTITY)))
                .orElseThrow(() -> new ProductNotFoundException(productId));
    }

    @Transactional(readOnly = true)
    public ProductListResponse getAll() {
        List<ProductResponse> products = dsl.select(PRODUCT.ID, PRODUCT.NAME, STOCK.QUANTITY)
                .from(STOCK)
                .join(PRODUCT).on(STOCK.PRODUCT_ID.eq(PRODUCT.ID))
                .orderBy(PRODUCT.ID)
                .fetch(record -> new ProductResponse(
                        record.get(PRODUCT.ID),
                        record.get(PRODUCT.NAME),
                        record.get(STOCK.QUANTITY)));
        return ProductListResponse.of(products);
    }
}
