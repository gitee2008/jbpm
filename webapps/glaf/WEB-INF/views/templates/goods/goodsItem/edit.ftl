<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>物品项</title>
<meta name="renderer" content="webkit">
<meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
<meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1">
<#include "/inc/init_layui_import.ftl"/>
<script type="text/javascript" src="${contextPath}/static/scripts/framework.js"></script>
<script type="text/javascript">

	function saveData(){
		//var jsonObject = fromToJson(document.getElementById("iForm"));
		var jsonObject = jQuery('#iForm').serializeObject();
        document.getElementById("json").value=JSON.stringify(jsonObject);
		var params = jQuery("#iForm").formSerialize();
		jQuery.ajax({
				   type: "POST",
				   url: '${contextPath}/goods/goodsItem/save',
				   data: params,
				   dataType: 'json',
				   error: function(data){
					   layer.msg('服务器处理错误！');
				   },
				   success: function(data){
					   if(data != null && data.message != null){
						   layer.msg(data.message);
					   } else {
						   layer.msg('操作成功完成！');
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
				   url: '${contextPath}/goods/goodsItem/save',
				   data: params,
				   dataType: 'json',
				   error: function(data){
					   layer.msg('服务器处理错误！');
				   },
				   success: function(data){
					   if(data != null && data.message != null){
						   layer.msg(data.message);
					   } 
					   if(data.statusCode == 200){
						   layer.msg('操作成功完成！');
					       window.parent.location.reload();
					   }
				   }
			 });
	}

</script>
</head>
<body style="margin:1px;">
<div class="layui-container" style="width:100%;">  
  <div> 
    <div class="toolbar-backgroud" style="height:48px"> 
	<span class="x_content_title">&nbsp;<img src="${contextPath}/static/images/window.png">&nbsp;编辑物品项</span>
	<button class="layui-btn layui-btn-normal layui-btn-sm" onclick="javascript:saveData();" >保存</button> 
    </div> 
  </div>

  <div>
  <form class="layui-form" id="iForm" name="iForm" method="post">
  <input type="hidden" id="json" name="json">
  <input type="hidden" id="id" name="id" value="${goodsItem.id}"/>
  <table style="line-height:45px; width:100%;" align="center">
    <tbody>
	<tr>
		<td width="20%" align="left">分类</td>
		<td align="left">
              <input id="category" name="category" type="text" 
			         class="layui-input"  
			  
				     value="${goodsItem.category}"/>
		</td>
	</tr>
	<tr>
		<td width="20%" align="left">发起人</td>
		<td align="left">
              <input id="itemName" name="itemName" type="text" 
			         class="layui-input"  
			  
				     value="${goodsItem.itemName}"/>
		</td>
	</tr>
	<tr>
		<td width="20%" align="left">地址</td>
		<td align="left">
              <input id="itemLocation" name="itemLocation" type="text" 
			         class="layui-input"  
			  
				     value="${goodsItem.itemLocation}"/>
		</td>
	</tr>
	<tr>
		<td width="20%" align="left">小图片地址</td>
		<td align="left">
              <input id="smallUrl" name="smallUrl" type="text" 
			         class="layui-input"  
			  
				     value="${goodsItem.smallUrl}"/>
		</td>
	</tr>
	<tr>
		<td width="20%" align="left">图片地址</td>
		<td align="left">
              <input id="itemUrl" name="itemUrl" type="text" 
			         class="layui-input"  
			  
				     value="${goodsItem.itemUrl}"/>
		</td>
	</tr>
	<tr>
		<td width="20%" align="left">标题</td>
		<td align="left">
              <input id="itemTitle" name="itemTitle" type="text" 
			         class="layui-input"  
			  
				     value="${goodsItem.itemTitle}"/>
		</td>
	</tr>
	<tr>
		<td width="20%" align="left">内容描述</td>
		<td align="left">
              <input id="itemContent" name="itemContent" type="text" 
			         class="layui-input"  
			  
				     value="${goodsItem.itemContent}"/>
		</td>
	</tr>
	<tr>
		<td width="20%" align="left">进度</td>
		<td align="left">
			<input id="itemStatus" name="itemStatus" type="text" 
			       class="layui-input" style="text-align: right" lay-verify="number"
				   increment="10"  
				   value="${goodsItem.itemStatus}"/>
		</td>
	</tr>
	<tr>
		<td width="20%" align="left">金额</td>
		<td align="left">
			<input id="itemMoney" name="itemMoney" type="text" lay-verify="number"
			       class="layui-input"  precision="2" style="text-align: right"
			
				  value="${goodsItem.itemMoney}"/>
		</td>
	</tr>
	<tr>
		<td width="20%" align="left">有效天数</td>
		<td align="left">
			<input id="itemDay" name="itemDay" type="text" 
			       class="layui-input" style="text-align: right" lay-verify="number"
				   increment="10"  
				   value="${goodsItem.itemDay}"/>
		</td>
	</tr>
 
    </tbody>
  </table>
  </form>
</div>
</div>
</body>
</html>