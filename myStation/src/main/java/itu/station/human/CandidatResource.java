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

@Path("/candidats")
public class CandidatResource {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCandidats(@Context HttpServletRequest request) {
        Connection connection = null;
        try {
            connection = new UtilDB().GetConn("gestion", "gestion");
            Candidat[] candidats = (Candidat[]) CGenUtil.rechercher(new Candidat(), null, null, connection, "");
            ObjectMapper mapper = new ObjectMapper();
            return Response.ok(mapper.writeValueAsString(candidats)).build();
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
    public Response insertCandidat(Candidat candidat) {
        Connection connection = null;
        try {
            connection = new UtilDB().GetConn("gestion", "gestion");
            CGenUtil.save(candidat, connection);
            return Response.ok("{\"message\":\"Candidat inséré avec succès\"}", MediaType.APPLICATION_JSON).build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("{\"error\":\"Erreur lors de l'insertion du candidat\"}").build();
        } finally {
            try {
                if (connection != null) connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
