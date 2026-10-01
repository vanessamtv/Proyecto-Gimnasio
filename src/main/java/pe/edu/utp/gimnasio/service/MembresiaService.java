package pe.edu.utp.gimnasio.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pe.edu.utp.gimnasio.model.Cliente;
import pe.edu.utp.gimnasio.model.Membresia;
import pe.edu.utp.gimnasio.repository.MembresiaRepository;

@Service
@Transactional(readOnly = true)
public class MembresiaService {

    private static final int DURACION_DIAS = 30;

    private static final Map<Membresia.Gimnasio,
            Map<Membresia.TipoPlan, BigDecimal>> PRECIOS = Map.of(
        Membresia.Gimnasio.CHOSICA, Map.of(
            Membresia.TipoPlan.SMART, new BigDecimal("109.90"),
            Membresia.TipoPlan.BLACK, new BigDecimal("59.90"),
            Membresia.TipoPlan.FIT, new BigDecimal("59.90")
        )
    );

    private final MembresiaRepository membresiaRepository;

    public MembresiaService(MembresiaRepository membresiaRepository) {
        this.membresiaRepository = membresiaRepository;
    }

    public List<Membresia> listarPorCliente(Cliente cliente) {
        validarCliente(cliente);
        return membresiaRepository.findByClienteOrderByFechaInicioDesc(cliente);
    }

    public List<Membresia> listarTodas() {
        return membresiaRepository.findAll();
    }

    public Membresia buscarPorId(Long id) {
        if (id == null) {
            throw new IllegalArgumentException(
                "Debes indicar el ID de la membresía."
            );
        }

        return membresiaRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException(
                "No existe una membresía con ID " + id + "."
            ));
    }

    public Optional<Membresia> membresiaActivaDe(Cliente cliente) {
        validarCliente(cliente);

        return membresiaRepository
            .findFirstByClienteAndEstadoOrderByFechaFinDesc(
                cliente,
                Membresia.EstadoMembresia.ACTIVA
            )
            .filter(m -> m != null && m.estaVigente());
    }

    @Transactional
    public Membresia inscribir(
            Cliente cliente,
            Membresia.TipoPlan plan,
            Membresia.Gimnasio gimnasio) {

        validarCliente(cliente);
        BigDecimal precio = calcularPrecio(plan, gimnasio);

        // Si existe una membresía vigente, comienza después de su vencimiento.
        LocalDate fechaInicio = membresiaActivaDe(cliente)
            .map(m -> m.getFechaFin().plusDays(1))
            .orElse(LocalDate.now());

        Membresia membresia = new Membresia();
        membresia.setCliente(cliente);
        membresia.setTipoPlan(plan);
        membresia.setGimnasio(gimnasio);
        membresia.setFechaInicio(fechaInicio);
        membresia.setFechaFin(fechaInicio.plusDays(DURACION_DIAS));
        membresia.setPrecio(precio);
        membresia.setEstado(Membresia.EstadoMembresia.ACTIVA);

        return membresiaRepository.save(membresia);
    }

    public BigDecimal calcularPrecio(
            Membresia.TipoPlan plan,
            Membresia.Gimnasio gimnasio) {

        if (plan == null || gimnasio == null) {
            throw new IllegalArgumentException(
                "Debes seleccionar un plan y un local."
            );
        }

        Map<Membresia.TipoPlan, BigDecimal> preciosLocal = PRECIOS.get(gimnasio);

        if (preciosLocal == null) {
            throw new IllegalArgumentException(
                "El local seleccionado no tiene precios configurados."
            );
        }

        BigDecimal precio = preciosLocal.get(plan);

        if (precio == null) {
            throw new IllegalArgumentException(
                "El plan seleccionado no está disponible en este local."
            );
        }

        return precio;
    }

    @Transactional
    public void cancelar(Long id) {
        Membresia membresia = buscarPorId(id);
        membresia.setEstado(Membresia.EstadoMembresia.CANCELADA);
        membresiaRepository.save(membresia);
    }

    // Actualiza las membresías vencidas diariamente a la 1:00 a. m.
    @Scheduled(cron = "0 0 1 * * *")
    @Transactional
    public void actualizarMembresiasVencidas() {
        List<Membresia> vencidas =
            membresiaRepository.findByEstadoAndFechaFinBefore(
                Membresia.EstadoMembresia.ACTIVA,
                LocalDate.now()
            );

        if (vencidas.isEmpty()) {
            return;
        }

        vencidas.forEach(m ->
            m.setEstado(Membresia.EstadoMembresia.VENCIDA)
        );

        membresiaRepository.saveAll(vencidas);
    }

    private void validarCliente(Cliente cliente) {
        if (cliente == null || cliente.getId() == null) {
            throw new IllegalArgumentException(
                "Debes seleccionar un cliente registrado."
            );
        }
    }
}