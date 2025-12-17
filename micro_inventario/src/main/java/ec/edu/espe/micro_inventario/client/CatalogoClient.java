package ec.edu.espe.micro_inventario.client;

import ec.edu.espe.micro_inventario.dto.MedicamentoDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "micro-catalogo", url = "http://micro-catalogo:8080")
public interface CatalogoClient {

    @GetMapping("/medicamentos/{id}")
    MedicamentoDTO findById(@PathVariable("id") Long id);

    @PutMapping("/medicamentos/{id}")
    MedicamentoDTO update(@PathVariable("id") Long id, @RequestBody MedicamentoDTO medicamentoDTO);
}
