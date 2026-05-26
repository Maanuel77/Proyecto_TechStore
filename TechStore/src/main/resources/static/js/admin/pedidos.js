// Modal de detalle del listado de pedidos del admin.
// Muestra cliente y email además de los datos básicos del pedido.
(() => {
	const modal = document.getElementById('pedidoModal');
	if (!modal) return;

	modal.addEventListener('show.bs.modal', event => {
		const btn = event.relatedTarget;
		if (!btn) return;
		const d = btn.dataset;

		document.getElementById('modalCodigo').textContent  = 'Pedido ' + d.codigo;
		document.getElementById('modalFecha').textContent   = d.fecha;
		document.getElementById('modalCliente').textContent = d.cliente;
		document.getElementById('modalEmail').textContent   = d.email ? '(' + d.email + ')' : '';
		document.getElementById('modalTotal').textContent   = d.total;

		document.querySelectorAll('.lineas-pedido')
			.forEach(row => row.classList.add('d-none'));
		document.querySelectorAll(`.lineas-pedido[data-pedido="${d.lineas}"]`)
			.forEach(row => row.classList.remove('d-none'));
	});
})();
