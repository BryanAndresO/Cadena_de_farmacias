package com.espe.test.services;

import com.espe.test.models.entities.ReporteVenta;
import com.espe.test.repositories.ReporteVentaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
public class ReporteVentaServiceImpl implements ReporteVentaService {
    @Autowired
    private ReporteVentaRepository repository;

    @Override
    @Transactional(readOnly = true)
    public List<ReporteVenta> buscarTodos() {
        return (List<ReporteVenta>) repository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ReporteVenta> buscarPorID(Long id) {
        return repository.findById(id);
    }

    @Override
    @Transactional
    public ReporteVenta guardar(ReporteVenta reporteVenta) {
        return repository.save(reporteVenta);
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        repository.deleteById(id);
    }
}
