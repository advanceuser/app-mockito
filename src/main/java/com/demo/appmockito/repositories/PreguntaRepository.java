package com.demo.appmockito.repositories;

import java.util.List;

public interface PreguntaRepository {

    public List<String> findPreguntasPorExamenId(Long id);

    public void guardarVarias(List<String> preguntas);
}
