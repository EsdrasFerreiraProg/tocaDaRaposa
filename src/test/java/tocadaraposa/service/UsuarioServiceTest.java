package tocadaraposa.service;

import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import tocadaraposa.ApplicationConfigTest;
import tocadaraposa.domain.Perfil;
import tocadaraposa.domain.PerfilTipo;
import tocadaraposa.domain.Usuario;
import tocadaraposa.repository.UsuarioRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class UsuarioServiceTest extends ApplicationConfigTest {

    @MockBean
    private UsuarioRepository userRepo;

    @Autowired
    private UsuarioService userService;

    @Test(expected = UsernameNotFoundException.class)
    public void shouldThrowUserNameNotFoundException(){
        final String mockedUsername = "test@gmail.com";
        userService.loadUserByUsername(mockedUsername);
    }

    @Test
    public void shouldCreateUserForSpringLogin(){
        final String mockedUsername = "test@gmail.com";

        Usuario mockedUser = Mockito.mock(Usuario.class);
        Optional<Usuario> optMockedUser = Optional.of(mockedUser);
        List<Perfil> list = List.of(PerfilTipo.ADMIN.buildPerfil());

        Mockito.when(mockedUser.getEmail()).thenReturn(mockedUsername);
        Mockito.when(mockedUser.getSenha()).thenReturn("senha");
        Mockito.when(mockedUser.getPerfis()).thenReturn(list);

        Mockito.when(userRepo.findByEmailAndAtivo(ArgumentMatchers.eq(mockedUsername))).thenReturn(optMockedUser);
        assertNotNull(userService.loadUserByUsername(mockedUsername));
    }

    @Test
    public void shouldBuscarPorEmailEAtivo(){
        final String mockedUsername = "test@gmail.com";

        Usuario mockedUser = Mockito.mock(Usuario.class);
        Optional<Usuario> optMockedUser = Optional.of(mockedUser);
        List<Perfil> list = List.of(PerfilTipo.ADMIN.buildPerfil());

        Mockito.when(mockedUser.getEmail()).thenReturn(mockedUsername);
        Mockito.when(mockedUser.getSenha()).thenReturn("senha");
        Mockito.when(mockedUser.getPerfis()).thenReturn(list);

        Mockito.when(userRepo.findByEmailAndAtivo(ArgumentMatchers.eq(mockedUsername))).thenReturn(optMockedUser);
        userService.buscarPorEmailEAtivo(mockedUsername);
        Mockito.verify(userRepo, Mockito.times(1)).findByEmailAndAtivo(mockedUsername);
    }

}
