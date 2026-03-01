package es.fplumara.dam1.campeonato.repository;

import es.fplumara.dam1.campeonato.model.Deportista;

import java.util.*;


/**
    * Implementación del repositorio de Deportistas.
    * - Almacena los datos EN MEMORIA utilizando un HashMap.
        * - Clave del mapa: id del deportista (String)
    * - Valor: la entidad Deportista
    * Importante: aquí NO se aplican reglas de negocio (duplicados, validaciones, etc.);
    *  eso lo hace la capa de Servicio. El repositorio solo guarda y recupera datos.
 */

public class DeportistaRepositoryImpl implements DeportistaRepository{
    // Estructura interna de almacenamiento: idDeportista -> Deportista
    // La declaramos 'final' porque la referencia al Map no cambia después
    // de construirse.
    private Map<String,Deportista> datos;


    /**
     * Constructor por defecto.
     * Crea un HashMap vacío para almacenar deportistas.
     * - Suficiente para la app de consola (no hay concurrencia).
     */
    public DeportistaRepositoryImpl() {
        this.datos = new HashMap<>();
    }


    /**
     * save(d) -> Guarda (inserta o actualiza) un deportista en memoria.
     * - Si ya existe una entrada con el mismo id, la REEMPLAZA (comportamiento estándar de Map).
     * - La verificación de duplicados por id (si no quieres permitirlo) la hace el Servicio.
     */
    @Override
    public void save(Deportista d) {
        datos.put(d.getId(), d);
    }

    /**
     * findById(id) -> Busca un deportista por su id.
     * - Devuelve Optional<Deportista>.
     * - Si no existe, devuelve Optional.empty() en lugar de null.
     */
    @Override
    public Optional<Deportista> findById(String id) {
        return Optional.ofNullable(datos.get(id));
    }

    /**
     * listAll() -> Devuelve una lista con TODOS los deportistas almacenados.
     * - Se devuelve una NUEVA lista para no exponer la colección interna
     *   (así nadie puede modificar el Map desde fuera).
     */
    @Override
    public List<Deportista> listAll() {
        return new ArrayList<>(datos.values());
    }

    @Override
    public List<Deportista> findByPais(String pais) {
        // Creamos una lista de salida donde vamos acumulando los deportistas que coinciden
        List<Deportista> resultado = new ArrayList<>();

        // Recorremos todos los deportistas guardados en el Map interno
        for (Deportista d : datos.values()) {
            // Como el servicio valida que 'pais' de Deportista no sea null cuando se registra,
            // podemos usar equals sin riesgo de NullPointerException.
            if (d.getPais().equals(pais)) {
                resultado.add(d); // Coincide: lo añadimos a la lista
            }
        }
        // Devolvemos la lista (puede ser vacía si no se encontró ninguno)
        return resultado;
    }
}
