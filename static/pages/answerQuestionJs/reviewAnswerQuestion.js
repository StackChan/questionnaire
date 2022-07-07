/**
 * Created by Amy on 2018/8/9.
 */
$(function () {
    isLoginFun();
    header();
    $("#ctl01_lblUserName").text(getCookie('userName'));
    var oTable = new TableInit();
    oTable.Init();
});

//????
$(document).keydown(function (event) {
    if (event.keyCode == 13) {
        getUserList();
    }
});

$('#userManager').on("keydown", function (event) {
    var keyCode = event.keyCode || event.which;
    if (keyCode == "13") {
        //console.log("1111")
        event.preventDefault();
    }
});

function getUserList() {
    $("#userTable").bootstrapTable('refresh');
}

function TableInit() {

    var oTableInit = new Object();
    //???Table
    oTableInit.Init = function () {
        $('#userTable').bootstrapTable({
            url: httpRequestUrl + '/project/queryAnswerList',         //?????URL?*?
            method: 'POST',                      //?????*?
            striped: true,                      //????????
            cache: false,                       //??????????true???????????????????*?
            pagination: true,                   //???????*?
            sortOrder: "asc",                   //????
            queryParamsType: '',
            dataType: 'json',
            paginationShowPageGo: true,
            showJumpto: true,
            pageNumber: 1, //??????????????
            queryParams: queryParams,//???????????
            sidePagination: 'server',//????????
            pageSize: 10,//?????
            pageList: [10, 20, 30, 40],//?????
            search: false, //?????????????????????????????????????
            silent: true,
            showRefresh: false,                  //????????
            showToggle: false,
            minimumCountColumns: 2,             //???????
            uniqueId: "id",                     //???????????????

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
                    title: 'name',
                    align: 'center',
                    width: '230px'
                },
                {
                    field: 'school',
                    title: 'school',
                    align: 'center'
                }, {
                    field: 'phone',
                    title: 'phone',
                    align: 'center'
                }, {
                    field: 'email',
                    title: 'email',
                    align: 'center'
                }
                , {
                    field: 'answer time',
                    title: 'answerTime',
                    align: 'center'
                },
                {
                    field: 'state',
                    title: 'state',
                    align: 'center',
                    events: operateEvents,//???????
                    formatter: addFunctionAlty//???????
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
                            dataNewObj.answerTime = timeFormat(userInfo[i].answer_time);
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

    // ???????
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
    //??
    'click #btn_count': function (e, value, row, index) {
        id = row.id;
        $.cookie('questionId', id);
    }
};


// ?????
function addFunctionAlty(value, row, index) {
    var btnText = '';
    if (row.state == "1") {
        btnText += "<button type=\"button\" id=\"btn_stop" + row.id + "\" onclick=\"changeStatus(" + "'" + row.id + "'" + ")\" class=\"btn btn-danger-g ajax-link\">done</button>&nbsp;&nbsp;";
    } else {
        btnText += "<button type=\"button\" id=\"btn_stop" + row.id + "\" onclick=\"changeStatus(" + "'" + row.id + "'" + ")\" class=\"btn btn-success-g ajax-link\">wait</button>&nbsp;&nbsp;"
    }

    return btnText;
}

//????
function resetPassword(id) {
    alert("????")

}

// ???????
function openCreateUserPage(id, value) {

    deleteCookie("userTitle");
    setCookie("userTitle", value);
    if (id != '') {
        deleteCookie("userId");
        setCookie("userId", id);
    }
    window.location.href = 'createNewUser.html';
}

function editUserPage() {
    alert("????")
}
// ?????????????
function changeStatus(index) {

    alert("??????")
}

//????
function deleteUser(id) {

    alert("????")
}

