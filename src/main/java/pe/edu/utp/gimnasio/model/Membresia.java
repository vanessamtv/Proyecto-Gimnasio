package pe.edu.utp.gimnasio.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Membresia (plan de suscripcion) de un cliente en uno de los locales de Imperium Cross.
 * El precio se calcula segun el plan y el local (misma logica que se ve en
 * inscripciones.html/planes.html del sitio estatico).
 */
@Entity
@Table(name = "membresias")
public class Membresia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "cliente_id", nullable = false)
    @JsonIgnoreProperties({"membresias", "asistencias"})
    private Cliente cliente;

    @NotNull(message = "Selecciona un plan")
    @Enumerated(EnumType.STRING)
    private TipoPlan tipoPlan;

    @NotNull(message = "Selecciona un local")
    @Enumerated(EnumType.STRING)
    private Gimnasio gimnasio;

    @Column(nullable = false)
    private LocalDate fechaInicio;

    @Column(nullable = false)
    private LocalDate fechaFin;

    @Column(nullable = false, precision = 8, scale = 2)
    private BigDecimal precio;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoMembresia estado = EstadoMembresia.ACTIVA;

    public Membresia() {
    }

    // ---------- Enums de dominio ----------

    public enum TipoPlan { SMART, BLACK, FIT }

    public enum Gimnasio { CHOSICA }

    public enum EstadoMembresia { ACTIVA, VENCIDA, CANCELADA }

    // ---------- Logica de negocio ----------

    /** true si la fecha de hoy cae dentro del rango de vigencia y no fue cancelada. */
    @Transient
    public boolean estaVigente() {
        LocalDate hoy = LocalDate.now();
        return estado == EstadoMembresia.ACTIVA
                && !hoy.isBefore(fechaInicio)
                && !hoy.isAfter(fechaFin);
    }

    @Transient
    public long getDiasRestantes() {
        long dias = LocalDate.now().until(fechaFin).getDays();
        return Math.max(dias, 0);
    }

    // ---------- Getters / Setters ----------

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Cliente getCliente() { return cliente; }
    public void setCliente(Cliente cliente) { this.cliente = cliente; }

    public TipoPlan getTipoPlan() { return tipoPlan; }
    public void setTipoPlan(TipoPlan tipoPlan) { this.tipoPlan = tipoPlan; }

    public Gimnasio getGimnasio() { return gimnasio; }
    public void setGimnasio(Gimnasio gimnasio) { this.gimnasio = gimnasio; }

    public LocalDate getFechaInicio() { return fechaInicio; }
    public void setFechaInicio(LocalDate fechaInicio) { this.fechaInicio = fechaInicio; }

    public LocalDate getFechaFin() { return fechaFin; }
    public void setFechaFin(LocalDate fechaFin) { this.fechaFin = fechaFin; }

    public BigDecimal getPrecio() { return precio; }
    public void setPrecio(BigDecimal precio) { this.precio = precio; }

    public EstadoMembresia getEstado() { return estado; }
    public void setEstado(EstadoMembresia estado) { this.estado = estado; }
}
