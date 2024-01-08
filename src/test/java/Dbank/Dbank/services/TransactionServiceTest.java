package Dbank.Dbank.services;

import Dbank.Dbank.domain.user.User;
import Dbank.Dbank.domain.user.UserType;
import Dbank.Dbank.dtos.TransactionDTO;
import Dbank.Dbank.repositories.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class TransactionServiceTest {

    @Mock
    private UserService userService;

    @Mock
    private TransactionRepository repository;

    @Mock
    private AuthorizationService authorizationService;

    @Mock
    private NotificationService notificationService;

    @Autowired
    @InjectMocks
    private TransactionService transactionService;

    @BeforeEach
    void setup(){
        MockitoAnnotations.initMocks(this);
    }

    @Test
    @DisplayName("should create a transaction successfully when everything is OK")
    void createTransactionUC1() throws Exception {

        User sender = new User(1l,"joao","silva","99999999999",
                "joaozinho@gmail.com","123333",new BigDecimal("1213"), UserType.COMMON);


        User receiver = new User(2l,"jose","silva","99999999990",
                "josezinho@gmail.com","123333",new BigDecimal("120"), UserType.MERCHANT);

        when( userService.findUserById(1L) ).thenReturn(sender);
        when( userService.findUserById(2L) ).thenReturn(receiver);
        when(authorizationService.authorizeTransaction(any(),any())).thenReturn(true);

        TransactionDTO request = new TransactionDTO(new BigDecimal(10), 1L,2L);
        transactionService.createTransaction(request);

        verify(repository,times(1)).save(any());

        sender.setBalance(new BigDecimal(1203));
        verify(userService,times(1)).saveUser(sender);

        receiver.setBalance(new BigDecimal(130));
        verify(userService,times(1)).saveUser(receiver);

        verify(notificationService,times(1)).sendNotification(sender,"");
        verify(notificationService,times(1)).sendNotification(receiver,"");
    }

    @Test
    @DisplayName("should throw exception when transaction is not allowed")
    void createTransactionUC2() {

    }
}