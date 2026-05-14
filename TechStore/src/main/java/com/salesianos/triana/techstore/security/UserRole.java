package com.salesianos.triana.techstore.security;

/*
 * Enum que define los roles disponibles en la aplicación.
 * Cada usuario tendrá asignado exactamente un rol.
 *
 * - ADMIN   → acceso completo (dashboard, gestión de productos, pedidos...)
 * - CLIENTE → acceso a catálogo, carrito y sus propios pedidos
 *
 * Spring Security antepondrá automáticamente el prefijo "ROLE_"
 * cuando se use con hasRole(). Es decir, ADMIN se convierte en ROLE_ADMIN.
 */
public enum UserRole {
    ADMIN, CLIENTE
}
