package models;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import play.db.jpa.Model;

@Entity
public class Result extends Model {

	@ManyToOne
	public Occurrence occurrence;

	@ManyToOne
	public Controls controls;

	public String result;

	public Result() {

	}

	public Result(Occurrence pOccurrence, Controls pControls, String pResult) {
		this.occurrence = pOccurrence;
		this.controls = pControls;
		this.result = pResult;
	}
}
