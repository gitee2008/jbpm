<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>应用设置</title>
<#include "/inc/init_easyui_import.ftl"/>
<script type="text/javascript">
   var contextPath="${contextPath}";

   var setting = {
			async: {
				enable: true,
				url:"${contextPath}/sys/application/treeJson?parentId=${parentId}",
				dataFilter: filter
			},
			callback: {
				onClick: zTreeOnClick
			}
		};
  
  	function filter(treeId, parentNode, childNodes) {
		if (!childNodes) return null;
		for (var i=0, l=childNodes.length; i<l; i++) {
			childNodes[i].name = childNodes[i].name.replace(/\.n/g, '.');
			childNodes[i].icon="${contextPath}/static/images/basic.gif";
		}
		return childNodes;
	}


    function zTreeOnClick(event, treeId, treeNode, clickFlag) {
		jQuery("#nodeId").val(treeNode.id);
		var link = "${contextPath}/sys/application/json?parentId="+treeNode.id;
		loadGridData(link);
 	}

    jQuery(document).ready(function(){
		jQuery.fn.zTree.init(jQuery("#myTree"), setting);
	});


    jQuery(function(){
		jQuery('#mydatagrid').datagrid({
				width:1000,
				height:480,
				fit:true,
				fitColumns: true,
				nowrap: false,
				striped: true,
				collapsible: true,
				url: '${contextPath}/sys/application/json?parentId=${parentId}',
				remoteSort: false,
				singleSelect: true,
				idField: 'id',
				columns:[[
				        {title:'序号', field:'startIndex', width:60, sortable:false},
					    {title:'编号', field:'id', width:60, sortable:false},
					    {title:'名称', field:'name', width:150, sortable:false},
					    {title:'代码', field:'code', width:120, sortable:false},
					    {title:'链接地址', field:'url', width:320, sortable:false},
				        {title:'是否启用', field:'locked', width:90, align:"center", sortable:false, formatter:formatterActive},
					    {field:'functionKey',title:'功能键',width:120, formatter:formatterKeys }
				]],
				rownumbers: false,
				pagination: true,
				pageSize: 20,
				pageList: [20,25,30,40,50,100,200,500],
				pagePosition: 'both'
			});


			$('#mydatagrid').datagrid({
				onDblClickRow: function(index, row){
					 editRow(row.id);
				}
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

		 
	function addNew(){
	    //var link="${contextPath}/sys/application/edit?fromUrl=${fromUrl}";
	    //art.dialog.open(link, { height: 420, width: 680, title: "添加记录", lock: true, scrollbars:"no" }, false);
		//location.href=link;
		var nodeId = jQuery("#nodeId").val();
		link="${contextPath}/sys/application/edit?parentId="+nodeId;
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
	    var link = '${contextPath}/sys/application/edit?id='+row.id;
	    //art.dialog.open(link, { height: 420, width: 680, title: "修改记录", lock: true, scrollbars:"no" }, false);
		location.href=link;
	}

	function formatterActive(val, row){
		if(val == 0){
			return "<font color='green'>启用</font>";
		} else {
            return "<font color='red'>禁用</font>";
		}
	}

	function formatterKeys(val, row){
		var str = "<a href='javascript:editRow(\""+row.id+"\");'>修改</a>&nbsp;<a href='javascript:deleteRow(\""+row.id+"\");'>删除</a>";
	    return str;
	}


	function editRow(id){
		link="${contextPath}/sys/application/edit?id="+id;
		jQuery.layer({
			type: 2,
			maxmin: true,
			shadeClose: true,
			title: "编辑记录",
			closeBtn: [0, true],
			shade: [0.8, '#000'],
			border: [10, 0.3, '#000'],
			offset: ['20px',''],
			fadeIn: 100,
			area: ['980px', (jQuery(window).height() - 50) +'px'],
            iframe: {src: link}
		});
	}


	function resize(){
		jQuery('#mydatagrid').datagrid('resize', {
			width:800,
			height:400
		});
	}

	function deleteRow(id){
		if(confirm("删除数据不能恢复，确定删除吗？")){
			jQuery.ajax({
				   type: "POST",
				   url: '${contextPath}/sys/application/deleteById?id='+id,
				   dataType:  'json',
				   error: function(data){
					   alert('服务器处理错误！');
				   },
				   success: function(data){
					   if(data != null && data.message != null){
						   alert(data.message);
					   } else {
						   alert('操作成功完成！');
					   }
					   //jQuery('#mydatagrid').datagrid('reload');
					   if(data.statusCode == 200){
						   window.location.reload();
					   }
				   }
			 });
		}
	}

	function editSelected(){
	    var rows = jQuery('#mydatagrid').datagrid('getSelections');
	    if(rows == null || rows.length !=1){
		  alert("请选择其中一条记录。");
		  return;
	    }
	    var selected = jQuery('#mydatagrid').datagrid('getSelected');
	    if (selected ){
		  var link = '${contextPath}/sys/application/edit?id='+selected.id+'&category=${category}&fromUrl=${fromUrl}';
		  //art.dialog.open(link, { height: 420, width: 680, title: "修改记录", lock: true, scrollbars:"no" }, false);
		  //location.href=link;
		  editRow(selected.id);
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
		    location.href='${contextPath}/sys/application/edit?id='+selected.id;
		}
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

	function searchData(){
        var params = jQuery("#searchForm").formSerialize();
        jQuery.ajax({
                    type: "POST",
                    url: '${contextPath}/sys/application/json',
                    dataType:  'json',
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
	

 
    function sortProject(){
	  var nodeId = jQuery("#nodeId").val();
	  var link = "${contextPath}/sys/application/showSort?parentId="+nodeId;
	  var width=680;
	  var height=430;
	  var scroll="yes";
	  jQuery.layer({
					type: 2,
					maxmin: true,
					shadeClose: true,
					title: "节点排序",
					closeBtn: [0, true],
					shade: [0.8, '#000'],
					border: [10, 0.3, '#000'],
					offset: ['20px',''],
					fadeIn: 100,
					area: ['650px', (jQuery(window).height() - 50) +'px'],
					iframe: {src: link}
				});
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

	function searchXY(namePinyinLike){
        var link = "${contextPath}/sys/application/json?namePinyinLike="+namePinyinLike;
		loadGridData(link);
	}
	
</script>
</head>
<body>
<input type="hidden" id="nodeId" name="nodeId" value="${nodeId}" >
<div class="easyui-layout" data-options="fit:true">  
    <div data-options="region:'west',split:true" style="width:220px;">
	  <div class="easyui-layout" data-options="fit:true">  
           
			 <div data-options="region:'center',border:false">
			    <ul id="myTree" class="ztree"></ul>  
			 </div> 
			 
        </div>  
	</div> 
   <div data-options="region:'center'"> 
	<div class="easyui-layout" data-options="fit:true">  
	  <div data-options="region:'center',border:false" class="toolbar-backgroud" style="height:68px">
	    <div style="margin:4px;"> 
		  <table width="100%" align="left">
			<tbody>
			  <tr>
				<td align="left">
					<img src="${contextPath}/static/images/window.png">
					&nbsp;<span class="x_content_title">应用列表</span>
					<a href="#" class="easyui-linkbutton" data-options="plain:true, iconCls:'icon-add'" 
					   onclick="javascript:addNew();">新增</a>  
					<a href="#" class="easyui-linkbutton" data-options="plain:true, iconCls:'icon-edit'"
					   onclick="javascript:editSelected();">修改</a> 
					<a href="#" class="easyui-linkbutton" data-options="plain:true, iconCls:'icon-sort'"
					   onclick="javascript:sortProject();">同级排序</a>
					<a href="#" class="easyui-linkbutton" data-options="plain:true, iconCls:'icon-reload'"
					   onclick="javascript:reloadGrid();">全部</a> 
                </td>
			  </tr>
			  <tr>
				<td>
					&nbsp;&nbsp;&nbsp;&nbsp;
					<#list charList as item>
					&nbsp;<span class="x_char_name" onclick="javascript:searchXY('${item}');">${item}</span>&nbsp;
					</#list>
				</td>
			  </tr>
			</tbody>
		  </table>
		</div> 
		<table id="mydatagrid"></table>
	  </div>  
	</div>
  </div>
</div>
</body>
</html>
