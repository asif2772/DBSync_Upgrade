
<%@ page import="bv.SoulAclGroups" %>
<!doctype html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'soulAclGroups.label', default: 'SoulAclGroups')}" />
		<title><g:message code="default.show.label" args="[entityName]" /></title>
	</head>
	<body>
		<a href="#show-soulAclGroups" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
		<div class="nav" role="navigation">
			<ul>
				<li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
				<li><g:link class="list" action="list"><g:message code="default.list.label" args="[entityName]" /></g:link></li>
				<li><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></li>
			</ul>
		</div>
		<div id="show-soulAclGroups" class="content scaffold-show" role="main">
			<h1><g:message code="default.show.label" args="[entityName]" /></h1>
			<g:if test="${flash.message}">
			<div class="message" role="status">${flash.message}</div>
			</g:if>
			<ol class="property-list soulAclGroups">
			
				<g:if test="${soulAclGroupsInstance?.description}">
				<li class="fieldcontain">
					<span id="description-label" class="property-label"><g:message code="soulAclGroups.description.label" default="Description" /></span>
					
						<span class="property-value" aria-labelledby="description-label"><g:fieldValue bean="${soulAclGroupsInstance}" field="description"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${soulAclGroupsInstance?.name}">
				<li class="fieldcontain">
					<span id="name-label" class="property-label"><g:message code="soulAclGroups.name.label" default="Name" /></span>
					
						<span class="property-value" aria-labelledby="name-label"><g:fieldValue bean="${soulAclGroupsInstance}" field="name"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${soulAclGroupsInstance?.status}">
				<li class="fieldcontain">
					<span id="status-label" class="property-label"><g:message code="soulAclGroups.status.label" default="Status" /></span>
					
						<span class="property-value" aria-labelledby="status-label"><g:fieldValue bean="${soulAclGroupsInstance}" field="status"/></span>
					
				</li>
				</g:if>
			
			</ol>
			<g:form>
				<fieldset class="buttons">
					<g:hiddenField name="id" value="${soulAclGroupsInstance?.id}" />
					<g:link class="edit" action="edit" id="${soulAclGroupsInstance?.id}"><g:message code="default.button.edit.label" default="Edit" /></g:link>
					<g:actionSubmit class="delete" action="delete" value="${message(code: 'default.button.delete.label', default: 'Delete')}" onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');" />
				</fieldset>
			</g:form>
		</div>
	</body>
</html>
