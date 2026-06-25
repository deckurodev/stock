package sh.stock.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sh.stock.domain.Product;
import sh.stock.service.dto.ProductResponse;

@Service
@RequiredArgsConstructor
public class InboundService {
    private final ProductService productService;
    private final StockDomainService stockDomainService;
    private final StockTransactionService stockTransactionService;

    @Transactional
    public ProductResponse inbound(String name, long quantity) {
        Product product = productService.getOrCreate(name);
        long stockQuantity = stockDomainService.increase(product.getId(), quantity);
        stockTransactionService.recordInbound(product.getId(), quantity);
        return ProductResponse.of(product, stockQuantity);
    }
}
