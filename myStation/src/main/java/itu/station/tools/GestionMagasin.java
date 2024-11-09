package itu.station.tools;
import itu.station.human.CandidatResource;
import itu.station.human.CompetenceResource;
import itu.station.human.EmployeResource;
import itu.station.human.OffreEmploiResource;
import itu.station.prelevement.PrelevementQuantity;
import itu.station.prelevement.PrelevementQuantityResource;
import itu.station.prelevement.PrelevementResource;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;

@ApplicationPath("/api")
public class GestionMagasin extends Application {

    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> resources = new HashSet<>();
        resources.add(ProduitResource.class);
        resources.add(VenteResource.class);
        resources.add(VenteDetailsResource.class);
        resources.add(DemandesResource.class);
        resources.add(CorsFilter.class);
        resources.add(UserResource.class);
        resources.add(ClientResource.class);
        resources.add(DepartementResource.class);
        resources.add(StockResource.class);
        resources.add(PrelevementResource.class);
        resources.add(PrelevementQuantityResource.class);
        resources.add(ProformatsResource.class);
        resources.add(CommandesResource.class);
        resources.add(EmployeResource.class);
        resources.add(OffreEmploiResource.class);
        resources.add(CompetenceResource.class);
        resources.add(CandidatResource.class);
        return resources;
    }
}
