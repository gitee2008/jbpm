<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>查询项</title>
<#include "/inc/init_easyui_import.ftl"/>
<script type="text/javascript">
    var contextPath="${request.contextPath}";

    function addElement() {
        var list = document.iForm.noselected;
        for (i = 0; i < list.length; i++) {
            if (list.options[i].selected) {
                var value = list.options[i].value;
                var text = list.options[i].text;
                addToList(value, text);
				list.remove(i);
				i=i-1;
            }
        }
    }
	
    function addToList(value, text) {
        var list = document.iForm.selected;
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

    function removeElement() {
        var list = document.iForm.selected;
		var slist = document.iForm.noselected;
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
		var len = document.iForm.selected.length;
		for (var i=0; i < len; i++) {
		  result = result + "," + document.iForm.selected.options[i].value;
		}
		document.iForm.dataHandlerChains.value=result;
		var params = jQuery("#iForm").formSerialize();
		jQuery.ajax({
				   type: "POST",
				   url: '${request.contextPath}/matrix/exportItem/save',
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


</script>
</head>

<body>
<div style="margin:0;"></div>  

<div class="easyui-layout" data-options="fit:true">  
  <div data-options="region:'north',split:true,border:true" style="height:40px"> 
    <div class="toolbar-backgroud"> 
	<img src="${request.contextPath}/static/images/window.png">&nbsp;<span class="x_content_title">编辑查询项</span>
	<a href="#" class="easyui-linkbutton" data-options="plain:true, iconCls:'icon-save'" 
	   onclick="javascript:saveData();" >保存</a> 
    </div> 
  </div>

  <div data-options="region:'center',border:false,cache:true">
  <form id="iForm" name="iForm" method="post">
  <input type="hidden" id="id" name="id" value="${exportItem.id}"/>
  <input type="hidden" id="expId" name="expId" value="${expId}"/>
  <input type="hidden" id="dataHandlerChains" name="dataHandlerChains" value="${exportItem.dataHandlerChains}"/>
  <table class="easyui-form" style="width:868px;" align="center">
    <tbody>
	<tr>
		<td width="15%" align="left">名称</td>
		<td align="left">
            <input id="name" name="name" type="text" 
			       class="easyui-validatebox x-text" style="width:525px;" 
				   value="${exportItem.name}"/>
		</td>
	</tr>
	<tr>
		<td width="15%" align="left">标题</td>
		<td align="left">
            <input id="title" name="title" type="text" 
			       class="easyui-validatebox x-text" style="width:525px;" 
				   value="${exportItem.title}"/>
		</td>
	</tr>

	<tr>
		<td width="15%" align="left">XML导出结果</td>
		<td align="left">
		    <select id="xmlExpId" name="xmlExpId">
			    <option value="">----请选择----</option>
				<#list xmlExportList as item>
				<option value="${item.id}">${item.title}[${item.name}]</option>     
				</#list> 
            </select>
            <script type="text/javascript">
                document.getElementById("xmlExpId").value="${exportItem.xmlExpId}";
            </script>
			&nbsp;（提示：XML导出结果将转成json格式对象。）
		</td>
	</tr>

	<tr>
		<td width="90" align="left">SQL循环条件语句</td>
		<td align="left">
		    <textarea id="recursionSql" name="recursionSql" rows="4" cols="46" class="x-textarea"
			     style="font: 13px Consolas,Courier New,Arial; width:525px;height:90px;">${exportItem.recursionSql}</textarea>
		   <div style="margin-top:5px;">
		     （提示：SQL循环条件的结果当作下面取数语句的输入条件。）
	       </div>
		</td>
	</tr>
	<tr>
		<td width="90" align="left">循环列</td>
		<td align="left">
            <input id="recursionColumns" name="recursionColumns" type="text" 
			       class="easyui-validatebox x-text" style="width:125px;"  
				   value="${exportItem.recursionColumns}"/>
			&nbsp;（提示：循环列,多个列之间用半角的逗号隔开。）
		</td>
	</tr>
<tr>
		<td width="90" align="left">主键列</td>
		<td align="left">
            <input id="primaryKey" name="primaryKey" type="text" 
			       class="easyui-validatebox x-text" style="width:125px;"  
				   value="${exportItem.primaryKey}"/>
			&nbsp;（提示：主键,可以是某个列别名,也可以是用半角逗号隔开的多个列。）
		</td>
	</tr>
	<tr>
		<td width="90" align="left">SQL取数语句</td>
		<td align="left">
		    <textarea id="sql" name="sql" rows="6" cols="46" class="x-textarea" 
			          style="font: 13px Consolas,Courier New,Arial; width:525px;height:320px;" >${exportItem.sql}</textarea>
		   <div style="margin-top:5px;">
		     （提示：可以使用union语句组合结果。）
			<br>
			<span>
			 （可以使用动态参数,也可以使用循环语句的输出变量当输入,例如: column1 = <%="#"%>{param1}。）
			</span>
			<br>
			<span>
			 （today_start内置变量代表当天的开始时间, today_end内置变量代表当天的结束时间。）
			</span>
			<br>
			<span>
			 （yesterday_start内置变量代表昨天的开始时间, yesterday_end内置变量代表昨天的结束时间。）
			</span>
			<br>
			<span>
			 （today_curr_hour_start内置变量代表当前小时的开始时间, today_curr_hour_end内置变量代表当前小时的结束时间。）
			</span>
			<br>
			<span>
			 （today_previous_hour_start内置变量代表前一小时的开始时间, today_previous_hour_end内置变量代表前一小时的结束时间。）
			</span>
			<br>
			<span>
			 （the_end_of_last_month内置变量代表上月最后一天的时间。）
			</span>
			<br>
			<span>
			 （如果图片附件是二进制文件流，as别名必须命名为_bytes_才会默认处理图片。）
			</span>
	      </div>
		</td>
	</tr>
    <tr>
		<td width="15%" align="left" valign="middle">数据处理器</td>
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
					<td height="26" align="left">
					 <div>
					  <select class="list select" id="noselected" name="noselected" 
						style="width: 260px; height: 180px;" multiple="multiple" size="12"
						ondblclick="addElement()">${bufferx}
					  </select>
					 </div>
					</td>
					<td width="95" align="left">
					<div><input name="add" value="添加->"
						onclick="addElement()" class="btn btnGray" type="button"> <br>
					<br>
					<input name="remove" value="<- 删除" onclick="removeElement()"
						class="btn btnGray" type="button"></div>
					</td>
					<td height="26" align="left">
					 <div>
					  <select id="selected" name="selected" class="list select"
						style="width: 260px; height: 180px;" multiple="multiple" size="12"
						ondblclick="removeElement()">${buffery}
					  </select>
					 </div>
					</td>
				</tr>
			</tbody>
		  </table>
		</td>
    </tr>
    <tr>
		<td width="20%" align="left">分页条数</td>
		<td align="left">
			<input id="pageSize" name="pageSize" type="text" 
			       class="easyui-numberbox" style="width:65px; text-align: right" 
				   increment="10" precision="0" maxLength="2"
				   value="${exportItem.pageSize}"/>
			<span style="margin-top:5px;">
			&nbsp;（提示：如果指定了分页条数，将会把记录按分页条数再次分片，分片名称为rows_名称_页号。）
			</span>
		</td>
	</tr>
	<tr>
        <td width="15%" align="left">空数据填充标识</td>
		<td align="left">
		    <select id="genEmptyFlag" name="genEmptyFlag">
				<option value="Y">是</option>
			    <option value="N">否</option>
             </select>
             <script type="text/javascript">
                 document.getElementById("genEmptyFlag").value="${exportItem.genEmptyFlag}";
             </script>
			 &nbsp;（提示：数据为空是否用空数据填充也保持模板样式。）
		</td>
	</tr>
	<tr>
        <td width="15%" align="left">结果标识</td>
		<td align="left">
		    <select id="resultFlag" name="resultFlag">
				<option value="O">单一记录</option>
			    <option value="M">多条记录</option>
             </select>
             <script type="text/javascript">
                 document.getElementById("resultFlag").value="${exportItem.resultFlag}";
             </script>
			 &nbsp;（提示：如果是单一记录，即使结果集返回多条记录也只取第一条记录。）
		</td>
	</tr>
	<tr>
        <td width="15%" align="left">附件标识</td>
		<td align="left">
		    <select id="fileFlag" name="fileFlag">
				<option value="Y">是</option>
			    <option value="N">否</option>
             </select>
             <script type="text/javascript">
                 document.getElementById("fileFlag").value="${exportItem.fileFlag}";
             </script>
		</td>
	</tr>
	<tr>
		<td width="90" align="left">文件名列名</td>
		<td align="left">
            <input id="fileNameColumn" name="fileNameColumn" type="text" 
			       class="easyui-validatebox x-text" style="width:125px;"  
				   value="${exportItem.fileNameColumn}"/>
			&nbsp;（提示：文件名的列或列别名，如果有附件需要指定。）
		</td>
	</tr>
	<tr>
		<td width="90" align="left">路径列名</td>
		<td align="left">
            <input id="filePathColumn" name="filePathColumn" type="text" 
			       class="easyui-validatebox x-text" style="width:125px;"  
				   value="${exportItem.filePathColumn}"/>
			&nbsp;（提示：相对路径的列名或列别名。）
		</td>
	</tr>
	<tr>
		<td width="90" align="left">根路径</td>
		<td align="left">
            <input id="rootPath" name="rootPath" type="text" 
			       class="easyui-validatebox x-text" style="width:525px;"  
				   value="${exportItem.rootPath}"/>
			<br>（提示：存储文件服务器的根路径,<script>document.write("$");</script>{ROOT_PATH}代表应用根路径。）
		</td>
	</tr>
	<tr>
        <td width="15%" align="left">图片变长区</td>
		<td align="left">
		    <select id="variantFlag" name="variantFlag">
				<option value="Y">是</option>
			    <option value="N">否</option>
             </select>
             <script type="text/javascript">
                 document.getElementById("variantFlag").value="${exportItem.variantFlag}";
             </script>
			 &nbsp;（提示：是否为图片变长区，如果是需要特殊处理。）
		</td>
	</tr>
	<tr>
        <td width="15%" align="left">合并图片</td>
		<td align="left">
		    <select id="imageMergeFlag" name="imageMergeFlag">
				<option value="Y">是</option>
			    <option value="N">否</option>
             </select>
             <script type="text/javascript">
                 document.getElementById("imageMergeFlag").value="${exportItem.imageMergeFlag}";
             </script>
			 &nbsp;（提示：是否将多张图片合并成一张。）
		</td>
	</tr>
	<tr>
        <td width="15%" align="left">合并方向</td>
		<td align="left">
		    <select id="imageMergeDirection" name="imageMergeDirection">
				<option value="H">横向</option>
			    <option value="V">竖向</option>
             </select>
             <script type="text/javascript">
                 document.getElementById("imageMergeDirection").value="${exportItem.imageMergeDirection}";
             </script>
			 &nbsp;（提示：横向或竖向合并图片。）
		</td>
	</tr>
	<tr>
        <td width="15%" align="left">合并后图片类型</td>
		<td align="left">
		    <select id="imageMergeTargetType" name="imageMergeTargetType">
				<option value="png">png</option>
			    <option value="gif">gif</option>
				<option value="jpg">jpg</option>
            </select>
            <script type="text/javascript">
                document.getElementById("imageMergeTargetType").value="${exportItem.imageMergeTargetType}";
            </script>
			&nbsp;（提示：横向或竖向合并图片后输出的目标图片类型。）
		</td>
	</tr>
	<tr>
		<td width="20%" align="left">单张图片宽度</td>
		<td align="left">
			<input id="imageWidth" name="imageWidth" type="text" 
			       class="easyui-numberbox" style="width:65px; text-align: right" 
				   increment="10" precision="0" maxLength="4"
				   value="${exportItem.imageWidth}"/>
			<span style="margin-top:5px;">
			&nbsp;（提示：可以对单张图片预处理，需要同时对其中的图片设定宽度和高度。）
			</span>
		</td>
	</tr>
	<tr>
		<td width="20%" align="left">单张图片高度</td>
		<td align="left">
			<input id="imageHeight" name="imageHeight" type="text" 
			       class="easyui-numberbox" style="width:65px; text-align: right" 
				   increment="10" precision="0" maxLength="4"
				   value="${exportItem.imageHeight}"/>
			<span style="margin-top:5px;">
			&nbsp;（提示：可以对单张图片预处理，需要同时对其中的图片设定宽度和高度。）
			</span>
		</td>
	</tr>
	<tr>
		<td width="20%" align="left">缩放图片大小</td>
		<td align="left">
			<input id="imageScaleSize" name="imageScaleSize" type="text" 
			       class="easyui-numberbox" style="width:65px; text-align: right" 
				   increment="1" precision="2" maxLength="4"
				   value="${exportItem.imageScaleSize}"/>
			<span style="margin-top:5px;">
			&nbsp;（提示： 缩放图片的大小，超过这个大小的才缩放，默认单位为MB。）
			</span>
		</td>
	</tr>
	<tr>
		<td width="20%" align="left">单张图片缩放比例</td>
		<td align="left">
			<input id="imageScale" name="imageScale" type="text" 
			       class="easyui-numberbox" style="width:65px; text-align: right" 
				   increment="0.1" precision="2" maxLength="4"
				   value="${exportItem.imageScale}"/>
			<span style="margin-top:5px;">
			&nbsp;（提示：可以对单张图片按指定的比例缩放，取值只能小于1.0。）
			</span>
		</td>
	</tr>
	<tr>
        <td width="15%" align="left">上下文变量标识</td>
		<td align="left">
		    <select id="contextVarFlag" name="contextVarFlag">
				<option value="Y">是</option>
			    <option value="N">否</option>
             </select>
             <script type="text/javascript">
                 document.getElementById("contextVarFlag").value="${exportItem.contextVarFlag}";
             </script>
			 &nbsp;（提示：是否需要将单条记录的字段值用做上下文变量传递到后续操作中。）
		</td>
	</tr>
	<tr>
        <td width="15%" align="left">小计汇总标识</td>
		<td align="left">
		    <select id="subTotalFlag" name="subTotalFlag">
				<option value="Y">是</option>
			    <option value="N">否</option>
             </select>
             <script type="text/javascript">
                 document.getElementById("subTotalFlag").value="${exportItem.subTotalFlag}";
             </script>
			 &nbsp;（提示：是否需要小计汇总标识，如果是需要对数据进行小计汇总处理。）
		</td>
	</tr>
	<tr>
		<td width="20%" align="left">小计汇总列</td>
		<td align="left">
			<input id="subTotalColumn" name="subTotalColumn" type="text" 
			       class="easyui-textbox" style="width:165px; text-align: left" 
				   maxLength="40" value="${exportItem.subTotalColumn}"/>
			<span style="margin-top:5px;">
			&nbsp;（提示：可以通过该列做小计汇总。）
			</span>
		</td>
	</tr>
	<tr>
		<td width="20%" align="left">自动换行列</td>
		<td align="left">
			<input id="lineBreakColumn" name="lineBreakColumn" type="text" 
			       class="easyui-textbox" style="width:165px; text-align: left" 
				   maxLength="40" value="${exportItem.lineBreakColumn}"/>
			<span style="margin-top:5px;">
			&nbsp;（提示：可以通过该列做自动换行，实现自动行高功能。）
			</span>
		</td>
	</tr>
	<tr>
		<td width="20%" align="left">自动换行字符数</td>
		<td align="left">
			<input id="charNumPerRow" name="charNumPerRow" type="text" 
			       class="easyui-numberbox" style="width:45px; text-align: right" 
				   increment="1" precision="0" maxLength="3" value="${exportItem.charNumPerRow}"/>
			<span style="margin-top:5px;">
			&nbsp;（提示：自动换行后每行字符数。）
			</span>
		</td>
	</tr>
	<tr>
		<td width="20%" align="left">默认行高</td>
		<td align="left">
			<input id="lineHeight" name="lineHeight" type="text" 
			       class="easyui-numberbox" style="width:45px; text-align: right" 
				   increment="1" precision="0" maxLength="3" value="${exportItem.lineHeight}"/>
			<span style="margin-top:5px;">
			&nbsp;（提示：Excel循环记录区每行默认行高。）
			</span>
		</td>
	</tr>
	<tr>
		<td width="90" align="left">预处理程序</td>
		<td align="left">
		    <textarea id="preprocessors" name="preprocessors" rows="6" cols="46" class="x-textarea" 
			          style="font: 13px Consolas,Courier New,Arial; width:525px; height:60px;" >${exportItem.preprocessors}</textarea>
			<div style="margin-top:5px;">
		     （提示：实现了com.glaf.matrix.export.preprocessor.IDataPreprocessor接口程序的类，多个类之间用半角的逗号,隔开。）
			 <br>（提示：生成结果以变量名_var的方式放到变量表中。）
			</div>
        </td>
	</tr>
	<tr>
		<td width="90" align="left">变量模板</td>
		<td align="left">
		    <textarea id="varTemplate" name="varTemplate" rows="6" cols="46" class="x-textarea" 
			          style="font: 13px Consolas,Courier New,Arial; width:525px; height:120px;" >${exportItem.varTemplate}</textarea>
			<div style="margin-top:5px;">
		     （提示：使用Freemarker模板引擎，使用需遵循Freemarker的语法）
			 <br>（提示：生成结果以变量名_var的方式放到变量表中。）
			</div>
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
                 document.getElementById("sortNo").value="${exportItem.sortNo}";
             </script>
			 &nbsp;（提示：顺序小的先执行。）
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
                 document.getElementById("locked").value="${exportItem.locked}";
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