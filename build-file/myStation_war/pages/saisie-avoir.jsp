<%@page import="itu.station.prelevement.*"%>
<%@page import="itu.station.tools.*"%>
<%@page import="java.sql.*"%>
<%@page import="bean.*"%>
<%@page import="utilitaire.*"%>
<%@page import="vente.*"%>
<%@ page import="vente.VenteDetails" %>
<%
    Connection connection = new UtilDB().GetConn("gallois","gallois");
    FactureClient fc = (FactureClient) session.getAttribute("facture");
    VenteDetails[] venteDetails = fc.getPrelevement().viser(connection).getVenteDetailsNonGrp(connection);
%>
<form action="${pageContext.request.contextPath}/genererAvoir" method="post">
    <input type="text" name="idClient" value="">
    <input type="text" name="idProduit" value="<%=venteDetails[0].getIdProduit()%>" />
    <input type="number" name="montant" />
    <input type="submit" />
</form>