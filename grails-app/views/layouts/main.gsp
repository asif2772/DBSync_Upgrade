<!doctype html>

<html lang="en" class="no-js">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
    <title><g:layoutTitle default="Budget View"/></title>
    %{--<meta http-equiv="cache-control" content="max-age=0" />--}%
    %{--<meta http-equiv="cache-control" content="no-cache" />--}%
    %{--<meta http-equiv="expires" content="0" />--}%
    %{--<meta http-equiv="expires" content="Tue, 01 Jan 1980 1:00:00 GMT" />--}%
    %{--<meta http-equiv="pragma" content="no-cache" />--}%

    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="shortcut icon" href="${assetPath(src: 'favicon.ico')}" type="image/x-icon">
    <link rel="apple-touch-icon" href="${assetPath(src: 'apple-touch-icon.png')}">
    <link rel="apple-touch-icon" sizes="114x114" href="${assetPath(src: 'apple-touch-icon-retina.png')}">

    <asset:stylesheet href="budget_view_main_mnf.css"/>
    <asset:stylesheet href="budget_view_plugin_mnf.css"/>

        <asset:javascript src="jquery-et-al.js"/> %{-- Arafat: Making Defered JS might create problems--}%
        <asset:javascript src="jquery-ui-1.9.2.legacy/jquery-ui.js"/>
        <asset:javascript src="datatable-et-al.js"/>
        <asset:javascript src="jqwidgets.js"/>
        <asset:javascript src="jqwidgets2.js"/>
        <script src="//cdn.datatables.net/plug-ins/1.10.11/sorting/date-eu.js" type="text/javascript"></script>
        <link href="https://cdn.jsdelivr.net/npm/select2@4.1.0-rc.0/dist/css/select2.min.css" rel="stylesheet" />
        <script src="https://cdn.jsdelivr.net/npm/select2@4.1.0-rc.0/dist/js/select2.min.js"></script>

%{--    Termst--}%

    <g:layoutHead/>
    <script>

        var appContext = '${request.contextPath}';
        var sessionExpMsg = '<g:message code="session.expiry.msg" default="Your session is about to finish, do you want to keep current session?"/>';
    </script>

</head>
<body>

<g:render template="/common/header" />
<div id="content-whole">
    <div id="content-whole-inner">
        <div class="logoDiv">

            <div class="logoDivLeft">
                <a href="#"><img src="${assetPath(src: 'logo.png')}" alt="Budget View"/></a>
            </div>

            <div class="logoDivRight">
                <div id="spinner" class="spinner" style="display:none;">
                    <img src="${assetPath(src:'spinner.gif')}" alt="${message(code:'spinner.alt.Loading',default:'Loading...')}" />
                </div>
            </div>
        </div>

        <g:layoutBody/>
        <div id="spinner" class="spinner" style="display:none;"><g:message code="spinner.alt.Loading&hellip" default="Loading&hellip;"/></div>
    </div>

    <g:render template="/common/footer" />

</div>


<script type="text/javascript">



</script>

</body>
</html>

