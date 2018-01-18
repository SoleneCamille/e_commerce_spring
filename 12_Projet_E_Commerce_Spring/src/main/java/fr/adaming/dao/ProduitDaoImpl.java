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

	// setter pour l'injection de d�pendance
	public void setSf(SessionFactory sf) {
		this.sf = sf;

	}

	@Override
	public Produit addProduit(Produit p) {
		// recup�rer la session hibernate
		Session s = sf.getCurrentSession();

		s.save(p);
		return p;

	}

	@Override
	public int deleteProduit(int id) {
		// recup�rer la session hibernate
		Session s = sf.getCurrentSession();

		// cr�ation de la requete JPQL
		String req = "delete Produit as p where p.idProduit =:pIdProduit ";

		// cr�ation du query
		Query query = s.createQuery(req);

		// assignation des param�tres
		query.setParameter("pIdProduit", id);

		// envoi de la requete
		int verif = query.executeUpdate();
		return verif;
	}

	@Override
	public Produit updateProduit(Produit p) {

		// recup�rer la session hibernate
		Session s = sf.getCurrentSession();
		
		Produit pOut = (Produit) s.get(Produit.class, p.getIdProduit());
		pOut.setDescription(p.getDescription());
		pOut.setDesignation(p.getDesignation());
		pOut.setImage(p.getImage());
		pOut.setPhoto(p.getPhoto());
		pOut.setPrix(p.getPrix());
		pOut.setQuantite(p.getQuantite());
		pOut.setRemise(p.getRemise());				

		s.saveOrUpdate(pOut);
		// envoi de la requete et recup du resultat
		return p;
	}

	@Override
	public Produit getProduitbyIdorName(Produit p) {

		// recup�rer la session hibernate
		Session s = sf.getCurrentSession();

		// creation de la requete JPQL
		String req = "FROM Produit as p where p.idProduit=:pId " + "or p.designation=:pNom";

		// creation du query
		Query query = s.createQuery(req);

		// assignation des param�tres
		query.setParameter("pId", p.getIdProduit());
		query.setParameter("pNom", p.getDesignation());

		// envoi de la requete et r�cup�ration du resultat
		Produit pFind = (Produit) query.uniqueResult();

		return pFind;

	}

	@Override
	public Produit getProduitByName(String name) {

		// recup�rer la session hibernate
		Session s = sf.getCurrentSession();

		Produit pFind = (Produit) s.get(Produit.class, name);
		return pFind;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Produit> getProduitByCat(Categorie c) {

		// recup�rer la session hibernate
		Session s = sf.getCurrentSession();

		// construire la requete JPQL
		String req = "FROM Produit as p WHERE p.categorie.idCategorie=:pIdC";

		// cr�er un query
		Query query = s.createQuery(req);
		query.setParameter("pIdC", c.getIdCategorie());

		// envoi de la requete et r�cup�ration du r�sultat
		return query.list();
	}

}
