/* FITZONE — Imperium Cross | JS del Integrante 1
   1) Marca la página activa en el menú.
   2) Simula cupos disponibles por turno.
   3) Valida el formulario de reserva. */

document.addEventListener('DOMContentLoaded', function () {

  // 1) Link activo del navbar
  const paginaActual = location.pathname.split('/').pop() || 'index.html';
  document.querySelectorAll('.navbar-ic .nav-link').forEach(link => {
    if (link.getAttribute('href') === paginaActual) link.classList.add('active');
  });

  // 2) Cupos disponibles por turno (datos simulados, sin backend)
  const cuposPorTurno = { manana: 9, mediodia: 4, tarde: 2, noche: 12 };
  const selectTurno = document.getElementById('turnoReserva');
  const cuposTexto = document.getElementById('cuposIndicador');

  if (selectTurno) {
    selectTurno.addEventListener('change', function () {
      const disponibles = cuposPorTurno[this.value];
      cuposTexto.className = 'cupos-indicador small mb-3 ' +
        (disponibles <= 2 ? 'text-danger' : disponibles <= 6 ? 'text-warning' : 'text-success');
      cuposTexto.textContent = `Quedan ${disponibles} cupos en este turno`;
    });
  }

  // 3) Validación y confirmación del formulario de reserva
  const form = document.getElementById('formReserva');
  const confirmacion = document.getElementById('reservaConfirmacion');

  if (form) {
    form.addEventListener('submit', function (e) {
      e.preventDefault();
      if (!form.checkValidity()) {
        form.classList.add('was-validated');
        return;
      }
      const nombre = document.getElementById('nombreReserva').value.trim();
      const turno = selectTurno.options[selectTurno.selectedIndex].text;

      confirmacion.classList.remove('d-none');
      confirmacion.innerHTML = `<strong>${nombre}</strong>, tu cupo para el turno <strong>${turno}</strong> quedó preseleccionado.`;

      form.reset();
      form.classList.remove('was-validated');
      cuposTexto.textContent = '';

      const modal = bootstrap.Modal.getInstance(document.getElementById('modalReserva'));
      setTimeout(() => modal && modal.hide(), 2000);
    });
  }

  // Limpiar el mensaje al cerrar el modal
  document.getElementById('modalReserva')?.addEventListener('hidden.bs.modal', function () {
    confirmacion?.classList.add('d-none');
    form?.classList.remove('was-validated');
  });

});
