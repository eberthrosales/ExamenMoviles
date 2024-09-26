package com.pe.edu.upeu.examenw.controller;

import com.pe.edu.upeu.examenw.entity.Muebles;
import com.pe.edu.upeu.examenw.service.MueblesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/muebles")
public class MueblesController {
    @Autowired
    private MueblesService mueblesService;
    @GetMapping()
    public ResponseEntity<List<Muebles>> list() {
        return ResponseEntity.ok().body(mueblesService.listar());
    }
    @PostMapping()
    public ResponseEntity<Muebles> save(@RequestBody Muebles muebles){
        return ResponseEntity.ok(mueblesService.guardar(muebles));
    }
    @PutMapping("/{id}")
    public ResponseEntity<Muebles> actualizarOferta(@PathVariable Integer id, @RequestBody Muebles muebles) {
        muebles.setId(Long.valueOf(id));  // Aseguramos que el ID del path se establece en la entidad
        Muebles ofertaActualizada = mueblesService.actualizar(muebles);
        return ResponseEntity.ok(ofertaActualizada);
    }   
    @GetMapping("/{id}")
    public ResponseEntity<Muebles> listById(@PathVariable(required = true) Integer id){
        return ResponseEntity.ok().body(mueblesService.listarPorId(id).get());
    }
    @DeleteMapping("/{id}")
    public String deleteById(@PathVariable(required = true) Integer id){
        mueblesService.eliminarPorId(id);
        return "Eliminacion Correcta";
    }
}
