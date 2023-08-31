package com.microservices.ProductService.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import com.microservices.ProductService.entity.Product;
import com.microservices.ProductService.excpetion.CustomException;
import com.microservices.ProductService.model.ProductRequest;
import com.microservices.ProductService.model.ProductResponse;
import com.microservices.ProductService.repository.ProductRepository;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Service
@AllArgsConstructor
@Log4j2
public class ProductService {

    private ProductRepository productRepository;

    public List<ProductResponse> getAll() {
        return productRepository.findAll()
                .stream()
                .map(product -> {
                    ProductResponse productResponse = new ProductResponse();
                    BeanUtils.copyProperties(product, productResponse);
                    return productResponse;
                }).collect(Collectors.toList());
    }

    public ProductResponse getById(long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(
                        () -> new CustomException("Product with id " + id + " not found", "PRODUCT_NOT_FOUND", 404));
        return productResponseMap(product);
    }

    public ProductResponse create(ProductRequest productRequest) {
        Product product = new Product();
        BeanUtils.copyProperties(productRequest, product);
        productRepository.save(product);
        return productResponseMap(product);
    }

    public ProductResponse update(long id, ProductRequest productRequest) {
        getById(id);
        Product product = new Product();
        product.setId(id);
        BeanUtils.copyProperties(productRequest, product); // ModelMapper

        // product.setName(productRequest.getName());
        // product.setPrice(productRequest.getPrice());
        // product.setQuantity(productRequest.getQuantity());

        productRepository.save(product);
        return productResponseMap(product);
    }

    public ProductResponse delete(long id) {
        ProductResponse productResponse = getById(id);
        productRepository.deleteById(id);
        return productResponse;
    }

    public static ProductResponse productResponseMap(Product product) {
        ProductResponse productResponse = new ProductResponse();
        BeanUtils.copyProperties(product, productResponse);
        return productResponse;
    }

    public void checkAvailableProduct(long productId, int quantity) {
        log.info("Validation the quantity of the product");
        ProductResponse product = getById(productId);
        if (product.getQuantity() < quantity) {
            throw new CustomException(
                    "Product does not have sufficient Quantity",
                    "INSUFFICIENT_QUANTITY",
                    400);
        }
    }

    public void reduceQuantity(long productId, int quantity) {
        ProductResponse productRes = getById(productId);
        Product product = new Product();
        BeanUtils.copyProperties(productRes, product);

        product.setQuantity(product.getQuantity() - quantity);
        productRepository.save(product);
        log.info("Product Quantity updated Successfully");
    }
}
