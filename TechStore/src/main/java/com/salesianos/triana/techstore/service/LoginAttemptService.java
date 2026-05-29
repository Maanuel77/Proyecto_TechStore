package com.salesianos.triana.techstore.service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;

// Rate limiting muy simple para el endpoint de login. Almacena en memoria los
// intentos fallidos por username. Cuando uno supera MAX_INTENTOS en menos de
// VENTANA_MINUTOS, se bloquea durante BLOQUEO_MINUTOS.
//
// Es defensa anti fuerza bruta básica: en producción se reforzaría con un
// limitador por IP, captcha tras N fallos, o un cache distribuido (Redis).
@Service
public class LoginAttemptService {

    private static final int MAX_INTENTOS    = 5;
    private static final int VENTANA_MINUTOS = 15;
    private static final int BLOQUEO_MINUTOS = 15;

    private static final class Estado {
        int    fallos;
        LocalDateTime primerFallo;
        LocalDateTime bloqueadoHasta;
    }

    private final Map<String, Estado> estados = new ConcurrentHashMap<>();

    // ¿Está este usuario bloqueado ahora mismo?
    public boolean estaBloqueado(String username) {
        if (username == null) return false;
        Estado e = estados.get(username.toLowerCase());
        if (e == null || e.bloqueadoHasta == null) return false;
        if (e.bloqueadoHasta.isBefore(LocalDateTime.now())) {
            // Bloqueo expirado: limpiamos para que vuelva a tener intentos.
            estados.remove(username.toLowerCase());
            return false;
        }
        return true;
    }

    // Segundos restantes de bloqueo (para mostrar en la UI si quieres).
    public long segundosRestantesBloqueo(String username) {
        if (username == null) return 0;
        Estado e = estados.get(username.toLowerCase());
        if (e == null || e.bloqueadoHasta == null) return 0;
        long s = java.time.Duration.between(LocalDateTime.now(), e.bloqueadoHasta).toSeconds();
        return Math.max(0, s);
    }

    // Registra un fallo. Si supera el umbral en la ventana, bloquea.
    public synchronized void registrarFallo(String username) {
        if (username == null) return;
        String key = username.toLowerCase();
        Estado e = estados.computeIfAbsent(key, k -> new Estado());

        // Resetea la cuenta si la ventana de tracking caducó.
        if (e.primerFallo == null
                || e.primerFallo.plusMinutes(VENTANA_MINUTOS).isBefore(LocalDateTime.now())) {
            e.fallos = 0;
            e.primerFallo = LocalDateTime.now();
        }
        e.fallos++;
        if (e.fallos >= MAX_INTENTOS) {
            e.bloqueadoHasta = LocalDateTime.now().plusMinutes(BLOQUEO_MINUTOS);
        }
    }

    // Login exitoso → limpiar contador.
    public void resetear(String username) {
        if (username == null) return;
        estados.remove(username.toLowerCase());
    }
}
