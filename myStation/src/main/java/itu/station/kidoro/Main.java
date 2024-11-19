package itu.station.kidoro;

import utilitaire.UtilDB;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {

    // Créer un logger pour gérer les erreurs
    private static final Logger logger = Logger.getLogger(Main.class.getName());

    public static void main(String[] args) {
        // Créer une instance de UtilDB pour obtenir la connexion
        UtilDB utilDB = new UtilDB();

        // Utiliser try-with-resources pour gérer la connexion automatiquement
        try (Connection conn = utilDB.GetConn("mystation", "mystation")) {

            // Récupérer la liste des machines dans la table 'machines'
            String queryMachines = "SELECT idMachine FROM machines"; // Assurez-vous que 'machines' contient bien l'ID des machines
            try (PreparedStatement psMachines = conn.prepareStatement(queryMachines);
                 ResultSet rsMachines = psMachines.executeQuery()) {

                // Pour chaque machine, récupérer et afficher le prix de revient
                while (rsMachines.next()) {
                    String idMachine = rsMachines.getString("idMachine");

                    // Récupérer et afficher le prix de revient pour cette machine
                    double prixTotalMachine = prixDeReviensParMachine(conn, idMachine);
                    System.out.println("Le prix de revient pour la machine " + idMachine + " est : " + prixTotalMachine);
                }
            }

        } catch (SQLException e) {
            // Gérer les exceptions liées à la base de données
            logger.log(Level.SEVERE, "Erreur lors de la récupération des prix de revient des machines", e);
        }
    }

    // La méthode pour calculer le prix de revient pour chaque machine
    public static double prixDeReviensParMachine(Connection conn, String idMachine) throws SQLException {
        double prixTotal = 0.0;

        // Requête pour sommer les prix de revient associés à chaque idMachine
        String queryPrixTotalMachine =
                "SELECT SUM(PRIX_REVIENT) AS PrixTotal " +
                        "FROM BLOCSVAOVAO " +
                        "WHERE IDSOURCE = ?";  // Assurez-vous que la table BLOCSVAOVAO contient une colonne 'IDMACHINE'

        try (PreparedStatement ps = conn.prepareStatement(queryPrixTotalMachine)) {
            ps.setString(1, idMachine);  // L'ID de la machine

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    prixTotal = rs.getDouble("PrixTotal");
                }
            }
        }

        return prixTotal;
    }
}
