package kr.hhplus.be.server.application.order;

import kr.hhplus.be.server.application.externalPlatform.OrderExternalCommand;
import kr.hhplus.be.server.infra.external.ExternalClient;
import kr.hhplus.be.server.infra.external.ExternalRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderExternalEventPublisher {

    private final ExternalClient externalClient;

    public void publish(OrderExternalCommand command) {
        ExternalRequest req = ExternalRequest.builder()
                        .orderId(command.orderId())
                        .status(command.status())
                        .build();

        log.info("external isSuccess : {}", externalClient.sendOrder(req).isSuccess());
    }
}
