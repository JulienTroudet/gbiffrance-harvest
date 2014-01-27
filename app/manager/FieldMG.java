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
