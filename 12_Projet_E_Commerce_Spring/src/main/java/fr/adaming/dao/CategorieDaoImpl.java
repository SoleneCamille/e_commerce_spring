package fr.adaming.dao;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import fr.adaming.model.Categorie;

@Repository
public class CategorieDaoImpl implements ICategorieDao {

	@Autowired // injection du collaborateur sf
	private SessionFactory sf;

	// setter pour l'injection de dépendance
	public void setSf(SessionFactory sf) {
		this.sf = sf;

	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Categorie> getAllCategories() {
		// recupérer la session hibernate
		Session s = sf.getCurrentSession();
		// construire la requete HQL
		String req = "FROM Categorie";

		Query query=s.createQuery(req);
	
		return query.list();
	}

	

	@Override
	public Categorie addCategorie(Categorie cat) {
		// recupérer la session hibernate
		Session s = sf.getCurrentSession();
		s.save(cat);
		//envoi de la requete
		return cat;
	}

	@Override
	public Categorie updateCategorie(Categorie cat) {
		// recupérer la session hibernate
		Session s = sf.getCurrentSession();
		s.saveOrUpdate(cat);
		return cat;
	}

	@Override
	public int deleteCategorie(int idCategorie) {
		// recupérer la session hibernate
		Session s = sf.getCurrentSession();
		//création de la requete HQL
		String req = "delete Categorie as cat where cat.idCategorie = :pId";
		
		//creation du query
		Query query = s.createQuery(req);
		
		//assignation des paramètres de la requete
		query.setParameter("pId", idCategorie);
		
		return query.executeUpdate();
	}

	@Override
	public Categorie getCategorieByIdOrName(Categorie cat) {
		// recupérer la session hibernate
		Session s = sf.getCurrentSession();
		//creation de la requete HQL
		String req = "FROM Categorie as cat where cat.idCategorie=:pId "
				+ "or cat.nomCategorie=:pNom";
		
		//creation du query
		Query query = s.createQuery(req);
		
		//assignation des paramètres
		query.setParameter("pId", cat.getIdCategorie());
		query.setParameter("pNom", cat.getNomCategorie());
		
		//envoi de la requete et récupération du resultat
		Categorie catFind = (Categorie) query.uniqueResult();
			
		return catFind;
	}

}
