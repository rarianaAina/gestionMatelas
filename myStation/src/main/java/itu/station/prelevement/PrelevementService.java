package itu.station.prelevement;

import bean.CGenUtil;
import historique.MapUtilisateur;
import itu.station.localEjbClient.EjbClientGetter;
import itu.station.stock.MvtArgent;
import itu.station.stock.MvtDTO;
import itu.station.stock.vente.VenteService;

import prelevement.PrelevementLocal;
import user.UserEJB;
import user.UserEJBClient;
import utilitaire.UtilDB;
import vente.Vente;

import javax.ejb.Stateless;
import java.sql.Connection;
@Stateless
public class PrelevementService implements PrelevementSignature{
    @Override
    public FactureClient ciblerDeuxBases(Prelevement prelevement,Connection connection) throws Exception {
        System.out.println("Insertion facture");
        // Prélever compteur
        if (connection == null) connection = new UtilDB().GetConn();
        Connection c1 = new UtilDB().GetConn("gallois","gallois");
        connection.setAutoCommit(false);
        c1.setAutoCommit(false);
        try{
            prelevement.construirePK(connection);

            MvtArgent mvtArgent = new VenteService();
            MvtDTO mvtDTO = new MvtDTO(prelevement.getPompe(connection).getId(), 1, 1 , prelevement.getDaty());
            FactureClient factureClient = null;
            PrelevementLocal prelevementLocal = EjbClientGetter.getPrelevementEjbService();
            prelevement.Prelevement prelevement1 = getPrelevementForGallois(prelevement);

            Prelevement prelAnt = prelevement.getPrelevementAnterieur(connection);
            if (prelAnt != null) {
                prelevement.setIdPrelevementAnterieur(prelAnt.getId());
                prelevement1.setIdPrelevementAnterieur(prelAnt.getId());
            }

            System.out.println("Hatreto izy dia mandeha");
            CGenUtil.save(prelevement,connection);
            CGenUtil.save(prelevement1,c1);

            if (prelevement.isVente(connection)){
                factureClient = processVente(prelevement, connection, prelAnt, mvtDTO, mvtArgent, prelevementLocal, prelevement1, c1);
            }
            connection.commit();
            c1.commit();
            return factureClient;
        }catch(Exception e){
            connection.rollback();
            c1.rollback();
            e.printStackTrace();
        }finally {
            connection.close();
            c1.close();
        }
        return null;
    }

    private FactureClient processVente(Prelevement prelevement, Connection connection, Prelevement prelAnt, MvtDTO mvtDTO, MvtArgent mvtArgent, PrelevementLocal prelevementLocal, prelevement.Prelevement prelevement1, Connection c1) throws Exception {
        FactureClient factureClient;
        System.out.println("ANT ANT ANT"+ prelAnt.getQte());
        double mvt = prelevement.getQte() - prelAnt.getQte();
        System.out.println(mvt);
        System.out.println(prelAnt.getId());
        mvtDTO.setQte(mvt);
        mvtArgent.makeMvtArgent(mvtDTO, connection);

        System.out.println("ID PRELEVEMENT:"+ prelevement.getId());

        prelevementLocal.generateAndPersistVenteRemote(prelevement1,"dg", c1);

        factureClient = new FactureClient(prelAnt, prelevement, connection);

        createPrelevementQuantityRow(prelevement, connection, prelAnt);

        Vente v = prelevement.viser(c1);
        v.setIdClient("CLI000054");
        v.validerObject("1060", c1);
        return factureClient;
    }

    private void createPrelevementQuantityRow(Prelevement prelevement, Connection connection, Prelevement prelAnt) throws Exception {
        PrelevementQuantity prelevementQuantity = new PrelevementQuantity();
        prelevementQuantity.setIdAct(prelevement.getId());
        prelevementQuantity.setIdAnt(prelAnt.getId());
        prelevementQuantity.setQty(prelevement.getQte()- prelAnt.getQte());
        prelevementQuantity.setDaty(prelevement.getDaty());
        prelevementQuantity.setIdPompe(prelevement.getIdPompe());
        prelevementQuantity.setIdUtilisateur(prelevement.getIdUtilisateur());

        prelevementQuantity.createObject("1060", connection);
    }

    private static prelevement.Prelevement getPrelevementForGallois(Prelevement prelevement) {
        prelevement.Prelevement prelevement1 = new prelevement.Prelevement();
        prelevement1.setId(prelevement.getId());
        prelevement1.setCompteur(prelevement.getQte());
        prelevement1.setDaty(prelevement.getDaty());
        prelevement1.setDesignation("Prelevement du "+prelevement.getDaty().toString());
        prelevement1.setIdPompe(prelevement.getIdPompe());
        prelevement1.setIdPompiste(Integer.parseInt(prelevement.getIdUtilisateur()));
        prelevement1.setHeure(prelevement.getHeure());
        prelevement1.setIdPrelevementAnterieur(prelevement.getIdPrelevementAnterieur());
        return prelevement1;
    }


}
