package itu.station.human;

import bean.CGenUtil;
import utilitaire.UtilDB;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.servlet.http.HttpServletRequest;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Path("/employes")
public class EmployeResource {


    private ObjectMapper objectMapper = new ObjectMapper();
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getEmployes(@Context HttpServletRequest request) {
        Connection connection = null;
        try {
            connection = new UtilDB().GetConn("gestion", "gestion");

            // Récupérer le rôle de l'utilisateur à partir des paramètres de la requête
            //String userRole = request.getParameter("role"); // Exemple de récupération d'un paramètre (si nécessaire)

            // Créez un objet Employe et récupérez la liste des employés
            Employe employe = new Employe();
            String jsonEmployes = employe.employesToJson(connection);

            return Response.ok(jsonEmployes).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Une erreur est survenue lors de la récupération des employés : " + e.getMessage()).build();
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
    @Path("/insert")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response insertEmployee(String requestData) {
        Connection connection = null;
        try {
            // Convertir la chaîne JSON en Map pour accéder aux valeurs
            Map<String, Object> employeeData = objectMapper.readValue(requestData, Map.class);

            // Créer une connexion
            connection = new UtilDB().GetConn("gestion", "gestion");
            connection.setAutoCommit(false);

            // Extraire les informations de l'employé
            //int idEmployee = (int) employeeData.get("idEmployee");
            String nom = (String) employeeData.get("nom");
            String prenom = (String) employeeData.get("prenom");
            String dateNaissanceee = (String) employeeData.get("dateNaissance");
            String dateEmbaucheeee = (String) employeeData.get("dateEmbauche");
            String poste = (String) employeeData.get("poste");
            String email = (String) employeeData.get("email");
            String telephone = (String) employeeData.get("telephone");
            String motDePasse = (String) employeeData.get("motDePasse");
            int departementId = (int) employeeData.get("departementId");
            java.sql.Date dateNaissancee;
            java.sql.Date dateEmbauche;
            dateNaissancee = java.sql.Date.valueOf(dateNaissanceee);
            dateEmbauche = java.sql.Date.valueOf(dateEmbaucheeee);
            Employe employe = new Employe();
            employe.setNom(nom);
            employe.setPrenom(prenom);
            employe.setDateEmbauche(dateEmbauche);
            employe.setDateNaissance(dateNaissancee);
            employe.setPoste(poste);
            employe.setEmail(email);
            employe.setTelephone(telephone);
            employe.setMotDePasse(motDePasse);
            employe.setDepartementId(departementId);


            // Insertion en base de données
            insertEmployeeIntoDB(employe, connection);

            // Commit de la transaction
            connection.commit();

            return Response.ok("{\"message\":\"Employé inséré avec succès\"}", MediaType.APPLICATION_JSON).build();

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
                    .entity("{\"error\":\"Erreur lors de l'insertion de l'employé\"}")
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
    private void insertEmployeeIntoDB(Employe employe, Connection connection) throws SQLException {
        // On suppose que la séquence "SEQ_EMPLOYE" existe dans la base de données
        String sql = "INSERT INTO EMPLOYE (ID_EMPLOYE, NOM, PRENOM, DATE_NAISSANCE, DATE_EMBAUCHE, POSTE, EMAIL, TELEPHONE, MOT_DE_PASSE, DEPARTEMENT_ID) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        int nextId = getNextId(connection);  // Obtenir le prochain ID via la séquence
        employe.setIdEmploye(nextId);  // Mettre à jour l'ID dans l'objet Employe avant l'insertion
        System.out.println(nextId);
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, nextId);  // Utiliser l'ID généré pour l'insertion
            pstmt.setString(2, employe.getNom());
            pstmt.setString(3, employe.getPrenom());
            pstmt.setDate(4, employe.getDateNaissance());
            pstmt.setDate(5, employe.getDateEmbauche());
            pstmt.setString(6, employe.getPoste());
            pstmt.setString(7, employe.getEmail());
            pstmt.setString(8, employe.getTelephone());
            pstmt.setString(9, employe.getMotDePasse());
            pstmt.setInt(10, employe.getDepartementId());

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLException("Failed to insert employee into database.");
            }
        }
    }

    private int getNextId(Connection connection) throws SQLException {
        String sql = "SELECT SEQ_EMPLOYE.NEXTVAL FROM dual";
        try (PreparedStatement pstmt = connection.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);  // Retourner l'ID généré par la séquence
            }
        }
        throw new SQLException("Unable to retrieve next ID from sequence");
    }
    @GET
    @Path("/{idEmploye}/competences")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCompetencesByEmployeId(@PathParam("idEmploye") int idEmploye, @Context HttpServletRequest request) {
        Connection connection = null;
        List<Competence> competences = new ArrayList<>();
        try {
            // Établir une connexion à la base de données
            connection = new UtilDB().GetConn("gestion", "gestion");

            // Requête SQL pour récupérer les compétences de l'employé par ID
            String sql = "SELECT * FROM Competence c "
                    + "JOIN Employe_Competence ec ON c.id_Competence = ec.id_Competence "
                    + "WHERE ec.id_Employe = ?";
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setInt(1, idEmploye);

            // Exécution de la requête
            ResultSet rs = stmt.executeQuery();

            // Remplir la liste de compétences avec les résultats de la requête
            while (rs.next()) {
                Competence competence = new Competence();
                competence.setIdCompetence(rs.getInt("idCompetence"));
                competence.setNom(rs.getString("nomCompetence"));
                // Vous pouvez ajouter d'autres champs selon votre modèle de données
                competences.add(competence);
            }

            // Si aucune compétence n'est trouvée
            if (competences.isEmpty()) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("{\"message\":\"Aucune compétence trouvée pour cet employé.\"}")
                        .build();
            }

            // Retourner la liste de compétences sous forme JSON
            ObjectMapper mapper = new ObjectMapper();
            return Response.ok(mapper.writeValueAsString(competences)).build();

        } catch (SQLException e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\":\"Erreur lors de la récupération des compétences.\"}")
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        } finally {
            // Fermeture de la connexion
            try {
                if (connection != null) connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
