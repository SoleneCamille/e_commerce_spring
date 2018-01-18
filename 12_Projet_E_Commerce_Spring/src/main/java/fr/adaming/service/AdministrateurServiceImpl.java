package fr.adaming.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fr.adaming.dao.IAdministrateurDao;
import fr.adaming.model.Administrateur;

@Service("adminService")
@Transactional // pour rendre les methodes de la classe transactionnelles
public class AdministrateurServiceImpl implements IAdministrateurService {

	@Autowired
	private IAdministrateurDao adminDao;

	@Override
	public Administrateur isExist(Administrateur a) throws Exception {
		// appel de la méthode DAO
		return adminDao.isExist(a);
	}

}
