package fr.adaming.service;

import java.util.List;


import fr.adaming.model.Client;
import fr.adaming.model.Commande;


public interface ICommandeService {

	// déclaration des méthodes
	public List<Commande> getAllCommandes();

	public List<Commande> getAllCommandesFromClient(Client client);

	public Commande addCommande(Commande comm, Client client);

	public Commande updateCommande(Commande comm, Client client);

	public int deleteCommande(int idCommande);

	public Commande getCommandeById(Commande comm);
	
	public double getPrixTotalAvantRemise(Commande com);
	public double getPrixTotalApresRemise(Commande com);

}
