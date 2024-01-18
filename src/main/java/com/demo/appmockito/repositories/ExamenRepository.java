package com.demo.appmockito.repositories;

import com.demo.appmockito.models.Examen;

import java.util.List;

public interface ExamenRepository {
    public List<Examen> findAll();

    public Examen guardar(Examen examen);
}
