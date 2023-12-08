package com.example.purebasketbe.domain.recipe.dto;

import com.example.purebasketbe.domain.product.entity.Event;
import com.example.purebasketbe.domain.product.entity.Product;
import com.example.purebasketbe.domain.recipe.entity.Recipe;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import org.springframework.util.StringUtils;

import java.util.List;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RecipeResponseDto {
    private final Long id;
    private final String name;
    private final String info;
    private final String imgUrl;
    private final List<RelatedProductResponseDto> products;

    @Builder
    private RecipeResponseDto(Long id, String name, String info, String imgUrl, List<RelatedProductResponseDto> products) {
        this.id = id;
        this.name = name;
        this.info = info;
        this.imgUrl = imgUrl;
        this.products = products;
    }

    public static RecipeResponseDto from(Recipe recipe) {
        String imgUrl = StringUtils.hasText(recipe.getImgUrl()) ? recipe.getImgUrl() : "default Url";
        return RecipeResponseDto.builder()
                .id(recipe.getId())
                .name(recipe.getName())
                .info(recipe.getInfo())
                .imgUrl(imgUrl)
                .build();
    }

    public static RecipeResponseDto of(Recipe recipe, List<Product> productList) {
        List<RelatedProductResponseDto> products = productList.stream().map(RelatedProductResponseDto::from).toList();
        String imgUrl = StringUtils.hasText(recipe.getImgUrl()) ? recipe.getImgUrl() : "default Url";

        return RecipeResponseDto.builder()
                .id(recipe.getId())
                .name(recipe.getName())
                .info(recipe.getInfo())
                .imgUrl(imgUrl)
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
