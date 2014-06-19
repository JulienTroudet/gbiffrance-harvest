package manager.job;

import java.util.HashMap;
import java.util.List;

import manager.MondeCommunesMG;
import manager.OccurrenceMG;
import models.Controls;
import models.Field;
import models.Occurrence;
import models.Result;
import play.Logger;
import play.jobs.Job;

/**
 * Correspond au processus lancé en asynchrone, de contrôle géographique sur un
 * un dataset pour chaque occurence.
 * 
 * @author Rémy PLAISANCE
 * 
 */
public class GeographicalJob extends Job {

	private Controls mControls;

	private long mIdDataset;

	/**
	 * Constructeur.
	 * 
	 * @param pControls
	 * @param pIdDataset
	 */
	public GeographicalJob(Controls pControls, long pIdDataset) {
		this.mControls = pControls;
		this.mIdDataset = pIdDataset;
	}

	/*
	 * Permet de lancer le traitement dans un thread séparé
	 * 
	 * @see play.jobs.Job#doJob()
	 */
	public void doJob() {
		Logger.info("Debut du controle geographique");
		OccurrenceMG lOccurrenceMG = new OccurrenceMG();
		List<Occurrence> lListOccurence = lOccurrenceMG
				.listOccurencesByDataset(mIdDataset);
		MondeCommunesMG lMondeCommunesMG = new MondeCommunesMG();
		// On recharge l'objet car dans un thread séparé il perd la session
		// (JPA)
		Controls lControls = Controls.findById(mControls.id);

		if (lControls.fields != null) {
			for (Occurrence lOccurrence : lListOccurence) {
				int nb = 0;
				for (Field lField : lControls.fields) {
					HashMap<Long, String> results = lMondeCommunesMG
							.verifGeographique(lOccurrence.getId(), lField.name);
					if (results != null) {
						String res = results.get(lOccurrence.getId());
						if (res != null) {
							if (res.equals("1")) {
								nb++;
							}
						}
					}
				}
				if (nb > 0) {
					Result lResult = new Result(lOccurrence, lControls, "1");
					lResult.save();
				} else {
					Result lResult = new Result(lOccurrence, lControls, "0");
					lResult.save();
				}

			}
		}
		Logger.info("Fin du controle geographique");
	}

}
