package com.jll.cibus.common.config;

import com.jll.cibus.branch.entity.BranchEntity;
import com.jll.cibus.branch.repository.BranchRepository;
import com.jll.cibus.common.exception.ResourceNotFoundException;
import com.jll.cibus.credential.entity.CredentialsEntity;
import com.jll.cibus.credential.repository.CredentialsRepository;
import com.jll.cibus.order.entity.OrderStatusEntity;
import com.jll.cibus.order.repository.OrderStatusRepository;
import com.jll.cibus.payment.entity.PaymentMethodEntity;
import com.jll.cibus.payment.repository.PaymentMethodRepository;
import com.jll.cibus.productcategory.entity.ProductCategoryEntity;
import com.jll.cibus.productcategory.repository.ProductCategoryRepository;
import com.jll.cibus.product.entity.ProductEntity;
import com.jll.cibus.product.repository.ProductRepository;
import com.jll.cibus.role.entity.PermitEntity;
import com.jll.cibus.role.entity.RoleEntity;
import com.jll.cibus.role.enums.Permits;
import com.jll.cibus.role.enums.Roles;
import com.jll.cibus.role.repository.PermitRepository;
import com.jll.cibus.role.repository.RoleRepository;
import com.jll.cibus.user.entity.UserEntity;
import com.jll.cibus.user.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.*;

@Configuration
public class DataLoader {

    @Bean
    CommandLineRunner initDatabase(

            ProductRepository productRepository,
            ProductCategoryRepository categoryRepository,
            RoleRepository roleRepository,
            PermitRepository permitRepository,
            PaymentMethodRepository paymentMethodRepository,
            OrderStatusRepository orderStatusRepository,
            BranchRepository branchRepository,
            UserRepository userRepository,
            CredentialsRepository credentialsRepository,
            PasswordEncoder passwordEncoder
    ) {

        return args -> {

            if (categoryRepository.count() == 0) {

                ProductCategoryEntity catPlatos = ProductCategoryEntity.builder().name("Platos Principales").build();
                ProductCategoryEntity catBebidas = ProductCategoryEntity.builder().name("Bebidas").build();
                ProductCategoryEntity catPostres = ProductCategoryEntity.builder().name("Postres").build();
                ProductCategoryEntity catEntradas = ProductCategoryEntity.builder().name("Entradas").build();

                categoryRepository.saveAll(List.of(
                        catPlatos,
                        catBebidas,
                        catPostres,
                        catEntradas
                ));

                List<ProductEntity> menuCompleto = List.of(

                        ProductEntity.builder()
                                .name("Empanadas de Carne")
                                .description("Dos empanadas cortadas a cuchillo fritas o al horno.")
                                .category(catEntradas)
                                .build(),

                        ProductEntity.builder()
                                .name("Provoleta Especial")
                                .description("Provolone fundido con tomates cherry y albahaca.")
                                .category(catEntradas)
                                .build(),

                        ProductEntity.builder()
                                .name("Bastones de Mozzarella")
                                .description("Seis bastones fritos con salsa pomodoro.")
                                .category(catEntradas)
                                .build(),

                        ProductEntity.builder()
                                .name("Milanesa con Papas Fritas")
                                .description("Milanesa de ternera XL acompañada de papas bastón.")
                                .category(catPlatos)
                                .build(),

                        ProductEntity.builder()
                                .name("Ravioles de Espinaca")
                                .description("Pasta casera con salsa bolognesa o blanca.")
                                .category(catPlatos)
                                .build(),

                        ProductEntity.builder()
                                .name("Entraña con Ensalada")
                                .description("Corte de entraña grillada con mix de verdes.")
                                .category(catPlatos)
                                .build(),

                        ProductEntity.builder()
                                .name("Risotto de Hongos")
                                .description("Arroz cremoso con champiñones y portobellos.")
                                .category(catPlatos)
                                .build(),

                        ProductEntity.builder()
                                .name("Agua Mineral 500ml")
                                .description("Con o sin gas.")
                                .category(catBebidas)
                                .build(),

                        ProductEntity.builder()
                                .name("Cerveza Artesanal IPA")
                                .description("Pinta de cerveza artesanal amarga y aromática.")
                                .category(catBebidas)
                                .build(),

                        ProductEntity.builder()
                                .name("Limonada con Menta")
                                .description("Jarra de 1 litro con jengibre y menta fresca.")
                                .category(catBebidas)
                                .build(),

                        ProductEntity.builder()
                                .name("Vino Malbec")
                                .description("Copa de vino tinto de la casa.")
                                .category(catBebidas)
                                .build(),

                        ProductEntity.builder()
                                .name("Tiramisú")
                                .description("Postre clásico italiano con café y mascarpone.")
                                .category(catPostres)
                                .build(),

                        ProductEntity.builder()
                                .name("Flan Casero")
                                .description("Con dulce de leche y crema batida.")
                                .category(catPostres)
                                .build(),

                        ProductEntity.builder()
                                .name("Volcán de Chocolate")
                                .description("Bizcocho caliente con centro líquido y helado de crema.")
                                .category(catPostres)
                                .build(),

                        ProductEntity.builder()
                                .name("Ensalada de Frutas")
                                .description("Frutas de estación seleccionadas.")
                                .category(catPostres)
                                .build()
                );

                productRepository.saveAll(menuCompleto);

                System.out.println("Menú cargado correctamente.");
            }

            if (roleRepository.count() == 0) {
                Map<Permits, PermitEntity> permits = new EnumMap<>(Permits.class);
                for (Permits p : Permits.values()) {
                    PermitEntity pe = PermitEntity.builder().permit(p).build();
                    permitRepository.save(pe);
                    permits.put(p, pe);
                }
                for (Roles role : Roles.values()) {
                    RoleEntity roleEntity = new RoleEntity(role);
                    role.getPermits().forEach(permit ->
                            roleEntity.addPermit(permits.get(permit))
                    );
                    roleRepository.save(roleEntity);
                }
                System.out.println("Roles y permisos cargados correctamente.");
            }

            if (!credentialsRepository.existsByUsername("admin") && !userRepository.existsByDni(0L)){
                RoleEntity adminRole = roleRepository.findByRole(Roles.ADMIN)
                        .orElseThrow(() -> new ResourceNotFoundException("Error: create admin user failed"));
                UserEntity admin = UserEntity.builder()
                        .dni(0L)
                        .role(adminRole)
                        .firstName("admin")
                        .lastName("master")
                        .phoneNumber("1111111111")
                        .email("admin@gmail.com")
                        .build();
                userRepository.save(admin);
                CredentialsEntity credentials = CredentialsEntity.builder()
                        .username("admin")
                        .password(passwordEncoder.encode("111111"))
                        .enabled(Boolean.TRUE)
                        .user(admin)
                        .roles(Set.of(adminRole))
                        .build();
                credentialsRepository.save(credentials);
                System.out.println("Admin cargado correctamente");
            }


            if (paymentMethodRepository.count() == 0) {

                List<PaymentMethodEntity> payments = List.of(

                        PaymentMethodEntity.builder()
                                .name("EFECTIVO")
                                .build(),

                        PaymentMethodEntity.builder()
                                .name("CREDITO")
                                .build(),

                        PaymentMethodEntity.builder()
                                .name("DEBITO")
                                .build(),

                        PaymentMethodEntity.builder()
                                .name("MERCADO PAGO")
                                .build()
                );

                paymentMethodRepository.saveAll(payments);

                System.out.println("Medios de pago cargados correctamente.");
            }
            if (orderStatusRepository.count() == 0)
            {
                List<OrderStatusEntity> orderStatus = List.of(
                        OrderStatusEntity.builder()
                                .name("PENDING")
                                .build(),
                        OrderStatusEntity.builder()
                                .name("PREPARING")
                                .build(),
                        OrderStatusEntity.builder()
                                        .name("READY")
                                        .build(),
                        OrderStatusEntity.builder()
                                .name("SERVED")
                                .build(),
                        OrderStatusEntity.builder()
                                .name("PAID")
                                .build(),
                        OrderStatusEntity.builder()
                                .name("CANCELLED")
                                .build()
                );

                orderStatusRepository.saveAll(orderStatus);

                System.out.println("Estados de orden cargados correctamente.");
            }
            if (branchRepository.count() == 0) {

                List<BranchEntity> branches = List.of(

                        BranchEntity.builder()
                                .name("Sucursal Centro")
                                .street("San Martín")
                                .number(1234)
                                .build(),

                        BranchEntity.builder()
                                .name("Sucursal Norte")
                                .street("Independencia")
                                .number(567)
                                .build(),

                        BranchEntity.builder()
                                .name("Sucursal Sur")
                                .street("Belgrano")
                                .number(890)
                                .build()
                );

                branchRepository.saveAll(branches);

                System.out.println("Sucursales cargadas correctamente.");
            }

        };
    }
}
