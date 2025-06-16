package grupo08.proyecto_efsrtv.controllers;

import grupo08.proyecto_efsrtv.dto.UsuarioDetDto;
import grupo08.proyecto_efsrtv.dto.UsuarioDto;
import grupo08.proyecto_efsrtv.models.Usuario;
import grupo08.proyecto_efsrtv.repositories.IUsuarioRepository;
import grupo08.proyecto_efsrtv.service.IUsuarioService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
