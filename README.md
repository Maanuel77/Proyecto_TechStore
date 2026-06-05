<div align="center">

# TechStore

**Plataforma de comercio electrónico robusta, segura y orientada a servicios basada en Spring Boot.**
*Desde la gestión de inventario transaccional hasta experiencias de usuario fluidas con 2FA.*

[![Release](https://img.shields.io/badge/Latest_release-v1.0.0-blue?style=flat-square)](#)
[![Java](https://img.shields.io/badge/Java-17+-ED8B00?style=flat-square&logo=openjdk&logoColor=white)](#)
[![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.x-6DB33F?style=flat-square&logo=spring-boot&logoColor=white)](#)
[![Thymeleaf](https://img.shields.io/badge/Thymeleaf-005F0F?style=flat-square&logo=thymeleaf&logoColor=white)](#)
[![License](https://img.shields.io/badge/License-MIT-lightgrey.svg?style=flat-square)](#)

</div>

---

> La mayoría de los proyectos de e-commerce fallan en la seguridad de las credenciales, el control de concurrencia en carritos abandonados y la integridad histórica de los datos. 
> **TechStore** elimina estas categorías de fallo implementando **2FA nativo, limitación de fuerza bruta (Rate Limiting) y Soft Deletion**, garantizando auditorías financieras impecables sin sacrificar el rendimiento.

## Project Status

| CI/CD & Seguridad | Tecnologías Core | Entorno de Ejecución |
| :--- | :--- | :--- |
| ![Build](https://img.shields.io/badge/Build-passing-brightgreen?style=flat-square) | ![Spring Security](https://img.shields.io/badge/Security-Spring_Security-6DB33F?style=flat-square) | ![OS](https://img.shields.io/badge/OS-Linux_%7C_macOS_%7C_Windows-0078D4?style=flat-square) |
| ![CodeQL](https://img.shields.io/badge/CodeQL-passing-brightgreen?style=flat-square) | ![Database](https://img.shields.io/badge/Database-H2_%7C_JPA-FFA500?style=flat-square) | ![Arch](https://img.shields.io/badge/Arch-x86_%7C_x86__64_%7C_arm64-lightgrey?style=flat-square) |

---

## Tabla de Contenidos
- [Características Principales](#-características-principales)
  - [Seguridad de Grado Empresarial](#-seguridad-de-grado-empresarial)
  - [Módulo de Administración (Backoffice)](#-módulo-de-administración-backoffice)
  - [Experiencia del Consumidor](#-experiencia-del-consumidor)
- [Guía de Inicio Rápido (Quickstart)](#-guía-de-inicio-rápido-quickstart)
- [Cuentas de Demostración (Data Seed)](#-cuentas-de-demostración-data-seed)
- [Arquitectura de Dominio](#-arquitectura-de-dominio)
  
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

## Características Principales

### Alta seguridad
* **Autenticación en Dos Pasos (2FA):** Verificación OOB (Out-of-Band) vía correo electrónico con tokens de un solo uso (OTP) para inicios de sesión y operaciones sensibles.
* **Defensa Anti-Fuerza Bruta:** Módulo `LoginAttemptService` con bloqueos temporales automáticos tras intentos de acceso anómalos.
* **Control de Acceso (RBAC):** Restricciones de rutas granulares y seguras mediante interceptores de Spring Security para roles `ADMIN` y `CLIENTE`.

### Módulo de Administración (Backoffice)
* **Dashboard Analítico:** Panel de control de KPIs en tiempo real y alertas de rotura de stock categorizadas (agotado, crítico, bajo).
* **Gestión de Catálogo Inmutable (Soft Delete):** Permite retirar productos de la venta sin corromper la integridad referencial de los históricos de pedidos.
* **Motor de Fidelización:** Generador automático de cupones de descuento (UUIDs) asignados al cruzar umbrales de gasto configurables.
* **Data Visualization:** Integración nativa con `Chart.js` para informes de facturación, top ventas y ránkings de clientes.

### Experiencia del Consumidor
* **Navegación Ágil:** Catálogo con filtros de búsqueda reactivos en el lado del cliente y modales dinámicos.
* **Control Transaccional (ACID):** Carrito en sesión HTTP con validaciones estrictas de inventario en el momento exacto del *checkout*.
* **Historial Congelado:** Preservación de los precios en el momento de la compra para garantizar facturación precisa, ajena a fluctuaciones posteriores del catálogo.

---

## Guía de Inicio Rápido (Quickstart)

### 1. Requisitos Previos
* **Java JDK 17** o superior.
* **Maven** (Apache Maven 3.8+).

### 2. Variables de Entorno (Recomendado para 2FA)
Para que los códigos de seguridad (OTP) lleguen a cuentas de correo reales, configura un servidor SMTP:
```bash
export EMAIL_USERNAME="tu_correo@gmail.com"
export EMAIL_PASSWORD="tu_contraseña_de_aplicacion_gmail"
```
## Compilar el programa

### 1. Clonar el repositorio
git clone https://github.com/Maanuel77/Proyecto_TechStore.git

cd Proyecto_TechStore

### 2. Compilar el proyecto
mvn clean install

### 3. Ejecutar la aplicación
mvn spring-boot:run
export EMAIL_PASSWORD="tu_contraseña_de_aplicacion"
