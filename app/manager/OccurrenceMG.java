package manager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import javax.persistence.Query;
import javax.persistence.TypedQuery;

import models.Controls;
import models.Field;
import models.Occurrence;
import play.Logger;
import play.db.DB;
import play.db.jpa.JPA;

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
			i++;
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
	 * Insertion d'une occurrence avec des données géographiques en JDBC. On
	 * utilise ici une requete JDBC suite a une problematique avec le driver
	 * oracle spatial.
	 * 
	 * @param pOccurrence
	 */
	public void insertINPN(Occurrence pOccurrence) {

		Connection lConnection = null;
		PreparedStatement lPreparedStatement = null;

		try {
			lConnection = DB.getConnection();

			// On prepare la requête
			StringBuilder lBuilder = new StringBuilder(
					"INSERT INTO OCCURRENCE (ID, CATALOGNUMBER, COORDINATEUNCERTAINTYINMETERS, DECIMALLATITUDE, DECIMALLONGITUDE");
			lBuilder.append(", IDENTIFIEDBY, LOCALITY, NAMEACCORDINGTO, OCCURRENCESTATUS, RECORDNUMBER, TAXONID, TYPE_SOURCE");
			lBuilder.append(", RESTRICTION_LOCALISATION_P, RESTRICTION_MAILLE, RESTRICTION_COMMUNE, RESTRICTION_TOTAL");
			lBuilder.append(", LIEN_ORIGINE, NOM_SCIENTIFIQUE_CITE, IDENTITE_OBS, ORGANISME_OBS, VALIDATEUR, DATE_INF");
			lBuilder.append(", DATE_SUP, CODE_INSEE, NOM_COMMUNE, POURCENTAGE_COMMUNE, CODE_EN, TYPE_EN, POURCENTAGE_EN");
			lBuilder.append(", CODE_MAILLE, POURCENTAGE_MAILLE, PROJECTION, DATASET_ID, QUALIFIED");
			if (pOccurrence.shape != null) {
				lBuilder.append(", SHAPE");
			}
			lBuilder.append(") VALUES (HIBERNATE_SEQUENCE.nextval, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?");
			if (pOccurrence.shape != null) {
				lBuilder.append(", SDO_UTIL.TO_WKTGEOMETRY(SDO_GEOMETRY(2001, NULL, SDO_POINT_TYPE(?,?,NULL),NULL,NULL))");
			}
			lBuilder.append(")");

			lPreparedStatement = lConnection.prepareStatement(lBuilder
					.toString());

			lPreparedStatement.setString(1, pOccurrence.catalogNumber);
			lPreparedStatement.setString(2,
					pOccurrence.coordinateUncertaintyInMeters);
			lPreparedStatement.setString(3, pOccurrence.decimalLatitude);
			lPreparedStatement.setString(4, pOccurrence.decimalLongitude);
			lPreparedStatement.setString(5, pOccurrence.identifiedBy);
			lPreparedStatement.setString(6, pOccurrence.locality);
			lPreparedStatement.setString(7, pOccurrence.nameAccordingTo);
			lPreparedStatement.setString(8, pOccurrence.occurrenceStatus);
			lPreparedStatement.setString(9, pOccurrence.recordNumber);
			lPreparedStatement.setString(10, pOccurrence.taxonID);
			lPreparedStatement.setString(11, pOccurrence.typeSource);
			lPreparedStatement.setString(12,
					pOccurrence.restrictionLocalisationP);
			lPreparedStatement.setString(13, pOccurrence.restrictionMaille);
			lPreparedStatement.setString(14, pOccurrence.restrictionCommune);
			lPreparedStatement.setString(15, pOccurrence.restrictionTotal);
			lPreparedStatement.setString(16, pOccurrence.lienOrigine);
			lPreparedStatement.setString(17, pOccurrence.nomScientifiqueCite);
			lPreparedStatement.setString(18, pOccurrence.identiteOBS);
			lPreparedStatement.setString(19, pOccurrence.organismeOBS);
			lPreparedStatement.setString(20, pOccurrence.validateur);
			lPreparedStatement.setString(21, pOccurrence.dateInf);
			lPreparedStatement.setString(22, pOccurrence.dateSup);
			lPreparedStatement.setString(23, pOccurrence.codeInsee);
			lPreparedStatement.setString(24, pOccurrence.nomCommune);
			lPreparedStatement.setString(25, pOccurrence.pourcentageCommune);
			lPreparedStatement.setString(26, pOccurrence.codeEN);
			lPreparedStatement.setString(27, pOccurrence.typeEN);
			lPreparedStatement.setString(28, pOccurrence.pourcentageEN);
			lPreparedStatement.setString(29, pOccurrence.codeMaille);
			lPreparedStatement.setString(30, pOccurrence.pourcentageMaille);
			lPreparedStatement.setString(31, pOccurrence.projection);
			lPreparedStatement.setLong(32, pOccurrence.dataset.id);
			lPreparedStatement.setBoolean(33, pOccurrence.qualified);
			if (pOccurrence.shape != null) {
				Logger.debug("insert shape");

				//lPreparedStatement.setDouble(34, pOccurrence.shape.getSRID());

				lPreparedStatement.setDouble(34,
						pOccurrence.shape.getCoordinate().x);
				lPreparedStatement.setDouble(35,
						pOccurrence.shape.getCoordinate().y);
			}

			lPreparedStatement.execute();
		} catch (SQLException e) {
			Logger.error(e.toString());
		} finally {
			try {
				if (lConnection != null) {
					lConnection.close();
				}
				if (lPreparedStatement != null) {
					lPreparedStatement.close();
				}
			} catch (SQLException e) {
				Logger.error(e.toString());
			}
		}
	}

}
