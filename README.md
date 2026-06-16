# Cibus - Sistema de Gestión Gastronómica Multisucursal

![Header](src/main/java/com/jll/cibus/common/assets/CIBUS%20Header.png)
![Java](https://img.shields.io/badge/Java-25-orange?logo=java)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-brightgreen?logo=spring-boot)
![MySQL](https://img.shields.io/badge/MySQL-8.x-blue?logo=mysql)
![JWT](https://img.shields.io/badge/JWT-Authentication-black?logo=json-web-tokens)
![OpenAPI](https://img.shields.io/badge/OpenAPI-Swagger-lightgreen?logo=openapi)

## Descripción del Proyecto

**Cibus** es un sistema de gestión integral para entidades gastronómicas diseñado para operar con una o múltiples sucursales de forma centralizada y autónoma. El sistema optimiza los procesos operativos diarios del restaurante, conectando dinámicamente los roles esenciales (administrador, encargado, mozo, cocinero y recepcionista) para reducir errores humanos en la toma de pedidos, agilizar los flujos de trabajo en tiempo real y proveer estadísticas analíticas de rendimiento comercial.

El proyecto está diseñado de forma modular e independiente a través de una arquitectura **API REST**, completamente documentada bajo estándares internacionales y protegida mediante mecanismos de autenticación robustos basados en tokens sin estado.

---

## Tecnologías y Herramientas

El backend de la aplicación ha sido desarrollado utilizando un stack tecnológico moderno de alta fidelidad:

- **Lenguaje principal:** Java 25
- **Framework base:** Spring Boot
- **Capa de persistencia:** Spring Data JPA con Hibernate
- **Motor de Base de Datos:** MySQL
- **Seguridad y Autorización:** Spring Security + JSON Web Tokens (JWT)
- **Documentación de API:** Swagger / OpenAPI
- **Gestión de dependencias:** Maven
- **Control de Versiones:** Git & GitHub

---

## Roles del Sistema y Requisitos Funcionales (RF)

El sistema implementa un control de acceso basado en jerarquia de roles:

### 1. Administrador (Admin)
- **RF01:** Ingreso al sistema mediante usuario y clave personal.
- **RF02 / RF03:** Consulta de listados y Gestión Completa (ABM) de sucursales.
- **RF04 / RF05:** Consulta de listados y Gestión Completa (ABM) de empleados globales.
- **RF06 / RF07:** Consulta de listados y Gestión Completa (ABM) de los productos del menú global.
- **RF08:** Visualización avanzada de estadísticas consolidadas de todas las sucursales y de cada sucursal específica.

### 2. Encargado de Sucursal (Manager)
- **RF09:** Ingreso mediante ID de sucursal específica y clave personal de usuario.
- **RF10 / RF11:** Consulta y Gestión Completa (ABM) de las mesas de su sucursal asignada.
- **RF12 / RF13:** Consulta y modificación de los productos del menú de su sucursal asignada.
- **RF14 / RF15:** Consulta y modificación de la información de los empleados de su sucursal.
- **RF16:** Acceso a las estadísticas y métricas analíticas de su sucursal asignada.

### 3. Cocinero (Chef)
- **RF17:** Ingreso mediante ID de sucursal y pin personal interno.
- **RF18:** Consulta en tiempo real del listado de pedidos entrantes.
- **RF19:** Modificación del estado del pedido (por ejemplo: de 'En preparación' a 'Listo').

### 4. Mozo (Waiter)
- **RF20:** Ingreso mediante ID de sucursal y pin personal interno.
- **RF21:** Visualización del listado de mesas de su sucursal asignada.
- **RF22:** Apertura de nuevos pedidos vinculados a una mesa específica asignada al mozo.
- **RF23:** Navegación por el menú de productos disponibles en la sucursal asignada y selección de los mismos para el pedido activo.
- **RF24:** Modificación o edición de un pedido existente en una mesa asociada al mozo.

### 5. Recepcionista (Host)
- **RF25:** Ingreso mediante ID de sucursal y pin personal interno.
- **RF26:** Consulta general del mapa/listado de mesas de la sucursal.
- **RF27:** Modificación del estado de ocupación de una mesa existente.
- **RF28:** Asignación dinámica de un mozo a una mesa libre específica.
- **RF29:** Apertura de nuevos pedidos vinculados a una mesa específica autoasignada.
- **RF30:** Navegación por el menú de productos disponibles en la sucursal asignada y selección de los mismos para el pedido activo.
- **RF31:** Modificación o edición de un pedido existente en una mesa autoasignada.

---

## Requisitos No Funcionales (RNF)

- **RNF01 (Rendimiento):** El sistema debe responder a las solicitudes de los usuarios en un tiempo máximo de 3 segundos bajo condiciones normales de uso.
- **RNF02 (Escalabilidad Horizontal):** Capaz de gestionar múltiples sesiones simultáneas sin degradación perceptible del servicio.
- **RNF03 (Seguridad):** Autenticación mandatoria previa al acceso de cualquier recurso o funcionalidad.
- **RNF04 (Sesiones):** Gestión de sesiones mediante tokens JWT firmados digitalmente con un tiempo de expiración definido.
- **RNF05 (Autorización):** Restricción estricta de endpoints según el rol asignado al usuario autenticado.
- **RNF06 (Extensibilidad):** Soporte nativo para la incorporación de nuevas sucursales sin requerir cambios de código en la arquitectura base.
- **RNF07 (Estandarización):** API completamente documentada bajo la especificación OpenAPI.

---

## Catálogo de Endpoints de la API REST

Toda la interacción con el backend se realiza a través de las siguientes rutas estructuradas:

### Sucursales (Branches)
| Método | Endpoint | Descripción |
| :--- | :--- | :--- |
| `GET` | `/api/branches` | Obtener listado de todas las sucursales |
| `GET` | `/api/branches/{branchId}` | Obtener el detalle de una sucursal mediante su ID |
| `GET` | `/api/branches/search` | Buscar sucursal por dirección física |
| `POST` | `/api/branches` | Registrar una nueva sucursal en el sistema |
| `PUT` | `/api/branches/{branchId}` | Modificar los datos de una sucursal por ID |
| `DELETE` | `/api/branches/{branchId}` | Eliminar una sucursal mediante su ID |

### Usuarios y Empleados (Users)
| Método | Endpoint | Descripción |
| :--- | :--- | :--- |
| `GET` | `/api/users` | Obtener lista de usuarios con soporte de filtros dinámicos |
| `GET` | `/api/users/{userId}` | Obtener un usuario específico mediante su ID |
| `POST` | `/api/users` | Crear/Registrar un nuevo usuario |
| `PUT` | `/api/users/{userId}` | Modificar los datos de un usuario por ID |
| `DELETE` | `/api/users/{userId}` | Eliminar un usuario del sistema por ID |
| `GET` | `/api/users/roles` | Obtener el catálogo de roles disponibles en el sistema |

### Productos y Menú (Products)
| Método | Endpoint | Descripción |
| :--- | :--- | :--- |
| `GET` | `/api/products` | Obtener productos con filtros dinámicos aplicados |
| `GET` | `/api/products/{productId}` | Obtener la información de un producto por ID |
| `POST` | `/api/products` | Agregar un nuevo producto al catálogo general |
| `PUT` | `/api/products/{productId}` | Modificar un producto existente por ID |
| `DELETE` | `/api/products/{productId}` | Eliminar un producto por ID |
| `GET` | `/api/products/categories` | Listar categorías globales de productos |

### Mesas por Sucursal (Tables)
| Método | Endpoint | Descripción |
| :--- | :--- | :--- |
| `GET` | `/api/branches/{branch_id}/tables` | Obtener mesas de una sucursal con filtros dinámicos |
| `GET` | `/api/branches/{branch_id}/tables/{tableId}` | Obtener una mesa específica por ID |
| `POST` | `/api/branches/{branch_id}/tables` | Registrar una mesa en una sucursal determinada |
| `PUT` | `/api/branches/{branch_id}/tables/{table_id}` | Modificar los atributos de una mesa por ID |
| `DELETE` | `/api/branches/{branch_id}/tables/{table_id}` | Eliminar una mesa mediante su número o ID |
| `PATCH` | `/api/branches/{branch_id}/tables/occupy/{tableNumber}`| Marcar el estado de una mesa como **Ocupada** |
| `PATCH` | `/api/branches/{branch_id}/tables/free/{tableNumber}` | Marcar el estado de una mesa como **Libre** |

### Pedidos (Orders)
| Método | Endpoint | Descripción |
| :--- | :--- | :--- |
| `GET` | `/api/branches/{branch_id}/orders` | Obtener pedidos de una sucursal con filtros dinámicos |
| `GET` | `/api/branches/{branch_id}/orders/{orderId}` | Obtener detalles generales de un pedido por ID |
| `POST` | `/api/branches/{branch_id}/orders` | Crear un nuevo pedido vinculado a una mesa |
| `PUT` | `/api/branches/{branch_id}/orders/{order_id}` | Modificar la cabecera de un pedido por ID |
| `GET` | `/api/branches/{branch_id}/orders/statuses` | Listar los estados válidos de un pedido en la cocina |
| `POST` | `/api/branches/{branch_id}/orders/{order_id}/payments` | Agregar un registro de pago asociado al pedido |
| `PATCH`| `/api/branches/{branch_id}/orders/{order_id}/discount`| Aplicar un descuento monetario sobre el total |
| `POST` | `/api/{branch_id}/orders/{order_id}/cancel` | Cancelar una orden de forma lógica por ID |
| `POST` | `/api/{branch_id}/orders/{order_id}/ready` | Marcar la orden como **Lista** (Cocina finalizada) |
| `POST` | `/api/{branch_id}/orders/{order_id}/serve` | Marcar la orden como **Servida** por el mozo |

### Detalles del Pedido (Orders Details)
| Método | Endpoint | Descripción |
| :--- | :--- | :--- |
| `GET` | `/api/orders/{order_id}/items` | Listar ítems de productos incluidos en un pedido |
| `GET` | `/api/orders/{order_id}/items/{productId}` | Consultar detalle de un producto específico en la orden |
| `POST` | `/api/orders/{order_id}/items` | Agregar una línea de producto al detalle de un pedido |
| `PUT` | `/api/orders/{order_id}/items/{productId}` | Modificar la cantidad u observaciones de un ítem |
| `DELETE`| `/api/orders/{order_id}/details/{detail_id}` | Eliminar una línea de ítem de un pedido |

### Categorías de Producto & Métodos de Pago
| Método | Endpoint | Descripción |
| :--- | :--- | :--- |
| `GET` | `/api/product-categories` | Obtener todas las categorías de producto |
| `GET` | `/api/product-categories/{id}` | Obtener categoría por ID |
| `GET` | `/api/product-categories/name/{name}` | Buscar categoría por su nombre exacto |
| `POST` | `/api/product-categories` | Crear una nueva categoría de producto |
| `PUT` | `/api/product-categories/{id}` | Modificar una categoría existente |
| `DELETE`| `/api/product-categories/{id}` | Eliminar una categoría por ID |
| `GET` | `/api/payment-method` | Obtener todos los métodos de pago habilitados |
| `GET` | `/api/payment-method/{id}` | Obtener método de pago por ID |
| `GET` | `/api/payment-method/name/{name}` | Buscar método de pago por nombre |
| `POST` | `/api/payment-method` | Dar de alta un nuevo método de pago |
| `PUT` | `/api/payment-method/{id}` | Modificar un método de pago por ID |
| `DELETE`| `/api/payment-method/{id}` | Deshabilitar/Eliminar método de pago por ID |

---

## Configuración e Instalación

Esta sección describe los pasos necesarios para inicializar el entorno de desarrollo local para el backend del **Proyecto Cibus**. El sistema está construido como una API REST autónoma que utiliza la arquitectura de Spring Boot para la lógica de negocio y MySQL para la persistencia de datos.

### Prerrequisitos

Antes de comenzar, asegúrate de tener instaladas las siguientes herramientas en tu máquina local:

* **Java Development Kit (JDK):** Versión 17 o superior.
* **Apache Maven:** Versión 3.8 o superior (o utilizar el Maven Wrapper `mvnw` incluido).
* **MySQL Server:** Versión 8.0 o superior.
* **Un IDE compatible:** IntelliJ IDEA (recomendado), Eclipse o VS Code con extensiones de Spring.

## Pasos para la Instalación

### 1. Clonar el repositorio

Descarga el código fuente del proyecto a tu máquina local mediante Git:
git clone [https://github.com/Jenkow/cibus]

### 2. Configurar la Base de Datos

Abre tu gestor de bases de datos MySQL (MySQL Workbench, phpMyAdmin, DBeaver, etc.).
Crea una nueva base de datos para el proyecto:
CREATE DATABASE cibus_db

### 4. Configurar las Variables de Entorno

Dirígete a la ruta src/main/resources/application.yaml. Asegúrate de ajustar las credenciales de conexión según tu entorno local:
spring.datasource.url = <TU_URL_MYSQL>
spring.datasource.username= <TU_USUARIO_MYSQL>
spring.datasource.password= <TU_CONTRASENA_MYSQL>

### Configuración de Hibernate / JPA
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQLDialect

1. Compilar y Construir la Aplicación.
2. Utiliza Maven para descargar las dependencias necesarias y compilar el proyecto.
3. Ejecutar la Aplicación.
4. Una vez compilado correctamente, puedes iniciar el servidor.

La API comenzará a correr por defecto en el puerto 8080 (ej. http://localhost:8080).
Acceso a la Documentación de la API (Swagger / OpenAPI) Una vez que el servidor esté en ejecución, puedes explorar, interactuar y probar todos los endpoints disponibles
(sucursales, usuarios, productos, pedidos, etc.) desde la interfaz de Swagger UI accediendo a:URL: http://localhost:8080/swagger-ui/index.html
Tambien puedes probar los endpoints mediante la plantilla de prueba de PostMan: src/main/resources/static/postman/cibus.postman_collection.json

# Equipo de Desarrollo
El diseño de sistemas, análisis de requisitos, modelado de base de datos e implementación del Proyecto Cibus fue llevado a cabo por el siguiente equipo técnico:
#### Auriti Primavera, Jerónimo — Desarrollador.
#### Oliver Cáceres, Luisina — Desarrolladora.
#### Sosa, Luciano — Desarrollador.   

### Cibus.