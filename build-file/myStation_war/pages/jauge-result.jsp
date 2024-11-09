<%@page import="jauge.*" %>
<%
    Jauge jauge = (Jauge) request.getAttribute("jauge");
    if(jauge == null){
        out.println("The jauge is empty");
    }
%>
<form action="${pageContext.request.contextPath}/anomalie" method="post">
    <h1>Voir existence d'anomalie </h1>
    <p> Résultat de la jauge : <%= jauge.getQte() %></p>
    <p> Date : <%= jauge.getDaty() %></p>
    <p> Id cuve : <%= jauge.getIdMagasin() %></p>
    <input type="hidden" name="idJauge" value="<%=jauge.getId()%>">
    <input type="submit"/>
</form>

