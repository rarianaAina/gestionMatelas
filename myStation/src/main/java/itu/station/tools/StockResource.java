package itu.station.tools;

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
@Path("/stocks")
public class StockResource {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getStock() {
        Connection connection = null;
        try {
            connection = new UtilDB().GetConn("gestion", "gestion");
            Stock stock = new Stock();
            String jsonStock = stock.stockToJson(connection);
            return Response.ok(jsonStock).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Une erreur est survenue lors de la récupération des produits : " + e.getMessage()).build();
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