package pe.edu.utp.gimnasio.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.utp.gimnasio.model.Cliente;
import pe.edu.utp.gimnasio.repository.ClienteRepository;

import java.util.List;

@Service
public class ClienteService {

    private final ClienteRepository clienteRepository;

    public ClienteService(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    public List<Cliente> listarActivos() {
        return clienteRepository.findByActivoTrue();
    }

    public List<Cliente> listarTodos() {
        return clienteRepository.findAll();
    }

    public Cliente buscarPorId(Long id) {
        return clienteRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("No existe el cliente con id " + id));
    }

    public List<Cliente> buscarPorNombre(String texto) {
        return clienteRepository.findByNombresContainingIgnoreCaseOrApellidosContainingIgnoreCase(texto, texto);
    }

    /**
     * Registra un cliente nuevo aplicando las reglas de negocio:
     * DNI y email deben ser unicos, y debe ser mayor de edad (>=16 anios,
     * politica del gimnasio para poder entrenar sin autorizacion de un tutor).
     */
    @Transactional
    public Cliente registrar(Cliente cliente) {
        if (clienteRepository.existsByDni(cliente.getDni())) {
            throw new IllegalArgumentException("Ya existe un cliente registrado con el DNI " + cliente.getDni());
        }
        if (clienteRepository.existsByEmail(cliente.getEmail())) {
            throw new IllegalArgumentException("Ya existe un cliente registrado con el email " + cliente.getEmail());
        }
        if (cliente.getEdad() < 16) {
            throw new IllegalArgumentException("El cliente debe tener al menos 16 anios para inscribirse");
        }
        cliente.setActivo(true);
        return clienteRepository.save(cliente);
    }

    @Transactional
    public Cliente actualizar(Long id, Cliente datos) {
        Cliente existente = buscarPorId(id);

        clienteRepository.findByDni(datos.getDni())
                .filter(c -> !c.getId().equals(id))
                .ifPresent(c -> { throw new IllegalArgumentException("El DNI ya pertenece a otro cliente"); });
        clienteRepository.findByEmail(datos.getEmail())
                .filter(c -> !c.getId().equals(id))
                .ifPresent(c -> { throw new IllegalArgumentException("El email ya pertenece a otro cliente"); });

        existente.setNombres(datos.getNombres());
        existente.setApellidos(datos.getApellidos());
        existente.setDni(datos.getDni());
        existente.setEmail(datos.getEmail());
        existente.setTelefono(datos.getTelefono());
        existente.setFechaNacimiento(datos.getFechaNacimiento());
        return clienteRepository.save(existente);
    }

    /** Baja logica: no se borra el registro para conservar el historial de membresias/asistencias. */
    @Transactional
    public void darDeBaja(Long id) {
        Cliente cliente = buscarPorId(id);
        cliente.setActivo(false);
        clienteRepository.save(cliente);
    }

    @Transactional
    public void reactivar(Long id) {
        Cliente cliente = buscarPorId(id);
        cliente.setActivo(true);
        clienteRepository.save(cliente);
    }
}
