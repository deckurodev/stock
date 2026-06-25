package sh.stock.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import sh.stock.UnitTestSupport;
import sh.stock.domain.Product;
import sh.stock.service.dto.ProductResponse;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@DisplayName("InboundService 단위 테스트")
class InboundServiceTest extends UnitTestSupport {

    @Mock
    private ProductService productService;
    @Mock
    private StockDomainService stockDomainService;
    @Mock
    private StockTransactionService stockTransactionService;
    @InjectMocks
    private InboundService inboundService;

    @Test
    @DisplayName("상품을 확보해 재고를 증가시키고 입고 이력을 남긴다")
    void inbound() {
        String name = "상품";
        Product product = new Product(name);
        given(productService.getOrCreate(name)).willReturn(product);
        given(stockDomainService.increase(product.getId(), 10)).willReturn(15L);

        ProductResponse result = inboundService.inbound(name, 10);

        assertThat(result.name()).isEqualTo(name);
        assertThat(result.quantity()).isEqualTo(15);
        verify(stockTransactionService).recordInbound(product.getId(), 10);
    }
}
