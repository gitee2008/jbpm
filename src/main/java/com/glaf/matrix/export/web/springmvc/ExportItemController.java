package com.glaf.matrix.export.web.springmvc;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentMap;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.glaf.core.base.BaseItem;
import com.glaf.core.config.ViewProperties;
import com.glaf.core.security.LoginContext;
import com.glaf.core.util.Paging;
import com.glaf.core.util.ParamUtils;
import com.glaf.core.util.RequestUtils;
import com.glaf.core.util.ResponseUtils;
import com.glaf.core.util.StringTools;
import com.glaf.core.util.Tools;

import com.glaf.matrix.export.domain.ExportItem;
import com.glaf.matrix.export.domain.XmlExport;
import com.glaf.matrix.export.handler.DataXFactory;
import com.glaf.matrix.export.query.ExportItemQuery;
import com.glaf.matrix.export.query.XmlExportQuery;
import com.glaf.matrix.export.service.ExportItemService;
import com.glaf.matrix.export.service.XmlExportService;

/**
 * 
 * SpringMVC控制器
 *
 */

@Controller("/matrix/exportItem")
@RequestMapping("/matrix/exportItem")
public class ExportItemController {
	protected static final Log logger = LogFactory.getLog(ExportItemController.class);

	protected ExportItemService exportItemService;

	protected XmlExportService xmlExportService;

	public ExportItemController() {

	}

	@ResponseBody
	@RequestMapping("/delete")
	public byte[] delete(HttpServletRequest request, ModelMap modelMap) {
		LoginContext loginContext = RequestUtils.getLoginContext(request);
		String id = RequestUtils.getString(request, "id");
		String ids = request.getParameter("ids");
		if (StringUtils.isNotEmpty(ids)) {
			StringTokenizer token = new StringTokenizer(ids, ",");
			while (token.hasMoreTokens()) {
				String x = token.nextToken();
				if (StringUtils.isNotEmpty(x)) {
					ExportItem exportItem = exportItemService.getExportItem(x);
					if (exportItem != null && (StringUtils.equals(exportItem.getCreateBy(), loginContext.getActorId())
							|| loginContext.isSystemAdministrator())) {
						exportItemService.deleteById(exportItem.getId());
					}
				}
			}
			return ResponseUtils.responseResult(true);
		} else if (id != null) {
			ExportItem exportItem = exportItemService.getExportItem(id);
			if (exportItem != null && (StringUtils.equals(exportItem.getCreateBy(), loginContext.getActorId())
					|| loginContext.isSystemAdministrator())) {
				exportItemService.deleteById(exportItem.getId());
				return ResponseUtils.responseResult(true);
			}
		}
		return ResponseUtils.responseResult(false);
	}

	@RequestMapping("/edit")
	public ModelAndView edit(HttpServletRequest request, ModelMap modelMap) {
		RequestUtils.setRequestParameterToAttribute(request);

		ConcurrentMap<String, String> nameMap01 = DataXFactory.getNameMap();

		List<BaseItem> allDataItems01 = new ArrayList<BaseItem>();
		List<BaseItem> selectedDataItems01 = new ArrayList<BaseItem>();
		List<String> selecteds01 = new ArrayList<String>();

		Set<Entry<String, String>> entrySet = nameMap01.entrySet();
		for (Entry<String, String> entry : entrySet) {
			String key = entry.getKey();
			String value = entry.getValue();
			BaseItem item = new BaseItem();
			item.setName(key);
			item.setTitle(value);
			allDataItems01.add(item);
		}

		ExportItem exportItem = exportItemService.getExportItem(RequestUtils.getString(request, "id"));
		if (exportItem != null) {
			request.setAttribute("exportItem", exportItem);

			if (StringUtils.isNotEmpty(exportItem.getDataHandlerChains())) {
				List<String> chains = StringTools.split(exportItem.getDataHandlerChains());
				for (String name : chains) {
					BaseItem item = new BaseItem();
					item.setName(name);
					item.setTitle(nameMap01.get(name));
					// allDataItems01.remove(item);
					selectedDataItems01.add(item);
					selecteds01.add(name);
				}
			}

		}

		StringBuffer bufferx = new StringBuffer();
		StringBuffer buffery = new StringBuffer();

		for (int j = 0; j < allDataItems01.size(); j++) {
			BaseItem item = (BaseItem) allDataItems01.get(j);
			if (selecteds01.contains(item.getName())) {
				buffery.append("\n<option value=\"").append(item.getName()).append("\">").append(item.getTitle())
						.append(" [").append(item.getName()).append("]").append("</option>");
			} else {
				bufferx.append("\n<option value=\"").append(item.getName()).append("\">").append(item.getTitle())
						.append(" [").append(item.getName()).append("]").append("</option>");
			}
		}

		request.setAttribute("bufferx", bufferx.toString());
		request.setAttribute("buffery", buffery.toString());

		request.setAttribute("allDataItems01", allDataItems01);
		request.setAttribute("selectedDataItems01", selectedDataItems01);

		XmlExportQuery query = new XmlExportQuery();
		List<XmlExport> xmlExportList = xmlExportService.list(query);
		request.setAttribute("xmlExportList", xmlExportList);

		String view = request.getParameter("view");
		if (StringUtils.isNotEmpty(view)) {
			return new ModelAndView(view, modelMap);
		}

		String x_view = ViewProperties.getString("exportItem.edit");
		if (StringUtils.isNotEmpty(x_view)) {
			return new ModelAndView(x_view, modelMap);
		}

		return new ModelAndView("/matrix/exportItem/edit", modelMap);
	}

	@RequestMapping("/json")
	@ResponseBody
	public byte[] json(HttpServletRequest request) throws IOException {
		LoginContext loginContext = RequestUtils.getLoginContext(request);
		Map<String, Object> params = RequestUtils.getParameterMap(request);
		ExportItemQuery query = new ExportItemQuery();
		Tools.populate(query, params);
		query.deleteFlag(0);
		query.setActorId(loginContext.getActorId());
		query.setLoginContext(loginContext);
		/**
		 * 此处业务逻辑需自行调整
		 */
		if (!loginContext.isSystemAdministrator()) {
			String actorId = loginContext.getActorId();
			query.createBy(actorId);
		}

		int start = 0;
		int limit = 10;
		String orderName = null;
		String order = null;

		int pageNo = ParamUtils.getInt(params, "page");
		limit = ParamUtils.getInt(params, "rows");
		start = (pageNo - 1) * limit;
		orderName = ParamUtils.getString(params, "sortName");
		order = ParamUtils.getString(params, "sortOrder");

		if (start < 0) {
			start = 0;
		}

		if (limit <= 0) {
			limit = Paging.DEFAULT_PAGE_SIZE;
		}

		JSONObject result = new JSONObject();
		int total = exportItemService.getExportItemCountByQueryCriteria(query);
		if (total > 0) {
			result.put("total", total);
			result.put("totalCount", total);
			result.put("totalRecords", total);
			result.put("start", start);
			result.put("startIndex", start);
			result.put("limit", limit);
			result.put("pageSize", limit);

			if (StringUtils.isNotEmpty(orderName)) {
				query.setSortOrder(orderName);
				if (StringUtils.equals(order, "desc")) {
					query.setSortOrder(" desc ");
				}
			}

			List<ExportItem> list = exportItemService.getExportItemsByQueryCriteria(start, limit, query);

			if (list != null && !list.isEmpty()) {
				JSONArray rowsJSON = new JSONArray();

				result.put("rows", rowsJSON);

				for (ExportItem exportItem : list) {
					JSONObject rowJSON = exportItem.toJsonObject();
					rowJSON.put("id", exportItem.getId());
					rowJSON.put("itemId", exportItem.getId());
					rowJSON.put("startIndex", ++start);
					rowsJSON.add(rowJSON);
				}

			}
		} else {
			JSONArray rowsJSON = new JSONArray();
			result.put("rows", rowsJSON);
			result.put("total", total);
		}
		return result.toJSONString().getBytes("UTF-8");
	}

	@RequestMapping
	public ModelAndView list(HttpServletRequest request, ModelMap modelMap) {
		RequestUtils.setRequestParameterToAttribute(request);

		String view = request.getParameter("view");
		if (StringUtils.isNotEmpty(view)) {
			return new ModelAndView(view, modelMap);
		}

		return new ModelAndView("/matrix/exportItem/list", modelMap);
	}

	@RequestMapping("/query")
	public ModelAndView query(HttpServletRequest request, ModelMap modelMap) {
		RequestUtils.setRequestParameterToAttribute(request);
		String view = request.getParameter("view");
		if (StringUtils.isNotEmpty(view)) {
			return new ModelAndView(view, modelMap);
		}
		String x_view = ViewProperties.getString("exportItem.query");
		if (StringUtils.isNotEmpty(x_view)) {
			return new ModelAndView(x_view, modelMap);
		}
		return new ModelAndView("/matrix/exportItem/query", modelMap);
	}

	@ResponseBody
	@RequestMapping("/save")
	public byte[] save(HttpServletRequest request) {
		LoginContext loginContext = RequestUtils.getLoginContext(request);
		if (!loginContext.isSystemAdministrator()) {
			return ResponseUtils.responseJsonResult(false, "只有管理员才能操作");
		}
		String actorId = loginContext.getActorId();
		Map<String, Object> params = RequestUtils.getParameterMap(request);
		ExportItem exportItem = new ExportItem();
		try {
			Tools.populate(exportItem, params);
			exportItem.setExpId(RequestUtils.getString(request, "expId"));
			exportItem.setDeploymentId(request.getParameter("deploymentId"));
			exportItem.setTitle(request.getParameter("title"));
			exportItem.setDatasetId(request.getParameter("datasetId"));
			exportItem.setXmlExpId(request.getParameter("xmlExpId"));
			exportItem.setSql(request.getParameter("sql"));
			exportItem.setRecursionSql(request.getParameter("recursionSql"));
			exportItem.setRecursionColumns(request.getParameter("recursionColumns"));
			exportItem.setPrimaryKey(request.getParameter("primaryKey"));
			exportItem.setExpression(request.getParameter("expression"));
			exportItem.setFileFlag(request.getParameter("fileFlag"));
			exportItem.setFilePathColumn(request.getParameter("filePathColumn"));
			exportItem.setFileNameColumn(request.getParameter("fileNameColumn"));
			exportItem.setImageMergeFlag(request.getParameter("imageMergeFlag"));
			exportItem.setImageMergeDirection(request.getParameter("imageMergeDirection"));
			exportItem.setImageMergeTargetType(request.getParameter("imageMergeTargetType"));
			exportItem.setImageWidth(RequestUtils.getInt(request, "imageWidth"));
			exportItem.setImageHeight(RequestUtils.getInt(request, "imageHeight"));
			exportItem.setImageScale(RequestUtils.getDouble(request, "imageScale"));
			exportItem.setImageScaleSize(RequestUtils.getDouble(request, "imageScaleSize"));
			exportItem.setImageNumPerUnit(RequestUtils.getInt(request, "imageNumPerUnit"));
			exportItem.setRootPath(request.getParameter("rootPath"));
			exportItem.setLineBreakColumn(request.getParameter("lineBreakColumn"));
			exportItem.setLineHeight(RequestUtils.getInt(request, "lineHeight"));
			exportItem.setCharNumPerRow(RequestUtils.getInt(request, "charNumPerRow"));
			exportItem.setPageSize(RequestUtils.getInt(request, "pageSize"));
			exportItem.setContextVarFlag(request.getParameter("contextVarFlag"));
			exportItem.setGenEmptyFlag(request.getParameter("genEmptyFlag"));
			exportItem.setResultFlag(request.getParameter("resultFlag"));
			exportItem.setDataHandlerChains(request.getParameter("dataHandlerChains"));
			exportItem.setPreprocessors(request.getParameter("preprocessors"));
			exportItem.setSubTotalFlag(request.getParameter("subTotalFlag"));
			exportItem.setSubTotalColumn(request.getParameter("subTotalColumn"));
			exportItem.setVarTemplate(request.getParameter("varTemplate"));
			exportItem.setVariantFlag(request.getParameter("variantFlag"));
			exportItem.setSortNo(RequestUtils.getInt(request, "sortNo"));
			exportItem.setLocked(RequestUtils.getInt(request, "locked"));
			exportItem.setCreateBy(actorId);
			this.exportItemService.save(exportItem);

			return ResponseUtils.responseJsonResult(true);
		} catch (Exception ex) {
			ex.printStackTrace();
			logger.error(ex);
		}
		return ResponseUtils.responseJsonResult(false);
	}

	@javax.annotation.Resource(name = "com.glaf.matrix.export.service.exportItemService")
	public void setExportItemService(ExportItemService exportItemService) {
		this.exportItemService = exportItemService;
	}

	@javax.annotation.Resource(name = "com.glaf.matrix.export.service.xmlExportService")
	public void setXmlExportService(XmlExportService xmlExportService) {
		this.xmlExportService = xmlExportService;
	}

}
