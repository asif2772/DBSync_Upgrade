<%@ page import="auth.User;" %>
<%@ page import="bv.BudgetViewDatabaseService; bv.UserLog; auth.User; org.springframework.security.core.context.SecurityContextHolder; org.springframework.security.core.Authentication; org.springframework.security.core.context.SecurityContext" %>

<%
//    def bsCoyId = sec.loggedInUserInfo(field: 'business_company_id');
//    println("Bussinsess company ID : "+bsCoyId);
    def CompanySetup = 1;//CompanySetup.executeQuery("select id from CompanySetup")
    //def budgetItemExpense = BudgetItemExpense.executeQuery("select id from BudgetItemExpense")
    //def budgetItemIncome = BudgetItemIncome.executeQuery("select id from BudgetItemIncome")
    //def vendorId = VendorMaster.executeQuery("select id from VendorMaster")
    //def customerId = CustomerMaster.executeQuery("select id from CustomerMaster")
    SecurityContext ctx = SecurityContextHolder.getContext()
    Authentication auth = ctx.getAuthentication()
    String username = auth.getName()
    User user = User.findByUsername(username)
    Integer authUserId = user.getAt('id')
%>

<div>
<div class="blue">
<ul id="mega-menu-1" class="mega-menu">



    <%if(authUserId==1){%>
    <li><g:link url="[action: 'list', controller: 'databaseSync']"><g:message code="bv.menu.Home" default="Home"/></g:link></li>
    <li>
        <g:link url="#"><g:message code="bv.menu.dbreports"
                                   default="DB Reports"/></g:link>
        <ul>
            <li><g:link url="[controller: 'reports', action: 'reportInvoiceWithoutBudget']"><g:message code="bv.menu.invoiceWithoutBudget"
                                                                                     default="Invoice Without Budget Report"/></g:link></li>
            <li><g:link url="[controller: 'reports', action: 'databaseMaintenance']">
                <g:message code="bv.menu.databaseMaintenance" default="Database Maintenance Report"/></g:link></li>
           %{-- <li><g:link url="[controller: 'reports', action: 'exportSummaryView']"><g:message code="bv.menu.excelSummaryView"
                                                                                  default="Summary Report"/></g:link></li>--}%

        <li><g:link url="[controller: 'reports', action: 'databaseWithoutCompanyBankAccount']"><g:message code="bv.menu.databaseWithoutCompanyBankAccount"
                                                                                          default="Database Without Company Bank Account"/></g:link></li>
        <li><g:link url="[controller: 'reports', action: 'getTransactionsAmount']">Get Transaction Amount</g:link></li>

        </ul>
    </li>
    <li>
        <g:link url="#"><g:message code="bv.menu.dbmanipulating"
                                   default="DB Manipulation "/></g:link>
        <ul>
            <li><g:link url="[controller: 'Restore', action: 'restoreDb']"><g:message code="bv.menu.dbrestore"
                                                                                                       default="Restore"/></g:link></li>
            <li><g:link url="[controller: 'Restore', action: 'dumpDB']">
                <g:message code="bv.menu.dbDump" default="Dump DB"/></g:link></li>
        </ul>
    </li>
    <% } %>


<li>
    <g:link url="#"><sec:loggedInUserInfo field="username"/></g:link>
    <ul>
        <li><g:link controller="logout" action="index"><g:message code="bv.menu.Logout" default="Logout"/></g:link></li>
        <%if(authUserId==1){%>
            <li>
                <g:link controller="user" action="updatePassword"
                        params="[id: sec.loggedInUserInfo(field: 'id'), st: 1]"><g:message code="bv.menu.ChangePassword"
                                                                                           default="Change Password"/></g:link>
            </li>
        <% } %>
    </ul>
</li>
</ul>

</div>
</div>