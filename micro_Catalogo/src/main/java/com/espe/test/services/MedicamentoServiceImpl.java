package com.espe.test.services;

import com.espe.test.models.entities.Medicamento;
import com.espe.test.repositories.MedicamentoRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class MedicamentoServiceImpl implements MedicamentoService {


    @Autowired
    private MedicamentoRepository repository;



/* <<<<<<<<<<  dba9fb5a-03ee-4a77-a3dc-864ed2810ad1  >>>>>>>>>>> */
    @Override
    @Transactional(readOnly = true)
    public List<Medicamento> buscarTodos() {
        return (List<Medicamento>) repository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Medicamento> buscarPorID(Long id) {
        return repository.findById(id);
    }

    @Override
    @Transactional
    public Medicamento guardar(Medicamento medicamento) {
        return repository.save(medicamento);
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        repository.deleteById(id);
    }
}
