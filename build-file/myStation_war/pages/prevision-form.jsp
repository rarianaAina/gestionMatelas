    <%@ page import="java.sql.Date, java.time.LocalDate, java.util.ArrayList" %>
    <%--<%@ page import="your.package.name.*" %>--%>
    <%@ page import="user.UserEJB" %>
    <%@ page import="vente.FactureCF" %>
    <%@ page import="prevision.Prevision" %>
    <%@ page import="bean.CGenUtil" %>
    <%@ page import="affichage.PageUpdateMultiple" %>
    <!-- Assurez-vous de remplacer par le bon package -->
        <%
    try {
        UserEJB u = (UserEJB) session.getValue("u");
        String id = request.getParameter("id");
        String classe = request.getParameter("classe");
        String dateParam = request.getParameter("date"); // Récupérer la date fournie par l'utilisateur
        LocalDate selectedDate = dateParam != null ? LocalDate.parse(dateParam) : LocalDate.now();

        FactureCF mere = (FactureCF) (Class.forName(classe).newInstance());
        Prevision fille = new Prevision();
        fille.setNomTable("PREVISION");
        fille.setIdFacture(id);
        fille.setDaty(Date.valueOf(selectedDate)); // Filtrer les prévisions par la date sélectionnée

        Prevision[] filles = (Prevision[]) CGenUtil.rechercher(fille, null, null, "AND daty = '" + Date.valueOf(selectedDate) + "'");
        if (filles.length == 0) {
            filles = new Prevision[1];
            filles[0] = new Prevision();
            filles[0].setDesignation("Aucune prévision trouvée pour cette date");
        }

        PageUpdateMultiple pi = new PageUpdateMultiple(mere, fille, filles, request, u, filles.length);
        pi.setLien((String) session.getValue("lien"));

        // Préparation des libellés et des champs visibles
        pi.getFormufle().getChamp("designation_0").setLibelle("Désignation");
        pi.getFormufle().getChamp("credit_0").setLibelle("Credit");
        pi.getFormufle().getChamp("debit_0").setLibelle("Debit");
        pi.getFormufle().getChamp("daty_0").setLibelle("Date");
        pi.getFormufle().getChampMulitple("id").setVisible(false);
        pi.getFormufle().getChampMulitple("idFacture").setVisible(false);

        pi.preparerDataFormu();
        pi.getFormu().makeHtmlInsertTabIndex();
        pi.getFormufle().setApres("vente/planPaiement-saisie.jsp");
        pi.getFormufle().makeHtmlInsertTableauIndex();
%>

    <div class="content-wrapper">
        <div class="row">
            <div class="col-md-12">
                <div class="box-fiche">
                    <div class="box">
                        <div class="box-title with-border">
                            <h1>Prévision</h1>
                        </div>
                        <div class="box-body">
                            <!-- Formulaire pour sélectionner une date -->
                            <form class="container" action="<%= pi.getLien() %>?id=<%= id %>" method="get">
                                <input type="hidden" name="classe" value="<%= classe %>">
                                <div>
                                    <label for="date">Sélectionner une date :</label>
                                    <input type="date" id="date" name="date" value="<%= selectedDate %>">
                                    <button type="submit">Afficher la prévision</button>
                                </div>
                            </form>

                            <!-- Formulaire pour afficher les prévisions -->
                            <form class='container' action="<%= pi.getLien() %>?but=apresPlanPaiement.jsp&id=<%= id %>" method="post">
                                <%= pi.getFormufle().getHtmlTableauInsert() %>
                                <input name="acte" type="hidden" value="insertFille">
                                <input name="bute" type="hidden" value="<%= request.getParameter("bute") %>">
                                <input name="classeMere" type="hidden" value="prevision.MereFictif">
                                <input name="classefille" type="hidden" value="prevision.Prevision">
                                <input name="nombreLigne" type="hidden" value="<%= pi.getNombreLigne() %>">
                                <input name="colonneMere" type="hidden" value="idFacture">
                                <input name="idmere" type="hidden" value="<%= id %>">
                                <input name="classe" type="hidden" value="<%= classe %>">
                                <input name="table" type="hidden" value="<%= request.getParameter("table") %>">
                            </form>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>

        <%
    } catch (Exception e) {
        e.printStackTrace();
%>
    <script>
        alert('<%= e.getMessage() %>');
        history.back();
    </script>
        <% } %>
