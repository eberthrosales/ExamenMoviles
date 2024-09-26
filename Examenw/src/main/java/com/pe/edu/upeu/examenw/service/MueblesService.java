package com.pe.edu.upeu.examenw.service;

import com.pe.edu.upeu.examenw.entity.Muebles;

import java.util.List;
import java.util.Optional;

public interface MueblesService {
    public List<Muebles> listar();
    public Muebles guardar(Muebles muebles);
    public Muebles actualizar(Muebles muebles);
    public Optional<Muebles> listarPorId(Integer id);
    public void eliminarPorId(Integer id);

}
