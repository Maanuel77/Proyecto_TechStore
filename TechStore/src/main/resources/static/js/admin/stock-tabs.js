// Filtra las filas de la tabla #stockTabla según el tab activo.
// Cada tab tiene data-gravedad="ALL|AGOTADO|CRITICO|BAJO" y cada fila lleva
// su gravedad. Al hacer click se ocultan las filas que no coinciden.
(() => {
	const tabs = document.querySelectorAll('#stockTabs .nav-link');
	const rows = document.querySelectorAll('#stockTabla tbody tr');
	if (!tabs.length || !rows.length) return;

	tabs.forEach(tab => tab.addEventListener('click', () => {
		// Pintamos el tab activo (Bootstrap nav-pills usa .active sobre .nav-link).
		tabs.forEach(t => t.classList.remove('active'));
		tab.classList.add('active');

		const filtro = tab.dataset.gravedad;
		rows.forEach(row => {
			const visible = filtro === 'ALL' || row.dataset.gravedad === filtro;
			row.classList.toggle('d-none', !visible);
		});
	}));
})();
