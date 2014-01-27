package manager;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Query;
import javax.persistence.TypedQuery;

import play.db.jpa.GenericModel.JPAQuery;
import play.db.jpa.JPA;
import models.Controls;
import models.Field;
import models.Occurrence;
import controllers.CRUD;

/**
 * Manager pour la class Occurrence
 * 
 * @author Rémy PLAISANCE
 * 
 */
public class OccurrenceMG {

	/**
	 * Retour une liste d'Occurences par Dataset paginé et ordonné
	 * 
	 * @param pOrder
	 * @param pNameField
	 * @param idDataset
	 * @param page
	 * @return List<Occurrence>
	 */
	public List<Occurrence> listOccurencesByDataset(String pOrder,
			String pNameField, Long idDataset, int page) {
		StringBuilder lString = new StringBuilder();
		lString.append("select o from Occurrence o where o.dataset.id=?");

		if (pOrder != null && !pOrder.isEmpty()) {
			if (pNameField != null && !pNameField.isEmpty()) {
				lString.append(" order by ?");
			}
		}
		TypedQuery<Occurrence> query = JPA.em().createQuery(lString.toString(),
				Occurrence.class);
		query.setParameter(1, idDataset);
		if (pOrder != null && !pOrder.isEmpty()) {
			if (pNameField != null && !pNameField.isEmpty()) {
				query.setParameter(2, "o." + pNameField + " " + pOrder);
			}
		}
		query.setFirstResult((page - 1) * 25);
		query.setMaxResults(25);
		return query.getResultList();
	}

	/**
	 * Retourne une liste d'occurence par dataset
	 * 
	 * @param idDataset
	 * @return List<Occurrence>
	 */
	public List<Occurrence> listOccurencesByDataset(Long idDataset) {
		StringBuilder lString = new StringBuilder();
		lString.append("select o from Occurrence o where o.dataset.id=?");

		TypedQuery<Occurrence> query = JPA.em().createQuery(lString.toString(),
				Occurrence.class);
		query.setParameter(1, idDataset);

		return query.getResultList();
	}

	/**
	 * Permet la mise à jour d'une occurence quand l'objet est détaché Mise à
	 * jour uniquement des champs : qualified et cdNom
	 * 
	 * @param pOccurrence
	 */
	public void updateOccurenceQualified(Occurrence pOccurrence) {
		Query queryUpdate = JPA
				.em()
				.createQuery(
						"update Occurrence o set o.qualified=?, o.cdNom=? where o.id=?");
		queryUpdate.setParameter(1, pOccurrence.qualified);
		queryUpdate.setParameter(2, pOccurrence.cdNom);
		queryUpdate.setParameter(3, pOccurrence.id);
		queryUpdate.executeUpdate();
	}

	/**
	 * Retourne le nombre de fois ou un element est trouvé selon une liste de
	 * champ
	 * 
	 * @param pListField
	 * @param pOccurrence
	 * @return Long
	 * @throws NoSuchFieldException
	 * @throws SecurityException
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 */
	public Long verifDuplicateField(List<Field> pListField,
			Occurrence pOccurrence) throws NoSuchFieldException,
			SecurityException, IllegalArgumentException, IllegalAccessException {
		// Construction de la requete
		StringBuilder lBuilder = new StringBuilder(
				"select count(o) from Occurrence o where ");
		int i = 1;

		for (Field lField : pListField) {
			if (pListField.size() >= i + 1) {
				lBuilder.append("o." + lField.camelCase + "=? or ");
			} else {
				lBuilder.append("o." + lField.camelCase + "=?");
			}
		}

		TypedQuery<Long> lQuery = JPA.em().createQuery(lBuilder.toString(),
				Long.class);

		i = 1;
		for (Field lField : pListField) {
			java.lang.reflect.Field val = pOccurrence.getClass().getField(
					lField.camelCase);
			Object object = val.get(pOccurrence);
			lQuery.setParameter(i, object);
			i++;
		}
		return lQuery.getSingleResult();
	}

	/**
	 * Retourne le nombre de champ qui ne sont pas remplis pour une occurence
	 * 
	 * @param pListField
	 * @param pOccurrence
	 * @return Long
	 */
	public Long verifRequireField(List<Field> pListField, Occurrence pOccurrence) {
		StringBuilder lBuilder = new StringBuilder(
				"select count(o) from Occurrence o where o.id=?");

		for (int i = 0; i < pListField.size(); i++) {
			lBuilder.append(" and ? is null");
		}

		TypedQuery<Long> lQuery = JPA.em().createQuery(lBuilder.toString(),
				Long.class);
		lQuery.setParameter(1, pOccurrence.id);

		int i = 2;
		for (Field lField : pListField) {
			lQuery.setParameter(i, lField.camelCase);

			i++;
		}

		return lQuery.getSingleResult();
	}
}
