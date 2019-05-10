<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>模板变量</title>
<#include "/inc/init_easyui_import.ftl"/>
<script type="text/javascript">
    var contextPath="${request.contextPath}";

	function saveData(){
		var params = jQuery("#iForm").formSerialize();
		jQuery.ajax({
				   type: "POST",
				   url: '${request.contextPath}/matrix/exportTemplateVar/save',
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
		document.getElementById("id").value="";
		var params = jQuery("#iForm").formSerialize();
		jQuery.ajax({
				   type: "POST",
				   url: '${request.contextPath}/matrix/exportTemplateVar/save',
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
	<img src="${request.contextPath}/static/images/window.png">&nbsp;<span class="x_content_title">编辑模板变量</span>
	<a href="#" class="easyui-linkbutton" data-options="plain:true, iconCls:'icon-save'" 
	   onclick="javascript:saveData();" >保存</a> 
    </div> 
  </div>

  <div data-options="region:'center',border:false,cache:true">
  <form id="iForm" name="iForm" method="post">
  <input type="hidden" id="id" name="id" value="${exportTemplateVar.id}"/>
  <input type="hidden" id="expId" name="expId" value="${expId}"/>
  <table class="easyui-form" style="width:788px;" align="center">
    <tbody>
	<tr>
		<td width="15%" align="left">标  题</td>
		<td align="left">
            <input id="title" name="title" type="text" 
			       class="easyui-validatebox x-text" style="width:525px;" 
				   value="${exportTemplateVar.title}"/>
		</td>
	</tr>
	<tr>
		<td width="15%" align="left">变量名</td>
		<td align="left">
            <input id="name" name="name" type="text" 
			       class="easyui-validatebox x-text" style="width:525px;" 
				   value="${exportTemplateVar.name}"/>
			<div style="color:red; margin-top:5px;">
		     （提示：只能使用大小写字母、数字及下划线，不能使用汉字及特殊字符做变量名称。）
			</div>
		</td>
	</tr>
	<tr>
		<td width="90" align="left">模  板</td>
		<td align="left">
		    <textarea id="varTemplate" name="varTemplate" rows="6" cols="46" class="x-textarea" 
			          style="font: 13px Consolas,Courier New,Arial; width:525px;height:240px;" >${exportTemplateVar.varTemplate}</textarea>
			<div style="margin-top:5px;">
		     （提示：使用Freemarker模板引擎，使用需遵循Freemarker的语法）
			 <br>（提示：可以使用查询项中的结果变量，支持运算及函数。）
			</div>
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
                 document.getElementById("locked").value="${exportTemplateVar.locked}";
             </script>
		</td>
	</tr>
	<tr>
	  <td><br><br><br><br><td>
	</tr>
    </tbody>
  </table>
  </form>
</div>
</div>
</body>
</html>