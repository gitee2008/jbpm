<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>${chart.subject}</title>
<#include "/inc/init_kendoui_import.ftl"/> 
<script type="text/javascript">

    function createChart() {
            $("#chart").kendoChart({
                title: {
                    text: "${chart.chartTitle}",
                    margin: {
                        top: 10,
                        bottom: 10,
                        left: -5
                    }
                },
				dataSource: {
                    transport: {
                        read: {
                            url: "${contextPath}/website/public/kendo/json?chartId=${chart.id}",
                            dataType: "json"
                        }
                    }
                },
                legend: {
                    visible: false
                },
                series: [{
                    type: "funnel",
                    dynamicSlope:true,
                    field: "value",
                    categoryField: "category",
                    dynamicHeight : false,
                    labels: {
                        color:"black",
                        visible: true,
                        background: "transparent",
                        template: "#= category #: #= value#",
                        align: "left"
                    }
                }],
                tooltip: {
                    visible: true,
                    template: "#= category # #= kendo.format('{0:p}',value/dataItem.parent()[0].value)#"
                }
            });
        }

    $(document).ready(createChart);
    $(document).bind("kendo:skinChange", createChart);
 </script>
</head>
<body>
 <div id="chart" style="min-width: 120px; max-width: ${chart.chartWidth*1.5}px; width: ${chart.chartWidth}px; height: ${chart.chartHeight}px; margin: 0 auto"></div>
</body>
</html>