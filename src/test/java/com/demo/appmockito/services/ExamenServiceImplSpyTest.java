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
class ExamenServiceImplSpyTest {

    @Spy
    private ExamenRepositoryImpl repository;
    @Spy
    private PreguntaRepositoryImpl preguntaRepository;

    @InjectMocks
    private ExamenServiceImpl service;

    @Test
    void testSpy() {
        List<String> preguntas= Arrays.asList("Aritmetica");
        //when(preguntaRepository.findPreguntasPorExamenId(anyLong())).thenReturn(preguntas);
        doReturn(preguntas).when(preguntaRepository).findPreguntasPorExamenId(anyLong());

        Examen examen=service.findPorNombreConPreguntas("Matematicas");
        assertEquals(5L,examen.getId());
        assertEquals("Matematicas",examen.getNombre());
        assertEquals(1,examen.getPreguntas().size());
        assertTrue(examen.getPreguntas().contains("Aritmetica"));

        verify(repository).findAll();
        verify(preguntaRepository).findPreguntasPorExamenId(anyLong());
    }
}