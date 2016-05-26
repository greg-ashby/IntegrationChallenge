<html>
	<head>
		<title>${title}</title>
		<!-- TODO put a nice stylesheet here and pretty this up -->
	</head>
	<body>	
		<#include "${templateName}">
		
		<br /><br /><br /><br /><br />
		
		<#if loggedin??>
			You are logged in, feel free to check out the secured-page too!
			<a href="./logout">Logout</a>
		<#else>
			<a href="./login?openid_identifier=https%3A%2F%2Fashbygreg-test.byappdirect.com%2Fopenid%2Fid%2F">Login with AppDirect</a>
			<a href="./login?openid_identifier=https%3A%2F%2Fme.yahoo.com">Login with Yahoo!</a>
		</#if>
		
		<br /><br />
		
		<a href="./">Return Home</a>
	</body>
</html>