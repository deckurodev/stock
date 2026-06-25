package sh.stock.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sh.stock.domain.StockTransaction;
import sh.stock.domain.repository.StockTransactionRepository;

@Service
@RequiredArgsConstructor
public class StockTransactionService {
    private final StockTransactionRepository stockTransactionRepository;

    @Transactional
    public void recordInbound(Long productId, long quantity) {
        stockTransactionRepository.save(StockTransaction.inbound(productId, quantity));
    }

    @Transactional
    public void recordOutbound(Long productId, long quantity) {
        stockTransactionRepository.save(StockTransaction.outbound(productId, quantity));
    }
}
