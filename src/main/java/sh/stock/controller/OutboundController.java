package sh.stock.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sh.stock.controller.dto.OutboundRequest;
import sh.stock.service.dto.ProductResponse;
import sh.stock.service.OutboundService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/stocks")
public class OutboundController {
    private final OutboundService outboundService;

    @PostMapping("/outbound")
    public ProductResponse outbound(@Valid @RequestBody OutboundRequest request) {
        return outboundService.outbound(request.productId(), request.quantity());
    }
}
