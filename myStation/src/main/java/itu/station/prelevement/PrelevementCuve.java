package itu.station.prelevement;

public class PrelevementCuve extends Prelevement{
    String id_cuve;

    public String getId_cuve() {
        return id_cuve;
    }

    public void setId_cuve(String id_cuve) {
        this.id_cuve = id_cuve;
    }

    public PrelevementCuve() {
        this.setNomTable("v_prelevement_cuve");
    }
}
