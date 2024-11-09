package servlet;

import utilitaire.UtilDB;  // Import de la classe UtilDB pour la gestion de la connexion à la base de données

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(name = "SuppressionServlet", urlPatterns = {"/supprimer"})
public class SuppressionServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Connection connection = null;
        PreparedStatement stmt = null;

        try {
            // Connexion à la base de données
            connection = new UtilDB().GetConn("mystation", "mystation");
            connection.setAutoCommit(false); // Désactiver l'auto-commit pour gérer la transaction manuellement

            // Suppression des données dans chaque table
            String[] tables = {"Restes", "Source", "Transformation", "Kidoro", "Blocs"};
            for (String table : tables) {
                String sql = "DELETE FROM " + table;
                stmt = connection.prepareStatement(sql);
                stmt.executeUpdate();
            }

            // Commit de la transaction si toutes les suppressions sont réussies
            connection.commit();
            response.sendRedirect("confirmation.jsp");  // Redirige vers la page de confirmation en cas de succès

        } catch (SQLException e) {
            // Gestion des erreurs et rollback de la transaction en cas de problème
            e.printStackTrace();
            if (connection != null) {
                try {
                    connection.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            response.sendRedirect("error.jsp");  // Redirige vers une page d'erreur en cas d'exception

        } finally {
            // Fermeture des ressources
            try {
                if (stmt != null) stmt.close();
                if (connection != null) connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
