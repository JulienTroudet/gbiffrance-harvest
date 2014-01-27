package manager.job;

import java.util.List;

import manager.OccurrenceMG;
import models.Controls;
import models.Occurrence;
import models.Result;
import play.Logger;
import play.jobs.Job;

/**
 * Correspond au processus lancé en asynchrone, de contrôle de valeur en double
 * sur un un dataset pour chaque occurence.
 * 
 * @author Rémy PLAISANCE
 * 
 */
public class DoublonJob extends Job {

	private Controls mControls;

	private long mIdDataset;

	/**
	 * Constructeur
	 * 
	 * @param pControls
	 * @param pIdDataset
	 */
	public DoublonJob(Controls pControls, long pIdDataset) {
		this.mControls = pControls;
		this.mIdDataset = pIdDataset;
	}

	/*
	 * Permet de lancer le traitement dans un thread séparé
	 * 
	 * @see play.jobs.Job#doJob()
	 */
	public void doJob() {
		OccurrenceMG lOccurrenceMG = new OccurrenceMG();
		List<Occurrence> lListOccurence = lOccurrenceMG
				.listOccurencesByDataset(mIdDataset);
		// On recharge l'objet car dans un thread séparé il perd la session
		// (JPA)
		Controls lControls = Controls.findById(mControls.id);
		Long result = null;
		if (lControls.fields != null) {
			for (Occurrence lOccurrence : lListOccurence) {
				try {
					result = lOccurrenceMG.verifDuplicateField(
							lControls.fields, lOccurrence);
				} catch (NoSuchFieldException e) {
					Logger.error(e.toString(), "Doublon");
				} catch (SecurityException e) {
					Logger.error(e.toString(), "Doublon");
				} catch (IllegalArgumentException e) {
					Logger.error(e.toString(), "Doublon");
				} catch (IllegalAccessException e) {
					Logger.error(e.toString(), "Doublon");
				}
				Result lResult = new Result(lOccurrence, lControls,
						result.toString());
				lResult.save();
			}
		}

	}
}
