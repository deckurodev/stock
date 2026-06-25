package sh.stock.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import sh.stock.UnitTestSupport;
import sh.stock.domain.Stock;
import sh.stock.exception.InsufficientStockException;
import sh.stock.exception.ProductNotFoundException;
import sh.stock.domain.repository.StockRepository;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@DisplayName("StockDomainService 단위 테스트")
class StockDomainServiceTest extends UnitTestSupport {

    @Mock
    private StockRepository stockRepository;
    @InjectMocks
    private StockDomainService stockDomainService;

    @Test
    @DisplayName("재고가 있으면 비관적 락으로 조회 후 수량을 증가시킨다")
    void increaseExisting() {
        Stock stock = new Stock(1L, 5);
        given(stockRepository.findByProductIdForUpdate(1L)).willReturn(Optional.of(stock));

        long result = stockDomainService.increase(1L, 10);

        assertThat(result).isEqualTo(15);
        verify(stockRepository, never()).save(any());
    }

    @Test
    @DisplayName("재고 행이 없으면 새로 생성한 뒤 수량을 증가시킨다")
    void increaseNew() {
        given(stockRepository.findByProductIdForUpdate(1L)).willReturn(Optional.empty());
        given(stockRepository.save(any(Stock.class))).willReturn(Stock.empty(1L));

        long result = stockDomainService.increase(1L, 10);

        assertThat(result).isEqualTo(10);
        verify(stockRepository).save(any(Stock.class));
    }

    @Test
    @DisplayName("재고가 충분하면 비관적 락으로 조회 후 수량을 감소시킨다")
    void decreaseSufficient() {
        Stock stock = new Stock(1L, 10);
        given(stockRepository.findByProductIdForUpdate(1L)).willReturn(Optional.of(stock));

        long result = stockDomainService.decrease(1L, 4);

        assertThat(result).isEqualTo(6);
    }

    @Test
    @DisplayName("재고가 부족하면 예외가 발생하고 수량이 변하지 않는다")
    void decreaseInsufficient() {
        Stock stock = new Stock(1L, 3);
        given(stockRepository.findByProductIdForUpdate(1L)).willReturn(Optional.of(stock));

        assertThatThrownBy(() -> stockDomainService.decrease(1L, 5))
                .isInstanceOf(InsufficientStockException.class);
        assertThat(stock.getQuantity()).isEqualTo(3);
    }

    @Test
    @DisplayName("출고 시 재고 행이 없으면 예외가 발생한다")
    void decreaseMissing() {
        given(stockRepository.findByProductIdForUpdate(99L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> stockDomainService.decrease(99L, 1))
                .isInstanceOf(ProductNotFoundException.class);
    }
}
