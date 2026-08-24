/* ========================================
   DATOS DE LOS GIMNASIOS
======================================== */

const gimnasios = {

    centro: {
        nombre: "FIT GYM CENTRO",
        direccion: "Av. Arequipa 1250"
    },

    miraflores: {
        nombre: "FIT GYM MIRAFLORES",
        direccion: "Av. Larco 850"
    },

    surco: {
        nombre: "FIT GYM SURCO",
        direccion: "Av. Caminos del Inca 450"
    },

    sanmiguel: {
        nombre: "FIT GYM SAN MIGUEL",
        direccion: "Av. La Marina 2200"
    }

};


/* ========================================
   SELECCIONAR GIMNASIO
======================================== */

function seleccionarGym(gym, elemento) {

    // Quitar selección de todos
    document
        .querySelectorAll(".gym-card")
        .forEach(card => {

            card.classList.remove("selected");

        });


    // Seleccionar el actual
    elemento.classList.add("selected");


    // Cambiar nombre
    document.getElementById("nombreGym").textContent =
        gimnasios[gym].nombre;


    // Ir automáticamente a los planes
    document.getElementById("planes").scrollIntoView({

        behavior: "smooth",

        block: "start"

    });

}


/* ========================================
   VER MÁS / VER MENOS
======================================== */

function toggleFeatures(button) {

    const card =
        button.closest(".plan-card");

    const features =
        card.querySelector(".features");

    const arrow =
        button.querySelector("span");


    if (features.classList.contains("hidden")) {

        features.classList.remove("hidden");

        button.firstChild.textContent =
            "VER MENOS ";

        arrow.textContent = "⌃";

    } else {

        features.classList.add("hidden");

        button.firstChild.textContent =
            "VER MÁS ";

        arrow.textContent = "⌄";

    }

}


/* ========================================
   ELEGIR PLAN
======================================== */

function elegirPlan(plan) {

    const gimnasio =
        document.getElementById("nombreGym")
            .textContent;


    alert(
        "Has seleccionado:\n\n" +
        gimnasio +
        "\n" +
        plan +
        "\n\n" +
        "Continuaremos con tu inscripción."
    );

}