package com.userfront.service.UserServiceImpl;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.userfront.dao.PrimaryAccountDao;
import com.userfront.dao.PrimaryTransactionDao;
import com.userfront.dao.RecipientDao;
import com.userfront.dao.SavingsAccountDao;
import com.userfront.dao.SavingsTransactionDao;
import com.userfront.domain.PrimaryAccount;
import com.userfront.domain.PrimaryTransaction;
import com.userfront.domain.Recipient;
import com.userfront.domain.SavingsAccount;
import com.userfront.domain.SavingsTransaction;
import com.userfront.domain.User;
import com.userfront.service.TransactionService;
import com.userfront.service.UserService;

@Service
public class TransactionServiceImpl implements TransactionService {
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private PrimaryAccountDao primaryAccountDao;
	
	@Autowired
	private SavingsAccountDao savingsAccountDao;
	
	@Autowired
	private PrimaryTransactionDao primaryTransactionDao;
	
	@Autowired
	private SavingsTransactionDao savingsTransactionDao;
	
	@Autowired
	private RecipientDao recipientDao;
	
	public List<PrimaryTransaction> findPrimaryTransactionList(String username){
		User user = userService.findByUsername(username); 
		List<PrimaryTransaction> primaryTransactionList = user.getPrimaryAccount().getPrimaryTransactionList();
		
		return primaryTransactionList;
	}
	
	public List<SavingsTransaction> findSavingsTransactionList(String username){
		User user = userService.findByUsername(username); 
		List<SavingsTransaction> savingsTransactionList = user.getSavingsAccount().getSavingsTransactionList();
		
		return savingsTransactionList;
	}
	
	public void savePrimaryDepositTransaction(PrimaryTransaction primaryTransaction){
		primaryTransactionDao.save(primaryTransaction);
	}
	
	public void saveSavingsDepositTransaction(SavingsTransaction savingsTransaction){
		savingsTransactionDao.save(savingsTransaction);
	}
	
	public void savePrimaryWithdrawTransaction(PrimaryTransaction primaryTransaction){
		primaryTransactionDao.save(primaryTransaction);
	}
	
	public void saveSavingsWithdrawTransaction(SavingsTransaction savingsTransaction){
		savingsTransactionDao.save(savingsTransaction);
	}

	public void betweenAccountsTransfer(String transferFrom, String transferTo, String transferAmount,
			PrimaryAccount primaryAccount, SavingsAccount savingsAccount) throws Exception {
		
		BigDecimal amount = new BigDecimal(transferAmount);
		Date date = new Date();
		
		if (transferFrom.equalsIgnoreCase("Primary") && transferTo.equalsIgnoreCase("Savings")){
			primaryAccount.setAccountBalance(primaryAccount.getAccountBalance().subtract(amount));
			savingsAccount.setAccountBalance(savingsAccount.getAccountBalance().add(amount));
			
			primaryAccountDao.save(primaryAccount);
			savingsAccountDao.save(savingsAccount);
			
			PrimaryTransaction primaryTransaction = new PrimaryTransaction(
				date, 
				"To savings", 
				"Transfer",
				"Finished",
				Double.parseDouble(transferAmount),
				primaryAccount.getAccountBalance(),
				primaryAccount
			);
			
			primaryTransactionDao.save(primaryTransaction);
			
		} else if (transferFrom.equalsIgnoreCase("Savings") && transferTo.equalsIgnoreCase("Primary")){
			primaryAccount.setAccountBalance(primaryAccount.getAccountBalance().add(amount));
			savingsAccount.setAccountBalance(savingsAccount.getAccountBalance().subtract(amount));
			
			primaryAccountDao.save(primaryAccount);
			savingsAccountDao.save(savingsAccount);
			
			SavingsTransaction savingsTransaction = new SavingsTransaction(
				date, 
				"To primary", 
				"Account",
				"Finished",
				Double.parseDouble(transferAmount),
				savingsAccount.getAccountBalance(),
				savingsAccount
			);
			savingsTransactionDao.save(savingsTransaction);
		} else{
			throw new Exception ("Invalid transfer");
		}
		
	}

	public List<Recipient> findRecipientList(Principal principal) {
		String username = principal.getName();
		
		List<Recipient> recipientList = recipientDao.findAll().stream() // convert list to stream
				.filter(recipient -> username.equals(recipient.getUser().getUsername())) // filters the line,
				.collect(Collectors.toList());
		
		return recipientList;
	}
	
	public Recipient saveRecipient(Recipient recipient) {
		return recipientDao.save(recipient);
	}
	
	public Recipient findRecipientByName (String recipientName) {
		return recipientDao.findByName(recipientName);
	}
	
	public void deleteRecipientByName (String recipientName) {
		recipientDao.deleteByName(recipientName);
	}
	
	public void toSomeoneElseTransfer(Recipient recipient, String accountType, String transferAmount, PrimaryAccount primaryAccount, SavingsAccount savingsAccount){
		Date date = new Date();
		BigDecimal amount = new BigDecimal(transferAmount);
		
		if (accountType.equalsIgnoreCase("Primary")){
			primaryAccount.setAccountBalance(primaryAccount.getAccountBalance().subtract(amount));
			primaryAccountDao.save(primaryAccount);
			
			PrimaryTransaction primaryTransaction = new PrimaryTransaction(date, "Transfer to recipient "+recipient.getName(), "Transfer","Finished",Double.parseDouble(transferAmount),primaryAccount.getAccountBalance(), primaryAccount);
			
			primaryTransactionDao.save(primaryTransaction);
		} else if (accountType.equalsIgnoreCase("Savings")) {
			savingsAccount.setAccountBalance(savingsAccount.getAccountBalance().subtract(amount));
			savingsAccountDao.save(savingsAccount);
			
			SavingsTransaction savingsTransaction = new SavingsTransaction(date, "Transfer to recipient "+recipient.getName(), "Transfer","Finished",Double.parseDouble(transferAmount),savingsAccount.getAccountBalance(), savingsAccount);
			
			savingsTransactionDao.save(savingsTransaction);
		}
	}
}
