package itu.station.prelevement;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import utilitaire.UtilDB;

import java.util.HashMap;
import java.util.Map;
@Path("/prelevementsQuantity")
public class PrelevementQuantityResource {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getPrelevementQuantity() {
        Connection connection = null;
        try {
            connection = new UtilDB().GetConn();
            PrelevementQuantity prelevementQuantity = new PrelevementQuantity();
            String jsonPrelevementQuantity = prelevementQuantity.prelevementsQuantityToJson(connection);
            return Response.ok(jsonPrelevementQuantity).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Une erreur est survenue lors de la récupération des prelevements : " + e.getMessage()).build();
        } finally {
            // Fermez la connexion si elle a été ouverte
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
