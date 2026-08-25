const sessionData = {
    fundamentos: {
        title: "Fundamentos",
        description: "Aprende los patrones esenciales, mejora tu movilidad y entrena con confianza desde el primer día.",
        time: "60 min"
    },
    fuerza: {
        title: "Fuerza",
        description: "Trabaja levantamientos clave con progresiones claras, control técnico y una dosis justa de intensidad.",
        time: "75 min"
    },
    metcon: {
        title: "Metcon",
        description: "Combina movimientos funcionales a alta intensidad para elevar tu capacidad cardiovascular y mental.",
        time: "45 min"
    }
};

const revealItems = document.querySelectorAll(".reveal");
const sessionCards = document.querySelectorAll(".type-card");
const title = document.querySelector("#session-title");
const description = document.querySelector("#session-description");
const time = document.querySelector("#session-time");
const menuToggle = document.querySelector(".menu-toggle");
const navigation = document.querySelector(".main-nav");

const revealObserver = new IntersectionObserver((entries) => {
    entries.forEach((entry) => {
        if (entry.isIntersecting) {
            entry.target.classList.add("is-visible");
            revealObserver.unobserve(entry.target);
        }
    });
}, { threshold: 0.12 });

revealItems.forEach((item) => revealObserver.observe(item));

sessionCards.forEach((card) => {
    card.querySelector(".text-link").addEventListener("click", () => {
        const session = sessionData[card.dataset.session];
        title.textContent = session.title;
        description.textContent = session.description;
        time.textContent = session.time;
        sessionCards.forEach((item) => item.classList.remove("selected"));
        card.classList.add("selected");
    });
});

menuToggle.addEventListener("click", () => {
    const isOpen = navigation.classList.toggle("open");
    menuToggle.setAttribute("aria-expanded", String(isOpen));
});
