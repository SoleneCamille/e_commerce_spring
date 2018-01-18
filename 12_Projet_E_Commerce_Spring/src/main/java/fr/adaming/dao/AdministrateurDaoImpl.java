package fr.adaming.dao;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import fr.adaming.model.Administrateur;

@Repository
public class AdministrateurDaoImpl implements IAdministrateurDao {

	@Autowired // injection du collaborateur sf
	private SessionFactory sf;

	// setter pour l'injection de d�pendance
	public void setSf(SessionFactory sf) {
		this.sf = sf;

	}

	@Override
	public Administrateur isExist(Administrateur a) throws Exception {
		// recup�rer la session hibernate
		Session s = sf.getCurrentSession();
		// construire la requete HQL
		String req = "FROM Administrateur as a where a.mail=:pMail and a.mdp=:pMdp";

		// cr�er un query
		Query query = s.createQuery(req);

		// assignation des param�tres de la requete
		query.setParameter("pMail", a.getMail());
		query.setParameter("pMdp", a.getMdp());

		// envoi de la requete et r�cup de l'agent
		Administrateur aOut = (Administrateur) query.uniqueResult();

		return aOut;
	}
}
