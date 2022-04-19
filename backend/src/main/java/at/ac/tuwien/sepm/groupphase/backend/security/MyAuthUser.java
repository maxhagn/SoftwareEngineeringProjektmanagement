package at.ac.tuwien.sepm.groupphase.backend.security;


import at.ac.tuwien.sepm.groupphase.backend.entity.User;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotAuthorizedException;
import at.ac.tuwien.sepm.groupphase.backend.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import java.lang.invoke.MethodHandles;
import java.util.concurrent.atomic.AtomicBoolean;

public class MyAuthUser{
    private  String mail;
    private User u;

    private UserService userService;

    public MyAuthUser( UserService userService, @Value("") Object principal) {
        mail = (String) principal;
        this.userService = userService;
        u = this.userService.findUserByEmail(mail);
    }

    public User getCurrentUser() {
        return u;
    }

    public boolean isAllowedToAccessTicket(Long id) throws NotAuthorizedException {
        AtomicBoolean found = new AtomicBoolean(false);
        this.u.getTickets().forEach(ticket ->{
            if(ticket.getId().equals(id)){
                found.set(true);
            }
        });
        if(!found.get()){
            throw new NotAuthorizedException("Not authorized");
        }
        return found.get();
    }
}
