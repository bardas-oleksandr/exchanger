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

    <script type="text/javascript">
        function selectCurrencyCode(){
            var currencyId = $('select[id="selectCurrencyCode"]').val();
            jQuery.ajax({
                type: 'GET',
                url: '/exchanger/rest/catalog/' + currencyId,
                accepts: 'application/json',
                contentType: 'application/json',
                headers:{'Accept':'application/json', 'Content-Type':'application/json'},
                dataType: 'json',
                success: function(catalogItemViewDto){
                    if(catalogItemViewDto.rateViewDto != null){
                        document.getElementById('rateIdInput').value = catalogItemViewDto.rateViewDto.id;
                    }else{
                        alert("Rate for this currency is not set");
                    }
                    if(catalogItemViewDto.nbuRateViewDto != null){
                        document.getElementById('nbuRateIdInput').value = catalogItemViewDto.nbuRateViewDto.id;
                    }
                }
            });
            enterCurrSum();
        };

        function buyOperationChange(){
            enterCurrSum();
        };

        function enterCurrSum(){
            var buyOperation = $('select[id="buyOperationSelect"]').val();
            var sumCurrency = $('input[id="sumCurrencyInput"]').val();
            var currencyId = $('select[id="selectCurrencyCode"]').val();
            jQuery.ajax({
                type: 'GET',
                url: '/exchanger/rest/catalog/' + currencyId,
                accepts: 'application/json',
                contentType: 'application/json',
                headers:{'Accept':'application/json', 'Content-Type':'application/json'},
                dataType: 'json',
                success: function(catalogItemViewDto){
                    if(buyOperation == 'true'){
                        document.getElementById('sumHrnInput').value = Math.round((catalogItemViewDto.rateViewDto.buy*sumCurrency)*100)/100;
                    }else{
                        document.getElementById('sumHrnInput').value = Math.round((catalogItemViewDto.rateViewDto.sale*sumCurrency)*100)/100;
                    }
                }
            });
        };

    </script>

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

            <!--CATALOG BUTTON-->
            <form action="${pageContext.request.contextPath}/catalog" method="GET">
                <div class="input-group mb-3">
                    <button type="submit" class="btn btn-outline-primary btn-block">
                        <spring:message code="catalog_label"/>
                    </button>
                </div>
            </form>

            <!--ADMINISTRATOR BUTTON BAR. Available for administrators-->
            <security:authorize access="hasRole('ADMIN')">

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


        <!--AVAILABLE FOR ADMINISTRATORS-->
        <security:authorize access="hasRole('ADMIN')">

            <!--MAIN BAR-->
            <div class="wide-main-bar">
                <!--JOURNAL-->
                <div id="adminJournalView">
                        <div class="row no-gutters">
                            <div class="w-100"></div>
                            <div class="col-1">
                                <div class="alert alert-secondary">
                                    <spring:message code="currency_code_label"/>
                                </div>
                            </div>
                            <div class="col-2">
                                <div class="alert alert-secondary">
                                    <spring:message code="currency_name_label"/>
                                </div>
                            </div>
                            <div class="col-2">
                                <div class="alert alert-secondary">
                                    <spring:message code="operation_label"/>
                                </div>
                            </div>
                            <div class="col-1">
                                <div class="alert alert-secondary">
                                    <spring:message code="hrn_label"/>
                                </div>
                            </div>
                            <div class="col-1">
                                <div class="alert alert-secondary">
                                    <spring:message code="curr_label"/>
                                </div>
                            </div>
                            <div class="col">
                                <div class="alert alert-secondary">
                                    <spring:message code="date_label"/>
                                </div>
                            </div>
                            <div class="col-2">
                                <div class="alert alert-secondary">
                                    <spring:message code="operator_label"/>
                                </div>
                            </div>
                            <div class="col-1">
                                <div class="alert alert-secondary">
                                    <spring:message code="deleted_label"/>
                                </div>
                            </div>

                            <c:forEach var="operation" items="${operationList}">
                                <div class="w-100"></div>
                                <div class="col-1">
                                    <input readonly type="text" value="${operation.rateViewDto.currencyViewDto.code}"
                                    class="form-control">
                                </div>
                                <div class="col-2">
                                    <input readonly type="text" value="${operation.rateViewDto.currencyViewDto.name}"
                                    class="form-control">
                                </div>
                                <c:choose>
                                    <c:when test="${operation.buyOperation == true}">
                                        <spring:message code="buy_label" var="operationType"/>
                                    </c:when>
                                    <c:otherwise>
                                        <spring:message code="sale_label" var="operationType"/>
                                    </c:otherwise>
                                </c:choose>
                                <div class="col-2">
                                    <input readonly type="text" value="${operationType}" class="form-control">
                                </div>
                                <div class="col-1">
                                    <input readonly type="text" value="${operation.sumHrn}" class="form-control">
                                </div>
                                <div class="col-1">
                                    <input readonly type="text" value="${operation.sumCurrency}" class="form-control">
                                </div>
                                <div class="col">
                                    <input readonly type="text" value="${operation.date}" class="form-control">
                                </div>
                                <div class="col-2">
                                    <input readonly type="text" value="${operation.userViewDto.username}"
                                    class="form-control">
                                </div>
                                <form action="${pageContext.request.contextPath}/operation/${operation.id}"
                                method="POST">
                                    <div class="col-1">
                                        <input type="checkbox" id="isDeletedCheckBoxFor${user.id}"
                                        name="isDeleted" <c:if test="${operation.deleted}">checked</c:if>
                                        class="form-control">
                                    </div>
                                    <button type="submit" class="btn btn-primary">
                                        <spring:message code="save_changes_label"/>
                                    </button>
                                </form>
                            </c:forEach>
                        </div>
                </div>
            </div>
        </security:authorize>

        <!--AVAILABLE FOR OPERATORS-->
        <security:authorize access="hasRole('OPERATOR')">
            <!--MAIN BAR-->
            <div class="main-bar">
                <div class="form-row">
                    <div class="form-group col-md-3">
        	            <label>
        	                <spring:message code="currency_label"/>
        	            </label>
                        <select id="selectCurrencyCode" name="selectCurrencyCode" class="custom-select"
                        onchange="javascript:selectCurrencyCode();">
                            <option value=""><spring:message code="select_currency_label"/></option>
                            <c:forEach var="currency" items="${currencyList}">
                                <option value=${currency.id}>${currency.code}</option>
                            </c:forEach>
                        </select>
                    </div>
                </div>
                <form action="${pageContext.request.contextPath}/operator/operation"
                modelAttribute="operationCreateDto" method="POST">
                    <div class="form-row">
                        <div class="form-group col-md-3">
    				        <input type="hidden" name="rateId" id="rateIdInput"/>
                            <input type="hidden" name="nbuRateId" id="nbuRateIdInput"/>
                            <label for="buyOperationSelect">
                                <spring:message code="operation_label"/>
                            </label>
    					    <select id="buyOperationSelect" name="buyOperation" class="custom-select"
    					    onchange="javascript:buyOperationChange();">
    						    <option selected value="true"><spring:message code="buy_label"/></option>
    						    <option value="false"><spring:message code="sale_label"/></option>
    					    </select>
                        </div>
                        <div class="form-group col-md-3">
                            <label for="sumCurrency">
                                <spring:message code="curr_label"/>
                            </label>
                            <input type="text" name="sumCurrency" class="form-control" id="sumCurrencyInput"
                            placeholder="Currency sum" onchange="javascript:enterCurrSum();">
                        </div>
                        <div class="form-group col-md-3">
                            <label for="sumHrn">
                                <spring:message code="hrn_label"/>
                            </label>
                            <input readonly type="text" name="sumHrn" class="form-control" id="sumHrnInput"
                            placeholder="Hryvnia sum">
                        </div>
                    </div>
                    <div class="form-row">
                        <div class="form-group col-md-3">
                            <button type="submit" class="btn btn-primary">
                                <spring:message code="execute_label"/>
                            </button>
                        </div>
                    </div>
                </form>

                <!--JOURNAL-->
                <div id="journalView">
                    <div class="row">
                        <div class="row no-gutters">
                            <div class="col-1">
                                <div class="alert alert-secondary">
                                    <spring:message code="currency_code_label"/>
                                </div>
                            </div>
                            <div class="col-2">
                                <div class="alert alert-secondary">
                                    <spring:message code="currency_name_label"/>
                                </div>
                            </div>
                            <div class="col">
                                <div class="alert alert-secondary">
                                    <spring:message code="operation_label"/>
                                </div>
                            </div>
                            <div class="col-1">
                                <div class="alert alert-secondary">
                                    <spring:message code="hrn_label"/>
                                </div>
                            </div>
                            <div class="col-1">
                                <div class="alert alert-secondary">
                                    <spring:message code="curr_label"/>
                                </div>
                            </div>
                            <div class="col">
                                <div class="alert alert-secondary">
                                    <spring:message code="date_label"/>
                                </div>
                            </div>
                            <div class="col-2">
                                <div class="alert alert-secondary">
                                    <spring:message code="operator_label"/>
                                </div>
                            </div>

                            <c:forEach var="operation" items="${operationList}">
                                <div class="w-100"></div>
                                <div class="col-1">
                                    <input readonly type="text" value="${operation.rateViewDto.currencyViewDto.code}"
                                    class="form-control">
                                </div>
                                <div class="col-2">
                                    <input readonly type="text" value="${operation.rateViewDto.currencyViewDto.name}"
                                    class="form-control">
                                </div>
                                <c:choose>
                                    <c:when test="${operation.buyOperation == true}">
                                        <spring:message code="buy_label" var="operationType"/>
                                    </c:when>
                                    <c:otherwise>
                                        <spring:message code="sale_label" var="operationType"/>
                                    </c:otherwise>
                                </c:choose>
                                <div class="col">
                                    <input readonly type="text" value="${operationType}" class="form-control">
                                </div>
                                <div class="col-1">
                                    <input readonly type="text" value="${operation.sumHrn}" class="form-control">
                                </div>
                                <div class="col-1">
                                    <input readonly type="text" value="${operation.sumCurrency}" class="form-control">
                                </div>
                                <div class="col">
                                    <input readonly type="text" value="${operation.date}" class="form-control">
                                </div>
                                <div class="col-2">
                                    <input readonly type="text" value="${operation.userViewDto.username}"
                                    class="form-control">
                                </div>
                            </c:forEach>
                        </div>
                    </div>
                </div>
            </div>
        </security:authorize>
	</body>
</html>