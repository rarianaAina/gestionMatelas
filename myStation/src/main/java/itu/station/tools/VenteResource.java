package itu.station.tools;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import bean.CGenUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import utilitaire.UtilDB;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static itu.station.utils.TimeUtils.convertToSqlDate;

@Path("/vente")
public class VenteResource {
    private Date datee;

    public void setDatee(Date datee) {
        this.datee = datee;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getVentes() {
        Connection connection = null;
        try {
            connection = new UtilDB().GetConn("gallois", "gallois");
            Vente vente = new Vente();

            String jsonVentes = vente.ventesToJson(connection);
            return Response.ok(jsonVentes).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Une erreur est survenue lors de la récupération des produits : " + e.getMessage()).build();
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    @POST
    @Path("/achatA")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response postAchatValid(String requestData) {
        Map<String, Object> responseMap = new HashMap<>();

        ObjectMapper objectMapper = new ObjectMapper();
        Connection connection = null;
        System.out.println("Tonga teto");
        try {
            // Désérialiser le JSON en un objet Vente
            Vente vente = objectMapper.readValue(requestData, Vente.class);

            connection = new UtilDB().GetConn("gallois", "gallois");

            // Utiliser l'id de la vente
            String idVente = vente.getId(); // Assurez-vous que cette méthode existe dans la classe Vente
            String apresWhere = " AND id='" + idVente + "'";
            vente.Vente[] ventee = (vente.Vente[]) CGenUtil.rechercher(new vente.Vente(), null, null, connection, apresWhere);
            vente.VenteLib[] ventelibs = (vente.VenteLib[]) CGenUtil.rechercher(new vente.VenteLib(), null, null, connection, apresWhere);
            System.out.println("Tonga");
            //System.out.println(ventelibs);


            if (ventee.length > 0) {
                // Exécution de la requête
                ventee[0].setMontantttc(ventelibs[0].getMontantreste());
                ventee[0].setMontantttcAr(ventelibs[0].getMontantreste());
                ventee[0].setMontantht(ventelibs[0].getMontantreste());
                Double vola = ventee[0].getMontantttc();
                System.out.println(vola);
                ventee[0].validerObject("100", connection);
                ventee[0].payer("1060", connection);
                ventee[0].payerObject("1060", connection);

                ventee[0].isPaye();
            } else {
                responseMap.put("success", false);
                responseMap.put("message", "Aucune vente trouvée avec cet ID");
                return Response.status(Response.Status.NOT_FOUND)
                        .entity(responseMap)
                        .type(MediaType.APPLICATION_JSON)
                        .build();
            }

            responseMap.put("success", true);
            responseMap.put("message", "Vente traitée avec succès");
            return Response.ok(responseMap).type(MediaType.APPLICATION_JSON).build();

        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\":\"Erreur lors de l'insertion des données\"}")
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        } finally {
            // Fermez la connexion si elle a été ouverte
            if (connection != null) {
                try {
                    connection.close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    @POST
    @Path("/achat")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response postAchat(String requestData) {
        Map<String, Object> responseMap = new HashMap<>();
        responseMap.put("success", true);
        responseMap.put("message", "Données reçues avec succès");

        ObjectMapper objectMapper = new ObjectMapper();
        Connection connection = null;

        try {
            connection = new UtilDB().GetConn("gallois", "gallois");
            Vente vente = new Vente();
            Map<String, Object> dataMap = objectMapper.readValue(requestData, Map.class);
            System.out.println(requestData);

            List<Map<String, Object>> produits = (List<Map<String, Object>>) dataMap.get("produits");

            String datee = (String) dataMap.get("Daty");
            Date datyy = convertToSqlDate(datee, "fr");
            String designation = (String) dataMap.get("designation");
            int total = (int) dataMap.get("total");

            vente.setDaty(datyy);
            vente.setDesignation(designation);
            vente.setIdMagasin("PHARM001");
            vente.setEtat(11);
            vente.setIdClient("CLI000054");

            insertVente(vente);

            String idVente = vente.getId();

            if (produits != null && !produits.isEmpty()) {
                for (Map<String, Object> produitData : produits) {
                    VenteDetails venteD = new VenteDetails();

                    String idProduit = (String) produitData.get("produitId");
                    String nomProduit = (String) produitData.get("nom");
                    int qte = (int) produitData.get("quantite");
                    int puvente = (int) produitData.get("puVente");

                    venteD.setIdProduit(idProduit);
                    venteD.setIdVente(idVente);
                    venteD.setQte(qte);
                    venteD.setPuVente(puvente);
                    venteD.setIdDevise("AR");
                    venteD.setPu(puvente);
                    venteD.setDesignation(idProduit);

                    insertVenteDetails(venteD);
                    System.out.println(idVente);
                    // Affichage de la requête SQL avant l'exécution

                    String apresWhere = " AND id='" + idVente + "'";
                    vente.Vente[] ventee = (vente.Vente[]) CGenUtil.rechercher(new vente.Vente(), null, null, connection, apresWhere);
                    System.out.println("Tonga");
                    // Exécution de la requête

                    //ventee[0].setIdMagasin("PHARM001");

                    ventee[0].validerObject("1060", connection);

                    ventee[0].payer("1060", connection);
                    ventee[0].payerObject("1060",connection);
                    ventee[0].isPaye();

                    // ---- Gestion du stock ----
                    try {
                        connection = new UtilDB().GetConn("gallois", "gallois");
                        connection.setAutoCommit(false);

                        String selectSQL = "SELECT QTE_STOCK, QTE_VENDUE FROM PRODUIT WHERE ID = ?";
                        System.out.println("Exécution de la requête : " + selectSQL);

                        PreparedStatement pstmt = connection.prepareStatement(selectSQL);
                        pstmt.setString(1, idProduit);
                        ResultSet rs = pstmt.executeQuery();

                        if (rs.next()) {
                            int quantiteStock = rs.getInt("QTE_STOCK");
                            int quantiteDejaVendue = rs.getInt("QTE_VENDUE");

                            if (qte > quantiteStock) {
                                throw new SQLException("Quantité vendue dépasse la quantité en stock.");
                            }

                            int nouvelleQuantiteStock = quantiteStock - qte;
                            int nouvelleQuantiteVendue = quantiteDejaVendue + qte;

                            String updateSQL = "UPDATE PRODUIT SET QTE_STOCK = ?, QTE_VENDUE = ? WHERE ID = ?";
                            System.out.println("Exécution de la requête : " + updateSQL);

                            pstmt = connection.prepareStatement(updateSQL);
                            pstmt.setInt(1, nouvelleQuantiteStock);
                            pstmt.setInt(2, nouvelleQuantiteVendue);
                            pstmt.setString(3, idProduit);

                            pstmt.executeUpdate();
                            connection.commit();
                            System.out.println("Stock mis à jour pour le produit : " + idProduit);
                        } else {
                            throw new SQLException("Produit introuvable.");
                        }

                    } catch (SQLException e) {
                        if (connection != null) {
                            connection.rollback();
                        }
                        e.printStackTrace();
                        throw e;
                    } finally {
                        if (connection != null) {
                            connection.close();
                        }
                    }
                }
            }

            System.out.println("Total : " + total);

            String jsonResponse = objectMapper.writeValueAsString(responseMap);
            return Response.ok(jsonResponse, MediaType.APPLICATION_JSON).build();

        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\":\"Erreur lors de l'insertion des données\"}")
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        }
    }

    public void insertVente(Vente vente) {
        Connection c = null;
        PreparedStatement pstmt = null;

        try {
            // Obtenir la connexion
            c = new UtilDB().GetConn("gallois", "gallois");
            vente.construirePK(c);

            CGenUtil.save(vente, c);

            // Valider la transaction
            c.commit();

            System.out.println("Données insérées et transaction validée avec succès.");

        } catch (SQLException e) {
            e.printStackTrace();
            if (c != null) {
                try {
                    c.rollback();
                    System.out.println("Transaction annulée en raison d'une erreur.");
                } catch (SQLException rollbackEx) {
                    rollbackEx.printStackTrace();
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            try {
                if (pstmt != null) pstmt.close();
                if (c != null) c.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    public void insertVenteDetails(VenteDetails venteDetails) {
        Connection c = null;
        PreparedStatement pstmt = null;

        try {
            // Obtenir la connexion
            c = new UtilDB().GetConn("gallois", "gallois");
            venteDetails.construirePK(c);
            CGenUtil.save(venteDetails, c);
            // Valider la transaction
            c.commit();
            System.out.println("Données insérées et transaction validée avec succès.");

        } catch (SQLException e) {
            e.printStackTrace();
            if (c != null) {
                try {
                    c.rollback();
                    System.out.println("Transaction annulée en raison d'une erreur.");
                } catch (SQLException rollbackEx) {
                    rollbackEx.printStackTrace();
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            try {
                if (pstmt != null) pstmt.close();
                if (c != null) c.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    public void ajouterMouvement(MvtCaisse mvtCaisse) throws Exception {

        try {
            Connection c = new UtilDB().GetConn("gallois", "gallois");
            mvtCaisse.construirePK(c);
            CGenUtil.save(mvtCaisse, c);
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'ajout du mouvement de caisse : " + e.getMessage());
            throw e; // Relancer l'exception après l'avoir journalisée
        }

    }

}

