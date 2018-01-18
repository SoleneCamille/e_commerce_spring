package fr.adaming.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fr.adaming.dao.ICommandeDao;
import fr.adaming.dao.ILignesCommandeDao;
import fr.adaming.dao.IProduitDao;
import fr.adaming.model.Commande;
import fr.adaming.model.LignesCommande;
import fr.adaming.model.Produit;

@Service("ligneCommService")
@Transactional // pour rendre les methodes de la classe transactionnelles
public class LignesCommandeServiceImpl implements ILignesCommandeService {

	@Autowired
	private ILignesCommandeDao ligneDao;

	@Autowired
	private IProduitDao produitDao;

	@Autowired
	private ICommandeDao commandeDao;

	@Override
	public List<LignesCommande> getAllLignes(int idCommande) {
		return ligneDao.getAllLignes(idCommande);
	}

	@Override
	public LignesCommande addLigne(LignesCommande ligne, Commande comm, Produit p) {
		Produit pOut = produitDao.getProduitbyIdorName(p);
		pOut.setSelectionne(true);

		ligne.setProduit(pOut);
		Commande cOut = commandeDao.getCommandeById(comm);
		ligne.setCommande(cOut);
		ligne.setQuantite(1);
		double prixTotal = p.getPrix() - (p.getPrix() * (p.getRemise() / 100));
		ligne.setPrix(prixTotal);
		ligne.setPrixAvantRemise(p.getPrix());

		return ligneDao.addLigne(ligne);
	}

	@Override
	public LignesCommande updateLigne(LignesCommande ligne, Commande comm, Produit p) {
		Produit pOut = produitDao.getProduitbyIdorName(p);
		ligne.setProduit(pOut);

		Commande cOut = commandeDao.getCommandeById(comm);
		ligne.setCommande(cOut);

		double prixTotal = ligne.getQuantite() * (p.getPrix() - (p.getPrix() * (p.getRemise() / 100)));
		ligne.setPrix(prixTotal);
		ligne.setPrixAvantRemise(p.getPrix() * ligne.getQuantite());

		return ligneDao.updateLigne(ligne);
	}

	@Override
	public int deleteLigne(LignesCommande ligne) {
		Produit pOut = ligne.getProduit();
		pOut.setSelectionne(false);
		produitDao.updateProduit(pOut);
		return ligneDao.deleteLigne(ligne.getIdLigne());
	}

	@Override
	public LignesCommande getLigneById(LignesCommande ligne) {
		return ligneDao.getLigneById(ligne);
	}

	@Override
	public LignesCommande getLigneByIdProduit(Produit p) {
		return ligneDao.getLigneByIdProduit(p);
	}

}
