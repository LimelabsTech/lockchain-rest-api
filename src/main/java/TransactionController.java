package hello;

import org.springframework.web.bind.annotation.RestController;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.EthGetBalance;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.exceptions.TransactionException;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.Transfer;
import org.web3j.utils.Convert;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.math.BigDecimal;
import java.math.BigInteger;


import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;



@RestController
@RequestMapping(method = RequestMethod.POST)
public class HelloController {
	

	public TransactionReceipt sendFundsToAccount(String amount, String recipient)
			throws InterruptedException, TransactionException, Exception {

		// TODO: Need to check if the recipient given here is actualy a user in our
		// database and if this is the user that calls this function

		// TODO: Need to make a config file from which we should get information about
		// the environment and set it to the server (line 47);

		// This JSON String should be stored in the backend an we should read it from
		// there, also the password
		String jsonString = "{\"version\":3,\"id\":\"d44f162d-1f91-4ade-9d5f-414661295df0\",\"address\":\"b63df2068d209f8ff3925c4c9dbbabfd31301825\",\"Crypto\":{\"ciphertext\":\"2d40317fc74b4ea71930a0a6681507addc103e127e65a663cf731537ef79726d\",\"cipherparams\":{\"iv\":\"f90df4df3a67d85e7e34f84a7c0b15fd\"},\"cipher\":\"aes-128-ctr\",\"kdf\":\"scrypt\",\"kdfparams\":{\"dklen\":32,\"salt\":\"473b462c67127e8260fea0ff7fe716d17b6792278160937a29d8875294d0f92a\",\"n\":8192,\"r\":8,\"p\":1},\"mac\":\"b94eada113e11314895cb559bd79e72c0dc27eb15ed4ebb666ffb80977859f13\"}}";
		String password = "123456789";

		// TODO: Need to make a config file from which we should get information about
		// the environment and set it to the server
//		Web3j web3 = Web3j.build(new HttpService()); // defaults to
	 Web3j web3 = Web3j.build(new HttpService("https://ropsten.infura.io/Up5uvBHSCSqtOmnlhL87"));																								// http://localhost:8545/
		
		// Create temp file from the json string to create the credentialls
		File tempWalletFile = File.createTempFile("temp-wallet-file", ".tmp");
		BufferedWriter writer = new BufferedWriter(new FileWriter(tempWalletFile));
		writer.write(jsonString);
		writer.close();
		tempWalletFile.deleteOnExit();

		Credentials credentials = WalletUtils.loadCredentials(password, tempWalletFile);
		TransactionReceipt transactionReceipt = Transfer
				.sendFunds(web3, credentials, recipient, new BigDecimal(amount), Convert.Unit.WEI).send();

		EthGetBalance ethGetBalance = web3.ethGetBalance(recipient, DefaultBlockParameterName.LATEST).sendAsync().get();

		BigInteger wei = ethGetBalance.getBalance();
		return transactionReceipt;
	}
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/sendFundsToAccount", method = RequestMethod.POST)
	public TransactionReceipt sendEther(@RequestBody FundTransactionParams fundParams)
			throws InterruptedException, TransactionException, Exception {
		return sendFundsToAccount(fundParams.amount, fundParams.recipient);

	}

}
