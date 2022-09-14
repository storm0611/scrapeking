package com.interior.ui.utils;

import org.primefaces.PrimeFaces;

public class Notification {

	public static void notifySuccess(String message) {
		PrimeFaces.current().executeScript("notify_('success', '" + message + "', 'success');");
	}

	public static void notifyError(String message) {
		PrimeFaces.current().executeScript("notify_('danger', '" + message + "', 'danger');");
	}

	public static void notifyHardError(String message) {
		PrimeFaces.current().executeScript("notify_('danger', '" + message + "', 'danger');");
	}
	
	public static void notifyWarning(String message) {
		PrimeFaces.current().executeScript("notify_('warning', '" + message + "', 'warning');");
	}
	
	public static void notifyInfo(String message) {
		PrimeFaces.current().executeScript("notify_('info', '" + message + "', 'info');");
	}
}
