package com.project.dao;

import com.project.domain.*;
import org.junit.jupiter.api.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)// indico que slo creo una instancia de manager test permite usar metodos no estticos en beforeall y afterall
class ManagerTest {

    @beforeall
    // Se ejecuta una sola vez antes de todos los tests.
    // Inicializa Hibernate y crea la SessionFactory.
    // Es una operación costosa, por eso no se repite en cada test.
    void setUp() {
        Manager.createSessionFactory();
    }

    @AfterAll
    // Se ejecuta una sola vez antes de todos los tests.
    // Inicializa Hibernate y crea la SessionFactory.
    // Es una operación costosa, por eso no se repite en cada test.
    void tearDown() { 
        Manager.close();
    }

    // -------------------------------------------------------------------------
    // TESTS CRUD BÁSICOS
    //

    @Test 
    void testAddAutor() {
        // Comprueba que se puede crear y persistir un autor correctamente
        // y que Hibernate genera el identificador automáticamente.

        Autor autor = Manager.addAutor("Isaac Asimov");

        assertNotNull(autor);
        assertNotNull(autor.getAutorId()); // Si hay ID, el objeto se ha persistido
        assertEquals("Isaac Asimov", autor.getNom());
    }

    @Test
    void testAddLlibre() { 
        // Comprueba la creación y persistencia de un libro
        // y la correcta asignación de sus atributos.
        Llibre llibre = Manager.addLlibre(
                "978-84-376-0494-7",
                "Fundació",
                "Debolsillo",
                1951
        );

        assertNotNull(llibre);
        assertEquals("Fundació", llibre.getTitol());
    }
    // -------------------------------------------------------------------------
    // TEST DE RELACIÓN MANY-TO-MANY (AUTOR - LLIBRE)
    // -------------------------------------------------------------------------
    @Test
    void testUpdateAutorWithLlibres() {
        //creamos datos autor y libros.
        Autor autor = Manager.addAutor("J. R. R. Tolkien");

        Llibre l1 = Manager.addLlibre("1", "El Hobbit", "Minotauro", 1937);
        Llibre l2 = Manager.addLlibre("2", "El Senyor dels Anells", "Minotauro", 1954);

        // Se actualiza el autor y se asignan los libros.
        // La relación Many-to-Many se persiste desde el LADO PROPIETARIO (Llibre),
        // tal como requiere JPA.
        Manager.updateAutor(
                autor.getAutorId(),
                "J. R. R. Tolkien (editat)",
                Set.of(l1, l2)
        );

        //verifico resultado usamos join fetch  y demostramos la relacion se ha persistido correctamente y se entiende el concepto de lado propietario JPA
        List<Llibre> llibres = Manager.findLlibresAmbAutors();

        Llibre llibreTest = llibres.stream()
                .filter(l -> l.getTitol().equals("El Hobbit"))
                .findFirst()
                .orElse(null);

        assertNotNull(llibreTest);
        assertTrue(
                llibreTest.getAutors()
                        .stream()
                        .anyMatch(a -> a.getNom().contains("Tolkien"))
        );
    }
    // -------------------------------------------------------------------------
    // TESTS DE LÓGICA DE NEGOCIO: PRÉSTAMOS
    // -------------------------------------------------------------------------
    @Test
    void testAddPrestecDisponible() {
         // Comprueba que se puede crear un préstamo
        // solo si el ejemplar está disponible.
        Biblioteca b = Manager.addBiblioteca(
                "Biblioteca Central",
                "Barcelona",
                "Carrer Major 1",
                "123456789",
                "biblio@test.com"
        );

        Llibre llibre = Manager.addLlibre("3", "Dune", "Nova", 1965);
        Exemplar exemplar = Manager.addExemplar("CB001", llibre, b);
        Persona persona = Manager.addPersona("12345678A", "Anna", "600000000", "anna@test.com");

        Prestec prestec = Manager.addPrestec(
                exemplar,
                persona,
                LocalDate.now(),
                LocalDate.now().plusDays(15)
        );
         // Se valida que el préstamo se crea correctamente
        // y que el ejemplar pasa a no estar disponible
        assertNotNull(prestec);
        assertTrue(prestec.isActiu());
        assertFalse(prestec.getExemplar().isDisponible());
    }

    @Test
    void testNoPrestecSiExemplarNoDisponible() {
        // Test de caso negativo:
        // comprueba que NO se permite un segundo préstamo
        // del mismo ejemplar si ya no está disponible
        Biblioteca b = Manager.addBiblioteca(
                "Biblio 2",
                "Girona",
                "Carrer Falsa 123",
                "999999999",
                "b2@test.com"
        );

        Llibre llibre = Manager.addLlibre("4", "Neuromancer", "Ace", 1984);
        Exemplar exemplar = Manager.addExemplar("CB002", llibre, b);
        Persona persona = Manager.addPersona("87654321B", "Marc", "611111111", "marc@test.com");
        // Primer préstamo (correcto
        Manager.addPrestec(exemplar, persona, LocalDate.now(), LocalDate.now().plusDays(7));
        // Segundo préstamo (debe fallar)
        Prestec segon = Manager.addPrestec(exemplar, persona, LocalDate.now(), LocalDate.now().plusDays(7));

        assertNull(segon);
    }
    //registro el retorno del prestamo y verifico que el ejemplar vuelve a estar dispo
    @Test
    void testRegistrarRetornPrestec() {
        // Comprueba que un préstamo activo se puede devolver correctamente
        // y que el ejemplar vuelve a estar disponible.
        Biblioteca b = Manager.addBiblioteca(
                "Biblio Retorns",
                "Lleida",
                "Plaça Major",
                "888888888",
                "retorn@test.com"
        );

        Llibre llibre = Manager.addLlibre("5", "1984", "Secker", 1949);
        Exemplar exemplar = Manager.addExemplar("CB003", llibre, b);
        Persona persona = Manager.addPersona("99999999C", "Laura", "622222222", "laura@test.com");

        Prestec prestec = Manager.addPrestec(
                exemplar,
                persona,
                LocalDate.now(),
                LocalDate.now().plusDays(10)
        );

        Manager.registrarRetornPrestec(prestec.getPrestecId(), LocalDate.now());

        assertFalse(prestec.isActiu());
        assertTrue(prestec.getExemplar().isDisponible());
    }
    // -------------------------------------------------------------------------
    // TEST DE CONSULTA HQL
    // -------------------------------------------------------------------------


    @Test
    void testFindLlibresEnPrestec() {
         // Comprueba la consulta HQL que devuelve los libros
        // que actualmente están en préstamo activo.
        // El resultado es una lista de Object[] con:
        // [título del libro, nombre de la persona]

        List<Object[]> resultats = Manager.findLlibresEnPrestec();

        assertNotNull(resultats);

        for (Object[] fila : resultats) {
            assertEquals(2, fila.length);
            assertNotNull(fila[0]); //titulo del libro
            assertNotNull(fila[1]); //nombre de la persona
        }
    }
}
