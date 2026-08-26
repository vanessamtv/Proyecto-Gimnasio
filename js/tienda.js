const productos = [
    {
        id: 1,
        nombre: "Polo Imperium Cross",
        categoria: "ropa",
        precio: "S/ 49.90",
        imagen: "../img/productos/polo.jpg",
        descripcion: "Polo deportivo Imperium Cross de material ligero y cómodo para tus entrenamientos."
    },
    {
        id: 2,
        nombre: "Short Deportivo",
        categoria: "ropa",
        precio: "S/ 59.90",
        imagen: "../img/productos/short.jpg",
        descripcion: "Short deportivo ligero ideal para entrenamiento y cardio."
    },
    {
        id: 3,
        nombre: "Leggings Imperium Cross",
        categoria: "ropa",
        precio: "S/ 69.90",
        imagen: "../img/productos/leggings.jpg",
        descripcion: "Leggings deportivos cómodos y flexibles para tus rutinas."
    },
    {
        id: 4,
        nombre: "Casaca Imperium Cross",
        categoria: "ropa",
        precio: "S/ 99.90",
        imagen: "../img/productos/casaca.jpg",
        descripcion: "Casaca deportiva ideal para entrenamientos al aire libre."
    },
    {
        id: 5,
        nombre: "Shaker Imperium Cross",
        categoria: "accesorios",
        precio: "S/ 29.90",
        imagen: "../img/productos/shaker.jpg",
        descripcion: "Shaker deportivo para preparar tus bebidas y suplementos."
    },
    {
        id: 6,
        nombre: "Botella Deportiva",
        categoria: "accesorios",
        precio: "S/ 24.90",
        imagen: "../img/productos/botella.jpg",
        descripcion: "Botella reutilizable para mantenerte hidratado durante tu entrenamiento."
    },
    {
        id: 7,
        nombre: "Toalla Deportiva",
        categoria: "accesorios",
        precio: "S/ 19.90",
        imagen: "../img/productos/toalla.jpg",
        descripcion: "Toalla ligera para acompañar tus sesiones de entrenamiento."
    },
    {
        id: 8,
        nombre: "Bolso Deportivo",
        categoria: "accesorios",
        precio: "S/ 49.90",
        imagen: "../img/productos/bolso.jpg",
        descripcion: "Bolso amplio para llevar tus accesorios deportivos."
    },
    {
        id: 9,
        nombre: "Agua Imperium Cross",
        categoria: "bebidas",
        precio: "S/ 3.00",
        imagen: "../img/productos/agua.jpg",
        descripcion: "Agua para mantenerte hidratado durante tu rutina."
    },
    {
        id: 10,
        nombre: "Bebida Deportiva",
        categoria: "bebidas",
        precio: "S/ 7.90",
        imagen: "../img/productos/bebida-deportiva.jpg",
        descripcion: "Bebida para acompañar tus sesiones de entrenamiento."
    },
    {
        id: 11,
        nombre: "Bebida Energética",
        categoria: "bebidas",
        precio: "S/ 8.90",
        imagen: "../img/productos/bebida-energetica.jpg",
        descripcion: "Bebida energética para complementar tus sesiones deportivas."
    }
];

document.addEventListener("DOMContentLoaded", () => {

    const botones = document.querySelectorAll(".btn-categoria");
    const productosHTML = document.querySelectorAll(".producto");

    botones.forEach(boton => {
        boton.addEventListener("click", () => {
            const categoria = boton.getAttribute("data-categoria");

            botones.forEach(btn => btn.classList.remove("activo"));
            boton.classList.add("activo");

            productosHTML.forEach(producto => {
                const cat = producto.getAttribute("data-categoria");
                if (categoria === "todos" || categoria === cat) {
                    producto.style.display = "block";
                } else {
                    producto.style.display = "none";
                }
            });
        });
    });
});

function verProducto(id) {

    const producto = productos.find(item => item.id === id);
    if (!producto) return;

    document.getElementById("modalTitulo").textContent = producto.nombre;
    document.getElementById("modalPrecio").textContent = producto.precio;
    document.getElementById("modalDescripcion").textContent = producto.descripcion;
    document.getElementById("modalImagen").src = producto.imagen;

    const opcionesTalla = document.getElementById("opcionesTalla");
    opcionesTalla.style.display = (producto.categoria === "ropa") ? "block" : "none";

    const modal = new bootstrap.Modal(document.getElementById("productoModal"));
    modal.show();

    window.productoSeleccionado = producto;
}

function agregarProducto() {

    if (!window.productoSeleccionado) return;

    const producto = window.productoSeleccionado;

    alert("Producto agregado correctamente.\n\n" + producto.nombre + "\n" + producto.precio);

    const modalEl = document.getElementById("productoModal");
    const modal = bootstrap.Modal.getInstance(modalEl);
    if (modal) modal.hide();
}
