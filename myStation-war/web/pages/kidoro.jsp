<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Page d'accueil - Kidoro</title>
</head>
<body>
<h2>Bienvenue sur Kidoro</h2>

<ul>
    <li><a href="${pageContext.request.contextPath}/index.jsp?but=pages/generer-bloc.jsp">Générer bloc</a></li>
    <li><a href="${pageContext.request.contextPath}/index.jsp?but=pages/transformation.jsp">Transformation</a></li>
    <li><a href="${pageContext.request.contextPath}/index.jsp?but=pages/etatStock.jsp">Etat de Stock</a></li>
    <li><a href="${pageContext.request.contextPath}/index.jsp?but=pages/checkStock.jsp">Vérification du Stock Réel</a></li>
    <li><a href="${pageContext.request.contextPath}/index.jsp?but=pages/blocs.jsp">Blocs</a></li>
    <li><a href="${pageContext.request.contextPath}/index.jsp?but=pages/uploadData.jsp">Ajout datas</a></li>
</ul>
</body>
</html>
