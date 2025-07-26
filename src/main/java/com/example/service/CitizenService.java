package com.example.service;

import com.example.dao.CitizenDAO;
import com.example.entity.Citizen;
import com.example.exception.CitizenNotFoundException;
import com.example.exception.CitizenValidationException;
import java.util.List;
import java.util.stream.Collectors;

public class CitizenService {
    
    private final CitizenDAO citizenDAO;
    
    public CitizenService() {
        this.citizenDAO = new CitizenDAO();
    }
    
    public CitizenService(CitizenDAO citizenDAO) {
        this.citizenDAO = citizenDAO;
    }
    
    /**
     * Register a new citizen
     */
    public Citizen registerCitizen(Citizen citizen) {
        validateCitizen(citizen);
        
        // Business rule: Check for duplicate names (optional)
        if (isDuplicateName(citizen.getName(), citizen.getSurname())) {
            throw new CitizenValidationException(
                "A citizen with the same name and surname already exists");
        }
        
        // Ensure ID is null for new registrations
        citizen.setId(null);
        
        return citizenDAO.save(citizen);
    }
    
    /**
     * Update existing citizen information
     */
    public Citizen updateCitizen(Long id, Citizen citizen) {
        if (id == null) {
            throw new CitizenValidationException("Citizen ID cannot be null");
        }
        
        Citizen existingCitizen = getCitizenById(id);
        validateCitizen(citizen);
        
        // Business rule: Check for duplicate names excluding current citizen
        if (isDuplicateNameExcluding(citizen.getName(), citizen.getSurname(), id)) {
            throw new CitizenValidationException(
                "Another citizen with the same name and surname already exists");
        }
        
        citizen.setId(id);
        return citizenDAO.save(citizen);
    }
    
    /**
     * Get citizen by ID
     */
    public Citizen getCitizenById(Long id) {
        if (id == null) {
            throw new CitizenValidationException("Citizen ID cannot be null");
        }
        
        Citizen citizen = citizenDAO.findById(id);
        if (citizen == null) {
            throw new CitizenNotFoundException("Citizen with ID " + id + " not found");
        }
        
        return citizen;
    }
    
    /**
     * Get all registered citizens
     */
    public List<Citizen> getAllCitizens() {
        return citizenDAO.findAll();
    }
    
    /**
     * Search citizens by name (case-insensitive partial match)
     */
    public List<Citizen> searchCitizensByName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new CitizenValidationException("Search name cannot be empty");
        }
        
        return citizenDAO.findByName(name.trim());
    }
    
    /**
     * Get citizens by surname
     */
    public List<Citizen> getCitizensBySurname(String surname) {
        if (surname == null || surname.trim().isEmpty()) {
            throw new CitizenValidationException("Surname cannot be empty");
        }
        
        return citizenDAO.findAll().stream()
                .filter(c -> c.getSurname().toLowerCase().contains(surname.toLowerCase().trim()))
                .collect(Collectors.toList());
    }
    
    /**
     * Get citizens with full name matching
     */
    public List<Citizen> getCitizensByFullName(String name, String surname) {
        if (name == null || name.trim().isEmpty() || 
            surname == null || surname.trim().isEmpty()) {
            throw new CitizenValidationException("Both name and surname are required");
        }
        
        return citizenDAO.findAll().stream()
                .filter(c -> c.getName().equalsIgnoreCase(name.trim()) && 
                           c.getSurname().equalsIgnoreCase(surname.trim()))
                .collect(Collectors.toList());
    }
    
    /**
     * Remove citizen from registry
     */
    public boolean removeCitizen(Long id) {
        if (id == null) {
            throw new CitizenValidationException("Citizen ID cannot be null");
        }
        
        // Check if citizen exists first
        getCitizenById(id); // This will throw exception if not found
        
        return citizenDAO.delete(id);
    }
    
    /**
     * Get total count of registered citizens
     */
    public long getTotalCitizensCount() {
        return citizenDAO.findAll().size();
    }
    
    /**
     * Check if registry is empty
     */
    public boolean isRegistryEmpty() {
        return citizenDAO.findAll().isEmpty();
    }
    
    // Private helper methods
    
    private void validateCitizen(Citizen citizen) {
        if (citizen == null) {
            throw new CitizenValidationException("Citizen data cannot be null");
        }
        
        if (citizen.getName() == null || citizen.getName().trim().isEmpty()) {
            throw new CitizenValidationException("Citizen name is required");
        }
        
        if (citizen.getSurname() == null || citizen.getSurname().trim().isEmpty()) {
            throw new CitizenValidationException("Citizen surname is required");
        }
        
        // Business rules validation
        if (citizen.getName().trim().length() < 2) {
            throw new CitizenValidationException("Name must be at least 2 characters long");
        }
        
        if (citizen.getSurname().trim().length() < 2) {
            throw new CitizenValidationException("Surname must be at least 2 characters long");
        }
        
        if (citizen.getName().trim().length() > 50) {
            throw new CitizenValidationException("Name cannot exceed 50 characters");
        }
        
        if (citizen.getSurname().trim().length() > 50) {
            throw new CitizenValidationException("Surname cannot exceed 50 characters");
        }
        
        // Check for invalid characters (only letters, spaces, hyphens, apostrophes)
        if (!isValidName(citizen.getName().trim())) {
            throw new CitizenValidationException("Name contains invalid characters");
        }
        
        if (!isValidName(citizen.getSurname().trim())) {
            throw new CitizenValidationException("Surname contains invalid characters");
        }
        
        // Trim and normalize the names
        citizen.setName(normalizeName(citizen.getName()));
        citizen.setSurname(normalizeName(citizen.getSurname()));
    }
    
    private boolean isValidName(String name) {
        // Allow letters, spaces, hyphens, and apostrophes
        return name.matches("^[a-zA-ZÀ-ÿ\\s'-]+$");
    }
    
    private String normalizeName(String name) {
        // Trim, convert to proper case (first letter uppercase, rest lowercase)
        if (name == null || name.trim().isEmpty()) {
            return name;
        }
        
        String trimmed = name.trim();
        String[] words = trimmed.split("\\s+");
        StringBuilder normalized = new StringBuilder();
        
        for (int i = 0; i < words.length; i++) {
            if (i > 0) {
                normalized.append(" ");
            }
            String word = words[i];
            if (!word.isEmpty()) {
                normalized.append(Character.toUpperCase(word.charAt(0)))
                          .append(word.substring(1).toLowerCase());
            }
        }
        
        return normalized.toString();
    }
    
    private boolean isDuplicateName(String name, String surname) {
        return !getCitizensByFullName(name, surname).isEmpty();
    }
    
    private boolean isDuplicateNameExcluding(String name, String surname, Long excludeId) {
        List<Citizen> duplicates = getCitizensByFullName(name, surname);
        return duplicates.stream().anyMatch(c -> !c.getId().equals(excludeId));
    }
}