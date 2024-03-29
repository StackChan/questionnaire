
$(function () {
    $("#questionNameDetail").html( getCookie("nameOfQuestionnaire") + "学校答案情况明细");

    var oTable = new TableInit();
    oTable.Init();

//        添加下拉选择问卷
    var selectContent = ''; //下拉选择内容
    jQuery.ajax({
        type: "POST",
        url: httpRequestUrl + "/queryAllQuestionnaireByCreated",
        dataType: 'json',
        contentType: "application/json",
        success: function (result) {
            for (var i = 0; i < result.data.length; i++) {
                selectContent += '<option value="' + result.data[i].id + '">' + result.data[i].questionName + '</option>'
            }
            $("#ddlActivitynew").html(selectContent)
            $("#ddlActivitynew option[value='"+getCookie("questionId") +"']").attr("selected","selected");
        }
    });
    getQuestionnaireCount();
});

//    切换问卷
$("#ddlActivitynew").change(function () {
    var activity = $(this).val();
    var nameQuestion =  $(this)[0].selectedOptions[0].innerHTML;
    if (activity) {
        deleteCookie("questionId");
        setCookie("questionId", activity)
        deleteCookie("nameOfQuestionnaire");
        setCookie("nameOfQuestionnaire", nameQuestion)
        $("#questionNameCount").html( getCookie("nameOfQuestionnaire") + "数量统计");
        $("#questionNameDetail").html( getCookie("nameOfQuestionnaire") + "学校答题情况明细");
        getQuestionnaireCount();
        getQuestionnaireAboutSchool();
    }
})

// XXX问卷数量统计
function getQuestionnaireCount() {
    var url = '/project/queryQuestionnaireCount';
    var data = {
        "questionId": getCookie("questionId")
    };
    commonAjaxPost(true, url, data, function (result) {
        if (result.code == "666") {
            $("#example1Tr1").empty();
            var questCountData = result.data;
            var text = "";
            text += "<tr>";
            text += "<td>" + questCountData.dataName + "</td>";
            text += "<td>" + questCountData.questionCount + "</td>";
            text += "<td>" + questCountData.answerTotal + "</td>";
            if (questCountData.answerRate == "�") {
                text += "<td>-</td>";
            } else {
                text += "<td>" + questCountData.answerRate + "</td>";
            }
            text += "<td>" + questCountData.effectiveAnswer + "</td>";
            text += "</tr>";
            $("#example1Tr1").append(text);

        } else if (result.code == "333") {
            layer.msg(result.message, {icon: 2});
            setTimeout(function () {
                window.location.href = 'login.html';
            }, 1000)
        } else {
            layer.msg(result.message, {icon: 2})
        }
    })
}

// XXX问卷学校答题情况明细
function getQuestionnaireAboutSchool() {
    $("#countTable").bootstrapTable('refresh');
}



function TableInit() {

    var oTableInit = new Object();
    //???Table
    oTableInit.Init = function () {
        $('#countTable').bootstrapTable({
            url: httpRequestUrl + '/project/queryAnswerList',
            method: 'POST',
            striped: true,
            cache: false,
            pagination: true,
            sortOrder: "asc",
            queryParamsType: '',
            dataType: 'json',
            paginationShowPageGo: true,
            showJumpto: true,
            pageNumber: 1,
            queryParams: queryParams,
            sidePagination: 'server',
            pageSize: 10,
            pageList: [10, 20, 30, 40],
            search: false,
            silent: true,
            showRefresh: false,
            showToggle: false,
            minimumCountColumns: 2,
            uniqueId: "id",

            columns: [{
                checkbox: true,
                visible: false
            }, {
                field: 'id',
                title: 'id',
                align: 'center',
                formatter: function (value, row, index) {
                    return index + 1;
                }
            },
                {
                    field: 'name',
                    title: '答题人',
                    align: 'center',
                    width: '100px'
                },
                {
                    field: 'school',
                    title: '学校',
                    align: 'center'
                }, {
                    field: 'phone',
                    title: '手机号码',
                    align: 'center'
                }, {
                    field: 'email',
                    title: '电子邮箱',
                    align: 'center'
                }
                , {
                    field: 'answerTime',
                    title: '答题时间',
                    align: 'center'
                },
                {
                    field: 'state',
                    title: '答题状态',
                    align: 'center',
                    events: operateEvents,
                    formatter: addFunctionAlty
                }],
            responseHandler: function (res) {
                //console.log(res.data);
                if(res.code == "666"){
                    //var userInfo = res.data.list;
                    //var userInfo=JSON.parse('[{"password":"1","startTime":"2022-05-12T10:09:28","id":"1","endTime":"2022-05-12T10:09:30","username":"aa","status":"1"},{"password":"123","startTime":"2022-05-12T12:10:37","id":"290e08f3ea154e33ad56a18171642db1","endTime":"2022-06-11T12:10:37","username":"aaa","status":"1"},{"password":"1","startTime":"2018-10-24T09:49:00","id":"8ceeee2995f3459ba1955f85245dc7a5","endTime":"2025-11-24T09:49:00","username":"admin","status":"1"},{"password":"aa","startTime":"2022-05-16T12:01:54","id":"a6f15c3be07f42e5965bec199f7ebbe6","endTime":"2022-06-15T12:01:54","username":"aaaaa","status":"1"}]');
                    var userInfo = res.data;
                    var NewData = [];
                    if (userInfo.length) {
                        for (var i = 0; i < userInfo.length; i++) {
                            var dataNewObj = {
                                'id': '',
                                "name": '',
                                'phone': '',
                                "email": '',
                                'school': '',
                                'state': '',
                                'answer_time':''
                            };

                            dataNewObj.id = userInfo[i].id;
                            dataNewObj.name = userInfo[i].name;
                            dataNewObj.school = userInfo[i].school;
                            dataNewObj.phone = userInfo[i].phone;
                            dataNewObj.email = userInfo[i].email;
                            dataNewObj.answerTime = timeFormat(userInfo[i].answer_time); //2022-07-05T12:23:44
                            dataNewObj.state = userInfo[i].state;
                            NewData.push(dataNewObj);
                        }
                        //console.log(NewData)
                    }
                    var data = {
                        total: res.data.total,
                        rows: NewData
                    };

                    return data;
                }

            }

        });
    };

    function queryParams(params) {
        var userName = $("#keyWord").val();
        //console.log(userName);
        var questionId = getCookie("questionId");
        var temp = {   //????????????????????????????????????
            pageNum: params.pageNumber,
            pageSize: params.pageSize,
            userName: userName,
            questionId:questionId
        };
        return JSON.stringify(temp);
    }

    return oTableInit;
}

window.operateEvents = {
    //编辑
    'click #btn_count': function (e, value, row, index) {
        id = row.id;
        $.cookie('questionId', id);
    }
};


// ?????
function addFunctionAlty(value, row, index) {
    var btnText = '';
    if (row.state == "1") {
        btnText += "<button type=\"button\" id=\"btn_stop" + row.id + "\" onclick=\"changeStatus(" + "'" + row.id + "'" + ")\" class=\"btn btn-danger-g ajax-link\">已提交</button>&nbsp;&nbsp;";
    } else {
        btnText += "<button type=\"button\" id=\"btn_stop" + row.id + "\" onclick=\"changeStatus(" + "'" + row.id + "'" + ")\" class=\"btn btn-success-g ajax-link\">未提交</button>&nbsp;&nbsp;"
    }

    return btnText;
}

function getReport() {

    //下载报告
    $("#countTable").tableExport({
        type: "excel",
        escape: "false",
        fileName:  getCookie("nameOfQuestionnaire")+ '答题情况'
    });

}

//显示图表
function seeChart(id,title,type,questionOption){

    switch (type){
        case "单选题":
        case "多选题":
            var myChart = echarts.init(document.getElementById("chart"));
            var ss = questionOption.split("\?")
            var finalStr ="";
            for(var i = 0 ; i<ss.length ; i++){
                finalStr = finalStr + ss[i];
                if (i != ss.length-1){
                    finalStr = finalStr + '\"'
                }
            }
            var detail = JSON.parse(finalStr)
            console.log(detail)
            let questionOptionName = []
            for (let j = 0 ; j < detail.length; j++){
                questionOptionName.push(detail[j].optionWord)
            }
            console.log(questionOptionName)
            let questionOptionTotal = []
            for (let j = 0 ; j < detail.length; j++){
                questionOptionTotal.push(detail[j].optionTotal)
            }
            console.log(questionOptionTotal)
            var option = {
                title:{
                    text:title
                },
                legend:{
                    data:['销量']
                },
                xAxis:{
                    data:questionOptionName
                },
                yAxis: {

                },
                series:[
                    {
                        name:'人数',
                        type:'bar',
                        data:questionOptionTotal
                    }
                ]
            }
            myChart.clear()
            myChart.setOption(option);
            break;
        case "填空题":
            break;
    }


}

//设计问卷
function designQuestionnaire() {
    var ifDesignQuestionnaire = getCookie("ifDesignQuestionnaire");
    if (ifDesignQuestionnaire == "false") {
        layer.msg("问卷处于运行状态或问卷已发布，不可设计问卷", {icon: 2})
    } else {
        $.cookie("QuestionId", getCookie("questionId"));
        window.open('designQuestionnaire.html?=' + getCookie("questionId"))
    }
}

//发送问卷
function ifSendQuestionnaire() {
    var ifSendQuestionnaire = getCookie("ifSendQuestionnaire");
    if (ifSendQuestionnaire == "false") {
        layer.msg("问卷处于未发送或暂停的状态，不可发送问卷", {icon: 2})
    } else {
        $.cookie("QuestionId", getCookie("questionId"));
        window.location.href = 'sendQuestionnaire.html';
    }
}


//预览问卷
$('#ctl02_hrefView').click(function () {
    window.open('previewQuestionnaire.html?=' + getCookie("questionId"))
});

//回车事件
$(document).keydown(function (event) {
    if (event.keyCode == 13) {
        getQuestionnaireAboutSchool();
    }
});