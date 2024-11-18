package itu.station.kidoro;

import bean.CGenUtil;
import bean.ClassMAPTable;
import utilitaire.UtilDB;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Achats extends ClassMAPTable {

    private String idAchat;
    private String idComposants;
    private int qte;
    private double pUAchat;
    private Date dateAchat;

    public Achats(String idAchat, String idComposants, int qte, double pUAchat, Date dateAchat) {
        this.idAchat = idAchat;
        this.idComposants = idComposants;
        this.qte = qte;
        this.pUAchat = pUAchat;
        this.dateAchat = dateAchat;
    }

    public Achats() {
        this.setNomTable("ACHATS");
    }

    @Override
    public String getTuppleID() {
        return idAchat;
    }

    @Override
    public String getAttributIDName() {
        return "idAchat";
    }

    // Getters et setters

    public String getIdAchat() {
        return idAchat;
    }

    public void setIdAchat(String idAchat) {
        this.idAchat = idAchat;
    }

    public String getIdComposants() {
        return idComposants;
    }

    public void setIdComposants(String idComposants) {
        this.idComposants = idComposants;
    }

    public int getQte() {
        return qte;
    }

    public void setQte(int qte) {
        this.qte = qte;
    }

    public double getPUAchat() {
        return pUAchat;
    }

    public void setPUAchat(double pUAchat) {
        this.pUAchat = pUAchat;
    }

    public Date getDateAchat() {
        return dateAchat;
    }

    public void setDateAchat(Date dateAchat) {
        this.dateAchat = dateAchat;
    }

    public void construirePK(Connection c) throws Exception {
        this.preparePk("ACH", "GET_ACHAT_SEQ");
        this.setIdAchat(makePK(c));
    }

    public Achats[] getAllAchats(Connection c) throws Exception {
        if (c == null) {
            c = new UtilDB().GetConn("mystation", "mystation");
        }
        // Récupérer tous les achats
        return (Achats[]) CGenUtil.rechercher(new Achats(), null, null, c, "");
    }

    public Achats[] getAchatsParComposant(Connection c, String idComposant) throws Exception {
        if (c == null) {
            c = new UtilDB().GetConn("mystation", "mystation");
        }

        // Préparez la requête pour récupérer les achats pour un composant spécifique
        String query = "SELECT * FROM ACHATS a WHERE a.idComposants = ?";

        List<Achats> achats = new ArrayList<>();
        try (PreparedStatement stmt = c.prepareStatement(query)) {
            stmt.setString(1, idComposant);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Achats achat = new Achats();
                    achat.setIdAchat(rs.getString("idAchat"));
                    achat.setIdComposants(rs.getString("idComposants"));
                    achat.setQte(rs.getInt("qte"));
                    achat.setPUAchat(rs.getDouble("pUAchat"));
                    achat.setDateAchat(rs.getDate("dateAchat"));

                    achats.add(achat);
                }
            }
        }

        // Convertir la liste en tableau
        return achats.toArray(new Achats[0]);
    }

    public void insererAchat(Connection connection) throws SQLException {
        // Obtenez le prochain numéro de la séquence
        String sqlSeq = "SELECT ACHATS_SEQ.NEXTVAL AS nextId FROM dual";
        try (PreparedStatement seqStatement = connection.prepareStatement(sqlSeq);
             ResultSet seqResultSet = seqStatement.executeQuery()) {
            if (seqResultSet.next()) {
                int nextId = seqResultSet.getInt("nextId");
                this.idAchat = String.format("ACH%03d", nextId); // Formatez l'ID avec trois chiffres
            }
        }

        // Log les valeurs pour débogage
        System.out.println("idAchat: " + this.idAchat);

        // Insérez l'achat
        String sql = "INSERT INTO ACHATS (idAchat, idComposants, qte, pUAchat, dateAchat) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, this.idAchat);
            preparedStatement.setString(2, this.idComposants);
            preparedStatement.setInt(3, this.qte);
            preparedStatement.setDouble(4, this.pUAchat);
            preparedStatement.setDate(5, this.dateAchat);
            preparedStatement.executeUpdate();
        }
    }
}
