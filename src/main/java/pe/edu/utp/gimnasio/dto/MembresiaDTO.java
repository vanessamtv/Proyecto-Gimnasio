package pe.edu.utp.gimnasio.dto;

import jakarta.validation.constraints.NotNull;
import pe.edu.utp.gimnasio.model.Membresia;

/**
 * DTO del formulario de inscripcion de membresias.
 * Spring Validation comprueba que el usuario haya elegido cliente, plan y local.
 */
public class MembresiaDTO {

    @NotNull(message = "Selecciona un cliente")
    private Long clienteId;

    @NotNull(message = "Selecciona un plan")
    private Membresia.TipoPlan plan;

    @NotNull(message = "Selecciona un local")
    private Membresia.Gimnasio gimnasio;

    public MembresiaDTO() {
    }

    public Long getClienteId() { return clienteId; }
    public void setClienteId(Long clienteId) { this.clienteId = clienteId; }

    public Membresia.TipoPlan getPlan() { return plan; }
    public void setPlan(Membresia.TipoPlan plan) { this.plan = plan; }

    public Membresia.Gimnasio getGimnasio() { return gimnasio; }
    public void setGimnasio(Membresia.Gimnasio gimnasio) { this.gimnasio = gimnasio; }
}
