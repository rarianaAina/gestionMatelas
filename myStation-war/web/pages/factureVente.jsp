<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.sql.*"%>
<%@ page import="utilitaire.UtilDB"%>

<html>
<head>
  <title>Facture Vente</title>
  <script>
    function encaisser(idVente) {
      fetch('<%= request.getContextPath() %>/api/vente/achatA', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify({ id: idVente })
      })
              .then(response => {
                if (response.ok) {
                  return response.json();
                } else {
                  throw new Error('Erreur lors de l\'encaissement');
                }
              })
              .then(data => {
                alert('Encaissement effectué avec succès!');
                window.location.href = '<%= request.getContextPath() %>/index.jsp?but=pages/encaissement.jsp';
              })
              .catch(error => {
                console.error('Erreur:', error);
                alert('Une erreur s\'est produite : ' + error.message);
              });
    }
  </script>

</head>
<body>

<div>
  <h1>Facture</h1>

  <%
    String idFacture = request.getParameter("idFacture");
    Connection connection = null;
    double montantVente = 0.0;

    try {
      connection = new UtilDB().GetConn("gallois", "gallois");
      String query = "SELECT MONTANTTOTAL FROM ventemontant WHERE id = ?";
      PreparedStatement ps = connection.prepareStatement(query);
      ps.setString(1, idFacture);
      ResultSet rs = ps.executeQuery();

      if (rs.next()) {
        montantVente = rs.getDouble("MONTANTTOTAL");
      }

      rs.close();
      ps.close();

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

  <p>Id Vente: <c:out value="${param.idFacture}" /></p>
  <p>Date Vente: <c:out value="${param.daty}" /></p>
  <p>Montant de la Vente: <%= montantVente %> AR</p>

  <button onclick="encaisser('<c:out value="${param.idFacture}" />')">Encaisser</button>
</div>

</body>
</html>
