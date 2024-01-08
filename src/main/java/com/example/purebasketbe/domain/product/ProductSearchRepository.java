package com.example.purebasketbe.domain.product;

import com.example.purebasketbe.domain.product.entity.ProductDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;


public interface ProductSearchRepository extends ElasticsearchRepository<ProductDocument, Long> {

}
