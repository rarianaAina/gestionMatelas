package itu.station.tools;

import bean.CGenUtil;
import bean.ClassMAPTable;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import utilitaire.UtilDB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public class Demandes extends ClassMAPTable {

    private String idDemande;
    private int idEmployee;
    private String rubriques;
    private int qte;
    private String raison;
    private String etat;



    private String nomDepartement;



    // Constructeur par défaut
    public Demandes() {
        this.setNomTable("demandes");
    }

    @Override
    public String getTuppleID() {
        return null;
    }

    @Override
    public String getAttributIDName() {
        return null;
    }

/*    // Constructeur avec paramètres
    public Demandes(String idDemande, int idEmployee, String rubriques, int qte, String raison, String etat) {
        this.idDemande = idDemande;
        this.idEmployee = idEmployee;
        this.rubriques = rubriques;
        this.qte = qte;
        this.raison = raison;
        this.etat = etat;
    }*/

    // Getters et Setters
    public String getIdDemande() {
        return idDemande;
    }

    public void setIdDemande(String idDemande) {
        this.idDemande = idDemande;
    }

    public int getIdEmployee() {
        return idEmployee;
    }

    public void setIdEmployee(int idEmployee) {
        this.idEmployee = idEmployee;
    }

    public String getRubriques() {
        return rubriques;
    }

    public void setRubriques(String rubriques) {
        this.rubriques = rubriques;
    }

    public int getQte() {
        return qte;
    }

    public void setQte(int qte) {
        this.qte = qte;
    }

    public String getRaison() {
        return raison;
    }

    public void setRaison(String raison) {
        this.raison = raison;
    }

    public String getEtat() {
        return etat;
    }

    public void setEtat(String etat) {
        this.etat = etat;
    }
/*    public void construirePK(Connection c) throws Exception {
        this.preparePk("DMD", "DEMANDESEQ");
        this.setIdDemande(makePK(c));
    }*/

    public String getNomDepartement() {
        return nomDepartement;
    }

    public void setNomDepartement(String nomDepartement) {
        this.nomDepartement = nomDepartement;
    }
    // Méthode toString() pour afficher l'objet sous forme de chaîne
    @Override
    public String toString() {
        return "Demande{" +
                "idDemande=" + idDemande +
                ", idEmployee=" + idEmployee +
                ", rubriques='" + rubriques + '\'' +
                ", qte=" + qte +
                ", raison='" + raison + '\'' +
                ", etat='" + etat + '\'' +
                ", nomdepartement='" + nomDepartement + '\'' +
                '}';
    }
    public Demandes[] getAllDemandes(Connection c, String role) throws Exception {
        String sql = "SELECT idDemande, idEmployee, rubriques, qte, raison, etat, nomdepartement FROM demandes";

        // Ajout de la clause WHERE si le rôle est chef_de_dep
        System.out.println(role);
        if ("Chef_de_dep".equals(role)) {
            sql += " WHERE etat = 0 OR etat = 4 OR etat = 1"; // Ajustez la condition selon vos besoins
        }
      /*  if ("dep_achat".equals(role)) {
            sql += " WHERE etat = 1 OR etat = 2 OR etat = 4 OR etat = 0 OR etat = 3";
        }*/
        if ("finance".equals(role)) {
            sql += " WHERE etat = 2 OR etat = 0 OR etat = 4 OR etat = 3 OR etat = 1";
        }
        if ("dir_ge".equals(role)) {
            sql += " WHERE etat = 3 OR etat = 0 OR etat = 4";
        }
        System.out.println(sql);
        try (PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            List<Demandes> list = new ArrayList<>();
            while (rs.next()) {
                Demandes demande = new Demandes();
                demande.setIdDemande(rs.getString("idDemande"));
                demande.setIdEmployee(rs.getInt("idEmployee"));
                demande.setRubriques(rs.getString("rubriques"));
                demande.setQte(rs.getInt("qte"));
                demande.setRaison(rs.getString("raison"));
                demande.setEtat(rs.getString("etat"));
                demande.setNomDepartement(rs.getString("nomdepartement")); // Assurez-vous que cela est correct

                list.add(demande);
            }
            return list.toArray(new Demandes[0]);
        }
    }

    public String demandesToJson(Connection c, String role) throws Exception {
        Demandes[] demandesRecues = getAllDemandes(c, role);

        Gson gson = new GsonBuilder().create();
        return gson.toJson(demandesRecues);
    }
}

