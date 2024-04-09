package hello.aop.internalcall;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CallServiceV0 {
    public void external() {
        log.info("call external");
        internal(); // 내부 메서드 호출 this.internal(), 자바에서 메서드 호출시 대상을 지정하지 않으면 this가 붙게된다.
    }

    public void internal() {
        log.info("call internal");
    }


}
