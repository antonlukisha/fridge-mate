package com.example.fridgemate.service;

import com.example.fridgemate.entity.ProductEntity;
import com.example.fridgemate.exception.ProductException;
import com.example.fridgemate.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final RedisTemplate<String, Object> redisTemplate;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final Executor executor;

    @Autowired
    public ProductService(ProductRepository productRepository,
                          RedisTemplate<String, Object> redisTemplate,
                          KafkaTemplate<String, String> kafkaTemplate,
                          @Qualifier("productExecutor") Executor executor) {
        this.productRepository = productRepository;
        this.redisTemplate = redisTemplate;
        this.kafkaTemplate = kafkaTemplate;
        this.executor = executor;
    }

    public CompletableFuture<ProductEntity> addProduct(ProductEntity product) {
        return CompletableFuture.supplyAsync(() -> {
            if (isValidProduct(product)) {
                ProductEntity savedProduct = productRepository.save(product);
                redisTemplate.opsForValue().set("product: " + savedProduct.getId(), savedProduct, 24, TimeUnit.HOURS);
                kafkaTemplate.send("product-topic", "Saved product: " + savedProduct.getId() + ", UID: " + savedProduct.getUserId());
                return savedProduct;
            } else {
                kafkaTemplate.send("product-topic", "Incorrect products data. ID: " + product.getId() + ", UID: " + product.getUserId());
                throw new ProductException("Incorrect products data.");
            }
        }, executor);
    }

    private boolean isProductExpired(ProductEntity product) {
        return product.getExpiryDate().isAfter(LocalDate.now());
    }

    private boolean isProductMissing(ProductEntity product) {
        return (product.getExpiryDate().isBefore(LocalDate.now())
                && ChronoUnit.DAYS.between(LocalDate.now(), product.getExpiryDate()) <= 1);
    }

    public CompletableFuture<List<ProductEntity>> getExpiredProducts(Long userId) {
        return CompletableFuture.supplyAsync(() -> {
            List<ProductEntity> products = productRepository.findAllByUserId(userId);
            kafkaTemplate.send("product-topic", "Retrieved expired products. Count: " + products.size() + ", UID: " + userId);
            return products.stream().filter(this::isProductExpired).toList();
        }, executor);
    }

    public CompletableFuture<List<ProductEntity>> getMissingProducts(Long userId) {
        return CompletableFuture.supplyAsync(() -> {
            List<ProductEntity> products = productRepository.findAllByUserId(userId);
            kafkaTemplate.send("product-topic", "Retrieved missing products. Count: " + products.size() + ", UID: " + userId);
            return products.stream().filter(this::isProductMissing).toList();
        }, executor);
    }

    private boolean isValidProduct(ProductEntity product) {
        return product.getQuantity() > 0
                && product.getExpiryDate().isAfter(LocalDate.now());
    }

    public CompletableFuture<List<ProductEntity>> getAllProducts(Long userId) {
        return CompletableFuture.supplyAsync(() -> {
            List<ProductEntity> products = productRepository.findAllByUserId(userId);
            kafkaTemplate.send("product-topic", "Retrieved all products: Count: " + products.size() + ", UID: " + userId);
            return products;
        }, executor);
    }

    public CompletableFuture<Optional<ProductEntity>> findProductById(Long id) {
        return CompletableFuture.supplyAsync(() -> {
            ProductEntity cachedProduct = (ProductEntity) redisTemplate.opsForValue().get("product: " + id);
            kafkaTemplate.send("product-topic", "Product found in cache");
            if (cachedProduct == null) {
                Optional<ProductEntity> gotProduct = productRepository.findById(id);
                gotProduct.ifPresent(product -> {
                    redisTemplate.opsForValue().set("product: " + id, product, 24, TimeUnit.HOURS);
                    kafkaTemplate.send("product-topic", "Retrieved product by ID: " + id + ", Type: " + product.getType());
                });
                return gotProduct;
            }
            kafkaTemplate.send("product-topic", "Retrieved product by ID: " + id + ", Type: " + cachedProduct.getType());
            return Optional.of(cachedProduct);
        }, executor);
    }

    public CompletableFuture<Void> deleteProduct(Long id) {
        return CompletableFuture.runAsync(() -> {
            productRepository.deleteById(id);
            redisTemplate.delete("product: " + id);
            kafkaTemplate.send("product-topic", "Deleted product: " + id);
        }, executor);
    }

    public CompletableFuture<Void> deleteAllProducts(Long userId) {
        return getAllProducts(userId).thenAccept(products -> {
            if (products.isEmpty()) {
                kafkaTemplate.send("product-topic", "No products for deleting UID: " + userId);
                throw new ProductException("No products.");
            }
            productRepository.deleteAll();
            products.forEach(product -> {
                redisTemplate.delete("product: " + product.getId());
                kafkaTemplate.send("product-topic", "Deleted product: " + product.getId());
            });
            kafkaTemplate.send("product-topic", "Deleted all products: Count: " + products.size() + ", UID: " + userId);
        }).exceptionally(exception -> {
            kafkaTemplate.send("product-topic", "Deleted products error: " + exception.getMessage() + ", UID: " + userId);
            throw new ProductException("Deleted products error: " + exception.getMessage());
        });
    }

    public CompletableFuture<Void> deleteExpiredProduct(Long userId) {
        return getExpiredProducts(userId).thenAccept(products -> {
            if (products.isEmpty()) {
                kafkaTemplate.send("product-topic", "No expired products for deleting UID: " + userId);
                throw new ProductException("No expired products.");
            }
            productRepository.deleteAllInBatch(products);
            products.forEach(product -> {
                redisTemplate.delete("product: " + product.getId());
                kafkaTemplate.send("product-topic", "Deleted expired product: " + product.getId() + ", UID: " + userId);
            });
            kafkaTemplate.send("product-topic", "Deleted all expired products: Count: " + products.size() + ", UID: " + userId);
        }).exceptionally(exception -> {
            kafkaTemplate.send("product-topic", "Deleted expired products error: " + exception.getMessage() + ", UID: " + userId);
            throw new ProductException("Deleted expired products error: " + exception.getMessage());
        });
    }


    public CompletableFuture<Optional<List<ProductEntity>>> findProductByType(String type, Long userId) {
        return CompletableFuture.supplyAsync(() -> {
            Optional<List<ProductEntity>> gotProducts = productRepository.findAllByTypeAndUserId(type, userId);
            gotProducts.ifPresent(products -> products.forEach(product -> {
                redisTemplate.opsForValue().set("product: " + product.getId(), product, 24, TimeUnit.HOURS);
                kafkaTemplate.send("product-topic", "Retrieved product by type: " + type + ", ID: " + product.getId() + ", UID: " + product.getUserId());
            }));
            return gotProducts;
        }, executor);
    }
}
