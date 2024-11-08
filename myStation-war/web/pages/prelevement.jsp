<%@page import="itu.station.tools.Pompiste"%>
<%@page import="itu.station.tools.Pompe"%>
<%@page import="itu.station.ejbService.*"%>
<%@page import="itu.station.localEjbClient.*"%>
<%@page import="itu.station.prelevement.*"%>
<%@page import="java.sql.*"%>
<%@page import="bean.*"%>
<%@page import="utilitaire.*"%>
<%@page import="clientEJB.*"%>
<%@page import="prelevement.*"%>
<%

    Connection connection = new UtilDB().GetConn();
    Pompe[] pompes = (Pompe[]) CGenUtil.rechercher(new Pompe(),null,null,connection," ");
    Pompiste[] pompistes = (Pompiste[]) CGenUtil.rechercher(new Pompiste(),null,null,connection," ");
    PrelevementQuantityCpl[] prels = (PrelevementQuantityCpl[]) CGenUtil.rechercher(new PrelevementQuantityCpl(),null,null,connection," ");
    connection.close();
%>
<%--<a href="${pageContext.request.contextPath}/prelevement">Générer</a>--%>
<form action="${pageContext.request.contextPath}/prelevement" method="post">
    <label>Quantité</label>
    <div>
        <input type="number" name="qte">
    </div>
    <div>
        <label for="">Produit</label>
        <!-- <input type="text"> -->
         <select name="idPompe" id="pompe">
         <%
            for(int i = 0 ; i < pompes.length ; i++) {
         %>
            <option value="<%=pompes[i].getId()%>">
                <%=pompes[i].getNom()%>
            </option>
         <%
            }
         %>
         </select>
    </div>
    <div>
    <label>Pompiste</label>
        <select name="idUtilisateur" id="Pompiste">
            <%
                for(int i = 0 ; i < pompistes.length ; i++) {
            %>
                <option value="<%=pompistes[i].getId()%>">
                    <%=pompistes[i].getNom()%>
                </option>
            <%
               }
            %>
        </select>
    </div>
    <div>
        <label>Heure</label>
        <input type="text" name="heure" placeholder="HH:MM:SS">
    </div>
    <div>
        <label>Date</label>
        <input type="date" name="daty" >
    </div>
    <div>
        <input type="submit">
    </div>
</form>
<table>
    <thead>
        <tr>
            <th>ID</th>
            <th>Quantité</th>
            <th>Date</th>
            <th>Magasin</th>
        </tr>
    </thead>
    <tbody>
    <%
    try{
        for(int i=0;i < prels.length;i++){ %>
          <tr>
              <td><a href="${pageContext.request.contextPath}/prelevement?idFacture=<%=prels[i].getIdAct()%>"><%=prels[i].getIdAct()%></a></td>
              <td><%=prels[i].getQty()%></td>
              <td><%=prels[i].getDaty()%></td>
              <td><%=prels[i].getId_cuve()%></td>
          </tr>
        <% }
        }catch(Exception e){
            e.printStackTrace();
        }
    %>
    </tbody>
</table>