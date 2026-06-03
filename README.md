# TechStore 

TechStore es una aplicación web de comercio electrónico (e-commerce) desarrollada en **Spring Boot** y **Thymeleaf**, especializada en la venta de electrodomésticos y tecnología de alta gama.

## Características Principales

### Seguridad y Autenticación Avanzada
* **Autenticación en dos pasos (2FA):** Verificación por correo electrónico con códigos de un solo uso (OTP) al iniciar sesión y al cambiar la contraseña (para los clientes).
* **Protección contra fuerza bruta:** Bloqueo temporal de cuentas tras 5 intentos fallidos de inicio de sesión (`LoginAttemptService`).
* **Roles de usuario:** Control de acceso basado en roles (`ADMIN` y `CLIENTE`).

### Panel de Administración (Admin)
* **Dashboard interactivo:** Resumen de KPIs y alertas de stock (agotado, crítico, bajo).
* **Gestión de Catálogo (CRUD):** Añadir, editar y eliminar productos con vista previa de imágenes en vivo.
* **Gestión de Pedidos:** Visualización de todos los pedidos de la tienda, con detalles, filtrado por fechas y subtotales.
* **Gestión de Usuarios:** Listado de clientes y posibilidad de otorgar o revocar privilegios de administrador.
* **Sistema de Cupones:** Creación de cupones públicos y configuración del **programa de fidelidad** (cupones asignados automáticamente al alcanzar un umbral de gasto).
* **Estadísticas Avanzadas:** Gráficos interactivos (Chart.js) mostrando la evolución de los pedidos por fecha, el top de productos más vendidos y el ranking de clientes.

### Experiencia del Cliente (Cliente)
* **Catálogo y Buscador:** Exploración de productos con filtros de búsqueda por nombre o marca en tiempo real.
* **Carrito de Compras:** Gestión del carrito basado en sesión HTTP, cálculo automático para envíos gratuitos, opción de añadir garantía extendida y aplicación de cupones de descuento.
* **Tramitación de Pedidos:** Proceso de compra con reflejo y validación inmediata en el stock.
* **Historial de Pedidos:** Consulta detallada de compras pasadas y comprobantes.
* **Perfil de Usuario:** Gestión de datos personales y actualización de contraseñas de forma segura (con validación de 2FA).

---

## Tecnologías y Herramientas

**Backend:**
* Java (Spring Boot)
* Spring Security (Autenticación Customizada & 2FA)
* Spring Data JPA / Hibernate
* Base de datos H2 (Configurada en fichero local `./db/basededatos`)
* JavaMail (Spring Mail para envío de códigos OTP)

**Frontend:**
* Thymeleaf (Renderizado del lado del servidor con fragments reutilizables)
* HTML5, CSS3, JavaScript (Vanilla JS)
* Bootstrap 5 & Bootstrap Icons
* Chart.js (Visualización de datos)

---

## Instalación y Ejecución Local

### 1. Requisitos previos
* Java Development Kit (JDK) 17 o superior.
* Maven instalado en el sistema.

### 2. Configurar variables de entorno (Opcional pero recomendado)
La aplicación utiliza el servidor SMTP de Gmail para enviar los correos de verificación (2FA). Para habilitarlo, configura las siguientes variables de entorno:
```bash
export EMAIL_USERNAME="tu_correo@gmail.com"
export EMAIL_PASSWORD="tu_contraseña_de_aplicacion"
