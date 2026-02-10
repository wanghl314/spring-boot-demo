package com.whl.spring.demo.dao;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.whl.spring.demo.entity.DeptEntity;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.jdbc.SQL;

public class DeptProvider {

    public String list(Page<?> page) {
        return new SQL()
                .SELECT("id, name, showorder")
                .FROM("sbd_dept")
                .ORDER_BY("id")
                .toString();
    }

    public String getById(Long id) {
        return new SQL()
                .SELECT("id, name, showorder")
                .FROM("sbd_dept")
                .WHERE("id = #{id}")
                .toString();
    }

    public String create(DeptEntity dept) {
        return new SQL()
                .INSERT_INTO("sbd_dept")
                .INTO_COLUMNS("id, name, showorder")
                .INTO_VALUES("#{id}, #{name}, #{showorder}")
                .toString();
    }

    public String update(DeptEntity dept) {
        SQL sql = new SQL()
                .UPDATE("sbd_dept");

        if (StringUtils.isNotBlank(dept.getName())) {
            sql.SET("name = #{name}");
        }

        if (StringUtils.isNotBlank(dept.getShoworder())) {
            sql.SET("showorder = #{showorder}");
        }
        return sql
                .WHERE("id = #{id}")
                .toString();
    }

    public String deleteById(Long id) {
        return new SQL()
                .DELETE_FROM("sbd_dept")
                .WHERE("id = #{id}")
                .toString();
    }

}
