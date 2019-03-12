package io.github.pleuvoir.springboot.example.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.github.pleuvoir.springboot.example.dao.PubParamDao;
import io.github.pleuvoir.springboot.example.dao.PubParamPO;

@Service
public class PubParamServiceImpl implements PubParamService {

	@Autowired
	private PubParamDao pubParamDao;

	@Override
	public List<PubParamPO> findByCodes(String[] codes) {
		return pubParamDao.findByCodes(codes);
	}


}