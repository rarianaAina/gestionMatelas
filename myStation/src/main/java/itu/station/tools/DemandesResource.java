package itu.station.tools;

import bean.CGenUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import utilitaire.UtilDB;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

@Path("/demandes")
public class DemandesResource {

    private ObjectMapper objectMapper = new ObjectMapper();

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getDemandes(@Context HttpServletRequest request) {
        Connection connection = null;
        try {
            connection = new UtilDB().GetConn("gestion", "gestion");

            // Récupérer le rôle de l'utilisateur à partir des paramètres de la requête
            String userRole = request.getParameter("role"); // Récupérer le rôle passé dans l'URL

            Demandes demandes = new Demandes();
            String jsonDemandes = demandes.demandesToJson(connection, userRole);
            return Response.ok(jsonDemandes).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Une erreur est survenue lors de la récupération des demandes : " + e.getMessage()).build();
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


    @POST
    @Path("/inserer")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response insertDemandes(String requestData) {
        Connection connection = null;
        try {
            // Convertir requestData en Map
            Map<String, Object> dataMap = objectMapper.readValue(requestData, Map.class);
            List<Map<String, Object>> demandesList = (List<Map<String, Object>>) dataMap.get("demandes");

            // Ouvrir une connexion
            connection = new UtilDB().GetConn("gestion", "gestion");
            connection.setAutoCommit(false);

            // Vérifier si demandesList n'est pas null et non vide
            if (demandesList != null && !demandesList.isEmpty()) {
                System.out.println("Données reçues : " + demandesList);

                for (Map<String, Object> demandeData : demandesList) {
                    // Créer une nouvelle instance de Demandes
                    Demandes demande = new Demandes();

                    // Récupérer les informations de chaque demande

                    int idEmployee = 2;
                    System.out.println(idEmployee);
                    String rubriques = (String) demandeData.get("rubriques");
                    int qte = (int) demandeData.get("qte");
                    String raison = (String) demandeData.get("raison");
                    String etat = (String) demandeData.get("etat");
                    String nomDepartement = (String) demandeData.get("departement");

                    // Définir les informations de l'objet Demandes
                    System.out.println(nomDepartement);
                    demande.setIdEmployee(idEmployee);
                    demande.setRubriques(rubriques);
                    demande.setQte(qte);
                    demande.setRaison(raison);
                    demande.setEtat(etat);
                    demande.setNomDepartement(nomDepartement);

                    // Ajouter un message avant l'insertion
                    System.out.println("Insertion des données : " + demande);

                    // Insérer la demande dans la base de données
                    insertDemande(demande, connection);
                }
            } else {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("{\"error\":\"Aucune demande reçue\"}")
                        .type(MediaType.APPLICATION_JSON)
                        .build();
            }

            // Valider la transaction
            connection.commit();
            return Response.ok("{\"message\":\"Demandes insérées avec succès\"}", MediaType.APPLICATION_JSON).build();

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
                    .entity("{\"error\":\"Erreur lors de l'insertion des demandes\"}")
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

    public void insertDemande(Demandes demande, Connection connection) throws SQLException {
        String sql = "INSERT INTO DEMANDES (IDDEMANDE, IDEMPLOYEE, RUBRIQUES, QTE, RAISON, ETAT, NOMDEPARTEMENT) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, "DMD" + String.valueOf(getNextId(connection))); // Utilisation d'un préfixe
            pstmt.setInt(2, demande.getIdEmployee());
            pstmt.setString(3, demande.getRubriques());
            pstmt.setInt(4, demande.getQte());
            pstmt.setString(5, demande.getRaison());
            pstmt.setString(6, demande.getEtat());
            pstmt.setString(7, demande.getNomDepartement());
            pstmt.executeUpdate();
        }
    }

    private int getNextId(Connection connection) throws SQLException {
        String sql = "SELECT GET_DEMANDE_SEQ.NEXTVAL FROM dual";
        try (PreparedStatement pstmt = connection.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        throw new SQLException("Unable to retrieve next ID from sequence");
    }

    @POST
    @Path("/traiter")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response traiterDemandes(List<Map<String, Object>> actions) {
        String role = null;

        // Récupérer le rôle à partir des actions (supposons qu'il soit envoyé dans chaque action)
        if (!actions.isEmpty()) {
            // Vous pouvez adapter cela selon votre structure JSON
            role = (String) actions.get(0).get("role");
        }

        if (role == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\":\"Rôle manquant\"}")
                    .build();
        }

        try (Connection connection = new UtilDB().GetConn("gestion", "gestion")) {
            String sql = "UPDATE DEMANDES SET etat = ? WHERE idDemande = ?";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                for (Map<String, Object> action : actions) {
                    String idDemande = (String) action.get("idDemande");
                    Boolean isRefuser = (Boolean) action.get("isRefuser");
                    Boolean isValider = (Boolean) action.get("isValider");

                    if (isRefuser != null && isRefuser) {
                        statement.setString(1, "-1"); // État refusé
                    } else if (isValider != null && isValider) {
                        String nouveauEtat = null;
                        switch (role) {
                            case "Chef_de_dep":
                                nouveauEtat = "1"; // État validé
                                break;
                            case "dep_achat":
                                nouveauEtat = "2"; // État validé
                                break;
                            case "finance":
                                nouveauEtat = "3"; // État validé
                                break;
                            case "dir_ge":
                                nouveauEtat = "4"; // État validé
                                break;
                            default:
                                return Response.status(Response.Status.FORBIDDEN)
                                        .entity("{\"error\":\"Rôle non reconnu\"}")
                                        .build();
                        }
                        statement.setString(1, nouveauEtat);
                    }

                    statement.setString(2, idDemande);
                    statement.executeUpdate();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\":\"Erreur lors de la mise à jour des demandes\"}")
                    .build();
        }

        return Response.ok("{\"mess.age\":\"Demandes traitées avec succès\"}").build();
    }
}




