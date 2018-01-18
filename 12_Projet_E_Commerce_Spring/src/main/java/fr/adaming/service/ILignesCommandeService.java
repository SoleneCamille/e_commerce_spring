package fr.adaming.service;

import java.util.List;

import fr.adaming.model.Commande;
import fr.adaming.model.LignesCommande;
import fr.adaming.model.Produit;

public interface ILignesCommandeService {	

	// déclaration des méthodes
	public List<LignesCommande> getAllLignes(int idCommande);

	public LignesCommande addLigne(LignesCommande ligne, Commande comm, Produit p);

	public LignesCommande updateLigne(LignesCommande ligne, Commande comm, Produit p);

	public int deleteLigne(LignesCommande ligne);

	public LignesCommande getLigneById(LignesCommande ligne);
	
	public LignesCommande getLigneByIdProduit (Produit p);
	

}
