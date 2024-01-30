package com.fly.web.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fly.web.model.entity.Generator;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author admin
 * @description 针对表【generator(代码生成器)】的数据库操作Mapper
 * @createDate 2024-01-09 11:25:58
 * @Entity com.fly.springbootinit.model.entity.Generator
 */
public interface GeneratorMapper extends BaseMapper<Generator> {

    /**
     * 找到已经删除的代码生成器
     * @return
     */
    @Select( "select id,distPath from generator where isDelete = 1" )
    List<Generator> listDeleteGenerator();
}




