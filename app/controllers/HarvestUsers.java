package controllers;


import java.util.List;

import models.HarvestUser;

import play.mvc.With;
 
@Check("admin")
@With(Secure.class)
public class HarvestUsers extends CRUD {

}

