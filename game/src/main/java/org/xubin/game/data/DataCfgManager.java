package org.xubin.game.data;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 数据配置管理器
 */

@Service
@Slf4j
public class DataCfgManager {
    @Autowired
    private DataCfgLoader loader;

    @PostConstruct
    public void init() throws Exception {
        loader.loadCfg();
    }

    // 获取整张表
    public Map<Long, Object> getDataCfg(String className) {
        return loader.getDataCfgMap().get(className);
    }

    // 通过id获取表的数据
    public Object getById(String className, long id) {
        Map<Long, Object> dataMap = loader.getDataCfgMap().get(className);
        if (dataMap == null) {
            return null;
        }
        return dataMap.get(id);
    }

    // 返回表中最大id
    public Object getMaxId(String className) {
        Map<Long, Object> dataMap = loader.getDataCfgMap().get(className);
        if (dataMap == null) {
            return null;
        }
        return dataMap.keySet().stream().max(Long::compareTo).orElse(null);
    }

    // 获取表的所有id
    public List<Long> getAllId(String className) {
        Map<Long, Object> dataMap = loader.getDataCfgMap().get(className);
        if (dataMap == null) {
            return null;
        }
        return List.copyOf(dataMap.keySet());
    }

    public List<Object> getAll(String className) {
        Map<Long, Object> dataMap = loader.getDataCfgMap().get(className);
        if (dataMap == null) {
            return null;
        }
        return List.copyOf(dataMap.values());
    }

}
