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
            jQuery("#chart").kendoChart({
                title: {
                    position: "bottom",
                    text: "${chart.chartTitle}"
                },
                legend: {
                    visible: false
                },
                chartArea: {
                    background: ""
                },
                seriesDefaults: {
                    labels: {
                        visible: true,
                        background: "transparent",
                        template: "#= category #: \n #= value# %"
                    }
                },
                series: [{
                    type: "pie",
                    startAngle: 50,
                    data: ${jsonArray}
                }],
                tooltip: {
                    visible: true,
                    format: "{0}%"
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