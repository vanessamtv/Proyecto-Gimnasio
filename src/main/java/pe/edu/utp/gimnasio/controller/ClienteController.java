
package pe.edu.utp.gimnasio.controller;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.validation.Valid;

import java.util.List;
import java.util.Locale;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import pe.edu.utp.gimnasio.dto.ClienteDTO;
import pe.edu.utp.gimnasio.model.Cliente;
import pe.edu.utp.gimnasio.service.AsistenciaService;
import pe.edu.utp.gimnasio.service.ClienteService;
import pe.edu.utp.gimnasio.service.MembresiaService;

@Controller
@RequestMapping("/clientes")
public class ClienteController {

    private static final int REGISTROS_POR_PAGINA = 50;

    private final ClienteService clienteService;
    private final MembresiaService membresiaService;
    private final AsistenciaService asistenciaService;

    @PersistenceContext
    private EntityManager entityManager;

    public ClienteController(
            ClienteService clienteService,
            MembresiaService membresiaService,
            AsistenciaService asistenciaService) {

        this.clienteService = clienteService;
        this.membresiaService = membresiaService;
        this.asistenciaService = asistenciaService;
    }

    // ==========================================
    // LISTAR, BUSCAR Y PAGINAR CLIENTES
    // ==========================================

    @GetMapping
    public String listar(
            @RequestParam(required = false) String q,
            @RequestParam(defaultValue = "0") int page,
            Model model) {

        boolean buscando = q != null && !q.isBlank();

        String filtro;

        if (buscando) {
            filtro = """
                WHERE LOWER(c.nombres) LIKE :busqueda
                   OR LOWER(c.apellidos) LIKE :busqueda
                   OR LOWER(CONCAT(CONCAT(c.nombres, ' '),
                                    c.apellidos)) LIKE :busqueda
                """;
        } else {
            filtro = "WHERE c.activo = true";
        }

        // CONTAR REGISTROS
        TypedQuery<Long> consultaConteo =
                entityManager.createQuery(
                        "SELECT COUNT(c) FROM Cliente c " + filtro,
                        Long.class
                );

        if (buscando) {
            consultaConteo.setParameter(
                    "busqueda",
                    "%" + q.trim().toLowerCase(Locale.ROOT) + "%"
            );
        }

        long totalRegistros =
                consultaConteo.getSingleResult();

        // CALCULAR TOTAL DE PAGINAS
        int totalPaginas = (int) (
                (totalRegistros + REGISTROS_POR_PAGINA - 1)
                / REGISTROS_POR_PAGINA
        );

        int paginaActual = Math.max(0, page);

        if (totalPaginas > 0) {
            paginaActual = Math.min(
                    paginaActual,
                    totalPaginas - 1
            );
        } else {
            paginaActual = 0;
        }

        // OBTENER SOLO 50 CLIENTES
        TypedQuery<Cliente> consultaClientes =
                entityManager.createQuery(
                        "SELECT c FROM Cliente c "
                        + filtro
                        + " ORDER BY c.id ASC",
                        Cliente.class
                );

        if (buscando) {
            consultaClientes.setParameter(
                    "busqueda",
                    "%" + q.trim().toLowerCase(Locale.ROOT) + "%"
            );
        }

        List<Cliente> clientes = consultaClientes
                .setFirstResult(
                        paginaActual * REGISTROS_POR_PAGINA
                )
                .setMaxResults(REGISTROS_POR_PAGINA)
                .getResultList();

        // ENVIAR DATOS A THYMELEAF
        model.addAttribute("clientes", clientes);
        model.addAttribute("q", q);

        model.addAttribute(
                "paginaActual", paginaActual
        );

        model.addAttribute(
                "totalPaginas", totalPaginas
        );

        model.addAttribute(
                "totalRegistros", totalRegistros
        );

        model.addAttribute(
                "registrosPorPagina",
                REGISTROS_POR_PAGINA
        );

        return "clientes/listar";
    }

    // ==========================================
    // FORMULARIO NUEVO CLIENTE
    // ==========================================

    @GetMapping("/nuevo")
    public String nuevoForm(Model model) {

        model.addAttribute(
                "cliente",
                new ClienteDTO()
        );

        return "clientes/form";
    }

    // ==========================================
    // FORMULARIO EDITAR CLIENTE
    // ==========================================

    @GetMapping("/{id}/editar")
    public String editarForm(
            @PathVariable Long id,
            Model model) {

        model.addAttribute(
                "cliente",
                aDTO(clienteService.buscarPorId(id))
        );

        return "clientes/form";
    }

    // ==========================================
    // DETALLE DE CLIENTE
    // ==========================================

    @GetMapping("/{id}")
    public String detalle(
            @PathVariable Long id,
            Model model) {

        Cliente cliente =
                clienteService.buscarPorId(id);

        model.addAttribute(
                "cliente", cliente
        );

        model.addAttribute(
                "membresias",
                membresiaService.listarPorCliente(cliente)
        );

        model.addAttribute(
                "membresiaVigente",
                membresiaService
                    .membresiaActivaDe(cliente)
                    .orElse(null)
        );

        model.addAttribute(
                "asistencias",
                asistenciaService.listarPorCliente(cliente)
        );

        model.addAttribute(
                "asistenciasMes",
                asistenciaService
                    .contarAsistenciasDelMes(cliente)
        );

        return "clientes/detalle";
    }

    // ==========================================
    // GUARDAR O ACTUALIZAR CLIENTE
    // ==========================================

    @PostMapping("/guardar")
    public String guardar(
            @Valid @ModelAttribute("cliente") ClienteDTO dto,
            BindingResult binding,
            RedirectAttributes redirectAttributes,
            Model model) {

        if (binding.hasErrors()) {
            return "clientes/form";
        }

        try {

            Cliente cliente = aEntidad(dto);

            if (dto.getId() == null) {

                clienteService.registrar(cliente);

                redirectAttributes.addFlashAttribute(
                        "mensaje",
                        "Cliente registrado correctamente."
                );

            } else {

                clienteService.actualizar(
                        dto.getId(),
                        cliente
                );

                redirectAttributes.addFlashAttribute(
                        "mensaje",
                        "Cliente actualizado correctamente."
                );
            }

        } catch (IllegalArgumentException ex) {

            model.addAttribute(
                    "error",
                    ex.getMessage()
            );

            return "clientes/form";
        }

        return "redirect:/clientes";
    }

    // ==========================================
    // DAR DE BAJA
    // ==========================================

    @PostMapping("/{id}/baja")
    public String darDeBaja(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {

        clienteService.darDeBaja(id);

        redirectAttributes.addFlashAttribute(
                "mensaje",
                "Cliente dado de baja."
        );

        return "redirect:/clientes";
    }

    // ==========================================
    // REACTIVAR
    // ==========================================

    @PostMapping("/{id}/reactivar")
    public String reactivar(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {

        clienteService.reactivar(id);

        redirectAttributes.addFlashAttribute(
                "mensaje",
                "Cliente reactivado."
        );

        return "redirect:/clientes";
    }

    // ==========================================
    // CONVERTIR CLIENTE A DTO
    // ==========================================

    private ClienteDTO aDTO(Cliente c) {

        ClienteDTO dto = new ClienteDTO();

        dto.setId(c.getId());
        dto.setNombres(c.getNombres());
        dto.setApellidos(c.getApellidos());
        dto.setDni(c.getDni());
        dto.setEmail(c.getEmail());
        dto.setTelefono(c.getTelefono());
        dto.setFechaNacimiento(c.getFechaNacimiento());

        return dto;
    }

    // ==========================================
    // CONVERTIR DTO A CLIENTE
    // ==========================================

    private Cliente aEntidad(ClienteDTO dto) {

        Cliente c = new Cliente();

        c.setNombres(dto.getNombres());
        c.setApellidos(dto.getApellidos());
        c.setDni(dto.getDni());
        c.setEmail(dto.getEmail());
        c.setTelefono(dto.getTelefono());
        c.setFechaNacimiento(dto.getFechaNacimiento());

        return c;
    }
}
