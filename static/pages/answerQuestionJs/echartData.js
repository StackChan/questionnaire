var chartResult;
var dataResult;

function success(res) {
    chartResult = res
    return;
}

function success1(res) {
    dataResult = res
    return;
}

$(function () {

    var url1 = '/project/queryQuestionnaireById'
    var url2 = '/project/queryAnswerChart'
    var id = getCookie("questionId");
    var da = {'questionId':id};
    commonAjaxPost(true, url1, da, success);
    commonAjaxPost(true, url2, da, success1);

    questionList = JSON.parse(chartResult.data.questionList)
    var sum = 0;
    if (questionList!=null){
        for (var i = 0; i < questionList.length; i++){
            var questionType = questionList[i].questionType;
            switch (questionType){
                case '0':
                case '1':
                    var main = "main"+sum;
                    sum++;
                    var myChart = echarts.init(document.getElementById(main));
                    var option = {
                        title:{
                            text:"Echarts ????"
                        },
                        legend:{

                        },
                        xAxis:{
                            data:["??","???","???","??","???","??"]
                        },
                        yAxis:{},
                        series:[
                            {
                                name:'??',
                                type:"bar",
                                data:[5,20,36,20,10,20]
                            }
                        ]
                    }
                    myChart.setOption(option);
                    break;
                case '2':
                    break;
                case '3':
                    break;
                case '4':
                    break;
            }
        }
    }

    // var myChart = echarts.init(document.getElementById("main"));
    // var option = {
    //     title:{
    //         text:"Echarts ????"
    //     },
    //     legend:{
    //
    //     },
    //     xAxis:{
    //         data:["??","???","???","??","???","??"]
    //     },
    //     yAxis:{},
    //     series:[
    //         {
    //             name:'??',
    //             type:"bar",
    //             data:[5,20,36,20,10,20]
    //         }
    //     ]
    // }
    // myChart.setOption(option);
    //
    //
    //
    // var myChart1 = echarts.init(document.getElementById("main1"));
    // var option1 = {
    //     title:{
    //         text:"Echarts ????"
    //     },
    //     legend:{
    //
    //     },
    //     xAxis:{
    //         data:["??","???","???","??","???","??"]
    //     },
    //     yAxis:{},
    //     series:[
    //         {
    //             name:'??',
    //             type:"bar",
    //             data:[5,20,36,20,10,20]
    //         }
    //     ]
    // }
    // myChart1.setOption(option1);
});

