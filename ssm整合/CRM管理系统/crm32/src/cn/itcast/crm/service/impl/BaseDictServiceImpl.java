package cn.itcast.crm.service.impl;

import cn.itcast.crm.mapper.BaseDictMapper;
import cn.itcast.crm.pojo.BaseDict;
import cn.itcast.crm.service.BaseDictService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BaseDictServiceImpl implements BaseDictService {
    @Autowired
    private BaseDictMapper baseDictMapper;

    public List<BaseDict> queryBaseDictByDictTypeCode(String typeCode){

        return baseDictMapper.queryBaseDictByDictTypeCode(typeCode);
    }
}
