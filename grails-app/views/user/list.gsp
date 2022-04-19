<%@ page import="auth.User" %>
<!doctype html>
<html>
<head>
    <meta name="layout" content="main">
    <r:require modules="flexiGrid"/>
    <g:set var="entityName" value="${message(code: 'user.label', default: 'User')}"/>
    <title><g:message code="default.list.label" args="[entityName]"/></title>
</head>

<body>
<%def contextPath = request.getServletContext().getContextPath()%>
<g:javascript>
     $(document).ready(function() {
         $('#userListView').dataTable( {

             "ajax":"${contextPath}/user/userGrid",
             "bProcessing": true,
             "bAutoWidth":false,

             "columns": [
                    { "data": "userName" },
                    { "data": "comapanyName" },
                    { "data": "accountExpire" },
                    { "data": "acountLocked" },
                    { "data": "enable" },
                    { "data": "passwordExpire" },
                    { "data": "action" }
                ]

    } );
    } );

      function editUser(userId,liveUrl){
                var redirectUrl=liveUrl+"/user/edit/"+userId;
                window.location.replace(redirectUrl);
            }

      function editPassword(userId,liveUrl){

           var redirectUrl=liveUrl+"/user/updatePassword/"+userId+"?"+"/st=1";
                window.location.replace(redirectUrl);
            }

      function deleteUser(userId,userName,liveUrl){
            //var msg = String.format("Are you sure to delete user {0}?",userName);
            var fmtMsg = "["+ userName+ "]?";
            var flag = confirm("Are you sure to delete user "+fmtMsg);
            if(flag){
                //var redirectUrl=liveUrl+"/user/updatePasswor/"+userId+"?"+"/st=1";
               var redirectUrl=liveUrl+"/user/remove/"+userId;
               window.location.replace(redirectUrl);
            }

      }

</g:javascript>

<a href="#list-user" class="skip" tabindex="-1"><g:message code="default.link.skip.label"
                                                           default="Skip to content&hellip;"/></a>

<% if (params.id) { %>
<g:include view="user/edit.gsp"/>
<% } else { %>
<g:include view="user/create.gsp"/>
<% } %>
<div id="list-page-body-inner" style="margin-top: 0px;" class="content" role="main">
    <h1><g:message code="default.list.label" args="[entityName]"/></h1>
    <g:if test="${flash.message}">
        <div class="message" role="status">${flash.message}</div>
    </g:if>
    <div id="create.reportExpenseBudgetAllList" class="content scaffold-create" role="main"
         style="margin-top: 0px;margin-bottom: 0px;">

        <table id="userListView" class="display" cellspacing="0" width="100%">

            <thead>
                <tr>
                    <th>${message(code: 'user.username.label', default: 'Username')}</th>
                    <th>${message(code: 'businessCompany.companyName.label', default: 'Company Name')}</th>
                    <th>${message(code: 'user.accountExpired.label', default: 'Account Expired')}</th>
                    <th>${message(code: 'user.accountLocked.label', default: 'Account Locked')}</th>
                    <th>${message(code: 'user.enabled.label', default: 'Enabled')}</th>
                    <th>${message(code: 'user.passwordExpired.label', default: 'Password Expired')}</th>
                    <th>${message(code: 'invoiceExpense.gridList.action.label', default: 'Action')}</th>
                </tr>
            </thead>

        </table>
    </div>

</div>
</body>
</html>
