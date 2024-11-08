package itu.station.kidoro;

import bean.CGenUtil;
import bean.ClassMAPTable;
import utilitaire.UtilDB;

import java.sql.*;

public class Kidoro extends ClassMAPTable {

    private String idKidoro;
    private String idTypeKidoro;
    private String idSource;
    private double qte;
    private Date date_creation;
    private String prixDeRevient; // Correction du nom de l'attribut

    public Kidoro(String idKidoro, String idTypeKidoro, String idSource, double qte, Date date_creation) {
        this.idKidoro = idKidoro;
        this.idTypeKidoro = idTypeKidoro;
        this.idSource = idSource;
        this.qte = qte;
        this.date_creation = date_creation;
    }

    public Kidoro() {
        this.setNomTable("KIDORO");
    }

    @Override
    public String getTuppleID() {
        return idKidoro;
    }

    @Override
    public String getAttributIDName() {
        return "idKidoro";
    }

    // Getters et setters

    public String getIdKidoro() {
        return idKidoro;
    }

    public void setIdKidoro(String idKidoro) {
        this.idKidoro = idKidoro;
    }

    public String getIdTypeKidoro() {
        return idTypeKidoro;
    }

    public void setIdTypeKidoro(String idTypeKidoro) {
        this.idTypeKidoro = idTypeKidoro;
    }

    public String getIdSource() {
        return idSource;
    }

    public void setIdSource(String idSource) {
        this.idSource = idSource;
    }

    public double getQte() {
        return qte;
    }

    public void setQte(double qte) {
        this.qte = qte;
    }

    public Date getDate_creation() {
        return date_creation;
    }

    public void setDate_creation(Date date_creation) {
        this.date_creation = date_creation;
    }

    public String getPrixDeRevient() {
        return prixDeRevient;
    }

    public void setPrixDeRevient(String prixDeRevient) {
        this.prixDeRevient = prixDeRevient;
    }

    public void insererKidoro(Connection connection) throws SQLException {
        // Obtenez le prochain numéro de la séquence
        String sqlSeq = "SELECT KR_KIDORO_SEQ.NEXTVAL AS nextId FROM dual";
        try (PreparedStatement seqStatement = connection.prepareStatement(sqlSeq);
             ResultSet seqResultSet = seqStatement.executeQuery()) {
            if (seqResultSet.next()) {
                int nextId = seqResultSet.getInt("nextId");
                this.idKidoro = String.format("KR%03d", nextId); // Formatez l'ID avec trois chiffres
            }
        }

        // Insérez le kidoro
        String sql = "INSERT INTO KIDORO (idKidoro, idTypeKidoro, idSource, qte, date_creation, prix_revient) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, this.idKidoro);
            preparedStatement.setString(2, this.idTypeKidoro);
            preparedStatement.setString(3, this.idSource);
            preparedStatement.setDouble(4, this.qte);
            preparedStatement.setDate(5, new java.sql.Date(this.date_creation.getTime())); // Ajout de la date de création
            preparedStatement.setString(6, this.prixDeRevient); // Ajout du prix de revient

            preparedStatement.executeUpdate();
        }
    }

    // Méthode pour calculer le prix de revient
    /**
     * Calcule le prix de revient du Kidoro.
     *
     * @param volumeMatelas Le volume du matelas.
     * @param prixBlocInitial Le prix de revient du bloc initial.
     * @param volumeBlocInitial Le volume du bloc initial.
     * @return Le prix de revient calculé.
     */
    public double calculerPrixDeRevient(double volumeMatelas, double prixBlocInitial, double volumeBlocInitial) {
        if (volumeBlocInitial == 0) {
            throw new IllegalArgumentException("Le volume du bloc initial ne peut pas être zéro.");
        }
        double prixDeRevient = (volumeMatelas / volumeBlocInitial) * prixBlocInitial;
        this.prixDeRevient = String.valueOf(prixDeRevient); // Mettre à jour l'attribut prixDeRevient
        return prixDeRevient;
    }

    // Méthodes supplémentaires pour la classe, si nécessaire
    public Kidoro[] getAllKidoros(Connection c) throws Exception {
        if (c == null) {
            c = new UtilDB().GetConn("mystation", "mystation");
        }
        // Récupérer tous les kidoros en utilisant une condition vide
        return (Kidoro[]) CGenUtil.rechercher(new Kidoro(), null, null, c, "");
    }
}
