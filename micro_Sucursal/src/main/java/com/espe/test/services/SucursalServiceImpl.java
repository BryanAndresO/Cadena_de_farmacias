package com.espe.test.services;

import com.espe.test.models.entities.Sucursal;
import com.espe.test.repositories.SucursalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
public class SucursalServiceImpl implements SucursalService {
    @Autowired
    private SucursalRepository repository;

    @Override
    @Transactional(readOnly = true)
    public List<Sucursal> buscarTodos() {
        return (List<Sucursal>) repository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Sucursal> buscarPorID(Long id) {
        return repository.findById(id);
    }

    @Override
    @Transactional
    public Sucursal guardar(Sucursal sucursal) {
        return repository.save(sucursal);
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        repository.deleteById(id);
    }
}
