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

import org.json.JSONObject;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping(method = RequestMethod.POST)
public class TransactionController {

	public TransactionReceipt sendFundsToAccount(String amount, String recipient)
			throws InterruptedException, TransactionException, Exception {

		// TODO: Need to check if the recipient given here is actualy a user in our
		// database and if this is the user that calls this function

		// TODO: Need to make a config file from which we should get information about
		// the environment and set it to the server (line 47);

		// This JSON String should be stored in the backend an we should read it from
		// there, also the password
		String jsonString = "{ \"version\": 3, \"id\": \"1ddf0087-79e0-48d9-a84b-5d364075a804\", \"address\": \"627306090abab3a6e1400e9345bc60c78a8bef57\", \"Crypto\": { \"ciphertext\": \"6d845d338f2cfc526adc5d49b5f95c13690b59ef56c9eb2c79e4ff7740adcc77\", \"cipherparams\": { \"iv\": \"f506358d43a8cd8bea70b34b3bd97190\" }, \"cipher\": \"aes-128-ctr\", \"kdf\": \"scrypt\", \"kdfparams\": { \"dklen\": 32, \"salt\": \"e15ccf22e4e35a5a321885c562e265d9fd7d0fbede7894e9195108bbf40563a2\", \"n\": 1024, \"r\": 8, \"p\": 1 }, \"mac\": \"2295347e6e31672df9e9e651f6d6593d769c848e9ceb2b15dd5e09fd2aaa4992\" } }";
		String password = "123456789";

		// TODO: Need to make a config file from which we should get information about
		// the environment and set it to the server
		Web3j web3 = Web3j.build(new HttpService("https://ropsten.infura.io/Up5uvBHSCSqtOmnlhL87")); // defaults to
																										// http://localhost:8545/

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
		System.out.println(wei);
		return transactionReceipt;
	}

	@RequestMapping("/sendFundsToAccount")
	public TransactionReceipt sendEther(@RequestParam(value = "amount") String amount,
			@RequestParam(value = "recipient") String recipient)
			throws InterruptedException, TransactionException, Exception {
		return sendFundsToAccount(amount, recipient);

	}

}
