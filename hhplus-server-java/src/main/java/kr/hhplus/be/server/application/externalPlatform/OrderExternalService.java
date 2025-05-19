package kr.hhplus.be.server.application.externalPlatform;

import kr.hhplus.be.server.infra.external.ExternalClient;
import kr.hhplus.be.server.infra.external.ExternalRequest;
import kr.hhplus.be.server.infra.external.ExternalResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderExternalService {

    private final ExternalClient externalClient;

    public void sendOrder(ExternalRequest externalRequest) {
        ExternalResponse response = externalClient.sendOrder(externalRequest);
        if (response.isSuccess()) {log.info("주문정보 외부 전송 : {}", true);}
    }
}
