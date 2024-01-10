package com.example.purebasketbe.domain.product.entity;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Mapping;
import org.springframework.data.elasticsearch.annotations.Setting;

import java.time.LocalDateTime;

@Getter
@Document(indexName = "product_doc")
@Setting(settingPath = "/elastic-settings.json")
@Mapping(mappingPath = "/product-mappings.json")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductDocument {
    @Id
//    @Field(type = FieldType.Long)
    private Long id;
    //    @Field(type = FieldType.Text)
    private String name;
    //    @Field(type = FieldType.Integer)
    private int price;
    //    @Field(type = FieldType.Text)
    private String info;
    //    @Field(type = FieldType.Text)
    private String category;
    //    @Field(type = FieldType.Text)
    private Event event;
    //    @Field(type = FieldType.Integer)
    private int discountRate;
    //    @Field(type = FieldType.Date)
    private LocalDateTime createdAt;
    //    @Field(type = FieldType.Date)
    private LocalDateTime modifiedAt;
    //    @Field(type = FieldType.Boolean)
    private boolean deleted;

    private String img_url;

    @Builder
    private ProductDocument(Long id, String name, int price, String info, String category, Event event, int discountRate,
                            LocalDateTime createdAt, LocalDateTime modifiedAt, boolean deleted, String img_url) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.info = info;
        this.category = category;
        this.event = event;
        this.discountRate = discountRate;
        this.createdAt = createdAt;
        this.modifiedAt = modifiedAt;
        this.deleted = deleted;
        this.img_url = img_url;
    }

    public static ProductDocument from(Product product) {
        return ProductDocument.builder()
                .id(product.getId())
                .name(product.getName())
                .price(product.getPrice())
                .info(product.getInfo())
                .category(product.getCategory())
                .event(product.getEvent())
                .discountRate(product.getDiscountRate())
                .createdAt(product.getCreatedAt())
                .modifiedAt(product.getModifiedAt())
                .deleted(product.isDeleted())
                .build();
    }
}
