package fr.adaming.managedBeans;

import java.io.FileNotFoundException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
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

	static public double arrondir(double value, int n) { 
		double r = (Math.round(value * Math.pow(10, n))) / (Math.pow(10, n)); 
		return r; 
		} 
	
	// methodes
	public String ajouterLigne() {

		
		
		// r�cup�rer la commande par d�faut
		Commande comDefaut = new Commande();
		comDefaut.setIdCommande(1);
		comDefaut = comService.getCommandeById(comDefaut);

		// si le produit n'est pas s�lectionn�, ajout d'une ligne
		if (!this.produit.isSelectionne()) {
			this.ligne = ligneService.addLigne(this.ligne, comDefaut, this.produit);
		} else {
			// sinon, r�cup�ration de la quantit� initiale sur la ligne
			// correspondant � ce produit
			this.ligne = ligneService.getLigneByIdProduit(this.produit);
			int quantite = this.ligne.getQuantite();

			// ajout d'un produit � cette quantit�
			this.ligne.setQuantite(quantite + 1);

			// calcul des nouveaux prix totaux pour cette ligne

			double prix = (this.ligne.getPrix() / quantite) * (quantite + 1);
			
			this.ligne.setPrix(arrondir(prix,2));
			double prixAvantRemise = (this.produit.getPrix() * (quantite + 1));
			this.ligne.setPrixAvantRemise(arrondir(prixAvantRemise,2));

			// modification de la ligne dans la base de donn�es
			ligneService.updateLigne(this.ligne, comDefaut, this.produit);
		}

		// mise � jour des prix totaux de la commande

		// r�cup�rer la liste de lignes dont la commande est nulle
		this.listeLignes = ligneService.getAllLignes(comDefaut.getIdCommande());

		// r�cup�ration du client par d�faut
		Client clientDefaut = new Client();
		clientDefaut.setIdClient(1);

		// mise � jour du prix avant remise
		double prixAvant = comService.getPrixTotalAvantRemise(comDefaut);
		comDefaut.setPrixAvant(arrondir(prixAvant,2));

		// mise � jour du prix apr�s remise
		double prixApres = comService.getPrixTotalApresRemise(comDefaut);
		comDefaut.setPrixApres(arrondir(prixApres,2));

		// mise � jour de la commande dans la BD
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

		// mettre � jour les prix totaux de la commande
		// r�cup�rer la commande par d�faut
		Commande comDefaut = new Commande();
		comDefaut.setIdCommande(1);
		comDefaut = comService.getCommandeById(comDefaut);

		comDefaut.setPrixApres(0);
		comDefaut.setPrixAvant(0);

		// r�cup�ration du client par d�faut
		Client clientDefaut = new Client();
		clientDefaut.setIdClient(1);

		// mise � jour de la commande dans la BD
		comDefaut = comService.updateCommande(comDefaut, clientDefaut);

		// ajout de la commande dans la session
		maSession.setAttribute("commande", comDefaut);

		return "accueil";
	}

	public String voirPanier() {
		this.listeLignes = ligneService.getAllLignes(1);

		// r�cup�rer la commande par d�faut
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

		// r�cup�rer la nouvelle liste de lignes de la BD
		this.listeLignes = ligneService.getAllLignes(1);

		// r�cup�rer la commande par d�faut
		Commande comDefaut = new Commande();
		comDefaut.setIdCommande(1);
		comDefaut = comService.getCommandeById(comDefaut);

		// r�cup�ration du client par d�faut
		Client clientDefaut = new Client();
		clientDefaut.setIdClient(1);

		// mise � jour des prix totaux de la commande
		// mise � jour du prix avant remise
		double prixAvant = comService.getPrixTotalAvantRemise(comDefaut);
		comDefaut.setPrixAvant(prixAvant);

		// mise � jour du prix apr�s remise
		double prixApres = comService.getPrixTotalApresRemise(comDefaut);
		comDefaut.setPrixApres(prixApres);

		// mise � jour de la commande dans la BD
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

		// r�cup�ration du client par d�faut
		Client clientDefaut = new Client();
		clientDefaut.setIdClient(1);

		// mise � jour des prix totaux de la commande
		// mise � jour du prix avant remise
		double prixAvant = comService.getPrixTotalAvantRemise(comDefaut);
		comDefaut.setPrixAvant(prixAvant);

		// mise � jour du prix apr�s remise
		double prixApres = comService.getPrixTotalApresRemise(comDefaut);
		comDefaut.setPrixApres(prixApres);

		// mise � jour de la commande dans la BD
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

		// r�cup�ration du client par d�faut
		Client clientDefaut = new Client();
		clientDefaut.setIdClient(1);

		// mise � jour des prix totaux de la commande
		// mise � jour du prix avant remise
		double prixAvant = comService.getPrixTotalAvantRemise(comDefaut);
		comDefaut.setPrixAvant(prixAvant);

		// mise � jour du prix apr�s remise
		double prixApres = comService.getPrixTotalApresRemise(comDefaut);
		comDefaut.setPrixApres(prixApres);

		// mise � jour de la commande dans la BD
		comDefaut = comService.updateCommande(comDefaut, clientDefaut);

		// ajout de la commande dans la session
		maSession.setAttribute("commande", comDefaut);

		// r�cup�rer la nouvelle liste de lignes de la BD
		this.listeLignes = ligneService.getAllLignes(1);

		// ajouter la liste dans la session
		maSession.setAttribute("lignesListe", this.listeLignes);

		return "panier";
	}

}
