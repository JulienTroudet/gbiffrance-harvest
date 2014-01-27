package manager.job;

import java.util.List;

import manager.OccurrenceMG;
import models.Controls;
import models.Espece;
import models.Field;
import models.Occurrence;
import models.Result;
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
		try {
			OccurrenceMG lOccurrenceMG = new OccurrenceMG();
			List<Occurrence> lListOccurence = lOccurrenceMG
					.listOccurencesByDataset(mIdDataset);
			// On recharge l'objet car dans un thread séparé il perd la session
			// (JPA)
			Controls lControls = Controls.findById(mControls.id);
			Long nb = null;
			if (lControls.fields != null) {
				for (Occurrence lOccurrence : lListOccurence) {
					for (Field lField : lControls.fields) {
						// On récupère la valeur du champ par introspection
						java.lang.reflect.Field val;
						val = lOccurrence.getClass().getField(lField.camelCase);
						Object object = val.get(lOccurrence);
						String value = (String) object;

						// On controle si le taxon est dans le référentiel
						if (value != null && !value.isEmpty()) {
							nb = Espece.count("cdNom", value);
						}
					}
					Result lResult = new Result(lOccurrence, lControls,
							String.valueOf(nb));
					lResult.save();
				}
			}
		} catch (NoSuchFieldException e) {
			Logger.error(e.toString(), "Taxon");
		} catch (IllegalArgumentException e) {
			Logger.error(e.toString(), "Taxon");
		} catch (IllegalAccessException e) {
			Logger.error(e.toString(), "Taxon");
		}
	}
}
