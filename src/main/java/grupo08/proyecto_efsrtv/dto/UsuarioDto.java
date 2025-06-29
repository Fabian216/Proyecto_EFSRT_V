package grupo08.proyecto_efsrtv.dto;

import java.time.LocalDateTime;

public record UsuarioDto(
        Integer id,
        String userName,
        String firstName,
        String lastName,
        String userRole,
        LocalDateTime created,
        LocalDateTime updated
) {
}
