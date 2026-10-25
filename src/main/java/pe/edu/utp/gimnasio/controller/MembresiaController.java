package pe.edu.utp.gimnasio.controller;

import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pe.edu.utp.gimnasio.dto.MembresiaDTO;
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
        MembresiaDTO dto = new MembresiaDTO();
        dto.setClienteId(clienteId);
        model.addAttribute("membresia", dto);
        cargarListas(model);
        return "membresias/form";
    }

    @PostMapping("/inscribir")
    public String inscribir(@Valid @ModelAttribute("membresia") MembresiaDTO dto, BindingResult binding,
                             RedirectAttributes redirectAttributes, Model model) {
        // Spring Validation: si faltan datos, se vuelve al formulario mostrando los mensajes
        if (binding.hasErrors()) {
            cargarListas(model);
            return "membresias/form";
        }

        Cliente cliente = clienteService.buscarPorId(dto.getClienteId());
        try {
            Membresia membresia = membresiaService.inscribir(cliente, dto.getPlan(), dto.getGimnasio());
            redirectAttributes.addFlashAttribute("mensaje",
                    "Membresia " + membresia.getTipoPlan() + " registrada para " + cliente.getNombreCompleto()
                            + " (S/ " + membresia.getPrecio() + ").");
        } catch (IllegalArgumentException ex) {
            model.addAttribute("error", ex.getMessage());
            cargarListas(model);
            return "membresias/form";
        }
        return "redirect:/clientes/" + dto.getClienteId();
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

    /** Datos de los combos del formulario (se necesitan tambien al volver con errores). */
    private void cargarListas(Model model) {
        model.addAttribute("clientes", clienteService.listarActivos());
        model.addAttribute("planes", Membresia.TipoPlan.values());
        model.addAttribute("gimnasios", Membresia.Gimnasio.values());
    }
}
