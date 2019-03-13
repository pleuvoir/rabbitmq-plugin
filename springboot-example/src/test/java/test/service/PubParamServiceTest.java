package test.service;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import io.github.pleuvoir.springboot.example.service.PubParamService;
import test.BaseTest;

public class PubParamServiceTest extends BaseTest{
	
	@Autowired
	private PubParamService paramService;
	
	//@Transactional
	@Test
	public void testGetSignKey() {
		
		paramService.saveAndUpdate9999();
	}
}
