package es.fplumara.dam1.campeonato.repository;

import es.fplumara.dam1.campeonato.model.Deportista;
import es.fplumara.dam1.campeonato.model.Resultado;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;


/**
 * Implementación del repositorio de resultados.
 * - Almacenamiento en memoria usando un Map<String, Resultado>
 * - Clave = id del resultado
 * - Valor = la entidad Resultado
 *
 * Usamos HashMap porque la app es de consola y mono-hilo.
 * Además, añadimos un constructor que permite INYECTAR el Map,
 * muy útil en tests (por ejemplo, para usar LinkedHashMap y tener orden estable).
 */

public class ResultadoRepositoryImpl  implements ResultadoRepository{

    // Estructura interna de almacenamiento.
    // Map<idResultado, Resultado>
    private Map<String, Resultado> datos;

    public ResultadoRepositoryImpl(){
        this.datos= new HashMap<>();
    }


    /**
     * save(...) -> Guarda el resultado (inserta o actualiza por id).
     * - Por diseño del repositorio, no lanzamos excepciones aquí.
     * - Las validaciones/duplicados se hacen en la capa de Servicio.
     */
    @Override
    public void save(Resultado r) {
        datos.put(r.getId(), r);
    }


    /**
     * findById(...) -> Busca por id y devuelve Optional<Resultado>.
     * - Optional.ofNullable(...) nos evita devolver null.
     */
    @Override
    public Optional<Resultado> findById(String id) {
        return Optional.ofNullable(datos.get(id));
    }


    /**
     * listAll() -> Devuelve una nueva lista con TODOS los resultados.
     * - new ArrayList<>(datos.values()) crea una COPIA.
     * - Así no exponemos ni dejamos modificar el Map interno desde fuera.
     */
    @Override
    public List<Resultado> listAll() {
        return new ArrayList<>(datos.values());
    }


    /**
     * existsByPruebaYDeportista(...) -> True si ya hay un resultado
     * para ese par (idPrueba, idDeportista).
     * - Se usa desde el Servicio para aplicar la regla de "1 resultado por prueba y deportista".
     * - anyMatch(...) corta en cuanto encuentra el primero (eficiente).
     */
    @Override
    public boolean existsByPruebaYDeportista(String idPrueba, String idDeportista) {

        // Recorremos todos los resultados guardados
        for (Resultado r : datos.values()) {
            // Como el servicio valida que idPrueba e idDeportista de r no son null,
            // podemos usar equals sin riesgo de NullPointerException.
            if (r.getIdPrueba().equals(idPrueba) && r.getIdDeportista().equals(idDeportista)) {
                return true; // Encontrado
            }
        }
        return false; // No hay ninguno que coincida
    }

}
