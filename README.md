# 定制化代码生成器

基于React + Spring Boot + Picocli + FreeMarker + Mysql + 对象存储 + Redis实现的可在线下载，制作和生成代码生成器。
管理员：可以管理用户，生成器，代码生成器模板，代码生成器配置，代码生成器生成记录，代码生成器生成记录详情，代码生成器生成记录详情的下载记录。
用户：可以生成代码生成器，可以下载自己生成的代码生成器

## 项目访问
[飞云代码生成器](http://124.71.207.114/)

## 目录

```
├─fly-generator-basic                         初始代码生成器 
├─fly-generator-demo-projects                 代码生成器demo项目
| └─acm-template							  基础acm项目示例
| └─acm-template-pro						  基础acm模板示例
| └─springboot-init							  基础SpringBoot项目示例
| └─springboot-init-pro						  基础SpringBoot模板示例
├─fly-generator-maker						  制作代码生成器
├─fly-generator-web-backend					  后端服务
├─fly-generator-web-frontend				  前端界面
├─xxl-job-master							  xxl-job分布式任务调度系统
├─压力测试.jmx								   压力测试样例
```

## 项目流程

![image-20240220134056804](http://cdn.flycode.icu/codeCenterImg/image-20240220134056804.png)



## 用户使用

### 使用代码生成器

#### 主界面

![image-20240220135431847](http://cdn.flycode.icu/codeCenterImg/image-20240220135431847.png)

#### 代码生成器详细界面

![image-20240220135444110](http://cdn.flycode.icu/codeCenterImg/image-20240220135444110.png)

#### 使用代码生成器

![image-20240220135454612](http://cdn.flycode.icu/codeCenterImg/image-20240220135454612.png)

#### 输入参数

![image-20240220135520430](http://cdn.flycode.icu/codeCenterImg/image-20240220135520430.png)

#### 生成文件

![image-20240220135533857](http://cdn.flycode.icu/codeCenterImg/image-20240220135533857.png)



### 创建代码生成器

![image-20240220135716394](http://cdn.flycode.icu/codeCenterImg/image-20240220135716394.png)

![image-20240220135732077](http://cdn.flycode.icu/codeCenterImg/image-20240220135732077.png)

产物包必须要使用经过代码生成器工具maker制作的模板项目，里面必须包含generator脚本文件

## 开发者使用

### 使用流程

1. 下载代码生成器制作工具

下载fly-generator-maker，打开项目，安装依赖install

![image-20240220134352340](http://cdn.flycode.icu/codeCenterImg/image-20240220134352340.png)

2. 引入代码生成器

新建项目，引入依赖

```xml
<dependency> 
	<groupId>com.fly</groupId>
    <artifactId>fly-generator-maker</artifactId>
    <version>1.0-SNAPSHOT</version>
</dependency>dependency>
```

3. 使用代码生成器

需要准备一个模板项目

方式1：手动编码

```java
    public void testMakeTemplate() {  	
		Meta meta = new Meta();
        meta.setName("acm-template-generator"); // 项目名必填
        meta.setDescription("ACM 示例模板生成器"); // 项目描述
        String projectPath = System.getProperty("user.dir");  // 项目路径
        String originProjectPath = new File(projectPath).getParent() + File.separator + "fly-generator-demo-projects/springboot-init";     // 原始模板项目路径

        String inputFilePath1 = "./";           

        List<FileFilterConfig> fileFilterConfigList = new ArrayList<>();
        // 文件过滤配置
        TemplateMakerFileConfig.FileInfoConfig fileInfoConfig1 = new TemplateMakerFileConfig.FileInfoConfig();
        fileInfoConfig1.setFileFilterConfigs(fileFilterConfigList);
        fileInfoConfig1.setPath(inputFilePath1);
        TemplateMakerFileConfig templateMakerFileConfig = new TemplateMakerFileConfig();
        templateMakerFileConfig.setFiles(Collections.singletonList(fileInfoConfig1));

        // 模型配置
        TemplateMakerModelConfig templateMakerModelConfig = new TemplateMakerModelConfig();
        TemplateMakerModelConfig.ModelInfoConfig modelInfoConfig1 = new TemplateMakerModelConfig.ModelInfoConfig();
        modelInfoConfig1.setFieldName("className");
        modelInfoConfig1.setType("String");
        modelInfoConfig1.setReplaceText("BaseResponse");

        List<TemplateMakerModelConfig.ModelInfoConfig> modelInfoConfigList = Arrays.asList(modelInfoConfig1);
        templateMakerModelConfig.setModels(modelInfoConfigList);
        long l = TemplateMaker.makeTemplate(meta, originProjectPath, templateMakerFileConfig, templateMakerModelConfig, null, 1743817327068332032L);
        System.out.println(l);
    }

```

方式2：使用Json配置

Json配置放在resource里面

支持分步制作

templateMaker.json

```json
{
  "id": 1,
  "meta": {
    "name": "springboot-init-generator",
    "description": "Spring Boot 模板项目生成器"
  },
  "originProjectPath": "../../../fly-generator-demo-projects/springboot-init"
}
```

templateMaker1.json

```json
{
  "id": 1,
  "fileConfig": {
    "files": [
      {
        "path": ""
      }
    ]
  },
  "modelConfig": {
    "models": [
      {
        "fieldName": "className",
        "type": "String",
        "description": "替换包名",
        "defaultValue": "com.fly",
        "replaceText": "com.yupi"
      }
    ]
  }
}
```







```java
    public void makeTemplate() {
        String rootPath = "examples/springboot-init/";  // 原始模板文件路径
        String configStr = ResourceUtil.readUtf8Str(rootPath + "templateMaker.json"); ？// json位置
        TemplateMakerConfig templateMakerConfig = JSONUtil.toBean(configStr, TemplateMakerConfig.class);
        long id = TemplateMaker.makeTemplate(templateMakerConfig);
		// 分步制作
        configStr = ResourceUtil.readUtf8Str(rootPath + "templateMaker1.json");
        templateMakerConfig = JSONUtil.toBean(configStr, TemplateMakerConfig.class);
        TemplateMaker.makeTemplate(templateMakerConfig);
    }
```

第一次会生成一个id，后续的代码生成器都可使用这个id继续创作

### 生成位置

项目会生成在当前项目的.temp目录里面



