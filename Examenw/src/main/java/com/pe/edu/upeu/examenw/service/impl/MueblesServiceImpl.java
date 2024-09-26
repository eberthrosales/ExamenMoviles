package com.pe.edu.upeu.examenw.service.impl;

import com.pe.edu.upeu.examenw.entity.Muebles;
import com.pe.edu.upeu.examenw.repository.MueblesRepository;
import com.pe.edu.upeu.examenw.service.MueblesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MueblesServiceImpl implements MueblesService {
    @Autowired
    private MueblesRepository mueblesRepository;
    @Override
    public List<Muebles> listar() {
        return mueblesRepository.findAll();
    }
    @Override
    public Muebles guardar(Muebles muebles) {
        return mueblesRepository.save(muebles);
    }
    @Override
    public Muebles actualizar(Muebles muebles) {
        // Verificar si el mueble ya existe en la base de datos
        Optional<Muebles> existingMueble = mueblesRepository.findById(Math.toIntExact(muebles.getId()));
        if (existingMueble.isPresent()) {
            Muebles muebleToUpdate = existingMueble.get();
            // Actualizar los campos necesarios
            muebleToUpdate.setNombre(muebles.getNombre());
            muebleToUpdate.setDescripcion(muebles.getDescripcion());
            muebleToUpdate.setTipo(muebles.getTipo());
            muebleToUpdate.setPrecio(muebles.getPrecio());
            muebleToUpdate.setStock(muebles.getStock());
            muebleToUpdate.setDimensiones(muebles.getDimensiones());
            muebleToUpdate.setColor(muebles.getColor());
            muebleToUpdate.setMaterial(muebles.getMaterial());
            // Guardar los cambios
            return mueblesRepository.save(muebleToUpdate);
        } else {
            throw new RuntimeException("Mueble no encontrado con el ID: " + muebles.getId());
        }
    }

    @Override
    public Optional<Muebles> listarPorId(Integer id) {
        return mueblesRepository.findById(id);
    }
    @Override
    public void eliminarPorId(Integer id) {
        mueblesRepository.deleteById(id);
    }
}
