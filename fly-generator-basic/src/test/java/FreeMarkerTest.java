import com.fly.model.MainTemplateConfig;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.junit.Test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.*;

public class FreeMarkerTest {

    @Test
    public void testFreeMarkerConfig() throws IOException, TemplateException {
        // 第一步：创建一个Configuration对象，直接new一个对象。构造方法的参数就是FreeMarker对于的版本号。
        Configuration configuration = new Configuration(Configuration.VERSION_2_3_21);
        // 第二步：设置模板文件所在的路径。
        configuration.setDirectoryForTemplateLoading(new File("src/main/resources/templates"));
        // 第三步：设置模板文件使用的字符集。一般就是utf-8.
        configuration.setDefaultEncoding("utf-8");

        // 第四步：加载模板文件，创建一个模板对象。
        Template template = configuration.getTemplate("myweb.html.ftl");

        // 第五步：创建一个模板使用的数据集，可以是pojo也可以是map。一般是Map。
        HashMap<String, Object> model = new HashMap<>();
        model.put("user", "fly");
        model.put("currentYear", 2023);

        List<HashMap<String, Object>> menuList = new ArrayList<>();
        HashMap<String, Object> menuItem1 = new HashMap<>();
        menuItem1.put("url", "https://www.baidu.com");
        menuItem1.put("label", "百度一下");

        HashMap<String, Object> menuItem2 = new HashMap<>();
        menuItem2.put("url", "https://www.flycode.icu");
        menuItem2.put("label", "飞云编程");
        menuList.add(menuItem1);
        menuList.add(menuItem2);
        model.put("menuItems", menuList);

        // 第六步：创建一个Writer对象，一般创建FileWriter对象，指定生成的文件名。
        Writer out = new FileWriter("myweb.html");

        // 第七步：调用模板对象的process方法输出文件。
        template.process(model, out);

        // 第八步：关闭流。
        out.close();
    }


    @Test
    public void testMainTemplate() throws IOException, TemplateException {
        // 第一步：创建一个Configuration对象，直接new一个对象。构造方法的参数就是FreeMarker对于的版本号。
        Configuration configuration = new Configuration(Configuration.VERSION_2_3_21);
        // 第二步：设置模板文件所在的路径。
        configuration.setDirectoryForTemplateLoading(new File("src/main/resources/templates"));
        // 第三步：设置模板文件使用的字符集。一般就是utf-8.
        configuration.setDefaultEncoding("utf-8");

        // 第四步：加载模板文件，创建一个模板对象。
        Template template = configuration.getTemplate("MainTemplate.java.ftl");

        // 第五步：创建一个模板使用的数据集，可以是pojo也可以是map。一般是Map。
        MainTemplateConfig model = new MainTemplateConfig();
        model.setAuthor("flycode");
        model.setOutputText("求和: ");
        model.setLoop(false);

        // 第六步：创建一个Writer对象，一般创建FileWriter对象，指定生成的文件名。
        Writer out = new FileWriter("MainTemplate.java");

        // 第七步：调用模板对象的process方法输出文件。
        template.process(model, out);

        // 第八步：关闭流。
        out.close();
    }
}
