package pe.edu.utp.gimnasio.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.edu.utp.gimnasio.model.Asistencia;
import pe.edu.utp.gimnasio.model.Cliente;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface AsistenciaRepository extends JpaRepository<Asistencia, Long> {

    List<Asistencia> findByClienteOrderByFechaDescHoraEntradaDesc(Cliente cliente);

    Optional<Asistencia> findByClienteAndFechaAndHoraSalidaIsNull(Cliente cliente, LocalDate fecha);

    long countByClienteAndFechaBetween(Cliente cliente, LocalDate desde, LocalDate hasta);

    List<Asistencia> findByFecha(LocalDate fecha);

    List<Asistencia> findByHoraSalidaIsNull();
}
