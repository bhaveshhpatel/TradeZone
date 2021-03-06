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
<link rel="shortcut icon" type="image/png"
	href="//cdn.jsdelivr.net/jquery.mcustomscrollbar/3.0.6/mCSB_buttons.png">
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
	$(document)
			.ready(
					function() {
						//var messageList = $('#twitter_tweets li:eq(0)');

						// defined a connection to a new socket endpoint
						var socket = new SockJS('/stomp');

						var stompClient = Stomp.over(socket);

						stompClient
								.connect(
										{},
										function(frame) {
											// subscribe to the /twitter endpoint
											stompClient
													.subscribe(
															"/t/twitter",
															function(data) {
																var message = jQuery
																		.parseJSON(data.body);
																//$("<div class=\"tweet first\"><p>" + message + "</p></div>").insertBefore( ".first" );
																$(
																		'#twitter_tweets li:eq(0)')
																		.before(
																				"<li class=\"tweet\"><p>"
																						+ message.tweetMsg
																						+ "</p><p>"
																						+ "<span class=\"time-ago scnd-font-color\">- @"
																						+ message.userName
																						+ " ("
																						+ message.date
																						+ ")</span>"
																						+ "</p></li>");
																//messageList = $("#twitter_tweets div:first");
															});
											stompClient
													.subscribe(
															"/t/trade",
															function(data) {
																var trade = jQuery
																		.parseJSON(data.body);
																$(
																		"#tradesTable tr:first")
																		.after(
																				"<tr><td>"
																						+ trade.symbol
																						+ "</td><td>"
																						+ trade.equityName
																						+ "</td><td>"
																						+ trade.expiration
																						+ "</td><td>"
																						+ trade.strike
																						+ "</td><td>"
																						+ trade.option
																						+ "</td><td>"
																						+ trade.action
																						+ "</td><td>"
																						+ trade.volume
																						+ "</td><td>"
																						+ trade.avgDailyOptionsVol
																						+ "</td><td>"
																						+ trade.multipleOfDailyOptionsVol
																						+ "</td><td>"
																						+ trade.avgDailyStockVol
																						+ "</td><td>"
																						+ trade.percentOfStockVol
																						+ "</td></tr>");
															});
											stompClient
											.subscribe(
													"/t/alert",
													function(data) {
														var alert = jQuery
																.parseJSON(data.body);
														$(
														'#alerts li:eq(0)')
														.before(
																"<li class=\"tweet\"><p>"
																//if ((alert.option.equals('Calls') && alert.action.equals('BUYING')) || (alert.option.equals('Puts') && alert.action.equals('SELLING'))) {
																//	"<img src=\"/images/bull.png\"></img>"
																//} else {
																//	"<img src=\"/images/bear.png\"></img>"
																//}
																		+ alert.volume + "&nbsp;" + alert.symbol + "&nbsp;" + alert.expiration + "&nbsp;" + alert.strike + "&nbsp;" + alert.option + "&nbsp;" + alert.action
																		+ "</p><p><span class=\"time-ago scnd-font-color\">- "
																		+ alert.date
																		+ "</span>"
																		+ "</p></li>");
													});
											stompClient
											.subscribe(
													"/t/equity",
													function(data) {
														var equity = jQuery
																.parseJSON(data.body);
														//$("<div class=\"tweet first\"><p>" + message + "</p></div>").insertBefore( ".first" );
														$('#symbol').html(equity.symbol);
														$('#name').html(equity.name);
														$('#avg_daily_vol').html(equity.AverageDailyVolume);
														$('#market_cap').html(equity.MarketCapitalization);
														$('#year_range').html(equity.YearLow + " - " + equity.YearHigh);
														$('#open').html(equity.Open);
														$('#previous_close').html(equity.PreviousClose);
													});
										});
					});
</script>
</head>

<body>
	<div class="app-title-container">
		<img src="<c:url value="/images/TradeZoneBanner.jpg" />"></img>
	</div>
	<div class="main-container">

		<!-- LEFT-CONTAINER -->
		<div class="left-container container">
			<div class="menu-box block">
				<!-- MENU BOX (LEFT-CONTAINER) -->
				<h2 class="titular">
					<span class="icon zocial-pinboard"></span>&nbsp;&nbsp;&nbsp;EQUITY
					OPTIONS INFORMATION
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
					<li><h2 class="titular">DETAILS</h2></li>
						<li><span class="equityitem">SYMBOL : </span><span id="symbol"><c:out value="${equity.symbol}" /></span></li>
						<li><span class="equityitem">NAME : </span><span id="name"><c:out value="${equity.name}" /></span></li>
						<li><span class="equityitem">AVG VOL : </span><span id="avg_daily_vol"><c:out value="${equity.AverageDailyVolume}" /></span></li>
						<li><span class="equityitem">MARKET CAP : </span><span id="market_cap"><c:out value="${equity.MarketCapitalization}" /></span></li>
						<li><span class="equityitem">YEAR RANGE : </span><span id="year_range"><c:out value="${equity.YearLow}" /> - <c:out value="${equity.YearHigh}" /></span></li>
						<li><span class="equityitem">OPEN : </span><span id="open"><c:out value="${equity.Open}" /></span></li>
						<li><span class="equityitem">PREVIOUS CLOSE : </span><span id="previous_close"><c:out value="${equity.PreviousClose}" /></span></li>
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
								<c:out value="${t.tweetMsg}" />
							</p>
							<p>
								<span class="time-ago scnd-font-color">- @<c:out
										value="${t.userName}" /> (<c:out value="${t.date}" />)
								</span>
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
					<span class="icon zocial-podcast"></span>&nbsp;&nbsp;&nbsp;TRADE
					ALERTS
				</h2>
				<ul class="scroll mCustomScrollbar" id="alerts">
					<c:forEach var="a" items="${alerts}">
						<li class="tweet">
							<p>
							<!--
								<c:choose>
									<c:when test="${(a.option eq 'Calls' && a.action eq 'BUYING') || (a.option eq 'Puts' && a.action eq 'SELLING')}">
									<img src="<c:url value="/images/bull.png" />"></img>
									</c:when>
									<c:otherwise>
									<img src="<c:url value="/images/bear.png" />"></img>
									</c:otherwise>
								</c:choose>
								-->
								<c:out value="${a.volume}" />&nbsp;<c:out value="${a.symbol}" />&nbsp;<c:out value="${a.expiration}" />&nbsp;<c:out value="${a.strike}" />&nbsp;<c:out value="${a.option}" />&nbsp;<c:out value="${a.action}" />
							</p>
							<p>
								<span class="time-ago scnd-font-color">- <c:out
										value="${a.date}" />
								</span>
							</p>
						</li>
					</c:forEach>
				</ul>
				<br>
			</div>
		</div>
		<!-- end right-container -->
	</div>
	<!-- end main-container -->
	<div class="lower-container container">
		<div class="tweets block">
			<!-- TWEETS (MIDDLE-CONTAINER) -->
			<h2 class="titular">
				<span class="icon zocial-stumbleupon"></span>TRADE ANALYZER
			</h2>
			<div class="scroll mCustomScrollbar">
			<table class="tradeTable" id="tradesTable">
				<tr class="tradeTableHeader">
					<th>SYMBOL</th>
					<th>EQUITY NAME</th>
					<th>EXPIRATION</th>
					<th>STRIKE</th>
					<th>OPTION</th>
					<th>ACTION</th>
					<th>VOLUME</th>
					<th>AVG DAILY OPTIONS VOL</th>
					<th>MULTIPLE OF DAILY OPTIONS VOL</th>
					<th>AVG DAILY STOCK VOL</th>
					<th>PERCENT OF DAILY STOCK VOL</th>
				</tr>
				<c:forEach var="trade" items="${trades}">
					<tr>
						<td><c:out value="${trade.symbol}" /></td>
						<td><c:out value="${trade.equityName}" /></td>
						<td><c:out value="${trade.expiration}" /></td>
						<td><c:out value="${trade.strike}" /></td>
						<td><c:out value="${trade.option}" /></td>
						<td><c:out value="${trade.action}" /></td>
						<td><c:out value="${trade.volume}" /></td>
						<td><c:out value="${trade.avgDailyOptionsVol}" /></td>
						<td><c:out value="${trade.multipleOfDailyOptionsVol}" /></td>
						<td><c:out value="${trade.avgDailyStockVol}" /></td>
						<td><c:out value="${trade.percentOfStockVol}" /></td>
					</tr>
				</c:forEach>
			</table>
			</div>
		</div>
	</div>
</body>

</html>
