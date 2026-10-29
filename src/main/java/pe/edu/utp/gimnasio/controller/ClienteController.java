package pe.edu.utp.gimnasio.controller;

import jakarta.validation.Valid;
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

import org.springframework.data.domain.Page;
import org.springframework.ui.Model;

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
        model.addAttribute("cliente", new ClienteDTO());
        return "clientes/form";
    }

    @GetMapping("/{id}/editar")
    public String editarForm(@PathVariable Long id, Model model) {
        model.addAttribute("cliente", aDTO(clienteService.buscarPorId(id)));
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

    
    @GetMapping
    public String listarClientes(
        @RequestParam(defaultValue = "0") int page,
        Model model) {

    Page<Cliente> resultado =
            clienteService.listarPaginado(page);

    model.addAttribute(
            "clientes", resultado.getContent()
    );

    model.addAttribute(
            "paginaActual", resultado.getNumber()
    );

    model.addAttribute(
            "totalPaginas", resultado.getTotalPages()
    );

    model.addAttribute(
            "totalRegistros", resultado.getTotalElements()
    );

    return "clientes/listar";
}


    @PostMapping("/guardar")
    public String guardar(@Valid @ModelAttribute("cliente") ClienteDTO dto, BindingResult binding,
                           RedirectAttributes redirectAttributes, Model model) {
        // Spring Validation: si el DTO tiene errores, se vuelve al formulario mostrando los mensajes
        if (binding.hasErrors()) {
            return "clientes/form";
        }
        try {
            Cliente cliente = aEntidad(dto);
            if (dto.getId() == null) {
                clienteService.registrar(cliente);
                redirectAttributes.addFlashAttribute("mensaje", "Cliente registrado correctamente.");
            } else {
                clienteService.actualizar(dto.getId(), cliente);
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

    // ---------- Conversion DTO <-> Entidad ----------

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
