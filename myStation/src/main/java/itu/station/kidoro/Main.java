package itu.station.kidoro;

import utilitaire.UtilDB;
import java.sql.*;
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

            // Récupérer la liste des blocs dans la table 'BLOCSVAOVAO'
            String queryBlocs = "SELECT IDBLOCSVAOVAO, DATE_FABRICATION FROM BLOCSVAOVAO ORDER BY DATE_FABRICATION, HEURE_FABRICATION";
            try (PreparedStatement psBlocs = conn.prepareStatement(queryBlocs);
                 ResultSet rsBlocs = psBlocs.executeQuery()) {

                // Pour chaque bloc, calculer et mettre à jour le prix de revient
                while (rsBlocs.next()) {
                    String idBloc = rsBlocs.getString("IDBLOCSVAOVAO");
                    Date dateFabrication = rsBlocs.getDate("DATE_FABRICATION");

                    try {
                        // Calculer le prix de revient et mettre à jour les quantités
                        double prixRevient = BlocsVaovao.calculerEtMettreAJourQuantites(conn, idBloc, dateFabrication);

                        // Mettre à jour le prix de revient dans la table 'BLOCSVAOVAO'
                        String updateQuery = "UPDATE BLOCSVAOVAO SET prix_revient = ? WHERE IDBLOCSVAOVAO = ?";
                        try (PreparedStatement psUpdate = conn.prepareStatement(updateQuery)) {
                            psUpdate.setDouble(1, prixRevient);
                            psUpdate.setString(2, idBloc);
                            psUpdate.executeUpdate();
                        }

                        System.out.println("Prix de revient pour le bloc " + idBloc + " : " + prixRevient);

                    } catch (IllegalArgumentException ex) {
                        // Si une exception personnalisée est levée, ignorer ce bloc et passer au suivant
                        System.err.println("Erreur pour le bloc " + idBloc + ": " + ex.getMessage());
                    }
                }
            }

        } catch (SQLException e) {
            // Gérer les exceptions liées à la base de données
            logger.log(Level.SEVERE, "Erreur lors de la mise à jour des prix de revient des blocs", e);
        }
    }
}
