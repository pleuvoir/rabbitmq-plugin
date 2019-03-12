package io.github.pleuvoir.springboot.example.service;

import java.util.List;

import io.github.pleuvoir.springboot.example.dao.PubParamPO;

public interface PubParamService {

	/**
	 * 通过code数组获取参数
	 */
	List<PubParamPO> findByCodes(String[] codes);

}
