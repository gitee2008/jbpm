<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>参数转换</title>
<#include "/inc/init_easyui_import.ftl"/>
<script type="text/javascript" src="${contextPath}/static/scripts/framework.js"></script>
<script type="text/javascript">

	function saveData(){
		//var jsonObject = fromToJson(document.getElementById("iForm"));
		//var jsonObject = jQuery('#iForm').serializeObject();
        //document.getElementById("json").value=JSON.stringify(jsonObject);
		var params = jQuery("#iForm").formSerialize();
		jQuery.ajax({
				   type: "POST",
				   url: '${request.contextPath}/matrix/parameterConversion/saveParameterConversion',
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
		//var jsonObject = jQuery('#iForm').serializeObject();
        //document.getElementById("json").value=JSON.stringify(jsonObject);
		document.getElementById("id").value="";
		var params = jQuery("#iForm").formSerialize();
		jQuery.ajax({
				   type: "POST",
				   url: '${request.contextPath}/matrix/parameterConversion/saveAsParameterConversion',
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
    <div class="toolbar-backgroud"> 
	<span class="x_content_title"><img src="${contextPath}/static/images/window.png">&nbsp;编辑参数转换</span>
	<a href="#" class="easyui-linkbutton" data-options="plain:true, iconCls:'icon-save'" 
	   onclick="javascript:saveData();" >保存</a> &nbsp;
	<a href="#" class="easyui-linkbutton" data-options="plain:true, iconCls:'icon-save'" 
	   onclick="javascript:saveAsData();" >另存</a> 
    </div> 
  </div>

  <div data-options="region:'center',border:false,cache:true">
  <form id="iForm" name="iForm" method="post">
  <input type="hidden" id="json" name="json">
  <input type="hidden" id="key" name="key" value="${key}"/>
  <input type="hidden" id="id" name="id" value="${parameterConversion.id}"/>
  <table class="easyui-form" style="width:850px;" align="center">
    <tbody>
	<tr>
		<td width="12%" align="left">标题</td>
		<td align="left">
              <input id="title" name="title" type="text" 
			         class="easyui-validatebox x-text"  
                     style="width:525px;" 
				     value="${parameterConversion.title}"/>
		</td>
	</tr>
	<tr>
		<td width="12%" align="left">来源名称</td>
		<td align="left">
              <input id="sourceName" name="sourceName" type="text" 
			         class="easyui-validatebox x-text"  
			         style="width:525px;" 
				     value="${parameterConversion.sourceName}"/>
		</td>
	</tr>
	<tr>
		<td width="12%" align="left">来源类型</td>
		<td align="left">
		    <select id="sourceType" name="sourceType">
			    <option value="">----请选择----</option>
				<option value="Integer">整数</option>
			    <option value="Long">长整数</option>
				<option value="Double">数值</option>
				<option value="Date">日期</option>
				<option value="String">字符串</option>
            </select>
            <script type="text/javascript">
                document.getElementById("sourceType").value="${parameterConversion.sourceType}";
            </script>
		</td>
	</tr>
	<tr>
		<td width="12%" align="left">集合标识</td>
		<td align="left">
		    <select id="sourceListFlag" name="sourceListFlag">
			    <option value="">----请选择----</option>
				<option value="Y">是</option>
			    <option value="N">否</option>
            </select>
            <script type="text/javascript">
                document.getElementById("sourceListFlag").value="${parameterConversion.sourceListFlag}";
            </script>
		</td>
	</tr>
	<tr>
		<td width="12%" align="left">目标名称</td>
		<td align="left">
              <input id="targetName" name="targetName" type="text" 
			         class="easyui-validatebox x-text"  
			         style="width:525px;" 
				     value="${parameterConversion.targetName}"/>
		</td>
	</tr>
	<tr>
		<td width="12%" align="left">目标类型</td>
		<td align="left">
		    <select id="targetType" name="targetType">
			    <option value="">----请选择----</option>
				<option value="Integer">整数</option>
			    <option value="Long">长整数</option>
				<option value="Double">数值</option>
				<option value="Date">日期</option>
				<option value="String">字符串</option>
            </select>
            <script type="text/javascript">
                document.getElementById("targetType").value="${parameterConversion.targetType}";
            </script>
		</td>
	</tr>
	<tr>
		<td width="12%" align="left">集合标识</td>
		<td align="left">
		    <select id="targetListFlag" name="targetListFlag">
			    <option value="">----请选择----</option>
				<option value="Y">是</option>
			    <option value="N">否</option>
            </select>
            <script type="text/javascript">
                document.getElementById("targetListFlag").value="${parameterConversion.targetListFlag}";
            </script>
		</td>
	</tr>
	<tr>
		<td width="12%" align="left">分隔符</td>
		<td align="left">
              <input id="delimiter" name="delimiter" type="text" 
			         class="easyui-validatebox x-text"  
			         style="width:125px;" 
				     value="${parameterConversion.delimiter}"/>
		</td>
	</tr>
	<tr>
		<td width="12%" align="left">转换类型</td>
		<td align="left">
            <select id="convertType" name="convertType">
				<option value="">----请选择----</option>
				<option value="expr">表达式</option>
				<option value="countSplitByComma">逗号分隔取个数</option>
				<option value="removeHtml">去除HTML标签</option>
				<option value="removeHtml2"><br>转换行后去除HTML标签</option>
				<option value="removeBlank"><br>去除空格</option>
				<option value="removeQuot"><br>去除单引号'</option>
				<option value="date_yyyyMMddHHmmss">日期转yyyyMMddHHmmss（如20180501120830）</option>
				<option value="date_yyyyMMddHHmm">日期转yyyyMMddHHmm（如201805011208）</option>
				<option value="date_yyyyMMddHH">日期转yyyyMMddHH（如2018050112）</option>
				<option value="date_yyyyMMdd">日期转yyyyMMdd（如20180501）</option>
				<option value="date_yyyyMM">日期转yyyyMM（如201805）</option>
				<option value="date_yyyy">日期转yyyy（如2018）</option>
				<option value="date_MM">日期转MM（如05）</option>
				<option value="date_dd">日期转dd（如01）</option>
				<option value="date_yyyyquarter">日期转yyyy季度（如2018Q2）</option>
				<option value="date_quarterQ">日期转季度（如Q2）</option>
				<option value="date_quarter">日期转季度（如2）</option>
			</select>
			<script type="text/javascript">
				document.getElementById("convertType").value="${parameterConversion.convertType}";
			</script> 
		</td>
	</tr>
	<tr>
		<td width="12%" align="left">转换条件</td>
		<td align="left">
		    <input id="convertCondition" name="convertCondition" type="text" 
			         class="easyui-validatebox x-text"  
			         style="width:525px;" 
				     value="${parameterConversion.convertCondition}"/>
            <div style="margin-top:5px;">
			  <span style="color:red; margin-left:2px;">
			     （提示：如果转换条件不为空，满足转换条件的字段才做转换，Java EL条件中使用的字段请转成小写。）
			  </span>
	        </div>
		</td>
	</tr>
	<tr>
		<td width="12%" align="left">转换表达式</td>
		<td align="left">
		    <input id="convertExpression" name="convertExpression" type="text" 
			         class="easyui-validatebox x-text"  
			         style="width:525px;" 
				     value="${parameterConversion.convertExpression}"/>
		</td>
	</tr>
	<tr>
		<td width="12%" align="left">转换模板</td>
		<td align="left">
		    <textarea id="convertTemplate" name="convertTemplate" rows="6" cols="46" class="x-textarea" style="width:525px;height:240px;" >${parameterConversion.convertTemplate}</textarea>
			<div style="margin-top:5px;">
		     <br>（提示：使用Freemarker模板引擎，使用需遵循Freemarker的语法）
			 <br>（提示：可以使用查询项中的结果变量，支持运算及函数。）
			</div>
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