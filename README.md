# geeklemon

**geeklemon** 包含ioc、aop、jdbc、netty Server、cache、cluster的解决。<br/>
各个模块示例均可在geeklemon-example模块中找到

 **geeklemon-core:**<br/>
一个注解配置的ioc、aop容器，使用注解注入。<br/>
提供了一些扩展支持，以下几个项目均为其扩展。<br/>
默认提供了一个方法拦截器拦截方法中的异常，使用@ExceptionAvoid注解，可以通过自定义的异常处理器返回默认结果而不抛出异常。
下面是启动方式以及相关功能开启
````java
@GeekLemonApplication//启动容器
@WebApplication//自动扫描web组件
@EnableDataConfig//自动扫描data并自动注入代理
@EnableCache//自动扫描缓存配置
public class MainStart {
    public static void main(String[] args) {
        ApplicationContext context = LemonApplication.run(MainStart.class);
    }
}
````
之后在项目中可以使用相关自动配置。示例：
````java
@Bean()
public class DataSourceConfig {
    /**
    * 自动装配application.properties文件中的值
    */
    @Value(name = "lemon.data.url")
    private String url;
    @Value(name = "lemon.data.user")
    private String user;
    @Value(name = "lemon.data.password")
    private String password;
    /**
    *自动装配管理的依赖类 
    */
    @Autowird
    private User user;
/**
* 向容器中注入dataSource对象，其他被管理的类可以直接使用
*/
    @Bean(single = true)
    public DataSource dataSource() {
        DruidDataSource druidDataSource = new DruidDataSource();
        druidDataSource.setUrl(url);
        druidDataSource.setUsername(user);
        druidDataSource.setPassword(password);
        druidDataSource.setMaxActive(50);
        druidDataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");

        return druidDataSource;
    }
}
````
关于aop的使用，提供了两种方式，注解标记和表达式标记
````java
@AopProxy
public class LogAop {

    /**
    *有Log标记的方法将被拦截
    */
    @AopPoint(type = AopType.AROUND, value = Log.class)
    public void log(PointDefine define) {
        System.out.println("around log");
    }
    /**
     *cn.geekelmon.app.api.service.VersionService.lastVersion()这个方法被拦截
    */
    @PointCut(value = "cn.geekelmon.app.api.service.VersionService.lastVersion()")
    public void str(PointDefine define) {
        System.out.println("查询版本 around");
    }
}
````
 **geeklemon-server:** <br/>
使用netty实现的http服务器，支持http和websocket。<br/>
只需使用@Controller标注类，@Mapping指定处理方法即可快速开发。<br/>
方法支持参数自动绑定，包括基础类型绑定，bean转换，MultiFile文件上传绑定<br/>
支持路径参数<br/>
支持模板渲染<br/>
支持自动解析返回json、xml<br/>

以下是一些使用示例：
````java
@Controller//标记为请求处理器
public class ArticleController {
	@Autowired
	private ArticleService service;
	//统一异常处理器，代码中有Integer.parseInt(id)会抛出异常，但是服务器不会返回500，因为被处理了
	@ExceptionAvoid(handler = ControllerExceptionHandler.class)
	//请求路径，默认返回json。
	@Mapping(path = "/app/info/article/detail/${id}") 
	/*
	* 参数自动绑定，支持路径参数
    */
	public ApiEntity<Article> pathDetail(@Param(key = "id") String id, HttpRequest request) throws Exception {
		String userOpenId = request.getParameter("userOpenId");
		Article detail = service.detail(Integer.parseInt(id), userOpenId);
		return new ApiEntity<>(detail);
	}
}
````
其他使用方法：
````java
/**
* 渲染返回template目录下文件名为leaf.html的网页
* 模板路径、前缀、后缀可以配置
*/
	@Mapping(path = "/admin-scripts.asp", renderType = RenderType.HTML)
	public String strIndex(ModelAndView modelAndView) {
		modelAndView.addAttribute("content", "Admin Access Get");
		return "leaf";
	}
````
也可以直接操作response，下面是一个使用hutool工具响应excel文件的示例
体验地址:http://49.234.192.162:8080/excel
````java
	@Mapping(path = "/excel")
	public void excel(HttpResponse response) {
		response.addHeader(HttpHeaderNames.CONTENT_TYPE, "application/vnd.ms-excel;charset=utf-8");
		response.addHeader("Content-Disposition", "attachment;filename=test.xls");
		OutputStream outputStream = response.getOutputStream();
		List<ArticlePreview> list = new LinkedList<>();
		for (int i = 0; i < 3000; i++) {
			ArticlePreview preview = new ArticlePreview();
			preview.setTitle("title" + i);
			list.add(preview);
		}
		ExcelWriter writer = ExcelUtil.getWriter();

		writer.write(list);
		writer.flush(outputStream);
		writer.close();
	}
````

 **geeklemon-data:**<br/>
 模仿mybatis mapper注解方式的数据库交互模块。<br/>
 通过定义接口，在接口方法中使用注解提供sql，可以在容器中注入代理类，使用@Autowired注解即可引入。<br/>
 支持注解参数绑定、自动参数绑定。<br/>
 支持单entity和数据库字段自动绑定，可通过注解进行映射。<br/>
 支持自定义SqlProvider提供动态sql<br/>
 提供service层的注解事务支持。并且事物不受异常处理注解拦截的影响<br/>
 
 在开头的@EnableDataConfig配置好之后，就可以方便地使用（需要容器中配置一个DataSource,上面有示例）
 ````java
 @LMapper//标注为mapper接口
 public interface UserInfoMapper {
     @LemonQuery(value = "select * from user where openid = ${openId}")
     UserInfo getUserByOpenId(String openId);
     /**
     *动态sql， sqlProviderMethod的方法参数必须和该方法一致
    */  
     @LemonQuery(sqlProviderClass=GalleryCountSqlProvider.class,sqlProviderMethod = "count")
     int count(String sql);
 }
 ````
 那么在其他文件中可以直接引用
 ````java
 @Bean(single = true)
 public class UserService {
    @Autowired
    private UserInfoMapper mapper;
    //省略使用
 }
 ````
 事务的使用
  ````java
 @Bean
 public class TransactionTestService {
 	@Autowired//自动注入的
 	private JdbcExecutorFactory factory;
 	@Autowired//mapper接口的代理实现
    private UserMapper userMapper;
 	@Transaction
 	public int save(Object object, boolean throwEx) {
        //使用 	JdbcExecutorFactory和UserMapper进行的操作将会在事务下进行
 	    return 0;
 	}
}
 ````
 **geeklemon=cache:**<br/>
 方法缓存拦截，使用简单，只需要 入口使用@EnnableCache注解，容器管理的类中使用@LCache即可完成拦截，提供了默认的缓存实现。<br/>
 支持自定义缓存实现，自定义缓存key生成以及缓存时间。<br/>
 
 使用@EnableCache自动配置之后，容器管理类中将可以使用缓存
  ````java
  @Bean(name = "articleService")
  public class ArticleService implements InitializingBean {
    /**
    *缓存拦截，使用默认的缓存实现需要注意参数的equals和hashCode 
    */
        @LCache(timeOut = 10 * 1000)
        public Article detail(int articleId, String queryUserOpenId){
            //省略从数据库获取数据以及return
        }
  }
````
  **geeklemon-cluster:**<br/>
  使用zookeeper做服务管理的服务注册、分发模块，server可注册至注册中心，访问cluster可以自动选择注册的server进行请求分发<br/>
  
   **示例:**<br/>
查看 geeklemon-example模块