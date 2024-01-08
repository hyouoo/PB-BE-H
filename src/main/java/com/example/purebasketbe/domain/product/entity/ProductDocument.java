package com.example.purebasketbe.domain.product.entity;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Setting;
import org.springframework.data.elasticsearch.annotations.Mapping;

@Getter
@Document(indexName = "product_doc")
@Setting(settingPath = "/elastic-settings.json")
@Mapping(mappingPath = "/product-mappings.json")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductDocument {
    @Id
    private Long id;

    private String name;

    private String info;

    private String category;

    private Event event;

    @Builder
    private ProductDocument(Long id, String name, String info, String category, Event event) {
        this.id = id;
        this.name = name;
        this.info = info;
        this.category = category;
        this.event = event;
    }

    public static ProductDocument from(Product product) {
        return ProductDocument.builder()
                .id(product.getId())
                .name(product.getName())
                .info(product.getInfo())
                .category(product.getCategory())
                .event(product.getEvent())
                .build();
    }
}
