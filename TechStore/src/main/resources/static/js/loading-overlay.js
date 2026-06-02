// Pantalla de carga minimalista para las esperas que dependen del envío de email:
//   - login -> verificar (POST /auth/login)
//   - botón "reenviar código" (POST /auth/verificar/reenviar)
//
// CLAVE: el envío SMTP es SÍNCRONO, así que esos POST tardan 2-5 s en responder.
// Durante esa espera el navegador mantiene visible la página actual; aprovechamos
// eso para superponer este overlay y desaparece solo cuando llega la redirección.
//
// ANTI-PARPADEO: el overlay NO se pinta hasta pasados ~150 ms. Si la respuesta
// llega antes (admin o contraseña incorrecta, que no mandan email), la página ya
// habrá navegado y el overlay nunca llega a verse. Cero flash en logins rápidos.
(() => {
	const DELAY_MS  = 150;    // retardo antes de mostrar (evita el flash en respuestas rápidas)
	const ROTATE_MS = 1600;   // cada cuánto cambia el mensaje
	const SAFETY_MS = 20000;  // red de seguridad: si algo va mal, no dejar el overlay colgado

	// Mensajes con temática de tienda de electrónica. Cada uno lleva su icono
	// de Bootstrap Icons (https://icons.getbootstrap.com) para reforzar la idea.
	const MENSAJES = [
		{ icon: 'bi-box-seam',        text: 'Buscando tu cuenta entre las cajas del almacén…' },
		{ icon: 'bi-shop',            text: 'Abriendo la tienda solo para ti…' },
		{ icon: 'bi-truck',           text: 'Tu acceso está en reparto…' },
		{ icon: 'bi-receipt',         text: 'Imprimiendo tu ticket de entrada…' },
		{ icon: 'bi-tag',             text: 'Comprobando la etiqueta de tu cuenta…' },
		{ icon: 'bi-cpu',             text: 'Encendiendo los procesadores de la trastienda…' },
		{ icon: 'bi-battery-charging',text: 'Cargando las baterías de la caja registradora…' },
		{ icon: 'bi-bag-check',       text: 'Preparando tu bolsa de compra…' },
		{ icon: 'bi-headset',         text: 'Avisando al servicio técnico…' },
		{ icon: 'bi-qr-code-scan',    text: 'Escaneando el código de barras de tu sesión…' },
	];

	const overlay = document.getElementById('tsLoadingOverlay');
	const textEl  = document.getElementById('tsLoadingText');
	const iconEl  = document.getElementById('tsLoadingIcon');
	if (!overlay || !textEl || !iconEl) return;

	let showTimer   = null;
	let rotateTimer = null;
	let safetyTimer = null;

	// Aplica un mensaje (icono + texto). Las clases bi-* anteriores se quitan
	// para que solo quede activa la del mensaje actual.
	function aplicarMensaje(msg) {
		// Quitamos cualquier clase que empiece por bi- (el icono anterior).
		iconEl.classList.forEach((c) => { if (c.startsWith('bi-')) iconEl.classList.remove(c); });
		iconEl.classList.add(msg.icon);
		textEl.textContent = msg.text;
	}

	// Cambia el mensaje con una pequeña transición de opacidad (definida en CSS).
	function pintarMensaje(i) {
		textEl.classList.add('is-swapping');
		iconEl.classList.add('is-swapping');
		setTimeout(() => {
			aplicarMensaje(MENSAJES[i % MENSAJES.length]);
			textEl.classList.remove('is-swapping');
			iconEl.classList.remove('is-swapping');
		}, 250);
	}

	function mostrar() {
		// Primer mensaje aleatorio para que no salga siempre el mismo.
		let i = Math.floor(Math.random() * MENSAJES.length);
		aplicarMensaje(MENSAJES[i]);
		overlay.classList.add('is-visible');
		overlay.setAttribute('aria-hidden', 'false');
		rotateTimer = setInterval(() => pintarMensaje(++i), ROTATE_MS);
		safetyTimer = setTimeout(ocultar, SAFETY_MS);
	}

	function ocultar() {
		clearTimeout(showTimer);
		clearInterval(rotateTimer);
		clearTimeout(safetyTimer);
		showTimer = rotateTimer = safetyTimer = null;
		overlay.classList.remove('is-visible');
		overlay.setAttribute('aria-hidden', 'true');
	}

	// Engancha el submit de todos los formularios marcados con data-ts-loading.
	document.querySelectorAll('form[data-ts-loading]').forEach((form) => {
		form.addEventListener('submit', () => {
			// Diferimos: si la respuesta es rápida, ni se pinta ni se deshabilita nada.
			showTimer = setTimeout(() => {
				mostrar();
				// Evita el doble envío. Se hace tras el delay para no interferir
				// con el propio submit (los datos del form ya se enviaron en t=0).
				const btn = form.querySelector('[type="submit"]');
				if (btn) btn.disabled = true;
			}, DELAY_MS);
		});
	});

	// bfcache: al volver atrás, el navegador puede restaurar esta página con el
	// overlay aún visible y el botón deshabilitado. Reseteamos el estado.
	window.addEventListener('pageshow', (e) => {
		if (!e.persisted) return;
		ocultar();
		document.querySelectorAll('form[data-ts-loading] [type="submit"]')
			.forEach((b) => { b.disabled = false; });
	});
})();
