package com.aim.questionnaire.controller;

import com.aim.questionnaire.beans.HttpResponseEntity;
import com.aim.questionnaire.common.Constans;
import com.aim.questionnaire.dao.entity.ModelEntity;
import com.aim.questionnaire.service.ModelService;
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
 * 
 * @author shan csk
 *
 */
@RestController
@RequestMapping("/project")
public class ModelController {
	
	private final Logger logger = LoggerFactory.getLogger(ProjectController.class);
	
	@Autowired
	ModelService modelService;
	
	/**
	 * 
	 * @param modelEntity
	 * @return
	 */
	@RequestMapping(value = "/queryAllDataType",method = RequestMethod.POST, headers = "Accept=application/json")
	 public HttpResponseEntity queryAllDataType(@RequestBody ModelEntity modelEntity) {
		 HttpResponseEntity httpResponseEntity = new HttpResponseEntity();
		 int parentId = modelEntity.getParentId();
		 List<Map<String, Object>> list = modelService.queryNameByParentId(parentId);
		 httpResponseEntity.setCode(Constans.SUCCESS_CODE);
		 httpResponseEntity.setData(list);
		 httpResponseEntity.setMessage(null);
		 return httpResponseEntity;
	 }
	
}
