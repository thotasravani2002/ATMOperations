package com.codegnan.cards;

import java.util.ArrayList;
import java.util.Collections;

import com.codegnan.customExceptions.InsufficientBalanceException;
import com.codegnan.customExceptions.InsufficientMachineBalanceException;
import com.codegnan.customExceptions.InvalidAmountException;
import com.codegnan.customExceptions.NotAOperatorException;
import com.codegnan.interfaces.IATMService;

public class HDFCDebitCard implements IATMService {
	String name;
	long debitCardNumber;
	double accountBalance;
	int pinNumber;
	ArrayList<String> statement;
	final String type = "user";
	int chances;

	public HDFCDebitCard(long debitCardNumber, String name, double accountBalance, int pinNumber) {
		chances = 3;
		this.name = name;
		this.accountBalance = accountBalance;
		this.pinNumber = pinNumber;
		statement = new ArrayList<>();

	}

	@Override
	public String getUserType() throws NotAOperatorException {
		// TODO Auto-generated method stub
		return type;
	}

	@Override
	public double withdrawAmount(double wthAmount)
			throws InvalidAmountException, InsufficientBalanceException, InsufficientMachineBalanceException {
		if (wthAmount <= 0) {
			throw new InvalidAmountException("you can enter zero(0) amount to wuuthdraw.pls enter valid amount");
		} else if (wthAmount % 100 != 0) {
			throw new InvalidAmountException("please withdraw multiples of 100");
		} else if (wthAmount < 500) {
			throw new InsufficientBalanceException("please withdraw more than 500 rupees");
		} else if (wthAmount > accountBalance) {
			throw new InsufficientBalanceException(
					"you dont have sufficient balance to withdraw ..please check your account balance");
		} else {
			accountBalance = accountBalance - wthAmount;
			statement.add("Debited : " + wthAmount);
			return wthAmount;
		}
	}

	@Override
	public void depositAmount(double deptAmount) throws InvalidAmountException {
		// TODO Auto-generated method stub
		if (deptAmount <= 0 || deptAmount % 100 != 0 || deptAmount < 500) {// logical or = any one is true
			throw new InvalidAmountException("Please deposit multiples of 100 and deposit more than 500");
		} else {
			accountBalance = accountBalance + deptAmount;
			statement.add("credited:" + deptAmount);
		}
	}

	@Override
	public double checkAccountBalance() {
		// TODO Auto-generated method stub
		return accountBalance;
	}

	@Override
	public void changePinNumber(int pinNumber) {
		// TODO Auto-generated method stub
		this.pinNumber = pinNumber;

	}

	@Override
	public int getPinNumber() {
		// TODO Auto-generated method stub
		return pinNumber;
	}

	@Override
	public String getUserName() {
		// TODO Auto-generated method stub
		return name;
	}

	@Override
	public void decreaseChances() {
		// TODO Auto-generated method stub
		--chances;

	}

	@Override
	public int getChances() {
		// TODO Auto-generated method stub
		return chances;
	}

	@Override
	public void resetPinChances() {
		// TODO Auto-generated method stub
		chances = 3;

	}

	@Override
	public void generateMiniStatement() {
		int count = 5;
		if (statement.size() == 0) {
			System.out.println("there are no transactions happened");
			return;
		}
		System.out.println("==========================list of 5 transactions===================== ");
		Collections.reverse(statement);
		for (String trans : statement) {
			if (count == 0) {
				break;
			}
			System.out.println(trans);
			count--;
		}
		Collections.reverse(statement);

	}

}

