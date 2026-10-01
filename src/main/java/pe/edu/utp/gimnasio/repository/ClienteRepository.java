package pe.edu.utp.gimnasio.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.edu.utp.gimnasio.model.Cliente;

import java.util.List;
import java.util.Optional;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    Optional<Cliente> findByDni(String dni);

    Optional<Cliente> findByEmail(String email);

    boolean existsByDni(String dni);

    boolean existsByEmail(String email);

    List<Cliente> findByActivoTrue();

    List<Cliente> findByNombresContainingIgnoreCaseOrApellidosContainingIgnoreCase(String nombres, String apellidos);
}
