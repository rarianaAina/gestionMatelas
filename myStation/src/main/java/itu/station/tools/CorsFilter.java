package itu.station.tools;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Provider
public class CorsFilter implements ContainerResponseFilter {

/*    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) throws IOException {
        String origin = requestContext.getHeaderString("Origin");
        responseContext.getHeaders().add("Access-Control-Allow-Origin", "*");


        responseContext.getHeaders().add("Access-Control-Allow-Credentials", "true");
        responseContext.getHeaders().add("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD");
        responseContext.getHeaders().add("Access-Control-Allow-Headers", "origin, content-type, accept, authorization");
        responseContext.getHeaders().add("Access-Control-Max-Age", "1209600");
    }*/
        @Override
        public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) throws IOException {
            String origin = requestContext.getHeaderString("Origin");

            // Vérifie si l'origine de la requête est celle autorisée
            if ("http://localhost:5191".equals(origin)) {
                responseContext.getHeaders().add("Access-Control-Allow-Origin", origin);
                responseContext.getHeaders().add("Access-Control-Allow-Credentials", "true");
            }

            responseContext.getHeaders().add("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD");
            responseContext.getHeaders().add("Access-Control-Allow-Headers", "origin, content-type, accept, authorization");
            responseContext.getHeaders().add("Access-Control-Max-Age", "1209600");
}

}

