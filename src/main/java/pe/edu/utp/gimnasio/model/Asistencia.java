package pe.edu.utp.gimnasio.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Registro de asistencia (check-in / check-out) de un cliente a un local.
 * Se usa para saber cuanto entrena cada cliente y para el control de aforo.
 */
@Entity
@Table(name = "asistencias")
public class Asistencia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "cliente_id", nullable = false)
    @JsonIgnoreProperties({"membresias", "asistencias"})
    private Cliente cliente;

    @NotNull
    private LocalDate fecha = LocalDate.now();

    @NotNull
    private LocalTime horaEntrada = LocalTime.now();

    /** Nulo mientras el cliente sigue entrenando; se completa al hacer check-out. */
    private LocalTime horaSalida;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Membresia.Gimnasio gimnasio;

    public Asistencia() {
    }

    // ---------- Logica de negocio ----------

    @Transient
    public boolean estaEnCurso() {
        return horaSalida == null;
    }

    /** Minutos entrenados; 0 si aun no ha marcado salida. */
    @Transient
    public long getMinutosEntrenados() {
        if (horaSalida == null) return 0;
        return Duration.between(horaEntrada, horaSalida).toMinutes();
    }

    // ---------- Getters / Setters ----------

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Cliente getCliente() { return cliente; }
    public void setCliente(Cliente cliente) { this.cliente = cliente; }

    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }

    public LocalTime getHoraEntrada() { return horaEntrada; }
    public void setHoraEntrada(LocalTime horaEntrada) { this.horaEntrada = horaEntrada; }

    public LocalTime getHoraSalida() { return horaSalida; }
    public void setHoraSalida(LocalTime horaSalida) { this.horaSalida = horaSalida; }

    public Membresia.Gimnasio getGimnasio() { return gimnasio; }
    public void setGimnasio(Membresia.Gimnasio gimnasio) { this.gimnasio = gimnasio; }
}
