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

                <!--LOGOUT BUTTON-->
                <form action="${pageContext.request.contextPath}/logout" method="POST">
                    <button type="submit" class="btn btn-outline-success">
                        <spring:message code="logout_label"/>
                    </button>
                </form>

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

            <!--CATALOG BUTTON-->
            <form action="${pageContext.request.contextPath}/catalog" method="GET">
                <div class="input-group mb-3">
                    <button type="submit" class="btn btn-outline-primary btn-block">
                        <spring:message code="catalog_label"/>
                    </button>
                </div>
            </form>

            <!--JOURNAL BUTTON-->
            <form action="${pageContext.request.contextPath}/operation" method="GET">
                <div class="input-group mb-3">
                    <button type="submit" class="btn btn-outline-primary btn-block">
                        <spring:message code="journal_label"/>
                    </button>
                </div>
            </form>

            <!--CREATE NEW USER BUTTON-->
            <div class="input-group mb-3">
                <!-- Button trigger modal -->
                <button type="button" class="btn btn-outline-danger btn-block" data-toggle="modal"
                data-target="#modalCenter-Register">
                    <spring:message code="new_user_label"/>
                </button>
                <!-- Modal -->
                <div class="modal fade" id="modalCenter-Register" tabindex="-1" role="dialog"
                aria-labelledby="modalCenterTitle" aria-hidden="true">
                    <div class="modal-dialog modal-dialog-centered" role="document">
                        <div class="modal-content">
                            <div class="modal-header">
                                <h5 class="modal-title">
                                    <spring:message code="register_form"/>
                                </h5>
                                <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                                    <span aria-hidden="true">&times;</span>
                                </button>
                            </div>
                            <form action="${pageContext.request.contextPath}/user"
                            modelAttribute="userCreateDto" method="POST">
                                <div class="modal-body">
                                    <div class="form-group">
                                        <label for="registerUsername">
                                            <spring:message code="username_label"/>
                                        </label>
                                        <input type="text" name="username" class="form-control"
                                        id="registerUsername" placeholder="Enter user name">
                                    </div>
                                    <div class="form-group">
                                        <label for="registerPassword">
                                            <spring:message code="password_label"/>
                                        </label>
                                        <input type="password" name="password" class="form-control"
                                        id="registerPassword" placeholder="Password">
                                    </div>
                                    <div class="form-group">
                                        <label>
                                            <spring:message code="user_state_label"/>
                                        </label>
    				                    <div class="input-group mb-3">
    					                    <select id="registerUserState" name="state" class="custom-select">
    						                    <option selected value="OPERATOR"><spring:message
    						                    code="operator_label"/></option>
    						                    <option value="ADMIN"><spring:message code="admin_label"/></option>
    					                    </select>
    				                    </div>
    				                </div>
                                </div>
                                <div class="modal-footer">
                                    <button type="submit" class="btn btn-primary">
                                        <spring:message code="register_label"/>
                                    </button>
                                    <button type="button" class="btn btn-secondary" data-dismiss="modal">
                                        <spring:message code="close_label"/>
                                    </button>
                                </div>
                            </form>
                        </div>
                    </div>
                </div>
            </div>

    	</div>

        <!--MAIN BAR-->
        <div class="main-bar">

            <!--USERS LIST-->
            <c:choose>
                <c:when test="${empty userList}">
                    <div class="input-group mb-3">
                        <h4><spring:message code="empty_users_list"/></h4>
                    </div>
                </c:when>
                <c:otherwise>
                    <div class="input-group mb-3">
                        <h4><spring:message code="users_list_label"/></h4>
                    </div>
                    <div class="accordion" id="orderListWrapper">
                        <c:forEach var="user" items="${userList}">
                            <form action="${pageContext.request.contextPath}/user/${user.id}"
                            modelAttribute="userUpdateDto" method="POST">
                                <div class="alert alert-success" role="alert">
                                    <div class="card">
                                        <div class="card-header">
                                            <h5 class="mb-0">
                                                <div class="input-group mb-3">
                                                    <span class="input-group-text"><spring:message
                                                    code="username_label"/>: ${user.username}</span>
                                                    <input type="hidden" name="username" type="text"
                                                    value="${user.username}">
                                                </div>
                                            </h5>
                                            <div class="input-group mb-3">
                                                <div class="input-group-prepend">
                                                    <span class="input-group-text"><spring:message
                                                    code="user_state_label"/>:</span>
                                                </div>
                                                <h1></h1>
    				                            <select id="updateUserState${user.id}" name="state"
    				                            class="custom-select">
    				                                <option <c:if test="${user.state == 'OPERATOR'}">selected</c:if>
    				                                value="OPERATOR"><spring:message code="operator_label"/></option>
    				                                <option <c:if test="${user.state == 'ADMIN'}">selected</c:if>
    				                                value="ADMIN"><spring:message code="admin_label"/></option>
    				                            </select>
                                            </div>
                                        </div>
                                    </div>
                                    <div class="card-body">
                                        <button type="submit" class="btn btn-primary">
                                            <spring:message code="save_changes_label"/>
                                        </button>
                                    </div>
                                </div>
                            </form>
                        </c:forEach>
                    </div>
                </c:otherwise>
            </c:choose>

        </div>

	</body>
</html>