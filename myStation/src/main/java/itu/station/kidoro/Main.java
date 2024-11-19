/*
package itu.station.kidoro;

import utilitaire.UtilDB;

import java.sql.*;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {

    public static void main(String[] args) {
        // Exemple de longueurs, largeurs, hauteurs et prixReviendPRA
        double[] longueurs = {25, 30, 35, 40};  // Dimensions de test
        double[] largeurs = {6, 7, 8, 9};
        double[] hauteurs = {12, 13, 14, 15};
        double[] prixReviendPRA = {10000.0, 15000.0, 20000.0, 25000.0};  // Prix de revient pratique

        // Connexion à la base de données
        Connection conn = null;
        try {
            // Obtenir la connexion via UtilDB ou autre méthode
            conn = new UtilDB().GetConn("mystation", "mystation");

            // Créer une instance de BlocsVaovao pour appeler la méthode insererBlocs
            BlocsVaovao blocsVaovao = new BlocsVaovao();

            // Appeler la fonction insererBlocs
            blocsVaovao.insererBlocs(conn, longueurs, largeurs, hauteurs, prixReviendPRA);

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            // Assurez-vous de fermer la connexion après l'utilisation
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}*/

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
            String queryBlocs = "SELECT IDBLOCSVAOVAO, DATE_FABRICATION FROM BLOCSVAOVAO";
            try (PreparedStatement psBlocs = conn.prepareStatement(queryBlocs);
                 ResultSet rsBlocs = psBlocs.executeQuery()) {

                // Pour chaque bloc, calculer et mettre à jour le prix de revient
                while (rsBlocs.next()) {
                    String idBloc = rsBlocs.getString("IDBLOCSVAOVAO");
                    Date dateFabrication = rsBlocs.getDate("DATE_FABRICATION");

                    // Calculer le prix de revient pour ce bloc en appelant la méthode existante dans BlocsVaovao
                    double prixRevient = BlocsVaovao.calculerPrixRevientBloc(conn, dateFabrication, idBloc);
                    System.out.println("Prix de revient pour le bloc " + idBloc + " : " + prixRevient);

                    // Mettre à jour le prix de revient dans la base de données en appelant la méthode existante dans BlocsVaovao
                    BlocsVaovao.mettreAJourPrixRevientBloc(conn);
                }
            }

        } catch (SQLException e) {
            // Gérer les exceptions liées à la base de données
            logger.log(Level.SEVERE, "Erreur lors de la mise à jour des prix de revient des blocs", e);
        }
    }
}

