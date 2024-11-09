package itu.station.human;

import bean.CGenUtil;
import bean.ClassMAPTable;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import utilitaire.UtilDB;

import java.sql.Connection;
import java.sql.Date;

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
    public Employe(int idEmploye, String nom, String prenom, Date dateNaissance, Date dateEmbauche, String poste,
                   String email, String telephone, String motDePasse, int departementId) {
        this.idEmploye = idEmploye;
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
        return (Employe[]) CGenUtil.rechercher(new Employe(), null, null, c, "");
    }

    // Méthode pour convertir la liste des Employes en JSON
    public String employesToJson(Connection c) throws Exception {
        Employe[] employesRecus = getAllEmployes(c);

        Gson gson = new GsonBuilder().create();
        return gson.toJson(employesRecus);
    }
}
