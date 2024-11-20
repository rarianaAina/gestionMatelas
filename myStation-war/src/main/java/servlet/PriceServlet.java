package servlet;

import utilitaire.UtilDB;
import java.io.IOException;
import java.sql.*;
import java.util.*;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/PriceServlet")
public class PriceServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Créer une instance de UtilDB pour obtenir la connexion
        UtilDB utilDB = new UtilDB();

        // Créer une liste pour stocker les prix de revient
        List<Map<String, Object>> prixMachines = new ArrayList<>();

        try (Connection conn = utilDB.GetConn("mystation", "mystation")) {
            // Récupérer la liste des machines
            String queryMachines = "SELECT idMachine FROM machines";
            try (PreparedStatement psMachines = conn.prepareStatement(queryMachines);
                 ResultSet rsMachines = psMachines.executeQuery()) {

                // Pour chaque machine, récupérer le prix de revient
                while (rsMachines.next()) {
                    String idMachine = rsMachines.getString("idMachine");
                    double prixTotalMachine = prixDeReviensParMachine(conn, idMachine);
                    double prixPratiqueMachine = prixPratiqueParMachine(conn, idMachine);

                    // Créer un map pour chaque machine et son prix
                    Map<String, Object> machineData = new HashMap<>();
                    machineData.put("idMachine", idMachine);
                    machineData.put("prixTotal", prixTotalMachine);
                    machineData.put("prixPratique", prixPratiqueMachine);

                    // Calculer l'écart et ajouter à la map
                    double ecart = prixTotalMachine - prixPratiqueMachine;
                    machineData.put("ecart", ecart);

                    prixMachines.add(machineData);
                }
            }

            // Trier la liste par écart croissant (moins d'écart en premier)
            prixMachines.sort(Comparator.comparingDouble(machine -> (double) machine.get("ecart")));

            // Passer les données à la JSP via l'objet request
            request.setAttribute("prixMachines", prixMachines);

            // Rediriger vers la page JSP
            request.getRequestDispatcher("index.jsp?but=pages/afficherPrix.jsp").forward(request, response);

        } catch (SQLException e) {
            e.printStackTrace();
            response.sendRedirect("error.jsp");
        }
    }

    // La méthode pour calculer le prix de revient pour chaque machine
    private double prixDeReviensParMachine(Connection conn, String idMachine) throws SQLException {
        double prixTotal = 0.0;
        String queryPrixTotalMachine =
                "SELECT SUM(PRIX_REVIENT) AS PrixTotal " +
                        "FROM BLOCSVAOVAO " +
                        "WHERE IDSOURCE = ?";  // Requête pour récupérer le prix de revient total

        try (PreparedStatement ps = conn.prepareStatement(queryPrixTotalMachine)) {
            ps.setString(1, idMachine);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    prixTotal = rs.getDouble("PrixTotal");
                }
            }
        }
        return prixTotal;
    }

    // La méthode pour calculer le prix de revient pratique pour chaque machine
    private double prixPratiqueParMachine(Connection conn, String idMachine) throws SQLException {
        double prixPratique = 0.0;
        String queryPrixPratiqueMachine =
                "SELECT SUM(PRIX_REVIENT_PRA) AS PrixPratique " +
                        "FROM BLOCSVAOVAO " +
                        "WHERE IDSOURCE = ?";  // Requête pour récupérer le prix de revient pratique

        try (PreparedStatement ps = conn.prepareStatement(queryPrixPratiqueMachine)) {
            ps.setString(1, idMachine);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    prixPratique = rs.getDouble("PrixPratique");
                }
            }
        }
        return prixPratique;
    }
}
