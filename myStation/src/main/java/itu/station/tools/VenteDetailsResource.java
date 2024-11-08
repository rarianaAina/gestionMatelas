package itu.station.tools;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import bean.CGenUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import utilitaire.UtilDB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@Path("/venteD")
public class VenteDetailsResource {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllVenteD() {
        Connection connection = null;
        try {
            connection = new UtilDB().GetConn("gallois", "gallois");
            //Produit produit = new Produit();
            VenteDetails venteD = new VenteDetails();
            String jsonVentesD = venteD.ventesDetailsToJson(connection);
            return Response.ok(jsonVentesD).build();
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



