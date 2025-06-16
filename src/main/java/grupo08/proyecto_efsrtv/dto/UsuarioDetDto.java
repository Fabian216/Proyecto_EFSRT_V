package grupo08.proyecto_efsrtv.dto;

public record UsuarioDetDto(
        Integer id,
        String userName,
        String passwordHash,
        String firstName,
        String lastName,
        String userRole
) {}

