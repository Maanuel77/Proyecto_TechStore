// Cooldown del botón "Reenviar código" en el paso 2 del cambio de contraseña.
// Mismo patrón que el de /auth/verificar pero con sus propios IDs para que
// no colisionen si algún día conviven en la misma página.
(() => {
	const btn   = document.getElementById('btnReenviarPwd');
	const label = document.getElementById('reenviarLabelPwd');
	if (!btn || !label) return;

	let restantes = parseInt(btn.dataset.cooldown, 10) || 0;
	if (restantes > 0) {
		btn.disabled = true;
		label.textContent = 'Reenviar en ' + restantes + 's…';
		const timer = setInterval(() => {
			restantes--;
			if (restantes <= 0) {
				clearInterval(timer);
				btn.disabled = false;
				label.textContent = 'Reenviar código';
			} else {
				label.textContent = 'Reenviar en ' + restantes + 's…';
			}
		}, 1000);
	}
})();
