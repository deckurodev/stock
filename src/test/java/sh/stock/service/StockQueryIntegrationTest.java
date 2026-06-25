package sh.stock.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import sh.stock.IntegrationTestSupport;
import sh.stock.service.dto.ProductListResponse;
import sh.stock.service.dto.ProductResponse;
import sh.stock.exception.ProductNotFoundException;
import sh.stock.domain.repository.ProductRepository;
import sh.stock.domain.repository.StockRepository;
import sh.stock.domain.repository.StockTransactionRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("재고 조회 통합 테스트 (jOOQ)")
class StockQueryIntegrationTest extends IntegrationTestSupport {

    @Autowired
    private InboundService inboundService;
    @Autowired
    private StockQueryService stockQueryService;
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
    @DisplayName("단건 조회 시 상품ID/이름/현재 수량을 반환한다")
    void getStock() {
        ProductResponse saved = inboundService.inbound("조회상품", 30);

        ProductResponse result = stockQueryService.getStock(saved.id());

        assertThat(result.id()).isEqualTo(saved.id());
        assertThat(result.name()).isEqualTo("조회상품");
        assertThat(result.quantity()).isEqualTo(30);
    }

    @Test
    @DisplayName("존재하지 않는 상품을 조회하면 예외가 발생한다")
    void getStockNotFound() {
        assertThatThrownBy(() -> stockQueryService.getStock(999_999L))
                .isInstanceOf(ProductNotFoundException.class);
    }

    @Test
    @DisplayName("목록 조회 시 count 와 함께 전체 상품 재고를 반환한다")
    void getAll() {
        inboundService.inbound("상품A", 10);
        inboundService.inbound("상품B", 20);

        ProductListResponse result = stockQueryService.getAll();

        assertThat(result.count()).isEqualTo(2);
        assertThat(result.products())
                .extracting(ProductResponse::name)
                .containsExactlyInAnyOrder("상품A", "상품B");
    }
}
