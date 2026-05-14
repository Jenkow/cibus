package com.jll.cibus.common.config;

import com.jll.cibus.product.ProductCategory;
import com.jll.cibus.product.ProductCategoryRepository;
import com.jll.cibus.product.ProductEntity;
import com.jll.cibus.product.ProductRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class DataLoader {
    @Bean
    CommandLineRunner initDatabase(
            ProductRepository productRepository,
            ProductCategoryRepository categoryRepository) {

        return args -> {

            if (categoryRepository.count() == 0) {

                ProductCategory catPlatos = ProductCategory.builder().name("Platos Principales").build();
                ProductCategory catBebidas = ProductCategory.builder().name("Bebidas").build();
                ProductCategory catPostres = ProductCategory.builder().name("Postres").build();
                ProductCategory catEntradas = ProductCategory.builder().name("Entradas").build();


                categoryRepository.saveAll(List.of(catPlatos, catBebidas, catPostres, catEntradas));


                List<ProductEntity> menuCompleto = List.of(
                        ProductEntity.builder()
                                .name("Empanadas de Carne")
                                .description("Dos empanadas cortadas a cuchillo fritas o al horno.")
                                .category(catEntradas)
                                .build(),
                        ProductEntity.builder()
                                .name("Milanesa con Papas Fritas")
                                .description("Milanesa de ternera XL acompañada de papas bastón.")
                                .category(catPlatos)
                                .build(),
                        // ... el resto de tu lista ...
                        ProductEntity.builder()
                                .name("Tiramisú")
                                .description("Postre clásico italiano con café y mascarpone.")
                                .category(catPostres)
                                .build()
                );

                productRepository.saveAll(menuCompleto);

                System.out.println("✅ Menú cargado correctamente.");
            }
        };
    }
}
