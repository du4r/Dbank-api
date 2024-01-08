package Dbank.Dbank.services;

import Dbank.Dbank.domain.transaction.Transaction;
import Dbank.Dbank.domain.user.User;
import Dbank.Dbank.dtos.TransactionDTO;
import Dbank.Dbank.repositories.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class TransactionService {

    @Autowired
    private UserService userService;

    @Autowired
    private TransactionRepository repository;

    @Autowired
    private AuthorizationService authorizationService;

    @Autowired
    private NotificationService notificationService;

    public Transaction createTransaction(TransactionDTO transaction) throws Exception {
        User sender = this.userService.findUserById(transaction.senderId());
        User receiver = this.userService.findUserById(transaction.receiverId());

        userService.validateTransaction(sender, transaction.value());

        Boolean isAuthorized = this.authorizationService.authorizeTransaction(sender, transaction.value());
        if (!isAuthorized) {
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

}
