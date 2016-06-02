<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta name="viewport" content="width=device-width, initial-scale=1">
<meta name="description" content="">
<meta name="author" content="">

<title>Login failed</title>


<link rel='stylesheet' href='http://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/css/bootstrap.min.css'>
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/login.css">

</head>

<body>
	<div class="bform">
		<div class="bform2">
			<div class="container">

				<h2 class="form-signin-heading">Login Error</h2>
				<h2>Invalid user name or password.</h2>

				<p>
					Please enter a user name or password that is authorized to access
					this application. Click here to <a href="${pageContext.request.contextPath}/index.jsp">Try Again</a>.
				</p>

			</div>
		</div>
	</div>

</body>
</html>
