package fr.adaming.model;

import java.util.List;

public class Panier {
	
	//déclaration des attributs
	private List<LignesCommande> listeLignes;

	
	//déclaration d'un constructeur vide
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
