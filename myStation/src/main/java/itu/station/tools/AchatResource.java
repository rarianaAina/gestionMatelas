package itu.station.tools;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.sql.Connection;
import java.sql.SQLException;
import utilitaire.UtilDB;
public class AchatResource {

    @POST
    @Path("/achat")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response processAchat(AchatRequest request) {
        // Affichage des données reçues
        System.out.println("Données reçues :");
        System.out.println("Total : " + request.getTotal());
        request.getProduits().forEach(produit -> {
            System.out.println("Produit ID : " + produit.getProduitId());
            System.out.println("Quantité : " + produit.getQuantite());
            System.out.println("PU Vente : " + produit.getPuVente());
        });

        // Retourner une réponse avec les données reçues
        return Response.ok("{\"success\": true, \"message\": \"Données reçues et affichées avec succès\"}")
                .build();
    }
}
