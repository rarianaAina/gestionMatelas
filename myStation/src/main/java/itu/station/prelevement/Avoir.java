package itu.station.prelevement;

import avoir.AvoirFC;
import avoir.AvoirFCFille;
import bean.CGenUtil;
import itu.station.tools.Carburant;
import itu.station.tools.Cuve;
import itu.station.tools.Pompe;
import utilitaire.UtilDB;
import vente.Vente;
import vente.VenteDetails;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Date;
public class Avoir {
    FactureClient factureClient;
    String idClient;
    String idProduit;
    double montant;
    double qteAvoir;

    String dateRetour;
    public Avoir() {
    }

    public Avoir(FactureClient factureClient, String idClient, String idProduit, String montant/*, String dateRetour*/) {
        this.factureClient = factureClient;
        this.idClient = idClient;
        this.idProduit = idProduit;
        this.setMontant(montant);
        /*this.dateRetour = dateRetour;*/
    }

    public FactureClient getFactureClient() {
        return factureClient;
    }

    public void setFactureClient(FactureClient factureClient) {
        this.factureClient = factureClient;
    }

    public String getIdClient() {
        return idClient;
    }

    public void setIdClient(String idClient) {
        this.idClient = idClient;
    }

    public String getIdProduit() {
        return idProduit;
    }

    public void setIdProduit(String idProduit) {
        this.idProduit = idProduit;
    }

    public double getMontant() {
        return montant;
    }
    public void setMontant(String montant) {
        this.montant = Double.parseDouble(montant);
    }
    public void setMontant(double montant) {
        this.montant = montant;
    }

    public AvoirFC creerAvoirFC(Connection tsotra,Connection connection) throws Exception {

        AvoirFC avoirFC = new AvoirFC();
        boolean estOuvert = false;
        try{
            if (connection == null) {connection = new UtilDB().GetConn("gallois","gallois");estOuvert=true;}
            //if (tsotra == null) {connection = new UtilDB().GetConn();}
            Pompe pompe = this.getFactureClient().getPrelevement().getPompe(tsotra);
            Cuve cuve = pompe.getCuveByIdPompe("",tsotra);
            avoirFC.setDaty(this.getFactureClient().getPrelevement().getDaty());
            avoirFC.setCompte("411002");
            avoirFC.setDesignation("Avoir de la vente du "+this.getFactureClient().getPrelevement().getDaty().toString()+" "+this.getFactureClient().getPrelevement().getId());
            avoirFC.setIdMagasin(cuve.getId());
            avoirFC.setEtat(1);
            /*avoirFC.setDateRetour(dateRetour);*/
            avoirFC.setIdOrigine(this.getFactureClient().getPrelevement().getId());
            avoirFC.setIdClient(this.getIdClient());
            avoirFC.setIdVente(this.getFactureClient().getPrelevement().viser(connection).getId());
        }catch (Exception e){
            e.printStackTrace();
            throw e;
        }finally {
            if (estOuvert) {connection.close();tsotra.close();};
        }
        return avoirFC;
    }

    public Carburant getCarburantByPrelevement(Prelevement prelevement,Connection connection) throws Exception {
        Pompe pompe = prelevement.getPompe(connection);
        Cuve cuve = pompe.getCuveByIdPompe(pompe.getId(),connection);
        return cuve.getCarburantDetails(connection);
    }
    public boolean estPermis(Connection connection) throws Exception {
        double sum = getSommeAvoirByOrigine(connection);
        return sum + this.getMontant() < getCarburantByPrelevement(this.getFactureClient().getPrelevement(),connection).getPu_vente() * this.getFactureClient().getPrelevement().getQte();
    }

    public AvoirFCFille[] genererFilles(Connection tsotra,Connection connection) throws Exception {
        AvoirFCFille avoirFCFille = new AvoirFCFille();
        Pompe pompe = this.getFactureClient().getPrelevement().getPompe(tsotra);
        Cuve cuve = pompe.getCuveByIdPompe("",tsotra);
        Carburant carburant = cuve.getCarburantDetails(tsotra);
        Prelevement prelevement = this.getFactureClient().getPrelevement();
        Vente vente = this.getFactureClient().getPrelevement().viser(connection);
        VenteDetails[] venteDetails = vente.getDetails(connection);
        avoirFCFille.setIdProduit(cuve.getId_carb());
        avoirFCFille.setQte(getCorrespondingQuantity(prelevement,carburant,pompe,tsotra));
        avoirFCFille.setPu(this.getMontant());
        avoirFCFille.setRemise(0);
        avoirFCFille.setTva(0);
        avoirFCFille.setPuAchat(carburant.getPu_achat());
        avoirFCFille.setIdDevise("AR");
        avoirFCFille.setTauxDeChange(1);
        avoirFCFille.setDesignation(carburant.getNom());
        avoirFCFille.setIdOrigine(this.getFactureClient().getPrelevement().getId());
        avoirFCFille.setIdVenteDetails(venteDetails[0].getId());
        return new AvoirFCFille[]{avoirFCFille};
    }

    public double getSommeAvoirByOrigine(Connection connection) throws Exception {
        AvoirFCFille[] avoirFCFilles = (AvoirFCFille[])  CGenUtil.rechercher(new AvoirFCFille(),null,null,connection," and idOrigine='"+factureClient.getPrelevement().getId()+"'");
        this.qteAvoir = 0;
        for (AvoirFCFille avoirFCFille : avoirFCFilles) {
            this.qteAvoir += avoirFCFille.getPu();
        }
        return this.qteAvoir;
    }
    public double getSommeAvoirByOrigine(String idOrigine,Connection connection) throws Exception {
        boolean estOuvert = false;
        if (connection == null) {connection = new UtilDB().GetConn("gallois","gallois");estOuvert = true;}
        double qteAvoir = 0;
        try{
            AvoirFCFille[] avoirFCFilles = (AvoirFCFille[])  CGenUtil.rechercher(new AvoirFCFille(),null,null,connection," and idOrigine='"+idOrigine+"'");
            for (AvoirFCFille avoirFCFille : avoirFCFilles) {
                qteAvoir += avoirFCFille.getPu();
            }
            return qteAvoir;
        }catch (Exception e){
            e.printStackTrace();
            throw e;
        }finally {
            if (estOuvert) connection.close();
        }
    }


    public double getCorrespondingQuantity(Prelevement prelevement,Carburant carburant,Pompe pompe,Connection connection) throws Exception {
        Prelevement prelevementAnt = prelevement.getPrelevementByIdAnterieur(connection);
        System.out.println("QTE ANT:"+prelevementAnt.getQte());
        double qtyVente = (prelevement.getQte()-prelevementAnt.getQte());
        System.out.println("QTYVENTE"+qtyVente);
        System.out.println("Quantité caalculée:"+(this.getMontant()*qtyVente) / (carburant.getPu_vente() *qtyVente));
        return (this.getMontant()*qtyVente) / (carburant.getPu_vente() *qtyVente);
    }

    public AvoirFC genererAvoir(String u, Connection c) throws SQLException, Exception{
        AvoirFC avoirFC = null;
        boolean estOuvert = false;
        Connection tsotra = null;
        if(c==null){
            c = new UtilDB().GetConn();
            estOuvert = true;
            c.setAutoCommit(false);
        }
        try {
//            if (!estPermis(c)) throw new Exception("Vous n'avez plus le droit de faire des avoirs");
            tsotra = new UtilDB().GetConn();
            AvoirFC avoirFC1 = creerAvoirFC(tsotra,c);
            double avoirMontantActuel = getSommeAvoirByOrigine(c);
            if (avoirMontantActuel + this.getMontant()  > this.getFactureClient().getMontant() ){
                throw new Exception("Avoir supérieur au montant initial");
            }
            Vente vente = this.getFactureClient().getPrelevement().viser(c);
//            vente.getVenteDetailsNonGrp(c);
            avoirFC1.createObject(u, c);
            AvoirFCFille[] avoirFCFilles = genererFilles(tsotra,c);
            avoirFCFilles[0].setIdAvoirFC(avoirFC1.getId());
            avoirFCFilles[0].createObject(u, c);
            avoirFC1.setAvoirDetails(avoirFCFilles);
            avoirFC = avoirFC1;
            avoirFC.validerObject("1060",c);
            c.commit();
        } catch (Exception e) {
            if(estOuvert) c.rollback();
            throw e ;
        } finally {
            tsotra.close();
            if(estOuvert) c.close();
        }

        return avoirFC;
    }

//    public AvoirFC genererAvoir(Connection connection) throws Exception {
//
//    }

}
