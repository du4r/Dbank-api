package Dbank.Dbank.services;

import Dbank.Dbank.domain.transaction.Transaction;
import Dbank.Dbank.domain.user.User;
import Dbank.Dbank.dtos.TransactionDTO;
import Dbank.Dbank.repositories.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

@Service
public class TransactionService {

    @Autowired
    private UserService userService;

    @Autowired
    private TransactionRepository repository;

    @Autowired
    private RestTemplate restTemplate;


    @Autowired
    private NotificationService notificationService;

    public Transaction createTransaction(TransactionDTO transaction) throws Exception {
        User sender = this.userService.findUserById(transaction.senderId());
        User receiver = this.userService.findUserById(transaction.receiverId());

        userService.validateTransaction(sender,transaction.value());

        Boolean isAuthorized = this.authorizeTransaction(sender,transaction.value());
        if (!isAuthorized){
            throw new Exception("transaction unauthorized");
        }

        Transaction transaction1 = new Transaction();
        transaction1.setAmount(transaction.value());
        transaction1.setSender(sender);
        transaction1.setReceiver(receiver);
        transaction1.setTimestamp(LocalDateTime.now());

        sender.setBalance(sender.getBalance().subtract(transaction.value()));
        receiver.setBalance(receiver.getBalance().add(transaction.value()));

        this.repository.save(transaction1);
        this.userService.saveUser(sender);
        this.userService.saveUser(receiver);

        this.notificationService.sendNotification(sender, "transaction sucessfully");
        this.notificationService.sendNotification(receiver, "transaction sucessfully");


        return transaction1;
    }

    private boolean authorizeTransaction(User sender, BigDecimal value){
       ResponseEntity<Map> authorizationResponse = restTemplate.getForEntity("https://run.mocky.io/v3/5794d450-d2e2-4412-8131-73d0293ac1cc", Map.class);

       if (authorizationResponse.getStatusCode() == HttpStatus.OK
               && authorizationResponse.getBody().get("message") == "Autorizado"){
           return true;
       }else return false;
    }
}
