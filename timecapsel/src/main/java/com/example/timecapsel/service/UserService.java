package com.example.timecapsel.service;
import com.example.timecapsel.dto.LoginDto;
import com.example.timecapsel.entity.UserEntity;
import com.example.timecapsel.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService  {

    @Autowired
    private UserRepository userRepository;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public UserEntity saveUser(UserEntity user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    public UserEntity findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    //enkel kontroll för inlogg
    public UserEntity login(LoginDto loginDto){
        UserEntity user = userRepository.findByEmail(loginDto.getEmail());

        if (!passwordEncoder.matches(loginDto.getPassword(), user.getPassword())){
            throw new RuntimeException("Wrong password");
        }

            return user;

    }

    public UserEntity saveMessage(String email, String message){
        UserEntity user = userRepository.findByEmail(email);

        try {
            String encryptedMessage = AesUtil.AESKryptering(message);
            user.setMessage(encryptedMessage);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return userRepository.save(user);
    }


    public String seeMessage(String email) throws Exception {
        UserEntity user = userRepository.findByEmail(email);

            String decryptedMessage = AesUtil.AESDekryptering(user.getMessage());
            user.setMessage(decryptedMessage);


        return decryptedMessage ;

    }



}
