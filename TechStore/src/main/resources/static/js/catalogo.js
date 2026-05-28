// Buscador del catálogo (filtra tarjetas en cliente) y modal de detalle de producto.

// Filtrado por nombre/marca: cada tarjeta tiene data-search precalculado.
(() => {
	const input    = document.getElementById('searchInput');
	const cards    = document.querySelectorAll('#productGrid > [data-search]');
	const counter  = document.getElementById('resultCount');
	const noResult = document.getElementById('noResults');
	if (!input) return;

	input.addEventListener('input', () => {
		const q = input.value.toLowerCase();
		let visible = 0;
		cards.forEach(card => {
			const match = !q || card.dataset.search.includes(q);
			//card.style.display = match ? '' : 'none';
			card.classList.toggle('d-none', !match)
			if (match) visible++;
		});
		counter.textContent = visible + ' resultados';
		noResult.classList.toggle('d-none', visible > 0);
	});
})();

// Modal de detalle: rellena la vista con los data-* de la tarjeta clicada
// y ajusta el form de "añadir al carrito" según el stock disponible.
(() => {
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

		// El nivel (AGOTADO/BAJO/DISPONIBLE) lo decide el backend (NivelStock.evaluar).
		// El JS solo lee el data-nivel ya calculado para decidir qué badge mostrar.
		const stock    = parseInt(d.stock, 10);
		const nivel    = d.nivel;
		const agotado  = nivel === 'AGOTADO';
		toggle('modalBadgeRefurbished', d.refurbished === 'true');
		toggle('modalBadgeEnStock',     nivel === 'DISPONIBLE');
		toggle('modalBadgePocas',       nivel === 'BAJO');
		toggle('modalBadgeAgotado',     agotado);

		form.action          = '/carrito/anadir/' + d.id;
		cantidadInput.value  = agotado ? 0 : 1;
		cantidadInput.min    = agotado ? 0 : 1;
		cantidadInput.max    = stock;
		cantidadInput.disabled = agotado;
		btnMenos.disabled    = agotado;
		btnMas.disabled      = agotado;
		btnAnadir.disabled   = agotado;
		btnAnadir.innerHTML  = agotado
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
