<%@ page import="itu.station.kidoro.Blocs" %>
<%@ page import="utilitaire.UtilDB" %>
<%@ page import="java.sql.Connection" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%
  // Connexion à la base de données
  Connection connection = null;
  Blocs[] blocsNonTransformes = null;

  try {
    connection = new UtilDB().GetConn("mystation", "mystation");
    Blocs blocs = new Blocs();

    // Récupérer les blocs non transformés
    blocsNonTransformes = blocs.getAllBlocsNonTransformes(connection);
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

<!DOCTYPE html>
<html>
<head>
  <meta charset="UTF-8">
  <title>Transformation</title>
  <script>
    function calculerVolume() {
      var longueur = parseFloat(document.getElementById("longueurReste").value) || 0;
      var largeur = parseFloat(document.getElementById("largeurReste").value) || 0;
      var hauteur = parseFloat(document.getElementById("hauteurReste").value) || 0;
      var volume = longueur * largeur * hauteur;
      document.getElementById("volume").value = volume.toFixed(2);
      document.getElementById("volumeAffichage").innerText = "Volume: " + volume.toFixed(2) + " m³";
    }
  </script>
</head>
<body>
<h2>Transformation</h2>


<form action="${pageContext.request.contextPath}/traitementTransformation" method="post">
  <div>
    <label for="bloc">Bloc</label>
    <select name="idBloc" id="bloc">
      <%
        if (blocsNonTransformes != null && blocsNonTransformes.length > 0) {
          for (int i = 0; i < blocsNonTransformes.length; i++) {
      %>
      <option value="<%=blocsNonTransformes[i].getIdBloc()%>">
        <%=blocsNonTransformes[i].getIdBloc()%>
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
  <label for="dateTransformation">Date de Transformation :</label>
  <input type="date" id="dateTransformation" name="dateTransformation" required>
  </div>
  <div>
    <label for="ecartAccepte">Ecart Accepté :</label>
    <input type="number" id="ecartAccepte" name="ecartAccepte" step="1" required>
  </div>
  <div>
    <h3> Formes Usuelles: </h3>
  </div>
  <label for="fu1">F.U1 (XL) :</label>
  <input type="number" id="fu1" name="fu1" step="1" required>
  <br><br>

  <label for="fu2">F.U2 (L) :</label>
  <input type="number" id="fu2" name="fu2" step="1" required>
  <br><br>

  <label for="fu3">F.U3 (M) :</label>
  <input type="number" id="fu3" name="fu3" step="1" required>
  <br><br>

  <label for="fu4">F.U4 (S) :</label>
  <input type="number" id="fu4" name="fu4" step="1" required>
  <br><br>

  <div>
    <h3> Reste: </h3>
  </div>

  <label for="longueurReste">Longueur :</label>
  <input type="text" id="longueurReste" name="longueurReste" step="0.01" required oninput="calculerVolume()">
  <br><br>

  <label for="largeurReste">Largeur :</label>
  <input type="text" id="largeurReste" name="largeurReste" step="0.01" required oninput="calculerVolume()">
  <br><br>

  <label for="hauteurReste">Hauteur :</label>
  <input type="text" id="hauteurReste" name="hauteurReste" step="0.01" required oninput="calculerVolume()">
  <br><br>

  <p id="volumeAffichage" style="font-weight: bold; display: none;">Volume: 0.00 m³</p><!-- Affichage du volume ici -->
  <input type="hidden" id="volume" name="volume" value="0.00">

  <button type="submit">Procéder</button>
</form>

<!-- Vérifier si un message d'erreur est passé -->
<c:if test="${not empty errorMessage}">
  <script type="text/javascript">
    alert("${errorMessage}");
  </script>
</c:if>

</body>
</html>
