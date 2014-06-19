package manager.job;

import java.util.List;

import manager.OccurrenceMG;
import models.Controls;
import models.EspecesComplet;
import models.Field;
import models.Occurrence;
import models.Result;
import models.Taxref;
import play.Logger;
import play.jobs.Job;

/**
 * Correspond au processus lancé en asynchrone, de contrôle de taxon
 * 
 * @author Rémy PLAISANCE
 * 
 */
public class TaxonJob extends Job {

	private Controls mControls;

	private long mIdDataset;

	/**
	 * Constructeur
	 * 
	 * @param pControls
	 * @param pIdDataset
	 */
	public TaxonJob(Controls pControls, long pIdDataset) {
		this.mControls = pControls;
		this.mIdDataset = pIdDataset;
	}

	/*
	 * Permet de lancer le traitement dans un thread séparé
	 * 
	 * @see play.jobs.Job#doJob()
	 */
	public void doJob() {
		Logger.info("Debut du controle sur un taxon");
		try {
			OccurrenceMG lOccurrenceMG = new OccurrenceMG();
			List<Occurrence> lListOccurence = lOccurrenceMG
					.listOccurencesByDataset(mIdDataset);
			// On recharge l'objet car dans un thread séparé il perd la session
			// (JPA)
			Controls lControls = Controls.findById(mControls.id);
			String result = null;
			Long nb = null;
			if (lControls.fields != null) {
				for (Occurrence lOccurrence : lListOccurence) {
					for (Field lField : lControls.fields) {
						// On récupère la valeur du champ par introspection
						java.lang.reflect.Field val;
						val = lOccurrence.getClass().getField(lField.camelCase);
						Object object = val.get(lOccurrence);
						if (object == null) {
							result = "NC";
						} else {
							if (mControls.taxoId == 1L) {
								nb = Taxref.count("cdNom", object.toString());
							} else if (mControls.taxoId == 2L) {
								nb = EspecesComplet.count("cdNom", object.toString());
							}
							if(nb > 0){
								result = "1";
							}else{
								result = "0";
							}
						}
					}
					Result lResult = new Result(lOccurrence, lControls, result);
					lResult.save();
				}
			}
			Logger.info("Fin du controle sur un taxon");
		} catch (IllegalArgumentException e) {
			Logger.error(e.toString(), "Taxon");
		} catch (NoSuchFieldException e) {
			Logger.error(e.toString(), "Taxon");
		} catch (SecurityException e) {
			Logger.error(e.toString(), "Taxon");
		} catch (IllegalAccessException e) {
			Logger.error(e.toString(), "Taxon");
		}
	}
}
