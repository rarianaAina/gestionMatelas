package itu.station.human;

import bean.CGenUtil;
import utilitaire.UtilDB;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.servlet.http.HttpServletRequest;
import java.sql.Connection;
import java.sql.SQLException;

@Path("/employes")
public class EmployeResource {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getEmployes(@Context HttpServletRequest request) {
        Connection connection = null;
        try {
            connection = new UtilDB().GetConn("gestion", "gestion");
            Employe[] employes = (Employe[]) CGenUtil.rechercher(new Employe(), null, null, connection, "");
            ObjectMapper mapper = new ObjectMapper();
            return Response.ok(mapper.writeValueAsString(employes)).build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        } finally {
            try {
                if (connection != null) connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    @POST
    @Path("/inserer")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response insertEmploye(Employe employe) {
        Connection connection = null;
        try {
            connection = new UtilDB().GetConn("gestion", "gestion");
            CGenUtil.save(employe, connection);
            return Response.ok("{\"message\":\"Employé inséré avec succès\"}", MediaType.APPLICATION_JSON).build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("{\"error\":\"Erreur lors de l'insertion de l'employé\"}").build();
        } finally {
            try {
                if (connection != null) connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
