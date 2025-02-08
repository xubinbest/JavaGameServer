package org.xubin.game.tools;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;

public class ExcelToJavaClassGenerator {

    /**
     * 根据 Excel 文件生成 Java 类并写入到指定路径
     *
     * @param filePath  Excel 文件路径
     * @param outputDir 输出目录路径
     */
    public static void generateJavaClass(String filePath, String outputDir) throws Exception {
        // 读取 Excel 文件
        FileInputStream fis = new FileInputStream(new File(filePath));
        Workbook workbook = new XSSFWorkbook(fis);
        // 读取第一个 Sheet
        Sheet sheet = workbook.getSheetAt(0);

        // 获取类名（第一行第一列）
        String className = sheet.getRow(0).getCell(0).getStringCellValue();

        // 获取字段名（第二行）
        Row fieldRow = sheet.getRow(1);

        // 获取字段类型（第三行）
        Row typeRow = sheet.getRow(2);

        // 拼接生成 Java 类代码
        StringBuilder classCode = new StringBuilder();
        classCode.append("package org.xubin.game.data.data;\n\n");
        classCode.append("import lombok.Data;\n\n@Data\n");
        classCode.append("public class ").append(className).append(" {\n\n");

        for (int i = 0; i < fieldRow.getLastCellNum(); i++) {
            String fieldName = fieldRow.getCell(i).getStringCellValue();
            String fieldType = typeRow.getCell(i).getStringCellValue();

            // 转换 Excel 中的类型为 Java 类型
            String javaType = convertToJavaType(fieldType);

            // 添加字段
            classCode.append("    private ").append(javaType).append(" ").append(fieldName).append(";\n");
        }

        classCode.append("\n");

        // 生成 Getter 和 Setter 方法
//        for (int i = 0; i < fieldRow.getLastCellNum(); i++) {
//            String fieldName = fieldRow.getCell(i).getStringCellValue();
//            String fieldType = typeRow.getCell(i).getStringCellValue();
//            String javaType = convertToJavaType(fieldType);
//
//            // 首字母大写，用于生成方法名
//            String capitalizedFieldName = fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
//
//            // 生成 Getter 方法
//            classCode.append("    public ").append(javaType).append(" get").append(capitalizedFieldName).append("() {\n");
//            classCode.append("        return ").append(fieldName).append(";\n");
//            classCode.append("    }\n\n");
//
//            // 生成 Setter 方法
//            classCode.append("    public void set").append(capitalizedFieldName).append("(").append(javaType).append(" ").append(fieldName).append(") {\n");
//            classCode.append("        this.").append(fieldName).append(" = ").append(fieldName).append(";\n");
//            classCode.append("    }\n\n");
//        }

        classCode.append("}");

        // 输出生成的类到指定路径
        writeClassToFile(outputDir, className, classCode.toString());

        workbook.close();
        fis.close();
    }

    /**
     * 将生成的 Java 类代码写入到指定路径
     *
     * @param outputDir 输出目录路径
     * @param className 类名
     * @param classCode 类代码
     */
    private static void writeClassToFile(String outputDir, String className, String classCode) throws IOException {
        // 创建输出目录（如果不存在）
        File dir = new File(outputDir);
        if (!dir.exists()) {
            if (!dir.mkdirs()) {
                throw new IOException("无法创建输出目录: " + outputDir);
            }
        }

        // 创建 Java 文件
        File javaFile = new File(dir, className + ".java");

        // 写入类代码到文件
        try (FileWriter writer = new FileWriter(javaFile)) {
            writer.write(classCode);
        }

        System.out.println("Java 类已生成: " + javaFile.getAbsolutePath());
    }

    /**
     * 将 Excel 中的类型转换为 Java 类型
     *
     * @param excelType Excel 类型
     * @return Java 类型
     */
    private static String convertToJavaType(String excelType) {
        switch (excelType.toLowerCase()) {
            case "int":
                return "int";
            case "string":
                return "Object";
            case "float":
                return "float";
            case "double":
                return "double";
            case "long":
                return "long";
            case "boolean":
                return "boolean";
            default:
                // 默认类型
                return "Object";
        }
    }

    public static void main(String[] args) {
        try {
            // 获取game/data/ 下所有的excel文件
            File file = new File("data/");
            File[] files = file.listFiles();

            // 输出目录路径
            String outputDir = "src/main/java/org/xubin/game/data/data/";

            for (File f : files) {
                if (f.getName().endsWith(".xlsx")) {
                    // Excel 文件路径
                    String filePath = f.getPath();
                    // 生成 Java 类
                    generateJavaClass(filePath, outputDir);
                }
            }
            System.out.println("Java 类生成完毕");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

