package fr.adaming.managedBeans;

import java.io.Serializable;
import java.util.List;

import javax.annotation.PostConstruct;


import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

import fr.adaming.model.Commande;
import fr.adaming.model.LignesCommande;
import fr.adaming.model.Produit;
import fr.adaming.service.ILignesCommandeService;
import fr.adaming.service.IProduitService;

@ManagedBean(name = "lMB")
@RequestScoped
public class LignesCommandeManagedBean implements Serializable {

	// transformation de l'association UML en java
	@ManagedProperty(value = "#{ligneCommService}")
	private ILignesCommandeService ligneService;

	@ManagedProperty(value = "#{pdtService}")
	private IProduitService produitService;

	private LignesCommande ligne;
	private List<LignesCommande> listeLignes;
	private Produit produit;

	private int qteAchetee;

	private HttpSession maSession;

	public LignesCommandeManagedBean() {
		this.ligne = new LignesCommande();
	}

	@PostConstruct
	public void init() {
		this.maSession = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
	}

	public LignesCommande getLigne() {
		return ligne;
	}

	public void setLigne(LignesCommande ligne) {
		this.ligne = ligne;
	}

	public List<LignesCommande> getListeLignes() {
		return listeLignes;
	}

	public void setListeLignes(List<LignesCommande> listeLignes) {
		this.listeLignes = listeLignes;
	}

	public void setLigneService(ILignesCommandeService ligneService) {
		this.ligneService = ligneService;
	}

	public Produit getProduit() {
		return produit;
	}

	public void setProduit(Produit produit) {
		this.produit = produit;
	}

	public void setProduitService(IProduitService produitService) {
		this.produitService = produitService;
	}

	public int getQteAchetee() {
		return qteAchetee;
	}

	public void setQteAchetee(int qteAchetee) {
		this.qteAchetee = qteAchetee;
	}

	// methodes

	public String ajouterLigne() {
		Commande comDefaut = new Commande();
		comDefaut.setIdCommande(1);
		if (!this.produit.isSelectionne()) {
			this.ligne = ligneService.addLigne(this.ligne, comDefaut, this.produit);
		} else {
			this.ligne = ligneService.getLigneByIdProduit(this.produit);
			int quantite = this.ligne.getQuantite();

			this.ligne.setQuantite(quantite + 1);
			double prix = (this.ligne.getPrix() / quantite) * (quantite + 1);
			this.ligne.setPrix(prix);
			double prixAvantRemise = (this.produit.getPrix() * (quantite + 1));
			this.ligne.setPrixAvantRemise(prixAvantRemise);
			ligneService.updateLigne(this.ligne, comDefaut, this.produit);
		}

		// récupérer la liste de lignes dont la commande est nulle
		this.listeLignes = ligneService.getAllLignes(comDefaut.getIdCommande());

		// ajouter la liste dans la session
		maSession.setAttribute("lignesListe", this.listeLignes);

		return "panier";
	}

	public String viderPanier() {
		this.listeLignes = ligneService.getAllLignes(1);

		for (LignesCommande element : listeLignes) {
			ligneService.deleteLigne(element);
		}

		return "accueil";
	}

	public String voirPanier() {
		this.listeLignes = ligneService.getAllLignes(1);

		if (this.listeLignes != null) {
			// ajout de la liste dans la session
			maSession.setAttribute("lignesListe", this.listeLignes);
		}

		return "panier";
	}

	public String supprimerLigne() {
		ligneService.deleteLigne(this.ligne);

		// récupérer la nouvelle liste de lignes de la BD
		this.listeLignes = ligneService.getAllLignes(1);

		// ajouter la liste dans la session
		maSession.setAttribute("lignesListe", this.listeLignes);

		return "panier";
	}

	public String augmenterQuantite() {

		Commande comDefaut = new Commande();
		comDefaut.setIdCommande(1);

		System.out.println(this.ligne.getQuantite());

		this.ligne.setQuantite(this.ligne.getQuantite() + 1);

		this.ligne = ligneService.updateLigne(this.ligne, comDefaut, this.ligne.getProduit());

		return "panier";
	}

	public String diminuerQuantite() {

		Commande comDefaut = new Commande();
		comDefaut.setIdCommande(1);

		if (this.ligne.getQuantite() > 1) {
			this.ligne.setQuantite(this.ligne.getQuantite() - 1);
			this.ligne = ligneService.updateLigne(this.ligne, comDefaut, this.ligne.getProduit());
		} else {
			if (this.ligne.getQuantite() == 1) {
				ligneService.deleteLigne(this.ligne);
			}
		}

		// récupérer la nouvelle liste de lignes de la BD
		this.listeLignes = ligneService.getAllLignes(1);

		// ajouter la liste dans la session
		maSession.setAttribute("lignesListe", this.listeLignes);

		return "panier";
	}

}
