package kr.hhplus.be.server.infra.external;

import org.springframework.stereotype.Component;

@Component
public class ExternalClient {

    public ExternalResponse sendOrder(ExternalRequest externalRequest) {
        return new ExternalResponse(true);
    }
}
