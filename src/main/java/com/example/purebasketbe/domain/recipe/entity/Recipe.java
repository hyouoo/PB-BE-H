package com.example.purebasketbe.domain.recipe.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
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

    @OneToMany(mappedBy = "recipe")
    @OrderBy("product.id asc")
    private List<RecipeProduct> recipeProductList = new ArrayList<>();
}

