package com.aim.questionnaire.controller;

import com.aim.questionnaire.beans.HttpResponseEntity;
import com.aim.questionnaire.common.Constans;
import com.aim.questionnaire.dao.entity.ProjectEntity;
import com.aim.questionnaire.dao.entity.QuestionnaireEntity;
import com.aim.questionnaire.feignClient.ProviderClient;
import com.aim.questionnaire.service.ProjectService;
import com.aim.questionnaire.service.QuestionnaireService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * Created by wln on 2018\8\6 0006.
 */
@RestController
@RequestMapping("/project")
public class ProjectController {

    private final Logger logger = LoggerFactory.getLogger(ProjectController.class);

    @Autowired
    private ProjectService projectService;
    @Autowired
    private QuestionnaireService questionnaireService;

    //动态代理对象，内部远程调用服务生产者
    @Autowired
    ProviderClient providerClient;


    /**
     * 查询项目的信息
     * @param projectEntity
     * @return
     */
    @RequestMapping(value = "/queryProjectList",method = RequestMethod.POST, headers = "Accept=application/json")
    public HttpResponseEntity queryProjectList(@RequestBody(required = false) ProjectEntity projectEntity) {
        HttpResponseEntity httpResponseEntity = new HttpResponseEntity();
        httpResponseEntity.setCode(Constans.SUCCESS_CODE);
        List<Map<String, Object>> resultList = projectService.queryProjectList(projectEntity);
        for(Map<String, Object> map : resultList) {
        	String id = map.get("id").toString();
        	List<Map<String,Object>> list = questionnaireService.queryQuestionListByProjectId(id);
        	map.put("list",list);
        }
        httpResponseEntity.setData(resultList);
        httpResponseEntity.setMessage(Constans.STATUS_MESSAGE);
        return httpResponseEntity;
    }

    /**
     * 根据id删除项目
     * @param projectEntity
     * @return
     */
    @RequestMapping(value = "/deleteProjectById",method = RequestMethod.POST, headers = "Accept=application/json")
    public HttpResponseEntity deleteProjectById(@RequestBody ProjectEntity projectEntity) {
        HttpResponseEntity httpResponseEntity = new HttpResponseEntity();
        QuestionnaireEntity questionnaireEntity = new QuestionnaireEntity();
        questionnaireEntity.setProjectId(projectEntity.getId());
        int count = questionnaireService.queryReleaseQuestionnaireCount(questionnaireEntity);
        if(count == 0) {
        	projectService.deleteProjectById(projectEntity);
        	List<Map<String, Object>> questionnaireList = questionnaireService.queryQuestionListByProjectId(projectEntity.getId());
        	for(Map<String, Object> questionnaire : questionnaireList) {
        		questionnaireService.deleteByPrimaryKey(questionnaire.get("id").toString());
        	}
        	httpResponseEntity.setCode(Constans.SUCCESS_CODE);
        	httpResponseEntity.setData(null);
        	httpResponseEntity.setMessage(Constans.DELETE_MESSAGE);
        }
        else {
        	httpResponseEntity.setCode(Constans.EXIST_CODE);
        	httpResponseEntity.setData(null);
        	httpResponseEntity.setMessage(Constans.QUESTION_EXIST_MESSAGE);
        }
        return httpResponseEntity;
    }

    /**
     * 添加项目
     * @param projectEntity
     * @return
     */
    @RequestMapping(value = "/addProjectInfo",method = RequestMethod.POST, headers = "Accept=application/json")
    public HttpResponseEntity addProjectInfo(@RequestBody ProjectEntity projectEntity) {
        HttpResponseEntity httpResponseEntity = new HttpResponseEntity();
        boolean flag=true;
        try {
            flag = projectService.queryProjectEntityIsExit(projectEntity);
        }catch (Exception e){
            httpResponseEntity.setCode(Constans.EXIST_CODE);
            httpResponseEntity.setData(null);
            httpResponseEntity.setMessage(Constans.PROJECT_EXIST_NAME);
        }
        if(flag) {
        	httpResponseEntity.setCode(Constans.EXIST_CODE);
        	httpResponseEntity.setData(null);
        	httpResponseEntity.setMessage(Constans.PROJECT_EXIST_NAME);
        }
        else {
            //关键代码: feign通过伪HTTP的方式远程调用,微服务相互调用(ms-project调用ms-user) 替代了原先的直接创建UserService类
            String user=null;
            try {
                 user = providerClient.queryIdService(projectEntity.getCreatedBy());
            }catch (Exception e){
                httpResponseEntity.setCode(Constans.EXIST_CODE);
            }
			projectService.addProjectInfo(projectEntity, user);
			httpResponseEntity.setCode(Constans.SUCCESS_CODE);
			httpResponseEntity.setData(null);
        	httpResponseEntity.setMessage(Constans.ADD_MESSAGE);
		}
        return httpResponseEntity;
    }

    /**
     * 根据项目id修改项目
     * @param projectEntity
     * @return
     */
    @RequestMapping(value = "/modifyProjectInfo",method = RequestMethod.POST, headers = "Accept=application/json")
    public HttpResponseEntity modifyProjectInfo(@RequestBody ProjectEntity projectEntity) {
        HttpResponseEntity httpResponseEntity = new HttpResponseEntity();
        ProjectEntity searchProject = projectService.queryProjectEntity(projectEntity);
        if(searchProject == null) {
        	projectService.updateProjectById(projectEntity);
        	httpResponseEntity.setCode(Constans.SUCCESS_CODE);
        	httpResponseEntity.setData(null);
        	httpResponseEntity.setMessage(Constans.UPDATE_MESSAGE);
        }
        else if(searchProject.getId().equals(projectEntity.getId())) {
        	projectService.updateProjectById(projectEntity);
        	httpResponseEntity.setCode(Constans.SUCCESS_CODE);
        	httpResponseEntity.setData(null);
        	httpResponseEntity.setMessage(Constans.UPDATE_MESSAGE);
        }
        else {
        	httpResponseEntity.setCode(Constans.EXIST_CODE);
        	httpResponseEntity.setData(null);
        	httpResponseEntity.setMessage(Constans.PROJECT_EXIST_NAME);
        }
        return httpResponseEntity;
    }
    /**
     * 判断项目中是否有正在发布问卷
     * @param projectEntity
     * @return
     */
    @RequestMapping(value = "/queryIsHaveReleaseQuestionnaire",method = RequestMethod.POST, headers = "Accept=application/json")
    public HttpResponseEntity queryIsHaveReleaseQuestionnaire(@RequestBody ProjectEntity projectEntity) {
    	HttpResponseEntity httpResponseEntity = new HttpResponseEntity();
    	QuestionnaireEntity questionnaireEntity = new QuestionnaireEntity();
        questionnaireEntity.setProjectId(projectEntity.getId());
        int count = questionnaireService.queryReleaseQuestionnaireCount(questionnaireEntity);
        if(count == 0) {
        	httpResponseEntity.setCode(Constans.SUCCESS_CODE);
        }else {
        	httpResponseEntity.setCode(Constans.EXIST_CODE);
        	httpResponseEntity.setMessage(Constans.QUESTION_EXIST_MESSAGE);
        }
    	return httpResponseEntity;
    }
    

    /**
     * 查询全部项目的名字接口
     * @return
     */
    @RequestMapping(value = "/queryAllProjectName",method = RequestMethod.POST, headers = "Accept=application/json")
    public HttpResponseEntity queryAllProjectName() {
        HttpResponseEntity httpResponseEntity = new HttpResponseEntity();
          
        return httpResponseEntity;
    }
}
