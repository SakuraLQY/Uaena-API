package com.uaena.common.service;


import com.uaena.common.model.entity.InterfaceInfo;

/**
* @author 裴承林
* @description 针对表【interface_info】的数据库操作Service
* @createDate 2023-03-12 17:16:08
*/
public interface InnerInterfaceInfoService {
    /**
     * 从数据库中查询模拟接口是否存在（请求路径、请求方法、请求参数）
     */
    InterfaceInfo getInterfaceInfo(String path, String method);

}
