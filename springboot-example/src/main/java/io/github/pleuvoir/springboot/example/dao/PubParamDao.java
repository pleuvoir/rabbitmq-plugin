package io.github.pleuvoir.springboot.example.dao;

import java.math.BigDecimal;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.mapper.BaseMapper;

/**
 * 参数表
 *
 */
public interface PubParamDao extends BaseMapper<PubParamPO> {

    List<PubParamPO> findByCodes(String[] codes);

    PubParamPO getParamByCode(@Param("code") String code);

    String getStringValue(@Param("code") String code);

    BigDecimal getDecimalValue(@Param("code") String code);

    Integer getIntegerValue(@Param("code") String code);

    Boolean getBooleanValue(@Param("code") String code);

}

