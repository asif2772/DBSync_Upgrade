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
    <title>DB Synchornize</title>
</head>

<body>
%{--<%if(authUserId==1){%>--}%
<% def contextPath = request.getServletContext().getContextPath() %>

<g:javascript>
var table ;
    $(document).ready(function(){

        table = $('#listOfDbInfo').dataTable({

                 "ajax":"${contextPath}/reports/showDataListForDumping",
                 "bProcessing": true,
                 "bAutoWidth":false,
                 "aLengthMenu": [[10,50,75,100,150,200,300], [10,50,75,100,150,200,300]],
                 "iDisplayLength": 10,
                 "columns": [
                        { "data": "dbName" },
                        { "data": "action" },

                    ]

        });

    });

    function dbBackUp(dbName){

     $.ajax({
                 type:'GET',
                 url:'../reports/dbDump',
                 contentType: "application/octet-stream; charset=utf-8",
                 data:{
                    dbName:dbName
                 },
                 success: function(response)
                 {
//                   $('a#someID').attr({target: '_blank', href  : 'E:/'+dbName+'.sql'});
alert('E:/'+dbName+'.sql');
                   window.location = 'E:/'+dbName+'.sql';
                 }
            });

    }


</g:javascript>
%{--<div id="afterUpdate">--}%



<input type="hidden" name="numberOfDb" id="numberOfDb">

<div id="list-page-body-inner" class="content white-background scaffold-create" role="main" style="">
    %{--<div id="list-page">--}%
    <div class="searchField" style=" margin: 0 0 0 -2px;width: 1001px;">
        <label class="searchHeadLineLabel">DB Status</label>
    </div>

    <div class="boxouter boxouter-modified" style="float: left;width: 99.8%;">

        %{--<div class="dataTableDiv">--}%
        <table id="listOfDbInfo" class="display dbSyncTable" cellspacing="0" width="100%">
            <thead>
            <tr>
                <th>Database Name</th>
                <th>Action</th>
            </tr>
            </thead>
        </table>
        %{--</div>--}%
        <hr class="dataTablBottom" style="">

    </div>  <!--boxouter-->
</div> <!--content-->

</body>

</html>