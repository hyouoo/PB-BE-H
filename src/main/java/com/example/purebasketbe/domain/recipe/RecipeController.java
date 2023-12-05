package com.example.purebasketbe.domain.recipe;

import com.example.purebasketbe.domain.recipe.dto.RecipeDetailResponseDto;
import com.example.purebasketbe.domain.recipe.dto.RecipeResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/recipes")
public class RecipeController {

    private final RecipeService recipeService;

    @GetMapping
    public ResponseEntity<Page<RecipeResponseDto>> getRecipes(@RequestParam(defaultValue = "1") int page) {
        Page<RecipeResponseDto> responseBody = recipeService.getRecipes(page - 1);
        return ResponseEntity.status(HttpStatus.OK).body(responseBody);
    }

    @GetMapping("/{recipeId}")
    public ResponseEntity<RecipeDetailResponseDto> getRecipe(@PathVariable Long recipeId) {
        RecipeDetailResponseDto responseBody = recipeService.getRecipe(recipeId);
        return ResponseEntity.status(HttpStatus.OK).body(responseBody);
    }



}
