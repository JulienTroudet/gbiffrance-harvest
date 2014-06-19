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
 * dataset grâce à une regex
 * 
 * @author Rémy PLAISANCE
 * 
 */
public class RegexJob extends Job {

	private Controls mControls;

	private long mIdDataset;

	/**
	 * Constructeur
	 * 
	 * @param pControls
	 * @param pIdDataset
	 */
	public RegexJob(Controls pControls, long pIdDataset) {
		this.mControls = pControls;
		this.mIdDataset = pIdDataset;
	}

	/*
	 * Permet de lancer le traitement dans un thread séparé
	 * 
	 * @see play.jobs.Job#doJob()
	 */
	public void doJob() {
		Logger.info("Debut du controle sur une regex");
		try {
			OccurrenceMG lOccurrenceMG = new OccurrenceMG();
			List<Occurrence> lListOccurence = lOccurrenceMG
					.listOccurencesByDataset(mIdDataset);
			// On recharge l'objet car dans un thread séparé il perd la session
			// (JPA)
			Controls lControls = Controls.findById(mControls.id);
			boolean result = false;
			if (lControls.fields != null) {
				for (Occurrence lOccurrence : lListOccurence) {
					int res = 0;
					int nb = 0;
					for (Field lField : lControls.fields) {
						// On récupère la valeur du champ par introspection
						java.lang.reflect.Field val;
						val = lOccurrence.getClass().getField(lField.camelCase);
						Object object = val.get(lOccurrence);
						String value = (String) object;
						
						// On contrôle que le champ correspond à la regex
						if (value != null) {
							result = value.matches(lControls.regex);
							if (result) {
								nb++;
							}
						}
					}
					if(nb > 0){
						res = 1;
					}
					Result lResult = new Result(lOccurrence, lControls,
							String.valueOf(res));
					lResult.save();
				}
			}
			Logger.info("Fin du controle sur une regex");
		} catch (NoSuchFieldException e) {
			Logger.error(e.toString(), "Regex");
		} catch (IllegalArgumentException e) {
			Logger.error(e.toString(), "Regex");
		} catch (IllegalAccessException e) {
			Logger.error(e.toString(), "Regex");
		}
	}
}
