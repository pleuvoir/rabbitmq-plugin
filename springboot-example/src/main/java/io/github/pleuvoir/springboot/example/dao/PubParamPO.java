package io.github.pleuvoir.springboot.example.dao;

import java.io.Serializable;
import java.math.BigDecimal;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;

/**
 * 动态参数表
 */
@TableName("pub_param")
public class PubParamPO implements Serializable {

	private static final long serialVersionUID = 44601249676525022L;
	
    @TableId("code")
	private String code;			//参数编号
	
    @TableField("name")
	private String name;			//参数名称
	
    @TableField("group_code")
	private String groupCode;  		//分组名
	
    @TableField("decimal_val")
	private BigDecimal decimalVal;	//decimal类型
	
    @TableField("int_val")
	private Integer intVal;			//int类型
	
    @TableField("str_val")
	private String strVal;			//字符串类型
	
    @TableField("boolean_val")
	private Boolean booleanVal;     //布尔类型
    
    @TableField("type")
	private String type;			//参数类型	1：decimal 2：int 3：string 4：boolean
	
    @TableField("modify_flag")
	private String modifyFlag;		//是否可修改		1：允许；0：不允许
	
    @TableField("remark")
	private String remark;			//描述

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getGroupCode() {
		return groupCode;
	}

	public void setGroupCode(String groupCode) {
		this.groupCode = groupCode;
	}

	public BigDecimal getDecimalVal() {
		return decimalVal;
	}

	public void setDecimalVal(BigDecimal decimalVal) {
		this.decimalVal = decimalVal;
	}

	public Integer getIntVal() {
		return intVal;
	}

	public void setIntVal(Integer intVal) {
		this.intVal = intVal;
	}

	public String getStrVal() {
		return strVal;
	}

	public void setStrVal(String strVal) {
		this.strVal = strVal;
	}

	public Boolean getBooleanVal() {
		return booleanVal;
	}

	public void setBooleanVal(Boolean booleanVal) {
		this.booleanVal = booleanVal;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getModifyFlag() {
		return modifyFlag;
	}

	public void setModifyFlag(String modifyFlag) {
		this.modifyFlag = modifyFlag;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

}
