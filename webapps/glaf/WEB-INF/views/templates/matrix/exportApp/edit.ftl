<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>数据导出</title>
<#include "/inc/init_easyui_import.ftl"/>
<script type="text/javascript">
    var contextPath="${request.contextPath}";

	function addElement2() {
        var list = document.iForm.noselected2;
        for (i = 0; i < list.length; i++) {
            if (list.options[i].selected) {
                var value = list.options[i].value;
                var text = list.options[i].text;
                addToList2(value, text);
				list.remove(i);
				i=i-1;
            }
        }
    }

	function addToList2(value, text) {
        var list = document.iForm.selected2;
        if (list.length > 0) {
            for (k = 0; k < list.length; k++) {
                if (list.options[k].value == value) {
                    return;
                }
            }
        }

        var len = list.options.length;
        list.length = len + 1;
        list.options[len].value = value;
        list.options[len].text = text;
    }

	function removeElement2() {
        var list = document.iForm.selected2;
		var slist = document.iForm.noselected2;
        if (list.length == 0 || list.selectedIndex < 0 || list.selectedIndex >= list.options.length){
            return;
        }
        for (i = 0; i < list.length; i++) {
            if (list.options[i].selected) {
			    var value = list.options[i].value;
                var text = list.options[i].text;
                list.options[i] = null;
                i--;
				var len = slist.options.length;
				slist.length = len+1;
                slist.options[len].value = value;
                slist.options[len].text = text;				
            }
        }
    }


	function saveData(){
		var result = "";
		var len = document.iForm.selected2.length;
		for (var i=0; i < len; i++) {
		  result = result + "," + document.iForm.selected2.options[i].value;
		}
		document.iForm.excelProcessChains.value=result;
		var params = jQuery("#iForm").formSerialize();
		jQuery.ajax({
				   type: "POST",
				   url: '${request.contextPath}/matrix/exportApp/save',
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
		var result = "";
		var len = document.iForm.selected2.length;
		for (var i=0; i < len; i++) {
		  result = result + "," + document.iForm.selected2.options[i].value;
		}
		document.iForm.excelProcessChains.value=result;
		var params = jQuery("#iForm").formSerialize();
		jQuery.ajax({
				   type: "POST",
				   url: '${request.contextPath}/matrix/exportApp/saveAs',
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

    function viewTemplate(){
		var myDate = new Date();
        var selected = jQuery("#templateId").val();
        var link = '${request.contextPath}/sys/template/edit?templateId='+selected+"&ts="+myDate.getTime();
		window.open(link);
	}


	function openTemplate(){
		var myDate = new Date();
        var selected = jQuery("#templateId").val();
        var link = '${request.contextPath}/dataFile/download?fileId='+selected+"&ts="+myDate.getTime();
		window.open(link);
	}

</script>
</head>

<body>
<div style="margin:0;"></div>  

<div class="easyui-layout" data-options="fit:true">  
  <div data-options="region:'north',split:true,border:true" style="height:40px"> 
    <div class="toolbar-backgroud"> 
	<img src="${request.contextPath}/static/images/window.png">&nbsp;<span class="x_content_title">编辑数据导出</span>
	<a href="#" class="easyui-linkbutton" data-options="plain:true, iconCls:'icon-save'" 
	   onclick="javascript:saveData();" >保存</a> 
	<a href="#" class="easyui-linkbutton" data-options="plain:true, iconCls:'icon-save'" 
	   onclick="javascript:saveAsData();" >另存</a> 
    </div> 
  </div>

  <div data-options="region:'center',border:false,cache:true">
  <form id="iForm" name="iForm" method="post">
  <input type="hidden" id="id" name="id" value="${exportApp.id}"/>
  <input type="hidden" id="expId" name="expId" value="${exportApp.id}"/>
  <input type="hidden" id="excelProcessChains" name="excelProcessChains" value="${exportApp.excelProcessChains}"/>
  <table class="easyui-form" style="width:880px;" align="center">
    <tbody>
	<tr>
		<td width="15%" align="left">标题</td>
		<td align="left">
            <input id="title" name="title" type="text" 
			       class="easyui-validatebox x-text" style="width:425px;" 
				   value="${exportApp.title}"/>
		</td>
	</tr>
	<tr>
		<td width="15%" align="left">数据库编号</td>
		<td align="left">
			<select id="srcDatabaseId" name="srcDatabaseId">
			    <option value="">----请选择----</option>
				<#list databases as database>
				<option value="${database.id}">${database.title}[${database.dbname}]</option>
				</#list>
            </select> 
            <script type="text/javascript">
                 document.getElementById("srcDatabaseId").value="${exportApp.srcDatabaseId}";
            </script>
		</td>
	</tr>
	<tr>
		<td width="15%" align="left">模板编号</td>
		<td align="left">
			<select id="templateId" name="templateId">
			    <option value="">----请选择----</option>
				<#list  templates as template>
				<option value="${template.templateId}">${template.title}</option>
				</#list>
            </select> 
            <script type="text/javascript">
                document.getElementById("templateId").value="${exportApp.templateId}";
            </script>
			&nbsp;
			<a href="#" onclick="javascript:viewTemplate();">
		    <img src="${request.contextPath}/static/images/view.gif" border="0"
				 title="查看模板信息">
			</a>
			&nbsp;
			<a href="#" onclick="javascript:openTemplate();">
		    <img src="${request.contextPath}/static/images/task.gif" border="0"
				 title="下载模板信息">
			</a>
		</td>
	</tr>
	<tr>
		<td width="20%" align="left">页面高度</td>
		<td align="left">
			<input id="pageHeight" name="pageHeight" type="text" 
			       class="easyui-numberbox" style="width:65px; text-align: right" 
				   increment="10" precision="0" maxLength="4"
				   value="${exportApp.pageHeight}"/>
			<span style="margin-top:5px;">
			&nbsp;（提示：页面高度，取值400~1000，一般页高650。）
			</span>
		</td>
	</tr>
	<tr>
		<td width="20%" align="left">每个Sheet最大页数</td>
		<td align="left">
			<input id="pageNumPerSheet" name="pageNumPerSheet" type="text" 
			       class="easyui-numberbox" style="width:65px; text-align: right" 
				   increment="10" precision="0" maxLength="3"
				   value="${exportApp.pageNumPerSheet}"/>
			<span style="margin-top:5px;">
			&nbsp;（提示：因Excel单元格样式总数限制64000，所以每个文件页数是64000除以每页单元格数，一般取值200左右。）
			</span>
		</td>
	</tr>
	<tr>
		<td width="15%" align="left">分页变量名称</td>
		<td align="left">
            <input id="pageVarName" name="pageVarName" type="text" 
			       class="easyui-validatebox x-text" style="width:425px;" 
				   value="${exportApp.pageVarName}"/>
			<div style="margin-top:5px;">
			  <span style=" margin-left:2px;">
			    （提示：分页循环变量的名称，与其中做分页查询项中定义的变量名称一致。）
			  </span>
	        </div>
		</td>
	</tr>
	<tr>
		<td width="15%" align="left">导出文件名称</td>
		<td align="left">
            <input id="exportFileExpr" name="exportFileExpr" type="text" 
			       class="easyui-validatebox x-text" style="width:425px;" 
				   value="${exportApp.exportFileExpr}"/>
			<div style="margin-top:5px;">
			  <span style="color:#0099ff; margin-left:2px;">
			    （提示：支持Java表达式，yyyyMMdd代表当前日期，yyyyMMddHHmmss代表当前时间。）
			  </span>
	        </div>
		</td>
	</tr>
	<tr>
		<td width="15%" align="left">是否生成历史文件</td>
		<td align="left">
		    <select id="historyFlag" name="historyFlag">
				<option value="Y">是</option>
			    <option value="N">否</option>
             </select>
             <script type="text/javascript">
                 document.getElementById("historyFlag").value="${exportApp.historyFlag}";
             </script>
		</td>
	</tr>
	<tr>
		<td width="15%" align="left">是否生成多个文件</td>
		<td align="left">
		    <select id="mulitiFlag" name="mulitiFlag">
				<option value="Y">是</option>
			    <option value="N">否</option>
             </select>
             <script type="text/javascript">
                 document.getElementById("mulitiFlag").value="${exportApp.mulitiFlag}";
             </script>&nbsp;
			  <span style="color:#000033; margin-left:2px;">
			    （提示：生成Excel时，可以根据每个Sheet进行划分，如果超出一个Excel，就按多个Excel导出。）
			  </span>
		</td>
	</tr>
	<tr>
		<td width="15%" align="left">是否定时调度</td>
		<td align="left">
		    <select id="shedulerFlag" name="shedulerFlag">
				<option value="Y">是</option>
			    <option value="N">否</option>
             </select>
             <script type="text/javascript">
                 document.getElementById("shedulerFlag").value="${exportApp.shedulerFlag}";
             </script>
		</td>
	</tr>
	<tr>
		<td width="15%" align="left">授权角色</td>
		<td align="left">
		    <textarea id="allowRoles" name="allowRoles" rows="6" cols="46" class="x-textarea" style="width:625px;height:90px;" >${exportApp.allowRoles}</textarea>
			<div style="margin-top:5px;">
			  <span style="color:red; margin-left:2px;">
			    （提示：如允许多个角色访问，角色代码之前用","隔开。）
			  </span>
	        </div>
		</td>
	</tr>

	<tr>
		<td width="15%" align="left" valign="middle">Excel处理器</td>
		<td align="left">
          <table class="table-border" align="center" cellpadding="0" cellspacing="0" width="100%">
			<tbody>		 
				<tr>
					<td>
					  <div align="left">可选处理器</div>
					</td>
					<td width="95" align="left"></td>
					<td>
					  <div align="left">已选处理器</div>
					</td>
				</tr>
				<tr>
					<td height="26" align="left" >
					 <div>
					  <select class="list select" id="noselected2" name="noselected2" 
						style="width: 260px; height: 180px;" multiple="multiple" size="12"
						ondblclick="addElement2()">${bufferx2}
					  </select>
					 </div>
					</td>
					<td width="95" align="left">
					<div><input name="add" value="添加->"
						onclick="addElement2()" class="btn btnGray" type="button"> <br>
					<br>
					<input name="remove" value="<- 删除" onclick="removeElement2()"
						class="btn btnGray" type="button"></div>
					</td>
					<td height="26" align="left">
					 <div>
					  <select id="selected2" name="selected2" class="list select"
						style="width: 260px; height: 180px;" multiple="multiple" size="12"
						ondblclick="removeElement2()">${buffery2}
					  </select>
					 </div>
					</td>
				</tr>
			</tbody>
		  </table>
		</td>
    </tr>
  
	<tr>
		<td width="15%" align="left">执行顺序</td>
		<td align="left">
		    <select id="sortNo" name="sortNo">
			    <option value="0">----请选择----</option>
				<option value="1">1</option>
				<option value="2">2</option>
				<option value="3">3</option>
				<option value="4">4</option>
				<option value="5">5</option>
				<option value="6">6</option>
				<option value="7">7</option>
			    <option value="8">8</option>
				<option value="9">9</option>
				<option value="10">10</option>
             </select>
             <script type="text/javascript">
                 document.getElementById("sortNo").value="${exportApp.sortNo}";
             </script>
			 &nbsp;（提示：顺序小的先执行。）
		</td>
	</tr>
	<tr>
		<td width="15%" align="left">是否有效</td>
		<td align="left">
		    <select id="active" name="active">
				<option value="Y">是</option>
			    <option value="N">否</option>
             </select>
             <script type="text/javascript">
                 document.getElementById("active").value="${exportApp.active}";
             </script>
		</td>
	</tr>
	<tr><td><br><br><br><br></td></tr>
    </tbody>
  </table>
 </form>
</div>
</div>
</body>
</html>