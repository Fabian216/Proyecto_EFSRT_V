package grupo08.proyecto_efsrtv.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lowagie.text.pdf.PdfPTable;
import com.opencsv.CSVWriter;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import grupo08.proyecto_efsrtv.dto.UsuarioDetDto;
import grupo08.proyecto_efsrtv.dto.UsuarioDto;
import grupo08.proyecto_efsrtv.models.Usuario;
import grupo08.proyecto_efsrtv.repositories.IUsuarioRepository;
import grupo08.proyecto_efsrtv.service.IUsuarioService;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Optional;

//@RestController
@Controller
@RequestMapping("/manage")
public class UsuarioController {

    private final Logger LOGGER = LoggerFactory.getLogger(UsuarioController.class);

    @Autowired
    private IUsuarioService usuarioService;

    @Autowired
    private IUsuarioRepository usuarioRepository;

    // Página de inicio de sesión
    @GetMapping("/login")
    public String login() {
        return "login";
    }

    // Página de acceso restringido
    @GetMapping("/restricted")
    public String restricted() {
        return "restricted";
    }

    //listado de usuari
    @GetMapping("/start")
    public String start(Model model) {
        String userName = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<Usuario> loggedUser = usuarioRepository.findByUserName(userName);

        if (loggedUser.isPresent()) {
            String role = loggedUser.get().getUserRole();
            // Imprimir el rol del usuario en la consola para depuración
            System.out.println("Rol del usuario autenticado: " + role); // Verificar el rol en la consola
            if ("ADMIN".equals(role) || "OPERATOR".equals(role)) {
                try {
                    List<UsuarioDto> users = usuarioService.getAllUsers();
                    model.addAttribute("users", users);
                    model.addAttribute("error", null);
                } catch (Exception e) {
                    e.printStackTrace();
                    model.addAttribute("error", "Ocurrió un error al obtener los datos.");
                }
                return "manage";  // Vista para mostrar la lista de usuarios
            } else {
                return "redirect:/manage/restricted";  // Redirigir si el usuario no tiene acceso
            }
        } else {
            return "redirect:/manage/restricted";  // Redirigir si no se encuentra el usuario
        }
    }
    @GetMapping("/export")
    public void exportUsers(@RequestParam String format, HttpServletResponse response) throws IOException {
        List<UsuarioDto> users = usuarioService.getAllUsers();

        response.setCharacterEncoding("UTF-8");
        response.setHeader("Content-Disposition", "attachment; filename=Usuarios." + format);

        switch (format) {
            case "csv":
                response.setContentType("text/csv");
                exportACSV(users, response.getWriter());
                break;
            case "json":
                response.setContentType("application/json");
                exportAJSON(users, response.getWriter());
                break;
            case "txt":
                response.setContentType("text/plain");
                exportarATXT(users, response.getWriter());
                break;
            case "pdf":
                response.setContentType("application/pdf");
                exportarAPdf(users, response.getOutputStream());
                break;
            default:
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Formato no soportado");
        }
    }

    private void exportarAPdf(List<UsuarioDto> users, ServletOutputStream outputStream) throws IOException {
        try {
            com.lowagie.text.Document document = new com.lowagie.text.Document();
            com.lowagie.text.pdf.PdfWriter.getInstance(document, outputStream);
            document.open();

            document.add(new com.lowagie.text.Paragraph("Lista de Usuarios"));
            document.add(new com.lowagie.text.Paragraph(" ")); // blank line

            PdfPTable table = getPdfPTabla(users);

            document.add(table);
            document.close();
        } catch (Exception e) {
            throw new IOException("error al generarPDF", e);
        }
    }

    private static PdfPTable getPdfPTabla(List<UsuarioDto> users) {
        PdfPTable table = new PdfPTable(5); // 5 columns

        // Add table headers
        table.addCell("ID");
        table.addCell("Usuario");
        table.addCell("Nombres");
        table.addCell("Apellidos");
        table.addCell("Rol");

        // Add user data
        for (UsuarioDto user : users) {
            table.addCell(String.valueOf(user.id()));
            table.addCell(user.userName());
            table.addCell(user.firstName());
            table.addCell(user.lastName());
            table.addCell(user.userRole());
        }
        return table;
    }

    private void exportarATXT(List<UsuarioDto> users, PrintWriter writer) throws IOException {
        writer.write("Lista de Usuarios\n");
        writer.write("=================\n\n");
        for (UsuarioDto user : users) {
            writer.write("ID: " + user.id() + "\n");
            writer.write("Usuario: " + user.userName() + "\n");
            writer.write("Nombres: " + user.firstName() + "\n");
            writer.write("Apellidos: " + user.lastName() + "\n");
            writer.write("Rol: " + user.userRole() + "\n");
            writer.write("----------------------------\n");
        }
    }

    private void exportAJSON(List<UsuarioDto> users, PrintWriter writer) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        writer.write(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(users));
    }

    private void exportACSV(List<UsuarioDto> users, PrintWriter writer) throws IOException {
        writer.write(" Lista de Usuarios\n");
        StatefulBeanToCsv<UsuarioDto> beanToCsv = new StatefulBeanToCsvBuilder<UsuarioDto>(writer)
                .withQuotechar(CSVWriter.NO_QUOTE_CHARACTER)
                .withSeparator(CSVWriter.DEFAULT_SEPARATOR)
                .withOrderedResults(false)
                .build();
        try {
            beanToCsv.write(users);
        } catch (Exception e) {
            throw new IOException("Error exportando CSV", e);
        }

    }

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("usuario", new UsuarioDetDto(null, "", "", "", "", ""));
        model.addAttribute("editMode", false);
        return "create"; // Your Thymeleaf template: create.html
    }

    @PostMapping("/create")
    public String createUser(@ModelAttribute("usuario") UsuarioDetDto dto, Model model) {
        try {
            usuarioService.createUser(dto);
            return "redirect:/manage/start";
        } catch (Exception e) {
            model.addAttribute("error", "Error al crear el usuario.");
            return "create";
        }
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Integer id, Model model) {
        Optional<UsuarioDetDto> optional = usuarioService.getUserById(id);
        if (optional.isPresent()) {
            model.addAttribute("usuario", optional.get());
            model.addAttribute("editMode", true);
            return "edit";
        } else {
            model.addAttribute("error", "Usuario no encontrado");
            return "redirect:/manage/start";
        }
    }

    @PostMapping("/edit/{id}")
    public String updateUser(@PathVariable Integer id, @ModelAttribute UsuarioDetDto dto, Model model) {
        try {
            if (!id.equals(dto.id())) {
                model.addAttribute("error", "ID de usuario no coincide");
                return "edit";
            }
            boolean success = usuarioService.saveUser(dto);
            if (success) {
                return "redirect:/manage/start";
            } else {
                model.addAttribute("error", "Error al guardar usuario");
                return "edit";
            }
        } catch (Exception e) {
            model.addAttribute("error", "Error interno");
            return "edit";
        }
    }

    @PostMapping("/delete/{id}")
    public String deleteUser(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        try {
            Optional<UsuarioDetDto> dto = usuarioService.getUserById(id);
            if (dto.isEmpty() || !usuarioService.deleteUser(dto.get())) {
                redirectAttributes.addFlashAttribute("error", "No se pudo eliminar el usuario.");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error interno al eliminar.");
        }
        return "redirect:/manage/start";
    }
/*Endpoints*/
//    @PostMapping("/create")
//    public ResponseEntity<UsuarioDetDto> createUser(@RequestBody UsuarioDetDto dto) {
//        try {
//            return ResponseEntity.ok(usuarioService.createUser(dto));
//        } catch (Exception e) {
//            return ResponseEntity.badRequest().build();
//        }
//    }
//    @PutMapping("/update/{id}")
//    public ResponseEntity<Void> updateUser(@PathVariable Integer id, @RequestBody UsuarioDetDto dto) {
//        try {
//            if (!id.equals(dto.id())) return ResponseEntity.badRequest().build();
//            boolean success = usuarioService.saveUser(dto);
//            LOGGER.info("Usuario actualizado: " + dto.userName());
//            return success ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
//        } catch (Exception e) {
//            LOGGER.error("Error al actualizar el usuario", e);
//            return ResponseEntity.internalServerError().build();
//        }
//    }
//
//    @DeleteMapping("/delete/{id}")
//    public ResponseEntity<Void> deleteUser(@PathVariable Integer id) {
//        try {
//            Optional<UsuarioDetDto> dtoOpt = usuarioService.getUserById(id);
//            if (dtoOpt.isEmpty()) return ResponseEntity.notFound().build();
//            boolean success = usuarioService.deleteUser(dtoOpt.get());
//            return success ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
//        } catch (Exception e) {
//            return ResponseEntity.internalServerError().build();
//        }
//    }

}
