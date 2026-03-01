package es.fplumara.dam1.campeonato.repository;

import es.fplumara.dam1.campeonato.model.Resultado;

import java.util.List;
import java.util.Optional;


/**
 * Define las operaciones disponibles para gestionar Resultados en memoria.
 * Nota: aquí no hay lógica de negocio (reglas). Solo "qué" operaciones hay.
 */


public interface ResultadoRepository {

    /**
     * save(...) -> Guarda el resultado en el repositorio.
     * - Si la clave (id) ya existe, la sobreescribe (como hace un Map por defecto).
     * - La "regla de duplicados" NO va aquí; va en el Servicio.
     */
    public void save(Resultado r);



    /**
     * findById(...) -> Busca un resultado por su id.
     * - Devuelve Optional<Resultado>.
     * - Si no existe, devuelve Optional.empty().
     */
    public Optional<Resultado> findById(String id);


    /**
     * listAll() -> Devuelve TODOS los resultados que hay en memoria.
     * - Devuelve una lista nueva para no exponer la estructura interna.
     */

    public List<Resultado> listAll();


    /**
     * existsByPruebaYDeportista(...) -> ¿Ya existe un resultado para ese (idPrueba, idDeportista)?
     * - Se usa desde el Servicio para aplicar la regla:
     *   "un deportista solo puede tener 1 resultado por prueba".
     */

    public boolean existsByPruebaYDeportista(String idPrueba, String idDeportista);
}
