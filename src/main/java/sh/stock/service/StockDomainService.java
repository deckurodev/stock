package sh.stock.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sh.stock.domain.Stock;
import sh.stock.exception.ProductNotFoundException;
import sh.stock.domain.repository.StockRepository;

@Service
@RequiredArgsConstructor
public class StockDomainService {
    private final StockRepository stockRepository;

    @Transactional
    public long increase(Long productId, long quantity) {
        Stock stock = stockRepository.findByProductIdForUpdate(productId)
                .orElseGet(() -> stockRepository.save(Stock.empty(productId)));
        stock.increase(quantity);
        return stock.getQuantity();
    }

    @Transactional
    public long decrease(Long productId, long quantity) {
        Stock stock = stockRepository.findByProductIdForUpdate(productId)
                .orElseThrow(() -> new ProductNotFoundException(productId));
        stock.decrease(quantity);
        return stock.getQuantity();
    }
}