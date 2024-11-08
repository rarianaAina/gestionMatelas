package servlet;

import utilitaire.UtilDB;  // Import de la classe UtilDB pour la gestion de la connexion à la base de données

import java.io.IOException;
import java.sql.*;
import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

@WebServlet(name = "UpdateServlet", urlPatterns = {"/UpdateServlet"})
public class UpdateServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Connection connection = null;
        PreparedStatement stmt = null;

        // Récupérer l'ID du bloc et la nouvelle valeur du prix de revient depuis la JSP
        String idBloc = request.getParameter("idBloc");
        String nouveauPR = request.getParameter("nouveauPR");

        // Valider les paramètres
        if (idBloc == null || idBloc.isEmpty() || nouveauPR == null || nouveauPR.isEmpty()) {
            response.sendRedirect("error.jsp");  // Redirige vers une page d'erreur si les paramètres sont manquants
            return;
        }

        try {
            // Connexion à la base de données
            connection = new UtilDB().GetConn("mystation", "mystation");

            // Requête SQL pour mettre à jour le prix de revient
            String sql = "UPDATE BLOCS SET PRIXDEREVIENT = ? WHERE IDBLOC = ?";

            // Préparer la requête SQL
            stmt = connection.prepareStatement(sql);
            stmt.setDouble(1, Double.parseDouble(nouveauPR));  // Nouvelle valeur du prix de revient
            stmt.setString(2, idBloc);  // ID du bloc à mettre à jour

            // Exécuter la mise à jour
            int rowsUpdated = stmt.executeUpdate();

            if (rowsUpdated > 0) {
                // Si la mise à jour est réussie, rediriger vers la page de confirmation
                response.sendRedirect("updateSuccess.jsp");
            } else {
                // Si aucune ligne n'est mise à jour (bloc introuvable), rediriger vers une page d'erreur
                response.sendRedirect("error.jsp");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            response.sendRedirect("error.jsp");  // Redirige vers une page d'erreur en cas d'exception
        } finally {
            // Fermer les ressources
            try {
                if (stmt != null) stmt.close();
                if (connection != null) connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
