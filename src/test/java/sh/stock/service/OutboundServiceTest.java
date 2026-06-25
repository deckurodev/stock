package sh.stock.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import sh.stock.UnitTestSupport;
import sh.stock.domain.Product;
import sh.stock.service.dto.ProductResponse;
import sh.stock.exception.ProductNotFoundException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@DisplayName("OutboundService 단위 테스트")
class OutboundServiceTest extends UnitTestSupport {

    @Mock
    private ProductService productService;
    @Mock
    private StockDomainService stockDomainService;
    @Mock
    private StockTransactionService stockTransactionService;
    @InjectMocks
    private OutboundService outboundService;

    @Test
    @DisplayName("재고를 감소시키고 출고 이력을 남긴 뒤 현재 수량을 반환한다")
    void outbound() {
        given(stockDomainService.decrease(1L, 4)).willReturn(6L);
        given(productService.getById(1L)).willReturn(new Product("상품"));

        ProductResponse result = outboundService.outbound(1L, 4);

        assertThat(result.quantity()).isEqualTo(6);
        verify(stockTransactionService).recordOutbound(1L, 4);
    }

    @Test
    @DisplayName("재고 감소가 실패하면 이력을 남기지 않는다")
    void outboundFails() {
        willThrow(new ProductNotFoundException(99L)).given(stockDomainService).decrease(99L, 1);

        assertThatThrownBy(() -> outboundService.outbound(99L, 1))
                .isInstanceOf(ProductNotFoundException.class);
        verifyNoInteractions(stockTransactionService);
    }
}
