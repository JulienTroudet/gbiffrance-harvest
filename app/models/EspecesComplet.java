package models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import play.db.jpa.GenericModel;
@Entity
@Table(name = "ESPECES_COMPLET", schema = "ISB")
public class EspecesComplet extends GenericModel{
	
	@Column(name = "NOM_VERN")
	public Long nomVern;
	
	@Column(name = "CD_NOM")
	public String cdNom;
	
	@Column(name = "NOM_COMPLET")
	public String nomComplet;
	
	@Column(name = "LB_NOM")
	public String lbNom;
	
	@Column(name = "LB_AUTEUR")
	public String lbAuteur;
	
	@Id
	@Column(name = "CD_REF")
	public Long cdRef;
	
	@Column(name = "FR")
	public String fr;
	
	@Column(name = "FRFRA")
	public String frfra;
	
	@Column(name = "FRCOR")
	public String frcor;
	
	@Column(name = "HABITAT")
	public String habitat;
	
	@Column(name = "CD_TAXSUP")
	public Long cdTaxsup;
	
	@Column(name = "GENRE_VERN")
	public String genreVern;
	
	@Column(name = "FAMILLE_VERN")
	public String familleVern;
	
	@Column(name = "CLASSE_VERN")
	public String ordreVern;
	
	@Column(name = "PHYLUM_VERN")
	public String phylumVern;
	
	@Column(name = "REGNE_VERN")
	public String regneVern;
	
	@Column(name = "GENRE")
	public String genre;
	
	@Column(name = "FAMILLE")
	public String famille;
	
	@Column(name = "ORDRE")
	public String ordre;
	
	@Column(name = "CLASSE")
	public String classe;
	
	@Column(name = "PHYLUM")
	public String phylum;
	
	@Column(name = "REGNE")
	public String regne;
	
	@Column(name = "GROUP1_INPN")
	public String group1Inpn;
	
	@Column(name = "GROUP2_INPN")
	public String group2Inpn;
	
	@Column(name = "LB_NOM_VALIDE")
	public String lbNomValide;
	
	@Column(name = "MAR")
	public String mar;
	
	@Column(name = "GUA")
	public String gua;
	
	@Column(name = "SM")
	public String sm;
	
	@Column(name = "SB")
	public String sb;
	
	@Column(name = "GF")
	public String gf;
	
	@Column(name = "SPM")
	public String spm;
	
	@Column(name = "REU")
	public String reu;
	
	@Column(name = "MAY")
	public String may;
	
	@Column(name = "EPA")
	public String epa;
	
	@Column(name = "TAAF")
	public String taaf;
	
	@Column(name = "PF")
	public String pf;
	
	@Column(name = "NC")
	public String nc;
	
	@Column(name = "WF")
	public String wf;
	
	@Column(name = "CLI")
	public String cli;
	
	@Column(name = "SMSB")
	public String smsb;
	
	@Column(name = "RANG")
	public String rang;
	
	@Column(name = "ANNEE")
	public Long annee;
	
	@Column(name = "LB_NOM_AUTEUR")
	public String lbNomAuteur;
	
	@Column(name = "TAXON_TERM")
	public Long taxonTerm;
	
	@Column(name = "ISMARINE")
	public String isbmarine;
	
	@Column(name = "ISCONTINENTAL")
	public String iscontinental;
	
	@Column(name = "FAEU_ID")
	public Long faeuId;
	
	@Column(name = "ERMS_ID")
	public Long ermsId;
	
	@Column(name = "NOM_COMPLET_NON_HTML")
	public String nomCompletNonHtml;
	
	@Column(name = "CD_NOM_KD")
	public Long cdNomKd;
	
	@Column(name = "CD_NOM_PH")
	public Long cdNomPh;
	
	@Column(name = "CD_NOM_CL")
	public Long cdNomCl;
	
	@Column(name = "CD_NOM_OR")
	public Long cdNomOr;
	
	@Column(name = "CD_NOM_FM")
	public Long cdNomFm;
	
	@Column(name = "CD_NOM_GN")
	public String cdNomGn;
	
	@Column(name = "PATH_CDNOM")
	public String pathCdnom;
	
	@Column(name = "APHIA_ID")
	public Long aphiaId;
	
	@Column(name = "ROWNB")
	public Long rownb;
}
