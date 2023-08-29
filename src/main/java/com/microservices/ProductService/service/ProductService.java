package com.microservices.ProductService.service;

import com.microservices.ProductService.entity.Product;
import com.microservices.ProductService.excpetion.CustomException;
import com.microservices.ProductService.model.ProductRequest;
import com.microservices.ProductService.model.ProductResponse;
import com.microservices.ProductService.repository.ProductRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ProductService {

    private ProductRepository productRepository;

    public List<ProductResponse> getAll(){
        return productRepository.findAll()
                .stream()
                .map(product -> {
                    ProductResponse productResponse = new ProductResponse();
                    BeanUtils.copyProperties(product,productResponse);
                    return productResponse;
                }).collect(Collectors.toList());
    }

    public ProductResponse getById(long id){
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new CustomException("Product with id " + id + " not found","PRODUCT_NOT_FOUND",404));
        return productResponseMap(product);
    }


    public ProductResponse create(ProductRequest productRequest){
        Product product = new Product();
        BeanUtils.copyProperties(productRequest,product);
        productRepository.save(product);
        return productResponseMap(product);
    }

    public ProductResponse update(long id, ProductRequest productRequest){
        getById(id);
        Product product = new Product();
        product.setId(id);
        BeanUtils.copyProperties(productRequest, product); // ModelMapper

//        product.setName(productRequest.getName());
//        product.setPrice(productRequest.getPrice());
//        product.setQuantity(productRequest.getQuantity());

        productRepository.save(product);
        return productResponseMap(product);
    }

    public ProductResponse delete(long id){
        ProductResponse productResponse = getById(id);
        productRepository.deleteById(id);
        return productResponse;
    }


    public static ProductResponse productResponseMap(Product product){
        ProductResponse productResponse = new ProductResponse();
        BeanUtils.copyProperties(product,productResponse);
        return productResponse;
    }
}
