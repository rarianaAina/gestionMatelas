<!doctype html>
<html lang="en">
<head>
    <meta charset="utf-8">
    <title>Station</title>
    <base href="/">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <link rel="icon" type="image/x-icon" href="favicon.ico">
    <link href="https://fonts.googleapis.com/icon?family=Material+Icons" rel="stylesheet">
    <style>
        /* Style de base pour le menu */
        nav ul {
            list-style-type: none;
            padding: 0;
        }

        nav ul li {
            position: relative;
            display: inline-block;
        }

        nav ul li a {
            text-decoration: none;
            padding: 10px 15px;
            display: block;
            background-color: #4CAF50;
            color: white;
            border-radius: 5px;
        }

        /* Sous-menu caché par défaut */
        .sub-menu {
            display: none;
            position: absolute;
            top: 100%;
            left: 0;
            background-color: #f9f9f9;
            box-shadow: 0px 8px 16px rgba(0, 0, 0, 0.2);
            min-width: 160px;
            border-radius: 5px;
        }

        .sub-menu li {
            display: block;
        }

        .sub-menu li a {
            background-color: #f1f1f1;
            color: black;
            padding: 10px;
            border-radius: 0;
        }

        .sub-menu li a:hover {
            background-color: #ddd;
        }

        /* Afficher le sous-menu au survol du parent */
        nav ul li:hover .sub-menu {
            display: block;
        }
    </style>
</head>
<body>
<header>
    <nav>
        <ul>
<%--
            <li><a href="${pageContext.request.contextPath}/index.jsp?but=pages/prelevement.jsp">Prélevement</a></li>
            <li><a href="${pageContext.request.contextPath}/index.jsp?but=pages/bilan-form.jsp">Bilan</a></li>
            <li><a href="${pageContext.request.contextPath}/index.jsp?but=pages/jauge-saisie.jsp">Test</a></li>
            <li><a href="${pageContext.request.contextPath}/index.jsp?but=pages/listeVentes.jsp">Ventes boutique</a></li>
--%>

            <!-- Menu déroulant Kidoro -->
            <li>
                <a href="#">Kidoro ETU000739</a>
                <ul class="sub-menu">
                    <li><a href="${pageContext.request.contextPath}/index.jsp?but=pages/generer-bloc.jsp">Générer bloc</a></li>
                    <li><a href="${pageContext.request.contextPath}/index.jsp?but=pages/transformation.jsp">Transformation</a></li>
                    <li><a href="${pageContext.request.contextPath}/index.jsp?but=pages/etatStock.jsp">Etat de Stock</a></li>
                    <li><a href="${pageContext.request.contextPath}/index.jsp?but=pages/checkStock.jsp">Vérification du Stock Réel</a></li>
                    <li><a href="${pageContext.request.contextPath}/index.jsp?but=pages/blocs.jsp">Blocs</a></li>
                    <li><a href="${pageContext.request.contextPath}/index.jsp?but=pages/updatePR.jsp">Update</a></li>
                    <li><a href="${pageContext.request.contextPath}/index.jsp?but=pages/deleteDonnees.jsp">Suppression</a></li>
                    <li><a href="${pageContext.request.contextPath}/index.jsp?but=pages/uploadData.jsp">Ajout datas</a></li>
                    <li><a href="${pageContext.request.contextPath}/index.jsp?but=pages/afficherPrix.jsp">PR Machines</a></li>
                </ul>
            </li>
        </ul>
    </nav>
</header>
<section>
    <% try { %>
    <jsp:include page='<%=request.getParameter("but")%>'/>
    <% } catch(Exception e) {
        out.println(e.getMessage());
    } %>
</section>
</body>
</html>
