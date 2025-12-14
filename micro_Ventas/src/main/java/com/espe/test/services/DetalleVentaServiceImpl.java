package com.espe.test.services;

import com.espe.test.models.entities.DetalleVenta;
import com.espe.test.repositories.DetalleVentaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
public class DetalleVentaServiceImpl implements DetalleVentaService {
    @Autowired
    private DetalleVentaRepository repository;

    @Override
    @Transactional(readOnly = true)
    public List<DetalleVenta> buscarTodos() {
        return (List<DetalleVenta>) repository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<DetalleVenta> buscarPorID(Long id) {
        return repository.findById(id);
    }

    @Override
    @Transactional
    public DetalleVenta guardar(DetalleVenta detalleVenta) {
        return repository.save(detalleVenta);
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        repository.deleteById(id);
    }
}
