package manager;

import java.util.HashMap;
import java.util.List;

import javax.persistence.TypedQuery;

import play.db.jpa.JPA;
import models.Adm;
import models.Controls;

public class AdmMG {

	public HashMap<String, Adm> getAll(String pType) {

		TypedQuery<Adm> query = JPA.em().createQuery(
				"select distinct a from Adm a where niveauAdmin=?", Adm.class);
		query.setParameter(1, pType);

		List<Adm> lListAdm = query.getResultList();

		HashMap<String, Adm> lHashMap = new HashMap<String, Adm>();

		for (Adm lAdm : lListAdm) {
			lHashMap.put(lAdm.cdInseeRef, lAdm);
		}

		return lHashMap;

	}

}
