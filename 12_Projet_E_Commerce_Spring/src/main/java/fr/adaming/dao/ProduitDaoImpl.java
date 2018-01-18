package fr.adaming.dao;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import fr.adaming.model.Categorie;
import fr.adaming.model.Produit;

@Repository
public class ProduitDaoImpl implements IProduitDao {

	@Autowired // injection du collaborateur sf
	private SessionFactory sf;

	// setter pour l'injection de dépendance
	public void setSf(SessionFactory sf) {
		this.sf = sf;

	}

	@Override
	public Produit addProduit(Produit p) {
		// recupérer la session hibernate
		Session s = sf.getCurrentSession();

		s.save(p);
		return p;

	}

	@Override
	public int deleteProduit(int id) {
		// recupérer la session hibernate
		Session s = sf.getCurrentSession();

		// création de la requete JPQL
		String req = "delete Produit as p where p.idProduit =:pIdProduit ";

		// création du query
		Query query = s.createQuery(req);

		// assignation des paramètres
		query.setParameter("pIdProduit", id);

		// envoi de la requete
		int verif = query.executeUpdate();
		return verif;
	}

	@Override
	public Produit updateProduit(Produit p) {

		// recupérer la session hibernate
		Session s = sf.getCurrentSession();

		s.saveOrUpdate(p);
		// envoi de la requete et recup du resultat
		return p;
	}

	@Override
	public Produit getProduitbyIdorName(Produit p) {

		// recupérer la session hibernate
		Session s = sf.getCurrentSession();

		// creation de la requete JPQL
		String req = "FROM Produit as p where p.idProduit=:pId " + "or p.designation=:pNom";

		// creation du query
		Query query = s.createQuery(req);

		// assignation des paramètres
		query.setParameter("pId", p.getIdProduit());
		query.setParameter("pNom", p.getDesignation());

		// envoi de la requete et récupération du resultat
		Produit pFind = (Produit) query.uniqueResult();

		return pFind;

	}

	@Override
	public Produit getProduitByName(String name) {

		// recupérer la session hibernate
		Session s = sf.getCurrentSession();

		Produit pFind = (Produit) s.get(Produit.class, name);
		return pFind;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Produit> getProduitByCat(Categorie c) {

		// recupérer la session hibernate
		Session s = sf.getCurrentSession();

		// construire la requete JPQL
		String req = "FROM Produit as p WHERE p.categorie.idCategorie=:pIdC";

		// créer un query
		Query query = s.createQuery(req);
		query.setParameter("pIdC", c.getIdCategorie());

		// envoi de la requete et récupération du résultat
		return query.list();
	}

}
