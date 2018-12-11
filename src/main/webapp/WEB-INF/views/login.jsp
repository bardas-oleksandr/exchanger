<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ page contentType="text/html;charset=UTF-8" language="java" errorPage="/error.jsp" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="security" %>

<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html: charset=utf-8">
        <meta charset="utf-8">
        <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
        <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.1.3/css/bootstrap.min.css" integrity="sha384-MCw98/SFnGE8fJT3GXwEOngsV7Zt27NXFoaoApmYm81iuXoPkFOJwJ8ERdknLPMO" crossorigin="anonymous">
        <title>Currency exchanger</title>
        <link href="${pageContext.request.contextPath}/resources/css/index_styles.css" type="text/css" rel="stylesheet">
	    <script src="https://code.jquery.com/jquery-3.2.1.min.js" integrity="sha256-hwg4gsxgFZhOsEEamdOYGBf13FyQuiTwlAQgxVSNgt4=" crossorigin="anonymous"></script>
		<script src="https://stackpath.bootstrapcdn.com/bootstrap/4.1.3/js/bootstrap.min.js" integrity="sha384-ChfqqxuZUCnJSK3+MXmPNIyE6ZbWh2IMqE241rYiqJxyMiZ6OW/JmZQ5stwEULTy" crossorigin="anonymous"></script>
	</head>
	<body>
	    <!--TOP SIDE BAR-->
	    <nav class="navbar navbar-light bg-light">
	        <a class="navbar-brand">
	            <spring:message code="currency_exchanger_label"/>
	        </a>
            <div class="btn-group" role="group" aria-label="Basic example">

                <!--MAIN PAGE BUTTON-->
                <a href="${pageContext.request.contextPath}/">
                    <button type="button" class="btn btn-outline-warning" data-toggle="modal" data-target="#modalCenter-MainPage">
                        <spring:message code="main_page_label"/>
                    </button>
                </a>

      	        <!--LANGUAGE BUTTONS-->
      	        <div id="localizationFrame">
      	            <span style="float: right">
                        <a href="?lang=en">en</a>
                        |
                        <a href="?lang=ua">ua</a>
                        |
                        <a href="?lang=ru">ru</a>
                    </span>
                </div>

            </div>
        </nav>

	    <!--MAIN BAR-->
		<div class="main-bar">

		    <!--LOGIN FORM-->
            <!--AVAILABLE FOR NOT AUTHENTICATED USERS-->
            <security:authorize access="!isAuthenticated()">
                <div class="modal-dialog modal-dialog-centered" role="document">
                    <div class="modal-content">
                        <div class="modal-header">
                            <h5 class="modal-title">
                                <spring:message code="login_form"/>
                            </h5>
                        </div>
                        <c:url value="/j_spring_security_check" var="loginUrl"/>
                        <form action="${loginUrl}" method="POST">
                            <div class="modal-body">

                                <c:if test="${not empty login_failed}">
                                    <p><spring:message code="access_denied"/></p>
                                </c:if>

                                <div class="form-group">
                                    <label for="loginUsername">
                                        <spring:message code="username_label"/>
                                    </label>
                                    <input type="text" name="username" class="form-control"
                                    id="loginUsername" placeholder="Enter username">
                                </div>
                                <div class="form-group">
                                    <label for="loginPassword">
                                        <spring:message code="password_label"/>
                                    </label>
                                    <input type="password" name="password" class="form-control"
                                    id="loginPassword" placeholder="Password">
                                </div>
                            </div>
                            <div class="modal-footer">
                                <button type="submit" class="btn btn-primary">
                                    <spring:message code="login_label"/>
                                </button>
                            </div>
                        </form>
                    </div>
                </div>
            </security:authorize>

        </div>
	</body>
</html>