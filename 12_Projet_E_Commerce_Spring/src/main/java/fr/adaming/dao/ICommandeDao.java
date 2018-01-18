package fr.adaming.dao;

import java.util.List;


import fr.adaming.model.Client;
import fr.adaming.model.Commande;


public interface ICommandeDao {

	// déclaration des méthodes
	public List<Commande> getAllCommandes();
	
	public List<Commande> getAllCommandesFromClient(Client client);

	public Commande addCommande(Commande comm);

	public Commande updateCommande(Commande comm);

	public int deleteCommande(int idCommande);

	public Commande getCommandeById(Commande comm);
	
	public double getPrixTotalAvantRemise(Commande com);
	
	public double getPrixTotalApresRemise(Commande com);

}
