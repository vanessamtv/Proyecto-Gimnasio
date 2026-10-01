package pe.edu.utp.gimnasio.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pe.edu.utp.gimnasio.model.Cliente;
import pe.edu.utp.gimnasio.model.Membresia;
import pe.edu.utp.gimnasio.service.ClienteService;
import pe.edu.utp.gimnasio.service.MembresiaService;

@Controller
@RequestMapping("/membresias")
public class MembresiaController {

    private final MembresiaService membresiaService;
    private final ClienteService clienteService;

    public MembresiaController(MembresiaService membresiaService, ClienteService clienteService) {
        this.membresiaService = membresiaService;
        this.clienteService = clienteService;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("membresias", membresiaService.listarTodas());
        return "membresias/listar";
    }

    @GetMapping("/nueva")
    public String nuevaForm(@RequestParam(required = false) Long clienteId, Model model) {
        model.addAttribute("clientes", clienteService.listarActivos());
        model.addAttribute("planes", Membresia.TipoPlan.values());
        model.addAttribute("gimnasios", Membresia.Gimnasio.values());
        model.addAttribute("clienteId", clienteId);
        return "membresias/form";
    }

    @PostMapping("/inscribir")
    public String inscribir(@RequestParam Long clienteId,
                             @RequestParam Membresia.TipoPlan plan,
                             @RequestParam Membresia.Gimnasio gimnasio,
                             RedirectAttributes redirectAttributes, Model model) {
        Cliente cliente = clienteService.buscarPorId(clienteId);
        try {
            Membresia membresia = membresiaService.inscribir(cliente, plan, gimnasio);
            redirectAttributes.addFlashAttribute("mensaje",
                    "Membresia " + membresia.getTipoPlan() + " registrada para " + cliente.getNombreCompleto()
                            + " (S/ " + membresia.getPrecio() + ").");
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/clientes/" + clienteId;
    }

    @PostMapping("/{id}/cancelar")
    public String cancelar(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        Membresia membresia = membresiaService.buscarPorId(id);
        Long clienteId = membresia.getCliente().getId();
        membresiaService.cancelar(id);
        redirectAttributes.addFlashAttribute("mensaje", "Membresia cancelada.");
        return "redirect:/clientes/" + clienteId;
    }

    /** Endpoint auxiliar usado por el formulario (AJAX opcional) para mostrar el precio antes de guardar. */
    @GetMapping("/precio")
    @ResponseBody
    public String precio(@RequestParam Membresia.TipoPlan plan, @RequestParam Membresia.Gimnasio gimnasio) {
        return membresiaService.calcularPrecio(plan, gimnasio).toString();
    }
}
