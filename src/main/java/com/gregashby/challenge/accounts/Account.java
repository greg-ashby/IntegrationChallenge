package com.gregashby.challenge.accounts;

import com.gregashby.challenge.json.AppDirectResponse;

public class Account {

	private int id = -1;
	private String userId = null;
	private String companyId = null;

	public Account(AppDirectResponse response) {
		setUserId(response.getCreator().getEmail());
		setCompanyId(response.getPayload().getCompany().getUuid());
	}

	public Account() {
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
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
				if (userId.equals(account.getUserId())) {
					if (companyId.equals(account.getCompanyId())) {
						return true;
					}
				}
			}
		}
		return false;
	}

}
