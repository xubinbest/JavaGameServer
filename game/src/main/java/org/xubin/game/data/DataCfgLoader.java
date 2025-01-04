package org.xubin.game.data;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.context.annotation.Configuration;

import java.io.File;
import java.io.FileInputStream;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Configuration
@Slf4j
public class DataCfgLoader {
    @Getter
    private final Map<String, Map<Long, Object>> dataCfgMap = new ConcurrentHashMap<>();

    public void loadCfg() throws Exception {
        // 获取game/data/ 下所有的excel文件
        File file = new File("game/data/");
        File[] files = file.listFiles();
        for (File f : files) {
            if(f.getName().endsWith(".xlsx")) {
                loadCfg(f.getPath());
            }
        }
    }

    public void loadCfg(String filePath) throws Exception {
        // 读取 Excel 文件
        FileInputStream fis = new FileInputStream(new File(filePath));
        Workbook workbook = new XSSFWorkbook(fis);
        // 读取第一个 Sheet
        Sheet sheet = workbook.getSheetAt(0);

        // 获取类名（第一行第一列）
        String className = sheet.getRow(0).getCell(0).getStringCellValue();
        Class<?> clazz = Class.forName("org.xubin.game.data.data." + className);

        // 获取表头（字段名）
        Row headerRow = sheet.getRow(1);
        int fieldCount = headerRow.getLastCellNum();

        Map<Long, Object> dataMap = new ConcurrentHashMap<>();
        // 遍历每一行（从第四行开始为数据）
        for (int i = 3; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            if (row == null) {
                continue;
            }

            // 创建类的实例
            Object obj = clazz.getDeclaredConstructor().newInstance();
            long id = (long) row.getCell(0).getNumericCellValue();

            // 遍历每一列，填充字段
            for (int j = 0; j < fieldCount; j++) {
                Cell cell = row.getCell(j);
                if (cell == null) {
                    continue;
                }

                // 获取字段名和字段类型
                String fieldName = headerRow.getCell(j).getStringCellValue();
                Field field = clazz.getDeclaredField(fieldName);
                field.setAccessible(true);

                // 根据字段类型设置值
                switch (cell.getCellType()) {
                    case STRING:
                        Object value = parseStringField(cell.getStringCellValue());
                        field.set(obj, value);
                        break;
                    case NUMERIC:
                        if (field.getType() == int.class) {
                            field.set(obj, (int) cell.getNumericCellValue());
                        } else if (field.getType() == double.class) {
                            field.set(obj, cell.getNumericCellValue());
                        } else if(field.getType() == long.class) {
                            field.set(obj, (long) cell.getNumericCellValue());
                        }
                        break;
                    default:
                        break;
                }
            }
            dataMap.put(id, obj);
        }
        dataCfgMap.put(className, dataMap);
    }

    private Object parseStringField(String value) {
        if(value.startsWith("[")) {
            JSONArray jsonArray = JSON.parseArray(value);
            Map<String, Long> map = new HashMap<>();
            for (int i = 0; i < jsonArray.size(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                for (String key : jsonObject.keySet()) {
                    map.put(key, jsonObject.getLong(key));
                }
            }
            return map;
        }
        return value;
    }
}
