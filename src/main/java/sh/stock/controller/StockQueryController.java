package sh.stock.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sh.stock.service.dto.ProductListResponse;
import sh.stock.service.dto.ProductResponse;
import sh.stock.service.StockQueryService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/products")
public class StockQueryController {
    private final StockQueryService stockQueryService;

    @GetMapping("/{id}")
    public ProductResponse get(@PathVariable Long id) {
        return stockQueryService.getStock(id);
    }

    @GetMapping
    public ProductListResponse getAll() {
        return stockQueryService.getAll();
    }
}
