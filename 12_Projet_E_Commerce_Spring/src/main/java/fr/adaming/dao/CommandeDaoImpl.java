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

	// setter pour l'injection de dépendance
	public void setSf(SessionFactory sf) {
		this.sf = sf;

	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Commande> getAllCommandes() {

		// recupérer la session hibernate
		Session s = sf.getCurrentSession();

		// construire la requete HQL
		String req = "FROM Commande";

		// créer un query
		Query query = s.createQuery(req);

		// envoi de la requete et récupération du résultat
		return query.list();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Commande> getAllCommandesFromClient(Client client) {

		// recupérer la session hibernate
		Session s = sf.getCurrentSession();

		// construire la requete JPQL
		String req = "FROM Commande as comm where comm.client.idClient=:pIdClient";

		// créer un query
		Query query = s.createQuery(req);

		// assignation des paramètres
		query.setParameter("pIdClient", client.getIdClient());

		// envoi de la requete et récupération du résultat
		return query.list();
	}

	@Override
	public Commande addCommande(Commande comm) {

		// recupérer la session hibernate
		Session s = sf.getCurrentSession();

		s.save(comm);
		return comm;
	}

	@Override
	public Commande updateCommande(Commande comm) {

		// recupérer la session hibernate
		Session s = sf.getCurrentSession();

		s.saveOrUpdate(comm);
		return comm;
	}

	@Override
	public int deleteCommande(int idCommande) {

		// recupérer la session hibernate
		Session s = sf.getCurrentSession();

		// création de la requete JPQL
		String req = "delete Commande as comm where comm.idCommande = :pId";

		// creation du query
		Query query = s.createQuery(req);

		// assignation des paramètres de la requete
		query.setParameter("pId", idCommande);

		return query.executeUpdate();
	}

	@Override
	public Commande getCommandeById(Commande comm) {

		// recupérer la session hibernate
		Session s = sf.getCurrentSession();

		return (Commande) s.get(Commande.class, comm.getIdCommande());
	}

	@SuppressWarnings("unchecked")
	@Override
	public double getPrixTotalAvantRemise(Commande com) {

		// recupérer la session hibernate
		Session s = sf.getCurrentSession();

		String req = "FROM LignesCommande as l where l.commande.idCommande=:pIdcom";
		// creation du query
		Query query = s.createQuery(req);

		// assignation des paramètres de la requete
		query.setParameter("pIdcom", com.getIdCommande());
		List<LignesCommande> liste = query.list();

		double somme = 0;

		for (LignesCommande l : liste) {
			double prixAvant = (l.getPrix() / (1 - (l.getProduit().getRemise()) / 100));
			somme = somme + prixAvant;
		}

		return somme;

	}

	@SuppressWarnings("unchecked")
	public double getPrixTotalApresRemise(Commande com) {

		// recupérer la session hibernate
		Session s = sf.getCurrentSession();

		String req = "FROM LignesCommande as l where l.commande.idCommande=:pIdcom";
		// creation du query
		Query query = s.createQuery(req);

		// assignation des paramètres de la requete
		query.setParameter("pIdcom", com.getIdCommande());
		List<LignesCommande> liste = query.list();
		double somme = 0;
		for (int i = 0; i < liste.size(); i++) {
			double prixApres = liste.get(i).getPrix();
			somme = somme + prixApres;
		}

		return somme;

	}

}
