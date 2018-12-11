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

                <!--AVAILABLE FOR NOT AUTHENTICATED USERS-->
                <security:authorize access="!isAuthenticated()">

                    <!--LOGIN BUTTON-->
                    <form action="${pageContext.request.contextPath}/login" method="GET">
                        <button type="submit" class="btn btn-outline-success" data-toggle="modal">
                            <spring:message code="login_label"/>
                        </button>
                    </form>

                </security:authorize>

                <!--AVAILABLE FOR AUTHENTICATED USERS-->
                <security:authorize access="isAuthenticated()">

                    <!--LOGOUT BUTTON-->
                    <form action="${pageContext.request.contextPath}/logout" method="POST">
                        <button type="submit" class="btn btn-outline-success">
                            <spring:message code="logout_label"/>
                        </button>
                    </form>

                </security:authorize>

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

        <!--LEFT SIDE BAR-->
    	<div class="left-side-bar">

            <security:authorize access="isAuthenticated()">
                <!--CATALOG BUTTON-->
                <form action="${pageContext.request.contextPath}/catalog" method="GET">
                    <div class="input-group mb-3">
                        <button type="submit" class="btn btn-outline-primary btn-block">
                            <spring:message code="catalog_label"/>
                        </button>
                    </div>
                </form>
            </security:authorize>

            <!--OPERATOR BUTTON BAR-->
            <security:authorize access="hasRole('OPERATOR')">

                <!--JOURNAL BUTTON-->
                <form action="${pageContext.request.contextPath}/operator/operation" method="GET">
                    <div class="input-group mb-3">
                        <button type="submit" class="btn btn-outline-primary btn-block">
                            <spring:message code="journal_label"/>
                        </button>
                    </div>
                </form>

            </security:authorize>

            <!--ADMINISTRATOR BUTTON BAR-->
            <security:authorize access="hasRole('ADMIN')">

                <!--JOURNAL BUTTON-->
                <form action="${pageContext.request.contextPath}/operation" method="GET">
                    <div class="input-group mb-3">
                        <button type="submit" class="btn btn-outline-primary btn-block">
                            <spring:message code="journal_label"/>
                        </button>
                    </div>
                </form>

                <!--USERS BUTTON-->
                <form action="${pageContext.request.contextPath}/user" method="GET">
                    <div class="input-group mb-3">
                        <button type="submit" class="btn btn-outline-primary btn-block">
                            <spring:message code="users_label"/>
                        </button>
                    </div>
                </form>

            </security:authorize>
    	</div>

        <!--MAIN BAR-->
        <div class="main-bar">
        </div>
	</body>
</html>