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
 * Correspond au processus lancé en asynchrone, de contrôle de valeur sur un
 * dataset
 * 
 * @author Rémy PLAISANCE
 * 
 */
public class ValueJob extends Job {

	private long mIdDataset;

	private Controls mControls;

	/**
	 * Constructeur
	 * 
	 * @param pControls
	 * @param pIdDataset
	 */
	public ValueJob(Controls pControls, Long pIdDataset) {
		this.mIdDataset = pIdDataset;
		this.mControls = pControls;
	}

	/*
	 * Permet de lancer le traitement dans un thread séparé
	 * 
	 * @see play.jobs.Job#doJob()
	 */
	public void doJob() {
		try {
			OccurrenceMG lOccurrenceMG = new OccurrenceMG();
			List<Occurrence> lListOccurence = lOccurrenceMG
					.listOccurencesByDataset(mIdDataset);
			// On recharge l'objet car dans un thread séparé il perd la session
			// (JPA)
			Controls lControls = Controls.findById(mControls.id);
			int result = 0;
			if (lControls.fields != null) {
				for (Occurrence lOccurrence : lListOccurence) {
					for (Field lField : lControls.fields) {
						// On récupère la valeur du champ par introspection
						java.lang.reflect.Field val;
						val = lOccurrence.getClass().getField(lField.camelCase);
						Object object = val.get(lOccurrence);

						// On contrôle que le champ ne contient pas la valeur en
						// paramètre
						if (object.toString().equals(lControls.value)) {
							result++;
						}
					}

					Result lResult = new Result(lOccurrence, lControls,
							String.valueOf(result));
					lResult.save();
				}
			}
		} catch (NoSuchFieldException e) {
			Logger.error(e.toString(), "Value");
		} catch (SecurityException e) {
			Logger.error(e.toString(), "Value");
		} catch (IllegalArgumentException e) {
			Logger.error(e.toString(), "Value");
		} catch (IllegalAccessException e) {
			Logger.error(e.toString(), "Value");
		}
	}
}
