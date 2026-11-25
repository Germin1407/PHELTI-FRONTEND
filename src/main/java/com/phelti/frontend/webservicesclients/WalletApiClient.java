package com.phelti.frontend.webservicesclients;

import com.phelti.frontend.Dtos.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import tools.jackson.databind.json.JsonMapper;
import java.util.List;

@Component
public class WalletApiClient extends BaseApiClient {
    public WalletApiClient(RestTemplate restTemplate, JsonMapper jsonMapper,
            @Value("${phelti.backend.base-url}") String baseUrl) {
        super(restTemplate, jsonMapper, baseUrl);
    }

    public WalletResponse crear(Integer userId) { return post("/wallets/users/{id}", null, WalletResponse.class, userId); }
    public WalletResponse obtener(Integer userId) { return get("/wallets/users/{id}", WalletResponse.class, userId); }
    public WalletResponse depositar(Integer userId, DepositRequest datos) {
        return post("/wallets/users/{id}/deposits", datos, WalletResponse.class, userId);
    }
    public List<TransactionResponse> movimientos(Integer userId) {
        return getList("/wallets/users/{id}/transactions", TransactionResponse[].class, userId);
    }
}
