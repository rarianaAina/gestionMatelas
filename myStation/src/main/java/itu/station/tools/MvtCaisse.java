package itu.station.tools;

import bean.CGenUtil;
import bean.ClassMAPTable;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import utilitaire.UtilDB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class MvtCaisse extends ClassMAPTable {

    private String id;
    private String designation;
    private String idCaisse;
    private String idVenteDetail;
    private String idVirement;
    private double debit;
    private double credit;
    private java.sql.Date daty;
    private int etat;
    private String idOp;
    private String idOrigine;
    private String idDevise;
    private double taux;
    private String idTiers;
    private String compte;
    private String idPrevision;

    // Constructeur par défaut
    public MvtCaisse() {
        this.setNomTable("MOUVEMENTCAISSE");
    }

    @Override
    public String getTuppleID() {
        return id;
    }

    @Override
    public String getAttributIDName() {
        return "ID";
    }

    // Getters et Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    public String getIdCaisse() {
        return idCaisse;
    }

    public void setIdCaisse(String idCaisse) {
        this.idCaisse = idCaisse;
    }

    public String getIdVenteDetail() {
        return idVenteDetail;
    }

    public void setIdVenteDetail(String idVenteDetail) {
        this.idVenteDetail = idVenteDetail;
    }

    public String getIdVirement() {
        return idVirement;
    }

    public void setIdVirement(String idVirement) {
        this.idVirement = idVirement;
    }

    public double getDebit() {
        return debit;
    }

    public void setDebit(double debit) {
        this.debit = debit;
    }

    public double getCredit() {
        return credit;
    }

    public void setCredit(double credit) {
        this.credit = credit;
    }

    public java.sql.Date getDaty() {
        return daty;
    }

    public void setDaty(java.sql.Date daty) {
        this.daty = daty;
    }

    public int getEtat() {
        return etat;
    }

    public void setEtat(int etat) {
        this.etat = etat;
    }

    public String getIdOp() {
        return idOp;
    }

    public void setIdOp(String idOp) {
        this.idOp = idOp;
    }

    public String getIdOrigine() {
        return idOrigine;
    }

    public void setIdOrigine(String idOrigine) {
        this.idOrigine = idOrigine;
    }

    public String getIdDevise() {
        return idDevise;
    }

    public void setIdDevise(String idDevise) {
        this.idDevise = idDevise;
    }

    public double getTaux() {
        return taux;
    }

    public void setTaux(double taux) {
        this.taux = taux;
    }

    public String getIdTiers() {
        return idTiers;
    }

    public void setIdTiers(String idTiers) {
        this.idTiers = idTiers;
    }

    public String getCompte() {
        return compte;
    }

    public void setCompte(String compte) {
        this.compte = compte;
    }

    public String getIdPrevision() {
        return idPrevision;
    }

    public void setIdPrevision(String idPrevision) {
        this.idPrevision = idPrevision;
    }

    public void construirePK(Connection c) throws Exception {
        this.preparePk("MCT", "GETSEQ_MVTCAISSE");
        this.setId(makePK(c));
    }

    // Méthode toString() pour afficher l'objet sous forme de chaîne
    @Override
    public String toString() {
        return "MouvementCaisse{" +
                "id='" + id + '\'' +
                ", designation='" + designation + '\'' +
                ", idCaisse='" + idCaisse + '\'' +
                ", idVenteDetail='" + idVenteDetail + '\'' +
                ", idVirement='" + idVirement + '\'' +
                ", debit=" + debit +
                ", credit=" + credit +
                ", daty=" + daty +
                ", etat=" + etat +
                ", idOp='" + idOp + '\'' +
                ", idOrigine='" + idOrigine + '\'' +
                ", idDevise='" + idDevise + '\'' +
                ", taux=" + taux +
                ", idTiers='" + idTiers + '\'' +
                ", compte='" + compte + '\'' +
                ", idPrevision='" + idPrevision + '\'' +
                '}';
    }

    public MvtCaisse[] getAllMouvements(Connection c) throws Exception {
        if (c == null) {
            c = new UtilDB().GetConn("gestion", "gestion");
        }

        // Journaliser la connexion
        System.out.println("Connexion établie : " + (c != null));

        // Obtenir tous les mouvements de caisse
        MvtCaisse[] mouvements = (MvtCaisse[]) CGenUtil.rechercher(new MvtCaisse(), null, null, c, "");

        // Journaliser le nombre de mouvements récupérés
        if (mouvements != null) {
            System.out.println("Nombre de mouvements récupérés : " + mouvements.length);
        } else {
            System.out.println("Aucun mouvement trouvé.");
        }

        return mouvements;
    }

    public String mouvementsToJson(Connection c) throws Exception {
        MvtCaisse[] mouvementsRecus = getAllMouvements(c);

        Gson gson = new GsonBuilder().create();
        return gson.toJson(mouvementsRecus);
    }

    // Méthode pour ajouter un mouvement de caisse



}
