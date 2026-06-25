package sh.stock.service.dto;

import java.util.List;

public record ProductListResponse(int count, List<ProductResponse> products) {

    public static ProductListResponse of(List<ProductResponse> products) {
        return new ProductListResponse(products.size(), products);
    }
}
