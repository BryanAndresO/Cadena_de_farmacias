package com.espe.test.services;

import com.espe.test.models.entities.Venta;
import com.espe.test.repositories.VentaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
public class VentaServiceImpl implements VentaService {
    @Autowired
    private VentaRepository repository;

    @Override
    @Transactional(readOnly = true)
    public List<Venta> buscarTodos() {
        return (List<Venta>) repository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Venta> buscarPorID(Long id) {
        return repository.findById(id);
    }

    @Override
    @Transactional
    public Venta guardar(Venta venta) {
        return repository.save(venta);
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        repository.deleteById(id);
    }
}
