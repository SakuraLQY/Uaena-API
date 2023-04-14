package com.uaena.project.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.uaena.common.model.entity.UserInterfaceInfo;


import java.util.List;


/**
 * UserInterfaceInfo的mapper
 *
 * @author huoyouri
 */
public interface UserInterfaceInfoMapper extends BaseMapper<UserInterfaceInfo> {

    //根据调用次数查询前几名的
    List<UserInterfaceInfo> listTopInvokeInterfaceInfo(int limit);

}




