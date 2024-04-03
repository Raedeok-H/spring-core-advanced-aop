package hello.aop.order.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;

@Slf4j
@Aspect
public class AspectV6Advice {

    /*
    - 메서드의 실행의 주변에서 실행된다. 메서드 실행 전후에 작업을 수행한다.
    - 가장 강력한 어드바이스
        -조인 포인트 실행 여부 선택 joinPoint.proceed() 호출 여부 선택
        -전달 값 변환: joinPoint.proceed(args[])
        -반환 값 변환
        -예외 변환
        -트랜잭션 처럼 try ~ catch~ finally 모두 들어가는 구문 처리 가능
    - 어드바이스의 첫 번째 파라미터는 ProceedingJoinPoint 를 사용해야 한다.
    - proceed() 를 통해 대상을 실행한다.
    - proceed() 를 여러번 실행할 수도 있음(재시도)
*/
    @Around("hello.aop.order.aop.Pointcuts.orderAndService()") // allOrder()이면서 allService 인 조인포인트
    public Object doTransaction(ProceedingJoinPoint joinPoint) throws Throwable {
        try {
            //@Before 를 사용한다면 실행되는 위치의 예시
            log.info("[트랜잭션 시작] {}", joinPoint.getSignature());
            Object result = joinPoint.proceed();
            //@AfterReturning 를 사용한다면 실행되는 위치의 예시
            log.info("[트랜잭션 커밋] {}", joinPoint.getSignature());
            return result;
        } catch (Exception e) {
            //@AfterThrowing 를 사용한다면 실행되는 위치의 예시
            log.info("[트랜잭션 롤백] {}", joinPoint.getSignature());
            throw e;
        } finally {
            //@After 를 사용한다면 실행되는 위치의 예시
            log.info("[리소스 릴리즈] {}", joinPoint.getSignature());
        }
    }

    @Before("hello.aop.order.aop.Pointcuts.orderAndService()")
    public void doBefore(JoinPoint joinPoint) {
        log.info("[before] {}", joinPoint.getSignature());
    } // @Before 는 ProceedingJoinPoint.proceed() 자체를 사용하지 않는다.
    // 메서드 종료시 자동으로 다음 타켓이 호출된다.
    // 물론 예외가 발생하면 다음 코드가 호출되지는 않는다.

    @AfterReturning(value = "hello.aop.order.aop.Pointcuts.orderAndService()", returning = "result")
    public void doReturning(JoinPoint joinPoint, Object result) {
        log.info("[return] {} return={}", joinPoint.getSignature(), result);
    } //returning 속성에 사용된 이름은 어드바이스 메서드의 매개변수 이름과 일치해야 한다.
    // returning 절에 지정된 이름(여기서 "result")의 타입(여기서 "Object")의 값을 반환하는 메서드만 대상으로 실행한다.(부모 타입을 지정하면 모든 자식타입은 인정된다.)
    // @Around 와 다르게 반환되는 객체를 변경할 수는 없다.

    @AfterThrowing(value = "hello.aop.order.aop.Pointcuts.orderAndService()", throwing = "ex")
    public void doThrowing(JoinPoint joinPoint, Exception ex) {
        log.info("[ex] {} message={}",joinPoint.getSignature(), ex.getMessage());
    } // throwing 속성에 사용된 이름은 어드바이스 메서드의 매개변수 이름과 일치해야 한다.
    //throwing 절에 지정된 타입과 맞는 예외를 대상으로 실행한다. (부모 타입을 지정하면 모든 자식 타입은 인정된다.)

    @After(value = "hello.aop.order.aop.Pointcuts.orderAndService()")
    public void doAfter(JoinPoint joinPoint) {
        log.info("[after] {}", joinPoint.getSignature());
    } // 메서드 실행이 종료되면 실행
    // 정상 및 예외 반환 조건을 모두 처리한다. => finally 를 생각하면 된다.
}
