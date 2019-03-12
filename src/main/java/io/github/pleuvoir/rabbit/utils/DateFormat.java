package io.github.pleuvoir.rabbit.utils;

import org.apache.commons.lang3.StringUtils;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public enum DateFormat {
	DATE_DEFAULT("yyyy-MM-dd"),
	DATE_COMPACT("yyyyMMdd"),
	TIME_DEFAULT("HH:mm:ss"),
	NOT_SS_DEFAULT("yyyy-MM-dd HH:mm"),
	DATETIME_DEFAULT("yyyy-MM-dd HH:mm:ss"),
	DATETIME_COMPACT("yyyyMMddHHmmss"),
	DATETIME_MILLISECOND("yyyy-MM-dd HH:mm:ss:SSS"),
	DATETIME_MILLISECOND_1("yyyy-MM-dd HH:mm:ss.SSS"),
	DATETIME_MILLISECOND_COMPACT("yyyyMMddHHmmssSSS"),
	/** 紧凑的时间格式：HHmmss */
	TIME_COMPACT("HHmmss");
	
	
	private String partten;
	private DateTimeFormatter formatter;
	
	private DateFormat(String partten){
		this.partten = partten;
		this.formatter = DateTimeFormatter.ofPattern(partten);
	}
	
	/**
	 * 获取格式字符串
	 * @return
	 */
	public String getPartten(){
		return this.partten;
	}
	
	/**
	 * 获取一个新的{@link SimpleDateFormat}对象
	 * @return
	 */
	public SimpleDateFormat get(){
		return new SimpleDateFormat(this.partten);
	}

	/**
	 * 格式化一个{@link LocalDateTime}日期对象，参数为空时返回空字符串
	 */
	public String format(LocalDateTime datetime){
		if(datetime==null){
			return StringUtils.EMPTY;
		}
		return formatter.format(datetime);
	}

	/**
	 * 格式化一个{@link LocalDate}日期对象，参数为空时返回空字符串
	 */
	public String format(LocalDate date){
		if(date==null){
			return StringUtils.EMPTY;
		}
		return formatter.format(date);
	}

	/**
	 * 格式化一个{@link Date}日期对象，参数为空时返回空字符串
	 */
	public String format(Date date){
		if(date==null){
			return StringUtils.EMPTY;
		}
		return get().format(date);
	}

	/**
	 * 解析字符串格式的日期，参数为空时返回null
	 */
	public LocalDateTime parse(String datetime){
		if(StringUtils.isBlank(datetime)){
			return null;
		}
		return LocalDateTime.parse(datetime, formatter);
	}
}
