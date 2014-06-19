package manager.job;

import java.util.List;

import manager.OccurrenceMG;
import models.Controls;
import models.EspecesComplet;
import models.Field;
import models.Occurrence;
import models.Result;
import play.Logger;
import play.db.jpa.GenericModel.JPAQuery;
import play.jobs.Job;

/**
 * Correspond au processus lancé en asynchrone, de contrôle de référentiel de la
 * localisation d'un taxon.
 * 
 * @author Rémy PLAISANCE
 * 
 */
public class LocalisationTaxonJob extends Job {

	private Controls mControls;

	private long mIdDataset;

	/**
	 * Constructeur.
	 * 
	 * @param pControls
	 *            Controls
	 * @param pIdDataset
	 *            long
	 */
	public LocalisationTaxonJob(Controls pControls, long pIdDataset) {
		this.mControls = pControls;
		this.mIdDataset = pIdDataset;
	}

	/*
	 * Permet de lancer le traitement dans un thread séparé.
	 * 
	 * @see play.jobs.Job#doJob()
	 */
	@Override
	public void doJob() {
		Logger.info("Debut du controle de localisation d'un taxon");
		try {
			OccurrenceMG lOccurrenceMG = new OccurrenceMG();
			List<Occurrence> lListOccurence = lOccurrenceMG
					.listOccurencesByDataset(mIdDataset);
			// On recharge l'objet car dans un thread séparé il perd la session
			// (JPA)
			Controls lControls = Controls.findById(mControls.id);

			if (lControls.fields != null) {
				for (Occurrence lOccurrence : lListOccurence) {
					int nb = 0;
					int res = 0;
					for (Field lField : lControls.fields) {
						// On récupère la valeur du champ par introspection
						java.lang.reflect.Field val;

						val = lOccurrence.getClass().getField(lField.camelCase);

						Object object = val.get(lOccurrence);
						String value = (String) object;
						EspecesComplet lEspeceComplet = EspecesComplet.find(
								"byLbNom", value).first();
						if (lEspeceComplet != null) {
							java.lang.reflect.Field valEspece;
							valEspece = lEspeceComplet.getClass().getField(
									lControls.codeTerritoire.toLowerCase());
							Object objectEspece = valEspece.get(lEspeceComplet);
							String valueEspece = (String) objectEspece;
							if (valueEspece.equals("I")
									|| valueEspece.equals("J")
									|| valueEspece.equals("M")
									|| valueEspece.equals("B")
									|| valueEspece.equals("C")
									|| valueEspece.equals("D")
									|| valueEspece.equals("E")
									|| valueEspece.equals("P")
									|| valueEspece.equals("S")) {
								nb++;
							}
						}
					}
					if (nb > 0) {
						res = 1;
					}
					Result lResult = new Result(lOccurrence, lControls,
							String.valueOf(res));
					lResult.save();
				}

			}
			Logger.info("Fin du controle de localisation d'un taxon");
		} catch (NoSuchFieldException e) {
			Logger.error(e.toString(), "Localisation taxon");
		} catch (SecurityException e) {
			Logger.error(e.toString(), "Localisation taxon");
		} catch (IllegalArgumentException e) {
			Logger.error(e.toString(), "Localisation taxon");
		} catch (IllegalAccessException e) {
			Logger.error(e.toString(), "Localisation taxon");
		}
	}
}
