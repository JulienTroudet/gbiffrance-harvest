package manager;

import java.util.List;

import javax.persistence.TypedQuery;

import models.Groups;
import play.db.jpa.JPA;

/**
 * Manager pour la class User
 * 
 * @author Rémy PLAISANCE
 * 
 */
public class UserMG {

	/**
	 * Verifie si l'utilisateur à les groupes nécessaire pour se connecter à
	 * l'application
	 * 
	 * @param idUser
	 * @return nombre de groupe de l'utilisateur pour l'application
	 */
	public Long verifGroup(Long idUser) {
		StringBuilder lBuilder = new StringBuilder(
				"select count(g) from Groups g where g.id in ");
		lBuilder.append("(select m.id_group from Memberships m where m.id_member = ? and m.user_member = '1')");
		lBuilder.append(" and g.name_group like '%PDQI%'");
		TypedQuery<Long> lQuery = JPA.em().createQuery(lBuilder.toString(),
				Long.class);
		lQuery.setParameter(1, idUser);
		return lQuery.getSingleResult();
	}

	/**
	 * Retourne les groupes de l'utilisateur
	 * 
	 * @param idUtilisateur
	 * @return List<Groups>
	 */
	public List<Groups> listGroupByUser(Long idUtilisateur) {
		TypedQuery<Groups> q = JPA
				.em()
				.createQuery(
						"from Groups g where g.id_group IN (select m.id_group from Memberships m where m.id_member = :idUser and m.user_member = '1')",
						Groups.class);
		q.setParameter("idUser", idUtilisateur);
		return q.getResultList();
	}
}
