# Imperium Cross: Sistema de gestión del box

Aplicación web empresarial para administrar **clientes, membresías y asistencias** del box de entrenamiento funcional Imperium Cross (Chosica).

Proyecto del curso **Marcos de Desarrollo Web**, UTP, Avance 3.

---

## Tecnologías

| Componente | Detalle |
|---|---|
| Lenguaje | Java 17 |
| Framework | Spring Boot 4.1.1 (Spring Web MVC) |
| Persistencia | Spring Data JPA con Hibernate |
| Base de datos | PostgreSQL |
| Validación | Spring Validation (Bean Validation) con DTO |
| Vistas | Thymeleaf |
| Estilos | Bootstrap 5.3.3 y hoja de estilos propia |
| Gestión del proyecto | Maven (incluye Maven Wrapper) |

---

## Requisitos previos

1. **JDK 17** instalado (comprueba con `java -version`).
2. **PostgreSQL** (el servidor, no solo pgAdmin) instalado y encendido en el puerto `5432`.
3. **pgAdmin 4** (opcional, para ver las tablas).

> pgAdmin es solo la ventana de administración. Para que la aplicación funcione debe estar instalado y corriendo el **servidor** de PostgreSQL.

---

## Configuración de la base de datos

1. Abre pgAdmin y conéctate a tu servidor PostgreSQL.
2. Crea una base de datos llamada **`dbimperiumcross`**:
   clic derecho en *Databases* → *Create* → *Database...* → nombre `dbimperiumcross` → *Save*.
3. No hace falta crear tablas: Hibernate las genera solas al iniciar la aplicación (`spring.jpa.hibernate.ddl-auto=update`).

La conexión está definida en `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/dbimperiumcross
spring.datasource.username=postgres
spring.datasource.password=${DB_PASSWORD}
```

Si tu PostgreSQL usa otro usuario o puerto, cámbialos en ese archivo.

### La clave de la base de datos (`DB_PASSWORD`)

La clave **no está escrita en el código** para no subirla a GitHub. Se lee de la variable de entorno `DB_PASSWORD`, que debes definir con la clave que pusiste al instalar PostgreSQL **antes de ejecutar la aplicación**.

Si no la defines, la aplicación no inicia y muestra `Could not resolve placeholder 'DB_PASSWORD'`.

---

## Cómo ejecutar la aplicación

Abre una terminal en la carpeta del proyecto (donde está el archivo `pom.xml`). Los dos comandos deben ejecutarse **en la misma ventana de terminal**.

### Windows: PowerShell

```powershell
$env:DB_PASSWORD="tu_clave"
./mvnw spring-boot:run
```

### Windows: CMD

```cmd
set DB_PASSWORD=tu_clave
mvnw spring-boot:run
```

### Linux o macOS

```bash
export DB_PASSWORD="tu_clave"
./mvnw spring-boot:run
```

### Desde IntelliJ IDEA

1. Abre *Run → Edit Configurations...*
2. Selecciona la configuración de `GimnasioApplication`.
3. En *Environment variables* escribe `DB_PASSWORD=tu_clave`.
4. Ejecuta con el botón *Run*.

### Definir la variable de forma permanente en Windows (opcional)

```cmd
setx DB_PASSWORD "tu_clave"
```

Cierra y vuelve a abrir la terminal (y el IDE) para que tome el cambio.

### Abrir la aplicación

Cuando en la consola aparezca `Started GimnasioApplication`, abre en el navegador:

```
http://localhost:8080
```

Para detenerla, presiona `Ctrl + C` en la terminal. Mientras esté detenida, el navegador mostrará `ERR_CONNECTION_REFUSED`.

---

## Funcionalidades

| Módulo | Ruta | Qué permite |
|---|---|---|
| Inicio | `/` | Indicadores: clientes activos, membresías activas, asistencias del día y clientes entrenando |
| Clientes | `/clientes` | Listar, registrar, editar, buscar por nombre o apellido, ver ficha con historial, dar de baja y reactivar |
| Membresías | `/membresias` | Listar, inscribir a un cliente en un plan y cancelar una membresía |
| Asistencias | `/asistencias` | Registrar entrada y salida, ver quién entrena ahora y las asistencias del día |

### CRUD de clientes

| Operación | Ruta HTTP | Método del controlador | Método del servicio |
|---|---|---|---|
| Crear | `POST /clientes/guardar` (sin id) | `guardar()` | `registrar()` |
| Leer | `GET /clientes`, `GET /clientes/{id}` | `listar()`, `detalle()` | `listarActivos()`, `buscarPorNombre()`, `buscarPorId()` |
| Actualizar | `GET /clientes/{id}/editar` y `POST /clientes/guardar` (con id) | `editarForm()`, `guardar()` | `actualizar()` |
| Eliminar (baja lógica) | `POST /clientes/{id}/baja` | `darDeBaja()` | `darDeBaja()` |
| Reactivar | `POST /clientes/{id}/reactivar` | `reactivar()` | `reactivar()` |

La baja es **lógica**: el cliente pasa a `activo = false` y se conserva su historial de membresías y asistencias.

---

## Reglas de negocio

- DNI y correo electrónico **únicos** por cliente.
- Edad mínima de **16 años** para inscribirse.
- Planes **SMART** (S/ 109.90), **BLACK** (S/ 59.90) y **FIT** (S/ 59.90), con vigencia de **30 días**.
- Si el cliente ya tiene una membresía vigente, la nueva comienza al día siguiente de su vencimiento.
- No se puede registrar asistencia sin membresía vigente, ni dos entradas abiertas el mismo día.
- Una tarea programada (`@Scheduled`) se ejecuta todos los días a la **1:00 a. m.** y marca como `VENCIDA` las membresías cuya fecha de fin ya pasó.

## Validaciones (Spring Validation)

Las reglas de formato están en los DTO del paquete `dto`, se activan con `@Valid` y `BindingResult` en el controlador, y los mensajes se muestran en la vista con `th:errors`.

| DTO | Reglas |
|---|---|
| `ClienteDTO` | Nombres y apellidos obligatorios (máx. 80); DNI de 8 dígitos; teléfono de 9 dígitos; correo con formato válido; fecha de nacimiento anterior a hoy |
| `MembresiaDTO` | Cliente, plan y local obligatorios |

---

## Arquitectura y estructura del proyecto

Arquitectura cliente-servidor por capas: `Navegador → Controller → Service → Repository → PostgreSQL`. Thymeleaf devuelve el HTML ya renderizado.

```
src/main/java/pe/edu/utp/gimnasio/
├─ GimnasioApplication.java      (@SpringBootApplication, @EnableScheduling)
├─ controller/                   ClienteController, MembresiaController,
│                                AsistenciaController, HomeController
├─ dto/                          ClienteDTO, MembresiaDTO
├─ model/                        Cliente, Membresia, Asistencia  (entidades JPA)
├─ repository/                   ClienteRepository, MembresiaRepository,
│                                AsistenciaRepository
└─ service/                      ClienteService, MembresiaService,
                                 AsistenciaService

src/main/resources/
├─ application.properties
├─ static/css/panel.css, static/img/
└─ templates/                    index, fragments, clientes/*, membresias/*,
                                 asistencias/*
```

**Modelo de datos:** un cliente tiene muchas membresías y muchas asistencias (`@OneToMany` en `Cliente`; `@ManyToOne` con `@JoinColumn(name = "cliente_id")` en `Membresia` y `Asistencia`).

---

## Solución de problemas

| Mensaje o síntoma | Causa | Solución |
|---|---|---|
| `Connection to localhost:5432 refused` | El servidor de PostgreSQL no está instalado o está apagado | Instala PostgreSQL o inícialo desde `services.msc` (servicio `postgresql-x64-...`) |
| `database "dbimperiumcross" does not exist` | Falta crear la base | Créala en pgAdmin (ver *Configuración de la base de datos*) |
| `password authentication failed for user "postgres"` | `DB_PASSWORD` no coincide con la clave de PostgreSQL | Define `DB_PASSWORD` con la clave correcta |
| `Could not resolve placeholder 'DB_PASSWORD'` | La variable de entorno no está definida | Defínela en la misma terminal donde ejecutas Maven |
| `Port 8080 was already in use` | Otra aplicación usa el puerto 8080 | Cierra la otra aplicación o cambia `server.port` en `application.properties` |
| `ERR_CONNECTION_REFUSED` en el navegador | La aplicación no está corriendo | Ejecuta `./mvnw spring-boot:run` y espera a ver `Started GimnasioApplication` |

Para comprobar si PostgreSQL escucha en el puerto 5432 (Windows):

```cmd
netstat -ano | findstr 5432
```

Debe aparecer una línea con `LISTENING`.

---

## Integrantes

- Aylas Valdez, Alexander Hernán
- Mendoza Laurente, Jose
- Paredes Huarcaya, Fabián Andrés
- Tito Valerio, Vanessa Mariana

**Docente:** Hermitaño Atencio, Bernardo Clímaco
**Curso:** Marcos de Desarrollo Web, UTP, Lima, Perú, 2026

---

## Próximos avances

- Control de acceso (login) con Spring Security.