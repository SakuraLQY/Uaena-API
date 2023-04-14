package com.uaena.project.service.inner;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.uaena.common.model.entity.InterfaceInfo;
import com.uaena.common.service.InnerInterfaceInfoService;
import com.uaena.project.common.ErrorCode;
import com.uaena.project.exception.BusinessException;
import com.uaena.project.mapper.InterfaceInfoMapper;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboService;

import javax.annotation.Resource;

/**
 * @Author: sakura
 * @Date: 2023/3/26 10:13
 * @Description: 查询请求接口是否存在
 * DubboService--->代表服务注册
 * @Version 1.0
 */
@DubboService
public class InnerInterfaceInfoServiceImpl implements InnerInterfaceInfoService {
    @Resource
    private InterfaceInfoMapper interfaceInfoMapper;
    @Override
    public InterfaceInfo getInterfaceInfo(String url, String method) {
        if(StringUtils.isAnyBlank(url,method)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        //去数据库进行查询
        QueryWrapper<InterfaceInfo>qw = new QueryWrapper<>();
        qw.eq("url",url);
        qw.eq("method",method);
        return interfaceInfoMapper.selectOne(qw);
    }
}
