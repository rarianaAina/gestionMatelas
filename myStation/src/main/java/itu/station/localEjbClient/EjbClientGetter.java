package itu.station.localEjbClient;

import ejbServer.GeneralEJBLocalServer;
import magasin.MagasinLocalEJB;
import prelevement.PrelevementEJB;
import prelevement.PrelevementLocal;
import user.UserEJB;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public class EjbClientGetter {
    public static PrelevementLocal getPrelevementEjbService() {
        try {
            Context c = new InitialContext();
            System.out.println("Insertion prelegcvjfe ....");
            return (PrelevementLocal) c.lookup("java:global/myStation/PrelevementEJB!prelevement.PrelevementLocal");
        } catch (NamingException ne) {
//            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "exception caught", ne);
            throw new RuntimeException(ne);
        }
    }
    public static MagasinLocalEJB getMagasinEjbService() {
        try {
            Context c = new InitialContext();
            return (MagasinLocalEJB) c.lookup("java:module/MagasinEJBService!magasin.MagasinLocalEJB");
        } catch (NamingException ne) {
//            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "exception caught", ne);
            throw new RuntimeException(ne);
        }
    }
    public static GeneralEJBLocalServer getGeneralEjbService() throws NamingException {
        try {
            System.out.println("Insertion facture from EJB CLIENT GETTER IN MY STATION ");
            Context c = new InitialContext();
            return (GeneralEJBLocalServer) c.lookup("java:global/myStation/GeneralEJBService!ejbServer.GeneralEJBLocalServer");
        }catch (Exception e){
            throw e;
        }
    }
}
