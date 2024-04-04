package hello.aop.pointcut;

import hello.aop.member.MemberServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;

import java.lang.reflect.Method;

@Slf4j
public class ExecutionTest {
    AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
    Method helloMethod;

    @BeforeEach
    public void init() throws NoSuchMethodException {
        helloMethod = MemberServiceImpl.class.getMethod("hello", String.class);
    }

    @Test
    void printMethod() {
        // public java.lang.String hello.aop.member.MemberServiceImpl.hello(java.lang.String)
        log.info("helloMethod ={}", helloMethod);
    }

    @Test
    void exactMatch() { // 가장 정확한 포인트 컷(예외만 생략하고 사용)
        // public java.lang.String hello.aop.member.MemberServiceImpl.hello(java.lang.String)
        pointcut.setExpression("execution(public String hello.aop.member.MemberServiceImpl.hello(String))");
        Assertions.assertThat(pointcut.matches(helloMethod, MemberServiceImpl.class)).isTrue();
    }

    @Test
    void allMatch() { // 가장 많이 생략한 포인트 컷
        pointcut.setExpression("execution(* *(..))"); // 반환타입과 메서드 이름, 파라미터만 사용
        Assertions.assertThat(pointcut.matches(helloMethod, MemberServiceImpl.class)).isTrue();
    }

    @Test
    void nameMatchStar1() { //hel로 시작하는 메소드
        pointcut.setExpression("execution(* hel*(..))");
        Assertions.assertThat(pointcut.matches(helloMethod, MemberServiceImpl.class)).isTrue();
    }

    @Test
    void nameMatchStar2() { //중간에 el이 들어가는 메소드
        pointcut.setExpression("execution(* *el*(..))");
        Assertions.assertThat(pointcut.matches(helloMethod, MemberServiceImpl.class)).isTrue();
    }

    @Test
    void nameMatchFalse() {
        pointcut.setExpression("execution(* nono(..))");
        Assertions.assertThat(pointcut.matches(helloMethod, MemberServiceImpl.class)).isFalse();
    }

    @Test
    void packageExactMatch1() { //정확한 매칭
        pointcut.setExpression("execution(* hello.aop.member.MemberServiceImpl.hello(..))");
        Assertions.assertThat(pointcut.matches(helloMethod, MemberServiceImpl.class)).isTrue();
    }

    @Test
    void packageExactMatch2() { // member패키지 안의 모든 클래스들의 모든 메소드의 모든 파라미터
        pointcut.setExpression("execution(* hello.aop.member.*.*(..))");
        Assertions.assertThat(pointcut.matches(helloMethod, MemberServiceImpl.class)).isTrue();
    }

    @Test
    void packageExactFalse() { // aop 패키지만 조회해서 false
        pointcut.setExpression("execution(* hello.aop.*.*(..))");
        Assertions.assertThat(pointcut.matches(helloMethod, MemberServiceImpl.class)).isFalse();
    }
    @Test
    void packageMatchSubPackage1() { // member와 하위 패키지들
        pointcut.setExpression("execution(* hello.aop.member..*.*(..))");
        Assertions.assertThat(pointcut.matches(helloMethod, MemberServiceImpl.class)).isTrue();
    }
    @Test
    void packageMatchSubPackage2() { //aop와 하위 패키지들
        pointcut.setExpression("execution(* hello.aop..*.*(..))");
        Assertions.assertThat(pointcut.matches(helloMethod, MemberServiceImpl.class)).isTrue();
    }


    @Test
    void typeExactMatch() { // 정확한 매칭 예
        pointcut.setExpression("execution(* hello.aop.member.MemberServiceImpl.*(..))");
        Assertions.assertThat(pointcut.matches(helloMethod, MemberServiceImpl.class)).isTrue();
    }
    @Test
    void typeMatchSuperType() { // 부모타입으로 해도 자식타입이 매칭된다.
        pointcut.setExpression("execution(* hello.aop.member.MemberService.*(..))");
        Assertions.assertThat(pointcut.matches(helloMethod, MemberServiceImpl.class)).isTrue();
    }

    @Test
    void typeMatchInternal() throws NoSuchMethodException { // 자식 타입을 지정하고, 자식타입에만 있는 메소드 조회(당연히 됨)
        pointcut.setExpression("execution(* hello.aop.member.MemberServiceImpl.*(..))");
        Method internalMethod = MemberServiceImpl.class.getMethod("internal",String.class);
        Assertions.assertThat(pointcut.matches(internalMethod, MemberServiceImpl.class)).isTrue();
    }
    @Test
    void typeMatchNoSuperTypeMethodFalse() throws NoSuchMethodException { // 부모클래스로 자식 매칭 시, 부모 클래스에 있는 메소드만 자식클래스에서 허용됨
        pointcut.setExpression("execution(* hello.aop.member.MemberService.*(..))");
        Method internalMethod = MemberServiceImpl.class.getMethod("internal",String.class); // 부모클래스에 없는 메소드
        Assertions.assertThat(pointcut.matches(internalMethod, MemberServiceImpl.class)).isFalse();
    }


    //String 타입의 파라미터 허용
    //(String)
    @Test
    void argsMatch(){
        pointcut.setExpression("execution(* *(String))");
        Assertions.assertThat(pointcut.matches(helloMethod, MemberServiceImpl.class)).isTrue();
    }



    // 파라미터가 없는 것만 허용
    //()
    @Test
    void argsMatchNoArgs(){
        pointcut.setExpression("execution(* *())");
        Assertions.assertThat(pointcut.matches(helloMethod, MemberServiceImpl.class)).isFalse();
        //hello 메소드는 파라미터가 있기 때문에 매칭이 안 됨.
    }



    // 정확히 하나의 파라미터 허용, 단 모든 타입을 허용한다.
    // (*)
    @Test
    void argsMatchStar(){
        pointcut.setExpression("execution(* *(*))");
        Assertions.assertThat(pointcut.matches(helloMethod, MemberServiceImpl.class)).isTrue();
    }


    // 파라미터 개수, 타입에 관계 없이 모두 허용한다.
    // (..)
    @Test
    void argsMatchAll(){
        pointcut.setExpression("execution(* *(..))");
        Assertions.assertThat(pointcut.matches(helloMethod, MemberServiceImpl.class)).isTrue();
    }


    // String 타입으로 시작, 그 이후의 파라미터는 개수, 타입이 상관 없이 허용한다.
    // (String, ..)
    @Test
    void argsMatchComplex(){
        pointcut.setExpression("execution(* *(String, ..))");
        Assertions.assertThat(pointcut.matches(helloMethod, MemberServiceImpl.class)).isTrue();
    }

}
