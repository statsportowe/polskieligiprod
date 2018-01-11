<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Polskie ligi</title>
</head>
<body>

	<form action="table" method="GET">
		Project id: <input type="text" name="projectId" /> 
		<input
			type="submit" value="Show table" />
	</form>
	
	<form action="importProject" method="GET">
		Project 90 minut id: <input type="text" name="projectId" /> 
		<input
			type="submit" value="Import project" />
	</form>
	
	<form action="importProjectsBatch" method="GET">
		Start: <input type="text" name="start" />
		End: <input type="text" name="end" />  
		<input
			type="submit" value="Start import batch" />
	</form>
	
	<a href="jobStatus">Job status</a>
	
</body>
</html>