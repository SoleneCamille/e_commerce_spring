package fr.adaming.managedBeans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

//import org.apache.tomcat.util.codec.binary.Base64;

import fr.adaming.model.Administrateur;
import fr.adaming.model.Categorie;
import fr.adaming.service.IAdministrateurService;
import fr.adaming.service.ICategorieService;

@ManagedBean(name = "aMB")
@RequestScoped
public class AdminManagedBean implements Serializable {

	@ManagedProperty(value = "#{adminService}")
	private IAdministrateurService adminService;

	@ManagedProperty(value = "#{catService}")
	private ICategorieService categorieService;

	private Administrateur admin;
	private List<Categorie> listeCategories;

	private HttpSession maSession;

	// méthode qui s'exécute après l'instanciation du managedBean
	@PostConstruct
	public void init() {
		this.maSession = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
	}

	// constructeur vide
	public AdminManagedBean() {
		super();
		this.admin = new Administrateur();
	}

	// getters & setters

	public IAdministrateurService getAdminService() {
		return adminService;
	}

	public void setAdminService(IAdministrateurService adminService) {
		this.adminService = adminService;
	}

	public ICategorieService getCategorieService() {
		return categorieService;
	}

	public void setCategorieService(ICategorieService categorieService) {
		this.categorieService = categorieService;
	}

	public Administrateur getAdmin() {
		return admin;
	}

	public void setAdmin(Administrateur admin) {
		this.admin = admin;
	}

	public List<Categorie> getListeCategories() {
		return listeCategories;
	}

	public void setListeCategories(List<Categorie> listeCategories) {
		this.listeCategories = listeCategories;
	}

	public String seConnecter() {

		try {
			Administrateur aOut = adminService.isExist(this.admin);

			//récupérer la liste de catégories
			List<Categorie> listOut = categorieService.getAllCategories();
			this.listeCategories = new ArrayList<Categorie>();
			
			for(Categorie element:listOut) {
				if (element.getPhoto()==null) {
					element.setImage(null);
				} else {
					//element.setImage("data:image/png;base64,"+Base64.encodeBase64String(element.getPhoto()));
				}
				this.listeCategories.add(element);
			}

			// mettre à jour la liste dans la session
			maSession.setAttribute("categoriesList", this.listeCategories);

			// ajouter l'admin dans la session
			maSession.setAttribute("adminSession", aOut);

			return "accueilAdmin";

		} catch (Exception e) {
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage("L'identifiant ou le mot de passe est incorrect"));

		}
		return "login";
	}

}
