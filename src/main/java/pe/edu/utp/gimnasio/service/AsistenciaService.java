package pe.edu.utp.gimnasio.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.utp.gimnasio.model.Asistencia;
import pe.edu.utp.gimnasio.model.Cliente;
import pe.edu.utp.gimnasio.model.Membresia;
import pe.edu.utp.gimnasio.repository.AsistenciaRepository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
public class AsistenciaService {

    private final AsistenciaRepository asistenciaRepository;
    private final MembresiaService membresiaService;

    public AsistenciaService(AsistenciaRepository asistenciaRepository, MembresiaService membresiaService) {
        this.asistenciaRepository = asistenciaRepository;
        this.membresiaService = membresiaService;
    }

    public List<Asistencia> listarPorCliente(Cliente cliente) {
        return asistenciaRepository.findByClienteOrderByFechaDescHoraEntradaDesc(cliente);
    }

    public List<Asistencia> listarDeHoy() {
        return asistenciaRepository.findByFecha(LocalDate.now());
    }

    public List<Asistencia> listarEnCurso() {
        return asistenciaRepository.findByHoraSalidaIsNull();
    }

    /**
     * Marca el ingreso de un cliente a un local. Reglas de negocio:
     *  - El cliente debe tener una membresia vigente (no se deja entrenar sin plan activo).
     *  - No puede registrar dos entradas el mismo dia sin haber marcado salida antes.
     */
    @Transactional
    public Asistencia registrarEntrada(Cliente cliente, Membresia.Gimnasio gimnasio) {
        boolean tieneMembresiaVigente = membresiaService.membresiaActivaDe(cliente).isPresent();
        if (!tieneMembresiaVigente) {
            throw new IllegalStateException("El cliente no tiene una membresia vigente, no puede registrar asistencia");
        }

        asistenciaRepository.findByClienteAndFechaAndHoraSalidaIsNull(cliente, LocalDate.now())
                .ifPresent(a -> { throw new IllegalStateException("El cliente ya tiene una entrada sin marcar salida hoy"); });

        Asistencia asistencia = new Asistencia();
        asistencia.setCliente(cliente);
        asistencia.setFecha(LocalDate.now());
        asistencia.setHoraEntrada(LocalTime.now());
        asistencia.setGimnasio(gimnasio);
        return asistenciaRepository.save(asistencia);
    }

    /** Marca la salida de la asistencia del dia que quedo abierta. */
    @Transactional
    public Asistencia registrarSalida(Cliente cliente) {
        Asistencia asistencia = asistenciaRepository.findByClienteAndFechaAndHoraSalidaIsNull(cliente, LocalDate.now())
                .orElseThrow(() -> new IllegalStateException("El cliente no tiene una entrada abierta hoy"));
        asistencia.setHoraSalida(LocalTime.now());
        return asistenciaRepository.save(asistencia);
    }

    /** Cuenta cuantas veces asistio el cliente en el mes actual (para reportes/fidelizacion). */
    public long contarAsistenciasDelMes(Cliente cliente) {
        LocalDate hoy = LocalDate.now();
        LocalDate primerDia = hoy.withDayOfMonth(1);
        LocalDate ultimoDia = hoy.withDayOfMonth(hoy.lengthOfMonth());
        return asistenciaRepository.countByClienteAndFechaBetween(cliente, primerDia, ultimoDia);
    }
}
