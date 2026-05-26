// Modal de detalle del historial de pedidos del cliente.
// Las líneas de todos los pedidos se renderizan ocultas; al abrir el modal
// se muestran solo las del pedido seleccionado.
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

		document.querySelectorAll('.lineas-pedido')
			.forEach(row => row.classList.add('d-none'));
		document.querySelectorAll(`.lineas-pedido[data-pedido="${d.lineas}"]`)
			.forEach(row => row.classList.remove('d-none'));
	});
})();
