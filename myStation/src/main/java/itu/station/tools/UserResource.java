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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Path("/auth")
public class UserResource {

/*
    @OPTIONS
    @Path("/login")
    @Produces(MediaType.APPLICATION_JSON)
    public Response optionsLogin() {
        return Response.ok()
                .build();
    }
*/

    @POST
    @Path("/login")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response login(UserLoginRequest loginRequest, @Context HttpServletRequest request) {
        if (loginRequest == null || loginRequest.getUsername() == null || loginRequest.getPassword() == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\":\"Données de connexion invalides\"}")
                    .build();
        }

        Connection connection = null;
        try {
            // Créer la connexion à la base de données
            connection = new UtilDB().GetConn("gestion", "gestion");

            // Chercher l'utilisateur dans la base de données
            User user = new User();
            user = user.findByUsername(loginRequest.getUsername(), connection);

            // Vérifier si l'utilisateur existe
            if (user == null) {
                return Response.status(Response.Status.UNAUTHORIZED)
                        .entity("{\"error\":\"Utilisateur non trouvé\"}")
                        .build();
            }

            // Vérification du mot de passe
            if (user.getPassword().equals(loginRequest.getPassword())) {
                // Créer une session pour l'utilisateur
                HttpSession session = request.getSession(true);
                System.out.println("Session créée : " + session.getId()); // Message de débogage
                session.setAttribute("user", user);
                System.out.println("Utilisateur ajouté à la session : " + user.getUsername()); // Message de débogage

                // Réponse de succès avec idEmployee inclus
                String jsonResponse = String.format(
                        "{\"message\":\"Connexion réussie\", \"role\":\"%s\", \"idEmployee\":%d}",
                        user.getRole(),
                        user.getIdEmployee() // Assurez-vous que cette méthode existe dans la classe User
                );
                return Response.ok(jsonResponse)
                        .build();
            } else {
                return Response.status(Response.Status.UNAUTHORIZED)
                        .entity("{\"error\":\"Mot de passe incorrect\"}")
                        .build();
            }
        } catch (Exception e) {
            e.printStackTrace();  // Pour obtenir une trace dans la console
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\":\"Erreur lors de la connexion à la base de données: " + e.getMessage() + "\"}")
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
    @Path("/logout")
    public Response logout(@Context HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        return Response.ok("{\"message\":\"Déconnexion réussie\"}")
                .header("Access-Control-Allow-Origin", "*")
                .build();
    }
}
