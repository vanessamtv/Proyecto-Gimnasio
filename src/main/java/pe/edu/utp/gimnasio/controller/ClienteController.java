package pe.edu.utp.gimnasio.controller;

import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pe.edu.utp.gimnasio.model.Cliente;
import pe.edu.utp.gimnasio.service.AsistenciaService;
import pe.edu.utp.gimnasio.service.ClienteService;
import pe.edu.utp.gimnasio.service.MembresiaService;

@Controller
@RequestMapping("/clientes")
public class ClienteController {

    private final ClienteService clienteService;
    private final MembresiaService membresiaService;
    private final AsistenciaService asistenciaService;

    public ClienteController(ClienteService clienteService, MembresiaService membresiaService, AsistenciaService asistenciaService) {
        this.clienteService = clienteService;
        this.membresiaService = membresiaService;
        this.asistenciaService = asistenciaService;
    }

    @GetMapping
    public String listar(@RequestParam(required = false) String q, Model model) {
        model.addAttribute("clientes", (q == null || q.isBlank())
                ? clienteService.listarActivos()
                : clienteService.buscarPorNombre(q));
        model.addAttribute("q", q);
        return "clientes/listar";
    }

    @GetMapping("/nuevo")
    public String nuevoForm(Model model) {
        model.addAttribute("cliente", new Cliente());
        return "clientes/form";
    }

    @GetMapping("/{id}/editar")
    public String editarForm(@PathVariable Long id, Model model) {
        model.addAttribute("cliente", clienteService.buscarPorId(id));
        return "clientes/form";
    }

    @GetMapping("/{id}")
    public String detalle(@PathVariable Long id, Model model) {
        Cliente cliente = clienteService.buscarPorId(id);
        model.addAttribute("cliente", cliente);
        model.addAttribute("membresias", membresiaService.listarPorCliente(cliente));
        model.addAttribute("membresiaVigente", membresiaService.membresiaActivaDe(cliente).orElse(null));
        model.addAttribute("asistencias", asistenciaService.listarPorCliente(cliente));
        model.addAttribute("asistenciasMes", asistenciaService.contarAsistenciasDelMes(cliente));
        return "clientes/detalle";
    }

    @PostMapping("/guardar")
    public String guardar(@Valid @ModelAttribute("cliente") Cliente cliente, BindingResult binding,
                           RedirectAttributes redirectAttributes, Model model) {
        if (binding.hasErrors()) {
            return "clientes/form";
        }
        try {
            if (cliente.getId() == null) {
                clienteService.registrar(cliente);
                redirectAttributes.addFlashAttribute("mensaje", "Cliente registrado correctamente.");
            } else {
                clienteService.actualizar(cliente.getId(), cliente);
                redirectAttributes.addFlashAttribute("mensaje", "Cliente actualizado correctamente.");
            }
        } catch (IllegalArgumentException ex) {
            model.addAttribute("error", ex.getMessage());
            return "clientes/form";
        }
        return "redirect:/clientes";
    }

    @PostMapping("/{id}/baja")
    public String darDeBaja(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        clienteService.darDeBaja(id);
        redirectAttributes.addFlashAttribute("mensaje", "Cliente dado de baja.");
        return "redirect:/clientes";
    }

    @PostMapping("/{id}/reactivar")
    public String reactivar(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        clienteService.reactivar(id);
        redirectAttributes.addFlashAttribute("mensaje", "Cliente reactivado.");
        return "redirect:/clientes";
    }
}
