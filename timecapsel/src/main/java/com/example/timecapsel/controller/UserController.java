package com.example.timecapsel.controller;
import com.example.timecapsel.dto.LoginDto;
import com.example.timecapsel.entity.UserEntity;
import com.example.timecapsel.service.JWTUtil;
import com.example.timecapsel.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private JWTUtil jwtUtil;

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody UserEntity user) {
        userService.saveUser(user);
        return ResponseEntity.ok("registered");
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginDto loginDto) {

        UserEntity user = userService.login(loginDto);
            String token = jwtUtil.generateToken(user.getEmail());
            return ResponseEntity.ok(token);

    }


    //ändrade så det ska fungera bättre med clientsidan - kan endast se och skicka meddelande med token vilket kräver login
    //token i header och message i body och mot entity istället för dto
    //använder request header istället för session
    @PostMapping("/message/post")
    public ResponseEntity<String> saveMessage(@RequestHeader ("Authorization") String token, @RequestBody String message){

        String email = jwtUtil.extractUsername(token.replace("Bearer ", ""));


        userService.saveMessage(email, message);
        return ResponseEntity.ok("message sent");
    }

    @GetMapping("/message/see")
    public ResponseEntity<String> seeMessage(@RequestHeader ("Authorization") String token ) throws Exception {
        String email = jwtUtil.extractUsername(token.replace("Bearer " , ""));
        String messages = userService.seeMessage(email);

        return ResponseEntity.ok(messages);
    }




}
