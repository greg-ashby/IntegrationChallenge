<#list accounts>
	<h2>CURRENT SUBSCRIPTION LIST</h2>
    <table border='1'>
    	<tr style='font-weight: bold; background-color: grey'>
    		<td>UUID</td>
    		<td>EMAIL</td>
    		<td>COMPANY ID</td>
    		<td>EDITION</td>
    		<td>STATUS</td>
    	</tr>
    <#items as account>
		<tr>
    		<td>${account.id}</td>
    		<td>${account.email}</td>
    		<td>${account.companyId}</td>
    		<td>${account.editionCode}</td>
    		<td>${account.status}</td>
    	</tr>
    </#items>
    </table>
<#else>
    <h2>NO SUBSCRIPTIONS FOUND</h2>
</#list>