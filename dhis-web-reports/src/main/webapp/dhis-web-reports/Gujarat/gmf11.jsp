
<%@ page import="java.sql.*,java.util.*" %>  
<%@ page import="com.opensymphony.xwork.util.OgnlValueStack" %>

<%@ page session="true"%>

<%
    Connection con=null;
            
    // For finding organisationunit name of selected Orgunit based on Orgunit id
    Statement st1=null;
    ResultSet rs1=null;
      
    // For finding start date of selected period based on period id
    Statement st2=null;
    ResultSet rs2=null;

    // For finding child orgunit ids and names based on selected orgunit id
    Statement st3=null;
    ResultSet rs3=null;

   // For finding child orgunit 
    Statement st4=null;
    ResultSet rs4=null;

  
    String userName = "dhis";      
    String password = "";           
    String urlForConnection = "jdbc:mysql://localhost/gj_dhis2";
          
	OgnlValueStack stack = (OgnlValueStack)request.getAttribute("webwork.valueStack");

	String selectedId = (String) stack.findValue( "orgUnitId" );
	int selectedOrgUnitID = 	Integer.parseInt( selectedId );
	
//	String selectedPeriodId = (String) stack.findValue( "periodSelect" );
//	int selectedDataPeriodID = 	Integer.parseInt( selectedPeriodId );

	String startingDate  = (String) stack.findValue( "startingPeriod" );
	String endingDate  = (String) stack.findValue( "endingPeriod" );
	
      
	String monthlyPeriodId = (String) stack.findValue( "monthlyPeriodTypeId" );
	int periodTypeID = 	Integer.parseInt( monthlyPeriodId );

      
  	String selectedOrgUnitName = "";  	
  	String selectedDataPeriodStartDate = ""; 
  	
  	String monthNames[] = { "", "January", "February", "March", "April", "May", "June", "July", "August", "September","October", "November", "December" };  		

	String monthlyDataElements[] = {										
										// Fever Treated
										"MAL_DE67",
										
										// BS Collected
										"MAL_DE2",
																				
  										// Positives
  										"MAL_DE11",	"MAL_DE12", "MAL_DE13", "MAL_DE14",

										// SPECIES - Pv, Pf R, Pf RG, Pm, Mixed 
	  									"MAL_DE23","MAL_DE24","MAL_DE25","MAL_DE27","MAL_DE28",
																						
										// Backlog Smears
										"MAL_DE68",  											
  											
									};
	
	List childOrgUnitIDs = new ArrayList();	
	List childOrgUnitNames = new ArrayList();	
		
  	int childOrgUnitCount = 0;  	
  	int count = 0;  	
  	int i=0;
  	int totPopulation = 0;
  	  	
	int tempval[] = new int[monthlyDataElements.length+5];
	int total[] = new int[monthlyDataElements.length+5];
%>

<%
     try
      {
        Class.forName ("com.mysql.jdbc.Driver").newInstance ();
        con = DriverManager.getConnection (urlForConnection, userName, password);
        
        st1=con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_READ_ONLY);
        st2=con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_READ_ONLY);
        st3=con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_READ_ONLY);
        st4=con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_READ_ONLY);

        //rs1 = st1.executeQuery("SELECT organisationunit.shortname FROM organisationunit WHERE id ="+selectedOrgUnitID);
        rs1 = st1.executeQuery("SELECT organisationunit.shortname FROM organisationunit WHERE organisationunitid ="+selectedOrgUnitID);        
        if(rs1.next())    {  selectedOrgUnitName = rs1.getString(1);     }
        else  {  selectedOrgUnitName = "";       }  
                
//       rs2 = st2.executeQuery("select startDate from period where id = "+selectedDataPeriodID);
//	    if(rs2.next())	{	selectedDataPeriodStartDate =  rs2.getDate(1).toString();	}  

		selectedDataPeriodStartDate = startingDate;
				
	    //rs3 =  st3.executeQuery("select id,shortname from organisationunit where parent = "+selectedOrgUnitID);
	    rs3 =  st3.executeQuery("select organisationunitid,shortname from organisationunit where parentid = "+selectedOrgUnitID);
	    while(rs3.next())
		 {
		  	Integer tempInt = new Integer(rs3.getInt(1));
		 	childOrgUnitIDs.add(childOrgUnitCount,tempInt);		 	
		 	childOrgUnitNames.add(childOrgUnitCount,rs3.getString(2));		 	
		 	childOrgUnitCount++;
		 } 
		 	  	 
	 } // try block end
    catch(Exception e)  { out.println(e.getMessage());  }

    String partsOfDataPeriodStartDate[] = selectedDataPeriodStartDate.split("-");	
    int lyear = Integer.parseInt(partsOfDataPeriodStartDate[0]) - 1;			     
%>


<HTML>
	<HEAD>
		<TITLE>M.F.:-11</TITLE>
	</HEAD>
	<BODY>					
	   <center>
			<b>
			<FONT face="Arial" size="3"><div align="right">M.F.11</div>SAVINGRAM FROM PRIMARY HEALTH CENTRE</font>								
        </center>      
        
		<table cellpadding="0" cellspacing="0" bgcolor="ffffff" align="center" style="border-style:solid; border-width:0; border-collapse: collapse; padding: 0; position:relative; top:10" bordercolor="#ffffff" width="100%"  border="0">
  			<tr style="border-collapse: collapse; border-left-style: solid; border-left-width: 0; border-right-style: solid; border-right-width: 0; padding: 0" bordercolor="#111111" width="100%">
   				<td width="50%" style="border-collapse: collapse; border-left-style: solid; border-left-width: 0; border-right-style: solid; border-right-width: 0; padding-left:15; padding-right:0; padding-top:0; padding-bottom:0" bordercolor="#111111"  height="23"><b><font face="Arial" size="2">Name of PHC&nbsp;:&nbsp;&nbsp;<%=selectedOrgUnitName%></b></font></td>
   				<td width="50%" align="right" style="border-collapse: collapse; border-left-style: solid; border-left-width: 0; border-right-style: solid; border-right-width: 0; padding-left:15; padding-right:0; padding-top:0; padding-bottom:0" bordercolor="#111111"  height="23"><font face="Arial" size="2"><b>Month :&nbsp;&nbsp;<%=monthNames[Integer.parseInt(partsOfDataPeriodStartDate[1])]%> - <%=partsOfDataPeriodStartDate[0]%> </b></font></td>   
  			</tr>
  			<tr style="border-collapse: collapse; border-left-style: solid; border-left-width: 0; border-right-style: solid; border-right-width: 0; padding: 0" bordercolor="#111111" width="100%">
   				<td width="50%" style="border-collapse: collapse; border-left-style: solid; border-left-width: 0; border-right-style: solid; border-right-width: 0; padding-left:15; padding-right:0; padding-top:0; padding-bottom:0" bordercolor="#111111"  height="23"><b><font face="Arial" size="2">Population&nbsp;:&nbsp;&nbsp;<%=totPopulation%></b></font></td>
   				<td width="50%" align="right" style="border-collapse: collapse; border-left-style: solid; border-left-width: 0; border-right-style: solid; border-right-width: 0; padding-left:15; padding-right:0; padding-top:0; padding-bottom:0" bordercolor="#111111"  height="23"><font face="Arial" size="2"></font></td>   
  			</tr>
		</table>
                               		
		<br>
		              
		<%
			
			try
			  {
				  count = 0;
				  while(count < childOrgUnitCount)
				   { 				   
				    if(count%16==0)
				     {
				       if(count != 0) {%></table><div align="right"><font face="Arial" size="1"><i>(page contd.)</i></font></div> <br><%}%>
				     </table><br>
				     <TABLE style="BORDER-COLLAPSE: collapse" borderColor=#111111 cellSpacing=3 cellPadding=3 width="100%" border=1>
  						<TR>
  							<TD align="center" width="3%" rowSpan=3><B><FONT face="Arial" size="2">Sl. No.</FONT></B></TD>
   							<TD align="center" width="43%" rowSpan=3><B><FONT face="Arial" size="2">Name of the Sub-Centre</FONT></B></TD>
   							<TD align="center" width="6%" rowspan="3"><b><font face="Arial" size="2">Fever Treated</font></b></TD>
   							<TD align="center" width="6%" rowspan="3"><b><font face="Arial" size="2">B.S. Collected</font></b></TD>
   							<TD align="center" width="6%" rowspan="3"><b><font face="Arial" size="2">Positives</font></b></TD>
   							<TD align="center" width="30%" colspan="5"><b><font face="Arial" size="2">SPECIES</font></b></TD>	
   							<TD align="center" width="6%" rowspan="3"><b><font face="Arial" size="2">Backlog Smears</font></b></TD>
    					</TR>
  						<TR>
   							<TD align="center" width="6%" rowspan="2"><b><font face="Arial" size="2">Pv</font></b></TD>
   							<TD align="center" width="6%" colspan="2"><b><font face="Arial" size="2">Pf</font></b></TD>
   							<TD align="center" width="6%" rowspan="2"><b><font face="Arial" size="2">Pm</font></b></TD>
   							<TD align="center" width="6%" rowspan="2"><b><font face="Arial" size="2">Mixed</font></b></TD>
    					</TR>
  						<TR>
   							<TD align="center" width="6%"><b><font face="Arial" size="2">R</font></b></TD>
   							<TD align="center" width="6%"><b><font face="Arial" size="2">RG</font></b></TD>
    					</TR>
		  				<TR>
    						<TD width="3%" HEIGHT="30" align="center"><FONT face="Arial" size="2"><b><i>1</i></b></font></TD>
    						<TD width="43%" HEIGHT="30" align="center"><FONT face="Arial" size="2"><b><i>2</i></b></font></TD>
    						<TD width="6%"  HEIGHT="30" align="center"><FONT face="Arial" size="2"><b><i>3</i></b></font></TD>
    						<TD width="6%"  HEIGHT="30" align="center"><FONT face="Arial" size="2"><b><i>4</i></b></font></TD>
    						<TD width="6%"  HEIGHT="30" align="center"><FONT face="Arial" size="2"><b><i>5</i></b></font></TD>
    						<TD width="6%"  HEIGHT="30" align="center"><FONT face="Arial" size="2"><b><i>6</i></b></font></TD>
    						<TD width="6%"  HEIGHT="30" align="center"><FONT face="Arial" size="2"><b><i>7</i></b></font></TD>
    						<TD width="6%"  HEIGHT="30" align="center"><FONT face="Arial" size="2"><b><i>8</i></b></font></TD>
    						<TD width="6%"  HEIGHT="30" align="center"><FONT face="Arial" size="2"><b><i>9</i></b></font></TD>
    						<TD width="6%"  HEIGHT="30" align="center"><FONT face="Arial" size="2"><b><i>10</i></b></font></TD>
    						<TD width="6%"  HEIGHT="30" align="center"><FONT face="Arial" size="2"><b><i>11</i></b></font></TD>
    					</TR>
	
			     <%}
					Integer temp1 = (Integer) childOrgUnitIDs.get(count);
					int currentChildID = temp1.intValue(); 			
					for(i=0;i<monthlyDataElements.length;i++)
					 {			
						//rs4 = st4.executeQuery("SELECT sum(datavalue.value) FROM datavalue INNER JOIN dataelement ON datavalue.dataElement = dataelement.id WHERE datavalue.period in (select id from period where startdate between '"+startingDate+"' and '"+startingDate+"') AND datavalue.source ="+currentChildID+" AND dataelement.code like '"+monthlyDataElements[i]+"'");
						rs4 = st4.executeQuery("SELECT sum(datavalue.value) FROM datavalue INNER JOIN dataelement ON datavalue.dataelementid = dataelement.dataelementid WHERE datavalue.periodid in (select periodid from period where startdate between '"+startingDate+"' and '"+startingDate+"') AND datavalue.sourceid ="+currentChildID+" AND dataelement.code like '"+monthlyDataElements[i]+"'");
												
						if(!rs4.next())  {  tempval[i] = 0;	 }
						else   {  tempval[i] = rs4.getInt(1);  }
						total[i] += tempval[i];
					 } 	
			   
				   %>			     				  		             		
  					
  					<TR>
    					<TD width="3%" HEIGHT="30" align="center"><FONT face="Arial" size="2"><%=(count+1)%></font></TD>
    					<TD width="43%" HEIGHT="30" align="center"><FONT face="Arial" size="2"><%=childOrgUnitNames.get(count)%></font></TD>
    					<TD width="6%"  HEIGHT="30" align="center"><FONT face="Arial" size="2"><%=tempval[0]%></font></TD>
    					<TD width="6%"  HEIGHT="30" align="center"><FONT face="Arial" size="2"><%=tempval[1]%></font></TD>
    					<TD width="6%"  HEIGHT="30" align="center"><FONT face="Arial" size="2"><%=(tempval[2]+tempval[3]+tempval[4]+tempval[5])%></font></TD>
    					<TD width="6%"  HEIGHT="30" align="center"><FONT face="Arial" size="2"><%=tempval[6]%></font></TD>
    					<TD width="6%"  HEIGHT="30" align="center"><FONT face="Arial" size="2"><%=tempval[7]%></font></TD>
    					<TD width="6%"  HEIGHT="30" align="center"><FONT face="Arial" size="2"><%=tempval[8]%></font></TD>
    					<TD width="6%"  HEIGHT="30" align="center"><FONT face="Arial" size="2"><%=tempval[9]%></font></TD>
    					<TD width="6%"  HEIGHT="30" align="center"><FONT face="Arial" size="2"><%=tempval[10]%></font></TD>
    					<TD width="6%"  HEIGHT="30" align="center"><FONT face="Arial" size="2"><%=tempval[11]%></font></TD>
    				</TR>
    				<% 					
    					count++;
  				   }	// while loop end
  			  } // try block end
  			catch(Exception e)  { out.println(e.getMessage());  }
     		finally
       		 {
				try
				 {
  					if(rs1!=null)  rs1.close();		if(st1!=null)  st1.close();
  					if(rs2!=null)  rs2.close();		if(st2!=null)  st2.close();
  					if(rs3!=null)  rs3.close();		if(st3!=null)  st3.close();  					
  					if(rs4!=null)  rs4.close();		if(st4!=null)  st4.close();				
  				
					if(con!=null)  con.close();
				 }
				catch(Exception e)   {  out.println(e.getMessage());   }
       		 } // finally block end		          		
  		%>
            <tr>
				<TD width="3%" HEIGHT="30" align="center"><FONT face="Arial" size="1">&nbsp;</font></TD>
				<TD width="43%" HEIGHT="30" align="center"><FONT face="Arial" size="2"><b>TOTAL</b></font></TD>
				<TD width="6%"  HEIGHT="30" align="center"><FONT face="Arial" size="1"><%=total[0]%></font></TD>
				<TD width="6%"  HEIGHT="30" align="center"><FONT face="Arial" size="1"><%=total[1]%></font></TD>
				<TD width="6%"  HEIGHT="30" align="center"><FONT face="Arial" size="1"><%=(total[2]+total[3]+total[4]+total[5])%></font></TD>
				<TD width="6%"  HEIGHT="30" align="center"><FONT face="Arial" size="1"><%=total[6]%></font></TD>
				<TD width="6%"  HEIGHT="30" align="center"><FONT face="Arial" size="1"><%=total[7]%></font></TD>
				<TD width="6%"  HEIGHT="30" align="center"><FONT face="Arial" size="1"><%=total[8]%></font></TD>
				<TD width="6%"  HEIGHT="30" align="center"><FONT face="Arial" size="1"><%=total[9]%></font></TD>
				<TD width="6%"  HEIGHT="30" align="center"><FONT face="Arial" size="1"><%=total[10]%></font></TD>
				<TD width="6%"  HEIGHT="30" align="center"><FONT face="Arial" size="1"><%=total[11]%></font></TD>
			</tr>

		</TABLE>
 	   </b>
       <p dir="ltr">&nbsp;</p>
 	</BODY>
</HTML>