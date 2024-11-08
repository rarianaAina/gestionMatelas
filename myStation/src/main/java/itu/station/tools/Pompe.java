package itu.station.tools;

import bean.CGenUtil;
import bean.ClassMAPTable;

import java.sql.Connection;

public class Pompe extends ClassMAPTable {
    String id,id_cuve,nom;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId_cuve() {
        return id_cuve;
    }

    public void setId_cuve(String id_cuve) {
        this.id_cuve = id_cuve;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
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
        this.preparePk("PMP", "GET_SEQ_POMPE");
        this.setId(makePK(c));
    }

    public Pompe() {
        this.setNomTable("POMPE");
    }
    public Cuve getCuveByIdPompe(String idPompe,Connection connection) throws Exception {
        if (idPompe == null || idPompe.equals("")) idPompe = this.getId();//
        //Alaina aloha ilay pompe e dia avoay ny idCuve!!!!
        Pompe pompe = ((Pompe[]) CGenUtil.rechercher(new Pompe(),null,null,connection," and id ='"+idPompe+"'"))[0];
        //izay voa manao hoe id_cuve=...
        System.out.println("POMPE ID:"+pompe.getId_cuve());
        return ((Cuve[]) CGenUtil.rechercher(new Cuve(),null,null,connection," and id='"+pompe.getId_cuve()+"'"))[0];
    }
}
