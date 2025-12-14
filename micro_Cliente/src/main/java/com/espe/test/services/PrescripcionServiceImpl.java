package com.espe.test.services;

import com.espe.test.models.entities.Prescripcion;
import com.espe.test.repositories.PrescripcionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
public class PrescripcionServiceImpl implements PrescripcionService {
    @Autowired
    private PrescripcionRepository repository;

    @Override
    @Transactional(readOnly = true)
    public List<Prescripcion> buscarTodos() {
        return (List<Prescripcion>) repository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Prescripcion> buscarPorID(Long id) {
        return repository.findById(id);
    }

    @Override
    @Transactional
    public Prescripcion guardar(Prescripcion prescripcion) {
        return repository.save(prescripcion);
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        repository.deleteById(id);
    }
}
