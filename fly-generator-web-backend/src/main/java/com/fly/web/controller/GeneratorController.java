package com.fly.web.controller;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fly.web.annotation.AuthCheck;
import com.fly.web.common.BaseResponse;
import com.fly.web.common.DeleteRequest;
import com.fly.web.common.ErrorCode;
import com.fly.web.common.ResultUtils;
import com.fly.web.constant.UserConstant;
import com.fly.web.exception.BusinessException;
import com.fly.web.exception.ThrowUtils;
import com.fly.web.meta.Meta;
import com.fly.web.model.dto.generator.GeneratorAddRequest;
import com.fly.web.model.dto.generator.GeneratorEditRequest;
import com.fly.web.model.dto.generator.GeneratorQueryRequest;
import com.fly.web.model.dto.generator.GeneratorUpdateRequest;
import com.fly.web.model.entity.Generator;
import com.fly.web.model.entity.User;
import com.fly.web.model.vo.GeneratorVO;
import com.fly.web.service.UserService;
import com.fly.web.service.impl.GeneratorServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

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
        Page<Generator> generatorPage = generatorService.page(new Page<>(current, size),
                generatorService.getQueryWrapper(generatorQueryRequest));
        return ResultUtils.success(generatorService.getGeneratorVOPage(generatorPage, request));
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

}
