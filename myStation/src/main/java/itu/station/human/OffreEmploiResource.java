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

@Path("/offresEmploi")
public class OffreEmploiResource {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getOffres(@Context HttpServletRequest request) {
        Connection connection = null;
        try {
            connection = new UtilDB().GetConn("gestion", "gestion");
            OffreEmploi[] offres = (OffreEmploi[]) CGenUtil.rechercher(new OffreEmploi(), null, null, connection, "");
            ObjectMapper mapper = new ObjectMapper();
            return Response.ok(mapper.writeValueAsString(offres)).build();
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
    public Response insertOffreEmploi(OffreEmploi offreEmploi) {
        Connection connection = null;
        try {
            connection = new UtilDB().GetConn("gestion", "gestion");
            CGenUtil.save(offreEmploi, connection);
            return Response.ok("{\"message\":\"Offre d'emploi insérée avec succès\"}", MediaType.APPLICATION_JSON).build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("{\"error\":\"Erreur lors de l'insertion de l'offre d'emploi\"}").build();
        } finally {
            try {
                if (connection != null) connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
