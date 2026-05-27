// Comportamientos del carrito:
//   1. Stepper: el input numérico envía el form al perder foco o pulsar Enter.
//   2. Modal de detalle del producto sugerido: rellena los datos desde los
//      data-* de la tarjeta clicada y prepara el form para añadir al carrito
//      (volver=carrito → no salimos de /carrito al añadir).

(() => {

	// --- Stepper: auto-submit al editar la cantidad ---
	document.querySelectorAll('.ts-stepper-input').forEach(input => {
		const enviar = () => {
			if (!input.checkValidity()) return;
			if (Number(input.value) === Number(input.defaultValue)) return;
			input.form.submit();
		};
		input.addEventListener('change', enviar);
		input.addEventListener('keydown', e => {
			if (e.key === 'Enter') { e.preventDefault(); enviar(); }
		});
	});

	// --- Modal de detalle del producto sugerido ---
	const modal         = document.getElementById('productoModal');
	const cantidadInput = document.getElementById('modalCantidad');
	const btnMenos      = document.getElementById('btnMenos');
	const btnMas        = document.getElementById('btnMas');
	const btnAnadir     = document.getElementById('modalBtnAnadir');
	const form          = document.getElementById('modalForm');
	if (!modal) return;

	const toggle = (id, show) =>
		document.getElementById(id).classList.toggle('d-none', !show);

	modal.addEventListener('show.bs.modal', event => {
		const card = event.relatedTarget;
		if (!card) return;
		const d = card.dataset;

		document.getElementById('modalImg').src = d.imagen && d.imagen !== 'null' ? d.imagen : '';
		document.getElementById('modalImg').alt = d.nombre;
		document.getElementById('modalMarca').textContent    = d.marca;
		document.getElementById('modalNombre').textContent   = d.nombre;
		document.getElementById('modalGarantia').textContent = d.garantia;
		document.getElementById('modalStock').textContent    = d.stock;
		document.getElementById('modalPrecio').textContent   =
			Number(d.precio).toFixed(2).replace('.', ',') + ' €';

		const stock = parseInt(d.stock, 10);
		toggle('modalBadgeRefurbished', d.refurbished === 'true');
		toggle('modalBadgeEnStock',     stock > 5);
		toggle('modalBadgePocas',       stock <= 5 && stock > 0);
		toggle('modalBadgeAgotado',     stock === 0);

		// Apuntamos al endpoint /carrito/anadir/{id}; el volver=carrito viaja
		// como input hidden dentro del propio form.
		form.action          = '/carrito/anadir/' + d.id;
		cantidadInput.value  = stock > 0 ? 1 : 0;
		cantidadInput.min    = stock > 0 ? 1 : 0;
		cantidadInput.max    = stock;
		cantidadInput.disabled = stock === 0;
		btnMenos.disabled    = stock === 0;
		btnMas.disabled      = stock === 0;
		btnAnadir.disabled   = stock === 0;
		btnAnadir.innerHTML  = stock === 0
			? '<i class="bi bi-x-circle me-1"></i>Sin stock'
			: '<i class="bi bi-bag-plus me-1"></i>Añadir al carrito';
	});

	btnMenos.addEventListener('click', () => {
		const v = parseInt(cantidadInput.value, 10) || 1;
		if (v > parseInt(cantidadInput.min, 10)) cantidadInput.value = v - 1;
	});
	btnMas.addEventListener('click', () => {
		const v   = parseInt(cantidadInput.value, 10) || 1;
		const max = parseInt(cantidadInput.max, 10);
		if (v < max) cantidadInput.value = v + 1;
	});

})();
