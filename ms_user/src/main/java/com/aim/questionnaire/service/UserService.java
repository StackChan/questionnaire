package com.aim.questionnaire.service;

import com.aim.questionnaire.common.utils.DateUtil;
import com.aim.questionnaire.common.utils.UUIDUtil;
import com.aim.questionnaire.dao.UserEntityMapper;
import com.aim.questionnaire.dao.entity.UserEntity;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;

/**
 * Created by wln on 2018\8\9 0009.
 */
@Service
public class UserService {

    @Autowired
    private UserEntityMapper userEntityMapper;


    /**
     * 查询用户列表（模糊搜索）
     * @param map
     * @return
     */
    @Cacheable(value="user", key="'queryUserList'")
    public PageInfo queryUserList(Map<String,Object> map) {
        String username="%";
        String name="";
        if(map.get("userName")!=null){
            name= (String) map.get("userName");
        }
        username+=name;
        username+="%";
        iniStatus();
        Map<String,Object> mapNew=new HashMap<>();
        mapNew.put("username",username);
        int pageNum=(int)map.get("pageNum");
        int pageSize=(int)map.get("pageSize");
        PageHelper.startPage(pageNum,pageSize);
        List<Map<String,Object>> list=userEntityMapper.queryUserList(mapNew);
        System.out.println("queryUserList方法被调用!");
        PageInfo<Map<String,Object>>page = new PageInfo<>(list);
        return page;
    }

    /**
     * 创建用户的基本信息
     * @param map
     * @return
     * 添加后,user:'queryUserList'缓存将被清除,以确保queryUserList时会重新从mysql数据库取得准确数据
     * 从而达成redis数据与mysql数据一致
     */
    @CacheEvict(value="user", key="'queryUserList")
    public int addUserInfo(Map<String,Object> map) {
        if(map.get("username") != null) {
            int userResult = userEntityMapper.queryExistUser(map);
            if(userResult != 0) {//用户名已经存在
                return 3;
            }
        }

        String id = UUIDUtil.getOneUUID();
        map.put("id",id);
        //创建时间
        Date date = DateUtil.getCreateTime();
        map.put("creationDate",date);
        map.put("lastUpdateDate",date);
        //创建人
        String user = "admin";
        map.put("createdBy",user);
        map.put("lastUpdatedBy",user);
        //前台传入的时间戳转换
        String startTimeStr = map.get("startTime").toString();
        String endTimeStr = map.get("stopTime").toString();
        Date startTime = DateUtil.getMyTime(startTimeStr);
        Date endTime = DateUtil.getMyTime(endTimeStr);
        map.put("startTime",startTime);
        map.put("stopTime",endTime);

        int result = userEntityMapper.addUserInfo(map);
        return result;
    }

    /**
     * 编辑用户的基本信息
     * @param map
     * @return
     * '@CacheEvict'作用同上
     */
    @CacheEvict(value="user", key="'queryUserList")
    public int modifyUserInfo(Map<String, Object> map) {
        Date date = DateUtil.getCreateTime();
        String startTimeStr = map.get("startTime").toString();
        String endTimeStr = map.get("stopTime").toString();
        Date startTime = DateUtil.getMyTime(startTimeStr);
        Date endTime = DateUtil.getMyTime(endTimeStr);

        String password = map.get("password").toString();
        map.replace("startTime",startTime);
        map.replace("stopTime",endTime);
        map.replace("password",password);
        map.put("lastUpdateDate",date);
        int result = userEntityMapper.modifyUserInfo(map);//返回修改过的user
        return result;
    }

    /**
     * 修改用户状态
     * @param map
     * @return
     * '@CacheEvict'作用同上
     */
    @CacheEvict(value="user", key="'queryUserList'")
    public int modifyUserStatus(Map<String, Object> map) {
        UserEntity userEntity=new UserEntity();
        userEntity.setId((String) map.get("id"));
        Map<String,Object> oldMap=userEntityMapper.selectUserInfoById(userEntity);
        String status= (String) oldMap.get("status");
        if(status.equals("1")){
            status="0";
        }else {
            status="1";
        }
        map.put("status",status);
        int result =userEntityMapper.modifyUserStatus(map);
        return result;
    }

    /**
     * 根据id查询用户信息
     * @param userEntity
     * @return
     */
    @Cacheable(value="user", key="#p0.id")
    public Map<String,Object> selectUserInfoById(UserEntity userEntity) {
        Map<String,Object> map=userEntityMapper.selectUserInfoById(userEntity);
        return map;
    }



    @Cacheable(value="user", key="'login:'+ #userEntity.username")
    public List<UserEntity> selectUserInfo(UserEntity userEntity){
        List<UserEntity> list=userEntityMapper.selectUserInfo(userEntity);
        return list;
    }

    /**
     * 删除用户信息
     * 删除操作若不能成功,将进行回滚
     * 确保redis数据库与mysql数据库都进行了删除
     * 从而确保redis数据与mysql数据一致
     * 删除后,user缓存将被整个清除(而非某个具体键值),以确保query和select时会重新从mysql数据库取得准确数据
     * 从而达成redis数据与mysql数据一致
     */
    @Transactional  //进行事务管理
    @CacheEvict(value="user",allEntries = true)
    public int deleteUserInfoById(UserEntity userEntity) {
        userEntityMapper.deleteUserInfoById(userEntity);
        return 0;
    }

    /**
     * 根据用户名精准查询用户
     */
    @Cacheable(value="user", key="#userName")
    public UserEntity queryByUserName(String userName) {
        return userEntityMapper.selectAllByName(userName);
    }


    /**
     * 更新用户权限
     */
    public void iniStatus() {
        long current = DateUtil.getCreateTime().getTime();//获得当前时间
        String username = "%%";
        Map<String, Object> newMap = new HashMap<>();
        newMap.put("username", username);
        List<Map<String, Object>> list = userEntityMapper.queryUserList(newMap);//查询所有User列表
        for (Map<String, Object> map : list) {
            if (map.get("status").equals("1")) {
                LocalDateTime stopTime = (LocalDateTime) map.get("stop_time");
                Long milliSecond = stopTime.toInstant(ZoneOffset.of("+8")).toEpochMilli();
                if (current > milliSecond) {
                    map.put("status", 0);
                    userEntityMapper.modifyUserStatus(map);
                }
            }
        }

    }
}
