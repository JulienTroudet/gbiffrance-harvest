package models;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import play.db.jpa.GenericModel;
@Entity
@Table(name = "TAXREF", schema = "TAXREF")
public class Taxref extends GenericModel{
	
	@Id
	@Column(name = "CD_TAXSUP")
	public Long cdTaxsup;
	
	@Column(name = "GROUPE")
	public String groupe;
	
	@Column(name = "CD_NOM")
	public Long cdNom;
	
	@Column(name = "CD_SUP")
	public Long cdSup;
	
	@Column(name = "CD_REF")
	public Long cdRef;
	
	@Column(name = "RANG")
	public String rang;
	
	@Column(name = "HASDOUBLE")
	public String hasdouble;
	
	@Column(name = "HASSYN")
	public String hasssyn;
	
	@Column(name = "HASCHILD")
	public String haschild;
	
	@Column(name = "LB_NOM")
	public String lbNom;
	
	@Column(name = "LB_AUTEUR")
	public String lbAuteur;
	
	@Column(name = "ANNEE")
	public Long annee;
	
	@Column(name = "FG_VALIDITE")
	public String fgValidite;
	
	@Column(name = "HABITAT")
	public Long habitat;
	
	@Column(name = "FR")
	public String fr;
	
	@Column(name = "FRFRA")
	public String frfra;
	
	@Column(name = "FRCOR")
	public String frcor;
	
	@Column(name = "GF")
	public String gf;
	
	@Column(name = "MAR")
	public String mar;
	
	@Column(name = "GUA")
	public String gua;
	
	@Column(name = "SM")
	public String sm;
	
	@Column(name = "SB")
	public String sb;
	
	@Column(name = "SPM")
	public String spm;
	
	@Column(name = "MAY")
	public String may;
	
	@Column(name = "REU")
	public String reu;
	
	@Column(name = "EPA")
	public String epa;
	
	@Column(name = "TAAF")
	public String taaf;
	
	@Column(name = "NC")
	public String nc;
	
	@Column(name = "WF")
	public String wf;
	
	@Column(name = "PF")
	public String pf;
	
	@Column(name = "CLI")
	public String cli;
	
	@Column(name = "NOM_COMPLET")
	public String nomComplet;
	
	@Column(name = "NOM_COMPLET_HTML")
	public String nomCompletHtml;
	
	@Column(name = "INPN")
	public String inpn;
	
	@Column(name = "INPN_EXTENDED")
	public String inpnExtended;
	
	@Column(name = "DATE_CREA")
	public Date dateCrea;
	
	@Column(name = "ORIGINE_CREA")
	public String origineCrea;
	
	@Column(name = "GENRE")
	public String genre;
	
	@Column(name = "ESPECE")
	public String espece;
	
	@Column(name = "SOUS_ESPECE")
	public String sousEspece;
	
	@Column(name = "BIBLIO")
	public String biblio;
	
	@Column(name = "FAEU_ID")
	public Long faeuId;
	
	@Column(name = "ERMS_ID")
	public Long ermsId;
	
	@Column(name = "APHIA_ID")
	public Long aphiaId;
	
	@Column(name = "CD_RC")
	public Long cdRc;
	
	@Column(name = "BDNFFNN")
	public Long bdnffnn;
	
	@Column(name = "NADEAUD_ID")
	public Long nadeaudId;
	
	@Column(name = "FISHBASE_ID")
	public Long fishbaseId;
	
	@Column(name = "BDNGM")
	public Long bdngm;
	
	@Column(name = "DATE_MODIF")
	public Date dateModif;
	
	@Column(name = "COL_LSID")
	public String colLsid;
	
	@Column(name = "PROTOLOGUE_URL")
	public String protologueUrl;
	
	@Column(name = "ID_UTILISATEUR_CREA")
	public Long idUtilisateurCrea;
	
	@Column(name = "ID_UTILISATEUR_MODIF")
	public Long idUtilisateurModif;
	
	@Column(name = "TROPICOS_ID")
	public String tropicosId;
	
	@Column(name = "TYPE_URL")
	public Long typeUrl;
	
	@Column(name = "ALGAEBASE_ID")
	public Long algaebaseId;
	
	@Column(name = "ITR")
	public Long itr;
	
	@Column(name = "PREMIERE_DIFFUSION")
	public Long premiereDiffusion;
}
