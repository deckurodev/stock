package sh.stock.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sh.stock.domain.Product;
import sh.stock.service.dto.ProductResponse;

@Service
@RequiredArgsConstructor
public class OutboundService {
    private final ProductService productService;
    private final StockDomainService stockDomainService;
    private final StockTransactionService stockTransactionService;

    @Transactional
    public ProductResponse outbound(Long productId, long quantity) {
        long stockQuantity = stockDomainService.decrease(productId, quantity);
        stockTransactionService.recordOutbound(productId, quantity);
        Product product = productService.getById(productId);
        return ProductResponse.of(product, stockQuantity);
    }
}
