# Imperium Cross - Backend (Spring Boot)

Backend con logica de negocio para el proyecto Imperium Cross, curso
de marcos de desarrollo web, siguiendo la estructura MVC:
`controller / model / repository / service`.

## Importante: como previsualizarlo

Este es un proyecto **Spring Boot + Thymeleaf**, no HTML plano. Las plantillas
usan `th:replace` para el navbar/footer y rutas como `/img/...` y `/css/...`
que Spring sirve desde `src/main/resources/static/` cuando la app esta
corriendo. Si abres `index.html` directo con Live Server (o doble clic desde
el explorador de archivos), el navegador NO procesa las etiquetas `th:*` ni
resuelve esas rutas, por eso se ve sin logo, sin fondo y con "0" fijo en vez
de los datos reales.

Para verlo como se disenio:
```bash
./mvnw spring-boot:run
```
y abre `http://localhost:8080` (no necesitas instalar nada mas, ver mas abajo).

## Frontend

El panel (Thymeleaf + Bootstrap 5) reutiliza el mismo sistema visual del
sitio original de Imperium Cross en vez de Bootstrap generico:
`src/main/resources/static/css/panel.css` trae las mismas variables de
color (`--ic-black`, `--ic-red`, `--ic-gold`), las mismas tipografias
(Anton para titulos, Barlow/Barlow Condensed para el resto) y las clases
`navbar-ic`, `btn-ic-primary/outline/gold`, `card-ic`, `table-ic`,
`badge-ic-*` que ya existian en `css/styles.css` del sitio estatico.

El logo usa dos versiones: `logo-mark.png` (solo el icono, para la barra de
navegacion) y `logo-full.png` (icono + "IMPERIUM CROSS", para el pie de
pagina). El banner de portada usa `entrenamiento.jpg` y la seccion "Nuestras
zonas" usa `maquinas.jpg`, `materiales.jpg`, `nutricion.jpg` y `espera.jpg`
— son las unicas 7 imagenes del proyecto, todas comprimidas y ya incluidas
en `src/main/resources/static/img/` (492 KB en total). Las imagenes de
producto y fotos sueltas que no se usaban en ninguna vista (tienda, fotos
sin nombre) se quitaron para no cargar peso de mas.

## Logica de negocio (avance 2)

- **Cliente**: alta con DNI/email unicos, validacion de mayoria de edad minima
  (16 anios), baja logica (no se borra el historial), busqueda por nombre.
- **Membresia**: planes SMART / BLACK / FIT, con los mismos precios que se
  veian en `inscripciones.html` / `planes.html` del sitio original, para
  nuestro unico local (Chosica). Calcula automaticamente la fecha de fin
  (30 dias), encadena renovaciones si el cliente ya tiene un plan vigente,
  y una tarea programada (`@Scheduled`) marca como VENCIDA la membresia
  cuando pasa su fecha de fin.
- **Asistencia**: check-in / check-out por cliente. No deja marcar entrada si
  el cliente no tiene membresia vigente, ni marcar dos entradas sin salida el
  mismo dia; calcula minutos entrenados y asistencias del mes.

## Como correrlo

No necesitas instalar MySQL ni nada externo: el proyecto usa **H2**, una
base de datos embebida que guarda los datos en un archivo local
(`./data/fitgym.mv.db`) que persiste entre ejecuciones.

1. Ejecuta:
   ```bash
   ./mvnw spring-boot:run
   ```
2. Abre `http://localhost:8080`.

Si mas adelante quieres usar MySQL en vez de H2 (por ejemplo para un
despliegue real), en `src/main/resources/application.properties` comenta
el bloque de H2 y descomenta el bloque de MySQL que ya esta preparado
ahi mismo (necesitas tener MySQL corriendo en `localhost:3306`).

## Pendiente / siguientes avances

- Las paginas estaticas del sitio original que aun no se migraron
  (`tienda.html`, `contacto.html`, `entrenamiento.html`, `pago.html`) siguen
  siendo HTML plano fuera de este backend.
- No se agrego seguridad (login) porque no se pidio; si el curso lo requiere
  en un siguiente avance, se puede sumar Spring Security.
