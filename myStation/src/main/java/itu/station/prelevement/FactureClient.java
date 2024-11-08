package itu.station.prelevement;

import avoir.AvoirFC;
import avoir.AvoirFCFille;
import bean.CGenUtil;
import itu.station.tools.Carburant;
import itu.station.tools.Pompe;

import java.sql.Connection;

public class FactureClient {
    double montant;
    Prelevement prelevement;
    double qteReleve;

    public double getQteReleve() {
        return qteReleve;
    }

    public void setQteReleve(double qteReleve) {
        this.qteReleve = qteReleve;
    }

    public double getMontant() {
        return montant;
    }

    public void setMontant(double montant) {
        this.montant = montant;
    }
    public void setMontant(Prelevement prelAnt,Prelevement prelevement,Connection connection) throws Exception {
        Carburant carburant = getCorrespondingCarburant(prelevement.getIdPompe(),connection);
        PrelevementQuantityCpl[] prels = (PrelevementQuantityCpl[]) CGenUtil.rechercher(new PrelevementQuantityCpl(),null,null,connection," ");
        this.setQteReleve(prelevement.getQte() - prelAnt.getQte());
        this.montant = carburant.getPu_vente() * (prelevement.getQte() - prelAnt.getQte());
    }

    public Prelevement getPrelevement() {
        return prelevement;
    }

    public void setPrelevement(Prelevement prelevement) {
        this.prelevement = prelevement;
    }


    public FactureClient(Prelevement prelAnt,Prelevement prelevement,Connection connection) throws Exception {
        this.setMontant(prelAnt,prelevement,connection);
        this.setPrelevement(prelevement);
    }

    public FactureClient(String idPrelevement,Connection connection) throws Exception {
        System.out.println("ID PRELEVEMENT:"+idPrelevement);
        Prelevement prelevement = ((Prelevement[]) CGenUtil.rechercher(new Prelevement(),null,null,connection," and id = '"+idPrelevement+"'"))[0];
        Prelevement prelevementAnt = prelevement.getPrelevementByIdAnterieur(connection);
        this.setMontant(prelevementAnt,prelevement,connection);
        this.setPrelevement(prelevement);
    }

    public Carburant getCorrespondingCarburant(String idPompe, Connection connection) throws Exception {
        Pompe pompe = new Pompe();
        return pompe.getCuveByIdPompe(idPompe,connection).getCarburantDetails(connection);
    }

}
