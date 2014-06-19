package manager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

import play.Logger;
import play.db.DB;

/**
 * Manager pour la table MONDE_COMMUNES du schéma SIG.
 * 
 * @author Rémy PLAISANCE
 * 
 */
public class MondeCommunesMG {

	public HashMap<Long, String> verifGeographique(Long idOccurence,
			String nomChamp) {
		Connection lConnection = null;
		PreparedStatement lPreparedStatement = null;
		lConnection = DB.getConnection();
		HashMap<Long, String> map = null;

		// On prepare la requête
		// On utilise ici une requete JDBC suite a une problematique avec le
		// driver oracle spatial
		StringBuilder lBuilder = new StringBuilder(
				"SELECT DT.ID, case when lower(?) = lower(MC.CD_INSEE) then '1' else '0' end as resultat_test");
		lBuilder.append(" FROM IPT.OCCURRENCE DT, SIG.MONDE_COMMUNES MC WHERE DT.id=?");
		lBuilder.append(" and DT.decimallongitude is not null and DT.decimallatitude is not null");
		lBuilder.append(" and SDO_RELATE(MC.shape,SDO_GEOMETRY(2001,4326,SDO_POINT_TYPE(cast(replace(DT.decimallongitude,'.',',') as number), ");
		lBuilder.append(" cast(replace(DT.decimallatitude,'.',',') as number), NULL), NULL, NULL), 'mask=contains')='TRUE'");

		try {
			lPreparedStatement = lConnection.prepareStatement(lBuilder
					.toString());

			lPreparedStatement.setString(1, "DT." + nomChamp);

			lPreparedStatement.setLong(2, idOccurence);

			ResultSet rs = lPreparedStatement.executeQuery();

			map = new HashMap<Long, String>();

			while (rs.next()) {
				map.put(rs.getLong("ID"), rs.getString("resultat_test"));
			}
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
		return map;
	}
}
