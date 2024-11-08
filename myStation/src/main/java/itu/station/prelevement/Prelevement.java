package itu.station.prelevement;

import bean.CGenUtil;
import bean.ClassMAPTable;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import ejbServer.GeneralEJBLocalServer;
import itu.station.localEjbClient.EjbClientGetter;
import itu.station.tools.Pompe;
import itu.station.utils.TimeUtils;
import utilitaire.UtilDB;
import vente.Vente;

import java.sql.Connection;
import java.sql.Date;

public class Prelevement extends ClassMAPTable {
    String id;
    String idPrelevementAnterieur;
    String idUtilisateur;
    String idPompe;
    String heure;
    double qte;
    Date daty;
//    Prelevement prelevementAnterieur;
//    Pompe pompe;
//

    public Prelevement() {
        this.setNomTable("PRELEVEMENT");
    }

    public String getHeure() {
        return heure;
    }

    public void setHeure(String heure) throws Exception {
        if (heure.isEmpty() || heure == null){
            throw new Exception("Choisissez une heure");
        }
        this.heure = heure;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public double getQte() {
        return qte;
    }

    public void setQte(double qte) {
        this.qte = qte;
    }
    public void setQte(String qte) throws Exception {
        if (qte.isEmpty()){
            throw new Exception("Il faut remplir le champ quantité");
        }
        this.qte = Double.parseDouble(qte);
    }

    public String getIdPrelevementAnterieur() {
        return idPrelevementAnterieur;
    }

    public void setIdPrelevementAnterieur(String idPrelevementAnterieur) {
        this.idPrelevementAnterieur = idPrelevementAnterieur;
    }

    public String getIdUtilisateur() {
        return idUtilisateur;
    }

    public void setIdUtilisateur(String idUtilisateur) throws Exception {
        if (idUtilisateur.isEmpty() || idUtilisateur == null){
            throw new Exception("Choisissez un pompiste");
        }
        this.idUtilisateur = idUtilisateur;
    }

    public String getIdPompe() {
        return idPompe;
    }

    public void setIdPompe(String idPompe) throws Exception {
        if (idPompe.isEmpty() || idPompe == null){
            throw new Exception("Choisissez une pompe");
        }
        this.idPompe = idPompe;
    }

    public Date getDaty() {
        return daty;
    }

    public void setDaty(Date daty) {
        this.daty = daty;
    }
    public void setDaty(String daty) throws Exception {
        if (daty.isEmpty() || daty == null){
            throw new Exception("Choisissez une date");
        }
        this.daty = TimeUtils.convertToSqlDate(daty,"eng");
    }
    public Prelevement getPrelevementByIdAnterieur(Connection connection) throws Exception {
        //SELECT * FROM prelevement WHERE daty = (SELECT MAX(daty) FROM prelevemnt WHERE idPompe = ?)
        return ((Prelevement[]) CGenUtil.rechercher(new Prelevement(),null,null,connection," and id='"+this.getIdPrelevementAnterieur()+"'"))[0];
    }
    public Prelevement getPrelevementAnterieur(Connection connection) throws Exception {
        //SELECT * FROM prelevement WHERE daty = (SELECT MAX(daty) FROM prelevemnt WHERE idPompe = ?)
        return getPrelevementAnterieur(this.getIdPompe(),connection);
    }
    public Prelevement getPrelevementAnterieur(String idPompe,Connection connection) throws Exception {
        //SELECT * FROM prelevement WHERE daty = (SELECT MAX(daty) FROM prelevemnt WHERE idPompe = ?)
        if (connection == null) connection = new UtilDB().GetConn();
        Prelevement ret = null;
        Prelevement[] list = (Prelevement[]) CGenUtil.rechercher(new Prelevement(),"SELECT * " +
                "FROM (" +
                "    SELECT *" +
                "    FROM prelevement" +
                "    WHERE idPompe = '"+idPompe+"'" +
                "    ORDER BY daty DESC, heure DESC" +
                ")" +
                " WHERE ROWNUM = 1",connection);
        if (list.length > 0){
            ret = list[0];
        }
        return ret;
    }

    public boolean isVente(Connection c) throws Exception {
        return getPompeRowsById(c).length % 2 == 0 && getPompeRowsById(c).length != 0;
    }
    public Prelevement[] getPompeRowsById(Connection connection) throws Exception {
        if (connection == null) connection = new UtilDB().GetConn();
        return getPompeRowsById(this.getIdPompe(),connection);
    }
    public Prelevement[] getPompeRowsById(String idPompe,Connection connection) throws Exception {
        if (connection == null) connection = new UtilDB().GetConn();
        return (Prelevement[]) CGenUtil.rechercher(new Prelevement(),null,null,connection," and idpompe='"+idPompe+"'");
    }
    public Pompe getPompe(Connection connection) throws Exception {
        if (connection == null) connection = new UtilDB().GetConn();
        return ((Pompe[]) CGenUtil.rechercher(new Pompe(),null,null,connection," and id='"+idPompe+"'"))[0];
    }

    public Prelevement[] getAllPrelevement(Connection c) throws Exception {
        if (c == null) {
            c = new UtilDB().GetConn("gallois", "gallois");
        }

        // Journaliser la connexion
        System.out.println("Connexion établie : " + (c != null));

        // Obtenir tous les prelevements
        Prelevement[] prelevements = (Prelevement[]) CGenUtil.rechercher(new Prelevement(), null, null, c, "");

        // Journaliser le nombre de ventes récupérées
        if (prelevements != null) {
            System.out.println("Nombre de ventes récupérées : " + prelevements.length);
        } else {
            System.out.println("Aucun prelevement trouvée.");
        }

        return prelevements;
    }

    public String prelevementsToJson(Connection c) throws Exception {
        Prelevement[] prelevementsRecus = getAllPrelevement(c);

        Gson gson = new GsonBuilder().create();
        return gson.toJson(prelevementsRecus);
    }
    public Vente viser(Connection connection) throws Exception {
        if (connection == null) connection = new UtilDB().GetConn("gallois","gallois");
        GeneralEJBLocalServer generalEJBLocalServer = EjbClientGetter.getGeneralEjbService();
        return generalEJBLocalServer.getVenteByIdPrelevement(this.getId(),connection);
    }

    @Override
    public String getTuppleID() {
        return "id";
    }

    @Override
    public String getAttributIDName() {
        return id;
    }
    @Override
    public void construirePK(Connection c) throws Exception {
        this.preparePk("PREL", "GET_SEQ_PRELEVEMENT");
        this.setId(makePK(c));
    }
}
