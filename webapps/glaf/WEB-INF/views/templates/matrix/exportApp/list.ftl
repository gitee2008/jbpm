<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>数据导出</title>
<#include "/inc/init_easyui_import.ftl"/>
<script type="text/javascript">
   var contextPath="${request.contextPath}";

   jQuery(function(){
		jQuery('#mydatagrid').datagrid({
				width:1000,
				height:480,
				fit:true,
				fitColumns: true,
				nowrap: false,
				striped: true,
				collapsible: true,
				url: '${request.contextPath}/matrix/exportApp/json',
				remoteSort: false,
				singleSelect: true,
				idField: 'id',
				columns:[[
				        {title:'序号', field:'startIndex', width:50, sortable:false},
					    {title:'编号', field:'id', width:198, sortable:false},
						{title:'标题', field:'title', width:200, sortable:true},
						{title:'创建人', field:'createBy', width:80, sortable:true},
						{title:'创建时间', field:'createTime', width:80, sortable:true},
					    {title:'是否有效', field:'active', width:80, formatter:formatterActive},
						{title:'功能键', field:'functionKey', width:280, formatter:formatterKeys}
				]],
				rownumbers: false,
				pagination: true,
				pageSize: 10,
				pageList: [10,15,20,25,30,40,50,100],
				pagePosition: 'both',
				onDblClickRow: onRowClick 
			});
	});


	function formatterActive(val, row){
        var str = "";
		if(val == "Y"){
             str = "<font color='green'>有效</font>";
		} else {
             str = "<font color='red'>无效</font>";
		}
	    return str;
	}


	function formatterKeys(val, row){
		var str = "<a href='javascript:editRow(\""+row.id+"\");'>修改</a>&nbsp;<a href='javascript:syncList(\""+row.id+"\");'>查询项</a>&nbsp;<a href='javascript:paramList(\""+row.id+"\");'>参数转换</a>&nbsp;<a href='javascript:varList(\""+row.id+"\");'>模板变量</a>&nbsp;<a href='javascript:preprocessor(\""+row.id+"\");'>预处理器</a>&nbsp;<a href='javascript:imageList(\""+row.id+"\");'>图表</a>&nbsp;<a href='javascript:exportXls(\""+row.id+"\");'>导出</a>&nbsp;<a href='javascript:hisList(\""+row.id+"\");'>历史</a>";
	    return str;
	}

	function preprocessor(expId){
		var link = "${request.contextPath}/sys/exportPreprocessor/edit?currentStep=export_" + expId + "&code=export&expId="+expId;
        jQuery.layer({
			type: 2,
			maxmin: true,
			shadeClose: true,
			title: "数据预处理器",
			closeBtn: [0, true],
			shade: [0.8, '#000'],
			border: [10, 0.3, '#000'],
			offset: ['20px',''],
			fadeIn: 100,
			area: ['1050px', (jQuery(window).height() - 50) +'px'],
            iframe: {src: link}
		}); 
	}


	function imageList(expId){
		var link = "${request.contextPath}/matrix/exportChart?expId="+expId;
        jQuery.layer({
			type: 2,
			maxmin: true,
			shadeClose: true,
			title: "图片列表",
			closeBtn: [0, true],
			shade: [0.8, '#000'],
			border: [10, 0.3, '#000'],
			offset: ['20px',''],
			fadeIn: 100,
			area: ['950px', (jQuery(window).height() - 50) +'px'],
            iframe: {src: link}
		}); 
	}

	 
    function syncList(expId){
	    var link = '${request.contextPath}/matrix/exportItem?expId='+expId;
	    jQuery.layer({
			type: 2,
			maxmin: true,
			shadeClose: true,
			title: "查询项",
			closeBtn: [0, true],
			shade: [0.8, '#000'],
			border: [10, 0.3, '#000'],
			offset: ['20px',''],
			fadeIn: 100,
			area: ['1150px', (jQuery(window).height() - 50) +'px'],
            iframe: {src: link}
		}); 
	}

	function hisList(expId){
	    var link = '${request.contextPath}/matrix/exportFileHistory?expId='+expId;
	    jQuery.layer({
			type: 2,
			maxmin: true,
			shadeClose: true,
			title: "导出文件历史",
			closeBtn: [0, true],
			shade: [0.8, '#000'],
			border: [10, 0.3, '#000'],
			offset: ['20px',''],
			fadeIn: 100,
			area: ['1050px', (jQuery(window).height() - 50) +'px'],
            iframe: {src: link}
		}); 
	}

	function paramList(expId){
	    var link = '${request.contextPath}/matrix/parameterConversion?key='+expId;
	    jQuery.layer({
			type: 2,
			maxmin: true,
			shadeClose: true,
			title: "参数转换",
			closeBtn: [0, true],
			shade: [0.8, '#000'],
			border: [10, 0.3, '#000'],
			offset: ['20px',''],
			fadeIn: 100,
			area: ['1080px', (jQuery(window).height() - 50) +'px'],
            iframe: {src: link}
		}); 
	}

	function varList(expId){
	    var link = '${request.contextPath}/matrix/exportTemplateVar?expId='+expId;
	    jQuery.layer({
			type: 2,
			maxmin: true,
			shadeClose: true,
			title: "模板变量",
			closeBtn: [0, true],
			shade: [0.8, '#000'],
			border: [10, 0.3, '#000'],
			offset: ['20px',''],
			fadeIn: 100,
			area: ['1080px', (jQuery(window).height() - 50) +'px'],
            iframe: {src: link}
		}); 
	}

 
	function editRow(id){
	    var link="${request.contextPath}/matrix/exportApp/edit?id="+id;
	    jQuery.layer({
			type: 2,
			maxmin: true,
			shadeClose: true,
			title: "修改记录",
			closeBtn: [0, true],
			shade: [0.8, '#000'],
			border: [10, 0.3, '#000'],
			offset: ['20px',''],
			fadeIn: 100,
			area: ['980px', (jQuery(window).height() - 50) +'px'],
            iframe: {src: link}
		});
	}

	function addNew(){
	    var link="${request.contextPath}/matrix/exportApp/edit";
	    jQuery.layer({
			type: 2,
			maxmin: true,
			shadeClose: true,
			title: "新增记录",
			closeBtn: [0, true],
			shade: [0.8, '#000'],
			border: [10, 0.3, '#000'],
			offset: ['20px',''],
			fadeIn: 100,
			area: ['980px', (jQuery(window).height() - 50) +'px'],
            iframe: {src: link}
		});
	}

	function onRowClick(rowIndex, row){
	    var link = '${request.contextPath}/matrix/exportApp/edit?id='+row.id;
	    jQuery.layer({
			type: 2,
			maxmin: true,
			shadeClose: true,
			title: "修改记录",
			closeBtn: [0, true],
			shade: [0.8, '#000'],
			border: [10, 0.3, '#000'],
			offset: ['20px',''],
			fadeIn: 100,
			area: ['980px', (jQuery(window).height() - 50) +'px'],
            iframe: {src: link}
		}); 
	}

	function searchWin(){
	    jQuery('#dlg').dialog('open').dialog('setTitle','数据导出查询');
	    //jQuery('#searchForm').form('clear');
	}

	function resize(){
		jQuery('#mydatagrid').datagrid('resize', {
			width:800,
			height:400
		});
	}

	function editSelected(){
	    var rows = jQuery('#mydatagrid').datagrid('getSelections');
	    if(rows == null || rows.length !=1){
		  alert("请选择其中一条记录。");
		  return;
	    }
	    var selected = jQuery('#mydatagrid').datagrid('getSelected');
	    if (selected ){
		  var link = '${request.contextPath}/matrix/exportApp/edit?id='+selected.id;
		  jQuery.layer({
			type: 2,
			maxmin: true,
			shadeClose: true,
			title: "修改记录",
			closeBtn: [0, true],
			shade: [0.8, '#000'],
			border: [10, 0.3, '#000'],
			offset: ['20px',''],
			fadeIn: 100,
			area: ['980px', (jQuery(window).height() - 50) +'px'],
            iframe: {src: link}
		  });
	    }
	}

	function viewSelected(){
		var rows = jQuery('#mydatagrid').datagrid('getSelections');
		if(rows == null || rows.length !=1){
			alert("请选择其中一条记录。");
			return;
		}
		var selected = jQuery('#mydatagrid').datagrid('getSelected');
		if (selected ){
		    var link = '${request.contextPath}/matrix/exportApp/edit?id='+selected.id;
			jQuery.layer({
				type: 2,
				maxmin: true,
				shadeClose: true,
				title: "修改记录",
				closeBtn: [0, true],
				shade: [0.8, '#000'],
				border: [10, 0.3, '#000'],
				offset: ['20px',''],
				fadeIn: 100,
				area: ['980px', (jQuery(window).height() - 50) +'px'],
				iframe: {src: link}
			  });
		}
	}

	function deleteSelections(){
		var ids = [];
		var rows = jQuery('#mydatagrid').datagrid('getSelections');
		for(var i=0;i<rows.length;i++){
			ids.push(rows[i].id);
		}
		if(ids.length > 0 ){
		  if(confirm("数据删除后不能恢复，确定删除吗？")){
		    var str = ids.join(',');
			jQuery.ajax({
				   type: "POST",
				   url: '${request.contextPath}/matrix/exportApp/delete?ids='+str,
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
		} else {
			alert("请选择至少一条记录。");
		}
	}

	function exportXls0(){
	    var rows = jQuery('#mydatagrid').datagrid('getSelections');
	    if(rows == null || rows.length !=1){
		    alert("请选择其中一条记录。");
		    return;
	    }
	    var selected = jQuery('#mydatagrid').datagrid('getSelected');
	    if (selected){
			var myDate = new Date();
		    var link = '${request.contextPath}/matrix/exportApp/exportXls?expId='+selected.id+"&ts="+myDate.getTime();
		    window.open(link);		
	    }
	}

	function exportXls(expId){
		var myDate = new Date();
		var link = '${request.contextPath}/matrix/exportApp/exportXls?q=1&expId='+expId+"&ts="+myDate.getTime();
		window.open(link);
	}

	function reloadGrid(){
	    jQuery('#mydatagrid').datagrid('reload');
	}

	function getSelected(){
	    var selected = jQuery('#mydatagrid').datagrid('getSelected');
	    if (selected){
		    alert(selected.code+":"+selected.name+":"+selected.addr+":"+selected.col4);
	    }
	}

	function getSelections(){
	    var ids = [];
	    var rows = jQuery('#mydatagrid').datagrid('getSelections');
	    for(var i=0;i<rows.length;i++){
		    ids.push(rows[i].code);
	    }
	    alert(ids.join(':'));
	}

	function clearSelections(){
	    jQuery('#mydatagrid').datagrid('clearSelections');
	}

	function loadGridData(url){
            //var params = jQuery("#iForm").formSerialize();
	    jQuery.ajax({
			type: "POST",
			url:  url,
			//data: params,
			dataType:  'json',
			error: function(data){
				alert('服务器处理错误！');
			},
			success: function(data){
				jQuery('#mydatagrid').datagrid('loadData', data);
			}
		});
	}

	function searchData(){
        var params = jQuery("#searchForm").formSerialize();
        jQuery.ajax({
                    type: "POST",
                    url: '${request.contextPath}/matrix/exportApp/json',
                    dataType: 'json',
                    data: params,
                    error: function(data){
                              alert('服务器处理错误！');
                    },
                    success: function(data){
                              jQuery('#mydatagrid').datagrid('loadData', data);
                    }
                  });

	    jQuery('#dlg').dialog('close');
	}
		 
</script>
</head>
<body style="margin:1px;">  
<div style="margin:0;"></div>  
<div class="easyui-layout" data-options="fit:true">  
   <div data-options="region:'north',split:true,border:true" style="height:39px"> 
    <div class="toolbar-backgroud"  > 
	<img src="${request.contextPath}/static/images/window.png">
	&nbsp;<span class="x_content_title">数据导出列表</span>
    <a href="#" class="easyui-linkbutton" data-options="plain:true, iconCls:'icon-add'" 
	   onclick="javascript:addNew();">新增</a>  
    <a href="#" class="easyui-linkbutton" data-options="plain:true, iconCls:'icon-edit'"
	   onclick="javascript:editSelected();">修改</a>
	<a href="#" class="easyui-linkbutton" data-options="plain:true, iconCls:'icon-exp'"
	   onclick="javascript:exportXls0();">导出Excel</a>
   </div> 
  </div> 
  <div data-options="region:'center',border:true">
	 <table id="mydatagrid"></table>
  </div>  
</div>
</body>
</html>