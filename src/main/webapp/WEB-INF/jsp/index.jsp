<%-- 
    Document   : index
    Created on : Mar 7, 2016, 12:48:32 PM
    Author     : Bhavesh Patel
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<!DOCTYPE html>
<html lang="en">
<head>
<title>TradeZone</title>
<link rel="stylesheet" media="screen"
	href="<c:url value="/css/site.css" />">
<link rel="stylesheet" media="screen"
	href="//cdn.jsdelivr.net/jquery.mcustomscrollbar/3.0.6/jquery.mCustomScrollbar.min.css">
<link rel="stylesheet" media="screen"
	href="https://raw.githubusercontent.com/malihu/malihu-custom-scrollbar-plugin/master/jquery.mCustomScrollbar.css">
<link rel="shortcut icon" type="image/png" href="//cdn.jsdelivr.net/jquery.mcustomscrollbar/3.0.6/mCSB_buttons.png">
<script type="text/javascript"
	src="//cdn.jsdelivr.net/jquery/1.11.2/jquery.min.js"></script>
<script type="text/javascript"
	src="//cdn.jsdelivr.net/sockjs/0.3.4/sockjs.min.js"></script>
<script type="text/javascript"
	src="//cdnjs.cloudflare.com/ajax/libs/stomp.js/2.3.3/stomp.min.js"></script>
<script type="text/javascript"
	src="//cdn.jsdelivr.net/jquery.mcustomscrollbar/3.0.6/jquery.mCustomScrollbar.concat.min.js"></script>
<script type="text/javascript"
	src="https://raw.githubusercontent.com/malihu/malihu-custom-scrollbar-plugin/master/jquery.mCustomScrollbar.concat.min.js"></script>
<script type="text/javascript"
	src="https://raw.githubusercontent.com/malihu/malihu-custom-scrollbar-plugin/master/jquery.mCustomScrollbar.js"></script>

<script type="text/javascript">
	$(document).ready(
			function() {
				//var messageList = $('#twitter_tweets li:eq(0)');

				// defined a connection to a new socket endpoint
				var socket = new SockJS('/stomp');

				var stompClient = Stomp.over(socket);

				stompClient.connect({}, function(frame) {
					// subscribe to the /twitter endpoint
					stompClient.subscribe("/t/twitter", function(data) {
						var message = jQuery.parseJSON(data.body);
						//$("<div class=\"tweet first\"><p>" + message + "</p></div>").insertBefore( ".first" );
						$('#twitter_tweets li:eq(0)').before("<li class=\"tweet\"><p>"
								+ message.tweetMsg + "</p><p>"
								+ "<span class=\"time-ago scnd-font-color\">- @" + message.userName +" (" + message.date + ")</span>"
								+ "</p></li>");
						//messageList = $("#twitter_tweets div:first");
					});
				});
			});
</script>
</head>

<body>
	<div class="app-title-container">
		<h2 class="titular">TradeZone</h2>
	</div>
	<div class="main-container">

		<!-- LEFT-CONTAINER -->
		<div class="left-container container">
			<div class="menu-box block">
				<!-- MENU BOX (LEFT-CONTAINER) -->
				<h2 class="titular">
					<span class="icon zocial-pinboard"></span>&nbsp;&nbsp;&nbsp;Equity
					Options Information
				</h2>
				<br>
				<div class="input-container">
					<form action="<c:url value="/equity"/>" method="POST">
						<input type="text" name="equitySymbol" placeholder="Stock Symbol">
						<input type="submit" value="Search" class="subscribe button">
					</form>
				</div>
				<ul class="equity">
					<!-- SOCIAL (MIDDLE-CONTAINER) -->
					<li><h2 class="titular">Information</h2></li>
					<li>Symbol:</li>
				</ul>
			</div>
		</div>

		<!-- MIDDLE-CONTAINER -->
		<div class="middle-container container">
			<div class="tweets block">
				<!-- TWEETS (MIDDLE-CONTAINER) -->
				<h2 class="titular">
					<span class="icon zocial-twitter"></span>LATEST UOA TWEETS
				</h2>
				<ul class="scroll mCustomScrollbar" id="twitter_tweets">
				<c:forEach var="t" items="${tweets}">
					<li class="tweet">
						<p>
							<c:out value="${t.tweetMsg}"/>
						</p>
						<p>
							<span class="time-ago scnd-font-color">- @<c:out value="${t.userName}"/> (<c:out value="${t.date}"/>)</span>
						</p>
					</li>
				</c:forEach>
				</ul>
			</div>
		</div>

		<!-- RIGHT-CONTAINER -->
		<div class="right-container container">
			<div class="trade-alerts block">
				<!-- MENU BOX (LEFT-CONTAINER) -->
				<h2 class="titular">
					<span class="icon zocial-podcast"></span>&nbsp;&nbsp;&nbsp;Trade
					Alerts
				</h2>
				<br>
			</div>
		</div>
		<!-- end right-container -->
	</div>
	<!-- end main-container -->
</body>

</html>
