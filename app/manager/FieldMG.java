package manager;

import java.util.List;

import javax.persistence.TypedQuery;

import models.Field;
import play.db.jpa.JPA;

/**
 * Manager pour la class Field
 * 
 * @author Rémy PLAISANCE
 * 
 */
public class FieldMG {

	/**
	 * Retoune une liste de champ par type de dataset ordonné par nom
	 * 
	 * @param idDataset
	 * @return List<Field>
	 */
	public List<Field> listFieldByDatasetType(Long idDataset) {
		StringBuilder lString = new StringBuilder();
		lString.append("select f from Field f join f.datasetTypes d where d.id=?");

		lString.append(" order by f.name");

		TypedQuery<Field> query = JPA.em().createQuery(lString.toString(),
				Field.class);
		query.setParameter(1, idDataset);

		return query.getResultList();
	}
}
