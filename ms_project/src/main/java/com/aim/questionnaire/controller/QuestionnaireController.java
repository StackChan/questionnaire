package com.aim.questionnaire.controller;

import com.aim.questionnaire.beans.HttpResponseEntity;
import com.aim.questionnaire.common.Constans;
import com.aim.questionnaire.dao.entity.QuestionnaireEntity;
import com.aim.questionnaire.service.QuestionnaireService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/project")
public class QuestionnaireController {
	private final Logger logger = LoggerFactory.getLogger(ProjectController.class);
	
	@Autowired
	QuestionnaireService questionnaireService;
	
	/**
	 * 添加问卷
	 * @author shan csk
	 */
	 @RequestMapping(value = "/addQuestionnaire",method = RequestMethod.POST, headers = "Accept=application/json")
	 public HttpResponseEntity addQuestionnaire(@RequestBody QuestionnaireEntity questionnaireEntity) {
		 HttpResponseEntity httpResponseEntity = new HttpResponseEntity();
		 String parentId =String.valueOf(questionnaireEntity.getProjectId());
		 List<Map<String,Object>> querList = questionnaireService.queryQuestionListByProjectId(parentId);
		 boolean flag = isHaveQuestion(querList,questionnaireEntity);
		 if(flag) {
			 httpResponseEntity.setCode(Constans.EXIST_CODE);
			 httpResponseEntity.setData(null);
			 httpResponseEntity.setMessage(Constans.PROJECT_EXIST_QUESTION);
		 }else {
			 questionnaireService.addQuestionnaire(questionnaireEntity);
			 httpResponseEntity.setCode(Constans.SUCCESS_CODE);
			 httpResponseEntity.setData(null);
			 httpResponseEntity.setMessage(Constans.ADD_MESSAGE);
		 }
//		 questionnaireService.addQuestionnaire(questionnaireEntity);
//		 httpResponseEntity.setCode(Constans.SUCCESS_CODE);
//         httpResponseEntity.setMessage(Constans.ADD_MESSAGE);
		 return httpResponseEntity;
	 }
	 
	 /**
	  * 根据id查询问卷状态
	  * @author shan csk
	  */
	 @RequestMapping(value = "/selectQuestionnaireStatus",method = RequestMethod.POST, headers = "Accept=application/json")
	 public HttpResponseEntity selectQuestionnaireStatus(@RequestBody HashMap<String, Object> map) {
		 String id = map.get("id").toString();
		 HttpResponseEntity httpResponseEntity = new HttpResponseEntity();
		 String questionStop = questionnaireService.queryQuestionnaireIsStopStatus(id);
		 httpResponseEntity.setCode(Constans.SUCCESS_CODE);
		 httpResponseEntity.setData(questionStop);
		 httpResponseEntity.setMessage(Constans.STATUS_MESSAGE);
		 return httpResponseEntity;
	 }
	 /**
	  * 修改问卷
	  * @author shan csk
	  */
	 @RequestMapping(value = "/modifyQuestionnaireInfo",method = RequestMethod.POST, headers = "Accept=application/json")
	 public HttpResponseEntity modifyQuestionnaireInfo(@RequestBody QuestionnaireEntity questionnaireEntity) {
		 HttpResponseEntity httpResponseEntity = new HttpResponseEntity();
		 QuestionnaireEntity queryQuestion = questionnaireService.selectByPrimaryKey(questionnaireEntity.getId());
		 String projectId = queryQuestion.getProjectId();
		 List<Map<String,Object>> querList = questionnaireService.queryQuestionListByProjectId(projectId);
		 boolean flag = isHaveIdQuestion(querList,questionnaireEntity);
		 if(flag) {
			 httpResponseEntity.setCode(Constans.EXIST_CODE);
			 httpResponseEntity.setData(null);
			 httpResponseEntity.setMessage(Constans.PROJECT_EXIST_QUESTION);
		 }
		 else {
			 questionnaireService.modifyQuestionnaireInfo(questionnaireEntity);
			 httpResponseEntity.setCode(Constans.SUCCESS_CODE);
			 httpResponseEntity.setData(null);
			 httpResponseEntity.setMessage(Constans.UPDATE_MESSAGE);
		 }
		 return httpResponseEntity;
	 }
	 
	 /**
	  * 设计问卷
	  * @param map
	  * @return
	  */
	 @RequestMapping(value = "/modifyQuestionnaire", method = RequestMethod.POST, headers = "Accept=application/json")
	    public HttpResponseEntity modifyQuestionnaire(@RequestBody(required = false) HashMap<String, Object> map) {
	        HttpResponseEntity httpResponseEntity = new HttpResponseEntity();
	        questionnaireService.modifyQuestionnaire(map);
	        httpResponseEntity.setCode(Constans.SUCCESS_CODE);
	        httpResponseEntity.setMessage(Constans.UPDATE_MESSAGE);
	        httpResponseEntity.setData(map.get("questionId"));
	        return httpResponseEntity;
	    }
	 
	 /**
	  * 删除问卷
	  * @author shan csk
	  * @param map
	  * @return
	  */
	 @RequestMapping(value = "/deleteQuestionnaire",method = RequestMethod.POST, headers = "Accept=application/json")
	 public HttpResponseEntity deleteQuestionnaire(@RequestBody HashMap<String, Object> map) {
		 HttpResponseEntity httpResponseEntity = new HttpResponseEntity();
		 String id = map.get("id").toString();
		 questionnaireService.deleteByPrimaryKey(id);
		 httpResponseEntity.setCode(Constans.SUCCESS_CODE);
		 httpResponseEntity.setData(null);
		 httpResponseEntity.setMessage(Constans.DELETE_MESSAGE);
		 return httpResponseEntity;
	 }
	 
	 /**
	  * 根据id删除问卷
	  * @param questionnaireEntity
	  * @return
	  */
	 @RequestMapping(value = "/deleteQuestionnaireById", method = RequestMethod.POST, headers = "Accept=application/json")
	  public HttpResponseEntity deleteQuestionnaireById(@RequestBody QuestionnaireEntity questionnaireEntity) {
	        HttpResponseEntity httpResponseEntity = new HttpResponseEntity();
	        questionnaireService.deleteByPrimaryKey(questionnaireEntity.getId());
			 httpResponseEntity.setCode(Constans.SUCCESS_CODE);
			 httpResponseEntity.setData(null);
			 httpResponseEntity.setMessage(Constans.DELETE_MESSAGE);
			 return httpResponseEntity;
	    }
	 
	 /**
	  * 根据id查询问卷
	  * @author shan csk
	  */
	 @RequestMapping(value = "/queryQuestionnaireAll",method = RequestMethod.POST, headers = "Accept=application/json")
	 public HttpResponseEntity queryQuestionnaireAll(@RequestBody HashMap<String, Object> map) {
		 HttpResponseEntity httpResponseEntity = new HttpResponseEntity();
		 String id = map.get("id").toString();
		 QuestionnaireEntity questionnaireEntity = questionnaireService.selectByPrimaryKey(id);
		 httpResponseEntity.setCode(Constans.SUCCESS_CODE);
		 httpResponseEntity.setData(questionnaireEntity);
		 httpResponseEntity.setMessage(Constans.STATUS_MESSAGE);
		 
		 return httpResponseEntity;
	 }
	 /**
	  * 根据项目ID查询该项目下问卷
	  * @author shan csk
	  * @param questionnaireEntity
	  * @return
	  */
	 @RequestMapping(value = "/queryQuestionListByProjectId", method = RequestMethod.POST, headers = "Accept=application/json")
	 public HttpResponseEntity queryQuestionListByProjectId(@RequestBody QuestionnaireEntity questionnaireEntity) {
	     HttpResponseEntity httpResponseEntity = new HttpResponseEntity();
	     List<Map<String,Object>> list= questionnaireService.queryQuestionListByProjectId(questionnaireEntity.getProjectId());
	     httpResponseEntity.setCode(Constans.SUCCESS_CODE);
	     httpResponseEntity.setData(list);
	     return httpResponseEntity;
	 }
	 /**
	  * 
	  * @param questionnaireEntity
	  * @return
	  */
	 @RequestMapping(value = "/queryQuestContextEnd", method = RequestMethod.POST, headers = "Accept=application/json")
	    public HttpResponseEntity queryQuestContextEnd(@RequestBody QuestionnaireEntity questionnaireEntity) {
	        HttpResponseEntity httpResponseEntity = new HttpResponseEntity();
	        QuestionnaireEntity result = questionnaireService.queryQuestContextEnd(questionnaireEntity.getId());
	        httpResponseEntity.setCode(Constans.SUCCESS_CODE);
	        httpResponseEntity.setData(result);
	        return httpResponseEntity;
	    }
	 /**
	  * 
	  * @return
	  */
	 @RequestMapping(value = "/selSum", method = RequestMethod.POST, headers = "Accept=application/json")
	    public HttpResponseEntity selSum() {
	        HttpResponseEntity httpResponseEntity = new HttpResponseEntity();
	        httpResponseEntity.setCode(Constans.SUCCESS_CODE);
	        httpResponseEntity.setData(100);
	        return httpResponseEntity;
	    }
	 /**
	  * 发送问卷时修改
	  * @param map
	  * @return
	  */
	 @RequestMapping(value = "/addSendQuestionnaire", method = RequestMethod.POST, headers = "Accept=application/json")
	    public HttpResponseEntity addSendQuestionnaire(@RequestBody HashMap<String, Object> map) {
	        HttpResponseEntity httpResponseEntity = new HttpResponseEntity();
	        try {
				int result = questionnaireService.addSendQuestionnaire(map);
				httpResponseEntity.setCode(Constans.SUCCESS_CODE);
				httpResponseEntity.setData(result);
			}catch (Exception e){
				httpResponseEntity.setCode(Constans.EXIST_CODE);
				httpResponseEntity.setData(null);
			}

	        return httpResponseEntity;
	    }
	    /**
	 * 添加答题记录
	 * @param map
	 * @return
	 */
	@RequestMapping(value = "/addAnswerQuestionnaire", method = RequestMethod.POST, headers = "Accept=application/json")
	public HttpResponseEntity addAnswerQuestionnaire(@RequestBody HashMap<String, Object> map) {
		HttpResponseEntity httpResponseEntity = new HttpResponseEntity();
		try {
			int result = questionnaireService.addAnswerQuestionnaire(map);
			httpResponseEntity.setCode(Constans.SUCCESS_CODE);
			httpResponseEntity.setData(result);
		}catch (Exception e){
			httpResponseEntity.setCode(Constans.EXIST_CODE);
			httpResponseEntity.setData(null);
			httpResponseEntity.setMessage(Constans.EXIST_MESSAGE);
		}
		return httpResponseEntity;
	}
	/**
	  * 查询历史问卷
	  * @param map
	  * @return
	  */
	 @RequestMapping(value = "/queryHistoryQuestionnaire", method = RequestMethod.POST, headers = "Accept=application/json")
	    public HttpResponseEntity queryHistoryQuestionnaire(@RequestBody(required = false) HashMap<String, Object> map) {
	        HttpResponseEntity httpResponseEntity = new HttpResponseEntity();
	        List<Map<String, Object>> result = questionnaireService.queryHistoryQuestionnaire(map);
	        httpResponseEntity.setCode(Constans.SUCCESS_CODE);
	        httpResponseEntity.setData(result);
	        return httpResponseEntity;
	    }

	    /**
	 * 查询问卷答题情况
	 * @param map
	 * @return
	 */
	@RequestMapping(value = "/queryQuestionnaireCount", method = RequestMethod.POST, headers = "Accept=application/json")
	public HttpResponseEntity queryQuestionnaireCount(@RequestBody(required = false) HashMap<String, Object> map) {
		HttpResponseEntity httpResponseEntity = new HttpResponseEntity();
		try {
			Map<String, Object> result = questionnaireService.queryQuestionnaireCount(map);
			httpResponseEntity.setCode(Constans.SUCCESS_CODE);
			httpResponseEntity.setData(result);
		}catch (Exception e){
			httpResponseEntity.setCode(Constans.EXIST_CODE);
			httpResponseEntity.setData(null);
			httpResponseEntity.setMessage(Constans.EXIST_MESSAGE);
		}
		return httpResponseEntity;
	}
	/**
	 * 暂停问卷
	 * @param map
	 * @return
	 */
	@RequestMapping(value = "/pauseQuestionnaire", method = RequestMethod.POST, headers = "Accept=application/json")
	public HttpResponseEntity pauseQuestionnaire(@RequestBody Map<String, Object> map) {
		HttpResponseEntity httpResponseEntity = new HttpResponseEntity();
		int result = questionnaireService.pauseQuestionnaire(map);
		httpResponseEntity.setCode(Constans.SUCCESS_CODE);
		httpResponseEntity.setData(result);
		return httpResponseEntity;
	}
	/**
	 * 按学校生成表格
	 * @param map
	 * @return
	 */
	@RequestMapping(value = "/queryQuestionnaireAboutSchool", method = RequestMethod.POST, headers = "Accept=application/json")
	public HttpResponseEntity queryQuestionnaireAboutSchool(@RequestBody Map<String, Object> map) {
		HttpResponseEntity httpResponseEntity = new HttpResponseEntity();
		try {
			List<Map<String,Object>> result = questionnaireService.queryQuestionnaireAboutSchool(map);
			httpResponseEntity.setCode(Constans.SUCCESS_CODE);
			httpResponseEntity.setData(result);
		}catch (Exception e){
			httpResponseEntity.setCode(Constans.EXIST_CODE);
			httpResponseEntity.setData(null);
		}
		return httpResponseEntity;
	}
	/**
	 * 按学校生成表格
	 * @param map
	 * @return
	 */
	@RequestMapping(value = "/queryAnswerList", method = RequestMethod.POST, headers = "Accept=application/json")
	public HttpResponseEntity queryAnswerList(@RequestBody Map<String, Object> map) {
		HttpResponseEntity httpResponseEntity = new HttpResponseEntity();
		try {
			List<Map<String,Object>> result = questionnaireService.queryAnswerList(map);
			httpResponseEntity.setCode(Constans.SUCCESS_CODE);
			httpResponseEntity.setData(result);
		}catch (Exception e){
			httpResponseEntity.setCode(Constans.EXIST_CODE);
			httpResponseEntity.setData(null);
		}
		return httpResponseEntity;
	}
	 /**
	  * 查询模板
	  * @param questionnaireEntity
	  * @return
	  */
	@RequestMapping(value = "/queryQuestionnaireMould", method = RequestMethod.POST, headers = "Accept=application/json")
	    public HttpResponseEntity queryQuestionnaireMould(@RequestBody QuestionnaireEntity questionnaireEntity) {
	        HttpResponseEntity httpResponseEntity = new HttpResponseEntity();
	        List<Map<String,Object>> result= questionnaireService.queryQuestionnaireMould(questionnaireEntity.getDataId());
	        httpResponseEntity.setCode(Constans.SUCCESS_CODE);
	        httpResponseEntity.setData(result);
	        return httpResponseEntity;
	   }
	/**
	 * 修改问卷状态
	 * @param map
	 * @return
	 */
    @RequestMapping(value = "/modifyQuestionnaireStatus", method = RequestMethod.POST, headers = "Accept=application/json")
    public HttpResponseEntity modifyQuestionnaireStatus(@RequestBody(required = false) HashMap<String, Object> map) {
        HttpResponseEntity httpResponseEntity = new HttpResponseEntity();
        int result = questionnaireService.modifyQuestionnaireStatus(map);
        httpResponseEntity.setCode(Constans.SUCCESS_CODE);
        httpResponseEntity.setMessage(Constans.UPDATE_MESSAGE);
        httpResponseEntity.setData(result);
        return httpResponseEntity;
    }
    /**
     * 查询历史问卷
     * @param map
     * @return
     */
    @RequestMapping(value = "/queryQuestionnaireList", method = RequestMethod.POST, headers = "Accept=application/json")
    public HttpResponseEntity queryQuestionnaireList(@RequestBody(required = false) HashMap<String, Object> map) {
        HttpResponseEntity httpResponseEntity = new HttpResponseEntity();
        List<Object> result = questionnaireService.queryQuestionnaireList(map);
        httpResponseEntity.setCode(Constans.SUCCESS_CODE);
        httpResponseEntity.setData(result);
        return httpResponseEntity;
    }
	
    /**
     * 修改历史问卷状态
     * @param map
     * @return
     */
    @RequestMapping(value = "/modifyHistoryQuestionnaireStatus", method = RequestMethod.POST, headers = "Accept=application/json")
    public HttpResponseEntity modifyHistoryQuestionnaireStatus(@RequestBody HashMap<String, Object> map) {
        HttpResponseEntity httpResponseEntity = new HttpResponseEntity();
        int result= questionnaireService.modifyHistoryQuestionnaireStatus(map);
        httpResponseEntity.setCode(Constans.SUCCESS_CODE);
        httpResponseEntity.setData(result);
        return httpResponseEntity;
    }
    
    /**
     * 预览问卷时，根据id查询问卷
     * @param map
     * @return
     */
    @RequestMapping(value = "/queryQuestionnaireById", method = RequestMethod.POST, headers = "Accept=application/json")
    public HttpResponseEntity queryQuestionnaireById(@RequestBody(required = false) HashMap<String, Object> map) {
        HttpResponseEntity httpResponseEntity = new HttpResponseEntity();
        Map<String, String> result = questionnaireService.queryQuestionnaireById(map);
        httpResponseEntity.setCode(Constans.SUCCESS_CODE);
        httpResponseEntity.setData(result);
        return httpResponseEntity;
    }
	 
	 
	/**
	 * 添加项目时，检测项目下 是否有同dataId的问卷存在
	 * @param querList
	 * @param questionnaireEntity
	 * @return
	 */
	private boolean isHaveQuestion(List<Map<String, Object>> querList, QuestionnaireEntity questionnaireEntity) {
		if(querList == null) {
			return false;
		}
		else {
			String dataId = questionnaireEntity.getDataId();
			String questionName =questionnaireEntity.getQuestionName();
			for(Map<String, Object> map2 : querList) {
				String queryName = String.valueOf(map2.get("questionName"));
				if(questionName.equals(queryName)) {
					String queryDataId = String.valueOf(map2.get("dataId"));
					if(dataId.equals(queryDataId)) {
						return true;
					}
				}
			}
			return false;
		}
	}
	/**
	 * 修改问卷时，判断项目下是否已有同id，同名问卷（除了自身）
	 * @param querList
	 * @param questionnaireEntity
	 * @return
	 */
	private boolean isHaveIdQuestion(List<Map<String, Object>> querList,QuestionnaireEntity questionnaireEntity) {
		String dataId = questionnaireEntity.getDataId();
		String questionName = questionnaireEntity.getQuestionName();
		String id = questionnaireEntity.getId();
		for(Map<String, Object> map2 : querList) {
			String queryName = String.valueOf(map2.get("questionName"));
			if(questionName.equals(queryName)) {
				String queryDataId = String.valueOf(map2.get("dataId"));
				if(dataId.equals(queryDataId)) {
					String queryId = String.valueOf(map2.get("id"));
					if(!id.equals(queryId)) {
						return true;
					}
				}
			}
		}
		return false;
	}
	 
	 
}
