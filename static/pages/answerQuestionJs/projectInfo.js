/**
 * Created by Amy on 2018/8/7.
 */
$(function () {
    isLoginFun();
    header();
    $("#ctl01_lblUserName").text(getCookie('userName'));
    getProjectInfo();

});

// 查看项目详细信息
function getProjectInfo() {
    var projectId = getCookie('projectId');

    var url = '/project/queryProjectList';
    var data = {
        "id": projectId
    };
    commonAjaxPost(true, url, data, getProjectInfoSuccess);

}

// 查看项目详细信息成功
function getProjectInfoSuccess(result) {
    // //console.log(result)
    if (result.code == "666") {
        var projectInfo = result.data[0];
        $("#projectNameSpan").text(projectInfo.projectName);
//        $("#createTimeSpan").text(timeFormat(projectInfo.creationDate.replace(/-/g,'/')));
        $("#createTimeSpan").text(timeFormat(projectInfo.creationDate));
        $("#adminSpan").text(projectInfo.createdBy);
        $("#projectContentSpan").text(projectInfo.projectContent);

        var text = "";
//            text += "<tr>";
//            text += "    <td style=\"text-align: center;color: #d9534f\" colspan=\"4\">暂无调查问卷</td>";
//            text += "</tr>";
        if(projectInfo.list.length == 0){
        	text += "<tr>";
            text += "    <td style=\"text-align: center;color: #d9534f\" colspan=\"4\">暂无调查问卷</td>";
            text += "</tr>";
        }
        else{
        	for(var i = 0;i<projectInfo.list.length;i++){
        		var index = i+1;
        		var questionnaireInfo = projectInfo.list[i];
        		var questionName = questionnaireInfo.questionName;
        		if (questionName.length >= 20) {
        			questionName = questionName.substring(0, 21) + "...";
                }
        		text += "<tr>";
        		text += "<td style=\"text-align: center;padding-top:20px\" colspan=\"1\">"+index+"</td>";
                text += "<td style=\"text-align: center;padding-top:20px\" colspan=\"1\">"+questionName+"</td>";
                if(questionnaireInfo.releaseTime != null){
                   text += "<td style=\"text-align: center;padding-top:20px\" colspan=\"1\">"+timeFormat(questionnaireInfo.releaseTime)+"</td>"
                       + "<td style=\"text-align: center\" colspan=\"1\"><button type=\"button\" id=\"btn_look\" onclick=\"editQuest(" + "'" + questionnaireInfo.id +"','"+questionnaireInfo.questionName+"','"+questionnaireInfo.questionContent+"','"+questionnaireInfo.endTime+"','"+questionnaireInfo.startTime+"','"+questionnaireInfo.dataId + "')\" class=\"btn btn-default-g ajax-link\">编辑</button>"
                       + "<button type=\"button\" id=\"btn_stop" + questionnaireInfo.id + "\" onclick=\"designQuest(" + "'" + questionnaireInfo.id +"','"+questionnaireInfo.questionName+"','"+questionnaireInfo.questionContent+"','"+questionnaireInfo.endTime+"','"+questionnaireInfo.creationDate+"','"+questionnaireInfo.dataId + "')\" class=\"btn btn-default-g ajax-link\">问题</button>"
                       + "<button type=\"button\" id=\"btn_stop" + questionnaireInfo.id + "\" onclick=\"pauseQuest(" + "'" + questionnaireInfo.id +"','"+questionnaireInfo.questionName+"','"+questionnaireInfo.questionContent+"','"+questionnaireInfo.endTime+"','"+questionnaireInfo.creationDate+"','"+questionnaireInfo.dataId + "')\" class=\"btn btn-danger-g ajax-link\">停止</button>";
                }
                else{
                	text += "<td style=\"text-align: center;padding-top:20px\" colspan=\"1\">问卷还未发布</td>"
                    + "<td style=\"text-align: center\" colspan=\"1\"><button type=\"button\" id=\"btn_look\" onclick=\"editQuest(" + "'" + questionnaireInfo.id +"','"+questionnaireInfo.questionName+"','"+questionnaireInfo.questionContent+"','"+questionnaireInfo.endTime+"','"+questionnaireInfo.startTime+"','"+questionnaireInfo.dataId + "')\" class=\"btn btn-default-g ajax-link\">编辑</button>"
                    + "<button type=\"button\" id=\"btn_stop" + questionnaireInfo.id + "\" onclick=\"designQuest(" + "'" + questionnaireInfo.id +"','"+questionnaireInfo.questionName+"','"+questionnaireInfo.questionContent+"','"+questionnaireInfo.endTime+"','"+questionnaireInfo.creationDate+"','"+questionnaireInfo.dataId + "')\" class=\"btn btn-default-g ajax-link\">问题</button>"
                    + "<button type=\"button\" id=\"btn_stop" + questionnaireInfo.id + "\" onclick=\"sendQuest(" + "'" + questionnaireInfo.id +"','"+questionnaireInfo.questionName+"','"+questionnaireInfo.questionContent+"','"+questionnaireInfo.endTime+"','"+questionnaireInfo.creationDate+"','"+questionnaireInfo.dataId + "')\" class=\"btn btn-success-g ajax-link\">发布</button>";
                }
                text +=  "<button type=\"button\" id=\"btn_stop" + questionnaireInfo.id + "\" onclick=\"stopQuest(" + "'" + questionnaireInfo.id +"','"+questionnaireInfo.questionName+"','"+questionnaireInfo.questionContent+"','"+questionnaireInfo.endTime+"','"+questionnaireInfo.creationDate+"','"+questionnaireInfo.dataId + "')\" class=\"btn btn-danger-g ajax-link\">删除</button></td>" ;
                text += "</tr>";
        	}


        }
        
        $("#questTableBody").empty();
        $("#questTableBody").append(text)

    } else if (result.code == "333") {
        layer.msg(result.message, {icon: 2});
        setTimeout(function () {
            window.location.href = 'login.html';
        }, 1000)
    } else {
        layer.msg(result.message, {icon: 2})
    }
}

//编辑问卷基本信息
function editQuest(id, name, content, endTime, startTime, dataId) {
    var data = {
        "id": id
    };
    commonAjaxPost(true, '/project/selectQuestionnaireStatus', data, function (result) {
         if (result.code == "666") {
             if (result.data != "5") {
                 layer.msg('问卷已发布，不可修改', {icon: 2});
             } else if (result.data == "5") {
                 deleteCookie("questionId");
                 deleteCookie("questionName");
                 deleteCookie("questionContent");
                 deleteCookie("endTime");
                 setCookie("questionId", id);
                 setCookie("questionName", name);
                 setCookie("questionContent", content);
                 setCookie("endTime", endTime);
                 //setCookie("creationDate", creationDate);
                 setCookie("startTime", startTime);
                 setCookie("dataId", dataId);
                 window.location.href = 'editQuestionnaire.html';
             }
         }
        if (result.code == "666") {
             if (result.data == "1") {
                 if ($("#operationAll" + m + n).children("a:first-child").text() == '开启') {
                     judgeIfChangeStatus(m, n);
                 }
                 layer.msg('问卷运行中，不可修改', {icon: 2});
             } else

             if (result.data != "1") {
                commonAjaxPost(true, '/project/selectQuestSendStatus', {id: id}, function (result) {
                   //发送过问卷
                   if (result.code == "40003") {
                    setCookie("ifEditQuestType", "false");
                   } else if (result.code == "666") {         //未发送过问卷
                    setCookie("ifEditQuestType", "true");
                   }
                  });
                deleteCookie("questionId");
                deleteCookie("questionName");
                deleteCookie("questionContent");
                deleteCookie("endTime");
                setCookie("questionId", id);
                setCookie("questionName", name);
                setCookie("questionContent", content);
                setCookie("endTime", endTime);
                setCookie("creationDate", creationDate);
                setCookie("dataId", dataId);
                window.location.href = 'editQuestionnaire.html';
             }
           }

        else if (result.code == "333") {
            layer.msg(result.message, {icon: 2});
            setTimeout(function () {
                window.location.href = 'login.html';
            }, 1000)
        } else {
            layer.msg(result.message, {icon: 2})
        }
    });
}
//发布问卷
function sendQuest(id, name, content, endTime, creationDate, dataId) {
    deleteCookie("questionId");
    deleteCookie("questionName");
    deleteCookie("questionContent");
    deleteCookie("endTime");
    setCookie("questionId", id);
    setCookie("nameOfQuestionnaire", name);
    setCookie("questionContent", content);
    setCookie("endTime", endTime);
    setCookie("creationDate", creationDate);
    setCookie("dataId", dataId);
    window.location.href = 'sendQuestionnaire.html'
}
//设计问卷
function designQuest(id, name, content, endTime, creationDate, dataId) {
    deleteCookie("QuestionId");
    deleteCookie("questionName");
    deleteCookie("questionContent");
    deleteCookie("endTime");
    setCookie("QuestionId", id);
    setCookie("nameOfQuestionnaire", name);
    setCookie("questionContent", content);
    setCookie("endTime", endTime);
    setCookie("creationDate", creationDate);
    setCookie("dataId", dataId);
    window.location.href ='designQuestionnaire.html'+'?qId='+id;
}

//停止问卷
function pauseQuest(id, name, content, endTime, creationDate, dataId) {
    deleteCookie("QuestionId");
    deleteCookie("questionName");
    deleteCookie("questionContent");
    deleteCookie("endTime");
    setCookie("QuestionId", id);
    setCookie("nameOfQuestionnaire", name);
    setCookie("questionContent", content);
    setCookie("endTime", endTime);
    setCookie("creationDate", creationDate);
    setCookie("dataId", dataId);
    var data = {
        "id" : id
    }
    commonAjaxPost(true,'/project/pauseQuestionnaire',data,function (result){
        if (result.code == "666") {
            layer.msg('问卷已停止', { icon: 1 });
            setTimeout(function() {
                window.location.href = 'projectInfo.html';
            }, 1000)
        } else if (result.code == "333") {
            layer.msg(result.message, { icon: 2 });
            setTimeout(function() {
                window.location.href = 'login.html';
            }, 1000)
        } else {
            layer.msg(result.message, { icon: 2 })
        }
    });
}


//删除问卷
function stopQuest(id, name, content, endTime, creationDate, dataId) {
    var data = {
        "id": id,
        "questionStop":4
        };
    commonAjaxPost(true, '/project/modifyQuestionnaireStatus', data, function(result) {
        if (result.code == "666") {
                layer.msg('问卷已成为历史问卷', { icon: 1 });
                setTimeout(function() {
                window.location.href = 'projectInfo.html';
            }, 1000)
        } else if (result.code == "333") {
            layer.msg(result.message, { icon: 2 });
            setTimeout(function() {
                window.location.href = 'login.html';
            }, 1000)
        } else {
            layer.msg(result.message, { icon: 2 })
        }
    });
}