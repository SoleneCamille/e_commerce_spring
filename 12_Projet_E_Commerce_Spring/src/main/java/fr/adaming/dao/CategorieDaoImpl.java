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

	// setter pour l'injection de d�pendance
	public void setSf(SessionFactory sf) {
		this.sf = sf;

	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Categorie> getAllCategories() {
		// recup�rer la session hibernate
		Session s = sf.getCurrentSession();
		// construire la requete HQL
		String req = "FROM Categorie";

		Query query=s.createQuery(req);
	
		return query.list();
	}

	

	@Override
	public Categorie addCategorie(Categorie cat) {
		// recup�rer la session hibernate
		Session s = sf.getCurrentSession();
		s.save(cat);
		//envoi de la requete
		return cat;
	}

	@Override
	public Categorie updateCategorie(Categorie cat) {
		// recup�rer la session hibernate
		Session s = sf.getCurrentSession();
		
		Categorie cOut=(Categorie) s.get(Categorie.class, cat.getIdCategorie());
		cOut.setImage(cat.getImage());
		cOut.setPhoto(cat.getPhoto());
		cOut.setDescription(cat.getDescription());
		cOut.setNomCategorie(cat.getNomCategorie());
		s.saveOrUpdate(cOut);
		return cat;
	}

	@Override
	public int deleteCategorie(int idCategorie) {
		// recup�rer la session hibernate
		Session s = sf.getCurrentSession();
		//cr�ation de la requete HQL
		String req = "delete Categorie as cat where cat.idCategorie = :pId";
		
		//creation du query
		Query query = s.createQuery(req);
		
		//assignation des param�tres de la requete
		query.setParameter("pId", idCategorie);
		
		return query.executeUpdate();
	}

	@Override
	public Categorie getCategorieByIdOrName(Categorie cat) {
		// recup�rer la session hibernate
		Session s = sf.getCurrentSession();
		//creation de la requete HQL
		String req = "FROM Categorie as cat where cat.idCategorie=:pId "
				+ "or cat.nomCategorie=:pNom";
		
		//creation du query
		Query query = s.createQuery(req);
		
		//assignation des param�tres
		query.setParameter("pId", cat.getIdCategorie());
		query.setParameter("pNom", cat.getNomCategorie());
		
		//envoi de la requete et r�cup�ration du resultat
		Categorie catFind = (Categorie) query.uniqueResult();
			
		return catFind;
	}

}
