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
                    text: "${chart.chartTitle}"
                },
                legend: {
                    position: "bottom"
                },
                seriesDefaults: {
                    type: "bar"
                },
                series:  ${seriesDataJson},
                valueAxis: {
                    labels: {
                        format: "{0}"
                    },
                    line: {
                        visible: false
                    },
                    axisCrossingValue: 0
                },
                categoryAxis: {
                    categories: ${categories_scripts},
                    line: {
                        visible: false
                    },
                    labels: {
                        padding: {top: 15}
                    }
                },
                tooltip: {
                    visible: true,
                    format: "{0}",
                    template: "#= series.name #: #= value #"
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