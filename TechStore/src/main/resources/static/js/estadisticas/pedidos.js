// Gráfico de línea con área sombreada para la evolución de pedidos.
// Lee los datos desde los data-* de #tablaPedidos (sin duplicarlos).
(() => {
	const rows = document.querySelectorAll('#tablaPedidos tbody tr');
	if (!rows.length) return;

	const labels   = [...rows].map(r => r.dataset.etiqueta);
	const pedidos  = [...rows].map(r => Number(r.dataset.pedidos));
	const ingresos = [...rows].map(r => Number(r.dataset.ingresos));

	const canvas = document.getElementById('chartPedidos');
	const ctx    = canvas.getContext('2d');

	// Degradado vertical para el área bajo la línea (azul → transparente).
	const area = ctx.createLinearGradient(0, 0, 0, canvas.height || 360);
	area.addColorStop(0,   'rgba(13, 110, 253, 0.35)');
	area.addColorStop(1,   'rgba(13, 110, 253, 0.00)');

	new Chart(ctx, {
		type: 'line',
		data: {
			labels,
			datasets: [{
				label: 'Pedidos',
				data: pedidos,
				borderColor: '#0d6efd',
				backgroundColor: area,
				borderWidth: 2.5,
				tension: 0.35,            // curva suave entre puntos
				fill: true,
				pointBackgroundColor: '#0d6efd',
				pointBorderColor: '#fff',
				pointBorderWidth: 2,
				pointRadius: 4,
				pointHoverRadius: 7,
			}],
		},
		options: {
			responsive: true,
			maintainAspectRatio: false,
			animation: { duration: 800, easing: 'easeOutQuart' },
			interaction: { mode: 'index', intersect: false },
			plugins: {
				legend: { display: false },
				tooltip: {
					backgroundColor: 'rgba(33, 37, 41, 0.95)',
					padding: 12,
					cornerRadius: 8,
					displayColors: false,
					callbacks: {
						label: c => {
							const ing = ingresos[c.dataIndex];
							const eur = ing.toLocaleString('es-ES', {
								minimumFractionDigits: 2, maximumFractionDigits: 2,
							});
							return [` ${c.parsed.y} pedidos`, ` ${eur} € en ingresos`];
						},
					},
				},
			},
			scales: {
				x: {
					ticks: { color: '#6c757d', maxRotation: 0, autoSkipPadding: 16 },
					grid:  { display: false },
				},
				y: {
					beginAtZero: true,
					ticks: { precision: 0, color: '#6c757d' },
					grid:  { color: 'rgba(0, 0, 0, 0.04)' },
				},
			},
		},
	});
})();
