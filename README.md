### 属性文件
获取配置文件里面的属性。`@Value("${hello}")`

运行时命令行属性。 `jar xxx --hello=lanyage`

指定配置文件。`jar xxx -Dspring.config.location=/User/lanyage/application.properites`,默认是resources下的`application.properties`

不同的环境不同的配置。用`---`分割

```
spring:
  application:
    name: started-project
server:
  port: 8080


---

spring:
  profiles: prod
  application:
    name: started-project-prod
server:
  port: 9090
```

再指定`--spring.profiles.active`属性。`java -jar ./spring-boot-getstarted-0.0.1-SNAPSHOT.jar --spring.profiles.active=prod`

封装属性文件到对象

```
@Component
@PropertySource(value = "classpath:test.properties")
public class TestProperties {
    @Value("${test.username}")
    private String username;
    @Value("${test.password}")
    private String password;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "TestProperties{" +
                "username='" + username + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
```
通过`@Autowired`来注入。
### 持久层配置

```
spring:
  application:
    name: started-project
  datasource:
    url: jdbc:mysql://localhost:3306/test
    username: root
    password: 920725
    driver-class-name: com.mysql.cj.jdbc.Driver
```
```
@Mapper
public interface UserMapper {

    @Select("select * from sys_user")
    List<User> list();
}
```

### 分页

```
<dependency>
            <groupId>com.github.pagehelper</groupId>
            <artifactId>pagehelper-spring-boot-starter</artifactId>
            <version>1.2.5</version>
</dependency>

```
配置

```
pagehelper:
  helper-dialect: mysql
  reasonable: true
  support-methods-arguments: true
  params: count=countSql
```

代码

```
@Service
public class UserService {

    @Autowired
    private UserMapper userMapper;

    public PageInfo<User> loadUsers(int page, int size){
        PageHelper.startPage(page, size);   //设置起始页和每页大小
        List<User> users = userMapper.list();
        PageInfo<User> userPageInfo = new PageInfo<>(users);    //封装成为pageInfo
        return userPageInfo;
    }
}
```

### 事务

`@EnableTransactionManagement(proxyTargetClass = false)`

分布式事务

分布式事务是多数据源之间的事务。Java的解决方案是JTA。springboot官方提供了`Atomikos`,`Bitronix`,`Narayana`类事务管理器。

```
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-jta-atomikos</artifactId>
</dependency>
```
然后配置多数据源

配置`MybatisConfig.java`

```
public class MyBatisConfig1 {
    @Primary
    @Bean(name = "testDataSource")
    public DataSource testDataSource(@Qualifier("testConfig") DBConfig1 testConfig) throws SQLException {
        MysqlXADataSource mysqlXaDataSource = new MysqlXADataSource();
        mysqlXaDataSource.setUrl(testConfig.getUrl());
        mysqlXaDataSource.setPinGlobalTxToPhysicalConnection(true);
        mysqlXaDataSource.setPassword(testConfig.getPassword());
        mysqlXaDataSource.setUser(testConfig.getUsername());
        mysqlXaDataSource.setPinGlobalTxToPhysicalConnection(true);

        AtomikosDataSourceBean xaDataSource = new AtomikosDataSourceBean();
        xaDataSource.setXaDataSource(mysqlXaDataSource);
        xaDataSource.setUniqueResourceName("testDataSource");

        xaDataSource.setMinPoolSize(testConfig.getMinPoolSize());
        xaDataSource.setMaxPoolSize(testConfig.getMaxPoolSize());
        xaDataSource.setMaxLifetime(testConfig.getMaxLifetime());
        xaDataSource.setBorrowConnectionTimeout(testConfig.getBorrowConnectionTimeout());
        xaDataSource.setLoginTimeout(testConfig.getLoginTimeout());
        xaDataSource.setMaintenanceInterval(testConfig.getMaintenanceInterval());
        xaDataSource.setMaxIdleTime(testConfig.getMaxIdleTime());
        xaDataSource.setTestQuery(testConfig.getTestQuery());
        return xaDataSource;
    }

    @Primary
    @Bean(name = "testSqlSessionFactory")
    public SqlSessionFactory testSqlSessionFactory(@Qualifier("testDataSource") DataSource dataSource)
            throws Exception {
        SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
        bean.setDataSource(dataSource);
        return bean.getObject();
    }

    @Primary
    @Bean(name = "testSqlSessionTemplate")
    public SqlSessionTemplate testSqlSessionTemplate(
            @Qualifier("testSqlSessionFactory") SqlSessionFactory sqlSessionFactory) throws Exception {
        return new SqlSessionTemplate(sqlSessionFactory);
    }
    
    @Bean(name="testTransactionManager")
    public UserTransactionManager userTransactionManager() {
	    UserTransactionManager userTransactionManager = new UserTransactionManager();
	    userTransactionManager.setForceShutdown(false);
	    return userTransactionManager;
	}

}
```

关键是`MysqlXADataSource`和`AtomikosDataSourceBean`和`UserTransactionManager`。

### 定时任务
`@EnableScheduling`来开启定时任务。

代码

```
@Component
public class ScheduleJobs {

    @Scheduled(fixedDelay = 1000 * 2)
    public void fixedDelayJob() {
        System.out.println("[Fixed Delay Job Executed]" + System.currentTimeMillis());
    }
}
```

Quartz任务调度

```
<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-quartz</artifactId>
</dependency>

```

配置`JobDetail`和`Trigger`。

```
@Configuration
public class QuartzConfig {
    private static final Logger logger = LoggerFactory.getLogger(QuartzConfig.class);
    @Bean(name = "helloJob")
    public JobDetail helloTaskDetail() {
        logger.info("[{}] is going to be created as a bean.","JobDetail");
        return JobBuilder.newJob(HelloTask.class).withIdentity("helloTask").storeDurably().build();
    }

    @Bean(name = "helloTrigger")
    public Trigger helloTaskTrigger() {
        logger.info("[{}] is going to be created as a bean.","Trigger");
        CronScheduleBuilder cronScheduleBuilder = CronScheduleBuilder.cronSchedule("*/5 * * * * ?");
        return TriggerBuilder.newTrigger().forJob(helloTaskDetail()).withIdentity("helloTaskTrigger")
                .withSchedule(cronScheduleBuilder).build();
    }
}
```
配置`QuartzJobBean`。

```
@Configuration
//@DisallowConcurrentExecution  //禁止并发执行
public class HelloTask extends QuartzJobBean {
    @Override
    protected void executeInternal(JobExecutionContext jobExecutionContext) {
        System.out.println("任务开始");
        System.out.println("Hello World" + System.currentTimeMillis());
        System.out.println("任务结束");
    }
}
```

AOP切面编程

```
<dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-aop</artifactId>
</dependency>
```

编写切面类

```
@Aspect
@Component
//@Scope
public class HelloAspect {

    private static final Logger logger = LoggerFactory.getLogger(HelloAspect.class);

    @Pointcut(value = "execution(public * com.lanyage.springbootgetstarted.web.*.*(..)) && within(com.lanyage.springbootgetstarted.web.*)")
    public void helloPointCut() {
    }

    @Before("helloPointCut()")
    public void doBefore(JoinPoint joinPoint) throws NoSuchMethodException {
        logger.info("the point cut method is : [{}]", joinPoint.getSignature().getName());
        //获取切面对应的方法及其所有的注解
        System.out.println(joinPoint.getTarget().getClass().getMethod("hello"));
    }

    @AfterReturning(value = "helloPointCut()", returning = "ret")
    public void doAfterReturning(JoinPoint joinPoint, Object ret) {
        logger.info("the return result = [{}]", ret);
    }

    @Around(value = "helloPointCut()")
    public Object doAround(ProceedingJoinPoint proceedingJoinPoint) {
        Object result = null;
        try {
            result = proceedingJoinPoint.proceed();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        } finally {
            return result;
        }
    }
}

```

### 拦截器

拦截器基于反射，过滤器基于函数回调，拦截器不依赖于servlet，而过滤器依赖servlet，拦截器仅仅对controller起作用，filter对几乎所有请求起作用。拦截器在filter后面执行。拦截器用途：拦截未登录用户，审计日志。

常用的接口有`HandlerInterceptor`等,这个类可以对指定的url进行拦截。然后在`WebMvcConfigurer`的实现类中配置。

```
@Configuration
public class WebAppConfig implements WebMvcConfigurer {

    @Autowired
    private LoginInterceptor loginInterceptor;

    private static final Logger logger = LoggerFactory.getLogger(WebAppConfig.class);

    //配置拦截器
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        logger.info("[{}] has been created", "WebMvcConfigurer");
        registry.addInterceptor(loginInterceptor).addPathPatterns("/*");
    }
}
```
> `WebMvcConfigurer`是一个配置类的接口，基本上所有web配置都可以在这里配置。

### 缓存
spring boot中`@EnableCaching`注解自动化配置合适的缓存管理器。按照如下的顺序去侦测缓存提供者:Generic ， JCache (JSR-107)， EhCache 2.x ，Hazelcast ， Infinispan ，Redis ，Guava ， Simple。

> ehcache
   
```
<dependency>
		<groupId>org.springframework.boot</groupId>
		<artifactId>spring-boot-starter-cache</artifactId>
</dependency>

<dependency>
		<groupId>net.sf.ehcache</groupId>
		<artifactId>ehcache</artifactId>
</dependency>
```

配置ehcache.xml。

```
<?xml version="1.0" encoding="UTF-8"?>
<ehcache xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:noNamespaceSchemaLocation="http://ehcache.org/ehcache.xsd"
         updateCheck="false">
    <diskStore path="java.io.tmpdir/Tmp_EhCache" />
    <defaultCache eternal="false" maxElementsInMemory="1000" overflowToDisk="false" diskPersistent="false"
                  timeToIdleSeconds="0" timeToLiveSeconds="600" memoryStoreEvictionPolicy="LRU" />

    <cache name="users" eternal="false" maxElementsInMemory="100" overflowToDisk="false" diskPersistent="false"
           timeToIdleSeconds="0" timeToLiveSeconds="300" memoryStoreEvictionPolicy="LRU" />
</ehcache>
```

```
spring:
	cache:
    ehcache:
      config: classpath:ehcache.xml
    type: ehcache
```
打开sql控制台日志。

```
logging:
  level:
    com:
      lanyage:
        springbootgetstarted:
          mapper: debug
```
创建配置类。

```
@Configuration
@EnableCaching
public class EhCacheConfig {
    private static final Logger logger = LoggerFactory.getLogger(EhCacheConfig.class);
    @Bean(name = "ehcacheManager")
    public EhCacheCacheManager ehcacheManager(@Qualifier("ehcacheManagerFactoryBean") EhCacheManagerFactoryBean bean) {
        logger.info("Creating [{}]'s instance.", "EhCacheCacheManager");
        return new EhCacheCacheManager(bean.getObject());
    }

    @Bean(name = "ehcacheManagerFactoryBean")
    public EhCacheManagerFactoryBean ehcacheManagerFactoryBean() {
        logger.info("Creating [{}]'s instance.", "EhCacheManagerFactoryBean");
        EhCacheManagerFactoryBean ehCacheManagerFactoryBean = new EhCacheManagerFactoryBean();
        ehCacheManagerFactoryBean.setConfigLocation(new ClassPathResource("ehcache.xml"));
        ehCacheManagerFactoryBean.setShared(true);
        return ehCacheManagerFactoryBean;
    }
}
```
然后在service或者dao上面进行配置。

```
@CacheConfig(cacheNames = "users", cacheManager = "ehcacheManager")
public class UserService
```
然后再就是`@Cachable`,`@Cacheput`,`@CacheEvit`等注解进行增删改查了。这些注解的核心就是将方法的返回值存入缓存，等到下次利用同样的参数来调用该方法时将不在执行方法，而是直接从缓存中拿数据。

每个注解都有`value``key``condition`三个属性。

`@Cachable`是将方法的返回结果存入缓存，并且`key`和`condition`都支持EL表达式。

`@CachePut`不会去检查缓存中是否存在之前执行过的结果，而是每次都会执行该方法，并将执行结果以键值对的形式存入指定的缓存中。

`@CacheEvict`标示对指定的缓存进行清除，可以是`entireEntry`和`beforeInvocation`

`@Caching`可以在一个方法或类上指定多个Spring Cache相关注解。

> redis

```
<dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-redis</artifactId>
</dependency>
```
单机配置

```
#redis
# Redis数据库索引（默认为0）
spring.redis.database=0
# Redis服务器地址
spring.redis.host=127.0.0.1
# Redis服务器连接端口
spring.redis.port=6379
# Redis服务器连接密码（默认为空）
spring.redis.password=
# 连接池最大连接数（使用负值表示没有限制）
spring.redis.pool.max-active=8
# 连接池最大阻塞等待时间（使用负值表示没有限制）
spring.redis.pool.max-wait=-1
# 连接池中的最大空闲连接
spring.redis.pool.max-idle=8
# 连接池中的最小空闲连接
spring.redis.pool.min-idle=0
# 连接超时时间（毫秒）
spring.redis.timeout=0
```
集群模式

```
#Matser的ip地址  
redis.hostName=192.168.177.128
#端口号  
redis.port=6382
#如果有密码  
redis.password=
#客户端超时时间单位是毫秒 默认是2000 
redis.timeout=10000  

#最大空闲数  
redis.maxIdle=300  
#连接池的最大数据库连接数。设为0表示无限制,如果是jedis 2.4以后用redis.maxTotal  
#redis.maxActive=600  
#控制一个pool可分配多少个jedis实例,用来替换上面的redis.maxActive,如果是jedis 2.4以后用该属性  
redis.maxTotal=1000  
#最大建立连接等待时间。如果超过此时间将接到异常。设为-1表示无限制。  
redis.maxWaitMillis=1000  
#连接的最小空闲时间 默认1800000毫秒(30分钟)  
redis.minEvictableIdleTimeMillis=300000  
#每次释放连接的最大数目,默认3  
redis.numTestsPerEvictionRun=1024  
#逐出扫描的时间间隔(毫秒) 如果为负数,则不运行逐出线程, 默认-1  
redis.timeBetweenEvictionRunsMillis=30000  
#是否在从池中取出连接前进行检验,如果检验失败,则从池中去除连接并尝试取出另一个  
redis.testOnBorrow=true  
#在空闲时检查有效性, 默认false  
redis.testWhileIdle=true  

#redis集群配置      
spring.redis.cluster.nodes=192.168.177.128:7001,192.168.177.128:7002,192.168.177.128:7003,192.168.177.128:7004,192.168.177.128:7005,192.168.177.128:7006
spring.redis.cluster.max-redirects=3

#哨兵模式
#redis.sentinel.host1=192.168.177.128
#redis.sentinel.port1=26379

#redis.sentinel.host2=172.20.1.231  
#redis.sentinel.port2=26379
```

然后其他的配置就和ehcache的操作一样了。

当然还有一些好的特性，如webflux和websocket,和devtools热部署等等高级特性。