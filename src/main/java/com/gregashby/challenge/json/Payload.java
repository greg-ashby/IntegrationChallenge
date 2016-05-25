package com.gregashby.challenge.json;

public class Payload {
	
	private String user = null;
	private Company company = null;
	private Account account = null;
	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public Account getAccount() {
		return account;
	}

	public void setAccount(Account account) {
		this.account = account;
	}

	public String getAddonInstance() {
		return addonInstance;
	}

	public void setAddonInstance(String addonInstance) {
		this.addonInstance = addonInstance;
	}

	public String getAddonBinding() {
		return addonBinding;
	}

	public void setAddonBinding(String addonBinding) {
		this.addonBinding = addonBinding;
	}

	public Order getOrder() {
		return order;
	}

	public void setOrder(Order order) {
		this.order = order;
	}

	private String addonInstance = null;
	private String addonBinding = null;
	private Order order = null;

	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

}
