package com.gregashby.challenge.accounts;

public class Account {

	private int id = -1;
	private String email = null;
	private String companyId = null;
	private String editionCode = null;
	private String status = null;

	public String getEditionCode() {
		return editionCode;
	}

	public void setEditionCode(String editionCode) {
		this.editionCode = editionCode;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Account() {
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getCompanyId() {
		return companyId;
	}

	public void setCompanyId(String companyId) {
		this.companyId = companyId;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj.getClass() == Account.class) {
			Account account = (Account) obj;
			if (id == account.getId()) {
				if (email.equals(account.getEmail())) {
					if (companyId.equals(account.getCompanyId())) {
						if(editionCode.equals(account.getEditionCode())){
							if(status.equals(account.getStatus())){
								return true;
							}
						}
					}
				}
			}
		}
		return false;
	}

}
