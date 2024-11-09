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

@Path("/competences")
public class CompetenceResource {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCompetences(@Context HttpServletRequest request) {
        Connection connection = null;
        try {
            connection = new UtilDB().GetConn("gestion", "gestion");
            Competence[] competences = (Competence[]) CGenUtil.rechercher(new Competence(), null, null, connection, "");
            ObjectMapper mapper = new ObjectMapper();
            return Response.ok(mapper.writeValueAsString(competences)).build();
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
    public Response insertCompetence(Competence competence) {
        Connection connection = null;
        try {
            connection = new UtilDB().GetConn("gestion", "gestion");
            CGenUtil.save(competence, connection);
            return Response.ok("{\"message\":\"Compétence insérée avec succès\"}", MediaType.APPLICATION_JSON).build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("{\"error\":\"Erreur lors de l'insertion de la compétence\"}").build();
        } finally {
            try {
                if (connection != null) connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
