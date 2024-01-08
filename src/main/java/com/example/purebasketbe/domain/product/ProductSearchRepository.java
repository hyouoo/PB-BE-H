package com.example.purebasketbe.domain.product;

import com.example.purebasketbe.domain.product.entity.ProductDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductSearchRepository extends ElasticsearchRepository<ProductDocument, Long> {

}
