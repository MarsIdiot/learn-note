package cn.itcast.crm.mapper;

import cn.itcast.crm.pojo.BaseDict;

import java.util.List;

public interface BaseDictMapper {

    /**
     * 根据typeCode查询数据字典
     * @param typeCode
     * @return List<BaseDict>
     */
    List<BaseDict> queryBaseDictByDictTypeCode(String typeCode);
}
