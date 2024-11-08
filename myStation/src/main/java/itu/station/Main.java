package itu.station;

import itu.station.tools.GestionMagasin;

/*
package itu.station;

import itu.station.tools.Demandes;
import utilitaire.UtilDB;

import java.sql.Connection;

public class Mains {
    public static void main(String[] args) {
        Connection connection = null;

        try {
            // Établir une connexion à la base de données
            connection = new UtilDB().GetConn("gestion", "gestion");

            // Vérifier si la connexion est établie
            if (connection != null) {
                System.out.println("Connexion à la base de données réussie.");

                // Créer une instance de Demandes
                Demandes demandes = new Demandes();

                // Récupérer toutes les demandes
                //Demandes[] toutesDemandes = demandes.getAllDemandes(connection);

                // Afficher toutes les demandes
                //if (toutesDemandes != null) {
                    //for (Demandes demande : toutesDemandes) {
                        System.out.println(demande);
                    }
                } else {
                    System.out.println("Aucune demande à afficher.");
                }

                // Convertir les demandes en JSON
                String jsonDemandes = demandes.demandesToJson(connection);
                System.out.println("Demandes en JSON : " + jsonDemandes);
            } else {
                System.out.println("Échec de la connexion à la base de données.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // Fermer la connexion à la base de données
            if (connection != null) {
                try {
                    connection.close();
                    System.out.println("Connexion fermée.");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
*/
public class Main {
    public static void main(String[] args) {
        new GestionMagasin(); // Appel explicite pour éviter l'avertissement
        // Autres initialisations si nécessaire
    }
}

