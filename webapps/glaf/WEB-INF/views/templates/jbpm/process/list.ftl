<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>流程实例</title>
<#include "/inc/init_easyui_layer3_import.ftl"/>
<script type="text/javascript">

    function getLink(){
		var link_ = "${request.contextPath}/jbpm/processInstances/json?q=1";
		var businessKey = jQuery("#businessKey").val();
		if(businessKey != undefined && businessKey != "" && businessKey != "undefined" ){
			link_ = link_ + "&businessKey="+businessKey;
		}
		return link_;
	}

    jQuery(function(){
		jQuery('#mydatagrid').datagrid({
				width:1000,
				height:480,
				fit:true,
				fitColumns: true,
				nowrap: false,
				striped: true,
				collapsible: true,
				url: getLink(),
				remoteSort: false,
				singleSelect: true,
				idField: 'id',
				columns:[[
				    {title:'序号', field:'startIndex', width:80, sortable:false},
					{title:'流程编号', field:'id', width:120, sortable:true},
					{title:'业务主键', field:'key', width:120, sortable:true},
					{title:'开始时间', field:'startDate', width:120, sortable:true},
					{title:'结束时间', field:'endDate', width:180, sortable:true},
					{title:'版本', field:'version', width:90, sortable:true},
					{title:'功能键', field:'functionKey', width:150, formatter:formatterKeys}
				]],
				rownumbers: false,
				pagination: true,
				pageSize: 20,
				pageList: [10,15,20,25,30,40,50,100,200],
				pagePosition: 'both',
				onDblClickRow: onMyRowClick 
			});

			var pgx = jQuery("#mydatagrid").datagrid("getPager");
			if(pgx){
			   jQuery(pgx).pagination({
				   onBeforeRefresh:function(){
					   //alert('before refresh');
				   },
				   onRefresh:function(pageNumber,pageSize){
					   //alert(pageNumber);
					   //alert(pageSize);
					   loadGridData(getLink()+"&page="+pageNumber+"&rows="+pageSize);
					},
				   onChangePageSize:function(){
					   //alert('pagesize changed');
					   loadGridData(getLink());
					},
				   onSelectPage:function(pageNumber, pageSize){
					   //alert(pageNumber);
					   //alert(pageSize);
					   loadGridData(getLink()+"&page="+pageNumber+"&rows="+pageSize);
					}
			   });
			}
	});


	function formatterKeys(val, row){
		var str = "<a href='#' onclick='javascript:viewProcess(\""+row.id+"\")'>查看</a>&nbsp;";
		if(row.isSuspended == "true"){
            str = str + "<a href='#' onclick='javascript:resume(\""+row.id+"\")'>恢复</a>";
		} else {
			if(row.isEnd == "false"){
               str = str + "<a href='#' onclick='javascript:suspend(\""+row.id+"\")'>挂起</a>";
			}
		}
	    return str;
	}

	function resume(processInstanceId){
		if(confirm("确定恢复流程执行吗？")){
           jQuery.ajax({
				   type: "POST",
				   url: '${request.contextPath}/jbpm/processInstances/resume?processInstanceId='+processInstanceId,
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
					   jQuery('#mydatagrid').datagrid('reload');
				   }
			 });
		}
	}


	function suspend(processInstanceId){
		//alert(processInstanceId);
		if(confirm("流程实例将不能执行，确定挂起流程吗？")){
           jQuery.ajax({
				   type: "POST",
				   url: '${request.contextPath}/jbpm/processInstances/suspend?processInstanceId='+processInstanceId,
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
					   jQuery('#mydatagrid').datagrid('reload');
				   }
			 });
		}
	}


	function onMyRowClick(rowIndex, row){
	    var link = '${contextPath}/jbpm/task/task?processInstanceId='+row.id;
		layer.open({
		  type: 2,
          maxmin: true,
		  shadeClose: true,
		  title: "查看流程",
		  area: ['1280px', (jQuery(window).height() - 50) +'px'],
		  shade: 0.8,
		  fixed: false, //不固定
		  shadeClose: true,
		  content: [link, 'yes']
		});
	}

	function viewProcess(processInstanceId){
	    var link = '${contextPath}/jbpm/task/task?processInstanceId='+processInstanceId;
		layer.open({
		  type: 2,
          maxmin: true,
		  shadeClose: true,
		  title: "查看流程",
		  area: ['1280px', (jQuery(window).height() - 50) +'px'],
		  shade: 0.8,
		  fixed: false, //不固定
		  shadeClose: true,
		  content: [link, 'yes']
		});
	}

      
	function reloadGrid(){
	    jQuery('#mydatagrid').datagrid('reload');
	}


	function loadGridData(url){
	    jQuery.ajax({
			type: "POST",
			url:  url,
			dataType: 'json',
			error: function(data){
				alert('服务器处理错误！');
			},
			success: function(data){
				jQuery('#mydatagrid').datagrid('loadData', data);
			}
		});
	}
 
</script>
</head>
<body style="margin:1px;">  
<div style="margin:0;"></div>  
<div class="easyui-layout" data-options="fit:true">  
   <div data-options="region:'north', split:false, border:true" style="height:42px" class="toolbar-backgroud"> 
    <div style="margin:4px;"> 
	<table style="width:100%;" align="left">
    <tbody>
	<tr>
	    <td width="40%" align="left">
		  <img src="${contextPath}/static/images/window.png">
		  &nbsp;<span class="x_content_title">流程实例列表</span>
		 
		</td>
		<td align="left">
           
		</td>
	</tr>
	</tbody>
   </table>
   </div> 
  </div> 
  <div data-options="region:'center',border:true">
	 <table id="mydatagrid"></table>
  </div>  
</div>
</div>
</body>
</html>