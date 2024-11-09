package itu.station.human;

import bean.CGenUtil;
import bean.ClassMAPTable;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import utilitaire.UtilDB;

import java.sql.Connection;

public class CompetenceEmploye extends ClassMAPTable {

    private int idCompetenceEmploye;
    private int idEmploye;
    private int idCompetence;
    private int niveau;

    // Constructeur avec paramètres
    public CompetenceEmploye(int idCompetenceEmploye, int idEmploye, int idCompetence, int niveau) {
        this.idCompetenceEmploye = idCompetenceEmploye;
        this.idEmploye = idEmploye;
        this.idCompetence = idCompetence;
        this.niveau = niveau;
    }

    public int getIdCompetenceEmploye() {
        return idCompetenceEmploye;
    }

    public void setIdCompetenceEmploye(int idCompetenceEmploye) {
        this.idCompetenceEmploye = idCompetenceEmploye;
    }

    public int getIdEmploye() {
        return idEmploye;
    }

    public void setIdEmploye(int idEmploye) {
        this.idEmploye = idEmploye;
    }

    public int getIdCompetence() {
        return idCompetence;
    }

    public void setIdCompetence(int idCompetence) {
        this.idCompetence = idCompetence;
    }

    public int getNiveau() {
        return niveau;
    }

    public void setNiveau(int niveau) {
        this.niveau = niveau;
    }

    // Constructeur par défaut
    public CompetenceEmploye() {
        this.setNomTable("EMPLOYE_COMPETENCE");
    }

    @Override
    public String getTuppleID() {
        return String.valueOf(idCompetenceEmploye);
    }

    @Override
    public String getAttributIDName() {
        return "idCompetenceEmploye";
    }

    // Getters et setters

    // Méthode pour obtenir toutes les Competences des Employes
    public CompetenceEmploye[] getAllCompetenceEmploye(Connection c) throws Exception {
        if (c == null) {
            c = new UtilDB().GetConn("gestion", "gestion");
        }
        return (CompetenceEmploye[]) CGenUtil.rechercher(new CompetenceEmploye(), null, null, c, "");
    }

    public String competenceEmployeToJson(Connection c) throws Exception {
        CompetenceEmploye[] competenceEmployeRecus = getAllCompetenceEmploye(c);

        Gson gson = new GsonBuilder().create();
        return gson.toJson(competenceEmployeRecus);
    }
}
