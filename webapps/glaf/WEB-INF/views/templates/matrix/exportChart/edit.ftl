<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>导出图表</title>
<#include "/inc/init_easyui_import.ftl"/>
<script type="text/javascript">
    var contextPath="${request.contextPath}";

	function saveData(){
		var params = jQuery("#iForm").formSerialize();
		jQuery.ajax({
				   type: "POST",
				   url: '${request.contextPath}/matrix/exportChart/save',
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
					   
					   if (window.opener) {
						    window.opener.location.reload();
					   } else if (window.parent) {
						    window.parent.location.reload();
					   } 
				   }
			 });
	}

	function saveAsData(){
		document.getElementById("id").value="";
		var params = jQuery("#iForm").formSerialize();
		jQuery.ajax({
				   type: "POST",
				   url: '${request.contextPath}/matrix/exportChart/save',
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
	<span class="x_content_title">&nbsp;<img src="${request.contextPath}/static/images/window.png">&nbsp;编辑</span>
	<a href="#" class="easyui-linkbutton" data-options="plain:true, iconCls:'icon-save'" 
	   onclick="javascript:saveData();" >保存</a> 
    </div> 
  </div>

  <div data-options="region:'center',border:false,cache:true">
  <form id="iForm" name="iForm" method="post">
  <input type="hidden" id="id" name="id" value="${exportChart.id}"/>
  <input type="hidden" id="expId" name="expId" value="${expId}"/>
  <table class="easyui-form" style="width:680px;" align="center">
    <tbody>
	<tr>
		<td width="20%" align="left">名称</td>
		<td align="left">
            <input id="name" name="name" type="text" 
			       class="easyui-validatebox  x-text" style="width:485px;"  
				   value="${exportChart.name}"/>
		</td>
	</tr>
	<tr>
		<td width="20%" align="left">标题</td>
		<td align="left">
            <input id="title" name="title" type="text" 
			       class="easyui-validatebox  x-text" style="width:485px;" 			
				   value="${exportChart.title}"/>
		</td>
	</tr>
	<tr>
		<td width="20%" align="left">高度</td>
		<td align="left">
			<input id="height" name="height" type="text" 
			       class="easyui-numberbox x-text" style="width:45px; text-align: right"  
				   increment="50" precision="0" maxLength="4"  
				   value="${exportChart.height}"/>
		    &nbsp; 宽度 &nbsp;
			<input id="width" name="width" type="text" 
			       class="easyui-numberbox x-text" style="width:45px; text-align: right" 
				   increment="50" precision="0" maxLength="4" 
				   value="${exportChart.width}"/>
		    &nbsp;图像类型&nbsp;
            <select id="imageType" name="imageType">
				<option value="png">png</option>
			    <option value="gif">gif</option>
				<option value="jpg">jpg</option>
            </select>
            <script type="text/javascript">
                document.getElementById("imageType").value="${exportChart.imageType}";
            </script>
		</td>
	</tr>
	<tr>
		<td width="20%" align="left">图表定义</td>
		<td align="left">
            <select id="chartId" name="chartId">
			    <option value="">----请选择----</option>
				<#list charts as item>
				<option value="${item.name}">${item.value}</option>
				</#list>
            </select>
            <script type="text/javascript">
                document.getElementById("chartId").value="${exportChart.chartId}";
            </script>
			<div style="margin-top:5px;">
		     （提示：可以选择配置好的图表或在下面输入链接地址通过浏览器截图生成）
			</div>
		</td>
	</tr>
	<tr>
		<td width="20%" align="left">链接地址</td>
		<td align="left">
		    <textarea id="chartUrl" name="chartUrl" rows="6" cols="46" class="x-textarea" 
			          style="font: 13px Consolas,Courier New,Arial; width:485px; height:90px;" >${exportChart.chartUrl}</textarea>
			<div style="margin-top:5px;">
		     （提示：使用Freemarker模板引擎，使用需遵循Freemarker的语法）<br>
			 （提示：<script type="text/javascript">document.write("#");</script>{service_url}代表本系统服务地址，
			      例如：http://127.0.0.1:8080/glaf）
			</div>
		</td>
	</tr>
	<tr>
		<td width="20%" align="left">图表种类</td>
		<td align="left">
            <select id="chartType" name="chartType">
				<option value="chart">JFreeChart</option>
			    <option value="echarts">百度ECharts</option>
				<option value="highcharts">HighCharts</option>
				<option value="kendo">KendoCharts</option>
            </select>
            <script type="text/javascript">
                document.getElementById("chartType").value="${exportChart.chartType}";
            </script>
		</td>
	</tr>
	<tr>
		<td width="20%" align="left">是否截图</td>
		<td align="left">
            <select id="snapshotFlag" name="snapshotFlag">
				<option value="Y">是</option>
			    <option value="N">否</option>
            </select>
            <script type="text/javascript">
                document.getElementById("snapshotFlag").value="${exportChart.snapshotFlag}";
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
                 document.getElementById("locked").value="${exportChart.locked}";
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