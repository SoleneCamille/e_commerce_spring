package fr.adaming.managedBeans;

import java.io.FileNotFoundException;
//import java.io.FileOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
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
import javax.faces.bean.ViewScoped;
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

import com.sun.mail.smtp.SMTPTransport;

import org.apache.commons.codec.binary.Base64;


import com.lowagie.text.DocumentException;


import fr.adaming.model.Categorie;
import fr.adaming.model.Client;
import fr.adaming.model.Commande;
import fr.adaming.model.LignesCommande;
import fr.adaming.model.Produit;
import fr.adaming.service.ICategorieService;
import fr.adaming.service.IClientService;
import fr.adaming.service.ICommandeService;
import fr.adaming.service.ILignesCommandeService;
import fr.adaming.service.IProduitService;

@ManagedBean(name = "clientMB")
@ViewScoped
public class ClientManagedBean implements Serializable {

	// transformation de l'association uml en java
	@ManagedProperty(value = "#{clientService}")
	private IClientService clientService;
	@ManagedProperty(value = "#{catService}")
	private ICategorieService catService;

	@ManagedProperty(value = "#{pdtService}")
	private IProduitService prodService;

	@ManagedProperty(value = "#{commService}")
	private ICommandeService comService;

	@ManagedProperty(value = "#{ligneCommService}")
	private ILignesCommandeService ligneService;

	private Client client;
	private List<Categorie> listeCategories;
	private Categorie categorie;
	private HttpSession maSession;
	private List<Produit> listeProduit;
	private List<LignesCommande> listeLignes;
	private Commande commande;

	// constructeur vide
	public ClientManagedBean() {
		this.client = new Client();
		this.categorie = new Categorie();
		this.commande = new Commande();

	}

	@PostConstruct
	public void init() {
		this.maSession = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
	}

	// getters et setters
	public Client getClient() {
		return client;
	}

	public void setClient(Client client) {
		this.client = client;
	}

	public List<Categorie> getListeCategories() {
		return listeCategories;
	}

	public void setListeCategories(List<Categorie> listeCategories) {
		this.listeCategories = listeCategories;
	}

	public void setCatService(ICategorieService catService) {
		this.catService = catService;
	}

	public Categorie getCategorie() {
		return categorie;
	}

	public void setCategorie(Categorie categorie) {
		this.categorie = categorie;
	}

	public HttpSession getMaSession() {
		return maSession;
	}

	public void setMaSession(HttpSession maSession) {
		this.maSession = maSession;
	}

	public List<Produit> getListeProduit() {
		return listeProduit;
	}

	public void setListeProduit(List<Produit> listeProduit) {
		this.listeProduit = listeProduit;
	}

	public Commande getCommande() {
		return commande;
	}

	public void setCommande(Commande commande) {
		this.commande = commande;
	}

	public ICommandeService getComService() {
		return comService;
	}

	public void setComService(ICommandeService comService) {
		this.comService = comService;
	}

	public List<LignesCommande> getListeLignes() {
		return listeLignes;
	}

	public void setListeLignes(List<LignesCommande> listeLignes) {
		this.listeLignes = listeLignes;
	}

	public void setClientService(IClientService clientService) {
		this.clientService = clientService;
	}

	public void setProdService(IProduitService prodService) {
		this.prodService = prodService;
	}

	public void setLigneService(ILignesCommandeService ligneService) {
		this.ligneService = ligneService;
	}

	// methodes
	public String consulterCategorie() {
		List<Produit> liste = prodService.getProduitByCat(this.categorie);
		this.listeProduit = new ArrayList<Produit>();

		if (liste != null) {
			for (Produit element : liste) {
				if (element.getPhoto() == null) {
					element.setImage(null);
				} else {
					element.setImage("data:image/png;base64," + Base64.encodeBase64String(element.getPhoto()));
				}

				this.listeProduit.add(element);
			}

			// ajout de la liste de produits dans la session
			maSession.setAttribute("listeProduits", this.listeProduit);

		} else {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Pas de produit dans cette catégorie"));

		}

		return "afficheProduitsClient";

	}

	public String ajouterClient() {
		Client cOut = clientService.addClient(this.client);

		Commande comDefaut = new Commande();
		comDefaut.setIdCommande(1);
		comDefaut = comService.getCommandeById(comDefaut);

		// récupérer la liste de lignes de sa commande
		List<LignesCommande> listOut = ligneService.getAllLignes(1);

		Commande commOut = new Commande(comDefaut.getPrixAvant(), comDefaut.getPrixApres());
		comDefaut.setListeLigneCommande(null);

		comDefaut.setPrixApres(0);
		comDefaut.setPrixAvant(0);
		comService.updateCommande(comDefaut, comDefaut.getClient());

		// créer une commande avec ces lignes de commande
		commOut = comService.addCommande(commOut, cOut);

		commOut.setListeLigneCommande(listOut);

		for (LignesCommande element : listOut) {
			ligneService.updateLigne(element, commOut, element.getProduit());
			Produit p = element.getProduit();
			p.setSelectionne(false);
			p.setQuantite(p.getQuantite() - element.getQuantite());
			prodService.updateProduit(p, p.getCategorie());
		}

		maSession.setAttribute("comListe", commOut);
		return "recapCommandes";
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
		this.client=(Client) maSession.getAttribute("client");
		System.out.println(this.client.getEmail());
		
		Properties props = System.getProperties();
		props.put("mail.smtps.host", "smtp.gmail.com");
		props.put("mail.smtps.auth", "true");
		Session session = Session.getInstance(props, null);
		Message msg = new MimeMessage(session);
		msg.setFrom(new InternetAddress("application.j2ee@gmail.com"));
		;
		msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(this.client.getEmail(), false));
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

	public String seConnecter2() {
		try {

			// récupération du client
			Client cOut = clientService.isExist(this.client);

			// récupération des lignes de commande de ce client
			List<Commande> listeCom = comService.getAllCommandesFromClient(cOut);
			maSession.setAttribute("comListe", listeCom);
			maSession.setAttribute("client", cOut);

			return "recapCommandes";

		} catch (Exception e) {
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage("L'identifiant ou le mot de passe est incorrect"));

		}
		return "loginClient";
	}

	public String seConnecter() {

		try {

			Client cOut = clientService.isExist(this.client);

			// récupérer la liste de lignes de sa commande
			this.listeLignes = ligneService.getAllLignes(1);

			Commande comDefaut = new Commande();
			comDefaut.setIdCommande(1);
			comDefaut = comService.getCommandeById(comDefaut);

			Commande commOut = new Commande(comDefaut.getPrixAvant(), comDefaut.getPrixApres());
			comDefaut.setListeLigneCommande(null);

			comDefaut.setPrixApres(0);
			comDefaut.setPrixAvant(0);
			comService.updateCommande(comDefaut, comDefaut.getClient());

			// créer une commande avec ces lignes de commande
			commOut = comService.addCommande(commOut, cOut);

			commOut.setListeLigneCommande(this.listeLignes);

			for (LignesCommande element : this.listeLignes) {
				ligneService.updateLigne(element, commOut, element.getProduit());
				Produit p = element.getProduit();
				p.setSelectionne(false);
				p.setQuantite(p.getQuantite() - element.getQuantite());
				prodService.updateProduit(p, p.getCategorie());
			}

			List<Commande> listeCom = comService.getAllCommandesFromClient(cOut);
			maSession.setAttribute("comListe", listeCom);
			maSession.setAttribute("client", cOut);

			return "recapCommandes";

		} catch (Exception e) {
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage("L'identifiant ou le mot de passe est incorrect"));

		}
		return "validationCommande";
	}

	public String afficherDetail() {
		this.listeLignes = ligneService.getAllLignes(this.commande.getIdCommande());

		maSession.setAttribute("lignesList", this.listeLignes);
		maSession.setAttribute("commande", this.commande);

		return "detailCommande";
	}

	public String seDeconnecter() {	
		return "accueil";
	}
	
	public void rechercher() {
		System.out.println("----------------------------------------- Coucou");
	}

}
