package fr.adaming.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fr.adaming.dao.ICategorieDao;
import fr.adaming.model.Categorie;

@Service("catService")
@Transactional // pour rendre les methodes de la classe transactionnelles
public class CategorieServiceImpl implements ICategorieService {

	@Autowired
	private ICategorieDao categorieDao;

	@Override
	public List<Categorie> getAllCategories() {
		return categorieDao.getAllCategories();
	}

	@Override
	public Categorie addCategorie(Categorie cat) {
		return categorieDao.addCategorie(cat);
	}

	@Override
	public Categorie updateCategorie(Categorie cat) {
		Categorie catFind = categorieDao.getCategorieByIdOrName(cat);
		if (catFind != null) {
			return categorieDao.updateCategorie(cat);
		} else {
			return null;
		}
	}

	@Override
	public int deleteCategorie(int idCategorie) {
		return categorieDao.deleteCategorie(idCategorie);
	}

	@Override
	public Categorie getCategorieByIdOrName(Categorie cat) {
		return categorieDao.getCategorieByIdOrName(cat);
	}

}
