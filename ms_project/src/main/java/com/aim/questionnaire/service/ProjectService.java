package com.aim.questionnaire.service;

import com.aim.questionnaire.common.utils.DateUtil;
import com.aim.questionnaire.common.utils.UUIDUtil;
import com.aim.questionnaire.dao.ProjectEntityMapper;
import com.aim.questionnaire.dao.entity.ProjectEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by wln on 2018\8\6 0006.
 */
@Service
public class ProjectService {

    @Autowired
    private ProjectEntityMapper projectEntityMapper;


    /**
     * 添加项目
     * @param projectEntity
     * @return
     */
    public int addProjectInfo(ProjectEntity projectEntity,String user) {
    	String id = UUIDUtil.getOneUUID();
    	projectEntity.setId(id);
    	projectEntity.setUserId(user);
    	Date date = DateUtil.getCreateTime();
    	projectEntity.setCreationDate(date);
    	projectEntity.setLastUpdateDate(date);
        projectEntityMapper.insertSelective(projectEntity);
        return 0;
    }

    /**
     * 修改项目
     * @param projectEntity
     * @return
     */
    public int modifyProjectInfo(ProjectEntity projectEntity,String user) {
        Date date = DateUtil.getCreateTime();
        projectEntity.setLastUpdateDate(date);
        projectEntity.setLastUpdatedBy(user);
        int result = projectEntityMapper.updateByPrimaryKeySelective(projectEntity);
        return result;
    }

    /**
     * 删除项目
     * @param projectEntity
     * @return
     */
    public int deleteProjectById(ProjectEntity projectEntity) {
    	int count = projectEntityMapper.deleteProjectById(projectEntity.getId());
        return count;
    }

    /**
     * 查询项目列表
     * @param projectEntity
     * @return
     */
    public List<Map<String, Object>> queryProjectList(ProjectEntity projectEntity) {
        List<Map<String, Object>> resultList = projectEntityMapper.queryProjectList(projectEntity);
        
        return resultList;
    }

    /**
     * 查询全部项目的名字接口
     * @return
     */
    public List<Map<String,Object>> queryAllProjectName() {
        return null;
    }
    
    /**
     * 根据项目名字精准查询是否存在
     * @author Youcf
     */
    public boolean queryProjectEntityIsExit(ProjectEntity projectEntity) {
		ProjectEntity projectEntity2 = projectEntityMapper.queryProjectEntity(projectEntity);
		if(projectEntity2==null) {
			return false;
		}
		else {
			return true;
		}
	}
    /**
     * 根据名字查询项目
     * @author Youcf
     */
    public ProjectEntity queryProjectEntity(ProjectEntity projectEntity) {
    	return projectEntityMapper.queryProjectEntity(projectEntity);
    }
    
    /**
     * 根据id修改项目
     * @author Youcf
     */
    public int updateProjectById(ProjectEntity projectEntity) {
    	Date date = DateUtil.getCreateTime();
        projectEntity.setLastUpdateDate(date);
    	int count = projectEntityMapper.updateProjectById(projectEntity);
    	return count;
    }
    
    /**
     * 查询项目名称
     * @author Youcf
     * @param id
     * @return
     */
    public String queryProjectName(String id) {
        String projectName = projectEntityMapper.queryProjectName(id);
        return projectName;
    }
}
