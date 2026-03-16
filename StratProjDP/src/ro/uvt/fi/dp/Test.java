package ro.uvt.fi.dp;

import ro.uvt.fi.dp.account.Account;
import ro.uvt.fi.dp.account.AccountFactory;
import ro.uvt.fi.dp.account.Currency;
import ro.uvt.fi.dp.bank.Bank;
import ro.uvt.fi.dp.client.Client;

public class Test {

	public static void main(String[] args) {
		/**
		 * Create BCR bank with 2 clients
		 */
		Bank bcr = new Bank("BCR Bank");
		// Client Ionescu has an EUR and a RON account
		Client cl1 = new Client.Builder("Ionescu Ion", "Timisoara").build();
		cl1.addAccount(AccountFactory.createAccount(Currency.EUR, "EUR124", 200.9));
		cl1.addAccount(AccountFactory.createAccount(Currency.RON, "RON1234", 400));
		bcr.addClient(cl1);
		// Client Marinescu has a RON account
		Client cl2 = new Client.Builder("Marinescu Marin", "Timisoara").build();
		cl2.addAccount(AccountFactory.createAccount(Currency.RON, "RON126", 100));
		bcr.addClient(cl2);
		System.out.println(bcr);

		/**
		 * Create bank CEC with one client
		 */
		Bank cec = new Bank("CEC Bank");
		Client clientCEC = new Client.Builder("Vasilescu Vasile", "Brasov").build();
		clientCEC.addAccount(AccountFactory.createAccount(Currency.EUR, "EUR128", 700));
		cec.addClient(clientCEC);
		System.out.println(cec);

		/**
		 * Perform operations on client accounts
		 */
		// depose in account RON126 of client Marinescu
		Client cl = bcr.getClient("Marinescu Marin");
		if (cl != null) {
			cl.getAccount("RON126").depose(400);
			System.out.println(cl);
		}

		// retrieve from account RON126 of Marinescu client
		if (cl != null) {
			cl.getAccount("RON126").retrieve(67);
			System.out.println(cl);
		}

		// transfer between accounts RON126 and RON1234
		Account a1 = cl.getAccount("RON126");
		Account a2 = bcr.getClient("Ionescu Ion").getAccount("RON1234");
		a1.transferTo(a2, 40);
		System.out.println(bcr);

		a1.printStatement();
		a2.printStatement();
	}
}
