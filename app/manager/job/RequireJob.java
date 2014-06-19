package manager.job;

import java.util.List;

import manager.OccurrenceMG;
import models.Controls;
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
		Long result = null;
		// On recharge l'objet car dans un thread séparé il perd la session
		// (JPA)
		Controls lControls = Controls.findById(mControls.id);
		for (Occurrence lOccurrence : lListOccurence) {
			int res = 0;
			result = lOccurrenceMG.verifRequireField(lControls.fields,
					lOccurrence);
			if (result > 0) {
				res = 1;
			}
			Result lResult = new Result(lOccurrence, lControls,
					String.valueOf(res));
			lResult.save();
		}
		Logger.info("Fin du controle sur un champ requis");
	}
}
