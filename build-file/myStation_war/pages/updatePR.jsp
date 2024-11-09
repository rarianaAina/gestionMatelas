<%@ page import="java.sql.Connection" %>
<%@ page import="itu.station.kidoro.Blocs" %>
<%@ page import="utilitaire.UtilDB" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%
    // Connexion à la base de données
    Connection connection = null;
    Blocs[] blocsNonTransformes = null;
    Blocs[] blocRehetra = null;
    try {
        connection = new UtilDB().GetConn("mystation", "mystation");
        Blocs blocs = new Blocs();

        // Récupérer les blocs non transformés
        blocsNonTransformes = blocs.getAllBlocsNonTransformes(connection);
        blocRehetra = blocs.getAllBlocs(connection);
    } catch (Exception e) {
        e.printStackTrace();
    } finally {
        if (connection != null) {
            try {
                connection.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
%>

<html>
<head>
    <title>Update PR</title>
</head>
<body>
<h1>Mettre à jour le prix de revient d'un bloc</h1>

<!-- Formulaire pour soumettre l'ID du bloc et le nouveau prix de revient -->
<form action="${pageContext.request.contextPath}/UpdateServlet" method="POST">
    <div>
        <label for="bloc">Bloc</label>
        <select name="idBloc" id="bloc" required>
            <%
                if (blocRehetra != null && blocRehetra.length > 0) {
                    for (int i = 0; i < blocRehetra.length; i++) {
            %>
            <option value="<%=blocRehetra[i].getIdBloc()%>">
                <%=blocRehetra[i].getIdBloc()%>
            </option>
            <%
                }
            } else {
            %>
            <option>Aucun bloc disponible</option>
            <%
                }
            %>
        </select>
    </div>
    <br><br>

    <div>
        <label for="nouveauPR">Nouveau prix de revient :</label>
        <input type="number" id="nouveauPR" name="nouveauPR" step="0.01" required>
    </div>
    <br><br>

    <div>
        <input type="submit" value="Mettre à jour" />
    </div>
</form>

</body>
</html>
