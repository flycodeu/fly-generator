package com.fly.web.controller;

import cn.hutool.core.codec.Base64Encoder;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.TypeReference;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.ZipUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fly.maker.generator.main.GenerateTemplate;
import com.fly.maker.generator.main.ZipGenerator;
import com.fly.maker.meta.MetaValidator;
import com.fly.web.annotation.AuthCheck;
import com.fly.web.common.BaseResponse;
import com.fly.web.common.DeleteRequest;
import com.fly.web.common.ErrorCode;
import com.fly.web.common.ResultUtils;
import com.fly.web.constant.UserConstant;
import com.fly.web.exception.BusinessException;
import com.fly.web.exception.ThrowUtils;
import com.fly.web.manager.CacheManager;
import com.fly.web.manager.CosManager;
import com.fly.maker.meta.Meta;
import com.fly.web.model.dto.generator.*;
import com.fly.web.model.entity.Generator;
import com.fly.web.model.entity.User;
import com.fly.web.model.vo.GeneratorVO;
import com.fly.web.service.UserService;
import com.fly.web.service.impl.GeneratorServiceImpl;
import com.qcloud.cos.model.COSObject;
import com.qcloud.cos.model.COSObjectInputStream;
import com.qcloud.cos.utils.IOUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.util.StopWatch;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * 代码生成器接口
 */
@RestController
@RequestMapping( "/generator" )
@Slf4j
public class GeneratorController {

    @Resource
    private GeneratorServiceImpl generatorService;

    @Resource
    private UserService userService;

    @Resource
    private CosManager cosManager;

    @Resource
    private CacheManager cacheManager;

    /**
     * 创建代码生成器
     *
     * @param generatorAddRequest 代码生成器的添加
     * @param request             请求
     * @return id
     */
    @PostMapping( "/add" )
    public BaseResponse<Long> addGenerator(@RequestBody GeneratorAddRequest generatorAddRequest, HttpServletRequest request) {
        if (generatorAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Generator generator = new Generator();
        BeanUtils.copyProperties(generatorAddRequest, generator);
        generator.setStatus(0);
        List<String> tags = generatorAddRequest.getTags();
        if (tags != null) {
            generator.setTags(JSONUtil.toJsonStr(tags));
        }
        // 元信息转换为字符串
        Meta.ModelConfig modelConfig = generatorAddRequest.getModelConfig();
        Meta.FileConfig fileConfig = generatorAddRequest.getFileConfig();
        generator.setModelConfig(JSONUtil.toJsonStr(modelConfig));
        generator.setFileConfig(JSONUtil.toJsonStr(fileConfig));
        generatorService.validGenerator(generator, true);
        User loginUser = userService.getLoginUser(request);
        generator.setUserId(loginUser.getId());
        boolean result = generatorService.save(generator);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        long newGeneratorId = generator.getId();
        return ResultUtils.success(newGeneratorId);
    }

    /**
     * 删除代码生成器
     *
     * @param deleteRequest 删除请求
     * @param request       获取对象
     * @return boolean
     */
    @PostMapping( "/delete" )
    public BaseResponse<Boolean> deleteGenerator(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = userService.getLoginUser(request);
        long id = deleteRequest.getId();
        // 判断是否存在
        Generator oldGenerator = generatorService.getById(id);
        ThrowUtils.throwIf(oldGenerator == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人或管理员可删除
        if (!oldGenerator.getUserId().equals(user.getId()) && !userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        boolean b = generatorService.removeById(id);
        return ResultUtils.success(b);
    }

    /**
     * 更新（仅管理员）
     *
     * @param generatorUpdateRequest 更新请求
     * @return boolean
     */
    @PostMapping( "/update" )
    @AuthCheck( mustRole = UserConstant.ADMIN_ROLE )
    public BaseResponse<Boolean> updateGenerator(@RequestBody GeneratorUpdateRequest generatorUpdateRequest) {
        if (generatorUpdateRequest == null || generatorUpdateRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Generator generator = new Generator();
        BeanUtils.copyProperties(generatorUpdateRequest, generator);
        List<String> tags = generatorUpdateRequest.getTags();
        if (tags != null) {
            generator.setTags(JSONUtil.toJsonStr(tags));
        }
        // 元信息转换为字符串
        Meta.ModelConfig modelConfig = generatorUpdateRequest.getModelConfig();
        Meta.FileConfig fileConfig = generatorUpdateRequest.getFileConfig();
        generator.setModelConfig(JSONUtil.toJsonStr(modelConfig));
        generator.setFileConfig(JSONUtil.toJsonStr(fileConfig));
        // 参数校验
        generatorService.validGenerator(generator, false);
        long id = generatorUpdateRequest.getId();
        // 判断是否存在
        Generator oldGenerator = generatorService.getById(id);
        ThrowUtils.throwIf(oldGenerator == null, ErrorCode.NOT_FOUND_ERROR);
        boolean result = generatorService.updateById(generator);
        return ResultUtils.success(result);
    }

    /**
     * 根据 id 获取
     *
     * @param id 代码生成器id
     * @return generatorVo
     */
    @GetMapping( "/get/vo" )
    public BaseResponse<GeneratorVO> getGeneratorVOById(long id, HttpServletRequest request) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Generator generator = generatorService.getById(id);
        if (generator == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        return ResultUtils.success(generatorService.getGeneratorVO(generator, request));
    }

    /**
     * 分页获取列表（仅管理员）
     *
     * @param generatorQueryRequest 查询请求
     * @return page
     */
    @PostMapping( "/list/page" )
    @AuthCheck( mustRole = UserConstant.ADMIN_ROLE )
    public BaseResponse<Page<Generator>> listGeneratorByPage(@RequestBody GeneratorQueryRequest generatorQueryRequest) {
        long current = generatorQueryRequest.getCurrent();
        long size = generatorQueryRequest.getPageSize();
        Page<Generator> generatorPage = generatorService.page(new Page<>(current, size),
                generatorService.getQueryWrapper(generatorQueryRequest));
        return ResultUtils.success(generatorPage);
    }

    /**
     * 分页获取列表（封装类）
     *
     * @param generatorQueryRequest 查询请求
     * @param request               获取对象
     * @return page
     */
    @PostMapping( "/list/page/vo" )
    public BaseResponse<Page<GeneratorVO>> listGeneratorVOByPage(@RequestBody GeneratorQueryRequest generatorQueryRequest,
                                                                 HttpServletRequest request) {
        long current = generatorQueryRequest.getCurrent();
        long size = generatorQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        Page<Generator> generatorPage = generatorService.page(new Page<>(current, size),
                generatorService.getQueryWrapper(generatorQueryRequest));
        stopWatch.stop();
        System.out.println("查询生成器耗时" + stopWatch.getTotalTimeMillis());

        stopWatch = new StopWatch();
        stopWatch.start();
        Page<GeneratorVO> generatorVOPage = generatorService.getGeneratorVOPage(generatorPage, request);
        stopWatch.stop();
        System.out.println("查询关联数据耗时：" + stopWatch.getTotalTimeMillis());

        return ResultUtils.success(generatorVOPage);
    }


    /**
     * 快速分页获取列表（封装类）
     *
     * @param generatorQueryRequest 查询请求
     * @param request               获取对象
     * @return page
     */
    @PostMapping( "/list/page/vo/fast" )
    public BaseResponse<Page<GeneratorVO>> listGeneratorVOByPageFast(@RequestBody GeneratorQueryRequest generatorQueryRequest,
                                                                     HttpServletRequest request) {
        long current = generatorQueryRequest.getCurrent();
        long size = generatorQueryRequest.getPageSize();
        // 缓存获取数据
        String cacheKey = getPageKey(generatorQueryRequest);
        log.info("缓存key:{}", cacheKey);
        Object value = cacheManager.get(cacheKey);
        log.info("缓存获取数据:{}", value);
        if (value!=null){
            // 转换为bean对象
            return ResultUtils.success((Page<GeneratorVO>) value);
        }
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);

        QueryWrapper<Generator> queryWrapper = generatorService.getQueryWrapper(generatorQueryRequest);
        queryWrapper.select("id",
                "name",
                "description",
                "tags",
                "picture",
                "status",
                "userId",
                "createTime",
                "updateTime"
        );

        Page<Generator> generatorPage = generatorService.page(new Page<>(current, size), queryWrapper);

        Page<GeneratorVO> generatorVOPage = generatorService.getGeneratorVOPage(generatorPage, request);

        // 缓存数据
        cacheManager.put(cacheKey, generatorVOPage);

        return ResultUtils.success(generatorVOPage);
    }

    /**
     * 分页获取当前用户创建的资源列表
     *
     * @param generatorQueryRequest 查询请求
     * @param request               获取对象
     * @return page
     */
    @PostMapping( "/my/list/page/vo" )
    public BaseResponse<Page<GeneratorVO>> listMyGeneratorVOByPage(@RequestBody GeneratorQueryRequest generatorQueryRequest,
                                                                   HttpServletRequest request) {
        if (generatorQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        generatorQueryRequest.setUserId(loginUser.getId());
        long current = generatorQueryRequest.getCurrent();
        long size = generatorQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        Page<Generator> generatorPage = generatorService.page(new Page<>(current, size),
                generatorService.getQueryWrapper(generatorQueryRequest));
        return ResultUtils.success(generatorService.getGeneratorVOPage(generatorPage, request));
    }


    /**
     * 编辑（用户）
     *
     * @param generatorEditRequest 编辑请求
     * @param request              获取对象
     * @return boolean
     */
    @PostMapping( "/edit" )
    public BaseResponse<Boolean> editGenerator(@RequestBody GeneratorEditRequest generatorEditRequest, HttpServletRequest request) {
        if (generatorEditRequest == null || generatorEditRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Generator generator = new Generator();
        BeanUtils.copyProperties(generatorEditRequest, generator);
        List<String> tags = generatorEditRequest.getTags();
        if (tags != null) {
            generator.setTags(JSONUtil.toJsonStr(tags));
        }
        // 元信息转换为字符串
        Meta.ModelConfig modelConfig = generatorEditRequest.getModelConfig();
        Meta.FileConfig fileConfig = generatorEditRequest.getFileConfig();
        generator.setModelConfig(JSONUtil.toJsonStr(modelConfig));
        generator.setFileConfig(JSONUtil.toJsonStr(fileConfig));
        // 参数校验
        generatorService.validGenerator(generator, false);
        User loginUser = userService.getLoginUser(request);
        long id = generatorEditRequest.getId();
        // 判断是否存在
        Generator oldGenerator = generatorService.getById(id);
        ThrowUtils.throwIf(oldGenerator == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人或管理员可编辑
        if (!oldGenerator.getUserId().equals(loginUser.getId()) && !userService.isAdmin(loginUser)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        boolean result = generatorService.updateById(generator);
        return ResultUtils.success(result);
    }


    /**
     * 根据项目的id下载对应的文件
     *
     * @param id
     * @param response
     * @param request
     * @throws IOException
     */
    @GetMapping( "/download" )
    public void downloadGeneratorById(Long id, HttpServletResponse response, HttpServletRequest request) throws IOException {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        Generator generator = generatorService.getById(id);
        if (generator == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        String filepath = generator.getDistPath();
        if (StrUtil.isBlank(filepath)) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "产物包不存在");
        }

        // 设置响应头
        response.setContentType("application/octet-stream;charset=UTF-8");
        response.setHeader("Content-Disposition", "attachment; filename=" + filepath);

        // 获取缓存路径
        String cacheFilePath = getCacheFilePath(id, filepath);
        if (FileUtil.exist(cacheFilePath)) {
            // 写入响应
            Files.copy(Paths.get(cacheFilePath), response.getOutputStream());
            return;
        }

        // 追踪事件
        log.info("用户 {} 下载了 {}", loginUser, filepath);

        COSObjectInputStream cosObjectInput = null;
        try {
            StopWatch stopWatch = new StopWatch();
            stopWatch.start();
            COSObject cosObject = cosManager.getCosObject(filepath);
            cosObjectInput = cosObject.getObjectContent();
            // 处理下载到的流
            byte[] bytes = IOUtils.toByteArray(cosObjectInput);
            stopWatch.stop();
            System.out.println("下载文件耗时：" + stopWatch.getTotalTimeMillis() + "ms");

            // 写入响应
            response.getOutputStream().write(bytes);
            response.getOutputStream().flush();
        } catch (Exception e) {
            log.error("file download error, filepath = " + filepath, e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "下载失败");
        } finally {
            if (cosObjectInput != null) {
                cosObjectInput.close();
            }
        }
    }


    /**
     * 根据项目的id下载对应的文件
     *
     * @param generatorUseRequest
     * @param response
     * @param request
     * @throws IOException
     */
    @PostMapping( "/use" )
    public void useGenerator(@RequestBody GeneratorUseRequest generatorUseRequest, HttpServletResponse response, HttpServletRequest request) throws IOException {
        //1. 获取用户传递过来的生成器id和数据模型
        Long id = generatorUseRequest.getId();
        Map<String, Object> dataModel = generatorUseRequest.getDataModel();

        //2. 判断用户是否登录
        User loginUser = userService.getLoginUser(request);
        log.info("userId=>{}下载了生成器=>{}", loginUser.getId(), id);
        //3. 获取存储生成器的路径
        Generator generator = generatorService.getById(id);
        if (generator == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "生成器不存在");
        }
        String distPath = generator.getDistPath();
        if (StrUtil.isBlank(distPath)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "生成器文件不存在");
        }
        //4. 下载压缩包
        //4.1 创建工作区间
        String projectPath = System.getProperty("user.dir");
        String tempDirPath = String.format("%s/.temp/use/%s", projectPath, id);
        String zipFilePath = tempDirPath + "/dist.zip";

        if (!FileUtil.exist(zipFilePath)) {
            FileUtil.touch(zipFilePath);
        }
        String cacheFilePath = getCacheFilePath(id, distPath);
        if (FileUtil.exist(cacheFilePath)) {
            Files.copy(Paths.get(cacheFilePath), Files.newOutputStream(Paths.get(zipFilePath)));
        } else {
            StopWatch stopWatch = new StopWatch();
            stopWatch.start();
            try {
                cosManager.download(distPath, zipFilePath);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            stopWatch.stop();
            System.out.println("下载文件耗时：" + stopWatch.getTotalTimeSeconds() + "秒");
        }


//        stopWatch = new StopWatch();
//        stopWatch.start();
        //4.2 解压文件
        File unzipDir = ZipUtil.unzip(zipFilePath);
//        stopWatch.stop();
//        System.out.println("解压文件耗时：" + stopWatch.getTotalTimeSeconds() + "秒");

        //5. 用户参数写入json
        String dataModelPath = tempDirPath + "/dataModel.json";
        String jsonStr = JSONUtil.toJsonStr(dataModel);
//        stopWatch = new StopWatch();
//        stopWatch.start();
        FileUtil.writeUtf8String(jsonStr, dataModelPath);
//        stopWatch.stop();
//        System.out.println("写入json文件耗时：" + stopWatch.getTotalTimeSeconds() + "秒");

//        stopWatch = new StopWatch();
//        stopWatch.start();
        //6. 执行脚本
        //6.1 找到脚本
        // 需要注意不是windows的使用generator
        File scriptFile = FileUtil.loopFiles(unzipDir, 2, null)
                .stream()
                .filter(file -> file.isFile() && "generator.bat".equals(file.getName()))
                .findFirst()
                .orElseThrow(RuntimeException::new);
//        stopWatch.stop();
//        System.out.println("查找脚本耗时：" + stopWatch.getTotalTimeSeconds() + "秒");

        //6.2 添加权限
        //todo 修复权限
//        try {
//            Set<PosixFilePermission> permissions = PosixFilePermissions.fromString("rwxrwxrwx");
//            Files.setPosixFilePermissions(scriptFile.toPath(), permissions);
//        } catch (IOException e) {
//
//        }
        //6.3执行脚本
        // 构建命令
//        stopWatch = new StopWatch();
//        stopWatch.start();
        String scriptAbsolutePath = scriptFile.getAbsolutePath().replace("\\", "/");
        String[] commands = new String[]{scriptAbsolutePath, "json-generate", "--file=" + dataModelPath};
        File scriptDir = scriptFile.getParentFile();
        //todo 修复存在的命令行的bug
        ProcessBuilder processBuilder = new ProcessBuilder(commands);
        processBuilder.directory(scriptDir);
        try {
            Process process = processBuilder.start();
            // 读取命令输出
            InputStream inputStream = process.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                System.out.println(line);
            }
            int exitCode = process.waitFor();
            System.out.println("Maven打包命令执行完毕，退出码：" + exitCode);
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "执行命令失败");
        }
        //stopWatch.stop();
        //    System.out.println("Maven打包命令执行完毕，耗时：" + stopWatch.getTotalTimeSeconds() + "秒");

//
//        stopWatch = new StopWatch();
//        stopWatch.start();
        //7. 压缩文件结果返回前端
        String generatePath = scriptDir.getAbsolutePath() + "/generated";
        String resultPath = tempDirPath + "/result.zip";
        File resultFile = ZipUtil.zip(generatePath, resultPath);
        response.setContentType("application/octet-stream;charset=UTF-8");
        response.setHeader("Content-Disposition", "attachment; filename=" + resultFile.getName());
        Files.copy(resultFile.toPath(), response.getOutputStream());
//        stopWatch.stop();
        // System.out.println("压缩文件返回前端，耗时：" + stopWatch.getTotalTimeSeconds() + "秒");


        // 异步删除文件
        CompletableFuture.runAsync(() -> {
            FileUtil.del(tempDirPath);
        });
    }


    /**
     * 制作代码生成器
     *
     * @param generatorMakeRequest
     * @param response
     * @param request
     * @throws IOException
     */
    @PostMapping( "/make" )
    public void makeGenerator(@RequestBody GeneratorMakeRequest generatorMakeRequest, HttpServletResponse response, HttpServletRequest request) throws IOException {
        // 1. 获取参数
        Meta meta = generatorMakeRequest.getMeta();
        String zipFilePath = generatorMakeRequest.getZipFilePath();
        if (StrUtil.isBlank(zipFilePath)) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "压缩包不存在");
        }

        // 2. 判断用户是否登录
        User loginUser = userService.getLoginUser(request);
        log.info("用户{}制作代码生成器", loginUser.getId());

        // 3. 创建临时工作目录，使用make目录,里面包含了一个随机数目录，随机数目录里面包含项目的模板文件和压缩包,下载到本地
        String projectPath = System.getProperty("user.dir");
        String id = IdUtil.getSnowflakeNextId() + RandomUtil.randomString(6);
        String tempDirPath = String.format("%s/.temp/make/%s", projectPath, id);
        String localZipFilePath = tempDirPath + "/project.zip";

        if (!FileUtil.exist(localZipFilePath)) {
            FileUtil.touch(localZipFilePath);
        }
//        StopWatch stopWatch = new StopWatch();
//        stopWatch.start();
        // 下载到服务器
        try {
            cosManager.download(zipFilePath, localZipFilePath);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
//        stopWatch.stop();
//        System.out.println("下载到服务器耗时：" + stopWatch.getTotalTimeMillis() + "ms");

        // 4. 解压压缩包到临时目录
        File unzipDistDir = ZipUtil.unzip(localZipFilePath);
        // 5. 构造元信息的相关配置，以及输入路径
        String sourceRootPath = unzipDistDir.getAbsolutePath();
        meta.getFileConfig().setSourceRootPath(sourceRootPath);
        MetaValidator.doValidateAndFill(meta);
        String outputPath = String.format("%s/generated/%s", tempDirPath, meta.getName());
        // 6. 调用make工具

        GenerateTemplate generateTemplate = new ZipGenerator();
        try {
            generateTemplate.doGenerate(meta, outputPath);
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "下载失败");
        }

        // 7. 压缩文件结果返回前端,我的压缩包的地址名字是dest而不是dist
        String suffix = "-dest.zip";
        String zipFileName = meta.getName() + suffix;
        String distZipFilePath = outputPath + suffix;
        log.info("distZipFilePath: {}", distZipFilePath);
        response.setContentType("application/octet-stream;charset=UTF-8");
        response.setHeader("Content-Disposition", "attachment; filename=" + zipFileName);
        Files.copy(Paths.get(distZipFilePath), response.getOutputStream());


        // 8. 异步删除文件
        CompletableFuture.runAsync(() -> {
            FileUtil.del(tempDirPath);
        });
    }


    /**
     * 使用缓存存放文件
     * 只有管理员才能使用缓存存放文件
     *
     * @param generatorCacheRequest
     * @param response
     * @param request
     * @throws IOException
     */
    @PostMapping( "/cache" )
    @AuthCheck( mustRole = UserConstant.ADMIN_ROLE )
    public void cacheGenerator(@RequestBody GeneratorCacheRequest generatorCacheRequest, HttpServletResponse response, HttpServletRequest request) throws IOException {
        if (generatorCacheRequest == null || generatorCacheRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Long id = generatorCacheRequest.getId();
        Generator generator = generatorService.getById(id);
        if (generator == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        String distPath = generator.getDistPath();
        if (StrUtil.isBlank(distPath)) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "产物包不存在");
        }

        // 使用缓存空间
        // 缓存空间
        String zipFilePath = getCacheFilePath(id, distPath);

        // 新建文件
        if (!FileUtil.exist(zipFilePath)) {
            FileUtil.touch(zipFilePath);
        }

        // 下载生成器
        try {
            cosManager.download(distPath, zipFilePath);
        } catch (InterruptedException e) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "压缩包下载失败");
        }
    }

    /**
     * 生成缓存文件路径
     *
     * @param id
     * @param distPath
     * @return
     */
    public String getCacheFilePath(Long id, String distPath) {
        String projectPath = System.getProperty("user.dir");
        String tempDirPath = String.format("%s/.temp/cache/%s", projectPath, id);
        String localZipFilePath = tempDirPath + "/" + distPath;
        return localZipFilePath;
    }


    /**
     * 获取分页缓存key
     *
     * @param generatorQueryRequest
     * @return
     */
    public static String getPageKey(GeneratorQueryRequest generatorQueryRequest) {
        String jsonStr = JSONUtil.toJsonStr(generatorQueryRequest);
        // 请求参数编码,避免参数过多
        String base64 = Base64Encoder.encode(jsonStr.getBytes());
        String key = "generator:page:" + base64;
        return key;
    }
}
