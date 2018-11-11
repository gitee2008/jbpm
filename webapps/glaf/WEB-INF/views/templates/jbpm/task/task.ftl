<!DOCTYPE html>
<html>
<title>流程任务</title> 
<#include "/inc/init_bootstrap_import.ftl"/>

<script language="javascript">

    function suspendX(btn){
		 if(confirm("该流程实例的全部待办任务都会被暂停执行，确认吗？")){
			 var params = jQuery("#iForm").formSerialize();
			  jQuery.ajax({
				   type: "POST",
				   url: '${request.contextPath}/jbpm/task/suspend',
				   data: params,
				   dataType:  'json',
				   error: function(data){
					   alert('服务器处理错误！');
				   },
				   success: function(data){
						 alert('操作成功完成！');
						 window.location.reload();
				   }
			 });
		 }
     };

	function resumeX(btn){
		 if(confirm("该流程实例的全部待办任务都会被恢复执行，确认吗？")){
              var params = jQuery("#iForm").formSerialize();
			  jQuery.ajax({
				   type: "POST",
				   url: '${request.contextPath}/jbpm/task/resume',
				   data: params,
				   dataType:  'json',
				   error: function(data){
					   alert('服务器处理错误！');
				   },
				   success: function(data){
						 alert('操作成功完成！');
						 window.location.reload();
				   }
			 });
		  }
     };
 
    function chooseUserXY(){
	    location.href= "${request.contextPath}/jbpm/task/chooseUser?processInstanceId=${processInstanceId}";
    }
 
</script>

</head>
<body >
<br>
<center>
<form id="iForm" name="iForm" class="x-form"
	action="${request.contextPath}/jbpm/task">
<input type="hidden" id="processInstanceId" name="processInstanceId" value="${processInstanceId}">
<input type="hidden" id="method" name="method" value="task">

<div class="content-block" style="width: 95%;">
<div class="x_content_title"><img
	src="${request.contextPath}/static/images/window.png"
	alt="流程实例信息"> 流程实例信息</div>
<br>

<fieldset class="x-fieldset" style="width: 95%;">
<legend>流程基本信息</legend>
<#if processInstance?exists>
<table class="x-table-border table table-striped table-bordered table-condensed" 
       align="left" cellspacing="1" cellpadding="4" width="98%" nowrap>
	<tr>
		<td width="12%" height="12" align="left">流程名称</td>
		<td width="38%"> 
		</td>
		<td width="12%" height="12" align="left">流程编号</td>
		<td width="38%">${processInstance.id}</td>
	</tr>
	<tr>
		<td width="12%" height="12" align="left">流程主题</td>
		<td width="38%"> 
		</td>
		<td width="12%" height="12" align="left">单据编号</td>
		<td width="38%">${processInstance.key}
		</td>
	</tr>
	<tr>
		<td width="12%" height="12" align="left">启动者</td>
		<td width="38%"> 
		</td>
		<td width="12%" height="12" align="left">启动时间</td>
		<td width="38%">${processInstance.start?string('yyyy-MM-dd')}
		</td>
	</tr>
</table>
</#if>
</fieldset>
<br>

<fieldset class="x-fieldset" style="width: 95%;">
<legend>流程处理信息</legend>
<table class="x-table-border table table-striped table-bordered table-condensed" 
       align="left" cellspacing="1" cellpadding="4" width="98%" nowrap>
    <#if finishedTaskItems?exists>
	<#list finishedTaskItems as ti>
	<tr>
		<td align="left">${ti.taskName}&nbsp;&nbsp;${ti.taskDescription}
		</td>
		<td align="left">${ti.actorName}
		</td>
		<td align="center"><#if ti.startDate?exists>${ti.startDate?string('yyyy-MM-dd')}</#if>
		</td>
		<td align="left">
	       <#if ti.isAgree == "true">
            <b><font color="green">通过</font></b>
           <#elseif ti.isAgree == "false">
            <b><font color="red">不通过</font></b>
		   </#if>
            &nbsp;${ti.opinion}
		</td>
	</tr>
	</#list>
	</#if>
</table>
</fieldset>
<br>

<#if taskItems?exists>
<fieldset class="x-fieldset"  style="width: 95%;">
<legend>待办任务</legend>
<table class="x-table-border table table-striped table-bordered table-condensed" 
       align="left" cellspacing="1" cellpadding="4" width="98%" nowrap>
    <#if taskItems?exists>
	<#list taskItems as ti>
	<tr>
		<td align="left">${ti.taskName}&nbsp;&nbsp;${ti.taskDescription}
		</td>
		<td align="left">${ti.actorName}
		</td>
		<td align="center">${ti.createDate?string('yyyy-MM-dd')}
		</td>
		<td align="left">
	       <#if ti.isAgree == "true">
            <b><font color="green">通过</font></b>
           <#elseif ti.isAgree == "false">
            <b><font color="red">不通过</font></b>
		   </#if>
            &nbsp;${ti.opinion}
		</td>
	</tr>
	</#list>
	</#if>
</table>
</fieldset>
</#if>
<br>

<fieldset class="x-fieldset"  style="width: 95%;"><legend>流程图</legend>
<br />
<div id="task_processimage" align="center">
   ${tag_script}
</div>
<br />
</fieldset>

<div align="center"><br />
<#if processInstanceRunning?exists>
 <input type="button" value="暂停任务" id="suspend" name="suspend"
	class="btn" onclick="javascript:suspendX();" />
 <input type="button"
	value="恢复任务" id="resume" name="resume" class="btn"
	onclick="javascript:resumeX();" /> 
 <input type="button" value="重新分派任务"
	id="reassignx" name="reassignx" class="btn"
	onclick="javascript:chooseUserXY();" />  
</#if>
 <input type="button"
	value="关闭" id="close" name="close" class="btn"
	onclick="javascript:window.close();" /> 
<br/>
<br/>
</div>

</div>
</div>
</form>
<br>
</center>
</body>
</html>