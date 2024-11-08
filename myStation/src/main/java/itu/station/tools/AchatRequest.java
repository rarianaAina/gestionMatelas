package itu.station.tools;

import java.util.List;

public class AchatRequest {
    private double total;
    private List<ProduitAchat> produits;

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public List<ProduitAchat> getProduits() {
        return produits;
    }

    public void setProduits(List<ProduitAchat> produits) {
        this.produits = produits;
    }

    @Override
    public String toString() {
        return "AchatRequest{" +
                "total=" + total +
                ", produits=" + produits +
                '}';
    }
}
