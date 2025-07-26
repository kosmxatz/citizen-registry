package com.example.resource;

import com.example.dao.CitizenDAO;
import com.example.entity.Citizen;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;


@Path("/citizens")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CitizenResource {
    
    private CitizenDAO citizenDAO = new CitizenDAO();
    
    @GET
    public Response getAllCitizens() {
        try {
            List<Citizen> citizens = citizenDAO.findAll();
            return Response.ok(citizens).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error retrieving citizens: " + e.getMessage())
                    .build();
        }
    }
    
    @GET
    @Path("/{id}")
    public Response getCitizenById(@PathParam("id") Long id) {
        try {
            Citizen citizen = citizenDAO.findById(id);
            if (citizen != null) {
                return Response.ok(citizen).build();
            } else {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("Citizen with ID " + id + " not found")
                        .build();
            }
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error retrieving citizen: " + e.getMessage())
                    .build();
        }
    }
    
    @GET
    @Path("/search")
    public Response searchByName(@QueryParam("name") String name) {
        try {
            if (name == null || name.trim().isEmpty()) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("Name parameter is required")
                        .build();
            }
            List<Citizen> citizens = citizenDAO.findByName(name);
            return Response.ok(citizens).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error searching citizens: " + e.getMessage())
                    .build();
        }
    }
    
    @POST
    public Response createCitizen(@Valid Citizen citizen) {
        try {
            // Ensure ID is null for new citizens
            citizen.setId(null);
            Citizen savedCitizen = citizenDAO.save(citizen);
            return Response.status(Response.Status.CREATED)
                    .entity(savedCitizen)
                    .build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Error creating citizen: " + e.getMessage())
                    .build();
        }
    }
    
    @PUT
    @Path("/{id}")
    public Response updateCitizen(@PathParam("id") Long id, @Valid Citizen citizen) {
        try {
            Citizen existingCitizen = citizenDAO.findById(id);
            if (existingCitizen == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("Citizen with ID " + id + " not found")
                        .build();
            }
            
            citizen.setId(id);
            Citizen updatedCitizen = citizenDAO.save(citizen);
            return Response.ok(updatedCitizen).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Error updating citizen: " + e.getMessage())
                    .build();
        }
    }
    
    @DELETE
    @Path("/{id}")
    public Response deleteCitizen(@PathParam("id") Long id) {
        try {
            boolean deleted = citizenDAO.delete(id);
            if (deleted) {
                return Response.status(Response.Status.NO_CONTENT).build();
            } else {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("Citizen with ID " + id + " not found")
                        .build();
            }
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error deleting citizen: " + e.getMessage())
                    .build();
        }
    }
}