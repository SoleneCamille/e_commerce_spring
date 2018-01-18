package fr.adaming.dao;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import fr.adaming.model.Commande;
import fr.adaming.model.LignesCommande;
import fr.adaming.model.Produit;

@Repository
public class LignesCommandeDaoImpl implements ILignesCommandeDao {

	@Autowired // injection du collaborateur sf
	private SessionFactory sf;

	// setter pour l'injection de d�pendance
	public void setSf(SessionFactory sf) {
		this.sf = sf;

	}

	@SuppressWarnings("unchecked")
	@Override
	public List<LignesCommande> getAllLignes(int idCommande) {

		// recup�rer la session hibernate
		Session s = sf.getCurrentSession();

		// construire la requete HQL
		String req = "FROM LignesCommande as l where l.commande.idCommande=:pIdComm";

		// cr�er un query
		Query query = s.createQuery(req);

		// assignation des param�tres
		query.setParameter("pIdComm", idCommande);

		// envoi de la requete et r�cup�ration du result
		return query.list();
	}

	@Override
	public LignesCommande addLigne(LignesCommande ligne) {

		// recup�rer la session hibernate
		Session s = sf.getCurrentSession();

		s.save(ligne);
		// envoi de la requete
		return ligne;
	}

	@Override
	public LignesCommande updateLigne(LignesCommande ligne) {

		// recup�rer la session hibernate
		Session s = sf.getCurrentSession();

		s.saveOrUpdate(ligne);
		return ligne;
	}

	@Override
	public int deleteLigne(int idLigne) {

		// recup�rer la session hibernate
		Session s = sf.getCurrentSession();

		// creation de la requete JPQL
		String req = "delete LignesCommande as l where l.idLigne=:pIdLigne";

		// cr�er un query
		Query query = s.createQuery(req);

		// assignation des param�tres
		query.setParameter("pIdLigne", idLigne);

		return query.executeUpdate();
	}

	@Override
	public LignesCommande getLigneById(LignesCommande ligne) {

		// recup�rer la session hibernate
		Session s = sf.getCurrentSession();

		return (LignesCommande) s.get(LignesCommande.class, ligne.getIdLigne());
	}

	@Override
	public LignesCommande getLigneByIdProduit(Produit p) {

		// recup�rer la session hibernate
		Session s = sf.getCurrentSession();

		// construire la requete JPQL
		String req = "FROM LignesCommande as l where l.produit.idProduit=:pIdProd";

		// cr�er un query
		Query query = s.createQuery(req);

		// assignation des param�tres
		query.setParameter("pIdProd", p.getIdProduit());

		// envoi de la requete et r�cup�ration du result
		return (LignesCommande) query.uniqueResult();
	}

}
