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
					this application. Click here to <a href="${pageContext.request.contextPath}/index.html">Try Again</a>.
				</p>

			</div>
		</div>
	</div>

</body>
</html>
