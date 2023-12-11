package com.example.purebasketbe.domain.recipe.entity;

import com.example.purebasketbe.domain.product.entity.Product;
import com.example.purebasketbe.domain.recipe.dto.RecipeRequestDto;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@Table(name = "recipe")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Recipe {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column
    private String info;

    @Column
    private String imgUrl;


    @ManyToMany
    @JoinTable(name = "recipe_product",
                joinColumns = @JoinColumn(name = "recipe_id"),
                inverseJoinColumns = @JoinColumn(name = "product_id"))
    private List<Product> productList = new ArrayList<>();


    @Builder
    private Recipe(String name, String info, String imgUrl, List<Product> productList) {
        this.name = name;
        this.info = info;
        this.imgUrl = imgUrl;
        this.productList = productList;
    }

    public static Recipe from(RecipeRequestDto requestDto, String imgUrl) {
        return Recipe.builder()
                .name(requestDto.getName())
                .info(requestDto.getInfo())
                .imgUrl(imgUrl)
                .productList(new ArrayList<>())
                .build();
    }

    public void addProduct(Product product) {
        this.productList.add(product);
    }
}

