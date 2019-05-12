<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>${chart.subject}</title>
</head>
<body style=" margin: 0">
 <div id="main" style="min-width: 120px; max-width: ${chart.chartWidth*1.5}px; width: ${chart.chartWidth}px; height: ${chart.chartHeight}px; margin: 0 auto"></div>
 <#include "/inc/init_echarts_import.ftl"/>
 <script type="text/javascript">

    var myChart = echarts.init(document.getElementById('main'), 'shine'); 

    var option = {
		    title: {
			    text: '${chart.chartTitle}'
				<#if chart.chartSubTitle?exists>
			    ,subtext: '${chart.chartSubTitle}'
			    </#if>
		    },
            tooltip: {
				trigger: 'item',
                formatter: "{a} <br/>{b} : {c} ({d}%)",
                show: true
            },
             
            series : [{
				        name: '${chart.chartTitle}',
				        type: 'pie',
						radius : '65%',
                        center: ['50%', '60%'],
						label: {
							normal: {
							    show: true,
							    formatter: '{b}: {c}'
							}
						},
                        data: ${pie_json},
						itemStyle: {
							emphasis: {
								shadowBlur: 10,
								shadowOffsetX: 0,
								shadowColor: 'rgba(0, 0, 0, 0.5)'
							}
						}
                   }]
        };
  
    // 为echarts对象加载数据 
    myChart.setOption(option); 

 </script>
</body>
</html>