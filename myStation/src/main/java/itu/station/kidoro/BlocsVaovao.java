package itu.station.kidoro;

import bean.CGenUtil;
import bean.ClassMAPTable;
import utilitaire.UtilDB;

import java.sql.*;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BlocsVaovao extends ClassMAPTable {

    private String idBloc;
    private double longueur;
    private double largeur;
    private double hauteur;
    private double volume;
    private double prixDeRevient;
    private Date dateFabrication;
    private String heureFabrication;
    private String idSource;
    private String prixRevientPra;

    public BlocsVaovao(String idBloc, double longueur, double largeur, double hauteur, double volume, double prixDeRevient,
                       Date dateFabrication, String heureFabrication, String idSource, String prixRevientPra) {
        this.idBloc = idBloc;
        this.longueur = longueur;
        this.largeur = largeur;
        this.hauteur = hauteur;
        this.volume = volume;
        this.prixDeRevient = prixDeRevient;
        this.dateFabrication = dateFabrication;
        this.heureFabrication = heureFabrication;
        this.idSource = idSource;
        this.prixRevientPra = prixRevientPra;
    }

    public BlocsVaovao() {
        this.setNomTable("BLOCSVAOVAO");
    }

    @Override
    public String getTuppleID() {
        return idBloc;
    }

    @Override
    public String getAttributIDName() {
        return "idBloc";
    }

    // Getters et setters
    public String getIdBloc() {
        return idBloc;
    }

    public void setIdBloc(String idBloc) {
        this.idBloc = idBloc;
    }

    public double getLongueur() {
        return longueur;
    }

    public void setLongueur(double longueur) {
        this.longueur = longueur;
    }

    public double getLargeur() {
        return largeur;
    }

    public void setLargeur(double largeur) {
        this.largeur = largeur;
    }

    public double getHauteur() {
        return hauteur;
    }

    public void setHauteur(double hauteur) {
        this.hauteur = hauteur;
    }

    public double getVolume() {
        return volume;
    }

    public void setVolume(double volume) {
        this.volume = volume;
    }

    public double getPrixDeRevient() {
        return prixDeRevient;
    }

    public void setPrixDeRevient(double prixDeRevient) {
        this.prixDeRevient = prixDeRevient;
    }

    public Date getDateFabrication() {
        return dateFabrication;
    }

    public void setDateFabrication(Date dateFabrication) {
        this.dateFabrication = dateFabrication;
    }

    public String getHeureFabrication() {
        return heureFabrication;
    }

    public void setHeureFabrication(String heureFabrication) {
        this.heureFabrication = heureFabrication;
    }

    public String getIdSource() {
        return idSource;
    }

    public void setIdSource(String idSource) {
        this.idSource = idSource;
    }

    public String getPrixRevientPra() {
        return prixRevientPra;
    }

    public void setPrixRevientPra(String prixRevientPra) {
        this.prixRevientPra = prixRevientPra;
    }

    // Fonction pour obtenir tous les blocs
    public BlocsVaovao[] getAllBlocsVaovao(Connection c) throws Exception {
        if (c == null) {
            c = new UtilDB().GetConn("mystation", "mystation");
        }
        return (BlocsVaovao[]) CGenUtil.rechercher(new BlocsVaovao(), null, null, c, "");
    }
        // Méthode statique pour calculer le prix de revient d'un bloc, en prenant en compte les quantités et les dates
        public static double calculerPrixRevientBloc(Connection conn, Date dateFabrication, String idBloc) throws SQLException {
            double prixRevientTotal = 0.0;

            // Requête pour récupérer le volume du bloc à partir de la table BlocsVaovao
            String queryVolumeBloc = "SELECT Volume FROM BlocsVaovao WHERE IDBLOCSVAOVAO = ?";
            double volumeBloc = 0.0;
            try (PreparedStatement psVolumeBloc = conn.prepareStatement(queryVolumeBloc)) {
                psVolumeBloc.setString(1, idBloc);
                try (ResultSet rsVolumeBloc = psVolumeBloc.executeQuery()) {
                    if (rsVolumeBloc.next()) {
                        volumeBloc = rsVolumeBloc.getDouble("Volume");
                    } else {
                        throw new SQLException("Volume du bloc introuvable pour l'idBloc: " + idBloc);
                    }
                }
            }

            // Requête pour récupérer les composants nécessaires pour la fabrication du bloc
            String queryComposants = "SELECT idComposants, qte FROM Composants";
            try (PreparedStatement psComposants = conn.prepareStatement(queryComposants);
                 ResultSet rsComposants = psComposants.executeQuery()) {

                // Pour chaque composant, récupérer les achats et calculer le prix de revient
                while (rsComposants.next()) {
                    String idComposant = rsComposants.getString("idComposant");
                    int qteNecessaire = rsComposants.getInt("qte");

                    // Calculer le prix de revient pour ce composant
                    double prixComposant = 0.0;
                    int qteRestante = qteNecessaire;

                    // Requête pour récupérer les achats disponibles pour ce composant avant ou à la date de fabrication
                    String queryAchats = "SELECT PUAchat, qte, DateAchat FROM Achats WHERE idComposants = ? AND DateAchat <= ? ORDER BY DateAchat ASC";
                    try (PreparedStatement psAchats = conn.prepareStatement(queryAchats)) {
                        psAchats.setString(1, idComposant);
                        psAchats.setDate(2, dateFabrication);

                        try (ResultSet rsAchats = psAchats.executeQuery()) {
                            // Traiter les achats successifs pour ce composant
                            while (rsAchats.next() && qteRestante > 0) {
                                double prixAchat = rsAchats.getDouble("PUAchat");
                                int qteAchetee = rsAchats.getInt("qte");

                                // Si la quantité achetée est suffisante pour couvrir la quantité restante
                                if (qteAchetee >= qteRestante) {
                                    prixComposant += prixAchat * qteRestante;
                                    qteRestante = 0; // La quantité nécessaire est maintenant satisfaite
                                } else {
                                    prixComposant += prixAchat * qteAchetee;
                                    qteRestante -= qteAchetee; // Réduire la quantité restante
                                }
                            }
                        }
                    }

                    // Ajuster le prix du composant en fonction du volume du bloc
                    prixComposant *= volumeBloc;

                    // Ajouter le prix de ce composant au prix total du bloc
                    prixRevientTotal += prixComposant;
                }
            }

            return prixRevientTotal;
        }
    public static double prixDeReviensParSource(Connection conn, String idSource, Date dateFabrication) throws SQLException {
        double prixTotal = 0.0;

        // Requête pour récupérer les blocs associés à l'idSource
        String queryBlocs = "SELECT IDBLOCSVAOVAO FROM BLOCSVAOVAO WHERE IDSOURCE = ?";

        try (PreparedStatement psBlocs = conn.prepareStatement(queryBlocs)) {
            psBlocs.setString(1, idSource);
            try (ResultSet rsBlocs = psBlocs.executeQuery()) {

                while (rsBlocs.next()) {
                    String idBloc = rsBlocs.getString("IDBLOCSVAOVAO");

                    // Calculer le prix de revient pour ce bloc (le volume est déjà pris en compte dans cette fonction)
                    double prixBloc = calculerPrixRevientBloc(conn,dateFabrication, idBloc);

                    // Ajouter le prix de revient du bloc au total
                    prixTotal += prixBloc;
                }
            }
        }

        return prixTotal;
    }

    public void insererBlocs(Connection conn, double[] longueurs, double[] largeurs, double[] hauteurs, double[] prixReviendPRA) throws SQLException {
        // Assurez-vous que la connexion est valide
        if (conn == null) {
            conn = new UtilDB().GetConn("mystation", "mystation");
        }

        // Insérer les 4 premières lignes depuis les données du formulaire
        double prixRevientMoyenne = insererPremieresLignes(conn, longueurs, largeurs, hauteurs, prixReviendPRA);

        // Générateur de nombres aléatoires
        Random random = new Random();

        // Insérer les 999 996 autres lignes avec un prix de revient aléatoire autour de la moyenne
        String insertQuery = "INSERT INTO BLOCSVAOVAO (IDBLOCSVAOVAO, LONGUEUR, LARGEUR, HAUTEUR, VOLUME, PRIX_REVIENT, DATE_FABRICATION, HEURE_FABRICATION, IDSOURCE, PRIX_REVIENT_PRA) "
                + "VALUES (blocsvaovao_seq.NEXTVAL, ?, ?, ?, ?, ?, CURRENT_DATE, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(insertQuery)) {
            for (int i = 0; i < 999996; i++) {
                // Générer des valeurs aléatoires pour les dimensions
                double longueur = 20 + (random.nextDouble() * 5);  // entre 20 et 25
                double largeur = 5 + (random.nextDouble() * 2);   // entre 5 et 7
                double hauteur = 10 + (random.nextDouble() * 5);   // entre 10 et 15

                // Calculer le volume du bloc
                double volume = longueur * largeur * hauteur;

                // Calculer un prix de revient aléatoire autour de la moyenne, avec une variation de -10% à +10%
                double variation = 1 + (random.nextDouble() * 0.2 - 0.1);  // variation entre -10% et +10%
                double prixRevientPra = prixRevientMoyenne * variation;

                // Obtenir l'heure actuelle
                String heureFabrication = LocalTime.now().toString().substring(0, 8); // HH:MM:SS format

                // Insérer la ligne dans la table
                ps.setDouble(1, longueur);
                ps.setDouble(2, largeur);
                ps.setDouble(3, hauteur);
                ps.setDouble(4, volume);
                ps.setDouble(5, prixRevientPra);
                ps.setString(6, heureFabrication);  // Heure actuelle
                ps.setString(7, "ID_12345");  // Exemple pour IDSource, vous pouvez ajuster selon votre logique
                ps.setString(8, prixRevientPra + "_PRA"); // Exemple pour PRIX_REVIENT_PRA, ajustez selon votre logique

                ps.addBatch();

                // Exécuter le batch tous les 1000 inserts
                if ((i + 1) % 1000 == 0) {
                    ps.executeBatch();
                }
            }
            // Exécuter les restes du batch
            ps.executeBatch();
        }
    }

    double insererPremieresLignes(Connection conn, double[] longueurs, double[] largeurs, double[] hauteurs, double[] prixReviendPRA) throws SQLException {
        // Assurez-vous que les tableaux contiennent bien 4 éléments
        if (longueurs.length != 4 || largeurs.length != 4 || hauteurs.length != 4 || prixReviendPRA.length != 4) {
            throw new IllegalArgumentException("Les tableaux doivent contenir 4 éléments.");
        }

        double prixRevientMoyenne = 0.0;

        // Insérer les 4 premières lignes avec les données du formulaire
        String insertQuery = "INSERT INTO BLOCSVAOVAO (IDBLOCSVAOVAO, LONGUEUR, LARGEUR, HAUTEUR, VOLUME, PRIX_REVIENT, DATE_FABRICATION, HEURE_FABRICATION, IDSOURCE, PRIX_REVIENT_PRA) "
                + "VALUES (blocsvaovao_seq.NEXTVAL, ?, ?, ?, ?, ?, CURRENT_DATE, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(insertQuery)) {
            for (int i = 0; i < 4; i++) {
                double longueur = longueurs[i];
                double largeur = largeurs[i];
                double hauteur = hauteurs[i];
                double prixRevientPra = prixReviendPRA[i];

                // Calculer le volume du bloc
                double volume = longueur * largeur * hauteur;

                // Ajouter à la moyenne des prix de revient
                prixRevientMoyenne += prixRevientPra;

                // Obtenir l'heure actuelle
                String heureFabrication = LocalTime.now().toString().substring(0, 8); // HH:MM:SS format

                // Insérer la ligne dans la table
                ps.setDouble(1, longueur);
                ps.setDouble(2, largeur);
                ps.setDouble(3, hauteur);
                ps.setDouble(4, volume);
                ps.setDouble(5, prixRevientPra);
                ps.setString(6, heureFabrication);  // Heure actuelle
                ps.setString(7, "ID_12345");  // Exemple pour IDSource
                ps.setString(8, prixRevientPra + "_PRA"); // Exemple pour PRIX_REVIENT_PRA

                ps.addBatch();
            }
            // Exécuter le batch
            ps.executeBatch();
        }

        // Calculer la moyenne des prix de revient des 4 premières lignes
        return prixRevientMoyenne / 4.0;
    }

}


