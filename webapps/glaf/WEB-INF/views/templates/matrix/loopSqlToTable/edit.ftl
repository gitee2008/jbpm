<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>循环SQL到表</title>
<#include "/inc/init_easyui_import.ftl"/>
<script type="text/javascript" src="${contextPath}/static/scripts/framework.js"></script>
<script type="text/javascript">

	function saveData(){
		//var jsonObject = fromToJson(document.getElementById("iForm"));
		var jsonObject = jQuery('#iForm').serializeObject();
        document.getElementById("json").value=JSON.stringify(jsonObject);
		var params = jQuery("#iForm").formSerialize();
		jQuery.ajax({
				   type: "POST",
				   url: '${contextPath}/sys/loopSqlToTable/save',
				   data: params,
				   dataType: 'json',
				   error: function(data){
					   alert('服务器处理错误！');
				   },
				   success: function(data){
					   if(data != null && data.message != null){
						   alert(data.message);
					   } else {
						   alert('操作成功完成！');
					   }
					   if(data.statusCode == 200){
					       window.parent.location.reload();
					   } 
				   }
			 });
	}

	function saveAsData(){
		//var jsonObject = fromToJson(document.getElementById("iForm"));
		var jsonObject = jQuery('#iForm').serializeObject();
        document.getElementById("json").value=JSON.stringify(jsonObject);
		document.getElementById("id").value="";
		var params = jQuery("#iForm").formSerialize();
		jQuery.ajax({
				   type: "POST",
				   url: '${contextPath}/sys/loopSqlToTable/saveAs',
				   data: params,
				   dataType: 'json',
				   error: function(data){
					   alert('服务器处理错误！');
				   },
				   success: function(data){
					   if(data != null && data.message != null){
						   alert(data.message);
					   } else {
						   alert('操作成功完成！');
					   }
					   if(data.statusCode == 200){
					       window.parent.location.reload();
					   }
				   }
			 });
	}

</script>
</head>

<body>
<div style="margin:0;"></div>  

<div class="easyui-layout" data-options="fit:true">  
  <div data-options="region:'north',split:true,border:true" style="height:40px"> 
    <div class="toolbar-backgroud" > 
	<span class="x_content_title"><img src="${contextPath}/static/images/window.png">&nbsp;编辑循环SQL到表</span>
	<a href="#" class="easyui-linkbutton" data-options="plain:true, iconCls:'icon-save'" 
	   onclick="javascript:saveData();" >保存</a> 
	<#if loopSqlToTable?exists>
	<a href="#" class="easyui-linkbutton" data-options="plain:true, iconCls:'icon-save'" 
	   onclick="javascript:saveAsData();" >另存</a> 
	</#if>
    </div> 
  </div>

  <div data-options="region:'center',border:false,cache:true">
  <form id="iForm" name="iForm" method="post">
  <input type="hidden" id="json" name="json">
  <input type="hidden" id="id" name="id" value="${loopSqlToTable.id}"/>
  <table class="easyui-form" style="width:880px;" align="center">
    <tbody>
	<tr>
		<td width="20%" align="left">名称</td>
		<td align="left">
              <input id="name" name="name" type="text" 
			         class="easyui-validatebox x-text" 
					 style="width:425px;" 
				     value="${loopSqlToTable.name}"/>
		</td>
	</tr>
	<tr>
		<td width="20%" align="left">标题</td>
		<td align="left">
              <input id="title" name="title" type="text" 
			         class="easyui-validatebox x-text"  
			         style="width:425px;" 
				     value="${loopSqlToTable.title}"/>
		</td>
	</tr>
	<tr>
		<td width="20%" align="left">来源数据库编号</td>
		<td align="left">
              <select id="sourceDatabaseId" name="sourceDatabaseId">
			    <option value="">----请选择----</option>
				<#list databases as database>
				<option value="${database.id}">${database.title}[${database.dbname}]</option>
				</#list>
             </select> 
             <script type="text/javascript">
                 document.getElementById("sourceDatabaseId").value="${loopSqlToTable.sourceDatabaseId}";
             </script>
		</td>
	</tr>
	<tr>
		<td width="20%" align="left">主键列</td>
		<td align="left">
              <input id="primaryKey" name="primaryKey" type="text" 
			         class="easyui-validatebox x-text"  
			         style="width:425px;" 
				     value="${loopSqlToTable.primaryKey}"/>
		</td>
	</tr>
	<tr>
		<td width="20%" align="left">SQL语句</td>
		<td align="left">
           <textarea id="sql" name="sql" rows="6" cols="46" class="x-textarea" 
		       style="font:13px Consolas,Courier New,Arial; width:625px; height:320px;" >${loopSqlToTable.sql}</textarea>
		    <div style="margin-top:5px;">
		     （提示：可以使用union语句组合结果。）
			<br>
			<span>
			 （可以使用动态参数,也可以使用循环语句的输出变量当输入,例如: column1 = 
			 <script>document.write('#');</script>{param1}。）
			</span>
			<br>
			<span>
			 （其中动态表名也可以使用参数,按日期维度的表也可以用变量代替,例如: sys_log_<script>document.write('$');</script>{today_int}。）
			</span>
			<br>
			<span>
			 （today_int内置变量代表当天的时间,格式为yyyyMMdd,如20181201。）
			</span>
			<br>
			<span>
			 （today_start内置变量代表当天的开始时间, today_end内置变量代表当天的结束时间。）
			</span>
			<br>
			<span>
			 （yesterday_int内置变量代表当天的前一天时间,格式为yyyyMMdd,如20181130。）
			</span>
			<br>
			<span>
			 （yesterday_start内置变量代表昨天的开始时间, yesterday_end内置变量代表昨天的结束时间。）
			</span>
			<br>
			<span>
			 （today_curr_hour_start内置变量代表当前小时的开始时间, today_curr_hour_end内置变量代表当前小时的结束时间。）
			</span>
			<br>
			<span>
			 （today_previous_hour_start内置变量代表前一小时的开始时间, today_previous_hour_end内置变量代表前一小时的结束时间。）
			</span>
			<br>
			<span>
			 （the_end_of_last_month内置变量代表上月最后一天的时间。）
			</span>
			<br>
			<span style="color:red;">
			 （loop_date_var 内置变量代表循环日期yyyyMMdd变量。）<br>
			 （loop_date_previous_var 内置变量代表循环日期的前一天yyyyMMdd变量。）<br>
			 （loop_date_start_var 内置变量代表循环开始日期yyyy-MM-dd 00:00:00变量。）<br>
			 （loop_date_end_var 内置变量代表循环结束日期yyyy-MM-dd 23:59:59变量。）<br>
			 （loop_date_previous_start_var 内置变量代表循环开始日期的前一天yyyy-MM-dd 00:00:00变量。）<br>
			 （loop_date_previous_end_var 内置变量代表循环结束日期的前一天yyyy-MM-dd 23:59:59变量。）<br>
			</span>
	      </div>
		</td>
	</tr>
	<tr>
		<td width="20%" align="left">目标表名</td>
		<td align="left">
              <input id="targetTableName" name="targetTableName" type="text" 
			         class="easyui-validatebox x-text"  
			         style="width:425px;" 
				     value="${loopSqlToTable.targetTableName}"/>
		</td>
	</tr>
	<tr>
		<td width="20%" align="left">目标数据库编号</td>
		<td align="left">
		      <select id="targetDatabaseId" name="targetDatabaseId">
			    <option value="">----请选择----</option>
				<#list databases as database>
				<option value="${database.id}">${database.title}[${database.dbname}]</option>
				</#list>
             </select> 
             <script type="text/javascript">
                 document.getElementById("targetDatabaseId").value="${loopSqlToTable.targetDatabaseId}";
             </script>
		</td>
	</tr>
	<tr>
		<td width="20%" align="left">最近多少天</td>
		<td align="left">
			<input id="recentlyDay" name="recentlyDay" type="text" increment="10" precision="0" maxLength="3"
			       class="easyui-numberbox" style="width:65px; text-align: right" value="${loopSqlToTable.recentlyDay}"/>
			<span style="color:red; margin-top:5px;">&nbsp;（提示：如果开始日期不固定，那么可以设置当前日期的前多少天开始。）</span>
		</td>
	</tr>
	<tr>
		<td width="20%" align="left">开始日期</td>
		<td align="left">
			<input id="startDate" name="startDate" type="text" style="width:125px;" 
			       class="easyui-datebox x-text" style="text-align: center"
			       <#if loopSqlToTable.startDate?exists>value="${loopSqlToTable.startDate?string('yyyy-MM-dd')}"</#if>/>
			<span style="margin-top:5px;">&nbsp;（提示：如果不设置，取上面的当前日期的前多少天。）</span>
		</td>
	</tr>
	<tr>
		<td width="20%" align="left">结束日期</td>
		<td align="left">
			<input id="stopDate" name="stopDate" type="text" style="width:125px;" 
			       class="easyui-datebox x-text" style="text-align: center"
			       <#if loopSqlToTable.stopDate?exists>value="${loopSqlToTable.stopDate?string('yyyy-MM-dd')}"</#if>/>
			 <span style="margin-top:5px;">&nbsp;（提示：如果不设置，默认为当天。）</span>
		</td>
	</tr>
	<tr>
		<td width="20%" align="left">每次抓取前删除</td>
		<td align="left">
              <select id="deleteFetch" name="deleteFetch">
			    <option value="">----请选择----</option>
				<option value="Y">是</option>
			    <option value="N">否</option>
             </select>
             <script type="text/javascript">
                 document.getElementById("deleteFetch").value="${loopSqlToTable.deleteFetch}";
             </script>
		</td>
	</tr>
	<tr>
		<td width="20%" align="left">是否跳过错误</td>
		<td align="left">
		     <select id="skipError" name="skipError">
			    <option value="Y">是</option>
				<option value="N">否</option>
             </select>
             <script type="text/javascript">
                 document.getElementById("skipError").value="${loopSqlToTable.skipError}";
             </script>
		</td>
	</tr>
	<tr>
		<td width="20%" align="left">批量标识</td>
		<td align="left">
		     <select id="batchFlag" name="batchFlag">
			    <option value="Y">全部抽取完批量入库</option>
				<option value="N">每次抽取一天的入库</option>
             </select>
             <script type="text/javascript">
                 document.getElementById("batchFlag").value="${loopSqlToTable.batchFlag}";
             </script>
		</td>
	</tr>
	<tr>
		<td width="20%" align="left">是否定时调度</td>
		<td align="left">
			 <select id="scheduleFlag" name="scheduleFlag">
			    <option value="Y">是</option>
				<option value="N">否</option>
             </select>
             <script type="text/javascript">
                 document.getElementById("scheduleFlag").value="${loopSqlToTable.scheduleFlag}";
             </script>
		</td>
	</tr>
	<tr>
		<td width="20%" align="left">顺序</td>
		<td align="left">
			<select id="sortNo" name="sortNo">
			    <option value="1">1</option>
				<option value="2">2</option>
				<option value="3">3</option>
				<option value="4">4</option>
				<option value="5">5</option>
				<option value="6">6</option>
				<option value="7">7</option>
				<option value="8">8</option>
				<option value="9">9</option>
             </select>
             <script type="text/javascript">
                 document.getElementById("sortNo").value="${loopSqlToTable.sortNo}";
             </script>
		</td>
	</tr>
	<tr>
		<td width="15%" align="left">是否有效</td>
		<td align="left">
		    <select id="locked" name="locked">
			    <option value="0">是</option>
				<option value="1">否</option>
             </select>
             <script type="text/javascript">
                 document.getElementById("locked").value="${loopSqlToTable.locked}";
             </script>
		</td>
	</tr>
	<tr>
		<td width="15%" align="left"> </td>
		<td align="left">
		     <br>&nbsp;<br>
		</td>
	</tr>
    </tbody>
  </table>
  </form>
</div>
</div>
</body>
</html>