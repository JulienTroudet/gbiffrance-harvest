package controllers;


import java.util.List;

import models.User;

import play.mvc.With;
 
@Check("admin")
@With(Secure.class)
public class Users extends CRUD {

}
