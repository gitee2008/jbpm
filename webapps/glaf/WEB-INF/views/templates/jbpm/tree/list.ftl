<#assign sortNo = 1 />
<!DOCTYPE html>
<html>
<title>分类设置</title> 
<#include "/inc/init_bootstrap_import.ftl"/>
</head>
<body style="padding-left:20px;padding-right:20px">
<center><br>
<div class="x_content_title"><img
	src="${request.contextPath}/static/images/window.png"
	alt="流程定义信息">&nbsp;流程定义信息</div>
<br>

<table align="center" class="x-table-border table table-striped table-bordered table-condensed" cellspacing="1"
	cellpadding="4" width="90%">
	<thead>
		<tr class="x-title">
			<td align="center" noWrap>序号</td>
			<td align="center" noWrap>名称</td>
			<td align="center" noWrap>描述</td>
			<td align="center" noWrap>版本</td>
			<td align="center" noWrap>功能键</td>
		</tr>
	</thead>
	<tbody>
		<#list rows as a>
			<tr onmouseover="this.className='x-row-over';"
				onmouseout="this.className='x-row-out';" class="x-content">
				<td align="center" noWrap>${sortNo}</td>
				<td align="left" noWrap>&nbsp; ${a.name}</td>
				<td align="left" noWrap>&nbsp;${a.description}
				</td>
				<td align="right" noWrap>&nbsp;${a.version}&nbsp;
				</td>
				<td align="center" noWrap>
				 &nbsp;<a
					href="${request.contextPath}/jbpm/definition/task?processDefinitionId=${a.id}"><img
					src="${request.contextPath}/static/images/actor.gif"
					border="0" title="查看流程任务定义"> 流程任务</a>   &nbsp;<a
					href="${request.contextPath}/jbpm/processInstances?processDefinitionId=${a.id}"><img
					src="${request.contextPath}/static/images/task.gif"
					border="0" title="查看全部流程实例"> 全部</a> &nbsp;<a
					href="${request.contextPath}/jbpm/processInstances?processDefinitionId=${a.id}&processType=running"><img
					src="${request.contextPath}/static/images/lightbulb.png"
					border="0" title="查看运行中的流程实例"> 运行中</a> &nbsp;<a
					href="${request.contextPath}/jbpm/processInstances?processDefinitionId=${a.id}&processType=finished"><img
					src="${request.contextPath}/static/images/lightbulb_off.png"
					border="0" title="查看已经完成的流程实例"> 已完成</a> &nbsp;<a
					href="${request.contextPath}/jbpm/image?processDefinitionId=${a.id}"
					target=""><img
					src="${request.contextPath}/static/images/process.gif"
					border="0" title="查看流程图"> 流程图</a>
					&nbsp;<a
					href="${request.contextPath}/jbpm/definition/download?processDefinitionId=${a.id}"
					target=""><img
					src="${request.contextPath}/static/images/zip.gif"
					border="0" title="查看流程图"> 下载 </a>&nbsp;
					</td>
			</tr>
		  <#assign sortNo = sortNo + 1 />
		</#list>
	</tbody>
</table>
</center>