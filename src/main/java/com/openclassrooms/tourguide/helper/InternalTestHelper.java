package com.openclassrooms.tourguide.helper;

public class InternalTestHelper {

	// Set this default up to 100,000 for testing
	private static int internalUserNumber = 100;

	// Ajout du flag testMode
	private static boolean testMode = false;

	public static void setInternalUserNumber(int internalUserNumber) {
		InternalTestHelper.internalUserNumber = internalUserNumber;
	}

	public static int getInternalUserNumber() {
		return internalUserNumber;
	}

	// Méthodes pour gérer le mode test
	public static void setTestMode(boolean isTest) {
		testMode = isTest;
	}

	public static boolean isTestMode() {
		return testMode;
	}
}
