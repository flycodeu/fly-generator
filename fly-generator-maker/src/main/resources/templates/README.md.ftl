# ${name}

> ${description}
>
> 作者：${author}
>
> 基于 [程序员鱼皮](https://yuyuanweb.feishu.cn/wiki/Abldw5WkjidySxkKxU2cQdAtnah) 的 [鱼籽代码生成器项目](https://github.com/liyupi/yuzi-generator) 制作，感谢您的使用！

可以通过命令行交互式输入的方式动态生成想要的项目代码

## 使用说明

执行项目根目录下的脚本文件：

```
generator <命令> <选项参数>
```

示例命令：

```
generator generate <#list modelConfig.models as modelInfo><#if modelInfo.groupKey??><#list modelInfo.models as subModelInfo><#if subModelInfo.abbr??> -${subModelInfo.abbr} <#else> -${subModelInfo.fieldName} </#if></#list><#else><#if modelInfo.abbr??> -${modelInfo.abbr} <#else> -${modelInfo.fieldName} </#if></#if></#list>
```

## 参数说明

<#list modelConfig.models as modelInfo>

<#if  modelInfo.groupKey??>

分组标签: ${modelInfo.groupKey}
分组条件: ${modelInfo.condition}
    <#list modelInfo.models as submodelInfo>

字段名：${submodelInfo.fieldName}

> 类型：${submodelInfo.type}

> 描述：${submodelInfo.description}

> 默认值：${submodelInfo.defaultValue?c}

> 缩写： <#if submodelInfo.abbr??>-${submodelInfo.abbr}<#else>无</#if>
</#list>
<#else>

字段名：${modelInfo.fieldName}

> 类型：${modelInfo.type}

> 描述：${modelInfo.description}

> 默认值： ${modelInfo.defaultValue?c}

> 缩写：  <#if modelInfo.abbr??>${modelInfo.abbr}<#else>无</#if>
</#if>

</#list>