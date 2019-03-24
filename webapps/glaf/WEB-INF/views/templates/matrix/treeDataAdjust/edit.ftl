<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>树表数据调整</title>
<#include "/inc/init_easyui_import.ftl"/>
<script type="text/javascript">
    var contextPath="${request.contextPath}";

	function saveData(){
		var params = jQuery("#iForm").formSerialize();
		jQuery.ajax({
				   type: "POST",
				   url: '${request.contextPath}/sys/treeDataAdjust/save',
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
					  
					   if(data.statusCode == 200) { 
					       parent.location.reload(); 
					   }
				   }
			 });
	}

	function saveAsData(){
		var params = jQuery("#iForm").formSerialize();
		jQuery.ajax({
				   type: "POST",
				   url: '${request.contextPath}/sys/treeDataAdjust/saveAs',
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
					   if(data.statusCode == 200) { 
					       parent.location.reload(); 
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
    <div class="toolbar-backgroud"> 
	<span class="x_content_title">&nbsp;<img src="${request.contextPath}/static/images/window.png">&nbsp;编辑树表数据调整</span>
 	<a href="#" class="easyui-linkbutton" data-options="plain:true, iconCls:'icon-save'" 
	   onclick="javascript:saveData();" >保存</a> 
	<a href="#" class="easyui-linkbutton" data-options="plain:true, iconCls:'icon-save'" 
	   onclick="javascript:saveAsData();" >另存</a> 
    </div> 
  </div>

  <div data-options="region:'center',border:false,cache:true">
  <form id="iForm" name="iForm" method="post">
  <input type="hidden" id="id" name="id" value="${treeDataAdjust.id}"/>
  <table class="easyui-form" style="width:880px;" align="center">
    <tbody>
	<tr>
		<td width="20%" align="left">名称</td>
		<td align="left">
            <input id="name" name="name" type="text" 
			       class="easyui-validatebox  x-text" style="width:450px"  
				   value="${treeDataAdjust.name}"/>
		</td>
	</tr>
	<tr>
		<td width="20%" align="left">标题</td>
		<td align="left">
            <input id="title" name="title" type="text" 
			       class="easyui-validatebox  x-text" style="width:450px" 
				   value="${treeDataAdjust.title}"/>
		</td>
	</tr>
	<tr>
		<td width="20%" align="left">数据库编号</td>
		<td align="left">
			 <select id="databaseId" name="databaseId">
			    <option value="">----请选择----</option>
				<#list databases as database>
				<option value="${database.id}">${database.title}[${database.dbname}]</option>
				</#list>
            </select> 
            <script type="text/javascript">
                 document.getElementById("databaseId").value="${treeDataAdjust.databaseId}";
            </script>
		</td>
	</tr>
	<tr>
		<td width="20%" align="left">表名</td>
		<td align="left">
            <input id="tableName" name="tableName" type="text" 
			       class="easyui-validatebox  x-text" style="width:180px"  
				   value="${treeDataAdjust.tableName}"/>
		</td>
	</tr>
	<tr>
		<td width="20%" align="left">目标表名</td>
		<td align="left">
            <input id="targetTableName" name="targetTableName" type="text" 
			       class="easyui-validatebox  x-text" style="width:180px"  
				   value="${treeDataAdjust.targetTableName}"/>&nbsp;（如果目标表就是来源表，可以不填写。）
			<div style="margin-top:5px;">
			  <span style="color:red; margin-left:0px;">
			  （提示：为了保证系统安全，目标表只能以useradd_、etl_、sync_、tmp_、tree_table_开头。）
			  </span>
			</div>
		</td>
	</tr>
	<tr>
		<td width="20%" align="left">主键列</td>
		<td align="left">
            <input id="primaryKey" name="primaryKey" type="text" 
			       class="easyui-validatebox  x-text" style="width:180px" 
				   value="${treeDataAdjust.primaryKey}"/>
		</td>
	</tr>
	<tr>
		<td width="20%" align="left">id列</td>
		<td align="left">
            <input id="idColumn" name="idColumn" type="text" 
			       class="easyui-validatebox  x-text" style="width:180px" 
				   value="${treeDataAdjust.idColumn}"/>
		</td>
	</tr>
	<tr>
		<td width="20%" align="left">parentId列</td>
		<td align="left">
            <input id="parentIdColumn" name="parentIdColumn" type="text" 
			       class="easyui-validatebox  x-text" style="width:180px" 
				   value="${treeDataAdjust.parentIdColumn}"/>
		</td>
	</tr>
	<tr>
		<td width="20%" align="left">treeId列</td>
		<td align="left">
            <input id="treeIdColumn" name="treeIdColumn" type="text" 
			       class="easyui-validatebox  x-text" style="width:180px" 
				   value="${treeDataAdjust.treeIdColumn}"/>
		</td>
	</tr>
	<tr>
		<td width="20%" align="left">treeId分隔符</td>
		<td align="left">
            <input id="treeIdDelimiter" name="treeIdDelimiter" type="text" 
			       class="easyui-validatebox  x-text" style="width:180px" 
				   value="${treeDataAdjust.treeIdDelimiter}"/>
		</td>
	</tr>
	<tr>
		<td width="20%" align="left">名称列</td>
		<td align="left">
            <input id="nameColumn" name="nameColumn" type="text" 
			       class="easyui-validatebox  x-text" style="width:180px" 
				   value="${treeDataAdjust.nameColumn}"/>
		</td>
	</tr>
    <tr>
		<td width="20%" align="left">调整列</td>
		<td align="left">
            <input id="adjustColumn" name="adjustColumn" type="text" 
			       class="easyui-validatebox  x-text" style="width:450px" 
				   value="${treeDataAdjust.adjustColumn}"/>
			<div style="margin-top:5px;">
		     （提示：如果调整功能支持多个列，列名之间用半角的逗号,隔开）
			</div>
		</td>
	</tr>
	<tr>
		<td width="20%" align="left">连接符</td>
		<td align="left">
            <input id="connector" name="connector" type="text" 
			       class="easyui-validatebox  x-text" style="width:180px" 
				   value="${treeDataAdjust.connector}"/>
		</td>
	</tr>
	<tr>
		<td width="20%" align="left">SQL查询</td>
		<td align="left">
             <textarea id="sqlCriteria" name="sqlCriteria" rows="6" cols="46" class="x-textarea" 
			          style="font: 13px Consolas,Courier New,Arial; width:450px;height:150px;" >${treeDataAdjust.sqlCriteria}</textarea>
			<div style="margin-top:5px;">
		     （提示：自行构建合法的查询语句）
			</div>
		</td>
	</tr>
	<tr>
		<td width="20%" align="left">Freemarker表达式</td>
		<td align="left">
             <textarea id="expression" name="expression" rows="6" cols="46" class="x-textarea" 
			          style="font: 13px Consolas,Courier New,Arial; width:450px;height:90px;" >${treeDataAdjust.expression}</textarea>
			<div style="margin-top:5px;">
		     （提示：使用Freemarker模板引擎，使用需遵循Freemarker的语法）
			 <br>（提示：可以使用查询项中的结果变量，支持运算及函数。）
			</div>
		</td>
	</tr>
	
	<tr>
		<td width="20%" align="left">预处理</td>
		<td align="left">
           <select id="preprocessFlag" name="preprocessFlag">
				<option value="Y">是</option>
			    <option value="N">否</option>
            </select>
            <script type="text/javascript">
                 document.getElementById("preprocessFlag").value="${treeDataAdjust.preprocessFlag}";
            </script>
			&nbsp;（提示：如果树表不存在treeid或treeid为空时是否自动添加。）
		</td>
	</tr>

	<tr>
		<td width="20%" align="left">调整功能</td>
		<td align="left">
           <select id="adjustType" name="adjustType">
		        <option value="">----请选择----</option>
				<option value="dateGT">父节点调整一个较大的日期</option>
			    <option value="dateLT">父节点调整一个较小的日期</option>
				<option value="nameChain">生成树形链式结构</option>
				<option value="treeAggregate">树表逐级汇总</option>
            </select>
            <script type="text/javascript">
                 document.getElementById("adjustType").value="${treeDataAdjust.adjustType}";
            </script>
			&nbsp;（提示：选择其中的一项功能进行调整，将只影响调整列对应的数据。）
		</td>
	</tr>

	<tr>
		<td width="20%" align="left">限制叶节点</td>
		<td align="left">
           <select id="leafLimitFlag" name="leafLimitFlag">
				<option value="Y">是</option>
			    <option value="N">否</option>
            </select>
            <script type="text/javascript">
                 document.getElementById("leafLimitFlag").value="${treeDataAdjust.leafLimitFlag}";
            </script>
			&nbsp;（提示：是否限制只能在叶节点上调整。）
		</td>
	</tr>
	<tr>
		<td width="20%" align="left">是否并行处理</td>
		<td align="left">
			 <select id="forkJoinFlag" name="forkJoinFlag">
			    <option value="Y">是</option>
				<option value="N">否</option>
             </select>
             <script type="text/javascript">
                 document.getElementById("forkJoinFlag").value="${treeDataAdjust.forkJoinFlag}";
             </script>
		</td>
	</tr>
    <tr>
		<td width="20%" align="left">更新标识</td>
		<td align="left">
              <select id="updateFlag" name="updateFlag">
			    <option value="">----请选择----</option>
				<option value="A">增量</option>
			    <option value="F">全量</option>
             </select>
             <script type="text/javascript">
                 document.getElementById("updateFlag").value="${treeDataAdjust.updateFlag}";
             </script>
			 <span style="margin-left:15px;">
		     （提示：只更新变化的数据或更新全部数据）
			 </span>
		</td>
	</tr>
	<tr>
		<td width="20%" align="left">每次执行前删除</td>
		<td align="left">
              <select id="deleteFetch" name="deleteFetch">
			    <option value="">----请选择----</option>
				<option value="Y">是</option>
			    <option value="N">否</option>
             </select>
             <script type="text/javascript">
                 document.getElementById("deleteFetch").value="${treeDataAdjust.deleteFetch}";
             </script>
			 <span style="margin-left:15px;">
		     （提示：如果目标表不是来源表才支持）
			 </span>
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
                 document.getElementById("scheduleFlag").value="${treeDataAdjust.scheduleFlag}";
             </script>
		</td>
	</tr>
	<tr>
		<td width="20%" align="left">是否有效</td>
		<td align="left">
		    <select id="locked" name="locked">
				<option value="0">是</option>
			    <option value="1">否</option>
            </select>
            <script type="text/javascript">
                 document.getElementById("locked").value="${treeDataAdjust.locked}";
            </script>
		</td>
	</tr>
	<tr>
		<td width="20%" align="left"> </td>
		<td align="left">
		    <br><br>
		</td>
	</tr>
    </tbody>
  </table>
  </form>
</div>
</div>
</body>
</html>