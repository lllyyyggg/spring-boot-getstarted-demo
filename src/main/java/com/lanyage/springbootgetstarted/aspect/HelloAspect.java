package com.lanyage.springbootgetstarted.aspect;

import com.lanyage.springbootgetstarted.web.HelloController;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;


@Aspect
//@Component
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
