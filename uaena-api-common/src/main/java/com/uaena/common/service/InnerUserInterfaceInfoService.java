package com.uaena.common.service;



/**
 * 用户接口信息服务
 *
 * @author huoyouri
 */
public interface InnerUserInterfaceInfoService {

    /**
     * 调用接口统计
     */
    boolean invokeCount(long interfaceInfoId, long userId);
}
