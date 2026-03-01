package es.fplumara.dam1.campeonato.repository;

import es.fplumara.dam1.campeonato.model.Deportista;

import java.util.List;
import java.util.Optional;

public interface DeportistaRepository {

    public void save(Deportista d);
    public Optional<Deportista> findById(String id);
    public List<Deportista> listAll();
    public List<Deportista> findByPais(String pais);

}
