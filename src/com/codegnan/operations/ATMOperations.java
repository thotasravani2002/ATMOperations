package com.codegnan.operations;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

import com.codegnan.cards.AxisDebitCard;
import com.codegnan.cards.HDFCDebitCard;
import com.codegnan.cards.OperatorCard;
import com.codegnan.cards.SBIDebitCard;
import com.codegnan.customExceptions.IncorrectPinLimitReachedException;
import com.codegnan.customExceptions.InsufficientBalanceException;
import com.codegnan.customExceptions.InsufficientMachineBalanceException;
import com.codegnan.customExceptions.InvalidAmountException;
import com.codegnan.customExceptions.InvalidCardException;
import com.codegnan.customExceptions.InvalidPinException;
import com.codegnan.customExceptions.NotAOperatorException;
import com.codegnan.interfaces.IATMService;

public class ATMOperations {

	// initial ATM machine balance
	public static double ATM_MACHINE_BALANCE = 1000000.0;

	// loop to keep the track of all activities performed on the machine
	public static ArrayList<String> ACTIVITY = new ArrayList<>();

	// Database to map card numbers to card objects.
	public static HashMap<Long, IATMService> dataBase = new HashMap<>();

	// Flag to indicate ATM machine is on or off
	public static boolean MACHINE_ON = true;

	// reference the current card in use
	public static IATMService card;

	// validate the inserted card by checking against the database
	public static IATMService validateCard(long cardNumber) throws InvalidCardException {
		if (dataBase.containsKey(cardNumber)) {
			return dataBase.get(cardNumber);
		} else {
			ACTIVITY.add("Accessed by : " + cardNumber + " is not compatiable");
			throw new InvalidCardException("this is not a valid card");
		}

	}

	// display the activities performed on the ATM machine
	public static void checkATMMachineActivities() {
		System.out.println("=============Activities performed ===============");
		for (String activity : ACTIVITY) {
			System.out.println("========================================================");
			System.out.println(activity);
			System.out.println("========================================================");
		}
	}

	// reset the number of pin attempts for a user
	public static void resetUserAttempts(IATMService operatorCard) {
		IATMService card = null;
		long number;
		Scanner scanner = new Scanner(System.in);
		System.out.println("Enter your card number :");
		number = scanner.nextLong();
		try {
			card = validateCard(number);
			card.resetPinChances();
			ACTIVITY.add("Accessed by : " + operatorCard.getUserName() + ": to resset the number of chances");

		} catch (InvalidCardException ive) {
			System.out.println(ive.getMessage());
		}

	}

	public static IATMService validCredentials(long cardNumber, int pinNumber)
			throws InvalidCardException, InvalidPinException, IncorrectPinLimitReachedException {
		if (dataBase.containsKey(cardNumber)) {
			card = dataBase.get(cardNumber);
		} else {
			throw new InvalidCardException("this is not a valid card");
		}
		try {
			if (card.getUserType().equals("operator")) {
				if (card.getPinNumber() != pinNumber) {
					throw new InvalidPinException("Dear operator,please enter correct pin number");
				}
			} else {
				return card;
			}
		} catch (NotAOperatorException noe) {
			noe.printStackTrace();
		}
		// validate pin and handle incorrect attempts
		if (card.getChances() >= 3) {
			throw new IncorrectPinLimitReachedException("you have reached wrong limit of pin number which is 3  attempts ");
		}
		if (card.getPinNumber() != pinNumber) {
			card.decreaseChances();// decrease the no of chances and tells the remaining chances
			throw new InvalidPinException("you have entered a wrong PIN number");
		} else {
			return card;
		}
	}
	// validate the amount for withdrawal to ensure sufficient machine balance

	public static void validateAmount(double amount) throws InsufficientMachineBalanceException {
		if (amount > ATM_MACHINE_BALANCE) {
			throw new InsufficientMachineBalanceException("Insufficient cash in the machine");
		}
	}

	public static void validateDepositeAmount(double amount)
			throws InsufficientMachineBalanceException, InvalidAmountException {
		// ensure deposit multiples of 100
		if (amount % 100 != 0) {
			throw new InvalidAmountException("Please deposite multiples of 100");
		}
		// check if deposit will exceed the machine capacity
		if (amount + ATM_MACHINE_BALANCE > 1200000.0d) {
			ACTIVITY.add("unable to deposite cash in ATM machine....");
			throw new InsufficientMachineBalanceException("you can't deposit cash as limit of atm machine ");
		}
	}

	// operations available on operator mode
	public static void operatorMode(IATMService card) {
		Scanner scanner = new Scanner(System.in);
		double amount;
		boolean flag = true;
		while (flag) {
			System.out.println("operator mode: operator name: " + card.getUserName());
			System.out.println("====================================================================");
			System.out.println("||            0.Switch off the machine                      ||");
			System.out.println("||            1.To check ATM machine balance                ||");
			System.out.println("||            2.Deposite cash in ATM machine                ||");
			System.out.println("||            3.Reset user pin attempts                     ||");
			System.out.println("||            4.To check activities performed in the machine||");
			System.out.println("||            5.Exit operator mode                          ||");
			System.out.println("Please enter your choice");
			int option = scanner.nextInt();
			switch (option) {
			case 0:
				MACHINE_ON = false;
				ACTIVITY.add(
						"Accessed by : " + card.getUserName() + " Activity performed: switching off the ATM machine");
				flag = false;
				break;
			case 1:
				ACTIVITY.add(
						"Accessed by : " + card.getUserName() + " Activity performed: checking ATM machine balance ");
				System.out.println("the balance of ATM machine is : " + ATM_MACHINE_BALANCE + ": is available");
				break;
			case 2:
				System.out.println("enter the amount to deposite : ");
				amount = scanner.nextDouble();
				try {
					validateDepositeAmount(amount); // validate deposit amount
					ATM_MACHINE_BALANCE += amount;
					ACTIVITY.add("Accessed by : " + card.getUserName()
							+ " Activity performed: deposite cash in the  ATM machine ");
					System.out.println("=====================================================================");
					System.out.println("===================================cash added to the ATM machine ==================================");
					System.out.println("=====================================================================");
				} catch (InvalidAmountException e) {
					System.out.println(e.getMessage());
				} catch (InsufficientMachineBalanceException e) {
					System.out.println(e.getMessage());
				}
				break;
			case 3:
				resetUserAttempts(card);
				System.out.println("=====================================================================");
				System.out.println("===================================user attempts are reset ==================================");
				System.out.println("=====================================================================");
				ACTIVITY.add("Accessed by : " + card.getUserName()+ " Activity performed: resetting user pin attempts of user ");
				break;
			case 4:
				checkATMMachineActivities(); // display ATM activities
				break;
			case 5:
				flag = false; // exit operator mode
				break;
			default:
				System.out.println("you have entered a wrong option");

			}
		}
	}

	public static void main(String[] args) throws NotAOperatorException{
		// initialize the database some sample input data
		dataBase.put(2222222222l, new AxisDebitCard(2222222222l,"Yash",50000.0,2222) );
		dataBase.put(3333333333l, new HDFCDebitCard(3333333333l,"Sathsena",30000.0,3333) );
		dataBase.put(4444444444l, new SBIDebitCard(4444444444l,"Yuva",70000.0,4444) );
		dataBase.put(1111111111l, new OperatorCard(1111111111l,1111,"Riya") );
		
		Scanner scanner = new Scanner(System.in);
		long cardNumber = 0;
		double depositeAmount = 0.0;
		double withdrawAmount = 0.0;
		int pin = 0;
		// main loop for ATM Operations.
		while(MACHINE_ON) {
			System.out.println("Please enter the debit card number : ");
			cardNumber = scanner.nextLong();
			try {
				System.out.println("Enter the pin :");
				pin = scanner.nextInt();
				card = validCredentials(cardNumber,pin); // validate card and pin
				
				if (card == null) {
					System.out.println("card validation failed");
					continue;
				}
				ACTIVITY.add("ACCESSED by : " + card.getUserName()+" status: Access approved");
				if(card.getUserType().equals("operator")) {
					operatorMode(card);
					continue;
				}
				while(true) {
					System.out.println("USER MODE: "+card.getUserName());
					System.out.println("==============================================");
					System.out.println("||                 1.Withdraw Amount                  ||");
					System.out.println("||                 2.Deposite Amount                  ||");
					System.out.println("||                 3.Check Balance                    ||");
					System.out.println("||                 4.Change PIN                       ||");
					System.out.println("||                 5.Generate MINI statement          ||");
					System.out.println("========================================================");
					System.out.println("Enter your choice : " );
					int option = scanner.nextInt();
					try {
						switch(option) {
						case 1:
							System.out.println("Please Enter the Amount to withdraw ");
							withdrawAmount = scanner.nextDouble();
							validateAmount(withdrawAmount);
							card.withdrawAmount(withdrawAmount);
							ATM_MACHINE_BALANCE = withdrawAmount;
							ACTIVITY.add("ACCESSED by : " + card.getUserName()+" Activity : amount withdraw" +withdrawAmount+"from machine");
							break;
						case 2:
							System.out.println("Please enter the amount to deposite");
							depositeAmount = scanner.nextDouble();
							validateDepositeAmount(depositeAmount);
							ATM_MACHINE_BALANCE +=depositeAmount;
							card.depositAmount(depositeAmount);
							ACTIVITY.add("ACCESSED by : " + card.getUserName()+" Activity : amount deposited" +depositeAmount+"in machine");
							break;
						case 3:
							System.out.println("Your account balance is :" +card.checkAccountBalance());
							ACTIVITY.add("ACCESSED by : " + card.getUserName()+" Activity :check balance " );
							break;
						case 4 :
							System.out.println("enter a new pin");
							pin = scanner.nextInt();
							card.changePinNumber(pin);
							ACTIVITY.add("ACCESSED by : " + card.getUserName()+" Activity : changed pin number");
							break;
						case 5 :
							ACTIVITY.add("ACCESSED by : " + card.getUserName()+" Activity :generating MINI statement" );
							card.generateMiniStatement();
							break;
							default:
								System.out.println("You have entered a wrong option");
								break;
						}
						System.out.println("Do you want to continue?(y/n)");
						String nextOption = scanner.next();
						if(nextOption.equalsIgnoreCase("N")) {
							break;
						}
						
					}catch(InvalidAmountException | InsufficientBalanceException|InsufficientMachineBalanceException e ) {
						System.out.println(e.getMessage());
					}
				}
			}catch(InvalidPinException|InvalidCardException|IncorrectPinLimitReachedException e) {
				ACTIVITY.add("ACCESSED by : " + card.getUserName()+"Activity: status: Access denied" );
				System.out.println(e.getMessage());

			}
		}
		System.out.println("=================================================");
		System.out.println("Thank you for using AXIS bank");
		System.out.println("=================================================");

	}

}