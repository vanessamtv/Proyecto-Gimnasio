document.addEventListener('DOMContentLoaded', () => {

    // 1. BASE DE DATOS DE SEDES Y PRECIOS
    const datosGimnasios = {
        centro: {
            nombre: "FITGYM Centro",
            direccion: "Av. Arequipa 1250, Lima - Lima",
            black: { precio: "S/ 59,90", desc: "53% OFF", posterior: "S/ 129,90" },
            fit: { precio: "S/ 59,90", desc: "40% OFF", posterior: "S/ 99,90" },
            smart: { precio: "S/ 109,90" }
        },
        miraflores: {
            nombre: "FITGYM Miraflores",
            direccion: "Av. Larco 850, Miraflores - Lima",
            black: { precio: "S/ 59,90", desc: "53% OFF", posterior: "S/ 129,90" },
            fit: { precio: "S/ 59,90", desc: "40% OFF", posterior: "S/ 99,90" },
            smart: { precio: "S/ 109,90" }
        },
        surco: {
            nombre: "FITGYM Surco",
            direccion: "Av. Caminos del Inca 450, Surco - Lima",
            black: { precio: "S/ 64,90", desc: "50% OFF", posterior: "S/ 129,90" },
            fit: { precio: "S/ 54,90", desc: "45% OFF", posterior: "S/ 99,90" },
            smart: { precio: "S/ 104,90" }
        },
        sanmiguel: {
            nombre: "FITGYM San Miguel",
            direccion: "Av. La Marina 2200, San Miguel - Lima",
            black: { precio: "S/ 59,90", desc: "53% OFF", posterior: "S/ 129,90" },
            fit: { precio: "S/ 49,90", desc: "50% OFF", posterior: "S/ 99,90" },
            smart: { precio: "S/ 99,90" }
        }
    };

    // 2. ACTUALIZAR INFORMACIÓN SEGÚN LA SEDE ELEGIDA (URL)
    const parametros = new URLSearchParams(window.location.search);
    const gymSeleccionado = parametros.get('gym') || 'centro';

    if (datosGimnasios[gymSeleccionado]) {
        const gym = datosGimnasios[gymSeleccionado];

        // Nombre y dirección
        const elNombre = document.getElementById('gym-nombre');
        const elDireccion = document.getElementById('gym-direccion');
        if (elNombre) elNombre.textContent = gym.nombre;
        if (elDireccion) elDireccion.textContent = gym.direccion;

        // Plan Black
        const elBlackPrecio = document.getElementById('black-precio');
        const elBlackDesc = document.getElementById('black-descuento');
        const elBlackCond = document.getElementById('black-condicion');
        if (elBlackPrecio) elBlackPrecio.textContent = gym.black.precio;
        if (elBlackDesc) elBlackDesc.textContent = gym.black.desc;
        if (elBlackCond) elBlackCond.textContent = `mensual por 3 meses, después ${gym.black.posterior}/mes`;

        // Plan Fit
        const elFitPrecio = document.getElementById('fit-precio');
        const elFitDesc = document.getElementById('fit-descuento');
        const elFitCond = document.getElementById('fit-condicion');
        if (elFitPrecio) elFitPrecio.textContent = gym.fit.precio;
        if (elFitDesc) elFitDesc.textContent = gym.fit.desc;
        if (elFitCond) elFitCond.textContent = `mensual por 3 meses, después ${gym.fit.posterior}/mes`;

        // Plan Smart
        const elSmartPrecio = document.getElementById('smart-precio');
        if (elSmartPrecio) elSmartPrecio.textContent = gym.smart.precio;
    }

    // 3. DESPLIEGUE/COLAPSO INDIVIDUAL DE CADA TARJETA
    const botonesVer = document.querySelectorAll('.btn-ver');

    botonesVer.forEach(boton => {
        boton.addEventListener('click', (e) => {
            const tarjeta = e.target.closest('.plan-card');
            tarjeta.classList.toggle('colapsado');
            
            if (tarjeta.classList.contains('colapsado')) {
                boton.textContent = 'VER MÁS ▼';
            } else {
                boton.textContent = 'VER MENOS ▲';
            }
        });
    });

    // 4. REDIRECCIÓN AL CLIC EN "ELEGIR PLAN"
    const botonesElegir = document.querySelectorAll('.btn-elegir');

    botonesElegir.forEach(boton => {
        boton.addEventListener('click', (e) => {
            const plan = boton.getAttribute('data-plan');
            window.location.href = `pago.html?gym=${gymSeleccionado}&plan=${plan}`;
        });
    });

});