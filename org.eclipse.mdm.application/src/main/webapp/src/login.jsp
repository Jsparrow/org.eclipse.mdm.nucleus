<!--****************************************************************************
*  Original work: Copyright (c) 2016 Gigatronik Ingolstadt GmbH                *
*  Modified work: Copyright (c) 2017 Peak Solution GmbH                        *
*                                                                              *
*  All rights reserved. This program and the accompanying materials            *
*  are made available under the terms of the Eclipse Public License v1.0       *
*  which accompanies this distribution, and is available at                    *
*  http://www.eclipse.org/legal/epl-v10.html                                   *
*                                                                              *
*  Contributors:                                                               *
*  Dennis Schroeder - initial implementation                                   *
*  Matthias Koller, Johannes Stamm - additional client functionality           *
*****************************************************************************-->

<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta name="viewport" content="width=device-width, initial-scale=1">
<meta name="description" content="">
<meta name="author" content="">

<title>MDM Web Login</title>

<link rel='stylesheet' href='http://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/css/bootstrap.min.css'>
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/login.css">


</head>

<body>
	<div class="bform">
		<div class="bform2">
			<div class="container">

				<form class="form-signin" method=post action="j_security_check">
					<h2 class="form-signin-heading">MDM Web Login</h2>
					<input type="text" class="form-control" name="j_username"
						placeholder="Username" required autofocus> <input
						type="password" class="form-control" name="j_password"
						placeholder="Password" required>

					<button class="btn btn-lg btn-primary btn-block" type="submit">Login</button>
				</form>

			</div>
		</div>
	</div>

</body>
</html>
