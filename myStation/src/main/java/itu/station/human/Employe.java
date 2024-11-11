package itu.station.human;

import bean.CGenUtil;
import bean.ClassMAPTable;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import itu.station.tools.Commandes;
import utilitaire.UtilDB;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class Employe extends ClassMAPTable {

    private int idEmploye;



    private String nom;
    private String prenom;
    private Date dateNaissance;
    private Date dateEmbauche;
    private String poste;
    private String email;
    private String telephone;
    private String motDePasse;
    private int departementId;

    // Constructeur avec paramètres
    public Employe(String nom, String prenom, Date dateNaissance, Date dateEmbauche, String poste,
                   String email, String telephone, String motDePasse, int departementId) {
        this.nom = nom;
        this.prenom = prenom;
        this.dateNaissance = dateNaissance;
        this.dateEmbauche = dateEmbauche;
        this.poste = poste;
        this.email = email;
        this.telephone = telephone;
        this.motDePasse = motDePasse;
        this.departementId = departementId;
    }

    public int getIdEmploye() {
        return idEmploye;
    }

    public void setIdEmploye(int idEmploye) {
        this.idEmploye = idEmploye;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public Date getDateNaissance() {
        return dateNaissance;
    }

    public void setDateNaissance(Date dateNaissance) {
        this.dateNaissance = dateNaissance;
    }

    public Date getDateEmbauche() {
        return dateEmbauche;
    }

    public void setDateEmbauche(Date dateEmbauche) {
        this.dateEmbauche = dateEmbauche;
    }

    public String getPoste() {
        return poste;
    }

    public void setPoste(String poste) {
        this.poste = poste;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getMotDePasse() {
        return motDePasse;
    }

    public void setMotDePasse(String motDePasse) {
        this.motDePasse = motDePasse;
    }

    public int getDepartementId() {
        return departementId;
    }

    public void setDepartementId(int departementId) {
        this.departementId = departementId;
    }
    // Constructeur par défaut
    public Employe() {
        this.setNomTable("EMPLOYE");
    }

    // Override pour retourner l'ID de l'enregistrement
    @Override
    public String getTuppleID() {
        return String.valueOf(idEmploye);
    }

    // Override pour retourner le nom de l'attribut ID
    @Override
    public String getAttributIDName() {
        return "idEmploye";
    }

    // Getters et setters
    // (getter et setters pour chaque attribut)

    // Méthode pour obtenir tous les Employes
    public Employe[] getAllEmployes(Connection c) throws Exception {
        if (c == null) {
            c = new UtilDB().GetConn("gestion", "gestion");
        }

        List<Employe> employes = new ArrayList<>();
        String sql = "SELECT * FROM EMPLOYE";

        try (PreparedStatement stmt = c.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Employe employe = new Employe();
                employe.setIdEmploye(rs.getInt("ID_EMPLOYE"));
                employe.setNom(rs.getString("NOM"));
                employe.setPrenom(rs.getString("PRENOM"));
                employe.setDateNaissance(rs.getDate("DATE_NAISSANCE"));
                employe.setDateEmbauche(rs.getDate("DATE_EMBAUCHE"));
                employe.setPoste(rs.getString("POSTE"));
                employe.setEmail(rs.getString("EMAIL"));
                employe.setTelephone(rs.getString("TELEPHONE"));
                employe.setMotDePasse(rs.getString("MOT_DE_PASSE"));
                employe.setDepartementId(rs.getInt("DEPARTEMENT_ID"));

                employes.add(employe);
            }
        }

        return employes.toArray(new Employe[0]);
    }

    public Employe[] getAllEmployesById(Connection c) throws Exception {
        if (c == null) {
            c = new UtilDB().GetConn("gestion", "gestion");
        }

        List<Employe> employes = new ArrayList<>();
        String sql = "SELECT * FROM EMPLOYE";

        try (PreparedStatement stmt = c.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Employe employe = new Employe();
                employe.setIdEmploye(rs.getInt("ID_EMPLOYE"));
                employe.setNom(rs.getString("NOM"));
                employe.setPrenom(rs.getString("PRENOM"));
                employe.setDateNaissance(rs.getDate("DATE_NAISSANCE"));
                employe.setDateEmbauche(rs.getDate("DATE_EMBAUCHE"));
                employe.setPoste(rs.getString("POSTE"));
                employe.setEmail(rs.getString("EMAIL"));
                employe.setTelephone(rs.getString("TELEPHONE"));
                employe.setMotDePasse(rs.getString("MOT_DE_PASSE"));
                employe.setDepartementId(rs.getInt("DEPARTEMENT_ID"));

                employes.add(employe);
            }
        }

        return employes.toArray(new Employe[0]);
    }

    // Méthode pour convertir la liste des Employes en JSON
    public String employesToJson(Connection c) throws Exception {
        Employe[] employesRecus = getAllEmployes(c);

        Gson gson = new GsonBuilder().create();
        return gson.toJson(employesRecus);
    }
    public static String toJson(Employe[] employes) {
        Gson gson = new GsonBuilder().create();
        return gson.toJson(employes);
    }
}
