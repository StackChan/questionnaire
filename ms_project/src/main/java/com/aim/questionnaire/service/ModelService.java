package com.aim.questionnaire.service;

import com.aim.questionnaire.dao.ModelEntityMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class ModelService {
	@Autowired
	private ModelEntityMapper modelEntityMapper;
	
	/**
	 * 根据parentId查询id和name

	 */
	public List<Map<String,Object>> queryNameByParentId(int parentId){
		List<Map<String,Object>> list = modelEntityMapper.queryNameByParentId(parentId);
		return list;
	}
}
