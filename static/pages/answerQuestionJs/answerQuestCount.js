
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
    //初始化Table
    oTableInit.Init = function () {
        $('#countTable').bootstrapTable({
            url: httpRequestUrl + '/project/queryAnswerDetail',         //请求后台的URL（*）
            method: 'POST',                      //请求方式（*）
            striped: true,                      //是否显示行间隔色
            cache: false,                       //是否使用缓存，默认为true，所以一般情况下需要设置一下这个属性（*）
            pagination: true,                   //是否显示分页（*）
            sortOrder: "asc",                   //排序方式
            queryParamsType: '',
            dataType: 'json',
            paginationShowPageGo: true,
            showJumpto: true,
            pageNumber: 1, //初始化加载第一页，默认第一页
            queryParams: queryParams,//请求服务器时所传的参数
            sidePagination: 'server',//指定服务器端分页
            pageSize: 10,//单页记录数
            pageList: [10, 20, 30, 40],//分页步进值
            search: false, //是否显示表格搜索，此搜索是客户端搜索，不会进服务端，所以，个人感觉意义不大
            silent: true,
            showRefresh: false,                  //是否显示刷新按钮
            showToggle: false,
            minimumCountColumns: 2,             //最少允许的列数
            uniqueId: "id",                      //每一行的唯一标识，一般为主键列

            columns: [{
                checkbox: true,
                visible: false
            }, {
                field: 'id',
                title: '序号',
                align: 'center',
                formatter: function (value, row, index) {
                    return index + 1;
                }
            },
                {
                    field: 'questionTitle',
                    title: '题目内容',
                    align: 'center',
                    width: '230px'
                },
                {
                    field: 'questionType',
                    title: '题目类型',
                    align: 'center'
                },
                {
                    field: 'operation',
                    title: '答案情况',
                    align: 'center',
                    events: operateEvents,//给按钮注册事件
                    formatter: addFunctionAlty//表格中增加按钮
                }],
            responseHandler: function (res) {
                //console.log(res);
                if(res.code === "666"){
                    var userInfo = res.data;//bug3:获取全部
                    console.log(userInfo);
                    //var userInfo=JSON.parse('[{"password":"1","startTime":"2022-05-12T10:09:28","id":"1","endTime":"2022-05-12T10:09:30","username":"aa","status":"1"},{"password":"123","startTime":"2022-05-12T12:10:37","id":"290e08f3ea154e33ad56a18171642db1","endTime":"2022-06-11T12:10:37","username":"aaa","status":"1"},{"password":"1","startTime":"2018-10-24T09:49:00","id":"8ceeee2995f3459ba1955f85245dc7a5","endTime":"2025-11-24T09:49:00","username":"admin","status":"1"},{"password":"aa","startTime":"2022-05-16T12:01:54","id":"a6f15c3be07f42e5965bec199f7ebbe6","endTime":"2022-06-15T12:01:54","username":"aaaaa","status":"1"}]');
                    var NewData = [];
                    if (userInfo.length) {
                        for (var i = 0; i < userInfo.length; i++) {
                            var dataNewObj = {
                                'id': '',
                                "questionTitle": '',
                                'questionType': '',
                                "answerDetail": '',
                                'questionDetail': ''
                            };

                            dataNewObj.id = userInfo[i].id;
                            dataNewObj.questionTitle = userInfo[i].questionTitle;
                            switch (userInfo[i].questionType){
                                case "0":
                                    dataNewObj.questionType = "单选题"
                                    break;
                                case "1":
                                    dataNewObj.questionType = "多选题"
                                    break;
                                case "2":
                                    dataNewObj.questionType = "填空题"
                                    break;
                                case "3":
                                    dataNewObj.questionType = "矩阵题"
                                    break;
                                case "4":
                                    dataNewObj.questionType = "量表"
                                    break;

                            }
                            dataNewObj.questionOption =userInfo[i].questionOption;
                            NewData.push(dataNewObj);
                        }

                    }
                    var data = {
                        total: res.data.total,//
                        rows: NewData
                    };

                    return data;
                }

            }

        });
    };

    // 得到查询的参数
    function queryParams(params) {
        var userName = $("#keyWord").val();
        var questionId = getCookie("questionId");
        //console.log(userName);
        var temp = {   //这里的键的名字和控制器的变量名必须一直，这边改动，控制器也需要改成一样的
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


// 表格中按钮
function addFunctionAlty(value, row, index) {
    var btnText = '';

    let str = JSON.stringify(row.questionOption);
    var ss = str.split("\"")
    var option ="";
    for(var i = 0 ; i<ss.length ; i++){
        option = option + ss[i];
        if (i != ss.length-1){
            option = option + '?'
        }
    }
    console.log(option)
    btnText += "<button type=\"button\" id=\"btn_look\" onclick=\"seeChart(" + "'" + row.id + "'" + ",'"+row.questionTitle+"','"+row.questionType+"','"+option+"'"+")\" style='width: 77px;' class=\"btn btn-default-g ajax-link\">查看图表</button>&nbsp;&nbsp;";

    return btnText;
}

function getReport() {

    //下载报告
    $("#countTable").tableExport({
        type: "excel",
        escape: "false",
        fileName:  getCookie("nameOfQuestionnaire")+ '学校答题情况明细'
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