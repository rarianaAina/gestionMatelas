package servlet;

import utilitaire.UtilDB; // Importez la classe UtilDB pour la connexion

import java.io.IOException;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

@WebServlet(name = "EtatStockServlet", urlPatterns = {"/EtatStockServlet"})
public class EtatStockServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Connection connection = null;
        PreparedStatement stmtKidoro = null;
        PreparedStatement stmtTypeKidoro = null;
        PreparedStatement stmtPrixRevient = null;
        ResultSet rsKidoro = null;
        ResultSet rsTypeKidoro = null;
        ResultSet rsPrixRevient = null;

        try {
            // Connexion à la base de données
            connection = new UtilDB().GetConn("mystation", "mystation");

            // Requête pour récupérer les quantités des FU par typeKidoro
            String sqlKidoro = "SELECT IDTYPEKIDORO, SUM(QTE) AS total_qte FROM KIDORO GROUP BY IDTYPEKIDORO";
            stmtKidoro = connection.prepareStatement(sqlKidoro);
            rsKidoro = stmtKidoro.executeQuery();

            // Requête pour récupérer les volumes unitaires par typeKidoro
            String sqlTypeKidoro = "SELECT IDTYPEKIDORO, VOLUME, PRIX_VENTE FROM TYPEKIDORO";
            stmtTypeKidoro = connection.prepareStatement(sqlTypeKidoro);
            rsTypeKidoro = stmtTypeKidoro.executeQuery();

            // Requête pour récupérer la moyenne pondérée des prix de revient par typeKidoro
            String sqlPrixRevient = "SELECT IDTYPEKIDORO, SUM(PRIX_REVIENT * QTE) / NULLIF(SUM(QTE), 0) AS MOYENNE_PONDEREE_PRIX_REVIENT FROM V_PRIX_REVIENT_KIDORO GROUP BY IDTYPEKIDORO";
            stmtPrixRevient = connection.prepareStatement(sqlPrixRevient);
            rsPrixRevient = stmtPrixRevient.executeQuery();

            // Créer une map pour stocker les données à passer à la JSP
            Map<String, Map<String, Object>> stockData = new HashMap<>();

            // Ajouter les données de KIDORO
            while (rsKidoro.next()) {
                String typeKidoro = rsKidoro.getString("IDTYPEKIDORO");
                int totalQte = rsKidoro.getInt("total_qte");

                // Initialiser une map pour ce typeKidoro
                Map<String, Object> data = new HashMap<>();
                data.put("qte", totalQte);

                // Ajouter dans la map principale
                stockData.put(typeKidoro, data);
            }

            // Ajouter les données de TYPEKIDORO
            while (rsTypeKidoro.next()) {
                String typeKidoro = rsTypeKidoro.getString("IDTYPEKIDORO");
                Double volume = rsTypeKidoro.getDouble("VOLUME");
                String prixVente = rsTypeKidoro.getString("PRIX_VENTE");

                // Récupérer les données de stock existantes et les ajouter
                if (stockData.containsKey(typeKidoro)) {
                    Map<String, Object> data = stockData.get(typeKidoro);
                    data.put("volume", volume);
                    data.put("prixVente", prixVente);
                }
            }

            // Ajouter les données de prix de revient moyen pondéré
            while (rsPrixRevient.next()) {
                String typeKidoro = rsPrixRevient.getString("IDTYPEKIDORO");
                Double moyennePrixRevient = rsPrixRevient.getDouble("MOYENNE_PONDEREE_PRIX_REVIENT");

                // Récupérer les données de stock existantes et les ajouter
                if (stockData.containsKey(typeKidoro)) {
                    Map<String, Object> data = stockData.get(typeKidoro);
                    data.put("prixRevientMoyen", moyennePrixRevient);
                }
            }

            // Passer les données à la JSP
            request.setAttribute("stockData", stockData);
            RequestDispatcher dispatcher = request.getRequestDispatcher("index.jsp?but=pages/etatStock.jsp");
            dispatcher.forward(request, response);

        } catch (SQLException | IOException e) {
            e.printStackTrace();
            response.sendRedirect("error.jsp");
        } finally {
            try {
                if (rsKidoro != null) rsKidoro.close();
                if (rsTypeKidoro != null) rsTypeKidoro.close();
                if (rsPrixRevient != null) rsPrixRevient.close();
                if (stmtKidoro != null) stmtKidoro.close();
                if (stmtTypeKidoro != null) stmtTypeKidoro.close();
                if (stmtPrixRevient != null) stmtPrixRevient.close();
                if (connection != null) connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
