package cn.geeklemon.core.util;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.lang.Assert;
import cn.hutool.setting.dialect.Props;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * @author : Kavin Gu Project Name : geeklemon Description :
 * @version : ${VERSION} 2019/9/10 14:31 Modified by : kavingu
 */
public class PropsUtil {
	private static final Logger LOGGER = LoggerFactory.getLogger(PropsUtil.class);
	/**
	 * 每一行字符串
	 */
	private List<String> lineList = new LinkedList<>();
	private Map<Object, Object> valueMap = new HashMap<Object, Object>(100);

	private static Set<String> propsFiles = new HashSet<>();

	private static PropsUtil ourInstance = new PropsUtil();

	public static PropsUtil getInstance() {
		return ourInstance;
	}

	private PropsUtil() {
		propsFiles.add("application.properties");
		init();
	}

	private void init() {
		for (String propsFile : propsFiles) {
			try {
				LOGGER.info("读取配置文件{}", propsFile);
				Props props = new Props(propsFile);
				valueMap.putAll(props);
			} catch (Exception ignored) {
			}
		}
	}

	public <T> T getValue(Class<T> cls, String name, T defaultValue) {
		Object stringValue = valueMap.get(name);
		return Convert.convert(cls, stringValue, defaultValue);
	}

	public <T> T getValue(Class<T> cls, String name) {
		Assert.notNull(cls, "{} 目标class不能为空" + cls.getName());
		Assert.notEmpty(name, "{} 配置名称不能为空", name);
		return getValue(cls, name, null);
	}

	public void addValue(String name, Object value) {
		valueMap.put(name, value);
	}

	public static void addPropFile(String file) {
		propsFiles.add(file);
	}
}
