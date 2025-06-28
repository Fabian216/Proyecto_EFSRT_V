package grupo08.proyecto_efsrtv.service.impl;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import grupo08.proyecto_efsrtv.dto.UsuarioDetDto;
import grupo08.proyecto_efsrtv.dto.UsuarioDto;
import grupo08.proyecto_efsrtv.models.Usuario;
import grupo08.proyecto_efsrtv.repositories.IUsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.*;

public class UsuarioServiceImplTest {

    @InjectMocks
    private UsuarioServiceImpl usuarioService;

    @Mock
    private IUsuarioRepository usuarioRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    private Usuario usuario;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        usuario = new Usuario();
        usuario.setId(1);
        usuario.setUserName("testUser ");
        usuario.setPasswordHash("hashedPassword");
        usuario.setFirstName("Test");
        usuario.setLastName("User ");
        usuario.setUserRole("USER");
    }

    @Test
    public void testGetAllUsers() {
        when(usuarioRepository.findAll()).thenReturn(Arrays.asList(usuario));

        List<UsuarioDto> users = usuarioService.getAllUsers();

        assertEquals(1, users.size());
        assertEquals("testUser ", users.get(0).userName());
    }

    @Test
    public void testGetUserByIdUserExists() {
        when(usuarioRepository.findById(1)).thenReturn(Optional.of(usuario));

        Optional<UsuarioDetDto> user = usuarioService.getUserById(1);

        assertTrue(user.isPresent());
        assertEquals("testUser ", user.get().userName());
    }

    @Test
    public void testGetUserByIdUserDoesNotExist() {
        when(usuarioRepository.findById(1)).thenReturn(Optional.empty());

        Optional<UsuarioDetDto> user = usuarioService.getUserById(1);

        assertFalse(user.isPresent());
    }

    @Test
    public void testCreateUser () {
        UsuarioDetDto dto = new UsuarioDetDto(null, "testUser ", "password", "Test", "User ", "USER");
        when(passwordEncoder.encode("password")).thenReturn("hashedPassword");
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);

        UsuarioDetDto createdUser  = usuarioService.createUser (dto);

        assertNotNull(createdUser );
        assertEquals("testUser ", createdUser .userName());
        verify(usuarioRepository).save(any(Usuario.class));
    }

    @Test
    public void testSaveUser_UserExists() {
        UsuarioDetDto dto = new UsuarioDetDto(1, "updatedUser ", null, "Updated", "User ", "USER");
        when(usuarioRepository.findById(1)).thenReturn(Optional.of(usuario));

        boolean result = usuarioService.saveUser (dto);

        assertTrue(result);
        assertEquals("updatedUser ", usuario.getUserName());
        verify(usuarioRepository).save(usuario);
    }

    @Test
    public void testSaveUser_UserDoesNotExist() {
        UsuarioDetDto dto = new UsuarioDetDto(1, "updatedUser ", null, "Updated", "User ", "USER");
        when(usuarioRepository.findById(1)).thenReturn(Optional.empty());

        boolean result = usuarioService.saveUser (dto);

        assertFalse(result);
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    @Test
    public void testDeleteUser_UserExists() {
        UsuarioDetDto dto = new UsuarioDetDto(1, "testUser ", null, "Test", "User ", "USER");
        when(usuarioRepository.existsById(1)).thenReturn(true);

        boolean result = usuarioService.deleteUser (dto);

        assertTrue(result);
        verify(usuarioRepository).deleteById(1);
    }

    @Test
    public void testDeleteUser_UserDoesNotExist() {
        UsuarioDetDto dto = new UsuarioDetDto(1, "testUser ", null, "Test", "User ", "USER");
        when(usuarioRepository.existsById(1)).thenReturn(false);

        boolean result = usuarioService.deleteUser (dto);

        assertFalse(result);
        verify(usuarioRepository, never()).deleteById(anyInt());
    }
}
