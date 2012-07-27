package models;
 
import play.*;
import play.data.validation.Email;
import play.data.validation.Required;
import play.db.jpa.*;
 
import javax.persistence.*;
import java.util.*;
 
@Entity
public class User extends Model {
 	
	@Required
	public String name;
	
	@Email
    @Required
	public String email;
	
	public String address;
	
	@Required
	public String password;
	
	public boolean isAdmin;
	public boolean isPublisher;
 	
}