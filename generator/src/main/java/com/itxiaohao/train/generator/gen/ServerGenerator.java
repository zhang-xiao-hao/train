package com.itxiaohao.train.generator.gen;

import com.itxiaohao.train.generator.util.DbUtil;
import com.itxiaohao.train.generator.util.Field;
import com.itxiaohao.train.generator.util.FreemarkerUtil;
import freemarker.template.TemplateException;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

import java.io.File;
import java.io.IOException;
import java.util.*;


public class ServerGenerator {
    static boolean readOnly = true;
    static String vuePath = "admin/src/views/main/";
    static String serverPath = "[module]/src/main/java/com/itxiaohao/train/[module]/";
    static String pomPath = "generator/pom.xml";
    static String module = "";
    static {
        new File(serverPath).mkdirs();
    }

    /**
     * 读取pom文件的configurationFile（当前持久层的代码生成器文件路径）
     * @return
     * @throws DocumentException
     */
    private static String getGeneratorPath() throws DocumentException {
        SAXReader saxReader = new SAXReader();
        Map<String, String> map = new HashMap<String, String>();
        map.put("pom", "http://maven.apache.org/POM/4.0.0");
        saxReader.getDocumentFactory().setXPathNamespaceURIs(map);
        Document document = saxReader.read(pomPath);
        Node node = document.selectSingleNode("//pom:configurationFile");
        System.out.println(node.getText());
        return node.getText();
    }

    /**
     * 获取参数
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        // 获取mybatis-generator
        String generatorPath = getGeneratorPath();
        // 得到模块名(member)
        module = generatorPath.replace("src/main/resources/generator-config-", "").replace(".xml", "");
        System.out.println("module: " + module);
        // 替换module
        serverPath = serverPath.replace("[module]", module);
        System.out.println("servicePath:" + serverPath);
        // 读取table节点下的属性（表名和对应的实体类名）
        Document document = new SAXReader().read("generator/" + generatorPath);
        Node table = document.selectSingleNode("//table");
        System.out.println(table);
        Node tableName = table.selectSingleNode("@tableName");
        Node domainObjectName = table.selectSingleNode("@domainObjectName");
        System.out.println(tableName.getText() + "/" + domainObjectName.getText());

        // 读取table节点下的属性，为DbUtil设置数据源
        Node connectionURL = document.selectSingleNode("//@connectionURL");
        Node userId = document.selectSingleNode("//@userId");
        Node password = document.selectSingleNode("//@password");
        System.out.println("url: " + connectionURL.getText());
        System.out.println("user: " + userId.getText());
        System.out.println("password: " + password.getText());
        DbUtil.url = connectionURL.getText();
        DbUtil.user = userId.getText();
        DbUtil.password = password.getText();

        // 获取替换模板中的参数
        // Domain=Member
        String Domain = domainObjectName.getText();
        // domain=member
        String domain = Domain.substring(0, 1).toLowerCase() + Domain.substring(1);
        // url名（如果是两个单词以上，使用“-”连接）
        String do_main = tableName.getText().replaceAll("_", "-");
        // 表中文名
        String tableNameCn = DbUtil.getTableComment(tableName.getText());
        // 表中列信息
        List<Field> fieldList = DbUtil.getColumnByTableName(tableName.getText());
        // 获取对应的java类型（去重）
        Set<String> typeSet = getJavaTypes(fieldList);
        // 组装参数
        Map<String, Object> param = new HashMap<>();
        param.put("Domain", Domain);
        param.put("domain", domain);
        param.put("do_main", do_main);
        param.put("module", module);
        param.put("tableNameCn", tableNameCn);
        param.put("fieldList", fieldList);
        param.put("typeSet", typeSet);
        param.put("readOnly", readOnly);
        System.out.println("组装参数："+ param);
        // 生成service
//        gen(Domain, param, "service", "service");
        // 生成用户controller
//        gen(Domain, param, "controller", "adminController");
        // 生成后台controller
//        gen(Domain, param, "controller/admin", "adminController");
        // 生成req
        gen(Domain, param, "req", "saveReq");
//        gen(Domain, param, "req", "queryReq");
        // 生成resp
        gen(Domain, param, "resp", "queryResp");
        // 生成用户vue
//        genVue(do_main, param, "vue");
        // 生成后台vue
        genVue(do_main, param, "adminVue");
    }
    private static void genVue(String do_main, Map<String, Object> param, String target) throws IOException, TemplateException {
        FreemarkerUtil.initConfig(target + ".ftl");
        new File(vuePath + module).mkdirs();
        String fileName = vuePath + module + "/" + do_main + ".vue";
        System.out.println("开始生成：" + fileName);
        FreemarkerUtil.generator(fileName, param);
    }
    /**
     * 通过参数和ftl模板，生成对应文件
     * @param Domain
     * @param param
     * @param target
     * @throws IOException
     * @throws TemplateException
     */
    private static void gen(String Domain, Map<String, Object> param, String packageName, String target) throws IOException, TemplateException {
        FreemarkerUtil.initConfig(target + ".ftl");
        String toPath = serverPath + packageName + "/";
        new File(toPath).mkdirs();
        String Target = target.substring(0, 1).toUpperCase() + target.substring(1);
        String fileName = toPath + Domain + Target + ".java";
        System.out.println("开始生成文件：" + fileName);
        FreemarkerUtil.generator(fileName, param);
    }
    /**
     * 获取所有的java类型，使用set去重
     */
    private static Set<String> getJavaTypes(List<Field> fieldList){
        Set<String> set = new HashSet<>();
        for (Field field : fieldList) {
            set.add(field.getJavaType());
        }
        return set;
    }
}
