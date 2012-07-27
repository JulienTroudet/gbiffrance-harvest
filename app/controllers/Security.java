package controllers;

import models.User;
 
public class Security extends Secure.Security {
    
    static boolean authenticate(String username, String password) {
        User user = User.find("byName", username).first();
        return user != null && user.password.equals(password);
    }
    
    static boolean check(String profile) {
        if("admin".equals(profile)) {
            return User.find("byName", connected()).<User>first().isAdmin;
        }
        if("publisher".equals(profile)) {
            return User.find("byName", connected()).<User>first().isPublisher;
        }
        return false;
    }
    static void onDisconnected() {
        Application.index();
    }
    static void onAuthenticated() {
    	Application.index();
    }
}
