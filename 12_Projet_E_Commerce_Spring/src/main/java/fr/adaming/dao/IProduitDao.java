package fr.adaming.dao;

import java.util.List;

import fr.adaming.model.Categorie;
import fr.adaming.model.Produit;


public interface IProduitDao {
	
	public Produit addProduit(Produit p);
	public int deleteProduit(int id);
	public Produit updateProduit (Produit p);
	public Produit getProduitbyIdorName(Produit p);
	public Produit getProduitByName(String name);
	public List<Produit>getProduitByCat(Categorie c);

}
