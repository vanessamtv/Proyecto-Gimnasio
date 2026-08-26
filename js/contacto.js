document.addEventListener("DOMContentLoaded", () => {

    const formulario = document.getElementById("formContacto");

    formulario.addEventListener("submit", (event) => {
        event.preventDefault();

        const nombre = document.getElementById("nombre");
        const correo = document.getElementById("correo");
        const asunto = document.getElementById("asunto");
        const mensaje = document.getElementById("mensaje");

        let formularioValido = true;

        if (nombre.value.trim() === "") {
            nombre.classList.add("is-invalid");
            formularioValido = false;
        } else {
            nombre.classList.remove("is-invalid");
            nombre.classList.add("is-valid");
        }

        const expresionCorreo = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        if (correo.value.trim() === "" || !expresionCorreo.test(correo.value)) {
            correo.classList.add("is-invalid");
            formularioValido = false;
        } else {
            correo.classList.remove("is-invalid");
            correo.classList.add("is-valid");
        }

        if (asunto.value === "") {
            asunto.classList.add("is-invalid");
            formularioValido = false;
        } else {
            asunto.classList.remove("is-invalid");
            asunto.classList.add("is-valid");
        }

        if (mensaje.value.trim() === "") {
            mensaje.classList.add("is-invalid");
            formularioValido = false;
        } else {
            mensaje.classList.remove("is-invalid");
            mensaje.classList.add("is-valid");
        }

        if (formularioValido) {
            const modal = new bootstrap.Modal(document.getElementById("modalContacto"));
            modal.show();
            formulario.reset();
            document.querySelectorAll(".is-valid").forEach(el => el.classList.remove("is-valid"));
        }
    });
});
