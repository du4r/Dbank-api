package Dbank.Dbank.services;

import Dbank.Dbank.domain.user.User;
import Dbank.Dbank.domain.user.UserType;
import Dbank.Dbank.dtos.UserDTO;
import Dbank.Dbank.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class UserService {

    @Autowired
    public UserRepository repository;

    public void validateTransaction(User sender, BigDecimal amount) throws Exception {
        if (sender.getUserType() == UserType.MERCHANT){
            throw new Exception("Merchant is not authorized to do transactions");
        }

        if (sender.getBalance().compareTo(amount) < 0){
            throw new Exception("insufficient balance");
        }
    }

    public User findUserById(Long id) throws Exception {
        return repository.findUserById(id).orElseThrow(() -> new Exception("user not found!"));
    }

    public User createUser(UserDTO userDTO){
        User newUser = new User(userDTO);
        this.saveUser(newUser);
        return newUser;
    }
    public void saveUser(User user){
        repository.save(user);
    }

    public List<User> getAllUsers(){
        return this.repository.findAll();
    }
}
