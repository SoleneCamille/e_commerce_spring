package fr.adaming.dao;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import fr.adaming.model.Client;
import fr.adaming.model.Commande;
import fr.adaming.model.LignesCommande;

@Repository
public class CommandeDaoImpl implements ICommandeDao {

	@Autowired // injection du collaborateur sf
	private SessionFactory sf;

	// setter pour l'injection de d�pendance
	public void setSf(SessionFactory sf) {
		this.sf = sf;

	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Commande> getAllCommandes() {

		// recup�rer la session hibernate
		Session s = sf.getCurrentSession();

		// construire la requete HQL
		String req = "FROM Commande";

		// cr�er un query
		Query query = s.createQuery(req);

		// envoi de la requete et r�cup�ration du r�sultat
		return query.list();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Commande> getAllCommandesFromClient(Client client) {

		// recup�rer la session hibernate
		Session s = sf.getCurrentSession();

		// construire la requete JPQL
		String req = "FROM Commande as comm where comm.client.idClient=:pIdClient";

		// cr�er un query
		Query query = s.createQuery(req);

		// assignation des param�tres
		query.setParameter("pIdClient", client.getIdClient());

		// envoi de la requete et r�cup�ration du r�sultat
		return query.list();
	}

	@Override
	public Commande addCommande(Commande comm) {

		// recup�rer la session hibernate
		Session s = sf.getCurrentSession();

		s.save(comm);
		return comm;
	}

	@Override
	public Commande updateCommande(Commande comm) {

		// recup�rer la session hibernate
		Session s = sf.getCurrentSession();

		s.saveOrUpdate(comm);
		return comm;
	}

	@Override
	public int deleteCommande(int idCommande) {

		// recup�rer la session hibernate
		Session s = sf.getCurrentSession();

		// cr�ation de la requete JPQL
		String req = "delete Commande as comm where comm.idCommande = :pId";

		// creation du query
		Query query = s.createQuery(req);

		// assignation des param�tres de la requete
		query.setParameter("pId", idCommande);

		return query.executeUpdate();
	}

	@Override
	public Commande getCommandeById(Commande comm) {

		// recup�rer la session hibernate
		Session s = sf.getCurrentSession();

		return (Commande) s.get(Commande.class, comm.getIdCommande());
	}

	static public double arrondir(double value, int n) {
		double r = (Math.round(value * Math.pow(10, n))) / (Math.pow(10, n));
		return r;
	}

	@SuppressWarnings("unchecked")
	@Override
	public double getPrixTotalAvantRemise(Commande com) {

		// recup�rer la session hibernate
		Session s = sf.getCurrentSession();

		String req = "FROM LignesCommande as l where l.commande.idCommande=:pIdcom";
		// creation du query
		Query query = s.createQuery(req);

		// assignation des param�tres de la requete
		query.setParameter("pIdcom", com.getIdCommande());
		List<LignesCommande> liste = query.list();

		double somme = 0;

		for (LignesCommande l : liste) {
			double prixAvant = (l.getPrix() / (1 - (l.getProduit().getRemise()) / 100));
			somme = somme + prixAvant;
		}

		return arrondir(somme, 2);

	}

	@SuppressWarnings("unchecked")
	public double getPrixTotalApresRemise(Commande com) {

		// recup�rer la session hibernate
		Session s = sf.getCurrentSession();

		String req = "FROM LignesCommande as l where l.commande.idCommande=:pIdcom";
		// creation du query
		Query query = s.createQuery(req);

		// assignation des param�tres de la requete
		query.setParameter("pIdcom", com.getIdCommande());
		List<LignesCommande> liste = query.list();
		double somme = 0;
		for (int i = 0; i < liste.size(); i++) {
			double prixApres = liste.get(i).getPrix();
			somme = somme + prixApres;
		}

		return arrondir(somme, 2);

	}

}
