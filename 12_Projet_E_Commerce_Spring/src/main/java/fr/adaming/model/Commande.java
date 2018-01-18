package fr.adaming.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "commandes")
public class Commande implements Serializable {

	// attributs
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_comm")
	private int idCommande;
	private Date dateCommande;
	private double prixAvant;
	private double prixApres;
	// transformation UML en java
	@OneToMany(mappedBy = "commande", cascade = CascadeType.ALL)
	private List<LignesCommande> listeLigneCommande;
	@ManyToOne
	@JoinColumn(name = "client_id", referencedColumnName = "id_client")
	private Client client;

	// constructeurs

	public Commande() {
		super();
	}

	public Commande(Date dateCommande, double prixAvant, double prixApres) {
		super();
		this.dateCommande = dateCommande;
		this.prixAvant = prixAvant;
		this.prixApres = prixApres;
	}
	
	
	public Commande(double prixAvant, double prixApres) {
		super();
		this.prixAvant = prixAvant;
		this.prixApres = prixApres;
	}

	public Commande(int idCommande, Date dateCommande, double prixAvant, double prixApres) {
		super();
		this.idCommande = idCommande;
		this.dateCommande = dateCommande;
		this.prixAvant = prixAvant;
		this.prixApres = prixApres;
	}

	// getters & setters

	public int getIdCommande() {
		return idCommande;
	}

	public void setIdCommande(int idCommande) {
		this.idCommande = idCommande;
	}

	public Date getDateCommande() {
		return dateCommande;
	}

	public void setDateCommande(Date dateCommande) {
		this.dateCommande = dateCommande;
	}

	public List<LignesCommande> getListeLigneCommande() {
		return listeLigneCommande;
	}

	public void setListeLigneCommande(List<LignesCommande> listeLigneCommande) {
		this.listeLigneCommande = listeLigneCommande;
	}

	public Client getClient() {
		return client;
	}

	public void setClient(Client client) {
		this.client = client;
	}

	public double getPrixAvant() {
		return prixAvant;
	}

	public void setPrixAvant(double prixAvant) {
		this.prixAvant = prixAvant;
	}

	public double getPrixApres() {
		return prixApres;
	}

	public void setPrixApres(double prixApres) {
		this.prixApres = prixApres;
	}

}
