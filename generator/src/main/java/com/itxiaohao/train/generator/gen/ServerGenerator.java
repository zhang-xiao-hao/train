package com.itxiaohao.train.generator.gen;

import com.itxiaohao.train.generator.util.FreemarkerUtil;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

import java.io.File;
import java.util.HashMap;
import java.util.Map;


public class ServerGenerator {
    static String servicePath = "[module]/src/main/java/com/itxiaohao/train/[module]/service/";

    static String pomPath = "generator/pom.xml";
    static {
        new File(servicePath).mkdirs();
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

    public static void main(String[] args) throws Exception {
        // 获取mybatis-generator
        String generatorPath = getGeneratorPath();
        // 得到模块名(member)
        String module = generatorPath.replace("src/main/resources/generator-config-", "").replace(".xml", "");
        System.out.println("module: " + module);
        // 替换module
        servicePath = servicePath.replace("[module]", module);
        System.out.println("servicePath:" + servicePath);
        // 读取table节点（表名和对应的实体类名）
        Document document = new SAXReader().read("generator/" + generatorPath);
        Node table = document.selectSingleNode("//table");
        System.out.println(table);
        Node tableName = table.selectSingleNode("@tableName");
        Node domainObjectName = table.selectSingleNode("@domainObjectName");
        System.out.println(tableName.getText() + "/" + domainObjectName.getText());

        // 获取替换模板中的参数
        // Domain=Member
        String Domain = domainObjectName.getText();
        // domain=member
        String domain = Domain.substring(0, 1).toLowerCase() + Domain.substring(1);
        // 表名
        String do_main = tableName.getText().replaceAll("_", "-");
        // 组装参数
        Map<String, Object> param = new HashMap<>();
        param.put("Domain", Domain);
        param.put("domain", domain);
        param.put("do_main", do_main);
        System.out.println("组装参数："+ param);

        FreemarkerUtil.initConfig("service.ftl");
        FreemarkerUtil.generator(servicePath + Domain + "Service.java", param);
    }
}
