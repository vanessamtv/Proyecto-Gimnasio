package pe.edu.utp.gimnasio.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pe.edu.utp.gimnasio.model.Cliente;
import pe.edu.utp.gimnasio.model.Membresia;
import pe.edu.utp.gimnasio.service.AsistenciaService;
import pe.edu.utp.gimnasio.service.ClienteService;

@Controller
@RequestMapping("/asistencias")
public class AsistenciaController {

    private final AsistenciaService asistenciaService;
    private final ClienteService clienteService;

    public AsistenciaController(AsistenciaService asistenciaService, ClienteService clienteService) {
        this.asistenciaService = asistenciaService;
        this.clienteService = clienteService;
    }

    /** Panel de control de aforo: quien esta entrenando ahora mismo. */
    @GetMapping
    public String listar(Model model) {
        model.addAttribute("enCurso", asistenciaService.listarEnCurso());
        model.addAttribute("hoy", asistenciaService.listarDeHoy());
        model.addAttribute("clientes", clienteService.listarActivos());
        model.addAttribute("gimnasios", Membresia.Gimnasio.values());
        return "asistencias/listar";
    }

    @PostMapping("/entrada")
    public String registrarEntrada(@RequestParam Long clienteId, @RequestParam Membresia.Gimnasio gimnasio,
                                    RedirectAttributes redirectAttributes) {
        Cliente cliente = clienteService.buscarPorId(clienteId);
        try {
            asistenciaService.registrarEntrada(cliente, gimnasio);
            redirectAttributes.addFlashAttribute("mensaje", "Entrada registrada para " + cliente.getNombreCompleto() + ".");
        } catch (IllegalStateException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/asistencias";
    }

    @PostMapping("/salida")
    public String registrarSalida(@RequestParam Long clienteId, RedirectAttributes redirectAttributes) {
        Cliente cliente = clienteService.buscarPorId(clienteId);
        try {
            asistenciaService.registrarSalida(cliente);
            redirectAttributes.addFlashAttribute("mensaje", "Salida registrada para " + cliente.getNombreCompleto() + ".");
        } catch (IllegalStateException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/asistencias";
    }
}
