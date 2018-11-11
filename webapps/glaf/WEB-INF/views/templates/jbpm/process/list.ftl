<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>流程实例</title>
<meta name="renderer" content="webkit">
<meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
<meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1">
<#include "/inc/init_layui_import.ftl"/>
<script type="text/javascript">

  function getLink(){
	var link_ = "${contextPath}/jbpm/processInstances/json?q=1";
	var businessKey = jQuery("#businessKey").val();
	if(businessKey != "" && businessKey != "undefined" ){
		link_ = link_ + "&businessKey="+businessKey;
	}
	return link_;
  }

  layui.use('table', function(){
	var table = layui.table;
	  
	table.render({
		elem: '#mydatagrid',
		//toolbar: '#toolbar',
        title: '流程实例',
		url: getLink(),
		parseData: function(res){ //res 即为原始返回的数据
		 return {
		  "code": res.code, //解析接口状态
		  "msg": res.message, //解析提示文本
		  "count": res.total, //解析数据长度
		  "data": res.rows //解析数据列表
		 };
	    },
		totalRow: false,
		cols: [[
		    {field:'startIndex', title:'序号', width:68, sort:true},
			{field:'id', title:'流程编号', width:120, sort:true},
			{field:'key', title:'业务主键', width:180, sort:true},
			{field:'startDate', title:'开始时间', width:120, sort:true},
			{field:'endDate', title:'结束时间', width:120, sort:true},
			{field:'version', title:'版本', width:90, sort:true},
			{fixed:'right', title:'功能键', toolbar: '#tool_function', width:120}
		]],
		page: true,
		limit: 10,
		limits: [10,15,20,25,50,100]
	});
 

    //头工具栏事件
    table.on('toolbar(mydatagrid)', function(obj){
      var checkStatus = table.checkStatus(obj.config.id);
      switch(obj.event){
        case 'getCheckData':
          var data = checkStatus.data;
          layer.alert(JSON.stringify(data));
        break;
        case 'getCheckLength':
          var data = checkStatus.data;
          layer.msg('选中了：'+ data.length + ' 个');
        break;
        case 'isAll':
          layer.msg(checkStatus.isAll ? '全选': '未全选');
        break;
      };
    });

    //监听行工具事件
    table.on('tool(mydatagrid)', function(obj){
      var data = obj.data;
      //console.log(obj)
      if(obj.event === 'pause'){
        
      } else if(obj.event === 'renew'){
        
      }
    });

  });


 
</script>
</head>
<body style="margin:1px;" style="width:100%;">  
<div class="layui-container" style="width:100%;">  
   <div style="height:48px" class="toolbar-backgroud"> 
    <div style="margin:4px;"> 
	  <table width="100%" align="left">
		<tbody>
		 <tr>
		    <td width="55%" align="left">
				<img src="${contextPath}/static/images/window.png">
				&nbsp;<span class="x_content_title">流程实例列表</span>
				 
			</td>
			<td width="45%" align="right">
			   
			  &nbsp;&nbsp;&nbsp;&nbsp;
			</td>
		</tr>
	   </tbody>
	  </table>
   </div> 
  </div> 

  <table id="mydatagrid" lay-filter="mydatagrid"></table>
 
  <script type="text/html" id="tool_function">
    <a class="layui-btn layui-btn-xs" lay-event="pause">挂起</a>
    <a class="layui-btn layui-btn-danger layui-btn-xs" lay-event="renew">恢复</a>
  </script>

</div>
</body>
</html>