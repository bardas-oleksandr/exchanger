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

            <!--OPERATOR BUTTON BAR. Available for user with operator state-->
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

            <!--ADMINISTRATOR BUTTON BAR. Available for administrators-->
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

                <!--SYNCHRONIZE WITH NBU BUTTON-->
                <form action="${pageContext.request.contextPath}/nburate/synchronize" method="POST">
                    <div class="input-group mb-3">
                        <button type="submit" class="btn btn-outline-danger btn-block">
                            <spring:message code="synchronize_nbu_label"/>
                        </button>
                    </div>
                </form>

                <!--SYNCHRONIZE WITH PRIVATBANK BUTTON-->
                <form action="${pageContext.request.contextPath}/rate/synchronize" method="POST">
                    <div class="input-group mb-3">
                        <button type="submit" class="btn btn-outline-danger btn-block">
                            <spring:message code="synchronize_pb_label"/>
                        </button>
                    </div>
                </form>

                <!--ADD CURRENCY BUTTON-->
                <div class="input-group mb-3">
                    <!-- Button trigger modal -->
                    <button type="button" class="btn btn-outline-danger btn-block" data-toggle="modal"
                    data-target="#modalCenter_add_currency">
                        <spring:message code="add_currency_label"/>
                    </button>
                    <!-- Modal -->
                    <div class="modal fade" id="modalCenter_add_currency" tabindex="-1" role="dialog"
                    aria-labelledby="modalCenterTitle" aria-hidden="true">
                        <div class="modal-dialog modal-dialog-centered" role="document">
                            <div class="modal-content">
                                <div class="modal-header">
                                    <h5 class="modal-title">
                                        <spring:message code="add_currency_form"/>
                                    </h5>
                                    <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                                        <span aria-hidden="true">&times;</span>
                                    </button>
                                </div>
                                <form action="${pageContext.request.contextPath}/currency"
                                modelAttribute="currencyCreateDto" method="POST">
                                    <div class="modal-body">
                                        <div class="form-group">
                                            <label for="addCurrencyCode">
                                                <spring:message code="currency_code_label"/>
                                            </label>
                                            <input type="text" name="code" class="form-control"
                                            id="addCurrencyCode" placeholder="Enter currency code">
                                        </div>
                                        <div class="form-group">
                                            <label for="addCurrencyName">
                                                <spring:message code="currency_code_label"/>
                                            </label>
                                            <input type="text" name="name" class="form-control"
                                            id="addCurrencyName" placeholder="Enter currency name">
                                        </div>
                                        <div class="form-group">
                                            <label for="addCurrencyBuy">
                                                <spring:message code="buy_label"/>
                                            </label>
                                            <input type="text" name="buy" class="form-control"
                                            id="addCurrencyBuy" placeholder="Enter buy price">
                                        </div>
                                        <div class="form-group">
                                            <label for="addCurrencySale">
                                                <spring:message code="sale_label"/>
                                            </label>
                                            <input type="text" name="sale" class="form-control"
                                            id="addCurrencySale" placeholder="Enter sale price">
                                        </div>
                                    </div>
                                    <div class="modal-footer">
                                        <button type="submit" class="btn btn-primary">
                                            <spring:message code="add_label"/>
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

            </security:authorize>

    	</div>

        <!--MAIN BAR-->
        <div class="main-bar">
                <!--AVAILABLE FOR ADMINISTRATORS-->
                <security:authorize access="hasRole('ADMIN')">
                    <spring:message code="currency_code_label" var="currencyCodeLabel"/>
                    <spring:message code="currency_name_label" var="currencyNameLabel"/>
                    <spring:message code="buy_label" var="buyLabel"/>
                    <spring:message code="sale_label" var="saleLabel"/>
                    <spring:message code="nbu_label" var="nbuLabel"/>
                    <!--CATALOG-->
                    <div id="adminCatalogView">
                        <div class="w-100">
                            <div class="row">
                                <div class="row no-gutters">
                                    <div class="col-1">
                                        <input type="text" readonly
                                            value="${currencyCodeLabel}"
                                            class="form-control">
                                    </div>
                                    <div class="col-3">
                                        <input type="text" readonly
                                            value="${currencyNameLabel}"
                                            class="form-control">
                                    </div>
                                    <div class="col-2">
                                        <input type="text" readonly
                                            value="${buyLabel}"
                                            class="form-control">
                                    </div>
                                    <div class="col-2">
                                        <input type="text" readonly
                                            value="${saleLabel}"
                                            class="form-control">
                                    </div>
                                    <div class="col-2">
                                        <input type="text" readonly
                                            value="${nbuLabel}"
                                            class="form-control">
                                    </div>
                                </div>
                            </div>
                        </div>

                        <c:forEach var="catalogItem" items="${catalogItems}">
                            <div class="w-100">
                                <form action="${pageContext.request.contextPath}/currency/${catalogItem.rateViewDto.currencyViewDto.id}"
                                modelAttribute="currencyCreateDto" method="POST">
                                    <div class="row">
                                        <div class="row no-gutters">
                                            <div class="col-1">
                                                <input type="text" name="code"
                                                value="${catalogItem.rateViewDto.currencyViewDto.code}"
                                                class="form-control">
                                            </div>
                                            <div class="col-3">
                                                <input type="text" name="name"
                                                value="${catalogItem.rateViewDto.currencyViewDto.name}"
                                                class="form-control">
                                            </div>
                                            <div class="col-2">
                                                <input type="text" name="buy"
                                                value="${catalogItem.rateViewDto.buy}"
                                                class="form-control">
                                            </div>
                                            <div class="col-2">
                                                <input type="text" name="sale"
                                                value="${catalogItem.rateViewDto.sale}"
                                                class="form-control">
                                            </div>
                                            <div class="col-2">
                                                <input readonly type="text"
                                                    <c:if test="${not empty catalogItem.nbuRateViewDto}">
                                                        value="${catalogItem.nbuRateViewDto.price}"
                                                    </c:if>
                                                class="form-control">
                                            </div>
                                            <div class="col">
                                                <button type="submit" class="btn btn-primary">
                                                    <spring:message code="save_changes_label"/>
                                                </button>
                                            </div>
                                        </div>
                                    </div>
                                </form>
                            </div>
                        </c:forEach>
                    </div>

                </security:authorize>

                <!--AVAILABLE FOR OPERATORS-->
                <security:authorize access="hasRole('OPERATOR')">

                    <!--CATALOG-->
                    <div id="catalogView" class="catalogWrapper">
                        <div class="row">
                            <div class="row no-gutters">
                                <div class="col">
                                    <div class="alert alert-secondary">
                                        <spring:message code="currency_code_label"/>
                                    </div>
                                </div>
                                <div class="col-5">
                                    <div class="alert alert-secondary">
                                        <spring:message code="currency_name_label"/>
                                    </div>
                                </div>
                                <div class="col">
                                    <div class="alert alert-secondary">
                                        <spring:message code="buy_label"/>
                                    </div>
                                </div>
                                <div class="col">
                                    <div class="alert alert-secondary">
                                        <spring:message code="sale_label"/>
                                    </div>
                                </div>
                                <div class="col">
                                    <div class="alert alert-secondary">
                                        <spring:message code="nbu_label"/>
                                    </div>
                                </div>

                                <c:forEach var="catalogItem" items="${catalogItems}">
                                    <div class="w-100"></div>
                                    <div class="col">
                                        <input readonly type="text"
                                        value="${catalogItem.rateViewDto.currencyViewDto.code}"
                                        class="form-control">
                                    </div>
                                    <div class="col-5">
                                        <input readonly type="text"
                                        value="${catalogItem.rateViewDto.currencyViewDto.name}"
                                        class="form-control">
                                    </div>
                                    <div class="col">
                                        <input readonly type="text"
                                        value="${catalogItem.rateViewDto.buy}"
                                        class="form-control">
                                    </div>
                                    <div class="col">
                                        <input readonly type="text"
                                        value="${catalogItem.rateViewDto.sale}"
                                        class="form-control">
                                    </div>
                                    <div class="col">
                                        <input readonly type="text"
                                            <c:if test="${not empty catalogItem.nbuRateViewDto}">
                                                value="${catalogItem.nbuRateViewDto.price}"
                                            </c:if>
                                            <c:if test="${empty catalogItem.nbuRateViewDto}">
                                                value="-"
                                            </c:if>
                                        class="form-control">
                                    </div>
                                </c:forEach>
                            </div>
                        </div>
                    </div>

                </security:authorize>

        </div>
	</body>
</html>