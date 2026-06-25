package sh.stock.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sh.stock.domain.Product;
import sh.stock.exception.ProductNotFoundException;
import sh.stock.domain.repository.ProductRepository;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;

    @Transactional
    public Product getOrCreate(String name) {
        productRepository.lockByName(name);
        if (!exists(name)) {
            return create(name);
        }
        return productRepository.findByName(name).orElseThrow();
    }

    private boolean exists(String name) {
        return productRepository.existsByName(name);
    }

    private Product create(String name) {
        return productRepository.save(new Product(name));
    }

    @Transactional(readOnly = true)
    public Product getById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));
    }
}
