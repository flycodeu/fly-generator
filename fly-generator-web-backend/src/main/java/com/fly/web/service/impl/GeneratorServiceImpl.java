package com.fly.web.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fly.web.common.ErrorCode;
import com.fly.web.constant.CommonConstant;
import com.fly.web.exception.BusinessException;
import com.fly.web.exception.ThrowUtils;
import com.fly.web.mapper.GeneratorMapper;
import com.fly.web.model.dto.generator.GeneratorQueryRequest;
import com.fly.web.model.entity.Generator;
import com.fly.web.model.entity.User;
import com.fly.web.model.vo.GeneratorVO;
import com.fly.web.model.vo.UserVO;
import com.fly.web.service.GeneratorService;
import com.fly.web.service.UserService;
import com.fly.web.utils.SqlUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author admin
 * @description 针对表【generator(代码生成器)】的数据库操作Service实现
 * @createDate 2024-01-09 11:25:58
 */
@Service
public class GeneratorServiceImpl extends ServiceImpl<GeneratorMapper, Generator>
        implements GeneratorService {
    @Resource
    private UserService userService;

    /**
     * 判断添加代码生成的的数据或者修改的数据是否合法
     *
     * @param generator
     * @param add
     */
    @Override
    public void validGenerator(Generator generator, boolean add) {
        if (generator == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String name = generator.getName();
        String description = generator.getDescription();
        String tags = generator.getTags();
        String basePackage = generator.getBasePackage();
        String version = generator.getVersion();
        String author = generator.getAuthor();
        String fileConfig = generator.getFileConfig();
        String modelConfig = generator.getModelConfig();
        String distPath = generator.getDistPath();

        // 创建时，参数不能为空
        if (add) {
            ThrowUtils.throwIf(StringUtils.isAnyBlank(name, description, basePackage, version, author, distPath), ErrorCode.PARAMS_ERROR);
        }
        // 有参数则校验
        if (StringUtils.isNotBlank(name) && name.length() > 80) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "名称过长");
        }
        if (StringUtils.isNotBlank(description) && description.length() > 256) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "描述过长");
        }
        if (StringUtils.isNotBlank(basePackage) && basePackage.length() > 128) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "基础包名过长");
        }
        if (StringUtils.isNotBlank(version) && version.length() > 128) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "版本号过长");
        }
        if (StringUtils.isNotBlank(author) && author.length() > 128) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "作者名过长");
        }
        if (StringUtils.isNotBlank(distPath) && distPath.length() > 128) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "输出路径");
        }

    }

    /**
     * 获取查询包装类
     *
     * @param generatorQueryRequest
     * @return
     */
    @Override
    public QueryWrapper<Generator> getQueryWrapper(GeneratorQueryRequest generatorQueryRequest) {
        QueryWrapper<Generator> queryWrapper = new QueryWrapper<>();
        if (generatorQueryRequest == null) {
            return queryWrapper;
        }
        String searchText = generatorQueryRequest.getSearchText();
        String sortField = generatorQueryRequest.getSortField();
        String sortOrder = generatorQueryRequest.getSortOrder();

        Long id = generatorQueryRequest.getId();
        String name = generatorQueryRequest.getName();
        String description = generatorQueryRequest.getDescription();
        Integer status = generatorQueryRequest.getStatus();
        String basePackage = generatorQueryRequest.getBasePackage();
        String distPath = generatorQueryRequest.getDistPath();
        String version = generatorQueryRequest.getVersion();
        String author = generatorQueryRequest.getAuthor();

        List<String> tagList = generatorQueryRequest.getTags();
        Long userId = generatorQueryRequest.getUserId();
        Long notId = generatorQueryRequest.getNotId();
        // 拼接查询条件
        if (StringUtils.isNotBlank(searchText)) {
            // todo修复拼接异常
            queryWrapper.and(qw -> qw.like("name", searchText).or().like("description", searchText));
        }
        queryWrapper.like(StringUtils.isNotBlank(name), "name", name);
        queryWrapper.like(StringUtils.isNotBlank(description), "description", description);
        queryWrapper.like(StringUtils.isNotEmpty(author), "author", author);

        if (CollUtil.isNotEmpty(tagList)) {
            for (String tag : tagList) {
                queryWrapper.like("tags", "\"" + tag + "\"");
            }
        }
        queryWrapper.ne(ObjectUtils.isNotEmpty(notId), "id", notId);
        queryWrapper.eq(ObjectUtils.isNotEmpty(status), "status", status);
        queryWrapper.eq(ObjectUtils.isNotEmpty(id), "id", id);
        queryWrapper.eq(ObjectUtils.isNotEmpty(userId), "userId", userId);
        queryWrapper.eq(StringUtils.isNotEmpty(basePackage), "basePackage", basePackage);
        queryWrapper.eq(StringUtils.isNotEmpty(distPath), "distPath", distPath);
        queryWrapper.eq(StringUtils.isNotEmpty(version), "version", version);
        queryWrapper.orderBy(SqlUtils.validSortField(sortField), sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
                sortField);
        return queryWrapper;
    }

    /**
     * 获取代码生成器表的vo数据
     *
     * @param generator
     * @param request
     * @return
     */
    @Override
    public GeneratorVO getGeneratorVO(Generator generator, HttpServletRequest request) {
        GeneratorVO generatorVO = GeneratorVO.objToVo(generator);
        // 1. 关联查询用户信息
        Long userId = generator.getUserId();
        User user = null;
        if (userId != null && userId > 0) {
            user = userService.getById(userId);
        }
        UserVO userVO = userService.getUserVO(user);
        generatorVO.setUserVO(userVO);
        return generatorVO;
    }

    /**
     * 获取分页代码生成器表的vo
     *
     * @param generatorPage
     * @param request
     * @return
     */
    @Override
    public Page<GeneratorVO> getGeneratorVOPage(Page<Generator> generatorPage, HttpServletRequest request) {
        List<Generator> generatorList = generatorPage.getRecords();
        Page<GeneratorVO> generatorVOPage = new Page<>(generatorPage.getCurrent(), generatorPage.getSize(), generatorPage.getTotal());
        if (CollUtil.isEmpty(generatorList)) {
            return generatorVOPage;
        }
        // 1. 关联查询用户信息
        Set<Long> userIdSet = generatorList.stream().map(Generator::getUserId).collect(Collectors.toSet());
        Map<Long, List<User>> userIdUserListMap = userService.listByIds(userIdSet).stream()
                .collect(Collectors.groupingBy(User::getId));

        // 填充信息
        List<GeneratorVO> generatorVOList = generatorList.stream().map(generator -> {
            GeneratorVO generatorVO = GeneratorVO.objToVo(generator);
            Long userId = generator.getUserId();
            User user = null;
            if (userIdUserListMap.containsKey(userId)) {
                user = userIdUserListMap.get(userId).get(0);
            }
            generatorVO.setUserVO(userService.getUserVO(user));
            return generatorVO;
        }).collect(Collectors.toList());
        generatorVOPage.setRecords(generatorVOList);
        return generatorVOPage;
    }
}




