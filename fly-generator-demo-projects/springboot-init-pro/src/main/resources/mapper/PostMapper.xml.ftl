<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper
        PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="${className}.springbootinit.mapper.PostMapper">

    <resultMap id="BaseResultMap" type="${className}.springbootinit.model.entity.Post">
        <id property="id" column="id" jdbcType="BIGINT"/>
        <result property="title" column="title" jdbcType="VARCHAR"/>
        <result property="content" column="content" jdbcType="VARCHAR"/>
        <result property="tags" column="tags" jdbcType="VARCHAR"/>
        <result property="userId" column="userId" jdbcType="BIGINT"/>
        <result property="createTime" column="createTime" jdbcType="TIMESTAMP"/>
        <result property="updateTime" column="updateTime" jdbcType="TIMESTAMP"/>
        <result property="isDelete" column="isDelete" jdbcType="TINYINT"/>
    </resultMap>

    <sql id="Base_Column_List">
        id,title,content,tags,userId,
        createTime,updateTime,isDelete
    </sql>

    <select id="listPostWithDelete" resultType="${className}.springbootinit.model.entity.Post">
        select *
        from post
        where updateTime >= <#noparse>#{minUpdateTime}</#noparse>
    </select>
</mapper>
