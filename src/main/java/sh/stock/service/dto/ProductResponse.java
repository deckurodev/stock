package sh.stock.service.dto;

import sh.stock.domain.Product;

public record ProductResponse(Long id, String name, long quantity) {

    public static ProductResponse of(Product product, long quantity) {
        return new ProductResponse(product.getId(), product.getName(), quantity);
    }
}
