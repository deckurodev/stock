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
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("재고 동시성 통합 테스트 (PostgreSQL Testcontainers)")
class StockConcurrencyTest extends IntegrationTestSupport {

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
    @DisplayName("동일 상품에 100건 동시 입고 시 수량이 정확히 누적된다 (Lost Update 방지)")
    void concurrentInboundOnSameProduct() throws InterruptedException {
        String name = "동시성-입고";
        Product product = productRepository.save(new Product(name));
        stockRepository.save(Stock.empty(product.getId()));
        Long productId = product.getId();

        int threadCount = 100;
        ExecutorService executor = Executors.newFixedThreadPool(16);
        CountDownLatch latch = new CountDownLatch(threadCount);

        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                try {
                    inboundService.inbound(name, 1);
                } finally {
                    latch.countDown();
                }
            });
        }
        latch.await();
        executor.shutdown();

        Stock result = stockRepository.findByProductId(productId).orElseThrow();
        assertThat(result.getQuantity()).isEqualTo(threadCount);
        assertThat(stockTransactionRepository.findByProductIdOrderByCreatedAtDesc(productId))
                .hasSize(threadCount);
    }

    @Test
    @DisplayName("미등록 상품명으로 100건 동시 입고 시 상품/재고는 1행만 생성되고 수량이 정확히 누적된다 (UNIQUE 충돌 UPSERT 흡수)")
    void concurrentInboundOnNewProduct() throws InterruptedException {
        String name = "동시성-신규입고";

        int threadCount = 100;
        ExecutorService executor = Executors.newFixedThreadPool(16);
        CountDownLatch latch = new CountDownLatch(threadCount);

        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                try {
                    inboundService.inbound(name, 1);
                } finally {
                    latch.countDown();
                }
            });
        }
        latch.await();
        executor.shutdown();

        Product product = productRepository.findByName(name).orElseThrow();
        Stock result = stockRepository.findByProductId(product.getId()).orElseThrow();
        assertThat(result.getQuantity()).isEqualTo(threadCount);
        assertThat(stockTransactionRepository.findByProductIdOrderByCreatedAtDesc(product.getId()))
                .hasSize(threadCount);
    }

    @Test
    @DisplayName("재고 100개에 200건 동시 출고 시 100건만 성공하고 재고는 음수 없이 0이 된다")
    void concurrentOutboundOnSameProduct() throws InterruptedException {
        Product product = productRepository.save(new Product("동시성-출고"));
        stockRepository.save(new Stock(product.getId(), 100));
        Long productId = product.getId();

        int threadCount = 200;
        ExecutorService executor = Executors.newFixedThreadPool(16);
        CountDownLatch latch = new CountDownLatch(threadCount);
        AtomicInteger success = new AtomicInteger();
        AtomicInteger failure = new AtomicInteger();

        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                try {
                    outboundService.outbound(productId, 1);
                    success.incrementAndGet();
                } catch (RuntimeException e) {
                    failure.incrementAndGet();
                } finally {
                    latch.countDown();
                }
            });
        }
        latch.await();
        executor.shutdown();

        Stock result = stockRepository.findByProductId(productId).orElseThrow();
        assertThat(result.getQuantity()).isZero();
        assertThat(success.get()).isEqualTo(100);
        assertThat(failure.get()).isEqualTo(100);
        assertThat(stockTransactionRepository.findByProductIdOrderByCreatedAtDesc(productId))
                .hasSize(100);
    }
}
