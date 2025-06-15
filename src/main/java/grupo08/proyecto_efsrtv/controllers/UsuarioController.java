package grupo08.proyecto_efsrtv.controllers;

import grupo08.proyecto_efsrtv.dto.UsuarioDto;
import grupo08.proyecto_efsrtv.models.Usuario;
import grupo08.proyecto_efsrtv.repositories.IUsuarioRepository;
import grupo08.proyecto_efsrtv.service.IUsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/manage")
public class UsuarioController {

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

}
