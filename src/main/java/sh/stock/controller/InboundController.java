package sh.stock.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sh.stock.controller.dto.InboundRequest;
import sh.stock.service.dto.ProductResponse;
import sh.stock.service.InboundService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/stocks")
public class InboundController {
    private final InboundService inboundService;

    @PostMapping("/inbound")
    public ProductResponse inbound(@Valid @RequestBody InboundRequest request) {
        return inboundService.inbound(request.name(), request.quantity());
    }
}
