package fr.adaming.dao;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import fr.adaming.model.Client;

@Repository
public class ClientDaoImpl implements IClientDao {

	@Autowired // injection du collaborateur sf
	private SessionFactory sf;

	// setter pour l'injection de dépendance
	public void setSf(SessionFactory sf) {
		this.sf = sf;

	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Client> getAllClients() {
		// recupérer la session hibernate
		Session s = sf.getCurrentSession();

		// construire la requete HQL
		String req = "FROM Client";

		// créer un query
		Query query = s.createQuery(req);

		// envoi de la requete et récupération du result
		return query.list();
	}

	@Override
	public Client addClient(Client client) {
		// recupérer la session hibernate
		Session s = sf.getCurrentSession();
		s.save(client);
		return client;
	}

	@Override
	public Client updateClient(Client client) {
		// recupérer la session hibernate
		Session s = sf.getCurrentSession();
		s.saveOrUpdate(client);
		return client;
	}

	@Override
	public int deleteClient(int idClient) {
		// recupérer la session hibernate
		Session s = sf.getCurrentSession();
		// creation de la requete HQL
		String req = "delete  Client as c where c.idClient=:pIdClient";

		// créer un query
		Query query = s.createQuery(req);

		// assignation des paramètres
		query.setParameter("pIdClient", idClient);

		return query.executeUpdate();
	}

	@Override
	public Client getClientById(Client client) {
		// recupérer la session hibernate
		Session s = sf.getCurrentSession();

		return (Client) s.get(Client.class, client.getIdClient());
	}

	@Override
	public Client isExist(Client client) {
		// recupérer la session hibernate
		Session s = sf.getCurrentSession();
		// creation de la requete HQL
		String req = "FROM Client as c where c.email=:pMail and c.mdp=:pMdp";

		// créer un query
		Query query = s.createQuery(req);

		// assignation des paramètres
		query.setParameter("pMail", client.getEmail());
		query.setParameter("pMdp", client.getMdp());

		return (Client) query.uniqueResult();
	}

}
