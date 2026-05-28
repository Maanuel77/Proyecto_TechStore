// Pantalla de verificación 2FA.
//   1. Cooldown del botón "Reenviar código": llega ya activo o con N segundos
//      por delante (data-cooldown del backend). El botón se desactiva y muestra
//      el countdown hasta que llega a 0.
//   2. Forzar solo dígitos en el input del código (extra defense además del pattern).
(() => {
	const btn   = document.getElementById('btnReenviar');
	const label = document.getElementById('reenviarLabel');

	if (btn && label) {
		let restantes = parseInt(btn.dataset.cooldown, 10) || 0;
		if (restantes > 0) {
			btn.disabled = true;
			label.textContent = 'Reenviar en ' + restantes + 's…';
			const timer = setInterval(() => {
				restantes--;
				if (restantes <= 0) {
					clearInterval(timer);
					btn.disabled = false;
					label.textContent = '¿No te ha llegado? Reenviar código';
				} else {
					label.textContent = 'Reenviar en ' + restantes + 's…';
				}
			}, 1000);
		}
	}

	// Limpia caracteres no numéricos al pegar/escribir.
	const codigoInput = document.querySelector('input[name="codigo"]');
	if (codigoInput) {
		codigoInput.addEventListener('input', () => {
			codigoInput.value = codigoInput.value.replace(/\D/g, '').slice(0, 6);
		});
	}
})();
