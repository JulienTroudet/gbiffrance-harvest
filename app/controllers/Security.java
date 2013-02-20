package controllers;

import models.HarvestUser;
 
public class Security extends Secure.Security {
    
    static boolean authenticate(String username, String password) {
    	HarvestUser user = HarvestUser.find("byName", username).first();
        return user != null && user.password.equals(password);
    }
    
    static boolean check(String profile) {
        if("admin".equals(profile)) {
            return HarvestUser.find("byName", connected()).<HarvestUser>first().isAdmin;
        }
        if("publisher".equals(profile)) {
            return HarvestUser.find("byName", connected()).<HarvestUser>first().isPublisher;
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
