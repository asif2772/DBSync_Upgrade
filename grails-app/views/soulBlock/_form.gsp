<%@ page import="bv.SoulAclGroups" %>



<div class="fieldcontain ${hasErrors(bean: soulAclGroupsInstance, field: 'description', 'error')} ">
	<label for="description">
		<g:message code="soulAclGroups.description.label" default="Description" />
		
	</label>
	<g:textField name="description" value="${soulAclGroupsInstance?.description}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: soulAclGroupsInstance, field: 'name', 'error')} ">
	<label for="name">
		<g:message code="soulAclGroups.name.label" default="Name" />
		
	</label>
	<g:textField name="name" value="${soulAclGroupsInstance?.name}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: soulAclGroupsInstance, field: 'status', 'error')} required">
	<label for="status">
		<g:message code="soulAclGroups.status.label" default="Status" />
		<span class="required-indicator">*</span>
	</label>
	<g:field name="status" type="number" value="${soulAclGroupsInstance.status}" required=""/>
</div>

