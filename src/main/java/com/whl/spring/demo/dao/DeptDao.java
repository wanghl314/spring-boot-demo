package com.whl.spring.demo.dao;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.whl.spring.demo.entity.DeptEntity;
import io.lettuce.core.dynamic.annotation.Param;
import org.apache.ibatis.annotations.*;

@Mapper
public interface DeptDao {
    @SelectProvider(value = DeptProvider.class, method = "list")
    Page<DeptEntity> list(Page<?> page) throws Exception;

    @SelectProvider.List({
            @SelectProvider(value = DeptProvider.class, method = "getFirst_mysql", databaseId = "mysql"),
            @SelectProvider(value = DeptProvider.class, method = "getFirst_oracle", databaseId = "oracle"),
            @SelectProvider(value = DeptProvider.class, method = "getFirst_sqlserver", databaseId = "sqlserver"),
            @SelectProvider(value = DeptProvider.class, method = "getFirst_postgresql", databaseId = "postgresql")
    })
    DeptEntity getFirst() throws Exception;

    @SelectProvider(value = DeptProvider.class, method = "getById")
    DeptEntity getById(@Param("id") Long id) throws Exception;

    @InsertProvider(value = DeptProvider.class, method = "create")
    int create(DeptEntity dept) throws Exception;

    @UpdateProvider(value = DeptProvider.class, method = "update")
    int update(DeptEntity dept) throws Exception;

    @DeleteProvider(value = DeptProvider.class, method = "deleteById")
    int deleteById(Long id) throws Exception;
}
