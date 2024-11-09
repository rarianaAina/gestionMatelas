package itu.station.human;

import bean.CGenUtil;
import bean.ClassMAPTable;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import utilitaire.UtilDB;

import java.sql.Connection;
import java.sql.Date;

public class OffreEmploi extends ClassMAPTable {

    private int idOffre;


    private String titre;
    private String description;
    private Date datePublication;
    private Date dateExpiration;
    private int departementId;

    // Constructeur avec paramètres
    public OffreEmploi(int idOffre, String titre, String description, Date datePublication, Date dateExpiration, int departementId) {
        this.idOffre = idOffre;
        this.titre = titre;
        this.description = description;
        this.datePublication = datePublication;
        this.dateExpiration = dateExpiration;
        this.departementId = departementId;
    }

    public int getIdOffre() {
        return idOffre;
    }

    public void setIdOffre(int idOffre) {
        this.idOffre = idOffre;
    }

    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getDatePublication() {
        return datePublication;
    }

    public void setDatePublication(Date datePublication) {
        this.datePublication = datePublication;
    }

    public Date getDateExpiration() {
        return dateExpiration;
    }

    public void setDateExpiration(Date dateExpiration) {
        this.dateExpiration = dateExpiration;
    }

    public int getDepartementId() {
        return departementId;
    }

    public void setDepartementId(int departementId) {
        this.departementId = departementId;
    }

    // Constructeur par défaut
    public OffreEmploi() {
        this.setNomTable("OFFRE_EMPLOI");
    }

    @Override
    public String getTuppleID() {
        return String.valueOf(idOffre);
    }

    @Override
    public String getAttributIDName() {
        return "idOffre";
    }

    // Getters et setters

    // Méthode pour obtenir tous les Offres d'emploi
    public OffreEmploi[] getAllOffres(Connection c) throws Exception {
        if (c == null) {
            c = new UtilDB().GetConn("gestion", "gestion");
        }
        return (OffreEmploi[]) CGenUtil.rechercher(new OffreEmploi(), null, null, c, "");
    }

    public String offresToJson(Connection c) throws Exception {
        OffreEmploi[] offresRecus = getAllOffres(c);

        Gson gson = new GsonBuilder().create();
        return gson.toJson(offresRecus);
    }
}
