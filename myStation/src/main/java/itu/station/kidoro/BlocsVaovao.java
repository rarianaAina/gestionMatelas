package itu.station.kidoro;

import bean.CGenUtil;
import bean.ClassMAPTable;
import utilitaire.UtilDB;

import java.sql.*;
import java.time.LocalDate;
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

            // Requête pour récupérer le volume du bloc et l'heure de fabrication (heure_fabrication est un VARCHAR)
            String queryVolumeBloc = "SELECT Volume, heure_fabrication FROM BlocsVaovao WHERE IDBLOCSVAOVAO = ?";
            double volumeBloc = 0.0;
            String heureFabricationBloc = null; // Heure de fabrication sous forme de chaîne (VARCHAR)
            try (PreparedStatement psVolumeBloc = conn.prepareStatement(queryVolumeBloc)) {
                psVolumeBloc.setString(1, idBloc);
                try (ResultSet rsVolumeBloc = psVolumeBloc.executeQuery()) {
                    if (rsVolumeBloc.next()) {
                        volumeBloc = rsVolumeBloc.getDouble("Volume");
                        System.out.println(volumeBloc);
                        heureFabricationBloc = rsVolumeBloc.getString("heure_fabrication"); // Heure de fabrication du bloc
                        System.out.println(heureFabricationBloc);
                    } else {
                        throw new SQLException("Volume ou heure de fabrication introuvable pour l'idBloc: " + idBloc);
                    }
                }
            }

            // Requête pour récupérer tous les blocs fabriqués à la même date que le bloc cible, triés par date et heure
            String queryBlocsParDate = "SELECT IDBLOCSVAOVAO, Volume, heure_fabrication " +
                    "FROM BlocsVaovao " +
                    "WHERE TRUNC(date_fabrication) = ? " + // Comparaison de la date (Oracle)
                    "ORDER BY date_fabrication ASC, heure_fabrication ASC";
            // Trier d'abord par date, puis par heure

            // Liste pour stocker les blocs fabriqués à la même date
            List<BlockInfo> blocsAvecDate = new ArrayList<>();

            try (PreparedStatement psBlocsParDate = conn.prepareStatement(queryBlocsParDate)) {
                psBlocsParDate.setDate(1, dateFabrication); // Paramètre de la date de fabrication du bloc
                System.out.println(queryBlocsParDate);
                try (ResultSet rsBlocsParDate = psBlocsParDate.executeQuery()) {
                    while (rsBlocsParDate.next()) {
                        String idBlocParDate = rsBlocsParDate.getString("IDBLOCSVAOVAO");
                        double volumeParDate = rsBlocsParDate.getDouble("Volume");
                        String heureFabricationParDate = rsBlocsParDate.getString("heure_fabrication");

                        // Ajouter les blocs à la liste
                        blocsAvecDate.add(new BlockInfo(idBlocParDate, volumeParDate, heureFabricationParDate));
                    }
                }
            }

            // Requête pour récupérer les composants nécessaires pour la fabrication du bloc
            String queryComposants = "SELECT idComposants, qte FROM Composants";
            try (PreparedStatement psComposants = conn.prepareStatement(queryComposants);
                 ResultSet rsComposants = psComposants.executeQuery()) {

                // Pour chaque composant, récupérer les achats et calculer le prix de revient
                while (rsComposants.next()) {
                    String idComposant = rsComposants.getString("idComposants");
                    int qteNecessaire = rsComposants.getInt("qte");

                    // Calculer le prix de revient pour ce composant
                    double prixComposant = 0.0;
                    int qteRestante = qteNecessaire;

                    // Requête pour récupérer les achats disponibles pour ce composant avant ou à la date de fabrication du bloc
                    String queryAchats = "SELECT PUAchat, qte, DateAchat FROM Achats WHERE idComposants = ? AND DateAchat <= ? ORDER BY DateAchat ASC, DateAchat DESC";
                    try (PreparedStatement psAchats = conn.prepareStatement(queryAchats)) {
                        psAchats.setString(1, idComposant);
                        psAchats.setDate(2, dateFabrication); // Date de fabrication du bloc

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

            for (int i = 0; i < 20; i++) {
                // Dimensions aléatoires
                double longueur = 20 + (random.nextDouble() * 5);
                double largeur = 5 + (random.nextDouble() * 2);
                double hauteur = 10 + (random.nextDouble() * 5);

                // Volume et prix
                double volume = longueur * largeur * hauteur;
                double variation = 1 + (random.nextDouble() * 0.2 - 0.1);
                double prixRevientPra = prixRevientMoyenne * variation;

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
                ps.setString(9, prixRevientPra + "_PRA");

                ps.addBatch();

                if ((i + 1) % 1000 == 0) {
                    ps.executeBatch();
                }
            }
            ps.executeBatch();
        }
    }

    double insererPremieresLignes(Connection conn, double[] longueurs, double[] largeurs, double[] hauteurs, double[] prixReviendPRA) throws SQLException {
        if (longueurs.length != 4 || largeurs.length != 4 || hauteurs.length != 4 || prixReviendPRA.length != 4) {
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
        double prixRevientMoyenne = 0.0;

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
                double prixRevientPra = prixReviendPRA[i];

                double volume = longueur * largeur * hauteur;
                prixRevientMoyenne += prixRevientPra;

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
                ps.setString(9, prixRevientPra + "_PRA");

                ps.addBatch();
            }
            ps.executeBatch();
        }

        return prixRevientMoyenne / 4.0;
    }
    public static void mettreAJourPrixRevientBloc(Connection conn) throws SQLException {
        String queryBlocs = "SELECT IDBLOCSVAOVAO, DATE_FABRICATION FROM BLOCSVAOVAO";
        int batchSize = 100; // Nombre de mises à jour par lot

        // Début de la transaction
        conn.setAutoCommit(false);

        try (PreparedStatement psBlocs = conn.prepareStatement(queryBlocs);
             ResultSet rsBlocs = psBlocs.executeQuery()) {

            // Préparer la requête de mise à jour avec batch
            String updateQuery = "UPDATE BLOCSVAOVAO SET PRIX_REVIENT = ? WHERE IDBLOCSVAOVAO = ?";
            try (PreparedStatement psUpdate = conn.prepareStatement(updateQuery)) {

                int count = 0; // Compteur pour le batch
                while (rsBlocs.next()) {
                    String idBloc = rsBlocs.getString("IDBLOCSVAOVAO");
                    Date dateFabrication = rsBlocs.getDate("DATE_FABRICATION");
                    double prixRevient = calculerPrixRevientBloc(conn, dateFabrication, idBloc);
                    System.out.println("Prix de revient pour le bloc " + idBloc + " : " + prixRevient);
                    try {
                        // Vérifier que la date de fabrication est valide
                        if (dateFabrication == null) {
                            System.err.println("Date de fabrication nulle pour le bloc " + idBloc);
                            continue; // Passer ce bloc si la date est nulle
                        }

                        // Calculer le prix de revient pour ce bloc


                        // Ajouter la mise à jour dans le batch
                        psUpdate.setDouble(1, prixRevient);
                        psUpdate.setString(2, idBloc);
                        psUpdate.addBatch();

                        if (++count % batchSize == 0) {
                            psUpdate.executeBatch(); // Exécuter le batch
                        }
                    } catch (SQLException e) {
                        System.out.println("Requête SQL : " + updateQuery);
                        // Gérer les erreurs spécifiques au bloc
                        System.err.println("Erreur lors du calcul ou de la mise à jour du prix de revient pour le bloc " + idBloc + ": " + e.getMessage());
                        // Gérer ou ignorer selon la logique métier (par exemple, peut-être une valeur par défaut pour le prix)
                    }
                }

                // Exécuter le reste des mises à jour en batch
                psUpdate.executeBatch();

                // Valider la transaction
                conn.commit();

            } catch (SQLException e) {
                // Si une erreur se produit dans le batch, rollback la transaction
                conn.rollback();
                System.err.println("Erreur lors de la mise à jour des prix de revient. La transaction a été annulée.");
                throw e; // Relever l'exception après rollback
            }

        } catch (SQLException e) {
            // Rollback si une erreur se produit avant d'atteindre la mise à jour
            conn.rollback();
            System.err.println("Erreur lors de la récupération des blocs. La transaction a été annulée.");
            throw e; // Relever l'exception après rollback
        } finally {
            // Réinitialiser le mode de commit à la normale
            conn.setAutoCommit(true);
        }
    }


}


