package models.harvest.ipt.eml;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

import models.DataPublisher;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.jdom.Element;

import play.db.jpa.Model;

@Entity
public class Party extends Model{
	private boolean individual;
	private boolean address;

	public String profil;
	public String givenName;
	public String surName;
	public String organizationName;
	public String positionName;
	public String phone;
	public String electronicMailAddress;
	public String onlineUrl;
	public String role;

	public String addressDeliveryPoint;
	public String addressCity;
	public String addressPostalCode;
	public String addressCountry;

	@ManyToOne
	public EmlData emlData;
	
	@ManyToOne
	public Project project;


	public Party parse(Element child, String prof, EmlData data) {
		if (child != null){
			this.setProfil(prof);
			if (child.getChild("individualName")!=null){
				this.setIndName(true);
				this.setGivenName(child.getChild("individualName").getChildText("givenName"));
				this.setSurName(child.getChild("individualName").getChildText("surName"));
			}
			this.setOrganizationName(child.getChildText("organizationName"));
			this.setPositionName(child.getChildText("positionName"));
			this.setPhone(child.getChildText("phone"));
			this.setElectronicMailAddress(child.getChildText("electronicMailAddress"));
			this.setOnlineUrl(child.getChildText("onlineUrl"));
			this.setRole(child.getChildText("role"));
			if (child.getChild("address")!=null){
				this.setAddressDeliveryPoint(child.getChild("address").getChildText("givenName"));
				this.setAddressCity(child.getChild("address").getChildText("givenName"));
				this.setAddressPostalCode(child.getChild("address").getChildText("givenName"));
				this.setAddressCountry(child.getChild("address").getChildText("givenName"));
			}
			this.setEmlData(data);
			return this;
		}
		return null;
	}

	public Party parse(Element child, String prof, Project proj) {
		if (child != null){

			this.setProfil(prof);
			if (child.getChild("individualName")!=null){
				this.setIndName(true);
				this.setGivenName(child.getChild("individualName").getChildText("givenName"));
				this.setSurName(child.getChild("individualName").getChildText("surName"));
			}
			this.setOrganizationName(child.getChildText("organizationName"));
			this.setPositionName(child.getChildText("positionName"));
			this.setPhone(child.getChildText("phone"));
			this.setElectronicMailAddress(child.getChildText("electronicMailAddress"));
			this.setOnlineUrl(child.getChildText("onlineUrl"));
			this.setRole(child.getChildText("role"));
			if (child.getChild("address")!=null){
				this.setAddressDeliveryPoint(child.getChild("address").getChildText("givenName"));
				this.setAddressCity(child.getChild("address").getChildText("givenName"));
				this.setAddressPostalCode(child.getChild("address").getChildText("givenName"));
				this.setAddressCountry(child.getChild("address").getChildText("givenName"));
			}
			this.setProject(proj);
			return this;
		}
		return null;
	}

	public String toString(){
		String str = "";

		if (this.isIndName()){
			str = str+ "\n\t givenName : " + this.getGivenName();
			str = str+ "\n\t surName : " + this.getSurName();
		}
		str = str+ "\n\t organizationName : " + this.getOrganizationName();
		str = str+ "\n\t positionName : " + this.getPositionName();
		str = str+ "\n\t phone : " + this.getPhone();
		str = str+ "\n\t electronicMailAddress : " + this.getElectronicMailAddress();
		str = str+ "\n\t onlineUrl : " + this.getOnlineUrl();
		str = str+ "\n\t role : " + this.getRole();

		if (this.isAddress()){
			str = str+ "\n\t addressDeliveryPoint : " + this.getAddressDeliveryPoint();
			str = str+ "\n\t addressCity : " + this.getAddressCity();
			str = str+ "\n\t addressPostalCode : " + this.getAddressPostalCode();
			str = str+ "\n\t addressCountry : " + this.getAddressCountry();
		}
		return str;
	}


	public String getProfil() {
		return profil;
	}
	public void setProfil(String profil) {
		this.profil = profil;
	}
	public boolean isIndName() {
		return individual;
	}
	public void setIndName(boolean indName) {
		this.individual = indName;
	}
	public boolean isAddress() {
		return address;
	}
	public void setAddress(boolean address) {
		this.address = address;
	}
	public String getGivenName() {
		return givenName;
	}
	public void setGivenName(String givenName) {
		this.givenName = givenName;
	}
	public String getSurName() {
		return surName;
	}
	public void setSurName(String surName) {
		this.surName = surName;
	}
	public String getOrganizationName() {
		return organizationName;
	}
	public void setOrganizationName(String organizationName) {
		this.organizationName = organizationName;
	}
	public String getPositionName() {
		return positionName;
	}
	public void setPositionName(String positionName) {
		this.positionName = positionName;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getElectronicMailAddress() {
		return electronicMailAddress;
	}
	public void setElectronicMailAddress(String electronicMailAddress) {
		this.electronicMailAddress = electronicMailAddress;
	}
	public String getOnlineUrl() {
		return onlineUrl;
	}
	public void setOnlineUrl(String onlineUrl) {
		this.onlineUrl = onlineUrl;
	}
	public String getRole() {
		return role;
	}
	public void setRole(String role) {
		this.role = role;
	}
	public String getAddressDeliveryPoint() {
		return addressDeliveryPoint;
	}
	public void setAddressDeliveryPoint(String addressDeliveryPoint) {
		this.addressDeliveryPoint = addressDeliveryPoint;
	}
	public String getAddressCity() {
		return addressCity;
	}
	public void setAddressCity(String addressCity) {
		this.addressCity = addressCity;
	}
	public String getAddressPostalCode() {
		return addressPostalCode;
	}
	public void setAddressPostalCode(String addressPostalCode) {
		this.addressPostalCode = addressPostalCode;
	}
	public String getAddressCountry() {
		return addressCountry;
	}
	public void setAddressCountry(String addressCountry) {
		this.addressCountry = addressCountry;
	}
	public EmlData getEmlData() {
		return emlData;
	}
	public void setEmlData(EmlData emlData) {
		this.emlData = emlData;
	}
	public Project getProject() {
		return project;
	}
	public void setProject(Project project) {
		this.project = project;
	}
}
