package com.example.purebasketbe.domain.recipe.dto;

import com.example.purebasketbe.domain.product.entity.Event;
import com.example.purebasketbe.domain.product.entity.Product;
import com.example.purebasketbe.domain.recipe.entity.Recipe;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
public class RecipeDetailResponseDto {
    private final Long id;
    private final String name;
    private final String info;
    private final List<RelatedProductResponseDto> products;

    @Builder
    private RecipeDetailResponseDto(Long id, String name, String info, List<RelatedProductResponseDto> products) {
        this.id = id;
        this.name = name;
        this.info = info;
        this.products = products;
    }

    public static RecipeDetailResponseDto of(Recipe recipe, List<Product> productList) {
        List<RelatedProductResponseDto> products = productList.stream().map(RelatedProductResponseDto::from).toList();

        return RecipeDetailResponseDto.builder()
                .id(recipe.getId())
                .name(recipe.getName())
                .info(recipe.getInfo())
                .products(products)
                .build();
    }


    @Getter
    @Builder
    private static class RelatedProductResponseDto {
        private Long id;
        private String name;
        private Integer price;
        private Event event;
        private String imgUrl;

        private static RelatedProductResponseDto from(Product product) {
            String imgUrl = product.getImages().isEmpty() ? "default Url" : product.getImages().get(0).getImgUrl();
            return RelatedProductResponseDto.builder()
                    .id(product.getId())
                    .name(product.getName())
                    .price(product.getPrice())
                    .event(product.getEvent())
                    .imgUrl(imgUrl)
                    .build();
        }
    }

}
