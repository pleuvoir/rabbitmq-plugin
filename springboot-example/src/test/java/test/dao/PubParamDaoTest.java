package test.dao;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import io.github.pleuvoir.springboot.example.dao.PubParamDao;
import test.BaseTest;

public class PubParamDaoTest extends BaseTest{
	
	@Autowired
	private PubParamDao pubParamDao;
	
	@Test
	public void test() {
		
		Integer integerValue = pubParamDao.getIntegerValue(String.valueOf(System.identityHashCode(this)));
		
		Assert.assertNull(integerValue);
	}
}
