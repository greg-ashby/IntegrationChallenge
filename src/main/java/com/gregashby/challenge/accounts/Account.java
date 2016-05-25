package com.gregashby.challenge.accounts;

public class Account {

	private String id = null;
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

	public String getId() {
		return id;
	}

	public void setId(String uuid) {
		this.id = uuid;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj.getClass() == Account.class) {
			Account account = (Account) obj;
			if (id.equals(account.getId())) { // putting on separate lines so it's easier to debug
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
