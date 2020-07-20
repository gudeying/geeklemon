//package cn.geekelmon.data;
//
//import cn.geekelmon.data.session.TransactionFactory;
//import cn.geeklemon.core.context.annotation.Bean;
//import cn.hutool.core.lang.ClassScaner;
//import org.apache.tools.ant.types.Mapper;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import javax.sql.DataSource;
//import java.io.*;
//import java.util.LinkedList;
//import java.util.List;
//import java.util.Set;
//
///**
// * @author : Kavin Gu
// * Project Name : redant
// * Description :
// * @version : ${VERSION} 2019/2/26 9:15
// * Modified by : kavingu
// */
//public class MybatisAutoConfiguration {
//    private static final Logger LOGGER = LoggerFactory.getLogger(MybatisAutoConfiguration.class);
//    private LemonBeanContext context;
//    private DataSource dataSource;
//    private Configuration configuration;
//    private SqlSessionFactory sessionFactory;
//    private SqlSession sqlSession;
//
//    public MybatisAutoConfiguration() {
//        this.dataSource = getDataSource();
//        this.configuration = getMybatisConfiguration(dataSource);
//        try {
//            this.sessionFactory = buildMybatisSessionFactory(configuration);
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }
//        this.sqlSession = openSession();
//        this.context = LemonBeanContext.getInstance();
//        registerMapper();
//    }
//
//    private DataSource getDataSource() {
//        try {
//            return com.redant.core.common.Environment.getBean("dataSource", MysqlDataSource.class);
//        } catch (IllegalAccessException e) {
//            e.printStackTrace();
//        } catch (InstantiationException e) {
//            e.printStackTrace();
//        }
//        return null;
//    }
//
//    private Configuration getMybatisConfiguration(DataSource dataSource) {
//        TransactionFactory transactionFactory = new JdbcTransactionFactory();
//        Environment environment = new Environment("defaultEnvironment", transactionFactory, dataSource);
//        Configuration configuration = new Configuration(environment);
//        configuration.setLazyLoadingEnabled(true);
//        return configuration;
//    }
//
//    private SqlSessionFactory buildMybatisSessionFactory(Configuration configuration) throws FileNotFoundException {
//        String mapperPackage = com.redant.core.common.Environment.getStrByGroup("mybatis","mapperPackage");
//        List<File> resources = getFileList(mapperPackage, ".xml");
//        LOGGER.info("扫描mapper包：" + mapperPackage + ",mapper数量：" + resources.size());
//        for (File resource : resources) {
//            InputStream inputStream = new FileInputStream(resource);
//            XMLMapperBuilder builder = new XMLMapperBuilder(inputStream, configuration, resource.getAbsolutePath(), configuration.getSqlFragments());
//            builder.parse();
//        }
//        SqlSessionFactory sqlSessionFactory = new DefaultSqlSessionFactory(configuration);
//        return sqlSessionFactory;
//
//    }
//
//    private List<File> getFileList(String path, String filter) {
//        File file = new File(path);
//        if (file.isDirectory()) {
//
//            File[] files = file.listFiles(new FileFilter() {
//                @Override
//                public boolean accept(File pathname) {
//                    if (pathname.getAbsolutePath().toUpperCase().contains(filter.toUpperCase())) {
//                        return true;
//                    }
//                    return false;
//                }
//            });
//            List<File> resources = new LinkedList<>();
//            for (File file1 : files) {
//                resources.add(file1);
//            }
//            return resources;
//        }
//        return new LinkedList<>();
//    }
//
//    private SqlSession openSession() {
//        return this.sessionFactory.openSession();
//    }
//
//    public LemonBeanContext getContext() {
//        return context;
//    }
//
//    public void setContext(LemonBeanContext context) {
//        this.context = context;
//    }
//
//    public void setDataSource(DataSource dataSource) {
//        this.dataSource = dataSource;
//    }
//
//    public Configuration getConfiguration() {
//        return configuration;
//    }
//
//    public void setConfiguration(Configuration configuration) {
//        this.configuration = configuration;
//    }
//
//    @Bean(name = "sqlSessionFactory")
//    public SqlSessionFactory getSessionFactory() {
//        return sessionFactory;
//    }
//
//    public void setSessionFactory(SqlSessionFactory sessionFactory) {
//        this.sessionFactory = sessionFactory;
//    }
//
//    public SqlSession getSqlSession() {
//        return sqlSession;
//    }
//
//    public void setSqlSession(SqlSession sqlSession) {
//        this.sqlSession = sqlSession;
//    }
//
//    public void registerMapper(){
//        Set<Class<?>> classSet = ClassScaner.scanPackageByAnnotation(CommonConstants.BEAN_SCAN_PACKAGE, Mapper.class);
//        for (Class<?> cls:classSet){
//            String name = cls.getName();
//            Object instance = sqlSession.getMapper(cls);
//            context.registerBean(name,cls.cast(instance));
//        }
//    }
//}
