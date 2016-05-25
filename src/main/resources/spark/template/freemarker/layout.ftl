<html>
	<head>
		<title>${title}</title>
		<!-- TODO put a nice stylesheet here and pretty this up -->
	</head>
	<body>	
		<#include "${templateName}">
		<br /><br /><br /><br /><br />
		<a href="./">Return Home</a>
		<!-- TODO I should store the 'market place' values with the account and change this dynamically -->
		<a href="https://ashbygreg-test.byappdirect.com/accounts/apps">Change Subscription</a>
		<a href="https://ashbygreg-test.byappdirect.com/accounts/users">Change Users</a>
		<a href="https://ashbygreg-test.byappdirect.com/">Logout</a>
	</body>
</html>