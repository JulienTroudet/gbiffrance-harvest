package models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import play.db.jpa.GenericModel;

@Entity
@Table(name = "ADM", schema = "ISB")
public class Adm extends GenericModel {

	@Column(name = "CD_INSEE")
	public String cdInsee;

	public String valide;

	@Column(name = "CD_INSEE_REF")
	public String cdInseeRef;

	@Id
	@Column(name = "CD_SIG")
	public String cdSig;

	@Column(name = "CD_SIG_SUP")
	public String cdSigSup;

	@Column(name = "CD_SIG_REF")
	public String cdSigRef;

	@Column(name = "LB_ADM")
	public String lbAdm;

	@Column(name = "CHARNIERE_TR")
	public String charniereTr;

	@Column(name = "LB_ARTICLE_TR")
	public String lbArticleTr;

	@Column(name = "LB_ADM_TR")
	public String lbAdmTr;

	public String modif;

	@Column(name = "NIVEAU_ADMIN")
	public String niveauAdmin;

	public String cheflieu;

	@Column(name = "CD_ISO3166_1")
	public String cdISO31661;

	@Column(name = "CD_ISO3166_2")
	public String cdISO31662;

	public String nuts;

	public String sr;

	public String sd;

	public String sc;

	public String territoire;

	public String terrestre;

	@Column(name = "CD_REG")
	public String cdReg;

	@Column(name = "CD_DEPT")
	public String cdDept;
}
