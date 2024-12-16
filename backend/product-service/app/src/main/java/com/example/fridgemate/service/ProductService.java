package com.example.fridgemate.service;

import com.example.fridgemate.dto.ProductDto;
import com.example.fridgemate.entity.ProductEntity;
import com.example.fridgemate.entity.ProductTypeEntity;
import com.example.fridgemate.exception.ProductException;
import com.example.fridgemate.repository.ProductRepository;
import com.example.fridgemate.repository.ProductTypeRepository;
import com.example.fridgemate.util.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

@Service
public class ProductService {
    private static final Logger logger = LoggerFactory.getLogger(ProductService.class);
    private final ProductRepository productRepository;
    private final ProductTypeRepository productTypeRepository;
    private final RedisTemplate<String, Object> redisTemplate;
    private final Executor executor;

    @Autowired
    public ProductService(ProductRepository productRepository,
                          ProductTypeRepository productTypeRepository,
                          RedisTemplate<String, Object> redisTemplate,
                          @Qualifier("productExecutor") Executor executor) {
        this.productRepository = productRepository;
        this.productTypeRepository = productTypeRepository;
        this.redisTemplate = redisTemplate;
        this.executor = executor;
    }

    /**
     * METHOD: addProduct.
     * This method add new product to personal db.
     *
     * @param token Token.
     * @param dto Product's data.
     * @return {@link ProductEntity} or ProductException.
     */
    @Transactional
    public CompletableFuture<ProductEntity> addProduct(String token, ProductDto dto) {
        return CompletableFuture.supplyAsync(() -> {
            ProductTypeEntity type = productTypeRepository.findById(Long.parseLong(dto.getTypeId())).orElseThrow(() -> new ProductException("Invalid type ID"));
            int quantity = Integer.parseInt(dto.getQuantity());
            BigDecimal amount = new BigDecimal(dto.getAmount());
            LocalDate expiryDate;
            if (dto.getExpiryDate() == null) {
                expiryDate = LocalDate.now().plusDays(type.getShelfDays());
            } else {
                expiryDate = LocalDate.parse(dto.getExpiryDate());
            }
            if (!isValidProduct(quantity, amount, expiryDate) || !isToken(token)) {
                logger.error("Incorrect product data");
                throw new ProductException("Incorrect product data.");
            }
            ProductEntity product = new ProductEntity();
            product.setToken(token);
            product.setName(dto.getName());
            product.setAddedDate(LocalDate.now());
            product.setAmount(amount);
            product.setExpiryDate(expiryDate);
            product.setQuantity(quantity);
            product.setType(type);
            ProductEntity savedProduct = productRepository.save(product);
            redisTemplate.opsForValue().set("product: " + savedProduct.getId(), savedProduct, 24, TimeUnit.HOURS);
            logger.info("Product added: Id: {}", savedProduct.getId());
            return savedProduct;
        }, executor);
    }

    /**
     * METHOD: isToken.
     * This method check validation of token.
     *
     * @param token Probable token.
     * @return true if token is valid else false.
     */
    private Boolean isToken(String token) { return JwtUtil.validateToken(token); }

    /**
     * METHOD: isProductExpired.
     * This method check expiry of product.
     *
     * @param product Product.
     * @return true if product is expired else false.
     */
    private boolean isProductExpired(ProductEntity product) {
        return product.getExpiryDate().isAfter(LocalDate.now());
    }

    /**
     * METHOD: isProductMissing.
     * This method check mission of product.
     *
     * @param product Product.
     * @return true if product is missing else false.
     */
    private boolean isProductMissing(ProductEntity product) {
        return (product.getExpiryDate().isBefore(LocalDate.now())
                && ChronoUnit.DAYS.between(LocalDate.now(), product.getExpiryDate()) <= 1);
    }

    /**
     * METHOD: getExpiredProducts.
     * This method get expired personal products from db.
     *
     * @param token Token.
     * @return List of {@link ProductEntity}.
     */
    public CompletableFuture<List<ProductEntity>> getExpiredProducts(String token) {
        return CompletableFuture.supplyAsync(() -> {
            List<ProductEntity> products = productRepository.findAllByToken(token);
            logger.info("Retrieved expired products: Count: {}", products.size());
            return products.stream().filter(this::isProductExpired).toList();
        }, executor);
    }

    /**
     * METHOD: getMissingProducts.
     * This method get missing personal products from db.
     *
     * @param token Token.
     * @return List of {@link ProductEntity}.
     */
    public CompletableFuture<List<ProductEntity>> getMissingProducts(String token) {
        return CompletableFuture.supplyAsync(() -> {
            List<ProductEntity> products = productRepository.findAllByToken(token);
            logger.info("Retrieved missing products: Count: {}", products.size());
            return products.stream().filter(this::isProductMissing).toList();
        }, executor);
    }

    /**
     * METHOD: isValidProduct.
     * This method check validation of product.
     *
     * @param quantity Product quantity.
     * @param amount Product amount.
     * @param expiryDate Product expiry date.
     * @return true if product is valid else false.
     */
    private boolean isValidProduct(int quantity, BigDecimal amount, LocalDate expiryDate) {
        return quantity > 0
                && expiryDate.isAfter(LocalDate.now())
                && amount.compareTo(BigDecimal.ZERO) > 0;
    }

    /**
     * METHOD: getAllProducts.
     * This method get all personal products from db.
     *
     * @param token Token.
     * @return List of {@link ProductEntity}.
     */
    public CompletableFuture<List<ProductEntity>> getAllProducts(String token) {
        return CompletableFuture.supplyAsync(() -> {
            if (!isToken(token)) {
                logger.error("Incorrect token");
                throw new ProductException("Incorrect token.");
            }
            List<ProductEntity> products = productRepository.findAllByToken(token);
            logger.info("Retrieved all products: Count: {}", products.size());
            return products;
        }, executor);
    }

    /**
     * METHOD: findProductById.
     * This method find and get product by id from db.
     *
     * @param id Identity of product.
     * @return An optional {@link ProductEntity}.
     */
    public CompletableFuture<Optional<ProductEntity>> findProductById(Long id) {
        return CompletableFuture.supplyAsync(() -> {
            ProductEntity cachedProduct = null;
            Object cachedProductObj = redisTemplate.opsForValue().get("product: " + id);
            if (cachedProductObj instanceof ProductEntity) {
                cachedProduct = (ProductEntity) cachedProductObj;
            }
            if (cachedProduct == null) {
                Optional<ProductEntity> gotProduct = productRepository.findById(id);
                gotProduct.ifPresent(product -> {
                    redisTemplate.opsForValue().set("product: " + id, product, 24, TimeUnit.HOURS);
                    logger.info("Product {} found", product.getId());
                });
                return gotProduct;
            }
            logger.info("Product {} found", cachedProduct.getId());
            return Optional.of(cachedProduct);
        }, executor);
    }

    /**
     * METHOD: deleteProductById.
     * This method delete product by id from db.
     *
     * @param id Identity of product.
     * @return A message confirming that product by id have been deleted.
     */
    public CompletableFuture<Void> deleteProductById(Long id) {
        return CompletableFuture.runAsync(() -> {
            productRepository.deleteById(id);
            logger.info("Deleted product by ID: {}", id);
            redisTemplate.delete("product: " + id);
        }, executor);
    }

    /**
     * METHOD: deleteAllProducts.
     * This method delete all personal products from db.
     *
     * @param token Token.
     * @return A message confirming that all products have been deleted or ProductException.
     */
    public CompletableFuture<Void> deleteAllProducts(String token) {
        return getAllProducts(token).thenAccept(products -> {
            if (products.isEmpty()) {
                logger.error("No products for delete");
                throw new ProductException("No products.");
            }
            productRepository.deleteAll();
            logger.info("Deleted all products");
            products.forEach(product -> {
                redisTemplate.delete("product: " + product.getId());
            });
        }).exceptionally(exception -> {
            logger.error("Deleted products error");
            throw new ProductException("Deleted products error: " + exception.getMessage());
        });
    }

    /**
     * METHOD: deleteExpiredProduct.
     * This method delete all personal expired products from db.
     *
     * @param token Token.
     * @return A message confirming that all expired products have been deleted or ProductException.
     */
    public CompletableFuture<Void> deleteExpiredProduct(String token) {
        return getExpiredProducts(token).thenAccept(products -> {
            if (products.isEmpty()) {
                logger.error("No expired products for delete");
                throw new ProductException("No expired products.");
            }
            productRepository.deleteAllInBatch(products);
            products.forEach(product -> {
                redisTemplate.delete("product: " + product.getId());
            });
        }).exceptionally(exception -> {
            logger.error("Deleted expired products error");
            throw new ProductException("Deleted expired products error: " + exception.getMessage());
        });
    }

    /**
     * METHOD: findProductByType.
     * This method find and get personal products by type from db.
     *
     * @param typeId Type identity of product.
     * @param token Token of product.
     * @return List of {@link ProductEntity} or ProductException.
     */
    public CompletableFuture<Optional<List<ProductEntity>>> findProductByType(Long typeId, String token) {
        return CompletableFuture.supplyAsync(() -> {
            if (!isToken(token)) {
                logger.error("Incorrect token");
                throw new ProductException("Incorrect token.");
            }
            Optional<ProductTypeEntity> type = productTypeRepository.findById(typeId);
            if (type.isEmpty()) {
                logger.error("Incorrect product type");
                throw new ProductException("Incorrect product type.");
            }
            Optional<List<ProductEntity>> gotProducts = productRepository.findAllByTypeAndToken(type.get(), token);
            gotProducts.ifPresent(products -> {
                products.forEach(product -> {
                    redisTemplate.opsForValue().set("product: " + product.getId(), product, 24, TimeUnit.HOURS);
                });
                logger.info("Retrieved all products: Count: {}", products.size());
            });
            return gotProducts;
        }, executor);
    }

    /**
     * METHOD: getAllProductTypes.
     * This method get all product types from db.
     *
     * @return List of {@link ProductTypeEntity}.
     */
    public CompletableFuture<List<ProductTypeEntity>> getAllProductTypes() {
        return CompletableFuture.supplyAsync(() -> {
            List<ProductTypeEntity> types = productTypeRepository.findAll();
            logger.info("Retrieved all product types: Count: {}", types.size());
            return types;
        }, executor);
    }

    /**
     * METHOD: findProductTypeByName.
     * This method get product type by name from db.
     *
     * @param name Type name.
     * @return An optional {@link ProductTypeEntity}.
     */
    public CompletableFuture<Optional<ProductTypeEntity>> findProductTypeByName(String name) {
        return CompletableFuture.supplyAsync(() -> {
            ProductTypeEntity cachedType = null;
            Object cachedTypeObj = redisTemplate.opsForValue().get("type: " + name);
            if (cachedTypeObj instanceof ProductTypeEntity) {
                cachedType = (ProductTypeEntity) cachedTypeObj;
            }
            if (cachedType == null) {
                Optional<ProductTypeEntity> gotType = productTypeRepository.findByName(name);
                gotType.ifPresent(type -> {
                    redisTemplate.opsForValue().set("type: " + name, type, 24, TimeUnit.HOURS);
                    logger.info("Product type {} found", type.getId());
                });
                return gotType;
            }
            logger.info("Product type {} found", cachedType.getId());
            return Optional.of(cachedType);
        }, executor);
    }
}
