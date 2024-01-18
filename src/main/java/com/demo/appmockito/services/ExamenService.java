package com.demo.appmockito.services;

import com.demo.appmockito.models.Examen;

import java.util.Optional;

public interface ExamenService {

    public Optional<Examen> findExamenPorNombre(String nombre);
    public Examen findPorNombreConPreguntas(String nombre);

    public Examen guardarExamen(Examen examen);
}
