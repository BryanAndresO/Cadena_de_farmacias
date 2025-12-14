package com.espe.test.services;

import com.espe.test.models.entities.ReporteInventario;
import com.espe.test.repositories.ReporteInventarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
public class ReporteInventarioServiceImpl implements ReporteInventarioService {
    @Autowired
    private ReporteInventarioRepository repository;

    @Override
    @Transactional(readOnly = true)
    public List<ReporteInventario> buscarTodos() {
        return (List<ReporteInventario>) repository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ReporteInventario> buscarPorID(Long id) {
        return repository.findById(id);
    }

    @Override
    @Transactional
    public ReporteInventario guardar(ReporteInventario reporteInventario) {
        return repository.save(reporteInventario);
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        repository.deleteById(id);
    }
}
