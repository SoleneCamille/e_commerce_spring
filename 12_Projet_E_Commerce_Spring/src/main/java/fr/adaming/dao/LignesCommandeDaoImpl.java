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

	// setter pour l'injection de dépendance
	public void setSf(SessionFactory sf) {
		this.sf = sf;

	}

	@SuppressWarnings("unchecked")
	@Override
	public List<LignesCommande> getAllLignes(int idCommande) {

		// recupérer la session hibernate
		Session s = sf.getCurrentSession();

		// construire la requete HQL
		String req = "FROM LignesCommande as l where l.commande.idCommande=:pIdComm";

		// créer un query
		Query query = s.createQuery(req);

		// assignation des paramètres
		query.setParameter("pIdComm", idCommande);

		// envoi de la requete et récupération du result
		return query.list();
	}

	@Override
	public LignesCommande addLigne(LignesCommande ligne) {

		// recupérer la session hibernate
		Session s = sf.getCurrentSession();

		s.save(ligne);
		// envoi de la requete
		return ligne;
	}

	@Override
	public LignesCommande updateLigne(LignesCommande ligne) {

		// recupérer la session hibernate
		Session s = sf.getCurrentSession();

		s.saveOrUpdate(ligne);
		return ligne;
	}

	@Override
	public int deleteLigne(int idLigne) {

		// recupérer la session hibernate
		Session s = sf.getCurrentSession();

		// creation de la requete JPQL
		String req = "delete LignesCommande as l where l.idLigne=:pIdLigne";

		// créer un query
		Query query = s.createQuery(req);

		// assignation des paramètres
		query.setParameter("pIdLigne", idLigne);

		return query.executeUpdate();
	}

	@Override
	public LignesCommande getLigneById(LignesCommande ligne) {

		// recupérer la session hibernate
		Session s = sf.getCurrentSession();

		return (LignesCommande) s.get(LignesCommande.class, ligne.getIdLigne());
	}

	@Override
	public LignesCommande getLigneByIdProduit(Produit p) {

		// recupérer la session hibernate
		Session s = sf.getCurrentSession();

		// construire la requete JPQL
		String req = "FROM LignesCommande as l where l.produit.idProduit=:pIdProd";

		// créer un query
		Query query = s.createQuery(req);

		// assignation des paramètres
		query.setParameter("pIdProd", p.getIdProduit());

		// envoi de la requete et récupération du result
		return (LignesCommande) query.uniqueResult();
	}

}
