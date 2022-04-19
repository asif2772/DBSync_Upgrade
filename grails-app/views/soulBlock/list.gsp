
<%@ page import="bv.SoulAclGroups" %>
<!doctype html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'soulAclGroups.label', default: 'SoulAclGroups')}" />
		<title><g:message code="default.list.label" args="[entityName]" /></title>
	</head>
	<body>
		<a href="#list-soulAclGroups" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
		<div class="nav" role="navigation">
			<ul>
				<li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
				<li><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></li>
			</ul>
		</div>
		<div id="list-soulAclGroups" class="content scaffold-list" role="main">
			<h1><g:message code="default.list.label" args="[entityName]" /></h1>
			<g:if test="${flash.message}">
			<div class="message" role="status">${flash.message}</div>
			</g:if>
			<table>
				<thead>
					<tr>
					
						<g:sortableColumn property="description" title="${message(code: 'soulAclGroups.description.label', default: 'Description')}" />
					
						<g:sortableColumn property="name" title="${message(code: 'soulAclGroups.name.label', default: 'Name')}" />
					
						<g:sortableColumn property="status" title="${message(code: 'soulAclGroups.status.label', default: 'Status')}" />
					
					</tr>
				</thead>
				<tbody>
				<g:each in="${soulAclGroupsInstanceList}" status="i" var="soulAclGroupsInstance">
					<tr class="${(i % 2) == 0 ? 'even' : 'odd'}">
					
						<td><g:link action="show" id="${soulAclGroupsInstance.id}">${fieldValue(bean: soulAclGroupsInstance, field: "description")}</g:link></td>
					
						<td>${fieldValue(bean: soulAclGroupsInstance, field: "name")}</td>
					
						<td>${fieldValue(bean: soulAclGroupsInstance, field: "status")}</td>
					
					</tr>
				</g:each>
				</tbody>
			</table>
			<div class="pagination">
				<g:paginate total="${soulAclGroupsInstanceTotal}" />
			</div>
		</div>
	</body>
</html>
