package com.salesianos.triana.techstore.service;

import java.security.SecureRandom;
import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import jakarta.servlet.http.HttpSession;

// Lógica de los códigos de verificación 2FA. Todo el estado vive en la sesión
// HTTP del usuario (no se persiste en BD): código, timestamp de expiración,
// intentos restantes, cooldown del botón reenviar, etc.
//
// Hay dos contextos diferentes (login y cambio de contraseña). Cada uno usa
// sus propias claves de sesión para que no se pisen entre sí.
@Service
public class VerificacionService {

    public static final int DURACION_MINUTOS    = 10;
    public static final int MAX_INTENTOS        = 3;
    public static final int COOLDOWN_REENVIO_SEG = 30;

    // Claves para guardar el código del LOGIN 2FA en sesión.
    public static final String S_LOGIN_USER_ID       = "verif.login.userId";
    public static final String S_LOGIN_CODIGO        = "verif.login.codigo";
    public static final String S_LOGIN_EXPIRA        = "verif.login.expira";
    public static final String S_LOGIN_INTENTOS      = "verif.login.intentos";
    public static final String S_LOGIN_ULTIMO_ENVIO  = "verif.login.ultimoEnvio";

    // Claves para guardar el código del CAMBIO DE CONTRASEÑA en sesión.
    public static final String S_PWD_USER_ID         = "verif.pwd.userId";
    public static final String S_PWD_NUEVA           = "verif.pwd.nuevaPassword";
    public static final String S_PWD_CODIGO          = "verif.pwd.codigo";
    public static final String S_PWD_EXPIRA          = "verif.pwd.expira";
    public static final String S_PWD_INTENTOS        = "verif.pwd.intentos";
    public static final String S_PWD_ULTIMO_ENVIO    = "verif.pwd.ultimoEnvio";

    private final SecureRandom random = new SecureRandom();

    // === Generación =====================================================

    // 6 dígitos numéricos (000000-999999). SecureRandom para que no sea
    // predecible aunque la entropía de 1 millón no es lo más fuerte del mundo.
    public String generarCodigo() {
        return String.format("%06d", random.nextInt(1_000_000));
    }

    // === Estado en sesión: LOGIN ========================================

    public void iniciarLogin(HttpSession session, Long usuarioId, String codigo) {
        session.setAttribute(S_LOGIN_USER_ID, usuarioId);
        session.setAttribute(S_LOGIN_CODIGO, codigo);
        session.setAttribute(S_LOGIN_EXPIRA, LocalDateTime.now().plusMinutes(DURACION_MINUTOS));
        session.setAttribute(S_LOGIN_INTENTOS, MAX_INTENTOS);
        session.setAttribute(S_LOGIN_ULTIMO_ENVIO, LocalDateTime.now());
    }

    public void limpiarLogin(HttpSession session) {
        session.removeAttribute(S_LOGIN_USER_ID);
        session.removeAttribute(S_LOGIN_CODIGO);
        session.removeAttribute(S_LOGIN_EXPIRA);
        session.removeAttribute(S_LOGIN_INTENTOS);
        session.removeAttribute(S_LOGIN_ULTIMO_ENVIO);
    }

    public boolean hayLoginPendiente(HttpSession session) {
        return session.getAttribute(S_LOGIN_USER_ID) != null;
    }

    public Long getUsuarioPendienteLogin(HttpSession session) {
        Object v = session.getAttribute(S_LOGIN_USER_ID);
        return v instanceof Long l ? l : null;
    }

    // === Estado en sesión: CAMBIO DE PASSWORD ===========================

    public void iniciarCambioPassword(HttpSession session, Long usuarioId, String nuevaPassword, String codigo) {
        session.setAttribute(S_PWD_USER_ID, usuarioId);
        session.setAttribute(S_PWD_NUEVA, nuevaPassword);
        session.setAttribute(S_PWD_CODIGO, codigo);
        session.setAttribute(S_PWD_EXPIRA, LocalDateTime.now().plusMinutes(DURACION_MINUTOS));
        session.setAttribute(S_PWD_INTENTOS, MAX_INTENTOS);
        session.setAttribute(S_PWD_ULTIMO_ENVIO, LocalDateTime.now());
    }

    public void limpiarCambioPassword(HttpSession session) {
        session.removeAttribute(S_PWD_USER_ID);
        session.removeAttribute(S_PWD_NUEVA);
        session.removeAttribute(S_PWD_CODIGO);
        session.removeAttribute(S_PWD_EXPIRA);
        session.removeAttribute(S_PWD_INTENTOS);
        session.removeAttribute(S_PWD_ULTIMO_ENVIO);
    }

    public boolean hayCambioPasswordPendiente(HttpSession session) {
        return session.getAttribute(S_PWD_USER_ID) != null;
    }

    public String getPasswordNuevaPendiente(HttpSession session) {
        Object v = session.getAttribute(S_PWD_NUEVA);
        return v instanceof String s ? s : null;
    }

    // === Validación común ===============================================

    public enum Resultado { OK, EXPIRADO, INCORRECTO, AGOTADO_INTENTOS, SIN_PENDIENTE }

    // Valida un código contra los que estén guardados con esos atributos en sesión.
    // Decrementa los intentos cuando es incorrecto. Si llega a 0, limpia todo.
    @SuppressWarnings("unchecked")
    public Resultado validar(HttpSession session, String codigoIntroducido,
                             String keyCodigo, String keyExpira, String keyIntentos,
                             Runnable onAgotado) {

        String codigoEsperado = (String) session.getAttribute(keyCodigo);
        LocalDateTime expira  = (LocalDateTime) session.getAttribute(keyExpira);
        Integer intentos      = (Integer) session.getAttribute(keyIntentos);

        if (codigoEsperado == null || expira == null || intentos == null) {
            return Resultado.SIN_PENDIENTE;
        }
        if (expira.isBefore(LocalDateTime.now())) {
            onAgotado.run();
            return Resultado.EXPIRADO;
        }
        if (codigoEsperado.equals(codigoIntroducido != null ? codigoIntroducido.trim() : "")) {
            return Resultado.OK;
        }
        // Incorrecto: descontamos un intento.
        int restantes = intentos - 1;
        if (restantes <= 0) {
            onAgotado.run();
            return Resultado.AGOTADO_INTENTOS;
        }
        session.setAttribute(keyIntentos, restantes);
        return Resultado.INCORRECTO;
    }

    // Devuelve los segundos que faltan para poder reenviar el código (0 si ya).
    public long segundosParaReenvio(HttpSession session, String keyUltimoEnvio) {
        LocalDateTime ultimo = (LocalDateTime) session.getAttribute(keyUltimoEnvio);
        if (ultimo == null) return 0;
        long segundosDesde = java.time.Duration.between(ultimo, LocalDateTime.now()).toSeconds();
        long restantes = COOLDOWN_REENVIO_SEG - segundosDesde;
        return Math.max(0, restantes);
    }

    // Regenera el código y resetea los intentos (para el botón "Reenviar").
    public void regenerar(HttpSession session, String codigoNuevo,
                          String keyCodigo, String keyExpira,
                          String keyIntentos, String keyUltimoEnvio) {
        session.setAttribute(keyCodigo, codigoNuevo);
        session.setAttribute(keyExpira, LocalDateTime.now().plusMinutes(DURACION_MINUTOS));
        session.setAttribute(keyIntentos, MAX_INTENTOS);
        session.setAttribute(keyUltimoEnvio, LocalDateTime.now());
    }

    public Integer getIntentosRestantes(HttpSession session, String keyIntentos) {
        return (Integer) session.getAttribute(keyIntentos);
    }
}
