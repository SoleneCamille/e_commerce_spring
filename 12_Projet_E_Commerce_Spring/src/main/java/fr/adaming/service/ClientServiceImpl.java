package fr.adaming.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fr.adaming.dao.IClientDao;
import fr.adaming.model.Client;

@Service("clientService")
@Transactional // pour rendre les methodes de la classe transactionnelles
public class ClientServiceImpl implements IClientService {

	@Autowired
	private IClientDao clientDao;

	@Override
	public List<Client> getAllClients() {
		return clientDao.getAllClients();
	}

	@Override
	public Client addClient(Client client) {
		return clientDao.addClient(client);
	}

	@Override
	public Client updateClient(Client client) {
		Client clientFind = clientDao.getClientById(client);

		if (clientFind != null) {
			return clientDao.updateClient(client);
		} else {
			return null;
		}
	}

	@Override
	public int deleteClient(int idClient) {
		return clientDao.deleteClient(idClient);
	}

	@Override
	public Client isExist(Client client) {
		return clientDao.isExist(client);
	}

	@Override
	public Client getClientById(Client client) {
		return clientDao.getClientById(client);
	}

}
