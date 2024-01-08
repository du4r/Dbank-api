package Dbank.Dbank.services;

import Dbank.Dbank.domain.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.Map;

public class AuthorizationService {

    @Autowired
    private RestTemplate restTemplate;
    private boolean authorizeTransaction(User sender, BigDecimal value){
        ResponseEntity<Map> authorizationResponse = restTemplate.getForEntity("https://run.mocky.io/v3/5794d450-d2e2-4412-8131-73d0293ac1cc", Map.class);

        if (authorizationResponse.getStatusCode() == HttpStatus.OK
                && authorizationResponse.getBody().get("message") == "Autorizado"){
            return true;
        }else return false;
    }
}
