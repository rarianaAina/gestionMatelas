package itu.station.tools;

import bean.CGenUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import itu.station.tools.Commandes;
import utilitaire.UtilDB;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.servlet.http.HttpServletRequest;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

@Path("/commandes")
public class CommandesResource {

    private ObjectMapper objectMapper = new ObjectMapper();

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCommandes(@Context HttpServletRequest request) {
        Connection connection = null;
        try {
            connection = new UtilDB().GetConn("gestion", "gestion");

            // Récupérer le rôle de l'utilisateur à partir des paramètres de la requête
            String userRole = request.getParameter("role");

            Commandes commandes = new Commandes();

            String jsonCommandes = commandes.commandesToJson(connection);
            return Response.ok(jsonCommandes).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Une erreur est survenue lors de la récupération des commandes : " + e.getMessage()).build();
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
    @GET
    @Path("/date/{date}") // URL avec paramètre date
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCommandesByDate(@PathParam("date") String date) {
        Connection connection = null;
        try {
            connection = new UtilDB().GetConn("gestion", "gestion");

            // Appel de la méthode pour obtenir les commandes par date
            Commandes[] commandesByDate = getCommandesByDate(date, connection);
            String jsonCommandes = Commandes.toJson(commandesByDate);
            return Response.ok(jsonCommandes).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Une erreur est survenue lors de la récupération des commandes par date : " + e.getMessage()).build();
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
    @Path("/inserer")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response insertCommandes(String requestData) {
        Connection connection = null;
        try {
            Map<String, Object> dataMap = objectMapper.readValue(requestData, Map.class);
            List<Map<String, Object>> commandesList = (List<Map<String, Object>>) dataMap.get("commandes");

            connection = new UtilDB().GetConn("gestion", "gestion");
            connection.setAutoCommit(false);

            // Étape 1: Récupérer le dernier idCommande
            String lastIdSql = "SELECT COALESCE(MAX(\"idCommande\"), 0) FROM COMMANDES";
            int lastIdCommande = 0;

            try (PreparedStatement lastIdStmt = connection.prepareStatement(lastIdSql);
                 ResultSet rs = lastIdStmt.executeQuery()) {
                if (rs.next()) {
                    lastIdCommande = rs.getInt(1); // Récupère le dernier idCommande
                }
            }

            if (commandesList != null && !commandesList.isEmpty()) {
                String checkSql = "SELECT COUNT(*) FROM COMMANDES WHERE \"idCommande\" = ? AND \"dateCommande\" = ?";
                String insertSql = "INSERT INTO COMMANDES (\"idCommande\", \"EtatCommande\", \"Qte\", \"Produit\", \"dateCommande\") VALUES (?, ?, ?, ?, ?)";

                try (PreparedStatement checkStmt = connection.prepareStatement(checkSql);
                     PreparedStatement insertStmt = connection.prepareStatement(insertSql)) {

                    for (Map<String, Object> commandeData : commandesList) {
                        lastIdCommande++; // Incrémente pour générer un nouvel id
                        int etatCommande = 0; // Initialisation de l'état de la commande

                        // Vérification des données de la commande
                        if (commandeData.containsKey("qteCommande") && commandeData.containsKey("produit") && commandeData.containsKey("dateCommande")) {
                            int qteCommande = (int) commandeData.get("qteCommande");
                            String produit = (String) commandeData.get("produit");
                            String dateStr = (String) commandeData.get("dateCommande");

                            java.sql.Date dateCommande;
                            try {
                                dateCommande = java.sql.Date.valueOf(dateStr); // Assurez-vous que dateStr est au format "yyyy-MM-dd"
                            } catch (IllegalArgumentException e) {
                                return Response.status(Response.Status.BAD_REQUEST)
                                        .entity("{\"error\":\"Format de date incorrect. Utilisez yyyy-MM-dd.\"}")
                                        .type(MediaType.APPLICATION_JSON)
                                        .build();
                            }

                            // Journaliser les données avant l'insertion
                            System.out.println("Inserting commande: idCommande=" + lastIdCommande + ", etatCommande=" + etatCommande + ", qte=" + qteCommande + ", produit=" + produit + ", dateCommande=" + dateCommande);

                            // Vérifier si une commande avec le même id et date existe déjà
                            checkStmt.setInt(1, lastIdCommande);
                            checkStmt.setDate(2, dateCommande);
                            try (ResultSet rs = checkStmt.executeQuery()) {
                                if (rs.next() && rs.getInt(1) > 0) {
                                    System.out.println("Commande déjà existante, idCommande=" + lastIdCommande + ", dateCommande=" + dateCommande);
                                    continue; // Ignore si une commande avec le même id et date existe
                                }
                            }

                            // Insérer la nouvelle commande
                            insertStmt.setInt(1, lastIdCommande);
                            insertStmt.setInt(2, etatCommande);
                            insertStmt.setInt(3, qteCommande);
                            insertStmt.setString(4, produit);
                            insertStmt.setDate(5, dateCommande);
                            insertStmt.addBatch();
                        } else {
                            return Response.status(Response.Status.BAD_REQUEST)
                                    .entity("{\"error\":\"Données de commande mal formées\"}")
                                    .type(MediaType.APPLICATION_JSON)
                                    .build();
                        }
                    }
                    insertStmt.executeBatch();
                }
            } else {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("{\"error\":\"Aucune commande reçue\"}")
                        .type(MediaType.APPLICATION_JSON)
                        .build();
            }

            connection.commit();
            return Response.ok("{\"message\":\"Commandes insérées avec succès\"}", MediaType.APPLICATION_JSON).build();

        } catch (Exception e) {
            e.printStackTrace();
            if (connection != null) {
                try {
                    connection.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\":\"Erreur lors de l'insertion des commandes\"}")
                    .type(MediaType.APPLICATION_JSON)
                    .build();
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
    @Path("/traiter")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response traiterCommandes(List<Map<String, Object>> actions) {
        if (actions.isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\":\"Aucune action fournie\"}")
                    .build();
        }

        try (Connection connection = new UtilDB().GetConn("gestion", "gestion")) {
            String sql = "UPDATE COMMANDES SET \"EtatCommande\" = ? WHERE \"dateCommande\" = ?";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                for (Map<String, Object> action : actions) {
                    String dateDemandeStr = (String) action.get("dateDemande");

                    // Conversion de la chaîne de date en Date
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    Date dateDemande = new Date(dateFormat.parse(dateDemandeStr).getTime());

                    // Mettre à jour l'état de la commande à 1
                    statement.setString(1, "1"); // État validé
                    statement.setDate(2, dateDemande); // Utiliser setDate pour un objet Date
                    statement.executeUpdate();
                }
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\":\"Erreur lors de la mise à jour des demandes\"}")
                    .build();
        }

        return Response.ok("{\"message\":\"Demandes traitées avec succès\"}").build();
    }
    public Commandes[] getCommandesByDate(String date, Connection c) throws Exception {
        if (c == null) {
            c = new UtilDB().GetConn("gestion", "gestion");
        }
        String apresWhere = "and \"dateCommande\" = TO_DATE('" + date + "', 'YYYY-MM-DD')";
        // Récupérer toutes les commandes avec une condition vide
        return (Commandes[]) CGenUtil.rechercher(new Commandes(), null, null, c, apresWhere);
    }

}
