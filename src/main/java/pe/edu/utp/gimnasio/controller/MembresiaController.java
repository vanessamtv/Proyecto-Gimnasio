
package pe.edu.utp.gimnasio.controller;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.validation.Valid;

import java.util.List;

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

    private static final int REGISTROS_POR_PAGINA = 50;

    private final MembresiaService membresiaService;
    private final ClienteService clienteService;

    @PersistenceContext
    private EntityManager entityManager;

    public MembresiaController(
            MembresiaService membresiaService,
            ClienteService clienteService) {

        this.membresiaService = membresiaService;
        this.clienteService = clienteService;
    }

    // ==========================================
    // LISTAR MEMBRESIAS CON PAGINACION
    // ==========================================

    @GetMapping
    public String listar(
            @RequestParam(defaultValue = "0") int page,
            Model model) {

        // Contar todas las membresias
        Long totalRegistros = entityManager
                .createQuery(
                        "SELECT COUNT(m) FROM Membresia m",
                        Long.class)
                .getSingleResult();

        // Calcular cantidad de paginas
        int totalPaginas = (int) Math.ceil(
                (double) totalRegistros / REGISTROS_POR_PAGINA
        );

        // Evitar paginas negativas o inexistentes
        int paginaActual = Math.max(0, page);

        if (totalPaginas > 0) {
            paginaActual = Math.min(
                    paginaActual,
                    totalPaginas - 1
            );
        } else {
            paginaActual = 0;
        }

        // Consultar solo 50 membresias
        List<Membresia> membresias = entityManager
                .createQuery(
                        """
                        SELECT m
                        FROM Membresia m
                        LEFT JOIN FETCH m.cliente
                        ORDER BY m.id ASC
                        """,
                        Membresia.class)
                .setFirstResult(
                        paginaActual * REGISTROS_POR_PAGINA)
                .setMaxResults(REGISTROS_POR_PAGINA)
                .getResultList();

        // Enviar informacion a Thymeleaf
        model.addAttribute("membresias", membresias);

        model.addAttribute(
                "paginaActual", paginaActual);

        model.addAttribute(
                "totalPaginas", totalPaginas);

        model.addAttribute(
                "totalRegistros", totalRegistros);

        model.addAttribute(
                "registrosPorPagina",
                REGISTROS_POR_PAGINA);

        return "membresias/listar";
    }

    // ==========================================
    // FORMULARIO NUEVA MEMBRESIA
    // ==========================================

    @GetMapping("/nueva")
    public String nuevaForm(
            @RequestParam(required = false) Long clienteId,
            Model model) {

        MembresiaDTO dto = new MembresiaDTO();
        dto.setClienteId(clienteId);

        model.addAttribute("membresia", dto);

        cargarListas(model);

        return "membresias/form";
    }

    // ==========================================
    // REGISTRAR MEMBRESIA
    // ==========================================

    @PostMapping("/inscribir")
    public String inscribir(
            @Valid @ModelAttribute("membresia")
            MembresiaDTO dto,
            BindingResult binding,
            RedirectAttributes redirectAttributes,
            Model model) {

        if (binding.hasErrors()) {
            cargarListas(model);
            return "membresias/form";
        }

        Cliente cliente =
                clienteService.buscarPorId(dto.getClienteId());

        try {

            Membresia membresia =
                    membresiaService.inscribir(
                            cliente,
                            dto.getPlan(),
                            dto.getGimnasio()
                    );

            redirectAttributes.addFlashAttribute(
                    "mensaje",
                    "Membresía "
                            + membresia.getTipoPlan()
                            + " registrada para "
                            + cliente.getNombreCompleto()
                            + " (S/ "
                            + membresia.getPrecio()
                            + ")."
            );

        } catch (IllegalArgumentException ex) {

            model.addAttribute("error", ex.getMessage());
            cargarListas(model);

            return "membresias/form";
        }

        return "redirect:/clientes/" + dto.getClienteId();
    }

    // ==========================================
    // CANCELAR MEMBRESIA
    // ==========================================

    @PostMapping("/{id}/cancelar")
    public String cancelar(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {

        Membresia membresia =
                membresiaService.buscarPorId(id);

        Long clienteId = membresia.getCliente().getId();

        membresiaService.cancelar(id);

        redirectAttributes.addFlashAttribute(
                "mensaje",
                "Membresía cancelada."
        );

        return "redirect:/clientes/" + clienteId;
    }

    // ==========================================
    // CONSULTAR PRECIO
    // ==========================================

    @GetMapping("/precio")
    @ResponseBody
    public String precio(
            @RequestParam Membresia.TipoPlan plan,
            @RequestParam Membresia.Gimnasio gimnasio) {

        return membresiaService
                .calcularPrecio(plan, gimnasio)
                .toString();
    }

    // ==========================================
    // CARGAR COMBOS
    // ==========================================

    private void cargarListas(Model model) {

        model.addAttribute(
                "clientes",
                clienteService.listarActivos());

        model.addAttribute(
                "planes",
                Membresia.TipoPlan.values());

        model.addAttribute(
                "gimnasios",
                Membresia.Gimnasio.values());
    }
}
