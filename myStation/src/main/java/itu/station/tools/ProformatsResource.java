package itu.station.tools;

import bean.CGenUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import utilitaire.UtilDB;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.sql.*;
import java.util.List;
import java.util.Map;

@Path("/proformats")
public class ProformatsResource {

    private ObjectMapper objectMapper = new ObjectMapper();

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getProformats(@Context HttpServletRequest request) {
        Connection connection = null;
        try {
            connection = new UtilDB().GetConn("gestion", "gestion");

            // Récupérer le rôle de l'utilisateur à partir des paramètres de la requête
            String userRole = request.getParameter("role");

            Proformats proformats = new Proformats();


            String jsonProformats = proformats.proformatsToJson(connection);
            return Response.ok(jsonProformats).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Une erreur est survenue lors de la récupération des proformats : " + e.getMessage()).build();
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
    public Response getProformatsByDate(@PathParam("date") String date) {
        Connection connection = null;
        try {
            connection = new UtilDB().GetConn("gestion", "gestion");

            // Appel de la méthode pour obtenir les proformats par date
            Proformats[] proformatsByDate = getProformatsByDate(date, connection);
            String jsonProformats = Proformats.toJson(proformatsByDate);
            return Response.ok(jsonProformats).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Une erreur est survenue lors de la récupération des proformats par date : " + e.getMessage()).build();
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
    public Response insertProformats(String requestData) {
        Connection connection = null;
        try {
            Map<String, Object> dataMap = objectMapper.readValue(requestData, Map.class);
            List<Map<String, Object>> proformatsList = (List<Map<String, Object>>) dataMap.get("proformats");

            connection = new UtilDB().GetConn("gestion", "gestion");
            connection.setAutoCommit(false);

            // Étape 1: Récupérer le dernier idProformat
            String lastIdSql = "SELECT COALESCE(MAX(\"idProformat\"), 0) FROM PROFORMATS";
            int lastIdProformat = 0;

            try (PreparedStatement lastIdStmt = connection.prepareStatement(lastIdSql);
                 ResultSet rs = lastIdStmt.executeQuery()) {
                if (rs.next()) {
                    lastIdProformat = rs.getInt(1); // Récupère le dernier idProformat
                }
            }

            if (proformatsList != null && !proformatsList.isEmpty()) {
                String checkSql = "SELECT COUNT(*) FROM PROFORMATS WHERE \"idProformat\" = ? AND \"dateDemande\" = ?";
                String insertSql = "INSERT INTO PROFORMATS (\"idProformat\", \"EtatProformat\", \"QteDemande\", \"Produit\", \"dateDemande\", \"PrixProduit\") VALUES (?, ?, ?, ?, ?, ?)";

                try (PreparedStatement checkStmt = connection.prepareStatement(checkSql);
                     PreparedStatement insertStmt = connection.prepareStatement(insertSql)) {

                    for (Map<String, Object> proformatData : proformatsList) {
                        // Incrémenter l'idProformat pour chaque nouveau proformat
                        lastIdProformat++; // Incrémente pour générer un nouvel id
                        int etatProformat = (int) proformatData.get("etatProformat");
                        int qteDemande = (int) proformatData.get("qteDemande");
                        String produit = (String) proformatData.get("produit");
                        String dateStr = (String) proformatData.get("dateDemande");

                        java.sql.Date dateDemande;
                        try {
                            dateDemande = java.sql.Date.valueOf(dateStr); // Assurez-vous que dateStr est au format "yyyy-MM-dd"
                        } catch (IllegalArgumentException e) {
                            return Response.status(Response.Status.BAD_REQUEST)
                                    .entity("{\"error\":\"Format de date incorrect. Utilisez yyyy-MM-dd.\"}")
                                    .type(MediaType.APPLICATION_JSON)
                                    .build();
                        }

                        checkStmt.setInt(1, lastIdProformat);
                        checkStmt.setDate(2, dateDemande);
                        try (ResultSet rs = checkStmt.executeQuery()) {
                            if (rs.next() && rs.getInt(1) > 0) {
                                continue; // Ignore si un proformat avec le même id et date existe
                            }
                        }

                        insertStmt.setInt(1, lastIdProformat);
                        insertStmt.setInt(2, etatProformat);
                        insertStmt.setInt(3, qteDemande);
                        insertStmt.setString(4, produit);
                        insertStmt.setDate(5, dateDemande);
                        insertStmt.setDouble(6, 0); // Prix initial à 0
                        insertStmt.addBatch();
                    }
                    insertStmt.executeBatch();
                }
            } else {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("{\"error\":\"Aucun proformat reçu\"}")
                        .type(MediaType.APPLICATION_JSON)
                        .build();
            }

            connection.commit();
            return Response.ok("{\"message\":\"Proformats insérés avec succès\"}", MediaType.APPLICATION_JSON).build();

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
                    .entity("{\"error\":\"Erreur lors de l'insertion des proformats\"}")
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

    public void insertProformat(Proformats proformat, Connection connection) throws SQLException {
        String sql = "INSERT INTO PROFORMATS (idProformat, EtatProformat, QteDemande, Produit, dateDemande, PrixProduit) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, proformat.getIdProformat());
            pstmt.setInt(2, proformat.getEtatProformat());
            pstmt.setInt(3, proformat.getQteDemande());
            pstmt.setString(4, proformat.getProduit());
            pstmt.setDate(5, proformat.getDateDemande());
            pstmt.setString(6, proformat.getPrixProduit());
            pstmt.executeUpdate();
        }
    }
    @POST
    @Path("/updateEtatFinance")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateEtatFinance(List<Map<String, Object>> actions) {
        String role = null;

        if (!actions.isEmpty()) {
            role = (String) actions.get(0).get("role");
        }

        if (role == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\":\"Rôle manquant\"}")
                    .build();
        }

        try (Connection connection = new UtilDB().GetConn("gestion", "gestion")) {
            String sql = "UPDATE PROFORMATS SET \"EtatProformat\" = ? WHERE \"dateDemande\" = TO_DATE(?, 'YYYY-MM-DD')";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                for (Map<String, Object> action : actions) {
                    int etatProformat2 = 2;
                    int etatProformat = 1;
                    String dateProformat = (String) action.get("dateProformatt");
                    Boolean isRefuser = (Boolean) action.get("isRefuser");
                    Boolean isValider = (Boolean) action.get("isValider");
                    System.out.println(isValider);
                    System.out.println(isRefuser);
                    System.out.println(dateProformat);

                    if (isRefuser != null && isRefuser) {
                        statement.setInt(1, etatProformat); // État refusé
                    } else if (isValider != null && isValider) {
                        statement.setInt(1, etatProformat2);
                    }



                    statement.setString(2, dateProformat);
                    statement.addBatch(); // Ajoute l'instruction au batch
                }

                // Exécute toutes les instructions du batch
                statement.executeBatch();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\":\"Erreur lors de la mise à jour des prix\"}")
                    .build();
        }

        return Response.ok("{\"message\":\"Prix mis à jour avec succès\"}").build();
    }

    @POST
    @Path("/updatePrix")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updatePrixProformats(List<Map<String, Object>> actions) {
        String role = null;

        if (!actions.isEmpty()) {
            role = (String) actions.get(0).get("role");
        }

        if (role == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\":\"Rôle manquant\"}")
                    .build();
        }

        try (Connection connection = new UtilDB().GetConn("gestion", "gestion")) {
            // Préparation des requêtes pour récupérer et mettre à jour les données
            String selectSql = "SELECT \"EtatProformat\" FROM PROFORMATS WHERE \"idProformat\" = ?";
            String updatePrixSql = "UPDATE PROFORMATS SET \"PrixProduit\" = ? WHERE \"idProformat\" = ?";
            String updateEtatAndPrixSql = "UPDATE PROFORMATS SET \"PrixProduit\" = ?, \"EtatProformat\" = ? WHERE \"idProformat\" = ?";

            try (PreparedStatement selectStatement = connection.prepareStatement(selectSql);
                 PreparedStatement updatePrixStatement = connection.prepareStatement(updatePrixSql);
                 PreparedStatement updateEtatAndPrixStatement = connection.prepareStatement(updateEtatAndPrixSql)) {

                for (Map<String, Object> action : actions) {
                    int idProformat = (int) action.get("idProformat");

                    // Récupération de "prixProduit" en vérifiant son type
                    Object prixProduitObj = action.get("prixProduit");
                    int prixProduit;

                    if (prixProduitObj instanceof String) {
                        prixProduit = Integer.parseInt((String) prixProduitObj);
                    } else if (prixProduitObj instanceof Integer) {
                        prixProduit = (Integer) prixProduitObj;
                    } else {
                        return Response.status(Response.Status.BAD_REQUEST)
                                .entity("{\"error\":\"prixProduit est de type invalide\"}")
                                .build();
                    }

                    // Vérification de l'état actuel dans la base de données
                    selectStatement.setInt(1, idProformat);
                    ResultSet resultSet = selectStatement.executeQuery();
                    if (resultSet.next()) {
                        int etatProformatActuel = resultSet.getInt("EtatProformat");

                        // Si EtatProformat est 1 ou 2, on met uniquement à jour le prix
                        if (etatProformatActuel == 1 || etatProformatActuel == 2) {
                            updatePrixStatement.setDouble(1, prixProduit);
                            updatePrixStatement.setInt(2, idProformat);
                            updatePrixStatement.addBatch();
                        } else {
                            // Sinon, on met à jour l'état et le prix
                            int nouvelEtatProformat = 1; // Définir l'état souhaité
                            updateEtatAndPrixStatement.setDouble(1, prixProduit);
                            updateEtatAndPrixStatement.setInt(2, nouvelEtatProformat);
                            updateEtatAndPrixStatement.setInt(3, idProformat);
                            updateEtatAndPrixStatement.addBatch();
                        }
                    } else {
                        return Response.status(Response.Status.BAD_REQUEST)
                                .entity("{\"error\":\"Proformat introuvable pour l'ID donné\"}")
                                .build();
                    }
                }

                // Exécute tous les batchs pour les mises à jour
                updatePrixStatement.executeBatch();
                updateEtatAndPrixStatement.executeBatch();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\":\"Erreur lors de la mise à jour des prix\"}")
                    .build();
        }

        return Response.ok("{\"message\":\"Prix mis à jour avec succès\"}").build();
    }

    public Proformats[] getProformatsByDate(String date, Connection c) throws Exception {
        if (c == null) {
            c = new UtilDB().GetConn("gestion", "gestion");
        }
        String apresWhere = "and \"dateDemande\" = TO_DATE('" + date + "', 'YYYY-MM-DD')";
        // Récupérer tous les proformats avec une condition vide
        return (Proformats[]) CGenUtil.rechercher(new Proformats(), null, null, c, apresWhere);
    }

}
