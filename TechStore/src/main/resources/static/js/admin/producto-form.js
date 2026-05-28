// Preview en vivo del formulario de añadir/editar producto.
// Mientras el admin escribe o pega valores, la columna izquierda
// (imagen, marca, nombre y precio) se actualiza sin necesidad de guardar.
(() => {
	const inputImg    = document.getElementById('campoImagen');
	const inputNombre = document.getElementById('campoNombre');
	const inputMarca  = document.getElementById('campoMarca');
	const inputPrecio = document.getElementById('campoPrecio');
	const img         = document.getElementById('previewImg');
	const placeholder = document.getElementById('previewPlaceholder');
	const previewNom  = document.getElementById('previewNombre');
	const previewMar  = document.getElementById('previewMarca');
	const previewPre  = document.getElementById('previewPrecio');

	if (!inputImg || !img) return; // por si el script se incluye en otra pantalla

	// Helpers para alternar visibilidad sin pelearse con el `!important`
	// de las clases display (d-flex / d-none) de Bootstrap. Los hijos de .ratio
	// son position:absolute y se solapan, así que ambos elementos viven en el
	// mismo hueco y hay que ocultar uno mientras se muestra el otro.
	function mostrarImagen() {
		img.classList.remove('d-none');
		placeholder.classList.remove('d-flex');
		placeholder.classList.add('d-none');
	}
	function mostrarPlaceholder() {
		img.classList.add('d-none');
		placeholder.classList.remove('d-none');
		placeholder.classList.add('d-flex');
	}

	function actualizarImagen() {
		const url = inputImg.value.trim();
		if (!url) {
			mostrarPlaceholder();
			return;
		}
		img.src = url;
		mostrarImagen();
	}
	// Si la imagen falla al cargar (URL rota, dominio caído...), volvemos al placeholder.
	img.addEventListener('error', mostrarPlaceholder);
	inputImg.addEventListener('input', actualizarImagen);

	//Nombre / Marca
	inputNombre.addEventListener('input', () => {
		previewNom.textContent = inputNombre.value.trim() || 'Nombre del producto';
	});
	inputMarca.addEventListener('input', () => {
		previewMar.textContent = inputMarca.value.trim() || 'Marca';
	});

	//Precio
	inputPrecio.addEventListener('input', () => {
		const v = parseFloat(inputPrecio.value);
		previewPre.textContent = (isNaN(v) ? 0 : v).toFixed(2).replace('.', ',') + ' €';
	});
})();
