package fr.adaming.model;

import java.util.List;

public class Panier {
	
	//d�claration des attributs
	private List<LignesCommande> listeLignes;

	
	//d�claration d'un constructeur vide
	public Panier() {
		super();
	}

	//getters et setters
	public List<LignesCommande> getListeLignes() {
		return listeLignes;
	}


	public void setListeLignes(List<LignesCommande> listeLignes) {
		this.listeLignes = listeLignes;
	}
	
	
	

}
