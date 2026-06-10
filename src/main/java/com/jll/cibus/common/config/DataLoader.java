package com.jll.cibus.common.config;

import com.jll.cibus.branch.entity.BranchEntity;
import com.jll.cibus.branch.repository.BranchRepository;
import com.jll.cibus.order.entity.OrderStatusEntity;
import com.jll.cibus.order.repository.OrderStatusRepository;
import com.jll.cibus.payment.entity.PaymentMethodEntity;
import com.jll.cibus.payment.repository.PaymentMethodRepository;
import com.jll.cibus.productcategory.entity.ProductCategoryEntity;
import com.jll.cibus.productcategory.repository.ProductCategoryRepository;
import com.jll.cibus.product.entity.ProductEntity;
import com.jll.cibus.product.repository.ProductRepository;
import com.jll.cibus.role.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

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
            BranchRepository branchRepository
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

                // 1. Crear todos los permisos
                Map<Permits, PermitEntity> permisos = new EnumMap<>(Permits.class);
                for (Permits p : Permits.values()) {
                    PermitEntity pe = PermitEntity.builder().permit(p).build();
                    permitRepository.save(pe);
                    permisos.put(p, pe);
                }

                // 2. Helper para construir un RoleEntity con sus permisos
                // (lambda o método privado)

                // 3. ROLE_ADMIN — todo
                RoleEntity admin = new RoleEntity(Roles.ROLE_ADMIN);
                Arrays.stream(Permits.values()).forEach(p -> admin.addPermit(permisos.get(p)));

                // 4. ROLE_MANAGER
                RoleEntity manager = new RoleEntity(Roles.ROLE_MANAGER);
                List.of(
                        Permits.USER_READ, Permits.USER_UPDATE,
                        Permits.PRODUCT_READ,
                        Permits.CATEGORY_READ,
                        Permits.TABLE_READ, Permits.ORDER_READ, Permits.ORDER_STATUS_READ,
                        Permits.PAYMENT_READ
                ).forEach(p -> manager.addPermit(permisos.get(p)));

                // 5. ROLE_HOST
                RoleEntity host = new RoleEntity(Roles.ROLE_HOST);
                List.of(
                        Permits.TABLE_READ,  Permits.TABLE_UPDATE,
                        Permits.TABLE_OPEN, Permits.TABLE_CLOSE,
                        Permits.ORDER_READ, Permits.ORDER_CREATE, Permits.ORDER_UPDATE,
                        Permits.ORDER_CANCEL, Permits.ORDER_CLOSE,
                        Permits.ORDER_STATUS_READ,
                        Permits.PAYMENT_READ, Permits.PAYMENT_CREATE
                ).forEach(p -> host.addPermit(permisos.get(p)));

                // 6. ROLE_WAITER
                RoleEntity waiter = new RoleEntity(Roles.ROLE_WAITER);
                List.of(
                        Permits.TABLE_READ,
                        Permits.ORDER_READ, Permits.ORDER_CREATE, Permits.ORDER_UPDATE,
                        Permits.ORDER_CANCEL, Permits.ORDER_CLOSE,
                        Permits.ORDER_STATUS_READ,
                        Permits.PAYMENT_READ, Permits.PAYMENT_CREATE
                ).forEach(p -> waiter.addPermit(permisos.get(p)));

                // 7. ROLE_KITCHEN
                RoleEntity kitchen = new RoleEntity(Roles.ROLE_KITCHEN);
                List.of(
                        Permits.ORDER_READ, Permits.ORDER_CHANGE_STATUS,
                        Permits.ORDER_STATUS_READ,
                        Permits.ORDER_STATUS_UPDATE
                ).forEach(p -> kitchen.addPermit(permisos.get(p)));

                roleRepository.saveAll(List.of(admin, manager, host, waiter, kitchen));
                System.out.println("Roles y permisos cargados correctamente.");
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
