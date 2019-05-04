<!DOCTYPE html>
<html>
<title>流程定义列表</title> 
<#include "/inc/init_bootstrap_import.ftl"/>
</head>
<body style="padding-left:20px;padding-right:20px">
<#assign sortNo = 1 />
<center><br>
<div class="x_content_title"><img
	src="${request.contextPath}/static/images/window.png"
	alt="${processDefinition.description}任务信息">&nbsp;${processDefinition.description}任务信息</div>
<br>
<table align="center" class="x-table-border table table-striped table-bordered table-condensed" cellspacing="1"
	cellpadding="4" width="90%">
	<thead>
		<tr class="x-title">
			<td align="center" noWrap>序号</td>
			<td align="center" noWrap>任务名称</td>
			<td align="center" noWrap>任务描述</td>
			<td align="center" noWrap>优先级</td>
			<td align="center" noWrap>是否发信号</td>
		</tr>
	</thead>
	<tbody>
		<#list  tasks as task>
			<tr onmouseover="this.className='x-row-over';"
				onmouseout="this.className='x-row-out';" class="x-content">
				<td align="center" noWrap>${sortNo}</td>
				<td align="left" noWrap>&nbsp; ${task.name}</td>
				<td align="left" noWrap>&nbsp;${task.description}</td>
				<td align="center" noWrap>&nbsp;${task.priority}</td>
				<td align="center" noWrap>&nbsp;
				<#if task.signalling >
					<font color="#6666FF"><b>是</b></font>
				<#else>
					<font color="#FFCC33"><b>否</b></font>
				</#if>
				</td>
			</tr>
		  <#assign sortNo = sortNo + 1 />
		</#list>
	</tbody>
</table>
</center>
</body>
</html>