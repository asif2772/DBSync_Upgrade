<%--
  Created by IntelliJ IDEA.
  User: ASIF
  Date: 1/10/2022
  Time: 7:17 PM
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<!doctype html>
<html lang="en">
<head>
    <title>Login 01</title>
    <meta charset="utf-8">
    <meta name="layout" content="">

    <link href="https://fonts.googleapis.com/css?family=Lato:300,400,700&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/font-awesome/4.7.0/css/font-awesome.min.css">

    <asset:stylesheet href="style.css"/>
    <asset:javascript src="jquery-3.3.1.min.js"/>
    <asset:javascript src="popper.js"/>
    <asset:javascript src="bootstrap.min.js"/>
</head>
<body>
<section class="ftco-section">
    <div class="container">
        <div class="row justify-content-center">
            <div class="col-md-7 col-lg-5">
                <g:if test='${flash.message}'>
                    <div id="loginMsg" class='login_message'><p>${flash.message}</p></div>
                </g:if>
                <div class="login-wrap p-4 p-md-5">
                    <div class="icon d-flex align-items-center justify-content-center">
                        <span class="fa fa-user-o"></span>
                    </div>
                    <h3 class="text-center mb-4">Sign In</h3>
                    <form action='${postUrl}' method='POST' id='loginForm' class='login-form' autocomplete='off'>
                        <div class="form-group">
                            <input type="text" name="username" class="form-control rounded-left" placeholder="Username" required>
                        </div>
                        <div class="form-group d-flex">
                            <input type="password" name="password" class="form-control rounded-left" placeholder="Password" required>
                        </div>
                        <div class="form-group">
                            <button type="submit" id="submit" class="form-control btn btn-primary rounded submit px-3">Login</button>
                        </div>
                        <div class="form-group d-md-flex">
                            <div class="w-50">
                                <label class="checkbox-wrap checkbox-primary">Remember Me
                                    <input type="checkbox" checked>
                                    <span class="checkmark"></span>
                                </label>
                            </div>
                            <div class="w-50 text-md-right">
                                <g:link controller="login" class="forgotPass" action="forgotPassword">Forgot password?</g:link>
                            </div>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    </div>
</section>
</body>
</html>

