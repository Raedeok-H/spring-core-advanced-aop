package hello.aop.proxyvs;

import hello.aop.member.MemberService;
import hello.aop.member.MemberServiceImpl;
import hello.aop.proxyvs.code.ProxyDIAspect;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@Slf4j
//@SpringBootTest(properties = {"spring.aop.proxy-target-class=false"}) //JDK 동적 프록시를 우선 사용하기 위한 세팅(여기서 설정하면 테스트마다 다르게 할 수 있다.)
                                                                        //인터페이스가 없으면 CGLIB를 사용한다.
                                                                        //  ->없으면 사용한다는 것이지, 인터페이스가 있는데 구체클래스를 주입받으려하면 안된다.
@SpringBootTest(properties = {"spring.aop.proxy-target-class=true"}) //CGLIB 프록시
@Import(ProxyDIAspect.class)
public class ProxyDITest {
    @Autowired
    MemberService memberService;

    @Autowired
    MemberServiceImpl memberServiceImpl; //CGLIB 프록시일 때만 가능

    @Test
    void go() {
        log.info("memberService class={}", memberService.getClass());
        log.info("memberServiceImpl class={}", memberServiceImpl.getClass()); //CGLIB 프록시일 때만 가능
        memberServiceImpl.hello("hello");//CGLIB 프록시일 때만 가능
    }
}
