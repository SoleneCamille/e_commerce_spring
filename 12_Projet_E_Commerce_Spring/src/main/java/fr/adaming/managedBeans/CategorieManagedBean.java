
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

@ManagedBean(name = "catMB")
@ViewScoped
public class CategorieManagedBean implements Serializable {

	// transformation de l'association UML en java
	@ManagedProperty(value = "#{catService}")
	private ICategorieService categorieService;

	@ManagedProperty(value = "#{pdtService}")
	private IProduitService produitService;

	private Categorie categorie;
	private List<Categorie> listeCategories;
	private List<Produit> listeProduits;

	private String recherche;

	private HttpSession maSession;
	private String image;

	public CategorieManagedBean() {
		this.categorie = new Categorie();
	}

	// m�thode qui s'ex�cute apr�s l'instanciation du managedBean
	@PostConstruct
	public void init() {
		this.maSession = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
	}

	// getters et setters
	public Categorie getCategorie() {
		return categorie;
	}

	public void setCategorie(Categorie categorie) {
		this.categorie = categorie;
	}

	public List<Categorie> getListeCategories() {
		return listeCategories;
	}

	public void setListeCategories(List<Categorie> listeCategories) {
		this.listeCategories = listeCategories;
	}

	public void setCategorieService(ICategorieService categorieService) {
		this.categorieService = categorieService;
	}

	public void setProduitService(IProduitService produitService) {
		this.produitService = produitService;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public List<Produit> getListeProduits() {
		return listeProduits;
	}

	public void setListeProduits(List<Produit> listeProduits) {
		this.listeProduits = listeProduits;
	}

	public String getRecherche() {
		return recherche;
	}

	public void setRecherche(String recherche) {
		this.recherche = recherche;
	}

	// les m�thodes m�tiers
	public String entrerSite() {
		// r�cup�rer la liste de cat�gories
		List<Categorie> listOut = categorieService.getAllCategories();
		this.listeCategories = new ArrayList<Categorie>();

		for (Categorie element : listOut) {
			if (element.getPhoto() == null) {
				element.setImage(null);
			} else {
				element.setImage("data:image/png;base64," + Base64.encodeBase64String(element.getPhoto()));
			}
			this.listeCategories.add(element);
		}

		// ajouter la liste dans la session
		maSession.setAttribute("categoriesList", listeCategories);

		return "accueil";
	}

	public String ajouterCategorie() {
		this.categorie = categorieService.addCategorie(this.categorie);

		if (this.categorie != null) {
			// r�cup�ration de la nouvelle liste de la bd
			List<Categorie> listOut = categorieService.getAllCategories();
			this.listeCategories = new ArrayList<Categorie>();

			for (Categorie element : listOut) {
				if (element.getPhoto() == null) {
					element.setImage(null);
				} else {
					element.setImage("data:image/jpeg;base64," + Base64.encodeBase64String(element.getPhoto()));
				}
				this.listeCategories.add(element);
			}
			// mettre � jour la liste dans la session
			maSession.setAttribute("categoriesList", this.listeCategories);

			return "accueilAdmin";
		} else {
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, "Cette cat�gorie n'a pas pu �tre ajout�e !", null));
			return "accueil";
		}

	}

	public String modifierCategorie() {
		this.categorie = categorieService.updateCategorie(this.categorie);

		if (this.categorie != null) {
			// r�cup�ration de la nouvelle liste de la bd
			List<Categorie> listOut = categorieService.getAllCategories();
			this.listeCategories = new ArrayList<Categorie>();

			for (Categorie element : listOut) {
				if (element.getPhoto() == null) {
					element.setImage(null);
				} else {
					element.setImage("data:image/jpeg;base64," + Base64.encodeBase64String(element.getPhoto()));
				}
				this.listeCategories.add(element);
			}

			// mettre � jour la liste dans la session
			maSession.setAttribute("categoriesList", this.listeCategories);

			return "accueilAdmin";
		} else {
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, "Cette cat�gorie n'existe pas !", null));
			return "modifCat";
		}

	}

	public String supprimerCategorie() {
		Categorie catOut = categorieService.getCategorieByIdOrName(this.categorie);

		if (catOut != null) {

			// r�cup�ration de la liste des produits de cette categorie
			listeProduits = catOut.getListeProduits();

			// supprimer les produits de cette categorie
			for (Produit p : listeProduits) {
				produitService.deleteProduit(p.getIdProduit());
			}

			// supprimer la categorie
			categorieService.deleteCategorie(catOut.getIdCategorie());

			// r�cup�ration de la nouvelle liste de categories de la bd
			this.listeCategories = categorieService.getAllCategories();

			// mettre � jour la liste dans la session
			maSession.setAttribute("categoriesList", this.listeCategories);

			return "accueilAdmin";
		} else {
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, "Cette cat�gorie n'existe pas !", null));
			return "supprCat";
		}

	}

	public String consulterCategorie() {
		Categorie catFind = categorieService.getCategorieByIdOrName(this.categorie);

		if (catFind != null) {
			if (catFind.getPhoto() == null) {
				catFind.setImage(null);
			} else {
				catFind.setImage("data:image/png;base64," + Base64.encodeBase64String(catFind.getPhoto()));
			}

			this.categorie = catFind;

			// ajout de la cat�gorie dans la session
			maSession.setAttribute("categ", this.categorie);

			// r�cup�ration des produits de la categorie
			List<Produit> liste = this.categorie.getListeProduits();
			this.listeProduits = new ArrayList<Produit>();

			if (liste != null) {
				for (Produit element : liste) {
					if (element.getPhoto() == null) {
						element.setImage(null);
					} else {
						element.setImage("data:image/jpeg;base64," + Base64.encodeBase64String(element.getPhoto()));
					}
					this.listeProduits.add(element);
				}

				// ajout de la liste de produits dans la session
				maSession.setAttribute("listeProd2", this.listeProduits);

			} else {
				FacesContext.getCurrentInstance().addMessage(null,
						new FacesMessage("Pas de produit dans cette cat�gorie"));

			}

		} else {
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, "Cette cat�gorie n'existe pas !", null));

		}

		return "rechercheCat";

	}

	// m�thode pour transformer une image en table de byte
	public void upload(FileUploadEvent event) {
		UploadedFile uploadedFile = event.getFile();
		// r�cup�rer le contenu de l'image en byte
		byte[] contents = uploadedFile.getContents();

		// stocker le contenu dans l'attribut photo de categorie
		categorie.setPhoto(contents);
		// transforme byteArray en string (format base64)
		this.image = "data:image/png;base64," + Base64.encodeBase64String(contents);
	}

	public String rechercherCat() {
		// r�cup�rer la liste de cat�gories
		this.listeCategories = categorieService.getAllCategories();

		// cr�er une nouvelle liste ou on stocke les cat�gories recherch�es
		List<Categorie> listOut = new ArrayList<Categorie>();

		for (Categorie element : listeCategories) {
			if (element.getNomCategorie().startsWith(this.recherche)) {
				if (element.getPhoto() == null) {
					element.setImage(null);
				} else {
					element.setImage("data:image/jpeg;base64," + Base64.encodeBase64String(element.getPhoto()));
				}
				listOut.add(element);
			}
		}

		// enregister la nouvelle liste dans la session
		maSession.setAttribute("categoriesList", listOut);

		return "accueil";
	}
}
