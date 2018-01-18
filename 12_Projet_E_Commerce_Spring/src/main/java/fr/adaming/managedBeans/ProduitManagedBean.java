package fr.adaming.managedBeans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

import org.apache.commons.codec.binary.Base64;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;

import fr.adaming.model.Categorie;
import fr.adaming.model.Produit;
import fr.adaming.service.ICategorieService;
import fr.adaming.service.IProduitService;

@ManagedBean(name = "pMB")
@ViewScoped
public class ProduitManagedBean implements Serializable {

	// UML en java

	@ManagedProperty(value = "#{pdtService}")
	private IProduitService produitService;

	@ManagedProperty(value = "#{catService}")
	private ICategorieService categorieService;

	private Produit produit;
	private List<Produit> listeProduit;
	private Categorie categorie;

	private HttpSession maSession;

	private String image;

	// constructeur
	public ProduitManagedBean() {

		this.produit = new Produit();
		this.categorie = new Categorie();

	}

	@PostConstruct
	public void init() {
		this.maSession = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
	}

	// getters et setters
	public Produit getProduit() {
		return produit;
	}

	public void setProduit(Produit produit) {
		this.produit = produit;
	}

	public List<Produit> getListeProduit() {
		return listeProduit;
	}

	public void setListeProduit(List<Produit> listeProduit) {
		this.listeProduit = listeProduit;
	}

	public void setProduitService(IProduitService produitService) {
		this.produitService = produitService;
	}

	public Categorie getCategorie() {
		return categorie;
	}

	public void setCategorie(Categorie categorie) {
		this.categorie = categorie;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public void setCategorieService(ICategorieService categorieService) {
		this.categorieService = categorieService;
	}

	// methodes
	public String ajouterProduit() {

		this.categorie = categorieService.getCategorieByIdOrName(this.categorie);

		if (this.categorie != null) {
			this.produit = produitService.addProduit(this.produit, this.categorie);

			if (this.produit != null) {
				// récupération de la nouvelle liste de la bd
				List<Produit> listOut = produitService.getProduitByCat(this.categorie);
				this.listeProduit = new ArrayList<Produit>();

				for (Produit element : listOut) {
					if (element.getPhoto() == null) {
						element.setImage(null);
					} else {
						element.setImage("data:image/jpeg;base64," + Base64.encodeBase64String(element.getPhoto()));
					}
					this.listeProduit.add(element);
				}

				// mettre à jour la liste dans la session
				maSession.setAttribute("produitList", this.listeProduit);

				return "afficheProduit";
			} else {
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("L'ajout a échoué"));
				return "ajoutProduit";
			}
		} else {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Cette catégorie n'existe pas !"));
			return "ajoutProduit";
		}
	}

	public String afficherProduit() {
		Produit pFind = produitService.getProduitbyIdorName(this.produit);

		if (pFind != null) {
			if (pFind.getPhoto() == null) {
				pFind.setImage(null);
			} else {
				pFind.setImage("data:image/jpeg;base64," + Base64.encodeBase64String(pFind.getPhoto()));
			}

			this.produit = pFind;
			maSession.setAttribute("produit", this.produit);

			return "afficheProduit";
		} else {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Ce produit n'existe pas !"));
			return "afficheProduit";
		}
	}

	public String supprimerProduit() {
		int verif = produitService.deleteProduit(this.produit.getIdProduit());
		if (verif == 1) {
			return "rechercheCat";

		} else {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("echec suppression"));
			return "supprProduit";
		}

	}

	public String modifierProduit() {
		this.produit = produitService.updateProduit(this.produit, this.categorie);
		if (this.produit != null) {
			// récupération de la nouvelle liste de la bd
			List<Produit> listOut = produitService.getProduitByCat(this.categorie);
			this.listeProduit = new ArrayList<Produit>();

			for (Produit element : listOut) {
				if (element.getPhoto() == null) {
					element.setImage(null);
				} else {
					element.setImage("data:image/jpeg;base64," + Base64.encodeBase64String(element.getPhoto()));
				}
				this.listeProduit.add(element);
			}

			// mettre à jour la liste dans la session
			maSession.setAttribute("produitList", this.listeProduit);

			return "rechercheCat";
		} else {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("echec modification"));
			return "modifProduit";
		}
	}

	// méthode pour transformer une image en table de byte
	public void upload(FileUploadEvent event) {
		UploadedFile uploadedFile = event.getFile();
		// récupérer le contenu de l'image en byte
		byte[] contents = uploadedFile.getContents();

		// stocker le contenu dans l'attribut photo de categorie
		produit.setPhoto(contents);
		// transforme byteArray en string (format base64)
		this.image = "data:image/png;base64," + Base64.encodeBase64String(contents);
	}

}
