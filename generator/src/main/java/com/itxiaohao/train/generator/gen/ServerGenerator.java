package com.itxiaohao.train.generator.gen;

import com.itxiaohao.train.generator.util.FreemarkerUtil;
import freemarker.template.TemplateException;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


public class ServerGenerator {
    static String serverPath = "[module]/src/main/java/com/itxiaohao/train/[module]/";

    static String pomPath = "generator/pom.xml";
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
        String module = generatorPath.replace("src/main/resources/generator-config-", "").replace(".xml", "");
        System.out.println("module: " + module);
        // 替换module
        serverPath = serverPath.replace("[module]", module);
        System.out.println("servicePath:" + serverPath);
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
        // url名（如果是两个单词以上，使用“-”连接）
        String do_main = tableName.getText().replaceAll("_", "-");
        // 组装参数
        Map<String, Object> param = new HashMap<>();
        param.put("Domain", Domain);
        param.put("domain", domain);
        param.put("do_main", do_main);
        System.out.println("组装参数："+ param);

        gen(Domain, param, "service");
        gen(Domain, param, "controller");
    }

    /**
     * 通过参数和ftl模板，生成对应文件
     * @param Domain
     * @param param
     * @param target
     * @throws IOException
     * @throws TemplateException
     */
    private static void gen(String Domain, Map<String, Object> param, String target) throws IOException, TemplateException {
        FreemarkerUtil.initConfig(target + ".ftl");
        String toPath = serverPath + target + "/";
        new File(toPath).mkdirs();
        String Target = target.substring(0, 1).toUpperCase() + target.substring(1);
        String fileName = toPath + Domain + Target + ".java";
        System.out.println("开始生成文件：" + fileName);
        FreemarkerUtil.generator(fileName, param);
    }
}
