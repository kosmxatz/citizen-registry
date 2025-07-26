package com.example.dao;

import com.example.entity.Citizen;
import org.junit.jupiter.api.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class CitizenDAOTest {

    private static CitizenDAO citizenDAO;

    @BeforeAll
    public static void setup() {
        citizenDAO = new CitizenDAO();
    }

    @Test
    @Order(1)
    public void testSaveAndFindById() {
        Citizen citizen = new Citizen("John", "Doe");
        Citizen saved = citizenDAO.save(citizen);
        assertNotNull(saved.getId(), "Saved citizen should have an ID");

        Citizen retrieved = citizenDAO.findById(saved.getId());
        assertNotNull(retrieved, "Citizen should be retrievable by ID");
        assertEquals("John", retrieved.getName());
        assertEquals("Doe", retrieved.getSurname());
    }

    @Test
    @Order(2)
    public void testFindAll() {
        List<Citizen> all = citizenDAO.findAll();
        assertFalse(all.isEmpty(), "There should be at least one citizen in the database");
    }

    @Test
    @Order(3)
    public void testFindByName() {
        List<Citizen> matches = citizenDAO.findByName("John");
        assertFalse(matches.isEmpty(), "There should be at least one citizen with the name John");
    }

    @Test
    @Order(4)
    public void testDelete() {
        List<Citizen> all = citizenDAO.findAll();
        assertFalse(all.isEmpty(), "There should be citizens to delete");

        Long idToDelete = all.get(0).getId();
        boolean deleted = citizenDAO.delete(idToDelete);
        assertTrue(deleted, "Citizen should be deleted");

        Citizen deletedCitizen = citizenDAO.findById(idToDelete);
        assertNull(deletedCitizen, "Deleted citizen should not be found");
    }

    @AfterAll
    public static void teardown() {
        CitizenDAO.closeEntityManagerFactory();
    }
}
