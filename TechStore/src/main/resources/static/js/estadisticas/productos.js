// Gráfico de barras horizontales para el ranking de productos más vendidos.
// Lee los datos desde los data-* de las filas de #tablaTop (sin duplicarlos).
(() => {
	const rows = document.querySelectorAll('#tablaTop tbody tr');
	if (!rows.length) return;

	const labels = [...rows].map(r => r.dataset.nombre);
	const data   = [...rows].map(r => Number(r.dataset.unidades));
	const total  = data.reduce((a, b) => a + b, 0);

	const canvas = document.getElementById('chartTop');
	const ctx    = canvas.getContext('2d');

	// Degradado horizontal para las barras (azul → violeta TechStore).
	const gradient = ctx.createLinearGradient(0, 0, canvas.width, 0);
	gradient.addColorStop(0, '#0d6efd');
	gradient.addColorStop(1, '#6610f2');

	new Chart(ctx, {
		type: 'bar',
		data: {
			labels,
			datasets: [{
				data,
				backgroundColor: gradient,
				borderRadius: 8,
				barThickness: 22,
			}],
		},
		options: {
			indexAxis: 'y',
			responsive: true,
			maintainAspectRatio: false,
			animation: { duration: 800, easing: 'easeOutQuart' },
			plugins: {
				legend: { display: false },
				tooltip: {
					backgroundColor: 'rgba(33, 37, 41, 0.95)',
					padding: 12,
					cornerRadius: 8,
					displayColors: false,
					callbacks: {
						label: c => ` ${c.parsed.x} uds. · ${((c.parsed.x / total) * 100).toFixed(1)}% del total`,
					},
				},
			},
			scales: {
				x: {
					beginAtZero: true,
					ticks: { precision: 0, color: '#6c757d' },
					grid:  { color: 'rgba(0, 0, 0, 0.04)' },
				},
				y: {
					ticks: { color: '#212529', font: { weight: '600' } },
					grid:  { display: false },
				},
			},
		},
	});
})();
