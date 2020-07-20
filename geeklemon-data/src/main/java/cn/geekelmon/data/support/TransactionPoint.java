package cn.geekelmon.data.support;

import java.lang.reflect.Method;
import java.sql.Connection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.geekelmon.data.annotation.Transaction;
import cn.geeklemon.core.aop.support.PointResult;
import cn.geeklemon.core.aop.support.ProxyChain;
import cn.geeklemon.core.aop.support.ProxyResult;
import cn.geeklemon.core.aop.support.VoidPoint;
import cn.hutool.core.annotation.AnnotationUtil;
import net.sf.cglib.proxy.MethodProxy;

public class TransactionPoint implements VoidPoint {
	private static final Logger LOGGER = LoggerFactory.getLogger(TransactionPoint.class);
	JdbcExecutorFactory factory;

	public TransactionPoint(JdbcExecutorFactory factory) {
		this.factory = factory;
	}

	@Override
	public void proceed(Object target, Method method, Object[] args, ProxyChain chain, MethodProxy methodProxy,
			PointResult pointResult) throws Throwable {
		Transaction transaction = AnnotationUtil.getAnnotation(method, Transaction.class);

		if (transaction != null) {
			LJdbcExecutor excutor = null;
			try {
				excutor = factory.getLJdbcExcutor(true);
				excutor.getConnection().setAutoCommit(false);

				ProxyResult result = chain.proceed(target, method, args, methodProxy, pointResult);
				if (result.exceptionCase()) {
					excutor.getConnection().rollback();
					LOGGER.warn("transaction rollback case ： {}", result.getException());
				} else {
					excutor.getConnection().commit();
				}
			} catch (Exception e) {
			} finally {
				excutor.forceClose();
			}
		} else {

			chain.proceed(target, method, args, methodProxy, pointResult);
		}

	}

}
