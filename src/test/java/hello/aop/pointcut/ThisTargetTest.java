package hello.aop.pointcut;

import hello.aop.member.MemberService;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

/**
 * application.properties                   <- 이 파일에 설정해주어도 되고,
 *                                           테스트에서 간단하게 보려면 아래처럼 @SpringBootTest 어노테이션에 달아줘도 된다.
 * spring.aop.proxy-target-class=true       CGLIB 로 동작   =>> 기본값임
 * spring.aop.proxy-target-class=false      JDK 동적 프록시로 동작
 */
@Slf4j
@Import({ThisTargetTest.ThisTargetAspect.class})
//@SpringBootTest(properties = "spring.aop.proxy-target-class=false") //JDK 동적 프록시로 돌게 설정.
@SpringBootTest(properties = "spring.aop.proxy-target-class=true") //CGLIB 로 돌게 설정(스프링 부트 기본 옵션)
public class ThisTargetTest {
    @Autowired
    MemberService memberService;

    @Test
    void success() {
        log.info("memverService Proxy{}", memberService.getClass());
        memberService.hello("helloA");
    }

    @Slf4j
    @Aspect
    static class ThisTargetAspect {

        @Around("this(hello.aop.member.MemberService)") // 부모 타입 허용
        public Object doThisInterface(ProceedingJoinPoint joinPoint) throws Throwable {
            log.info("[this-interface] {}", joinPoint.getSignature());
            return joinPoint.proceed();
        }
        @Around("target(hello.aop.member.MemberService)") // 부모 타입 허용
        public Object doTargetInterface(ProceedingJoinPoint joinPoint) throws Throwable {
            log.info("[target-interface] {}", joinPoint.getSignature());
            return joinPoint.proceed();
        }

        /**
         * jdk 동적 프록시를 사용하게 설정하면,
         * jdk 동적 프록시는 MemberService 인터페이스로 만들어졌기 때문에
         * 프록시 객체를 대상으로 매칭하는 this 는
         * 프록시 객체중에 없는 impl 객체를 찾으려하니 없을 수 밖에 없다.
         */
        @Around("this(hello.aop.member.MemberServiceImpl)") // 부모 타입 허용
        public Object doThis(ProceedingJoinPoint joinPoint) throws Throwable {
            log.info("[this-impl] {}", joinPoint.getSignature());
            return joinPoint.proceed();
        }
        @Around("target(hello.aop.member.MemberServiceImpl)")
        public Object doTarget(ProceedingJoinPoint joinPoint) throws Throwable {
            log.info("[target-impl] {}", joinPoint.getSignature());
            return joinPoint.proceed();
        }
    }
}
