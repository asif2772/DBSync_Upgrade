<%@ page import="bv.SoulLogs" %>
<calendar:resources lang="en" theme="tiger"/>


<div class="fieldcontain ${hasErrors(bean: soulLogsInstance, field: 'endTime', 'error')} required">
	<label for="endTime">
		<g:message code="soulLogs.endTime.label" default="End Time" />
		<span class="required-indicator">*</span>
	</label>
	%{--<g:datePicker name="endTime" precision="day"  value="${soulLogsInstance?.endTime}"  />--}%
    <calendar:datePicker name="endTime" defaultValue="${soulLogsInstance?.endTime}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: soulLogsInstance, field: 'ipAddress', 'error')} ">
	<label for="ipAddress">
		<g:message code="soulLogs.ipAddress.label" default="Ip Address" />
		
	</label>
	<g:textField name="ipAddress" value="${soulLogsInstance?.ipAddress}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: soulLogsInstance, field: 'loginAttempt', 'error')} required">
	<label for="loginAttempt">
		<g:message code="soulLogs.loginAttempt.label" default="Login Attempt" />
		<span class="required-indicator">*</span>
	</label>
	<g:field name="loginAttempt" type="number" value="${soulLogsInstance.loginAttempt}" required=""/>
</div>

<div class="fieldcontain ${hasErrors(bean: soulLogsInstance, field: 'soulId', 'error')} required">
	<label for="soulId">
		<g:message code="soulLogs.soulId.label" default="Soul Id" />
		<span class="required-indicator">*</span>
	</label>
	<g:field name="soulId" type="number" value="${soulLogsInstance.soulId}" required=""/>
</div>

<div class="fieldcontain ${hasErrors(bean: soulLogsInstance, field: 'startTime', 'error')} required">
	<label for="startTime">
		<g:message code="soulLogs.startTime.label" default="Start Time" />
		<span class="required-indicator">*</span>
	</label>
	%{--<g:datePicker name="startTime" precision="day"  value="${soulLogsInstance?.startTime}"  />--}%
    <calendar:datePicker name="startTime" defaultValue="${soulLogsInstance?.startTime}"/>
</div>

