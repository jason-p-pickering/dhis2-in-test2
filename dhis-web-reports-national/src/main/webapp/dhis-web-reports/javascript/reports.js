
function getOUDetails(orgUnitIds)
{
	var url = "getOrgUnitDetails.action?orgUnitId=" + orgUnitIds;
	
	var request = new Request();
	request.setResponseTypeXML( 'orgunit' );
	request.setCallbackSuccess( getOUDetailsRecevied );
	request.send( url );

	getReports();
}

function getOUDetailsForOuWiseProgressReport(orgUnitIds)
{
	var url = "getOrgUnitDetails.action?orgUnitId=" + orgUnitIds;

	var request = new Request();
	request.setResponseTypeXML( 'orgunit' );
	request.setCallbackSuccess( getOUDetailsRecevied );
	request.send( url );

}

function getOUDetailsRecevied(xmlObject)
{
		
	var orgUnits = xmlObject.getElementsByTagName("orgunit");

    for ( var i = 0; i < orgUnits.length; i++ )
    {
        var id = orgUnits[ i ].getElementsByTagName("id")[0].firstChild.nodeValue;
        var orgUnitName = orgUnits[ i ].getElementsByTagName("name")[0].firstChild.nodeValue;
		var level = orgUnits[ i ].getElementsByTagName("level")[0].firstChild.nodeValue;
		
		
		document.reportForm.ouNameTB.value = orgUnitName;
		document.reportForm.ouLevelTB.value = level;	
    }    		
}

//--------------------------------------
//
//--------------------------------------
function getDataElements()
{
    var dataElementGroupList = document.getElementById("dataElementGroupId");
    var dataElementGroupId = dataElementGroupList.options[ dataElementGroupList.selectedIndex ].value;
        
    if ( dataElementGroupId != null )
    {
        var url = "getDataElements.action?id=" + dataElementGroupId;
        var request = new Request();
        request.setResponseTypeXML('dataElement');
        request.setCallbackSuccess(getDataElementsReceived);
        request.send(url);
    }
}// getDataElements end           

function getDataElementsReceived( xmlObject )
{
    var availableDataElements = document.getElementById("availableDataElements");
    var selectedDataElements = document.getElementById("selectedDataElements");

    clearList(availableDataElements);

    var dataElements = xmlObject.getElementsByTagName("dataElement");

    for ( var i = 0; i < dataElements.length; i++ )
    {
        var id = dataElements[ i ].getElementsByTagName("id")[0].firstChild.nodeValue;
        var dataElementName = dataElements[ i ].getElementsByTagName("name")[0].firstChild.nodeValue;
        if ( listContains(selectedDataElements, id) == false )
        {
            var option = document.createElement("option");
            option.value = id;
            option.text = dataElementName;
            option.title = dataElementName;
            availableDataElements.add(option, null);
        }
    }    
}// getDataElementsReceived end

//---------------------------------------------------------------
// Get Periods 
//---------------------------------------------------------------

function getPeriods()
{
  //document.reportForm.generate.disabled=false;
  var periodTypeList = document.getElementById( "periodTypeId" );
  var periodTypeId = periodTypeList.options[ periodTypeList.selectedIndex ].value;
  var availablePeriods = document.getElementById( "availablePeriods" );
  var reportsList = document.getElementById( "reportList" );
  
  if ( periodTypeId != "NA" )
  {   
    var url = "getPeriods.action?id=" + periodTypeId;
    
    var request = new Request();
      request.setResponseTypeXML( 'period' );
      request.setCallbackSuccess( getPeriodsReceived );
      request.send( url );
      document.reportForm.generate.disabled=false;
      var ouId = document.reportForm.ouIDTB.value;
      var reportListFileName = document.reportForm.reportListFileNameTB.value;
  
      getReports(ouId, reportListFileName);
  }
  else
  {
    
      document.reportForm.generate.disabled=true;
      clearList( availablePeriods );
      clearList( reportsList );

  }
  
}

function getPeriodsForCumulative()
{
	//document.reportForm.generate.disabled=false;
	var periodTypeList = document.getElementById( "periodTypeId" );
	var periodTypeId = periodTypeList.options[ periodTypeList.selectedIndex ].value;
	var reportsList = document.getElementById( "reportList" );
	
	if ( periodTypeId != "NA" )
  {   
   	   var ouId = document.reportForm.ouIDTB.value;
       var reportListFileName = document.reportForm.reportListFileNameTB.value;
  
       getReports(ouId, reportListFileName);
       document.reportForm.generate.disabled=false;
  }
  else
  {
    
      document.reportForm.generate.disabled=true;
      clearList( reportsList );
      jQuery("#startDate").val("");
      jQuery("#endDate").val("");
      document.reportForm.startDate = "";
      document.reportForm.endDate = " ";
  }
 
}

function getReports( ouId, reportListFileName )
{ 
  var periodTypeList = document.getElementById( "periodTypeId" );
  var periodType = periodTypeList.options[ periodTypeList.selectedIndex ].value;
  var autogenvalue = document.getElementById( "autogen" ).value;
          
  if ( periodType != "NA" && ouId != null && ouId != "" )
  {   
    var url = "getReports.action?periodType=" + periodType + "&ouId="+ouId + "&reportListFileName="+reportListFileName+"&autogenrep="+autogenvalue;
    
    var request = new Request();
      request.setResponseTypeXML( 'report' );
      request.setCallbackSuccess( getReportsReceived );
      request.send( url );
  }
}

function getReportsReceived( xmlObject )
{	
    var reportsList = document.getElementById( "reportList" );
	var orgUnitName = document.getElementById( "ouNameTB" );
    
    clearList( reportsList );
    
    var reports = xmlObject.getElementsByTagName( "report" );
    for ( var i = 0; i < reports.length; i++)
	{
		var id = reports[ i ].getElementsByTagName( "id" )[0].firstChild.nodeValue;
		var name = reports[ i ].getElementsByTagName( "name" )[0].firstChild.nodeValue;
		var model = reports[ i ].getElementsByTagName( "model" )[0].firstChild.nodeValue;
		var fileName = reports[ i ].getElementsByTagName( "fileName" )[0].firstChild.nodeValue;
		var ouName = reports[ i ].getElementsByTagName( "ouName" )[0].firstChild.nodeValue;
	
		orgUnitName.value = ouName;			
	
		var option = document.createElement( "option" );
		option.value = id;
		option.text = name;
		reportsList.add( option, null );
		
		reportModels.put(id,model);
		reportFileNames.put(id,fileName);
	}
}

function getPeriodsReceived( xmlObject )
{	
	var availablePeriods = document.getElementById( "availablePeriods" );
	var selectedPeriods = document.getElementById( "selectedPeriods" );
	
	
	
	clearList( availablePeriods );
	
	var periods = xmlObject.getElementsByTagName( "period" );
	//document.reportForm.generate.disabled=false;
	if(periods.length <= 0)
	document.reportForm.generate.disabled=true;
	
	for ( var i = 0; i < periods.length; i++)
	{
		var id = periods[ i ].getElementsByTagName( "id" )[0].firstChild.nodeValue;
		//var startDate = periods[ i ].getElementsByTagName( "startDate" )[0].firstChild.nodeValue;
		//var endDate = periods[ i ].getElementsByTagName( "endDate" )[0].firstChild.nodeValue;		
		var periodName = periods[ i ].getElementsByTagName( "periodname" )[0].firstChild.nodeValue;
		
		if ( listContains( selectedPeriods, id ) == false )
		{						
			var option = document.createElement( "option" );
			option.value = id;
			option.text = periodName;
			availablePeriods.add( option, null );
		}			
	}
	
	// If the list of available periods is empty, an empty placeholder will be added
	addOptionPlaceHolder( availablePeriods );
}



// -----------------------------------------------------------------------------
// Date Validation
// -----------------------------------------------------------------------------

// Declaring valid date character, minimum year and maximum year
var dtCh= "-";
var minYear=1900;
var maxYear=2100;

function isInteger(s)
{
  var i;
  for (i = 0; i < s.length; i++)
  {   
    // Check that current character is number.
    var c = s.charAt(i);
    if (((c < "0") || (c > "9"))) return false;
  }
  // All characters are numbers.
  return true;
}

function stripCharsInBag(s, bag)
{
  var i;
  var returnString = "";
  
  // Search through string's characters one by one.
  // If character is not in bag, append to returnString.
  for (i = 0; i < s.length; i++)
  {   
    var c = s.charAt(i);
    if (bag.indexOf(c) == -1) returnString += c;
  }
  
  return returnString;
}

function daysInFebruary (year)
{
  // February has 29 days in any year evenly divisible by four,
  // EXCEPT for centurial years which are not also divisible by 400.
  
  return (((year % 4 == 0) && ( (!(year % 100 == 0)) || (year % 400 == 0))) ? 29 : 28 );
}

function DaysArray(n) 
{
  for (var i = 1; i <= n; i++) 
  {
    this[i] = 31
    if (i==4 || i==6 || i==9 || i==11) {this[i] = 30}
    if (i==2) {this[i] = 29}
  } 
  
  return this
}

function isDate(dtStr)
{
  var daysInMonth = DaysArray(12)
  var pos1=dtStr.indexOf(dtCh)
  var pos2=dtStr.indexOf(dtCh,pos1+1)

  var strYear=dtStr.substring(0,pos1)
  var strMonth=dtStr.substring(pos1+1,pos2)
  var strDay=dtStr.substring(pos2+1)

  //var strMonth=dtStr.substring(0,pos1)
  //var strDay=dtStr.substring(pos1+1,pos2)
  //var strYear=dtStr.substring(pos2+1)
  strYr=strYear
  if (strDay.charAt(0)=="0" && strDay.length>1) strDay=strDay.substring(1)
  if (strMonth.charAt(0)=="0" && strMonth.length>1) strMonth=strMonth.substring(1)
  for (var i = 1; i <= 3; i++) 
  {
    if (strYr.charAt(0)=="0" && strYr.length>1) strYr=strYr.substring(1)
  }
  month=parseInt(strMonth)
  day=parseInt(strDay)
  year=parseInt(strYr)
  if (pos1==-1 || pos2==-1)
  {
    alert("The date format should be : yyyy-mm-dd")
    return false
  }
  
  if (strMonth.length<1 || month<1 || month>12)
  {
    alert("Please enter a valid month")
    return false
  }
  if (strDay.length<1 || day<1 || day>31 || (month==2 && day>daysInFebruary(year)) || day > daysInMonth[month])
  {
    alert("Please enter a valid day")
    return false
  }
  if (strYear.length != 4 || year==0 || year<minYear || year>maxYear)
  {
    alert("Please enter a valid 4 digit year between "+minYear+" and "+maxYear)
    return false
  }
  if (dtStr.indexOf(dtCh,pos2+1)!=-1 || isInteger(stripCharsInBag(dtStr, dtCh))==false)
  {
    alert("Please enter a valid date")
    return false
  }

  return true
}

function isInteger(s)
{
  var n = trim(s);
  return n.length > 0 && !(/[^0-9]/).test(n);
}

// -----------------------------------------------------------------------------
// String Trim
// -----------------------------------------------------------------------------

function trim( stringToTrim ) 
{
  return stringToTrim.replace(/^\s+|\s+$/g,"");
}
