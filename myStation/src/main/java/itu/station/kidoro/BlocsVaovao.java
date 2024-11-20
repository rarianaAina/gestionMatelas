package itu.station.kidoro;

import bean.CGenUtil;
import bean.ClassMAPTable;
import utilitaire.UtilDB;

import java.sql.*;
import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

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
    private Double prixRevientPra;

    public BlocsVaovao(String idBloc, double longueur, double largeur, double hauteur, double volume, double prixDeRevient,
                       Date dateFabrication, String heureFabrication, String idSource, double prixRevientPra) {
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

    public Double getPrixRevientPra() {
        return prixRevientPra;
    }

    public void setPrixRevientPra(Double prixRevientPra) {
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

    public static void mettreAJourPrixRevient(Connection conn, Date dateFabrication) throws SQLException {
        // 1. Trier la table `BlocsVaovao` par date et heure pour déterminer l'ordre des fabrications
        String queryBlocs = "SELECT IDBLOCSVAOVAO, heure_fabrication FROM BlocsVaovao WHERE date_fabrication = ? ORDER BY date_fabrication ASC, heure_fabrication ASC";
        try (PreparedStatement psBlocs = conn.prepareStatement(queryBlocs)) {
            psBlocs.setDate(1, dateFabrication);

            try (ResultSet rsBlocs = psBlocs.executeQuery()) {
                while (rsBlocs.next()) {
                    String idBloc = rsBlocs.getString("IDBLOCSVAOVAO");

                    // 2. Calculer le prix de revient pour le bloc courant et ajuster les quantités dans Achats
                    double prixRevientBloc = calculerEtMettreAJourQuantites(conn, idBloc, dateFabrication);

                    // 3. Mettre à jour la table `BlocsVaovao` avec le prix de revient
                    String updatePrixRevientQuery = "UPDATE BlocsVaovao SET prix_revient = ? WHERE IDBLOCSVAOVAO = ?";
                    try (PreparedStatement psUpdate = conn.prepareStatement(updatePrixRevientQuery)) {
                        psUpdate.setDouble(1, prixRevientBloc);
                        psUpdate.setString(2, idBloc);
                        psUpdate.executeUpdate();
                    }
                }
            }
        }
    }

    static double calculerEtMettreAJourQuantites(Connection conn, String idBloc, Date dateFabrication) throws SQLException {
        double prixRevientTotal = 0.0;

        // Requête pour récupérer le volume du bloc
        String queryVolumeBloc = "SELECT Volume FROM BlocsVaovao WHERE IDBLOCSVAOVAO = ?";
        double volumeBloc;
        try (PreparedStatement psVolumeBloc = conn.prepareStatement(queryVolumeBloc)) {
            psVolumeBloc.setString(1, idBloc);
            try (ResultSet rsVolumeBloc = psVolumeBloc.executeQuery()) {
                if (rsVolumeBloc.next()) {
                    volumeBloc = rsVolumeBloc.getDouble("Volume");
                } else {
                    throw new SQLException("Volume introuvable pour l'idBloc: " + idBloc);
                }
            }
        }

        // Récupérer les composants associés à ce bloc
        String queryComposants = "SELECT idComposants, qte FROM Composants";
        try (PreparedStatement psComposants = conn.prepareStatement(queryComposants);
             ResultSet rsComposants = psComposants.executeQuery()) {

            while (rsComposants.next()) {
                String idComposant = rsComposants.getString("idComposants");
                double qteNecessaire = rsComposants.getDouble("qte");
                double qteTenaNec = qteNecessaire * volumeBloc;

                // Étape 1 : Validation des quantités disponibles
                String queryValidation = "SELECT SUM(qte) AS totalQte FROM Achats WHERE idComposants = ? AND DateAchat <= ?";
                double totalQteDisponible;
                try (PreparedStatement psValidation = conn.prepareStatement(queryValidation)) {
                    psValidation.setString(1, idComposant);
                    psValidation.setDate(2, dateFabrication);
                    try (ResultSet rsValidation = psValidation.executeQuery()) {
                        if (rsValidation.next()) {
                            totalQteDisponible = rsValidation.getDouble("totalQte");
                        } else {
                            totalQteDisponible = 0.0;
                        }
                    }
                }

                // Si la quantité est insuffisante, passer au bloc suivant
                if (totalQteDisponible < qteTenaNec) {
                    System.out.println("Quantité insuffisante pour le composant " + idComposant + ". Passer au bloc suivant.");
                    break; // Passer au bloc suivant
                }

                // Étape 2 : Mise à jour des quantités dans les achats
                String queryAchats = "SELECT idAchat, PUAchat, qte FROM Achats WHERE idComposants = ? AND DateAchat <= ? ORDER BY DateAchat ASC";
                double prixComposant = 0.0;
                double qteRestante = qteTenaNec;

                try (PreparedStatement psAchats = conn.prepareStatement(queryAchats)) {
                    psAchats.setString(1, idComposant);
                    psAchats.setDate(2, dateFabrication);
                    try (ResultSet rsAchats = psAchats.executeQuery()) {
                        while (rsAchats.next() && qteRestante > 0) {
                            String idAchat = rsAchats.getString("idAchat");
                            double prixAchat = rsAchats.getDouble("PUAchat");
                            double qteAchetee = rsAchats.getDouble("qte");

                            if (qteAchetee >= qteRestante) {
                                prixComposant += prixAchat * qteRestante;
                                // Mise à jour de la quantité restante
                                String updateAchatQuery = "UPDATE Achats SET qte = qte - ? WHERE idAchat = ?";
                                try (PreparedStatement psUpdateAchat = conn.prepareStatement(updateAchatQuery)) {
                                    psUpdateAchat.setDouble(1, qteRestante);
                                    psUpdateAchat.setString(2, idAchat);
                                    psUpdateAchat.executeUpdate();
                                }
                                qteRestante = 0;
                            } else {
                                prixComposant += prixAchat * qteAchetee;
                                String updateAchatQuery = "UPDATE Achats SET qte = 0 WHERE idAchat = ?";
                                try (PreparedStatement psUpdateAchat = conn.prepareStatement(updateAchatQuery)) {
                                    psUpdateAchat.setString(1, idAchat);
                                    psUpdateAchat.executeUpdate();
                                }
                                qteRestante -= qteAchetee;
                            }
                        }
                    }
                }

                prixRevientTotal += prixComposant;
            }
        }

        return prixRevientTotal;
    }

    // Classe interne pour stocker les informations du bloc (ID, volume, heure de fabrication)
    private static class BlockInfo {
        String idBloc;
        double volume;
        String heureFabrication;

        public BlockInfo(String idBloc, double volume, String heureFabrication) {
            this.idBloc = idBloc;
            this.volume = volume;
            this.heureFabrication = heureFabrication;
        }
    }
    public static double prixDeReviensParSource(Connection conn, String idSource) throws SQLException {
        double prixTotal = 0.0;

        // Requête pour sommer les prix de revient associés à l'idSource
        String queryPrixTotal =
                "SELECT SUM(PRIX_REVIENT) AS PrixTotal " +
                        "FROM BLOCSVAOVAO " +
                        "WHERE IDSOURCE = ?";

        try (PreparedStatement ps = conn.prepareStatement(queryPrixTotal)) {
            ps.setString(1, idSource);  // L'ID de la source

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    prixTotal = rs.getDouble("PrixTotal");
                }
            }
        }

        return prixTotal;
    }

    public void insererBlocs(Connection conn, double[] longueurs, double[] largeurs, double[] hauteurs, double[] prixReviendPRA) throws SQLException {
        if (conn == null) {
            conn = new UtilDB().GetConn("mystation", "mystation");
        }

        // Insérer les 4 premières lignes
        double prixRevientMoyenne = insererPremieresLignes(conn, longueurs, largeurs, hauteurs, prixReviendPRA);
        System.out.println(prixRevientMoyenne);
        // Récupérer tous les idMachines
        List<String> idMachines = new ArrayList<>();
        String selectMachinesQuery = "SELECT IDMACHINE FROM MACHINES";
        try (PreparedStatement ps = conn.prepareStatement(selectMachinesQuery);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                idMachines.add(rs.getString("IDMACHINE"));
            }
        }

        if (idMachines.isEmpty()) {
            throw new SQLException("Aucune machine disponible dans la table MACHINES.");
        }

        Random random = new Random();
        String insertQuery = "INSERT INTO BLOCSVAOVAO (IDBLOCSVAOVAO, LONGUEUR, LARGEUR, HAUTEUR, VOLUME, PRIX_REVIENT, DATE_FABRICATION, HEURE_FABRICATION, IDSOURCE, PRIX_REVIENT_PRA) "
                + "VALUES (blocsvaovao_seq.NEXTVAL, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = conn.prepareStatement(insertQuery)) {
            LocalDate startDate = LocalDate.of(2022, 1, 1);
            LocalDate today = LocalDate.now();
            long totalDays = today.toEpochDay() - startDate.toEpochDay();

            for (int i = 0; i < 10; i++) {
                // Dimensions aléatoires
                double longueur = 20 + (random.nextDouble() * 5);
                double largeur = 5 + (random.nextDouble() * 2);
                double hauteur = 10 + (random.nextDouble() * 5);

                // Volume et prix
                double volume = longueur * largeur * hauteur;
                double variation = 1 + (random.nextDouble() * 0.2 - 0.1);
                double prixRevientPra = prixRevientMoyenne * variation * volume;

                // Générer une date aléatoire entre 2022 et aujourd'hui
                LocalDate randomDate = startDate.plusDays(random.nextInt((int) totalDays + 1));

                // Heure aléatoire
                LocalTime randomTime = LocalTime.of(random.nextInt(24), random.nextInt(60), random.nextInt(60));

                // Choisir un idSource aléatoire
                String idSource = idMachines.get(random.nextInt(idMachines.size()));

                // Insérer les données
                ps.setDouble(1, longueur);
                ps.setDouble(2, largeur);
                ps.setDouble(3, hauteur);
                ps.setDouble(4, volume);
                ps.setDouble(5, prixRevientPra);
                ps.setDate(6, java.sql.Date.valueOf(randomDate));
                ps.setString(7, randomTime.toString());
                ps.setString(8, idSource);
                ps.setDouble(9, prixRevientPra);

                ps.addBatch();

                if ((i + 1) % 1000 == 0) {
                    ps.executeBatch();
                }
            }
            ps.executeBatch();
        }
    }

    double insererPremieresLignes(Connection conn, double[] longueurs, double[] largeurs, double[] hauteurs, double[] prixRevientPRA) throws SQLException {
        if (longueurs.length != 4 || largeurs.length != 4 || hauteurs.length != 4 || prixRevientPRA.length != 4) {
            throw new IllegalArgumentException("Les tableaux doivent contenir 4 éléments.");
        }

        List<String> idMachines = new ArrayList<>();
        String selectMachinesQuery = "SELECT IDMACHINE FROM MACHINES";
        try (PreparedStatement ps = conn.prepareStatement(selectMachinesQuery);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                idMachines.add(rs.getString("IDMACHINE"));
            }
        }

        if (idMachines.isEmpty()) {
            throw new SQLException("Aucune machine disponible dans la table MACHINES.");
        }

        Random random = new Random();
        double prixRevientTotal = 0.0;
        double volumeTotal = 0.0;

        String insertQuery = "INSERT INTO BLOCSVAOVAO (IDBLOCSVAOVAO, LONGUEUR, LARGEUR, HAUTEUR, VOLUME, PRIX_REVIENT, DATE_FABRICATION, HEURE_FABRICATION, IDSOURCE, PRIX_REVIENT_PRA) "
                + "VALUES (blocsvaovao_seq.NEXTVAL, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = conn.prepareStatement(insertQuery)) {
            LocalDate startDate = LocalDate.of(2022, 1, 1);
            LocalDate today = LocalDate.now();
            long totalDays = today.toEpochDay() - startDate.toEpochDay();

            for (int i = 0; i < 4; i++) {
                double longueur = longueurs[i];
                double largeur = largeurs[i];
                double hauteur = hauteurs[i];
                double prixRevientPra = prixRevientPRA[i];

                double volume = longueur * largeur * hauteur;
                prixRevientTotal += prixRevientPra;  // Calculer le total du prix de revient pondéré par le volume
                volumeTotal += volume;  // Ajouter au volume total

                // Date et heure aléatoires
                LocalDate randomDate = startDate.plusDays(random.nextInt((int) totalDays + 1));
                LocalTime randomTime = LocalTime.of(random.nextInt(24), random.nextInt(60), random.nextInt(60));

                String idSource = idMachines.get(random.nextInt(idMachines.size()));

                ps.setDouble(1, longueur);
                ps.setDouble(2, largeur);
                ps.setDouble(3, hauteur);
                ps.setDouble(4, volume);
                ps.setDouble(5, prixRevientPra);
                ps.setDate(6, java.sql.Date.valueOf(randomDate));
                ps.setString(7, randomTime.toString());
                ps.setString(8, idSource);
                ps.setDouble(9, prixRevientPra);

                ps.addBatch();
            }
            ps.executeBatch();
        }

        // Calculer le prix de revient moyen en fonction du volume total
        if (volumeTotal == 0) {
            throw new ArithmeticException("La somme des volumes des 4 blocs est égale à zéro, impossible de calculer le prix de revient.");
        }
        System.out.println(prixRevientTotal);
        System.out.println(volumeTotal);
        return prixRevientTotal / volumeTotal;  // Diviser par la somme des volumes
    }


    public static Map<String, Double> calculerPrixTotalParSource(Connection conn) throws SQLException {
        Map<String, Double> prixTotalParSource = new HashMap<>();

        // Requête SQL pour récupérer le total de PRIX_REVIENT_PRA pour chaque IDSOURCE
        String query = "SELECT IDSOURCE, SUM(PRIX_REVIENT_PRA) AS TOTAL_PRA " +
                "FROM BLOCSVAOVAO " +
                "GROUP BY IDSOURCE";

        // Exécution de la requête
        try (PreparedStatement ps = conn.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {

            // Parcours des résultats et ajout dans la map
            while (rs.next()) {
                String idSource = rs.getString("IDSOURCE");
                double totalPra = rs.getDouble("TOTAL_PRA");

                // Ajout du résultat dans la map
                prixTotalParSource.put(idSource, totalPra);
            }
        }

        // Retourne la map contenant le total PRIX_REVIENT_PRA pour chaque IDSOURCE
        return prixTotalParSource;
    }

}


