package com.example.dao;

import com.example.entity.Citizen;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.persistence.TypedQuery;
import java.util.List;

public class CitizenDAO {
    
    private static final String PERSISTENCE_UNIT_NAME = "citizenPU";
    private static EntityManagerFactory emf;
    
    static {
        emf = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
    }
    
    public Citizen save(Citizen citizen) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            if (citizen.getId() == null) {
                em.persist(citizen);
            } else {
                citizen = em.merge(citizen);
            }
            em.getTransaction().commit();
            return citizen;
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw new RuntimeException("Error saving citizen", e);
        } finally {
            em.close();
        }
    }
    
    public Citizen findById(Long id) {
        EntityManager em = emf.createEntityManager();
        try {
            return em.find(Citizen.class, id);
        } finally {
            em.close();
        }
    }
    
    public List<Citizen> findAll() {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<Citizen> query = em.createQuery("SELECT c FROM Citizen c", Citizen.class);
            return query.getResultList();
        } finally {
            em.close();
        }
    }
    
    public List<Citizen> findByName(String name) {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<Citizen> query = em.createQuery(
                "SELECT c FROM Citizen c WHERE c.name LIKE :name", Citizen.class);
            query.setParameter("name", "%" + name + "%");
            return query.getResultList();
        } finally {
            em.close();
        }
    }
    
    public boolean delete(Long id) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            Citizen citizen = em.find(Citizen.class, id);
            if (citizen != null) {
                em.remove(citizen);
                em.getTransaction().commit();
                return true;
            }
            em.getTransaction().rollback();
            return false;
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw new RuntimeException("Error deleting citizen", e);
        } finally {
            em.close();
        }
    }
    
    public static void closeEntityManagerFactory() {
        if (emf != null && emf.isOpen()) {
            emf.close();
        }
    }
}