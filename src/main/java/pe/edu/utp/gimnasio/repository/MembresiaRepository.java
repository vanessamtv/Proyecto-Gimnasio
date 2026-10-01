package pe.edu.utp.gimnasio.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.edu.utp.gimnasio.model.Cliente;
import pe.edu.utp.gimnasio.model.Membresia;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface MembresiaRepository extends JpaRepository<Membresia, Long> {

    List<Membresia> findByClienteOrderByFechaInicioDesc(Cliente cliente);

    Optional<Membresia> findFirstByClienteAndEstadoOrderByFechaFinDesc(Cliente cliente, Membresia.EstadoMembresia estado);

    List<Membresia> findByEstadoAndFechaFinBefore(Membresia.EstadoMembresia estado, LocalDate fecha);

    List<Membresia> findByEstado(Membresia.EstadoMembresia estado);
}
