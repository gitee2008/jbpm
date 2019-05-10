<!DOCTYPE html>
<html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>导出文件历史</title>
<#include "/inc/init_easyui_import.ftl"/>
<script type="text/javascript">
    var contextPath="${request.contextPath}";

    function getLink(){
	    var link_ = contextPath+"/matrix/exportFileHistory/json?expId=${expId}&genYmd=${genYmd}&jobNo=${jobNo}";
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
				        {title:'序号', field:'startIndex', width:50, sortable:false},
					    {title:'文件编号', field:'id', width:90, sortable:true},
						{title:'文件名', field:'filename', width:220, sortable:true},
						{title:'报表路径', field:'path', width:120, sortable:true},
						{title:'批次编号', field:'jobNo', width:120, sortable:true},
						{title:'排序号', field:'sortNo', width:90, sortable:true, align:'right'},
						{title:'创建人', field:'createBy', width:90, sortable:true},
						{title:'创建时间', field:'createTime_datetime', width:120, sortable:true},
						{title:'功能键', field:'functionKey', width:100, formatter:formatterKeys}
				]],
				rownumbers: false,
				pagination: true,
				pageSize: 10,
				pageList: [10,15,20,25,30,40,50,100],
				pagePosition: 'both',
				onDblClickRow: onMyRowClick 
			});

			var pgx = $("#mydatagrid").datagrid("getPager");
			if(pgx){
			   $(pgx).pagination({
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
		var str = "<a href='javascript:download(\""+row.id+"\");'>下载</a>&nbsp;<a href='javascript:deleteRow(\""+row.id+"\");'>删除</a>";
	    return str;
	}

	function onMyRowClick(rowIndex, row){
	    var link = contextPath + '/matrix/exportFileHistory/download?id='+row.id;
		window.open(link); 
	}

    function download(id){
	    var link = contextPath + '/matrix/exportFileHistory/download?id='+id;
		window.open(link); 
	}

    function downloadx(){
	    var rows = jQuery('#mydatagrid').datagrid('getSelections');
	    if(rows == null || rows.length !=1){
		    alert("请选择其中一条记录。");
		    return;
	    }
	    var selected = jQuery('#mydatagrid').datagrid('getSelected');
	    if (selected){
		    var link = contextPath + '/matrix/exportFileHistory/download?id='+selected.id;
		    window.open(link); 
	    }
	}
	
	function deleteRow(id){
		if(confirm("数据删除后不能恢复，确定删除吗？")){
			jQuery.ajax({
				   type: "POST",
				   url: contextPath+'/matrix/exportFileHistory/delete?id='+id,
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
						   var link_ = jQuery("#link_").val();
						   if(link_ != ""){
							   //alert(link_);
                               loadGridData(link_);
						   }else{
					           jQuery('#mydatagrid').datagrid('reload');
						   }
					   }
				   }
			 });
		  }
	}

	function deleteSelection(){
		var rows = jQuery('#mydatagrid').datagrid('getSelections');
	    if(rows == null || rows.length !=1){
		    alert("请选择其中一条记录。");
		    return;
	    }
	    var selected = jQuery('#mydatagrid').datagrid('getSelected');
	    if (selected){
		  if(confirm("数据删除后不能恢复，确定删除吗？")){
			jQuery.ajax({
				   type: "POST",
				   url: contextPath+'/matrix/exportFileHistory/delete?id='+selected.id,
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
						   var link_ = jQuery("#link_").val();
						   if(link_ != ""){
							   //alert(link_);
                               loadGridData(link_);
						   }else{
					           jQuery('#mydatagrid').datagrid('reload');
						   }
					   }
				   }
			 });
		  }
	   }
	}

	function loadGridData(url){
		jQuery("#link_").val(url);
	    jQuery.ajax({
			type: "POST",
			url: url,
			dataType: 'json',
			error: function(data){
				alert('服务器处理错误！');
			},
			success: function(data){
				jQuery('#mydatagrid').datagrid('loadData', data);
			}
		});
	}

	function reloadGrid(){
	    jQuery('#mydatagrid').datagrid('reload');
	}

</script>
</head>
<body style="margin:1px;">  
<div style="margin:0;"></div>  
<input type="hidden" id="link_" name="link_">
<div class="easyui-layout" data-options="fit:true">  
   <div data-options="region:'north',split:false, border:true" style="height:48px" class="toolbar-backgroud"> 
    <div style="margin:4px;"> 
	  <table width="100%" align="left">
		<tbody>
		 <tr>
		    <td width="55%" align="left">
				<img src="${contextPath}/static/images/window.png">
				&nbsp;<span class="x_content_title">导出文件历史列表</span>
				<a href="#" class="easyui-linkbutton" data-options="plain:true, iconCls:'icon-down'"
				   onclick="javascript:downloadx();">下载</a>  
				<a href="#" class="easyui-linkbutton" data-options="plain:true, iconCls:'icon-remove'"
				   onclick="javascript:deleteSelection();">删除</a>
			</td>
			<td width="45%" align="left">&nbsp;</td>
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