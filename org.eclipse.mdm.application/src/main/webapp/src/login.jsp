<!--********************************************************************************
 * Copyright (c) 2015-2018 Contributors to the Eclipse Foundation
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 ********************************************************************************-->


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
