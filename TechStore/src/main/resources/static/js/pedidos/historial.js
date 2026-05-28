// Modal de detalle del historial de pedidos del cliente.
// Las líneas de todos los pedidos se renderizan ocultas; al abrir el modal
// se muestran solo las del pedido seleccionado. Si el pedido tenía cupón
// aplicado, se muestran también las filas de Subtotal y Descuento.
(() => {
	const modal = document.getElementById('pedidoModal');
	if (!modal) return;

	modal.addEventListener('show.bs.modal', event => {
		const btn = event.relatedTarget;
		if (!btn) return;
		const d = btn.dataset;

		document.getElementById('modalCodigo').textContent = 'Pedido ' + d.codigo;
		document.getElementById('modalFecha').textContent  = d.fecha;
		document.getElementById('modalTotal').textContent  = d.total;

		// Filas de subtotal y descuento: solo si el pedido tenía cupón aplicado.
		// El subtotal se reconstruye a partir del total y el %, ya que en BD
		// guardamos los precios originales en las líneas y el total ya descontado.
		const subRow = document.getElementById('modalSubtotalRow');
		const dctoRow = document.getElementById('modalDescuentoRow');
		const pct = parseFloat(d.cuponDescuento);
		const totalRaw = parseFloat(d.totalRaw);
		if (d.cuponCodigo && pct > 0 && !isNaN(totalRaw)) {
			const subtotal = totalRaw / (1 - pct);
			const descuento = subtotal - totalRaw;
			document.getElementById('modalSubtotal').textContent = formatEuro(subtotal);
			document.getElementById('modalDescuentoImporte').textContent = formatEuro(descuento);
			document.getElementById('modalCuponCodigo').textContent = d.cuponCodigo;
			document.getElementById('modalCuponPct').textContent = Math.round(pct * 100);
			subRow.hidden = false;
			dctoRow.hidden = false;
		} else {
			subRow.hidden = true;
			dctoRow.hidden = true;
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
