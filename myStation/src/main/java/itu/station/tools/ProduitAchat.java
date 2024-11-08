package itu.station.tools;

public class ProduitAchat {
    private int produitId;
    private double quantite;
    private double puVente;

    public int getProduitId() {
        return produitId;
    }

    public void setProduitId(int produitId) {
        this.produitId = produitId;
    }

    public double getQuantite() {
        return quantite;
    }

    public void setQuantite(double quantite) {
        this.quantite = quantite;
    }

    public double getPuVente() {
        return puVente;
    }

    public void setPuVente(double puVente) {
        this.puVente = puVente;
    }

    @Override
    public String toString() {
        return "ProduitAchat{" +
                "produitId=" + produitId +
                ", quantite=" + quantite +
                ", puVente=" + puVente +
                '}';
    }
}
