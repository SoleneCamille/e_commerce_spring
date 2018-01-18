package fr.adaming.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fr.adaming.dao.ICategorieDao;
import fr.adaming.dao.IProduitDao;
import fr.adaming.model.Categorie;
import fr.adaming.model.Produit;

@Service("pdtService")
@Transactional // pour rendre les methodes de la classe transactionnelles
public class ProduitServiceImpl implements IProduitService {

	@Autowired
	private IProduitDao produitDao;

	@Autowired
	private ICategorieDao catDao;

	// methodes

	public void setCatDao(ICategorieDao catDao) {
		this.catDao = catDao;
	}
	
	

	public void setProduitDao(IProduitDao produitDao) {
		this.produitDao = produitDao;
	}



	@Override
	public Produit addProduit(Produit p, Categorie c) {

		Categorie cOut = catDao.getCategorieByIdOrName(c);
		p.setCategorie(cOut);
		return produitDao.addProduit(p);
	}

	@Override
	public int deleteProduit(int id) {
		return produitDao.deleteProduit(id);
	}

	@Override
	public Produit updateProduit(Produit p, Categorie c) {
		Categorie cOut = catDao.getCategorieByIdOrName(c);
		p.setCategorie(cOut);
		return produitDao.updateProduit(p);
	}

	@Override
	public Produit getProduitbyIdorName(Produit p) {
		return produitDao.getProduitbyIdorName(p);
	}

	@Override
	public Produit getProduitByName(String name) {
		return produitDao.getProduitByName(name);
	}

	@Override
	public List<Produit> getProduitByCat(Categorie c) {
		return produitDao.getProduitByCat(c);
	}

}
