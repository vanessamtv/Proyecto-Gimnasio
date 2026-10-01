package pe.edu.utp.gimnasio.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;

/**
 * Cliente inscrito en Imperium Cross. Un cliente puede tener varias membresias
 * a lo largo del tiempo (historial) y varias asistencias registradas.
 */
@Entity
@Table(name = "clientes", uniqueConstraints = {
        @UniqueConstraint(columnNames = "dni"),
        @UniqueConstraint(columnNames = "email")
})
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 80)
    private String nombres;

    @NotBlank(message = "El apellido es obligatorio")
    @Size(max = 80)
    private String apellidos;

    @NotBlank(message = "El DNI es obligatorio")
    @Pattern(regexp = "\\d{8}", message = "El DNI debe tener 8 digitos")
    @Column(length = 8)
    private String dni;

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El email no tiene un formato valido")
    private String email;

    @NotBlank(message = "El telefono es obligatorio")
    @Pattern(regexp = "\\d{9}", message = "El telefono debe tener 9 digitos")
    @Column(length = 9)
    private String telefono;

    @NotNull(message = "La fecha de nacimiento es obligatoria")
    @Past(message = "La fecha de nacimiento debe ser anterior a hoy")
    private LocalDate fechaNacimiento;

    @Column(nullable = false, updatable = false)
    private LocalDate fechaRegistro = LocalDate.now();

    /** Baja logica: permite "eliminar" un cliente sin borrar su historial. */
    @Column(nullable = false)
    private boolean activo = true;

    @JsonIgnore
    @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Membresia> membresias = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Asistencia> asistencias = new ArrayList<>();

    public Cliente() {
    }

    // ---------- Logica de negocio propia de la entidad ----------

    /** Edad calculada a partir de la fecha de nacimiento (regla de negocio simple). */
    @Transient
    public int getEdad() {
        if (fechaNacimiento == null) return 0;
        return Period.between(fechaNacimiento, LocalDate.now()).getYears();
    }

    public String getNombreCompleto() {
        return nombres + " " + apellidos;
    }

    // ---------- Getters / Setters ----------

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombres() { return nombres; }
    public void setNombres(String nombres) { this.nombres = nombres; }

    public String getApellidos() { return apellidos; }
    public void setApellidos(String apellidos) { this.apellidos = apellidos; }

    public String getDni() { return dni; }
    public void setDni(String dni) { this.dni = dni; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public LocalDate getFechaNacimiento() { return fechaNacimiento; }
    public void setFechaNacimiento(LocalDate fechaNacimiento) { this.fechaNacimiento = fechaNacimiento; }

    public LocalDate getFechaRegistro() { return fechaRegistro; }
    public void setFechaRegistro(LocalDate fechaRegistro) { this.fechaRegistro = fechaRegistro; }

    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }

    public List<Membresia> getMembresias() { return membresias; }
    public void setMembresias(List<Membresia> membresias) { this.membresias = membresias; }

    public List<Asistencia> getAsistencias() { return asistencias; }
    public void setAsistencias(List<Asistencia> asistencias) { this.asistencias = asistencias; }
}
