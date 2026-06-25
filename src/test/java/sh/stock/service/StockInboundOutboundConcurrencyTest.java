package sh.stock.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import sh.stock.IntegrationTestSupport;
import sh.stock.domain.Product;
import sh.stock.domain.Stock;
import sh.stock.domain.repository.ProductRepository;
import sh.stock.domain.repository.StockRepository;
import sh.stock.domain.repository.StockTransactionRepository;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("입고·출고 혼합 동시성 통합 테스트 (PostgreSQL Testcontainers)")
class StockInboundOutboundConcurrencyTest extends IntegrationTestSupport {

    @Autowired
    private InboundService inboundService;
    @Autowired
    private OutboundService outboundService;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private StockRepository stockRepository;
    @Autowired
    private StockTransactionRepository stockTransactionRepository;

    @AfterEach
    void tearDown() {
        stockTransactionRepository.deleteAllInBatch();
        stockRepository.deleteAllInBatch();
        productRepository.deleteAllInBatch();
    }

    @Test
    @DisplayName("동일 상품에 입고·출고를 동시에 섞어 요청해도 최종 수량과 이력이 정확하다")
    void concurrentInboundAndOutbound() throws InterruptedException {
        String name = "동시성-혼합";
        Product product = productRepository.save(new Product(name));
        stockRepository.save(new Stock(product.getId(), 100));
        Long productId = product.getId();

        int eachCount = 100;
        ExecutorService executor = Executors.newFixedThreadPool(16);
        CountDownLatch latch = new CountDownLatch(eachCount * 2);

        for (int i = 0; i < eachCount; i++) {
            executor.submit(() -> {
                try {
                    inboundService.inbound(name, 1);
                } finally {
                    latch.countDown();
                }
            });
            executor.submit(() -> {
                try {
                    outboundService.outbound(productId, 1);
                } finally {
                    latch.countDown();
                }
            });
        }
        latch.await();
        executor.shutdown();

        Stock result = stockRepository.findByProductId(productId).orElseThrow();
        assertThat(result.getQuantity()).isEqualTo(100);
        assertThat(stockTransactionRepository.findByProductIdOrderByCreatedAtDesc(productId))
                .hasSize(eachCount * 2);
    }
}
