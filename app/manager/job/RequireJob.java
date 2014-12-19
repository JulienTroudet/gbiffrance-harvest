package manager.job;

import java.util.List;

import manager.OccurrenceMG;
import models.Controls;
import models.Field;
import models.Occurrence;
import models.Result;
import play.Logger;
import play.jobs.Job;

/**
 * Correspond au processus lancé en asynchrone, de contrôle de champs requis sur
 * un dataset.
 * 
 * @author Rémy PLAISANCE
 * 
 */
public class RequireJob extends Job {

	private Controls mControls;

	private long mIdDataset;

	/**
	 * Constructeur
	 * 
	 * @param pControls
	 * @param pIdDataset
	 */
	public RequireJob(Controls pControls, long pIdDataset) {

		this.mControls = pControls;
		this.mIdDataset = pIdDataset;
	}

	/*
	 * Permet de lancer le traitement dans un thread séparé
	 * 
	 * @see play.jobs.Job#doJob()
	 */
	public void doJob() {
		Logger.info("Debut du controle sur un champ requis");
		OccurrenceMG lOccurrenceMG = new OccurrenceMG();
		List<Occurrence> lListOccurence = lOccurrenceMG
				.listOccurencesByDataset(mIdDataset);
		// On recharge l'objet car dans un thread séparé il perd la session
		// (JPA)
		Controls lControls = Controls.findById(mControls.id);
		for (Occurrence lOccurrence : lListOccurence) {
			int res = 0;
			for (Field lField : lControls.fields) {
				java.lang.reflect.Field val;
				try {
					val = lOccurrence.getClass().getField(lField.camelCase);
					Object object = val.get(lOccurrence);
					String value = (String) object;
				
					if (value == null) {
						res++;
					} else if (value.isEmpty()) {
						res++;
					}
				} catch (NoSuchFieldException e) {
					wasError = true;
					Logger.error(e.toString(), "Require");
				} catch (IllegalArgumentException e) {
					wasError = true;
					Logger.error(e.toString(), "Require");
				} catch (IllegalAccessException e) {
					wasError = true;
					Logger.error(e.toString(), "Require");
				}
			}
			if(res < 1){
				res = 0;
			}else{
				res = 1;
			}
			
			Result lResult = new Result(lOccurrence, lControls,
					String.valueOf(res));
			lResult.save();
		}
		Logger.info("Fin du controle sur un champ requis");
	}
}
