package fr.adaming.managedBeans;

import java.io.FileNotFoundException;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.servlet.http.HttpSession;

import com.lowagie.text.DocumentException;
import com.sun.mail.smtp.SMTPTransport;

import fr.adaming.model.Client;
import fr.adaming.model.Commande;
import fr.adaming.model.LignesCommande;
import fr.adaming.model.Produit;
import fr.adaming.service.IClientService;
import fr.adaming.service.ICommandeService;
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

	@ManagedProperty(value = "#{clientService}")
	private IClientService clientService;

	@ManagedProperty(value = "#{commService}")
	private ICommandeService comService;

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

	public void setClientService(IClientService clientService) {
		this.clientService = clientService;
	}

	public void setComService(ICommandeService comService) {
		this.comService = comService;
	}

	// methodes
	public String ajouterLigne() {

		// récupérer la commande par défaut
		Commande comDefaut = new Commande();
		comDefaut.setIdCommande(1);
		comDefaut = comService.getCommandeById(comDefaut);

		// si le produit n'est pas sélectionné, ajout d'une ligne
		if (!this.produit.isSelectionne()) {
			this.ligne = ligneService.addLigne(this.ligne, comDefaut, this.produit);
		} else {
			// sinon, récupération de la quantité initiale sur la ligne
			// correspondant à ce produit
			this.ligne = ligneService.getLigneByIdProduit(this.produit);
			int quantite = this.ligne.getQuantite();

			// ajout d'un produit à cette quantité
			this.ligne.setQuantite(quantite + 1);

			// calcul des nouveaux prix totaux pour cette ligne
			double prix = (this.ligne.getPrix() / quantite) * (quantite + 1);
			this.ligne.setPrix(prix);
			double prixAvantRemise = (this.produit.getPrix() * (quantite + 1));
			this.ligne.setPrixAvantRemise(prixAvantRemise);

			// modification de la ligne dans la base de données
			ligneService.updateLigne(this.ligne, comDefaut, this.produit);
		}

		// mise à jour des prix totaux de la commande

		// récupérer la liste de lignes dont la commande est nulle
		this.listeLignes = ligneService.getAllLignes(comDefaut.getIdCommande());

		// récupération du client par défaut
		Client clientDefaut = new Client();
		clientDefaut.setIdClient(1);

		// mise à jour du prix avant remise
		double prixAvant = comService.getPrixTotalAvantRemise(comDefaut);
		comDefaut.setPrixAvant(prixAvant);

		// mise à jour du prix après remise
		double prixApres = comService.getPrixTotalApresRemise(comDefaut);
		comDefaut.setPrixApres(prixApres);

		// insertion de la date du jour dans la commande
		String date = LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
		comDefaut.setDateCommande(date);
		// mise à jour de la commande dans la BD
		comDefaut = comService.updateCommande(comDefaut, clientDefaut);

		// ajout de la commande dans la session
		maSession.setAttribute("commande", comDefaut);

		// ajouter la liste dans la session
		maSession.setAttribute("lignesListe", this.listeLignes);

		return "panier";
	}

	public String viderPanier() {
		this.listeLignes = ligneService.getAllLignes(1);

		for (LignesCommande element : listeLignes) {
			ligneService.deleteLigne(element);
		}

		// mettre à jour les prix totaux de la commande
		// récupérer la commande par défaut
		Commande comDefaut = new Commande();
		comDefaut.setIdCommande(1);
		comDefaut = comService.getCommandeById(comDefaut);

		comDefaut.setPrixApres(0);
		comDefaut.setPrixAvant(0);

		// récupération du client par défaut
		Client clientDefaut = new Client();
		clientDefaut.setIdClient(1);

		// mise à jour de la commande dans la BD
		comDefaut = comService.updateCommande(comDefaut, clientDefaut);

		// ajout de la commande dans la session
		maSession.setAttribute("commande", comDefaut);

		return "accueil";
	}

	public String voirPanier() {
		this.listeLignes = ligneService.getAllLignes(1);

		// récupérer la commande par défaut
		Commande comDefaut = new Commande();
		comDefaut.setIdCommande(1);
		comDefaut = comService.getCommandeById(comDefaut);

		// ajout de la commande dans la session
		maSession.setAttribute("commande", comDefaut);

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

		// récupérer la commande par défaut
		Commande comDefaut = new Commande();
		comDefaut.setIdCommande(1);
		comDefaut = comService.getCommandeById(comDefaut);

		// récupération du client par défaut
		Client clientDefaut = new Client();
		clientDefaut.setIdClient(1);

		// mise à jour des prix totaux de la commande
		// mise à jour du prix avant remise
		double prixAvant = comService.getPrixTotalAvantRemise(comDefaut);
		comDefaut.setPrixAvant(prixAvant);

		// mise à jour du prix après remise
		double prixApres = comService.getPrixTotalApresRemise(comDefaut);
		comDefaut.setPrixApres(prixApres);

		// mise à jour de la commande dans la BD
		comDefaut = comService.updateCommande(comDefaut, clientDefaut);

		// ajout de la commande dans la session
		maSession.setAttribute("commande", comDefaut);

		// ajouter la liste dans la session
		maSession.setAttribute("lignesListe", this.listeLignes);

		return "panier";
	}

	public String augmenterQuantite() {

		Commande comDefaut = new Commande();
		comDefaut.setIdCommande(1);
		comDefaut = comService.getCommandeById(comDefaut);

		if (this.ligne.getQuantite() < this.ligne.getProduit().getQuantite()) {

			this.ligne.setQuantite(this.ligne.getQuantite() + 1);

			this.ligne = ligneService.updateLigne(this.ligne, comDefaut, this.ligne.getProduit());
		} else {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Stock insuffisant"));
		}

		// récupération du client par défaut
		Client clientDefaut = new Client();
		clientDefaut.setIdClient(1);

		// mise à jour des prix totaux de la commande
		// mise à jour du prix avant remise
		double prixAvant = comService.getPrixTotalAvantRemise(comDefaut);
		comDefaut.setPrixAvant(prixAvant);

		// mise à jour du prix après remise
		double prixApres = comService.getPrixTotalApresRemise(comDefaut);
		comDefaut.setPrixApres(prixApres);

		// mise à jour de la commande dans la BD
		comDefaut = comService.updateCommande(comDefaut, clientDefaut);

		// ajout de la commande dans la session
		maSession.setAttribute("commande", comDefaut);

		return "panier";
	}

	public String diminuerQuantite() {

		Commande comDefaut = new Commande();
		comDefaut.setIdCommande(1);
		comDefaut = comService.getCommandeById(comDefaut);

		if (this.ligne.getQuantite() > 1) {
			this.ligne.setQuantite(this.ligne.getQuantite() - 1);
			this.ligne = ligneService.updateLigne(this.ligne, comDefaut, this.ligne.getProduit());
		} else {
			if (this.ligne.getQuantite() == 1) {
				ligneService.deleteLigne(this.ligne);
			}
		}

		// récupération du client par défaut
		Client clientDefaut = new Client();
		clientDefaut.setIdClient(1);

		// mise à jour des prix totaux de la commande
		// mise à jour du prix avant remise
		double prixAvant = comService.getPrixTotalAvantRemise(comDefaut);
		comDefaut.setPrixAvant(prixAvant);

		// mise à jour du prix après remise
		double prixApres = comService.getPrixTotalApresRemise(comDefaut);
		comDefaut.setPrixApres(prixApres);

		// mise à jour de la commande dans la BD
		comDefaut = comService.updateCommande(comDefaut, clientDefaut);

		// ajout de la commande dans la session
		maSession.setAttribute("commande", comDefaut);

		// récupérer la nouvelle liste de lignes de la BD
		this.listeLignes = ligneService.getAllLignes(1);

		// ajouter la liste dans la session
		maSession.setAttribute("lignesListe", this.listeLignes);

		return "panier";
	}

	
}
