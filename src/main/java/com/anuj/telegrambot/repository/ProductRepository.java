package com.anuj.telegrambot.repository;

import com.anuj.telegrambot.model.db.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {

    Optional<Product> findByServerProductId(Long serverProductId);

}
