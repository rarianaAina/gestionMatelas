package itu.station.tools;

import bean.CGenUtil;
import bean.ClassMAPTable;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import utilitaire.UtilDB;

import java.sql.Connection;

public class Client extends ClassMAPTable {
    String id;
    String nom;
    String telephone;
    String mail;
    String adresse;
    String remarque;
    String compte;

    @Override
    public String getTuppleID() {
        return "id";
    }

    @Override
    public String getAttributIDName() {
        return id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getAdresse() {
        return adresse;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }

    public String getRemarque() {
        return remarque;
    }

    public void setRemarque(String remarque) {
        this.remarque = remarque;
    }

    public String getCompte() {
        return compte;
    }

    public void setCompte(String compte) {
        this.compte = compte;
    }

    public Client() {
        this.setNomTable("client");
    }

    @Override
    public void construirePK(Connection c) throws Exception {
        this.preparePk("CL", "GET_SEQ_CLIENT");
        this.setId(makePK(c));
    }

    // Exemple d'une méthode pour obtenir plus de détails sur un autre objet lié
    public Client[] getAllClients(Connection c) throws Exception {
        if (c == null) c = new UtilDB().GetConn();

        Client[] clients = (Client[]) CGenUtil.rechercher(new Client(), null, null, c, "");
        return clients;
    }
    public String clientsToJson(Connection c) throws Exception {
        Client[] clientsRecus = getAllClients(c);

        Gson gson = new GsonBuilder().create();
        return gson.toJson(clientsRecus);
    }
}
