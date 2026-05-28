package com.salesianos.triana.techstore.model;

// Nivel de disponibilidad de un producto. Centraliza los umbrales (5 / 0) que
// antes estaban repartidos por templates y JS. Si mañana cambia la regla
// ("pocas" pasa a ser ≤ 3, por ejemplo) basta con tocar `evaluar`.
public enum NivelStock {

    AGOTADO,     // stock == 0
    BAJO,        // 0 < stock <= 5
    DISPONIBLE;  // stock > 5

    public static NivelStock evaluar(int stock) {
        if (stock <= 0) return AGOTADO;
        if (stock <= 5) return BAJO;
        return DISPONIBLE;
    }
}
