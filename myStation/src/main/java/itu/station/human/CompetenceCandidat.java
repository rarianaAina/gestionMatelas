package itu.station.human;

import bean.CGenUtil;
import bean.ClassMAPTable;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import utilitaire.UtilDB;

import java.sql.Connection;

public class CompetenceCandidat extends ClassMAPTable {

    private int idCompetenceCandidat;
    private int id_Candidat;
    private int id_Competence;
    private int niveau;

    // Constructeur avec paramètres
    public CompetenceCandidat(int idCompetenceCandidat, int id_Candidat, int idCompetence, int niveau) {
        this.idCompetenceCandidat = idCompetenceCandidat;
        this.id_Candidat = id_Candidat;
        this.id_Competence = id_Competence;
        this.niveau = niveau;
    }

    public int getIdCompetenceCandidat() {
        return idCompetenceCandidat;
    }

    public void setIdCompetenceCandidat(int idCompetenceCandidat) {
        this.idCompetenceCandidat = idCompetenceCandidat;
    }

    public int getIdCandidat() {
        return id_Candidat;
    }

    public void setIdCandidat(int idCandidat) {
        this.id_Candidat = idCandidat;
    }

    public int getIdCompetence() {
        return id_Competence;
    }

    public void setIdCompetence(int idCompetence) {
        this.id_Competence = idCompetence;
    }

    public int getNiveau() {
        return niveau;
    }

    public void setNiveau(int niveau) {
        this.niveau = niveau;
    }

    // Constructeur par défaut
    public CompetenceCandidat() {
        this.setNomTable("COMPETENCECANDIDAT");
    }

    @Override
    public String getTuppleID() {
        return String.valueOf(idCompetenceCandidat);
    }

    @Override
    public String getAttributIDName() {
        return "idCompetenceCandidat";
    }

    // Getters et setters

    // Méthode pour obtenir toutes les Competences des Candidats
    public CompetenceCandidat[] getAllCompetenceCandidat(Connection c) throws Exception {
        if (c == null) {
            c = new UtilDB().GetConn("gestion", "gestion");
        }
        return (CompetenceCandidat[]) CGenUtil.rechercher(new CompetenceCandidat(), null, null, c, "");
    }

    public String competenceCandidatToJson(Connection c) throws Exception {
        CompetenceCandidat[] competenceCandidatRecus = getAllCompetenceCandidat(c);

        Gson gson = new GsonBuilder().create();
        return gson.toJson(competenceCandidatRecus);
    }
}
