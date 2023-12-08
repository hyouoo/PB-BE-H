package com.example.purebasketbe.domain.recipe.entity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RecipeProductRepository extends JpaRepository<RecipeProduct, Long> {

}
