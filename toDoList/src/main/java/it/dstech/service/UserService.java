package it.dstech.service;

import it.dstech.models.Activity;
import it.dstech.models.User;

public interface UserService {

    User findByEmail(String email);

    User save(UserRegistrationDao registration);
    
//    Long count();
    
    void deleteById(Long userId);
    
    void addActivities(User user, Activity activities);
}
