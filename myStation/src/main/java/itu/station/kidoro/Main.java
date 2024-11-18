package itu.station.kidoro;

import utilitaire.UtilDB;

import java.sql.Connection;
import java.sql.SQLException;

public class Main {

    public static void main(String[] args) {
        // Créer une instance de UtilDB pour obtenir la connexion
        UtilDB utilDB = new UtilDB();
        Connection conn = null;

        // Si la connexion n'existe pas, obtenir une nouvelle connexion à la base de données
        if (conn == null) {
            conn = utilDB.GetConn("mystation", "mystation");
        }

        // Données pour les 4 premières lignes à insérer via le formulaire
        double[] longueurs = {22.0, 23.5, 21.0, 24.0};  // Longueurs des 4 blocs
        double[] largeurs = {5.5, 6.0, 5.8, 6.2};     // Largeurs des 4 blocs
        double[] hauteurs = {12.0, 14.0, 13.5, 11.5};  // Hauteurs des 4 blocs
        double[] prixReviendPRA = {15.0, 13.5, 14.0, 16.0};  // Prix de revient des 4 blocs

        // Essayer d'exécuter l'insertion des 4 premières lignes
        try {
            // Créer une instance de BlocsVaovao
            BlocsVaovao blocVaovao = new BlocsVaovao();

            // Insertion des 4 premières lignes dans la table BLOCSVAOVAO
            blocVaovao.insererBlocs(conn, longueurs, largeurs, hauteurs, prixReviendPRA);

            System.out.println("Les 4 premières lignes ont été insérées avec succès !");
        } catch (SQLException e) {
            // Gérer les exceptions liées à la base de données
            e.printStackTrace();
        } finally {
            // Fermer la connexion à la base de données si elle est ouverte
            try {
                if (conn != null && !conn.isClosed()) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
