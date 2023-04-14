package com.uaena.project.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.uaena.common.model.entity.InterfaceInfo;


/**
* @author 裴承林
* @description 针对表【interface_info】的数据库操作Service
* @createDate 2023-03-12 17:16:08
*/
public interface InterfaceInfoService extends IService<InterfaceInfo> {
    void validInterfaceInfo(InterfaceInfo interfaceInfo, boolean add);
}
