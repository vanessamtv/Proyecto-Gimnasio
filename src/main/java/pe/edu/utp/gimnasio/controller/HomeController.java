package pe.edu.utp.gimnasio.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import pe.edu.utp.gimnasio.model.Membresia;
import pe.edu.utp.gimnasio.repository.AsistenciaRepository;
import pe.edu.utp.gimnasio.repository.ClienteRepository;
import pe.edu.utp.gimnasio.repository.MembresiaRepository;

@Controller
public class HomeController {

    private final ClienteRepository clienteRepository;
    private final MembresiaRepository membresiaRepository;
    private final AsistenciaRepository asistenciaRepository;

    public HomeController(ClienteRepository clienteRepository, MembresiaRepository membresiaRepository,
                           AsistenciaRepository asistenciaRepository) {
        this.clienteRepository = clienteRepository;
        this.membresiaRepository = membresiaRepository;
        this.asistenciaRepository = asistenciaRepository;
    }

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("totalClientes", clienteRepository.findByActivoTrue().size());
        model.addAttribute("membresiasActivas", membresiaRepository.findByEstado(Membresia.EstadoMembresia.ACTIVA).size());
        model.addAttribute("asistenciasHoy", asistenciaRepository.findByFecha(java.time.LocalDate.now()).size());
        model.addAttribute("enCurso", asistenciaRepository.findByHoraSalidaIsNull().size());
        return "index";
    }
}
