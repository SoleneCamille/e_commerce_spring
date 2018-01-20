package fr.adaming.managedBeans;

import java.io.FileNotFoundException;
import java.io.Serializable;
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
	
	public void envoiMail() throws AddressException, MessagingException, FileNotFoundException, DocumentException {
		// création pdf
		//
		// /* Create a new Document object */
		// Document document = new Document();
		//
		// try {
		// /* Associate the document with a PDF writer and an output stream */
		// PdfWriter.getInstance(document, new
		// FileOutputStream("C:\\Users\\inti-0257\\Desktop\\formation\\Commande.pdf"));
		//
		// /* Open the document (ready to add items) */
		// document.open();
		//
		// /* Populate the document (add items to it) */
		//// Commande comDefaut = new Commande();
		//
		// System.out.println("###################");
		// List<LignesCommande> liste=ligneService.getAllLignes(23);
		//
		// document.add(new Paragraph("Récapitulatif de votre commande"));
		//
		//
		// PdfPTable table = new PdfPTable(5);
		////
		//// //On créer l'objet cellule.
		// PdfPCell cell;
		////
		// cell = new PdfPCell(new Phrase("Liste des produits commandés"));
		// cell.setColspan(5);
		// table.addCell(cell);
		//
		// table.addCell("nom du produit");
		// table.addCell("quantité");
		// table.addCell("prix avant remise");
		// table.addCell("remise");
		// table.addCell("prix après remise");
		//
		//
		// // cell = new PdfPCell(new Phrase("Fusion de 2 cellules de la
		// première colonne"));
		// //cell.setRowspan(3);
		// //table.addCell(cell);
		//
		//// //contenu du tableau.
		// System.out.println("###################");
		//
		// for(int i=0;i<liste.size();i++){
		// //System.out.println(liste.get(0).getProduit().getDesignation());
		// table.addCell(liste.get(i).getProduit().getDesignation());
		// table.addCell(Integer.toString(liste.get(i).getQuantite()));
		// table.addCell(Double.toString(liste.get(i).getPrixAvantRemise()));
		// table.addCell(Double.toString(liste.get(i).getProduit().getRemise()));
		// table.addCell(Double.toString(liste.get(i).getPrix()));}
		//
		// document.add(table);
		//
		//
		//
		// PdfPTable table2 = new PdfPTable(2);
		//
		//
		// cell = new PdfPCell(new Phrase("Montant total"));
		// cell.setColspan(2);
		// table2.addCell(cell);
		//
		// table2.addCell("prix total avant remise");
		// table2.addCell("prix total après remise");
		//
		//// table.addCell(Double.toString(this.commande.getPrixAvant()));
		//// table.addCell(Double.toString(this.commande.getPrixApres()));
		//
		// document.add(table2);
		//
		// System.out.println("pdf cree");
		// }
		// catch(DocumentException e) {
		// /* Oups */
		// System.err.println(e);
		// }
		// finally {
		// /* Don't forget to close the document! */
		// document.close();
		//
		// }
		//

		// Envoi du mail contenant le pdf
		System.out.println("############test mail#############");
		int id=this.ligne.getCommande().getIdCommande();
		System.out.println("test id");
		System.out.println(id);
		System.out.println(this.ligne.getIdLigne());
		//System.out.println(this.listeLignes);
		//System.out.println(this.ligne.getCommande().getClient().getEmail());
			
		Properties props = System.getProperties();
		props.put("mail.smtps.host", "smtp.gmail.com");
		props.put("mail.smtps.auth", "true");
		Session session = Session.getInstance(props, null);
		Message msg = new MimeMessage(session);
		msg.setFrom(new InternetAddress("application.j2ee@gmail.com"));
		;
		msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse("jegonday.solene@gmail.com", false));
		msg.setSubject("winterIsComing " + System.currentTimeMillis());
		msg.setText("Votre commande est validée ");
		msg.setSentDate(new Date());

		Multipart multipart = new MimeMultipart();
		MimeBodyPart messageBodyPart = new MimeBodyPart();
		messageBodyPart.setText("Votre commande:");
		multipart.addBodyPart(messageBodyPart);

		messageBodyPart = new MimeBodyPart();
		DataSource source = new FileDataSource("C:\\Users\\inti-0257\\Downloads\\Commande.pdf");
		messageBodyPart.setDataHandler(new DataHandler(source));
		messageBodyPart.setFileName("commande.pdf");
		multipart.addBodyPart(messageBodyPart);
		msg.setContent(multipart);

		SMTPTransport t = (SMTPTransport) session.getTransport("smtps");
		t.connect("smtp.gmail.com", "application.j2ee@gmail.com", "adamingintijee");
		t.sendMessage(msg, msg.getAllRecipients());
		System.out.println("Mail envoyé");
		t.close();
	}


}
