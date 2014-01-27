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

public class ControlMG {

	/**
	 * Retourne une liste de controle disponible pour un datasetType et un type
	 * de controle ordonné par nom
	 * 
	 * @param pType
	 * @param pIdValidationType
	 * @return List<Controls>
	 */
	public List<Controls> listControlsByDatasetTypeValidationType(String pType,
			Long pIdValidationType) {
		StringBuilder lString = new StringBuilder();
		lString.append("select c from Controls c join c.datasetType t join c.validationType v where t.name like '%' || ? || '%' and v.id=?");

		lString.append(" order by c.name");

		TypedQuery<Controls> query = JPA.em().createQuery(lString.toString(),
				Controls.class);
		query.setParameter(1, pType);
		query.setParameter(2, pIdValidationType);

		return query.getResultList();
	}

	/**
	 * Retourne une liste de controle par datasetType
	 * 
	 * @param idDataset
	 * @return List<Controls>
	 */
	public List<Controls> listControlsDatasetType(Long idDataset) {
		StringBuilder lString = new StringBuilder();
		lString.append("select distinct c from Controls c join c.results r join r.occurrence o join o.dataset d where d.id=?");

		TypedQuery<Controls> query = JPA.em().createQuery(lString.toString(),
				Controls.class);
		query.setParameter(1, idDataset);

		return query.getResultList();
	}
}
