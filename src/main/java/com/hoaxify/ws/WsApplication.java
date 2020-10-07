package com.hoaxify.ws;

import com.hoaxify.ws.hoax.vm.HoaxSubmitVM;
import com.hoaxify.ws.user.User;
import com.hoaxify.ws.user.UserService;
import com.hoaxify.ws.hoax.Hoax;
import com.hoaxify.ws.hoax.HoaxService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication(/*exclude = SecurityAutoConfiguration.class*/)
public class WsApplication {

    public static void main(String[] args) {
        SpringApplication.run(WsApplication.class, args);
    }

    @Bean
//    @Profile("dev")
    CommandLineRunner createInitialUsers(UserService userService, HoaxService hoaxService) {
        return args -> {
            for (int i = 1; i <= 25; i++) {
                User user = new User();
                user.setUsername("emin" + i);
                user.setDisplayName("Emin" + i);
                user.setPassword("Emin@123");
                userService.save(user);
                for (int j = 1; j <= 20; j++) {
                    HoaxSubmitVM hoax = new HoaxSubmitVM();
                    hoax.setContent("Hoax " + j + " from user" + i);
                    hoaxService.save(hoax, user);

                }
            }


        };
    }

}
