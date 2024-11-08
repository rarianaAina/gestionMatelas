package itu.station.prelevement;

import bean.CGenUtil;
import bean.ClassMAPTable;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import utilitaire.UtilDB;

import java.sql.Connection;
import java.sql.Date;

public class PrelevementQuantity extends ClassMAPTable {
    String id,idAnt,idAct;
    double qty;
    Date daty;
    String idPompe,idUtilisateur;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIdAnt() {
        return idAnt;
    }

    public void setIdAnt(String idAnt) {
        this.idAnt = idAnt;
    }

    public String getIdAct() {
        return idAct;
    }

    public void setIdAct(String idAct) {
        this.idAct = idAct;
    }

    public double getQty() {
        return qty;
    }

    public void setQty(double qty) {
        this.qty = qty;
    }

    public Date getDaty() {
        return daty;
    }

    public void setDaty(Date daty) {
        this.daty = daty;
    }

    public String getIdPompe() {
        return idPompe;
    }

    public void setIdPompe(String idPompe) {
        this.idPompe = idPompe;
    }

    public String getIdUtilisateur() {
        return idUtilisateur;
    }

    public void setIdUtilisateur(String idUtilisateur) {
        this.idUtilisateur = idUtilisateur;
    }

    @Override
    public String getTuppleID() {
        return id;
    }

    @Override
    public String getAttributIDName() {
        return "id";
    }

    public PrelevementQuantity() {
        this.setNomTable("prelevement_quantity");
    }

    @Override
    public void construirePK(Connection c) throws Exception {
        this.preparePk("PRELQ","GETSEQPRELQTY");
        this.setId(makePK(c));
    }

    public PrelevementQuantity[] getAllPrelevementQuantity(Connection c) throws Exception {
        if (c == null) {
            c = new UtilDB().GetConn();
        }

        // Journaliser la connexion
        System.out.println("Connexion établie : " + (c != null));

        // Obtenir tous les prelevements
        PrelevementQuantity[] prelevementsQuantity = (PrelevementQuantity[]) CGenUtil.rechercher(new PrelevementQuantity(), null, null, c, "");

        // Journaliser le nombre de ventes récupérées
        if (prelevementsQuantity != null) {
            System.out.println("Nombre de ventes récupérées : " + prelevementsQuantity.length);
        } else {
            System.out.println("Aucun prelevement trouvée.");
        }

        return prelevementsQuantity;
    }

    public String prelevementsQuantityToJson(Connection c) throws Exception {
        PrelevementQuantity[] prelevementsRecus = getAllPrelevementQuantity(c);

        Gson gson = new GsonBuilder().create();
        return gson.toJson(prelevementsRecus);
    }
}
