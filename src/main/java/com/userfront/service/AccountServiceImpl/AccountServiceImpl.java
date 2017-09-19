package com.userfront.service.AccountServiceImpl;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.userfront.dao.PrimaryAccountDao;
import com.userfront.dao.SavingsAccountDao;
import com.userfront.domain.PrimaryAccount;
import com.userfront.domain.PrimaryTransaction;
import com.userfront.domain.SavingsAccount;
import com.userfront.domain.SavingsTransaction;
import com.userfront.domain.User;
import com.userfront.service.AccountService;
import com.userfront.service.UserService;

@Service
@Transactional
public class AccountServiceImpl implements AccountService {
	private static final Logger LOG = LoggerFactory.getLogger(AccountService.class);

	private static int nextAccountNumber = 12211356;
	
	@Autowired
	private PrimaryAccountDao primaryAccountDao;
	
	@Autowired
	private SavingsAccountDao savingsAccountDao;
	
	@Autowired
	private UserService userService;
	
	private int accountGen(){
		LOG.info("nextAccountNumber: {}." + nextAccountNumber);
		return ++nextAccountNumber;
	}
	
	public PrimaryAccount createPrimaryAccount(){
		PrimaryAccount primaryAccount = new PrimaryAccount();
		
		primaryAccount.setAccountBalance(new BigDecimal(0.0));
		primaryAccount.setAccountNumber(accountGen());
		
		primaryAccountDao.save(primaryAccount);
		
		return primaryAccountDao.findByAccountNumber(primaryAccount.getAccountNumber());
	}
	
	public SavingsAccount createSavingsAccount(){
		SavingsAccount savingsAccount = new SavingsAccount();
		
		savingsAccount.setAccountBalance(new BigDecimal(0.0));
		savingsAccount.setAccountNumber(accountGen());
		
		savingsAccountDao.save(savingsAccount);
		
		return savingsAccountDao.findByAccountNumber(savingsAccount.getAccountNumber());
	}
	
	public void deposit(String accountType, double amount, Principal principal){
		User user = userService.findByUsername(principal.getName());
		
		Date date = new Date();
		
		if (accountType.equalsIgnoreCase("Primary")){
			PrimaryAccount primaryAccount = user.getPrimaryAccount();
			primaryAccount.setAccountBalance(primaryAccount.getAccountBalance().add(new BigDecimal(amount)));
			primaryAccountDao.save(primaryAccount);
			
			PrimaryTransaction primaryTransaction = new PrimaryTransaction(date, "Deposit to Primary Account", "account","finished", amount, primaryAccount.getAccountBalance(),primaryAccount);
			// Falta persistirlo en la base de datos
			
		} else if (accountType.equalsIgnoreCase("Savings")){
			SavingsAccount savingsAccount = user.getSavingsAccount();
			savingsAccount.setAccountBalance(savingsAccount.getAccountBalance().add(new BigDecimal(amount)));
			savingsAccountDao.save(savingsAccount);
			
			SavingsTransaction savingsTransaction = new SavingsTransaction(date, "Deposit to Savings Account", "account","finished", amount, savingsAccount.getAccountBalance(), savingsAccount);
			
		}
		
	}

}
