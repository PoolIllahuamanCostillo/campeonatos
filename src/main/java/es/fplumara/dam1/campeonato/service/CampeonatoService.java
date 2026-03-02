package es.fplumara.dam1.campeonato.service;

import es.fplumara.dam1.campeonato.exception.DuplicadoException;
import es.fplumara.dam1.campeonato.exception.NoEncontradoException;
import es.fplumara.dam1.campeonato.exception.OperacionNoPermitidaException;
import es.fplumara.dam1.campeonato.model.Deportista;
import es.fplumara.dam1.campeonato.model.LineaRanking;
import es.fplumara.dam1.campeonato.model.Resultado;
import es.fplumara.dam1.campeonato.repository.DeportistaRepository;
import es.fplumara.dam1.campeonato.repository.ResultadoRepository;

import java.util.*;

public class CampeonatoService {

// Repositorios que vamos a usar dentro del servicio
    private DeportistaRepository deportistaRepo;
    private ResultadoRepository resultadoRepo;

// Constructor: recibimos las implementaciones de los repos

    public CampeonatoService(DeportistaRepository deportistaRepo, ResultadoRepository resultadoRepo) {
        this.deportistaRepo = deportistaRepo;
        this.resultadoRepo = resultadoRepo;
    }


// --------------------------------------------------------------------------
//                              REGISTRAR DEPORTISTA
// --------------------------------------------------------------------------

    /*
     * Reglas del enunciado:
     * 1) Si d es null → IllegalArgumentException
     * 2) Si id, nombre o pais son null/vacíos → IllegalArgumentException
     * 3) Si ya existe un deportista con ese id → DuplicadoException
     * 4) Si todo OK → guardar en repo
     */
    public void registrarDeportista(Deportista d){

        // 1. Validar null
        if (d == null) {
            throw new IllegalArgumentException("Deportista no puede ser null");
        }

        // 2. Validar campos vacíos
        if (esVacio(d.getId()) || esVacio(d.getNombre()) || esVacio(d.getPais())) {
            throw new IllegalArgumentException("Campos id/nombre/pais no pueden estar vacíos");
        }

        // 3. Comprobar duplicado por id
        if (deportistaRepo.findById(d.getId()).isPresent()) {
            throw new DuplicadoException("Ya existe deportista con id " + d.getId());
        }

        // 4. Guardar
        deportistaRepo.save(d);
    }

    // --------------------------------------------------------------------------
    // REGISTRAR RESULTADO
    // --------------------------------------------------------------------------

    /**
     * Reglas según enunciado:
     * 1) r null → IllegalArgumentException
     * 2) id, idPrueba, idDeportista vacíos → IllegalArgumentException
     * 3) tipoPrueba null → IllegalArgumentException
     * 4) posicion <= 0 → IllegalArgumentException
     * 5) si ya existe resultado con ese id → DuplicadoException
     * 6) si el deportista no existe → NoEncontradoException
     * 7) si ya tiene un resultado en esa misma prueba → OperacionNoPermitidaException
     * 8) si todo OK → guardar
     */

    public void registrarResultado(Resultado r) {

        // 1. null check
        if (r == null) {
            throw new IllegalArgumentException("Resultado no puede ser null");
        }

        // 2. validar campos vacíos
        if (esVacio(r.getId()) || esVacio(r.getIdPrueba()) || esVacio(r.getIdDeportista())) {
            throw new IllegalArgumentException("id, idPrueba o idDeportista no pueden estar vacíos");
        }

        // 3. tipoPrueba no puede ser null
        if (r.getTipoPrueba() == null) {
            throw new IllegalArgumentException("Tipo de prueba no puede ser null");
        }

        // 4. posición > 0
        if (r.getPosicion() <= 0) {
            throw new IllegalArgumentException("Posición debe ser mayor que cero");
        }

        // 5. Duplicado por id
        if (resultadoRepo.findById(r.getId()).isPresent()) {
            throw new DuplicadoException("Ya existe resultado con id " + r.getId());
        }

        // 6. El deportista debe existir
        if (deportistaRepo.findById(r.getIdDeportista()).isEmpty()) {
            throw new NoEncontradoException("No existe deportista con id " + r.getIdDeportista());
        }

        // 7. Regla 1 resultado por prueba y deportista
        if (resultadoRepo.existsByPruebaYDeportista(r.getIdPrueba(), r.getIdDeportista())) {
            throw new OperacionNoPermitidaException("Ese deportista ya tiene un resultado en esa prueba");
        }

        // 8. Guardar
        resultadoRepo.save(r);
    }

    // --------------------------------------------------------------------------
    // RANKING
    // --------------------------------------------------------------------------

    /**
     * ranking():
     * 1. Sumar puntos por deportista
     * 2. Crear objetos LineaRanking
     * 3. Ordenar por puntos descendente
     * 4. Retornar lista
     */
    public List<LineaRanking> ranking() {

        // 1) Sumar puntos por deportista
        Map<String, Integer> puntosPorDep = new HashMap<>();

        // Recorremos todos los resultados guardados
        List<Resultado> todos = resultadoRepo.listAll();
        for (Resultado r : todos) {
            String idDep = r.getIdDeportista();
            int puntos = r.getPuntos(); // 5/3/1/0 según getPuntos()

            // Acumular: lo que había + los puntos nuevos
            int acumulado = puntosPorDep.getOrDefault(idDep, 0);
            puntosPorDep.put(idDep, acumulado + puntos);
        }

        // 2) Convertir ese Map en una lista de LineaRanking
        List<LineaRanking> ranking = new ArrayList<>();

        // Recorremos las claves (ids de deportista) del mapa
        for (String idDep : puntosPorDep.keySet()) {
            int puntos = puntosPorDep.get(idDep);

            // Buscamos el deportista para obtener nombre y país
            Optional<Deportista> depOpt = deportistaRepo.findById(idDep);
            if (depOpt.isEmpty()) {
                // No debería ocurrir si el servicio valida al registrar resultados
                throw new IllegalStateException("Resultado con deportista inexistente: " + idDep);
            }
            Deportista dep = depOpt.get();

            // Creamos la línea del ranking
            LineaRanking linea = new LineaRanking(dep.getId(), dep.getNombre(), dep.getPais(), puntos);
            ranking.add(linea);
        }

        // 3) Ordenar por puntos DESC (de mayor a menor)
        ranking.sort((a, b) -> Integer.compare(b.getPuntos(), a.getPuntos()));

        // 4) Devolver
        return ranking;
    }


    // --------------------------------------------------------------------------
    // RESULTADOS DE UN PAÍS
    // --------------------------------------------------------------------------

    /**
     * resultadosDePais(pais):
     * 1. Buscar deportistas de ese país
     * 2. Guardar sus IDs en un Set para consulta rápida
     * 3. Filtrar resultados por esos IDs
     */
    public List<Resultado> resultadosDePais(String pais) {

        // 1. conseguir ids de deportistas de ese país
        Set<String> ids = new HashSet<>();

        for (Deportista d : deportistaRepo.listAll()) {
            if (d.getPais().equals(pais)) {
                ids.add(d.getId());
            }
        }

        // 2. filtrar resultados por esos ids
        List<Resultado> lista = new ArrayList<>();

        for (Resultado r : resultadoRepo.listAll()) {
            if (ids.contains(r.getIdDeportista())) {
                lista.add(r);
            }
        }

        return lista;
    }

    // --------------------------------------------------------------------------
    // PAÍSES PARTICIPANTES
    // --------------------------------------------------------------------------

    /**
     * paisesParticipantes():
     * 1. Recorrer todos los deportistas
     * 2. Añadir sus países a un Set (evita repetidos)
     */
    public Set<String> paisesParticipantes() {
        Set<String> paises = new HashSet<>();

        for (Deportista d : deportistaRepo.listAll()) {
            if (!esVacio(d.getPais())) {
                paises.add(d.getPais());
            }
        }

        return paises;
    }

    // Función útil para validar strings
    private boolean esVacio(String s) {
        return s == null || s.isBlank();
    }
}



