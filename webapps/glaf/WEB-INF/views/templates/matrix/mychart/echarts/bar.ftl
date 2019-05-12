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
                show: true
            },
            legend: {
                data:${legendArrayJson}
            },
            xAxis : [
                {
                    type : 'category',
                    data : ${categories_scripts}
                }
            ],
            yAxis : [
                {
                    type : 'value'
                }
            ],
            series : [
		        {
				    label: {
							normal: {
								position: 'top',
							    show: true,
							    formatter: '{c}'
							}
			     	},
                    data: ${barSeriesDataArray},
				    type: 'bar'
                }]
        };
  
    // 为echarts对象加载数据 
    myChart.setOption(option); 

 </script>
</body>
</html>