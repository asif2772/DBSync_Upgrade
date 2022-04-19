<%@ page import="auth.User;" %>
<%@ page import="bv.BudgetViewDatabaseService; bv.UserLog; auth.User; org.springframework.security.core.context.SecurityContextHolder; org.springframework.security.core.Authentication; org.springframework.security.core.context.SecurityContext" %>

<%
    SecurityContext ctx = SecurityContextHolder.getContext()
    Authentication auth = ctx.getAuthentication()
    String username = auth.getName()
    User user = User.findByUsername(username)
    Integer authUserId = user.getAt('id')
%>

<html>
<head>
    <meta name="layout" content="main">
    <title>DB Restore Output</title>
</head>

<body>

<div id="list-page-body-inner" class="content white-background scaffold-create" role="main" style="">
    %{--<div id="list-page">--}%
    <div class="searchField" style=" margin: 0 0 0 -2px;width: 1001px;">
        <div>
           <label class="searchHeadLineLabel">DB Restore Output</label>
        </div>
        <div class="navigationbtn" style="float: right; margin-right: 43px; margin-top:14px;">
            %{--<a href="/dashboardDetails/showBudgetAndBookingSummary"><g:message code="bv.dashboard.seeMyResult.label" default="See My Results" /></a>--}%
            <g:link controller="restore" action="restoreDb">Back</g:link>
        </div>
    </div>
<div style="padding-top: 12px;padding-left: 20px;">
    ${finalResult}
</div>


</div>

</body>

</html>