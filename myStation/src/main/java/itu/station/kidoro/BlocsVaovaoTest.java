/*
package itu.station.kidoro;

import bean.CGenUtil;
import bean.ClassMAPTable;
import utilitaire.UtilDB;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

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
    public static double calculerPrixRevientBloc(Connection conn, Date dateFabrication) throws SQLException {
        double prixRevientTotal = 0.0;

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

                // Ajouter le prix de ce composant au prix total du bloc
                prixRevientTotal += prixComposant;
            }
        }

        return prixRevientTotal;
    }


}


*/
