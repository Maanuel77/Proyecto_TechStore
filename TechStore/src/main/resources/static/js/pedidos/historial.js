// Modal de detalle del historial de pedidos del cliente.
// Las líneas de todos los pedidos se renderizan ocultas; al abrir el modal
// se muestran solo las del pedido seleccionado. Si el pedido tenía cupón
// aplicado, se muestran también las filas de Subtotal y Descuento.
(() => {
	const modal = document.getElementById('pedidoModal');
	if (!modal) return;

	const subRow  = document.getElementById('modalSubtotalRow');
	const dctoRow = document.getElementById('modalDescuentoRow');

	modal.addEventListener('show.bs.modal', event => {
		const btn = event.relatedTarget;
		if (!btn) return;
		const d = btn.dataset;

		document.getElementById('modalCodigo').textContent = 'Pedido ' + d.codigo;
		document.getElementById('modalFecha').textContent  = d.fecha;
		document.getElementById('modalTotal').textContent  = d.total;

		// Filas de subtotal y descuento: solo si el pedido tenía cupón aplicado.
		// Validamos: cuponCodigo no vacío y no literal "null"; descuento numérico > 0.
		// El subtotal lo calcula el backend (Pedido.getSubtotalSinDescuento) y lo
		// pasa ya hecho en data-subtotal-raw: el JS solo lo lee.
		const code        = d.cuponCodigo;
		const pct         = parseFloat(d.cuponDescuento);
		const subtotalRaw = parseFloat(d.subtotalRaw);
		const hayCupon    = code && code !== 'null' && code.trim() !== ''
		                    && !isNaN(pct) && pct > 0
		                    && !isNaN(subtotalRaw);

		if (hayCupon) {
			const descuentoImporte = subtotalRaw * pct;
			document.getElementById('modalSubtotal').textContent         = formatEuro(subtotalRaw);
			document.getElementById('modalDescuentoImporte').textContent = formatEuro(descuentoImporte);
			document.getElementById('modalCuponCodigo').textContent      = code;
			document.getElementById('modalCuponPct').textContent         = Math.round(pct * 100);
			// Mostrar: quita d-none Y añade d-flex (Bootstrap define d-flex después
			// que d-none en su CSS; si dejásemos las dos, d-flex ganaría).
			subRow.classList.remove('d-none');
			subRow.classList.add('d-flex');
			dctoRow.classList.remove('d-none');
			dctoRow.classList.add('d-flex');
		} else {
			// Limpiamos para que no quede info del pedido anterior visible.
			document.getElementById('modalSubtotal').textContent          = '0,00';
			document.getElementById('modalDescuentoImporte').textContent  = '0,00';
			document.getElementById('modalCuponCodigo').textContent       = '';
			document.getElementById('modalCuponPct').textContent          = '0';
			// Ocultar: quita d-flex Y añade d-none.
			subRow.classList.remove('d-flex');
			subRow.classList.add('d-none');
			dctoRow.classList.remove('d-flex');
			dctoRow.classList.add('d-none');
		}

		document.querySelectorAll('.lineas-pedido')
			.forEach(row => row.classList.add('d-none'));
		document.querySelectorAll(`.lineas-pedido[data-pedido="${d.lineas}"]`)
			.forEach(row => row.classList.remove('d-none'));
	});

	function formatEuro(n) {
		return n.toFixed(2).replace('.', ',');
	}
})();
