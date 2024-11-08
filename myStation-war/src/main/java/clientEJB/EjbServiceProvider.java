package clientEJB;

import itu.station.dataGenerator.DataGeneratorEjbSignature;
import itu.station.ejbService.EjbStation2;
import itu.station.prelevement.PrelevementSignature;
import itu.station.stock.achat.AchatExecutorSignature;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public class EjbServiceProvider {
    public static DataGeneratorEjbSignature getMagasinEjbService() {
        try {
            Context c = new InitialContext();
            return (DataGeneratorEjbSignature) c.lookup("java:global/myStation/DataGeneratorAjbService!itu.station.dataGenerator.DataGeneratorEjbSignature");
        } catch (NamingException ne) {
//            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "exception caught", ne);
            throw new RuntimeException(ne);
        }
    }
    public static PrelevementSignature getPrelevementEjbService() {
        try {
            Context c = new InitialContext();
            System.out.println("Insertion facture from client servlet Preelevemet");
            return (PrelevementSignature) c.lookup("java:global/myStation/PrelevementService!itu.station.prelevement.PrelevementSignature");
        } catch (NamingException ne) {
//            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "exception caught", ne);
            throw new RuntimeException(ne);
        }
    }
    public static AchatExecutorSignature getAchatExecutor(){
        try {
            Context c = new InitialContext();
            return (AchatExecutorSignature) c.lookup("java:global/myStation/AchatExecutor!itu.station.stock.achat.AchatExecutorSignature");
        } catch (NamingException ne) {
//            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "exception caught", ne);
            throw new RuntimeException(ne);
        }
    }
    public static EjbStation2 getEjbLocalServer(){
        try {
            Context c = new InitialContext();
            System.out.println("Insertion facture from client servlet");
            return (EjbStation2) c.lookup("java:global/myStation/EjbStationService!itu.station.ejbService.EjbStation2");
        } catch (NamingException ne) {
            throw new RuntimeException(ne);
        }
    }


}
