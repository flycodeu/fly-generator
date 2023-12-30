<html>
<head>
    <title>Welcome to FlyCode 编程</title><br>
</head>
<body>
<#-- 注释部分 -->
<#-- 下面使用插值 -->
<h1>Welcome ${user} !</h1><br>
<u1>
    <#-- 使用FTL指令 -->
    <#list menuItems as item><br>
        <li><a href="${item.url}">${item.label}</a></li>
    </#list>
</u1>
<footer>
    ${currentYear}
</footer>
</body>
</html>