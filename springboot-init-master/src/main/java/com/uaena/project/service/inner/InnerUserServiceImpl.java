package com.uaena.project.service.inner;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.uaena.common.model.entity.User;
import com.uaena.common.service.InnerUserService;
import com.uaena.project.common.ErrorCode;
import com.uaena.project.exception.BusinessException;
import com.uaena.project.mapper.UserMapper;
import com.uaena.project.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboService;

import javax.annotation.Resource;

/**
 * @Author: sakura
 * @Date: 2023/3/26 10:31
 * @Description: 查看用户是否分配密钥
 * @Version 1.0
 */
@DubboService
public class InnerUserServiceImpl implements InnerUserService {

    @Resource
    private UserMapper userMapper;

    @Override
    public User getInvokeUser(String accessKey) {
        if(StringUtils.isAnyBlank(accessKey)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        QueryWrapper<User>queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("accessKey",accessKey);
        return userMapper.selectOne(queryWrapper);
    }
}
