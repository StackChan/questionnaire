package com.aim.questionnaire.service;


import com.aim.questionnaire.common.utils.DateUtil;
import com.aim.questionnaire.common.utils.UUIDUtil;
import com.aim.questionnaire.dao.QuestionnaireEntityMapper;
import com.aim.questionnaire.dao.entity.QuestionnaireEntity;
import org.apache.commons.mail.EmailException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 
 * @author Youcf
 *
 */
@Service
public class QuestionnaireService {
	@Autowired
	private QuestionnaireEntityMapper questionnaireEntityMapper;
	@Autowired
    private ProjectService projectService;
	@Autowired
	private EmailService emailService;

	/**
     * 根据项目id查询发布中问卷数量
     * @param questionnaireEntity
     * @return
     */
	public int queryReleaseQuestionnaireCount(QuestionnaireEntity questionnaireEntity) {
		int count = questionnaireEntityMapper.queryReleaseQuestionnaireCount(questionnaireEntity);
		return count;
	}
	
	/**
	 * 增加问卷
	 */
//	public void addQuestionnaire(HashMap<String, Object> map) {
//		String id = UUIDUtil.getOneUUID();
//		map.put("id",id);
//		Date date = DateUtil.getCreateTime();
//		map.put("creationDate",date);
//        map.put("lastUpdateDate",date);
//        //创建人
//        String user = map.get("user").toString();
//        map.put("createdBy",user);
//        map.put("lastUpdatedBy",user);
//        //前台传入的时间戳转换
//        String startTimeStr = map.get("startTime").toString();
//        String endTimeStr = map.get("endTime").toString();
//        Date startTime = DateUtil.getMyTime(startTimeStr);
//        Date endTime = DateUtil.getMyTime(endTimeStr);
//        map.put("startTime",startTime);
//        map.put("endTime",endTime);
//		questionnaireEntityMapper.addQuestionnaire(map);
//		
//		
//	}
    public void addQuestionnaire(QuestionnaireEntity questionnaireEntity) {
    	String user = questionnaireEntity.getCreatedBy();
        String id = UUIDUtil.getOneUUID();
        questionnaireEntity.setId(id);
        // 获取用户信息
        questionnaireEntity.setCreatedBy(user);
        questionnaireEntity.setLastUpdatedBy(user);
        // 获取当前时间
        Date date = DateUtil.getCreateTime();
        questionnaireEntity.setCreationDate(date);
        questionnaireEntity.setLastUpdateDate(date);

        questionnaireEntityMapper.insertSelective(questionnaireEntity);
        //return result;
    }
	
	/**
	 * 根据项目id查询所有问卷
	 * @param parentId
	 * @return
	 */
	public  List<Map<String,Object>> queryQuestionListByProjectId(String parentId){
		List<Map<String,Object>> list = questionnaireEntityMapper.queryQuestionListByProjectId(parentId);
		return list;
	}
	/**
	 * 根据项目id查询问卷个数
	 */
	public int selectQuestionCountByProjectId(String parentId) {
		int count = questionnaireEntityMapper.selectQuestionCountByProjectId(parentId);
		return count;
	}
	/**
	 * 根据问卷id查询问卷isStop状态
	 */
	public String queryQuestionnaireIsStopStatus(String id) {
		String questionStop = questionnaireEntityMapper.queryQuestionnaireIsStopStatus(id);
		return questionStop;
	}
	/**
	 * 根据问卷id修改问卷
	 */
	public void modifyQuestionnaireInfo(QuestionnaireEntity questionnaireEntity) {
		Date date = DateUtil.getCreateTime();
		questionnaireEntity.setLastUpdateDate(date);
		questionnaireEntityMapper.modifyQuestionnaireInfo(questionnaireEntity);
	}
	/**
	 * 设计问卷
	 * @param map
	 * @return
	 */
    public int modifyQuestionnaire(HashMap<String, Object> map) {
        Date date = DateUtil.getCreateTime();
        map.put("lastUpdateDate", date);
        int result =questionnaireEntityMapper.modifyQuestionnaire(map);
        System.out.print(result);
        return result;
    }
	/**
	 * 根据问卷id查询问卷
	 */
	public QuestionnaireEntity selectByPrimaryKey(String id) {
		QuestionnaireEntity questionnaireEntity = questionnaireEntityMapper.selectByPrimaryKey(id);
		return questionnaireEntity;
	}
	/**
	 * 根据id删除问卷
	 */
	public void deleteByPrimaryKey(String id) {
		questionnaireEntityMapper.deleteByPrimaryKey(id);
	}
	
	/**
	 * 
	 * @param id
	 * @return
	 */
	public QuestionnaireEntity queryQuestContextEnd(String id){
        QuestionnaireEntity questionnaireEntity = questionnaireEntityMapper.queryQuestContextEnd(id);
        return questionnaireEntity;
    }
	
	/**
	 * 
	 * @param map
	 * @return
	 */
    public int addSendQuestionnaire(HashMap<String, Object> map){
        map.put("questionStop","1");
        // 获取当前时间
        Date date = DateUtil.getCreateTime();
        map.put("lastUpdateDate",date);
        map.put("releaseTime",date);
        int total = 0;
        if(map.get("sendType").toString().equals("0")){

		} else if(map.get("sendType").toString().equals("1"))
		{
			String emailTitle = map.get("emailTitle").toString();
			String context = map.get("context").toString();
			List<Map<String, Object>> send = (List<Map<String, Object>>) map.get("sendInfo");
			total = send.size();
			for (Map<String, Object> person : send
			) {
				try {
					//
					String contextTemp = context;
					String name = person.get("answerName").toString();
					String result = contextTemp.replaceAll("【联系人姓名】",name);
					String email = person.get("answerEmail").toString();
					//TODO:改成服务器ip
					String result1 = result.replaceAll("【填写问卷地址】","127.0.0.1:56010"+"/pages/previewQuestionnaire.html?id="+map.get("questionId").toString()+"&e="+email);
					emailService.sendMail(email, emailTitle, result1, false);
				} catch (EmailException e) {
					e.printStackTrace();
				}
			}
		}
		map.put("answerTotal",total);
        int result = questionnaireEntityMapper.addSendQuestionnaire(map);
        return result;
    }
    
    /**
     * 查询历史问卷
     * 
     * @param map
     * @return
     */
    public List<Map<String,Object>> queryHistoryQuestionnaire(HashMap<String, Object> map) {
        List<Map<String,Object>> resultList = questionnaireEntityMapper.queryHistoryQuestionnaire(map);
        return resultList;
    }
    
    /**
     * 查询问卷模板
     * @param dataId
     * @return
     */
    public List<Map<String,Object>> queryQuestionnaireMould(String dataId) {
        List<Map<String,Object>> resultList = questionnaireEntityMapper.queryQuestionnaireMould(dataId);
        return resultList;
    }
    /**
     * 修改问卷状态为4
     * @param map
     * @return
     */
    public int modifyQuestionnaireStatus(HashMap<String, Object> map) {
        int result = questionnaireEntityMapper.modifyQuestionnaireStatus(map);
        return result;
    }
    /**
     * 查询历史问卷
     * @param map
     * @return
     */
    public List<Object> queryQuestionnaireList(HashMap<String, Object> map) {
        List<Object> resultList = new ArrayList<Object>();
//        if("".equals(map.get("questionName").toString())){
//            map.put("questionName", null);
//        }
        List<Map<String,Object>> tempResult = questionnaireEntityMapper.queryQuestionnaireList(map);
        for(Map<String,Object> tempQuest : tempResult) {
            String projectName = projectService.queryProjectName(tempQuest.get("projectId").toString());
            tempQuest.put("projectName",projectName);
            resultList.add(tempQuest);
        }
        return resultList;
    }
    
    /**
     * 修改历史问卷状态
     * @param map
     * @return
     */
    public int modifyHistoryQuestionnaireStatus(HashMap<String, Object> map){
        map.put("questionStop","1");
        String endTimeStr = map.get("endTime").toString();
        Date endTime = DateUtil.getMyTime(endTimeStr);
        map.put("endTime",endTime);
        int result = questionnaireEntityMapper.modifyHistoryQuestionnaireStatus(map);
        return result;
    }
    
    /**
     * 预览问卷，根据问卷id查询问卷
     * 
     * @param map
     * @return
     */
    public Map<String, String> queryQuestionnaireById(HashMap<String, Object> map) {
        Map<String, String> result = questionnaireEntityMapper.queryQuestionnaireById(map);
        return result;
    }

	public int pauseQuestionnaire(Map<String, Object> map) {
		int result = questionnaireEntityMapper.pauseQuestionnaire(map);
		return result;
	}


	public Map<String, Object> queryQuestionnaireCount(HashMap<String, Object> map) {
		Map<String , Object> result = questionnaireEntityMapper.queryQuestionnaireCount(map);

		int count = result.get("questionCount")==null ? 0:(int)result.get("questionCount");
		if(result.get("questionCount")==null) result.put("questionCount",count);
		result.replace("questionCount",count);
		String questionTotal = result.get("answerTotal").toString();
		int total = Integer.valueOf(questionTotal);
		total =total==0?1:total;
		double answerRate = (double) count/(double) total;
		result.put("answerRate",answerRate);
		result.put("effectiveAnswer",count);

    	return result;
	}

	public int addAnswerQuestionnaire(HashMap<String, Object> map) {
    	Map<String , Object> map1 = questionnaireEntityMapper.queryQuestionnaireInfoById(map.get("questionId").toString());
    	Integer count = (Integer) map1.get("questionCount");
    	if (count==null){
    		count = 0;
		}else {
    		count += 1;
		}
    	map.put("questionCount",count);
		int result = questionnaireEntityMapper.addAnswerQuestionnaire(map);
		return result;

	}
}
