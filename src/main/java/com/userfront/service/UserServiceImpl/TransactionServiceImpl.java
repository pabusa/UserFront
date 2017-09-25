package com.userfront.service.UserServiceImpl;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.userfront.dao.PrimaryAccountDao;
import com.userfront.dao.PrimaryTransactionDao;
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

	@Override
	public List<Recipient> findRecipientList(Principal principal) {
		// TODO Auto-generated method stub
		return null;
	}
}
