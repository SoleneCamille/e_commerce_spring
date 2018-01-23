package fr.adaming.service;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fr.adaming.dao.IClientDao;
import fr.adaming.dao.ICommandeDao;
import fr.adaming.model.Client;
import fr.adaming.model.Commande;

@Service("commService")
@Transactional // pour rendre les methodes de la classe transactionnelles
public class CommandeServiceImpl implements ICommandeService {

	@Autowired
	private ICommandeDao commandeDao;

	@Autowired
	private IClientDao clientDao;

	@Override
	public List<Commande> getAllCommandes() {
		return commandeDao.getAllCommandes();
	}

	@Override
	public List<Commande> getAllCommandesFromClient(Client client) {
		return commandeDao.getAllCommandesFromClient(client);
	}

	@Override
	public Commande addCommande(Commande comm, Client client) {
		Client cOut = clientDao.getClientById(client);
		comm.setClient(cOut);
		String date = LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
		comm.setDateCommande(date);
		return commandeDao.addCommande(comm);
	}

	@Override
	public Commande updateCommande(Commande comm, Client client) {
		Client cOut = clientDao.getClientById(client);
		comm.setClient(cOut);
		//String date = LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
		Date actuelle = new Date();
		 DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		 String dat = dateFormat.format(actuelle);
		comm.setDateCommande(dat);
		return commandeDao.updateCommande(comm);
	}

	@Override
	public int deleteCommande(int idCommande) {
		return commandeDao.deleteCommande(idCommande);
	}

	@Override
	public Commande getCommandeById(Commande comm) {
		return commandeDao.getCommandeById(comm);
	}

	@Override
	public double getPrixTotalAvantRemise(Commande com) {

		return commandeDao.getPrixTotalAvantRemise(com);
	}

	@Override
	public double getPrixTotalApresRemise(Commande com) {

		return commandeDao.getPrixTotalApresRemise(com);
	}

}
