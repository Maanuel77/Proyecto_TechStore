// Comportamientos del carrito:
//   1. El input numérico del stepper envía el form al perder foco o pulsar Enter
//      (asi no hace falta un boton "actualizar" visible).
//   2. Cupon decorativo: muestra un mensaje simulado ya que no hay logica real.

(() => {

	// --- Stepper: auto-submit al editar la cantidad ---
	document.querySelectorAll('.ts-stepper-input').forEach(input => {
		const enviar = () => {
			if (!input.checkValidity()) return;     // no enviar valores invalidos
			if (Number(input.value) === Number(input.defaultValue)) return; // sin cambios
			input.form.submit();
		};
		input.addEventListener('change', enviar);   // se dispara al blur tras editar
		input.addEventListener('keydown', e => {
			if (e.key === 'Enter') { e.preventDefault(); enviar(); }
		});
	});

	// --- Cupon decorativo ---
	const btnCupon  = document.getElementById('aplicarCupon');
	const inputCup  = document.getElementById('cupon');
	const feedback  = document.getElementById('cuponFeedback');
	if (btnCupon && inputCup && feedback) {
		btnCupon.addEventListener('click', () => {
			const code = inputCup.value.trim();
			if (!code) {
				feedback.textContent = 'Introduce un código para canjear.';
				feedback.className = 'text-warning';
				return;
			}
			feedback.textContent = `El código "${code}" no es válido o ha caducado.`;
			feedback.className = 'text-danger';
		});
	}

})();
