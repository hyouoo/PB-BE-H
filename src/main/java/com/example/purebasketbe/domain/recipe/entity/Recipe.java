package com.example.purebasketbe.domain.recipe.entity;

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

    @OneToMany(mappedBy = "recipe", cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    @OrderBy("product.id asc")
    private List<RecipeProduct> recipeProductList;

    @Builder
    private Recipe(String name, String info, String imgUrl, List<RecipeProduct> recipeProductList) {
        this.name = name;
        this.info = info;
        this.imgUrl = imgUrl;
        this.recipeProductList = recipeProductList;
    }

    public static Recipe from(RecipeRequestDto requestDto, String imgUrl) {
        return Recipe.builder()
                .name(requestDto.getName())
                .info(requestDto.getInfo())
                .imgUrl(imgUrl)
                .recipeProductList(new ArrayList<>())
                .build();
    }

    public void addRecipeProduct(RecipeProduct recipeProduct) {
        this.recipeProductList.add(recipeProduct);
        recipeProduct.addRecipe(this);
    }
}

