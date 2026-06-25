package sh.stock.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import sh.stock.UnitTestSupport;
import sh.stock.exception.InsufficientStockException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Stock 도메인 단위 테스트")
class StockTest extends UnitTestSupport {

    @Test
    @DisplayName("입고하면 수량이 증가한다")
    void increase() {
        Stock stock = new Stock(1L, 5);

        stock.increase(10);

        assertThat(stock.getQuantity()).isEqualTo(15);
    }

    @Test
    @DisplayName("출고하면 수량이 감소한다")
    void decrease() {
        Stock stock = new Stock(1L, 10);

        stock.decrease(4);

        assertThat(stock.getQuantity()).isEqualTo(6);
    }

    @Test
    @DisplayName("잔량 전체를 출고하면 0이 된다")
    void decreaseToZero() {
        Stock stock = new Stock(1L, 7);

        stock.decrease(7);

        assertThat(stock.getQuantity()).isZero();
    }

    @Test
    @DisplayName("재고보다 많은 수량을 출고하면 예외가 발생한다")
    void decreaseMoreThanQuantity() {
        Stock stock = new Stock(1L, 3);

        assertThatThrownBy(() -> stock.decrease(5))
                .isInstanceOf(InsufficientStockException.class)
                .hasMessageContaining("재고가 부족");

        assertThat(stock.getQuantity()).isEqualTo(3);
    }

    @Test
    @DisplayName("입고 수량이 0 이하이면 예외가 발생한다")
    void increaseWithNonPositive() {
        Stock stock = new Stock(1L, 5);

        assertThatThrownBy(() -> stock.increase(0))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> stock.increase(-1))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("출고 수량이 0 이하이면 예외가 발생한다")
    void decreaseWithNonPositive() {
        Stock stock = new Stock(1L, 5);

        assertThatThrownBy(() -> stock.decrease(0))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> stock.decrease(-1))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
