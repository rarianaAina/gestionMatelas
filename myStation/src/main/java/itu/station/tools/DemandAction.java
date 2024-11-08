package itu.station.tools;

public class DemandAction {
    private String idDemande;
    private boolean valider;
    private boolean refuser;

    public DemandAction() {
    }
    public DemandAction(String idDemande, boolean valider, boolean refuser) {
        this.idDemande = idDemande;
        this.valider = valider;
        this.refuser = refuser;
    }

    public String getIdDemande() {
        return idDemande;
    }

    public boolean isValider() {
        return valider;
    }

    public boolean isRefuser() {
        return refuser;
    }
}

