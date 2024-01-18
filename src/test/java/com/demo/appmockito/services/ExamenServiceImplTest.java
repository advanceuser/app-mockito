package com.demo.appmockito.services;

import com.demo.appmockito.Datos;
import com.demo.appmockito.models.Examen;
import com.demo.appmockito.repositories.ExamenRepository;
import com.demo.appmockito.repositories.ExamenRepositoryImpl;
import com.demo.appmockito.repositories.PreguntaRepository;
import com.demo.appmockito.repositories.PreguntaRepositoryImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExamenServiceImplTest {

    @Mock
    private ExamenRepositoryImpl repository;
    @Mock
    private PreguntaRepositoryImpl preguntaRepository;

    @InjectMocks
    private ExamenServiceImpl service;

    @Captor
    ArgumentCaptor<Long> captor;

    @BeforeEach
    void setUp() {
        //MockitoAnnotations.openMocks(this);
        //repository = mock(ExamenRepository.class);
        //preguntaRepository=mock(PreguntaRepository.class);
        //service = new ExamenServiceImpl(repository,preguntaRepository);
    }

    @Test
    void findExamenPorNombre() {
        when(repository.findAll()).thenReturn(Datos.EXAMENES);

        Optional<Examen> examen = service.findExamenPorNombre("Matematicas");

        assertTrue(examen.isPresent());
        assertEquals(5L, examen.orElseThrow().getId());
        assertEquals("Matematicas", examen.orElseThrow().getNombre());
    }

    @Test
    void findExamenPorNombreListaVacia() {
        List<Examen> datos = Collections.emptyList();
        when(repository.findAll()).thenReturn(datos);

        Optional<Examen> examen = service.findExamenPorNombre("Matematicas");

        assertFalse(examen.isPresent());
    }

    @Test
    void testPreguntasExamen() {
        when(repository.findAll()).thenReturn(Datos.EXAMENES);
        when(preguntaRepository.findPreguntasPorExamenId(anyLong())).thenReturn(Datos.PREGUNTAS);
        Examen examen = service.findPorNombreConPreguntas("Historia");
        assertEquals(5, examen.getPreguntas().size());
        assertTrue(examen.getPreguntas().contains("Derivadas"));
    }

    @Test
    void testPreguntasExamenVerify() {
        when(repository.findAll()).thenReturn(Datos.EXAMENES);
        when(preguntaRepository.findPreguntasPorExamenId(anyLong())).thenReturn(Datos.PREGUNTAS);
        Examen examen = service.findPorNombreConPreguntas("Matematicas");
        assertEquals(5, examen.getPreguntas().size());
        assertTrue(examen.getPreguntas().contains("Integrales"));
        verify(repository).findAll();
        verify(preguntaRepository).findPreguntasPorExamenId(anyLong());
    }

    @Test
    void testNoExisteExamenVerify() {
        when(repository.findAll()).thenReturn(Datos.EXAMENES);
        when(preguntaRepository.findPreguntasPorExamenId(anyLong())).thenReturn(Datos.PREGUNTAS);
        Examen examen = service.findPorNombreConPreguntas("Matematicas");
        assertNotNull(examen);
        verify(repository).findAll();
        verify(preguntaRepository).findPreguntasPorExamenId(anyLong());
    }

    @Test
    void testGuardarExamen() {
        //Given
        Examen newExamen = Datos.EXAMEN;
        newExamen.setPreguntas(Datos.PREGUNTAS);

        when(repository.guardar(any(Examen.class))).then(new Answer<Examen>() {
            Long secuencia = 8L;

            @Override
            public Examen answer(InvocationOnMock invocation) throws Throwable {
                Examen examen = invocation.getArgument(0);
                examen.setId(secuencia++);
                return examen;
            }
        });
        // When
        Examen examen = service.guardarExamen(Datos.EXAMEN);

        //Then
        assertNotNull(examen.getId());
        assertEquals(8L, examen.getId());
        assertEquals("Fisica", examen.getNombre());

        verify(repository).guardar(any(Examen.class));
        verify(preguntaRepository).guardarVarias(anyList());
    }

    @Test
    void testManejoException() {
        when(repository.findAll()).thenReturn(Datos.ID_NULL);
        when(preguntaRepository.findPreguntasPorExamenId(isNull())).thenThrow(IllegalArgumentException.class);
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            service.findPorNombreConPreguntas("Matematicas");
        });
        assertEquals(IllegalArgumentException.class, exception.getClass());
        verify(repository).findAll();
        verify(preguntaRepository).findPreguntasPorExamenId(isNull());
    }

    @Test
    void testArgumentMatchers() {
        when(repository.findAll()).thenReturn(Datos.EXAMENES);
        when(preguntaRepository.findPreguntasPorExamenId(anyLong())).thenReturn(Datos.PREGUNTAS);
        service.findPorNombreConPreguntas("Matematicas");
        verify(repository).findAll();
        //verify(preguntaRepository).findPreguntasPorExamenId(argThat(arg -> arg.equals(5L)));
        //verify(preguntaRepository).findPreguntasPorExamenId(argThat(arg -> arg != null && arg.equals(5L)));
        verify(preguntaRepository).findPreguntasPorExamenId(argThat(arg -> arg != null && arg >= 5L));
        //verify(preguntaRepository).findPreguntasPorExamenId(eq(5L));
    }

    @Test
    void testArgumentMatchers2() {
        when(repository.findAll()).thenReturn(Datos.EXAMENES_NEGATIVOS);
        when(preguntaRepository.findPreguntasPorExamenId(anyLong())).thenReturn(Datos.PREGUNTAS);
        service.findPorNombreConPreguntas("Historia");

        verify(repository).findAll();
        verify(preguntaRepository).findPreguntasPorExamenId(argThat(new MiArgsMatchers()));

    }

    @Test
    void testArgumentMatchers3() {
        when(repository.findAll()).thenReturn(Datos.EXAMENES_NEGATIVOS);
        when(preguntaRepository.findPreguntasPorExamenId(anyLong())).thenReturn(Datos.PREGUNTAS);
        service.findPorNombreConPreguntas("Matematicas");

        verify(repository).findAll();
        verify(preguntaRepository).findPreguntasPorExamenId(argThat(argument -> argument != null && argument > 0));

    }

    public static class MiArgsMatchers implements ArgumentMatcher<Long> {

        private Long argument;

        @Override
        public boolean matches(Long argument) {
            this.argument = argument;
            return argument != null && argument > 0;
        }

        @Override
        public String toString() {
            return "este es un mensaje personalizado de error en caso de que el test de mockito falle," + argument + " debe ser un numero entero positivo";
        }
    }

    @Test
    void testArgumentCaptor() {
        when(repository.findAll()).thenReturn(Datos.EXAMENES);
        when(preguntaRepository.findPreguntasPorExamenId(anyLong())).thenReturn(Datos.PREGUNTAS);
        service.findPorNombreConPreguntas("Matematicas");

        //ArgumentCaptor<Long> captor=ArgumentCaptor.forClass(Long.class);

        verify(preguntaRepository).findPreguntasPorExamenId(captor.capture());

        assertEquals(5L, captor.getValue());
    }

    @Test
    void testDoThrow() {
        Examen examen = Datos.EXAMEN;
        examen.setPreguntas(Datos.PREGUNTAS);

        doThrow(IllegalArgumentException.class).when(preguntaRepository).guardarVarias(anyList());

        assertThrows(IllegalArgumentException.class, () -> {
            service.guardarExamen(examen);
        });
    }

    @Test
    void testDoAnswer() {
        when(repository.findAll()).thenReturn(Datos.EXAMENES);
        //when(preguntaRepository.findPreguntasPorExamenId(anyLong())).thenReturn(Datos.PREGUNTAS);
        doAnswer(invocation -> {
            Long id = invocation.getArgument(0);
            return id == 5 ? Datos.PREGUNTAS : Collections.emptyList();
        }).when(preguntaRepository).findPreguntasPorExamenId(anyLong());

        Examen examen = service.findPorNombreConPreguntas("Matematicas");

        assertEquals(5, examen.getPreguntas().size());
        assertTrue(examen.getPreguntas().contains("Geometria"));
        assertEquals(5L, examen.getId());
        assertEquals("Matematicas", examen.getNombre());

        verify(preguntaRepository).findPreguntasPorExamenId(anyLong());
    }

    @Test
    void testDoAnswerGuardarExamen() {
        //Given
        Examen newExamen = Datos.EXAMEN;
        newExamen.setPreguntas(Datos.PREGUNTAS);

        doAnswer(new Answer<Examen>() {
            Long secuencia = 8L;

            @Override
            public Examen answer(InvocationOnMock invocation) throws Throwable {
                Examen examen = invocation.getArgument(0);
                examen.setId(secuencia++);
                return examen;
            }
        }).when(repository).guardar(any(Examen.class));

        // When
        Examen examen = service.guardarExamen(Datos.EXAMEN);

        //Then
        assertNotNull(examen.getId());
        assertEquals(8L, examen.getId());
        assertEquals("Fisica", examen.getNombre());

        verify(repository).guardar(any(Examen.class));
        verify(preguntaRepository).guardarVarias(anyList());
    }

    @Test
    void testDoCallRealMethod() {
        when(repository.findAll()).thenReturn(Datos.EXAMENES);
        //when(preguntaRepository.findPreguntasPorExamenId(anyLong())).thenReturn(Datos.PREGUNTAS);
        doCallRealMethod().when(preguntaRepository).findPreguntasPorExamenId(anyLong());

        Examen examen = service.findPorNombreConPreguntas("Matematicas");

        assertEquals(5L, examen.getId());
        assertEquals("Matematicas", examen.getNombre());
    }

    @Test
    void testSpy() {
        ExamenRepository examenRepository = spy(ExamenRepositoryImpl.class);
        PreguntaRepository preguntaRepository=spy(PreguntaRepositoryImpl.class);
        ExamenService examenService=new ExamenServiceImpl(examenRepository,preguntaRepository);

        List<String> preguntas= Arrays.asList("Aritmetica");
        //when(preguntaRepository.findPreguntasPorExamenId(anyLong())).thenReturn(preguntas);
        doReturn(preguntas).when(preguntaRepository).findPreguntasPorExamenId(anyLong());

        Examen examen=examenService.findPorNombreConPreguntas("Matematicas");
        assertEquals(5L,examen.getId());
        assertEquals("Matematicas",examen.getNombre());
        assertEquals(1,examen.getPreguntas().size());
        assertTrue(examen.getPreguntas().contains("Aritmetica"));

        verify(examenRepository).findAll();
        verify(preguntaRepository).findPreguntasPorExamenId(anyLong());
    }

    @Test
    void testOrdenDeInvocaciones() {
        when(repository.findAll()).thenReturn(Datos.EXAMENES);

        service.findPorNombreConPreguntas("Matematicas");
        service.findPorNombreConPreguntas("Lenguaje");

        InOrder inOrder = inOrder(preguntaRepository);

        inOrder.verify(preguntaRepository).findPreguntasPorExamenId(5L);
        inOrder.verify(preguntaRepository).findPreguntasPorExamenId(6L);
    }

    @Test
    void testOrdenDeInvocaciones2() {
        when(repository.findAll()).thenReturn(Datos.EXAMENES);

        service.findPorNombreConPreguntas("Matematicas");
        service.findPorNombreConPreguntas("Lenguaje");

        InOrder inOrder = inOrder(repository,preguntaRepository);

        inOrder.verify(repository).findAll();
        inOrder.verify(preguntaRepository).findPreguntasPorExamenId(5L);
        inOrder.verify(repository).findAll();
        inOrder.verify(preguntaRepository).findPreguntasPorExamenId(6L);
    }

    @Test
    void testNumerDeInvocaciones() {
        when(repository.findAll()).thenReturn(Datos.EXAMENES);

        service.findPorNombreConPreguntas("Matematicas");
        verify(preguntaRepository).findPreguntasPorExamenId(5L);
        verify(preguntaRepository,times(1)).findPreguntasPorExamenId(5L);
        verify(preguntaRepository,atLeast(1)).findPreguntasPorExamenId(5L);
        verify(preguntaRepository,atLeastOnce()).findPreguntasPorExamenId(5L);
        verify(preguntaRepository,atMost(1)).findPreguntasPorExamenId(5L);
        verify(preguntaRepository,atMostOnce()).findPreguntasPorExamenId(5L);
    }

    @Test
    void testNumerDeInvocaciones2() {
        when(repository.findAll()).thenReturn(Datos.EXAMENES);

        service.findPorNombreConPreguntas("Matematicas");
        //verify(preguntaRepository).findPreguntasPorExamenId(5L);
        verify(preguntaRepository,times(2)).findPreguntasPorExamenId(5L);
        verify(preguntaRepository,atLeast(2)).findPreguntasPorExamenId(5L);
        verify(preguntaRepository,atLeastOnce()).findPreguntasPorExamenId(5L);
        verify(preguntaRepository,atMost(20)).findPreguntasPorExamenId(5L);
        //verify(preguntaRepository,atMostOnce()).findPreguntasPorExamenId(5L);
    }

    @Test
    void testNumerDeInvocaciones3() {
        when(repository.findAll()).thenReturn(Collections.emptyList());

        service.findPorNombreConPreguntas("Matematicas");

        verify(preguntaRepository,never()).findPreguntasPorExamenId(5L);
        verifyNoInteractions(preguntaRepository);
        verify(repository).findAll();
        verify(repository,times(1)).findAll();
        verify(repository,atLeast(1)).findAll();
        verify(repository,atLeastOnce()).findAll();
        verify(repository,atMost(10)).findAll();
        verify(repository,atMostOnce()).findAll();
    }
}