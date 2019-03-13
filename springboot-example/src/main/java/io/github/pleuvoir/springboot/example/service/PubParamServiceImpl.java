package io.github.pleuvoir.springboot.example.service;

import java.math.BigDecimal;
import java.util.concurrent.ThreadLocalRandom;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import io.github.pleuvoir.springboot.example.dao.PubParamDao;
import io.github.pleuvoir.springboot.example.dao.PubParamPO;

@Service
public class PubParamServiceImpl implements PubParamService {

	@Autowired
	private PubParamDao pubParamDao;

	// 这里不加事务，在执行可靠消息时也会将业务操作和记录日志加入同一事务
	//@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Throwable.class)
	@Override
	public void saveAndUpdate9999() {
		PubParamPO po = new PubParamPO();
		po.setCode("9999");
		po.setName("9999测试");
		po.setDecimalVal(new BigDecimal("9999"));
		po.setIntVal(9999);
		pubParamDao.insert(po);

		po.setRemark("更新了");
		pubParamDao.updateById(po);
	}

	@Override
	public void saveAndUpdate9999WithException() {
		
		PubParamPO po = new PubParamPO();
		po.setCode("9999");
		po.setName("9999测试");
		po.setDecimalVal(new BigDecimal("9999"));
		po.setIntVal(9999);
		// 当出现数据库异常时会发现插入并未入库
		
		po.setRemark("saveAndUpdate9999WithException");
		Integer count = pubParamDao.updateById(po);
		
		if(count > 0) {
			pubParamDao.insert(po);	// 这里肯定会报唯一约束的错，可以查看上面的更新已经回滚了
		}
		
	}

}