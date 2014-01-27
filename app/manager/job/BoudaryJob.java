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
 * Correspond au processus lancé en asynchrone, de contrôle de valeur via des
 * bornes sur un dataset par occurence
 * 
 * @author Rémy PLAISANCE
 * 
 */
public class BoudaryJob extends Job {

	private Controls mControls;

	private long mIdDataset;

	/**
	 * Constructeur
	 * 
	 * @param pControls
	 * @param pIdDataset
	 */
	public BoudaryJob(Controls pControls, long pIdDataset) {
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
		if (lControls.fields != null && isNumeric(lControls.boudaryHigher)
				&& isNumeric(lControls.boudaryLower)) {
			for (Occurrence lOccurrence : lListOccurence) {
				int nb = 0;
				try {
					for (Field lField : lControls.fields) {
						// On récupère la valeur du champ par introspection
						java.lang.reflect.Field val = lOccurrence.getClass()
								.getField(lField.camelCase);
						Object object = val.get(lOccurrence);

						// On compare si le champs est inferieur à la borne
						// inférieur définit
						String value = object.toString();
						int result = Long.valueOf(value).compareTo(
								Long.parseLong(lControls.boudaryLower));
						if (result < 0) {
							nb++;
						}
						// On compare si le champ est supérieur à la borne
						// supérieur définit
						result = Long.valueOf(value).compareTo(
								Long.parseLong(lControls.boudaryHigher));
						if (result > 0) {
							nb++;
						}
					}
				} catch (SecurityException e) {
					Logger.error(e.toString(), "Boudary");
				} catch (IllegalArgumentException e) {
					Logger.error(e.toString(), "Boudary");
				} catch (NoSuchFieldException e) {
					Logger.error(e.toString(), "Boudary");
				} catch (IllegalAccessException e) {
					Logger.error(e.toString(), "Boudary");
				}
				Result lResult = new Result(lOccurrence, lControls,
						String.valueOf(nb));
				lResult.save();
			}
		} else {
			Logger.debug("Boudary lower or boudary higher is not a number");
		}
	}

	public static boolean isNumeric(String pStr) {
		if (pStr == null) {
			return false;
		} else {
			try {
				Long d = Long.parseLong(pStr);
			} catch (NumberFormatException nfe) {
				return false;
			}
			return true;
		}
	}
}
