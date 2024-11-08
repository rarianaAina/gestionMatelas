<%@page import="itu.station.tools.*"%>
<%@page import="jauge.*"%>
<%@page import="bean.*"%>
<%@page import="utilitaire.*"%>
<%@page import="java.sql.*"%>
<%
    Connection connection = new UtilDB().GetConn("gallois","gallois");
    Cuve[] cuves = (Cuve[]) CGenUtil.rechercher(new Cuve(),null,null," ");
    JaugeCpl[] jauges = (JaugeCpl[]) CGenUtil.rechercher(new JaugeCpl(),null,null,connection," ");
    connection.close();
%>

<form action="${pageContext.request.contextPath}/jauge" method="post">
    <h1>Jauger</h1>
    <div>
        <label>Cuve</label>
        <select name="idCuve" id="">
            <% for(int i=0;i<cuves.length;i++){ %>
                <option value="<%=cuves[i].getId()%>"><%=cuves[i].getLabel()%></option>
            <% } %>
        </select>
    </div>
    <div>
        <label>Date</label>
        <input type="date" name="daty">
    </div>
    <div>
        <label>Mesure (cm)</label>
        <input type="number" name="mesure" step="0.1" placeholder="Mesure">
    </div>
    <input type="submit">
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
        <% for(int i=0;i<jauges.length;i++){ %>
            <tr>
                <td><a href="${pageContext.request.contextPath}/anomalie?idJauge=<%=jauges[i].getId()%>"><%=jauges[i].getId()%></a></td>
                <td><%=jauges[i].getQte()%></td>
                <td><%=jauges[i].getDaty()%></td>
                <td><%=jauges[i].getVal()%></td>
            </tr>
        <% } %>
    </tbody>
</table>