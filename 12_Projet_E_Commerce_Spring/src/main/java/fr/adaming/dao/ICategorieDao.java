package fr.adaming.dao;

import java.util.List;

import fr.adaming.model.Categorie;

public interface ICategorieDao {
	
	//d�claration des m�thodes
	public List<Categorie> getAllCategories();
	
	public Categorie addCategorie(Categorie cat);
	
	public Categorie updateCategorie(Categorie cat);
	
	public int deleteCategorie(int idCategorie);
	
	public Categorie getCategorieByIdOrName(Categorie cat);

}
