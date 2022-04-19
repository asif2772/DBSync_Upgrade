
<%@ page import="bv.SoulLogs" %>
<!doctype html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'soulLogs.label', default: 'SoulLogs')}" />
		<title><g:message code="default.list.label" args="[entityName]" /></title>
	</head>
	<body>
		<a href="#list-soulLogs" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
		<div class="nav" role="navigation">
			<ul>
				<li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
				<li><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></li>
			</ul>
		</div>
		<div id="list-soulLogs" class="content scaffold-list" role="main">
			<h1><g:message code="default.list.label" args="[entityName]" /></h1>
			<g:if test="${flash.message}">
			<div class="message" role="status">${flash.message}</div>
			</g:if>
			<table>
				<thead>
					<tr>
					
						<g:sortableColumn property="endTime" title="${message(code: 'soulLogs.endTime.label', default: 'End Time')}" />
					
						<g:sortableColumn property="ipAddress" title="${message(code: 'soulLogs.ipAddress.label', default: 'Ip Address')}" />
					
						<g:sortableColumn property="loginAttempt" title="${message(code: 'soulLogs.loginAttempt.label', default: 'Login Attempt')}" />
					
						<g:sortableColumn property="soulId" title="${message(code: 'soulLogs.soulId.label', default: 'Soul Id')}" />
					
						<g:sortableColumn property="startTime" title="${message(code: 'soulLogs.startTime.label', default: 'Start Time')}" />
					
					</tr>
				</thead>
				<tbody>
				<g:each in="${soulLogsInstanceList}" status="i" var="soulLogsInstance">
					<tr class="${(i % 2) == 0 ? 'even' : 'odd'}">
					
						<td><g:link action="show" id="${soulLogsInstance.id}">${fieldValue(bean: soulLogsInstance, field: "endTime")}</g:link></td>
					
						<td>${fieldValue(bean: soulLogsInstance, field: "ipAddress")}</td>
					
						<td>${fieldValue(bean: soulLogsInstance, field: "loginAttempt")}</td>
					
						<td>${fieldValue(bean: soulLogsInstance, field: "soulId")}</td>
					
						<td><g:formatDate date="${soulLogsInstance.startTime}" /></td>
					
					</tr>
				</g:each>
				</tbody>
			</table>
			<div class="pagination">
				<g:paginate total="${soulLogsInstanceTotal}" />
			</div>
		</div>
	</body>
</html>
