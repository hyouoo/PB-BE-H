package com.example.purebasketbe.domain.recipe.dto;

import com.example.purebasketbe.domain.product.entity.Event;
import com.example.purebasketbe.domain.product.entity.Product;
import com.example.purebasketbe.domain.recipe.entity.Recipe;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record RecipeResponseDto(
        Long id,
        String name,
        String info,
        String imgUrl,
        List<RelatedProductResponseDto> products
) {

    @Builder
    public RecipeResponseDto {
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


    @Builder
    private record RelatedProductResponseDto(
            Long id,
            String name,
            Integer price,
            Event event,
            String imgUrl
    ) {

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
