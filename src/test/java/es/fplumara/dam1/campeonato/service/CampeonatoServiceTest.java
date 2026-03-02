package es.fplumara.dam1.campeonato.service;

import es.fplumara.dam1.campeonato.exception.DuplicadoException;
import es.fplumara.dam1.campeonato.exception.NoEncontradoException;
import es.fplumara.dam1.campeonato.exception.OperacionNoPermitidaException;
import es.fplumara.dam1.campeonato.model.Deportista;
import es.fplumara.dam1.campeonato.model.Resultado;
import es.fplumara.dam1.campeonato.model.TipoPrueba;
import es.fplumara.dam1.campeonato.model.LineaRanking;
import es.fplumara.dam1.campeonato.repository.DeportistaRepository;
import es.fplumara.dam1.campeonato.repository.ResultadoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests unitarios de CampeonatoService usando Mockito.
 * La idea es "aislar" el Servicio de los repos: mockeamos los repos y
 * controlamos qué devuelven para comprobar las reglas de negocio.
 */
@ExtendWith(MockitoExtension.class) // Habilita Mockito en JUnit 5
class CampeonatoServiceTest {

    // ----- Mocks (dobles de prueba) -----
    @Mock
    private DeportistaRepository deportistaRepo;  // No usamos la impl real; esto es un mock

    @Mock
    private ResultadoRepository resultadoRepo;    // Mock para controlar respuestas

    // ----- SUT (System Under Test): la clase que probamos) -----
    @InjectMocks
    private CampeonatoService service;            // Mockito inyecta los mocks en el constructor

    // Datos de ejemplo reutilizables en varios tests
    private Deportista ana;
    private Deportista bruno;
    private Deportista carla;

    @BeforeEach
    void init() {
        // Creamos 3 deportistas válidos para usar en distintos casos
        ana   = new Deportista("D001", "Ana",   "ES");
        bruno = new Deportista("D002", "Bruno", "PT");
        carla = new Deportista("D003", "Carla", "ES");
    }

    // -------------------------------------------------------------------------
    // Tests de registrarDeportista
    // -------------------------------------------------------------------------
    @Nested
    @DisplayName("registrarDeportista(...)")
    class RegistrarDeportista {

        @Test
        @DisplayName("OK: guarda cuando no existe duplicado y campos son válidos")
        void registrarDeportista_ok() {
            // Arrange: Simulamos que NO existe deportista con ese id
            when(deportistaRepo.findById("D001")).thenReturn(Optional.empty());

            // Act: llamamos al método a probar
            service.registrarDeportista(ana);

            // Assert: verificamos que se llamara a save con el objeto correcto
            verify(deportistaRepo, times(1)).save(ana);
            // Opcional: verifica que no se hayan hecho llamadas extra
            verifyNoMoreInteractions(deportistaRepo, resultadoRepo);
        }

        @Test
        @DisplayName("Error: duplicado por id -> DuplicadoException")
        void registrarDeportista_duplicado() {
            // Arrange: Simulamos que SÍ existe un deportista con ese id
            when(deportistaRepo.findById("D001")).thenReturn(Optional.of(ana));

            // Act + Assert: esperamos la excepción
            assertThrows(DuplicadoException.class, () -> service.registrarDeportista(ana));

            // Y además, que NO se intente guardar
            verify(deportistaRepo, never()).save(any());
        }

        @Test
        @DisplayName("Error: null o campos vacíos -> IllegalArgumentException")
        void registrarDeportista_invalido() {
            // Caso 1: objeto null
            assertThrows(IllegalArgumentException.class, () -> service.registrarDeportista(null));

            // Caso 2: id vacío
            var dSinId = new Deportista("", "X", "ES");
            assertThrows(IllegalArgumentException.class, () -> service.registrarDeportista(dSinId));

            // Caso 3: nombre vacío
            var dSinNombre = new Deportista("D010", "   ", "ES");
            assertThrows(IllegalArgumentException.class, () -> service.registrarDeportista(dSinNombre));

            // Caso 4: pais vacío
            var dSinPais = new Deportista("D011", "Pepe", "  ");
            assertThrows(IllegalArgumentException.class, () -> service.registrarDeportista(dSinPais));

            // En ningún caso se intenta guardar
            verify(deportistaRepo, never()).save(any());
        }
    }

    // -------------------------------------------------------------------------
    // Tests de registrarResultado
    // -------------------------------------------------------------------------
    @Nested
    @DisplayName("registrarResultado(...)")
    class RegistrarResultado {

        @Test
        @DisplayName("OK: guarda si pasa todas las validaciones")
        void registrarResultado_ok() {
            // Arrange: Resultado válido
            var res = new Resultado("R001", "P001", TipoPrueba.CARRERA, "D001", 1);

            // No existe un resultado con ese id
            when(resultadoRepo.findById("R001")).thenReturn(Optional.empty());
            // El deportista referenciado existe
            when(deportistaRepo.findById("D001")).thenReturn(Optional.of(ana));
            // Regla: aún NO tiene resultado en la misma prueba
            when(resultadoRepo.existsByPruebaYDeportista("P001", "D001")).thenReturn(false);

            // Act
            service.registrarResultado(res);

            // Assert
            verify(resultadoRepo).save(res);
            verifyNoMoreInteractions(resultadoRepo, deportistaRepo);
        }

        @Test
        @DisplayName("Error: resultado duplicado por id -> DuplicadoException")
        void registrarResultado_duplicado() {
            var res = new Resultado("R001", "P001", TipoPrueba.CARRERA, "D001", 1);

            // Simulamos que YA existe resultado con ese id
            when(resultadoRepo.findById("R001")).thenReturn(Optional.of(res));

            assertThrows(DuplicadoException.class, () -> service.registrarResultado(res));
            verify(resultadoRepo, never()).save(any());
        }

        @Test
        @DisplayName("Error: deportista no existe -> NoEncontradoException")
        void registrarResultado_deportistaNoExiste() {
            var res = new Resultado("R002", "P001", TipoPrueba.CARRERA, "D999", 2);

            when(resultadoRepo.findById("R002")).thenReturn(Optional.empty());
            when(deportistaRepo.findById("D999")).thenReturn(Optional.empty());

            assertThrows(NoEncontradoException.class, () -> service.registrarResultado(res));
            verify(resultadoRepo, never()).save(any());
        }

        @Test
        @DisplayName("Error: ya existe resultado para (prueba, deportista) -> OperacionNoPermitidaException")
        void registrarResultado_yaTieneResultadoEnEsaPrueba() {
            var res = new Resultado("R003", "P001", TipoPrueba.CARRERA, "D001", 3);

            when(resultadoRepo.findById("R003")).thenReturn(Optional.empty());
            when(deportistaRepo.findById("D001")).thenReturn(Optional.of(ana));
            when(resultadoRepo.existsByPruebaYDeportista("P001", "D001")).thenReturn(true);

            assertThrows(OperacionNoPermitidaException.class, () -> service.registrarResultado(res));
            verify(resultadoRepo, never()).save(any());
        }

        @Test
        @DisplayName("Error: validaciones básicas -> IllegalArgumentException")
        void registrarResultado_validacionesBasicas() {
            // id null
            assertThrows(IllegalArgumentException.class,
                    () -> service.registrarResultado(new Resultado(null, "P", TipoPrueba.CARRERA, "D1", 1)));

            // idPrueba vacío
            assertThrows(IllegalArgumentException.class,
                    () -> service.registrarResultado(new Resultado("R", "   ", TipoPrueba.CARRERA, "D1", 1)));

            // idDeportista vacío
            assertThrows(IllegalArgumentException.class,
                    () -> service.registrarResultado(new Resultado("R", "P", TipoPrueba.CARRERA, " ", 1)));

            // tipoPrueba null
            assertThrows(IllegalArgumentException.class,
                    () -> service.registrarResultado(new Resultado("R", "P", null, "D1", 1)));

            // posicion <= 0
            assertThrows(IllegalArgumentException.class,
                    () -> service.registrarResultado(new Resultado("R", "P", TipoPrueba.CARRERA, "D1", 0)));
        }
    }

    // -------------------------------------------------------------------------
    // Tests de ranking()
    // -------------------------------------------------------------------------
    @Nested
    @DisplayName("ranking()")
    class RankingTests {

        @Test
        @DisplayName("Suma puntos por deportista y ordena descendentemente")
        void ranking_sumaYOrdena() {
            // Arrange: 3 resultados (D001: 1º=5 + 3º=1 = 6; D002: 2º=3)
            var r1 = new Resultado("R1", "P001", TipoPrueba.CARRERA, "D001", 1); // 5
            var r2 = new Resultado("R2", "P001", TipoPrueba.CARRERA, "D002", 2); // 3
            var r3 = new Resultado("R3", "P002", TipoPrueba.SALTO,   "D001", 3); // +1 => 6 total

            when(resultadoRepo.listAll()).thenReturn(List.of(r1, r2, r3));
            // Para construir LineaRanking necesitamos nombre y país de cada id
            when(deportistaRepo.findById("D001")).thenReturn(Optional.of(ana));
            when(deportistaRepo.findById("D002")).thenReturn(Optional.of(bruno));

            // Act
            List<LineaRanking> rk = service.ranking();

            // Assert: esperado: D001(6), D002(3)
            assertEquals(2, rk.size());
            assertEquals("D001", rk.get(0).getIdDeportista());
            assertEquals(6, rk.get(0).getPuntos());

            assertEquals("D002", rk.get(1).getIdDeportista());
            assertEquals(3, rk.get(1).getPuntos());
        }
    }

    // -------------------------------------------------------------------------
    // Tests de resultadosDePais(String)
    // -------------------------------------------------------------------------
    @Nested
    @DisplayName("resultadosDePais(pais)")
    class ResultadosDePaisTests {

        @Test
        @DisplayName("Filtra resultados por ids de deportistas del país")
        void resultadosDePais_filtra() {
            // Arrange: En ES están Ana (D001) y Carla (D003)
            when(deportistaRepo.listAll()).thenReturn(List.of(ana, bruno, carla));

            var r1 = new Resultado("R1", "P001", TipoPrueba.CARRERA, "D001", 1); // ES
            var r2 = new Resultado("R2", "P001", TipoPrueba.CARRERA, "D002", 2); // PT
            var r3 = new Resultado("R3", "P002", TipoPrueba.SALTO,   "D003", 3); // ES
            when(resultadoRepo.listAll()).thenReturn(List.of(r1, r2, r3));

            // Act
            var out = service.resultadosDePais("ES");

            // Assert: solo R1 y R3 (D001 y D003)
            assertEquals(2, out.size());
            assertTrue(out.stream().anyMatch(r -> r.getId().equals("R1")));
            assertTrue(out.stream().anyMatch(r -> r.getId().equals("R3")));
        }
    }

    // -------------------------------------------------------------------------
    // Tests de paisesParticipantes()
    // -------------------------------------------------------------------------
    @Nested
    @DisplayName("paisesParticipantes()")
    class PaisesParticipantesTests {

        @Test
        @DisplayName("Devuelve un Set sin repetidos con los países de los deportistas")
        void paisesParticipantes_ok() {
            when(deportistaRepo.listAll()).thenReturn(List.of(ana, bruno, carla));

            Set<String> paises = service.paisesParticipantes();

            assertEquals(Set.of("ES", "PT"), paises);
        }
    }
}
