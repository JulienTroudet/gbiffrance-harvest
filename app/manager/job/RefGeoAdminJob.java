package manager.job;

import java.util.HashMap;
import java.util.List;

import manager.AdmMG;
import manager.OccurrenceMG;
import models.Adm;
import models.Controls;
import models.Field;
import models.Occurrence;
import models.Result;
import play.Logger;
import play.jobs.Job;

/**
 * Correspond au processus lancé en asynchrone, de contrôle de référentiel
 * géographique et administratif sur un un dataset pour chaque occurence.
 * 
 * @author Rémy PLAISANCE
 * 
 */
public class RefGeoAdminJob extends Job {

	private Controls mControls;

	private long mIdDataset;

	/**
	 * Constructeur.
	 * 
	 * @param pControls
	 * @param pIdDataset
	 */
	public RefGeoAdminJob(Controls pControls, long pIdDataset) {
		this.mControls = pControls;
		this.mIdDataset = pIdDataset;
	}

	/*
	 * Permet de lancer le traitement dans un thread séparé.
	 * 
	 * @see play.jobs.Job#doJob()
	 */
	public void doJob() {
		Logger.info("Debut du controle de reference geographique");
		OccurrenceMG lOccurrenceMG = new OccurrenceMG();
		List<Occurrence> lListOccurence = lOccurrenceMG
				.listOccurencesByDataset(mIdDataset);
		// On recharge l'objet car dans un thread séparé il perd la session
		// (JPA)
		Controls lControls = Controls.findById(mControls.id);

		if (lControls.fields != null) {
			AdmMG lAdmMG = new AdmMG();
			HashMap<String, Adm> lHashMap = null;
			if (lControls.refGeoAdmID == 1) {
				lHashMap = lAdmMG.getAll("Commune");
			} else if (lControls.refGeoAdmID == 2) {
				lHashMap = lAdmMG.getAll("Département");
			}
			for (Occurrence lOccurrence : lListOccurence) {
				int res = 0;
				try {
					if (lHashMap != null) {
						for (Field lField : lControls.fields) {
							java.lang.reflect.Field val = lOccurrence
									.getClass().getField(lField.camelCase);
							Object object = val.get(lOccurrence);
							Adm lVal = lHashMap.get(object);
							if (lVal != null) {
								res = 1;
							}
						}
					}
				} catch (SecurityException e) {
					Logger.error(e.toString(), "Ref geo");
				} catch (IllegalArgumentException e) {
					Logger.error(e.toString(), "Ref geo");
				} catch (NoSuchFieldException e) {
					Logger.error(e.toString(), "Ref geo");
				} catch (IllegalAccessException e) {
					Logger.error(e.toString(), "Ref geo");
				}
				Result lResult = new Result(lOccurrence, lControls,
						String.valueOf(res));
				lResult.save();
			}
		}
		Logger.info("Fin du controle de reference geographique");
	}
}
