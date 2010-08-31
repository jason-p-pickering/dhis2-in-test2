// Removes slected orgunits from the Organisation List
function remOUFunction()
{
    var index = document.ChartGenerationForm.orgUnitListCB.options.length;
    var i=0;
    for(i=index-1;i>=0;i--)
    {
        if(document.ChartGenerationForm.orgUnitListCB.options[i].selected)
            document.ChartGenerationForm.orgUnitListCB.options[i] = null;
    }
}// remOUFunction end

function getImmChildInfo(evt, dsId, selOrgUnit)
{

    immChildOption = "yes";
    
    var urlForImmChild = "dataStatusResult.action?immChildOption="+immChildOption+"&dsId="+dsId+"&sDateLB="+startDate+"&eDateLB="+endDate+"&ouId="+selOrgUnit;
    		
    evt.target.href = "dataStatusResult.action?immChildOption="+immChildOption+"&dsId="+dsId+"&sDateLB="+startDate+"&eDateLB="+endDate+"&ouId="+selOrgUnit;
}

function getImmChildInfo1(evt, dsId, selOrgUnit)
{

    alert("urlForImmChild");
}
			
function exportDataStatusResultToWorkBook()
{			    
    document.getElementById('htmlCode').value = document.getElementById('formResult').innerHTML;
		    			
    return true;
}

// Category ListBox Change function
function categoryChangeFunction(evt)
{
    selCategory = evt.target.value;
    if(selCategory == "period")
    {
        document.ChartGenerationForm.facilityLB.disabled = true;
        var index = document.ChartGenerationForm.orgUnitListCB.options.length;
        for(i=0;i<index;i++)
        {
            document.ChartGenerationForm.orgUnitListCB.options[0] = null;
        }
    }
    else
    {
        document.ChartGenerationForm.facilityLB.disabled = false;
    }
}// categoryChangeFunction end
			          
//Facility ListBox Change Function
function facilityChangeFunction(evt)
{
    selFacility = evt.target.value;
    if(selFacility == "children" || selFacility == "immChildren")
    {
        var index = document.ChartGenerationForm.orgUnitListCB.options.length;
        for(i=0;i<index;i++)
        {
            document.ChartGenerationForm.orgUnitListCB.options[0] = null;
        }
    }
}// facilityChangeFunction end

function textvalue(summary)
{
  
    document.getElementById("selectedButton").value = summary;
  
    if(formValidationsForDataStatus())
    {
        if(summary == "SummaryStatus")
        {
            //document.getElementById("facilityLB").options[1].selected = true;
            document.ChartGenerationForm.action = "summaryStatusResult.action";
            document.ChartGenerationForm.submit();
        }
        else if(summary == "GroupWiseStatus")
        {
            document.ChartGenerationForm.action = "groupWiseDataStatusResult.action";
            document.ChartGenerationForm.submit();
  	 	
        }
        else if(summary == "ValidationStatus")
        {
            document.ChartGenerationForm.action = "validationStatusResult.action";
            document.ChartGenerationForm.submit();
        }
        else if(summary == "LastUpdatedStatus")
        {
            document.ChartGenerationForm.action = "lastUpdatedDataSetResult.action";
            document.ChartGenerationForm.submit();
        }
        else
        {
  	 	
            document.ChartGenerationForm.action = "dataStatusResult.action";
            document.ChartGenerationForm.submit();
  	 	
        }
    }
  	 
}


// DataStatus Form Validations
function formValidationsForDataStatus()
{
    var selOUListIndex = document.ChartGenerationForm.orgUnitListCB.options.length;
    var selDSListSize  = document.ChartGenerationForm.selectedDataSets.options.length;
    //var orgunitIdValue = document.ChartGenerationForm.ouIDTB.value;
    sDateIndex    = document.ChartGenerationForm.sDateLB.selectedIndex;
    eDateIndex    = document.ChartGenerationForm.eDateLB.selectedIndex;
    sDateTxt = document.ChartGenerationForm.sDateLB.options[sDateIndex].text;
    sDate = formatDate(new Date(getDateFromFormat(sDateTxt,"MMM-y")),"yyyy-MM-dd");
    eDateTxt = document.ChartGenerationForm.eDateLB.options[eDateIndex].text;
    eDate = formatDate(new Date(getDateFromFormat(eDateTxt,"MMM-y")),"yyyy-MM-dd");

    if(selOUListIndex <= 0) {
        alert("Please Select OrganisationUnit(s)"); return false;
    }
    else if(selDSListSize <= 0) {
        alert("Please Select DataSet(s)"); return false;
    }
    else if(sDateIndex < 0) {
        alert("Please Select Starting Period"); return false;
    }
    else if(eDateIndex < 0) {
        alert("Please Select Ending Period"); return false;
    }
    else if(sDate > eDate) {
        alert("Starting Date is Greater"); return false;
    }
    // else if(orgunitIdValue == null || orgunitIdValue == "") {alert("Please Select OrganisationUnit"); return false;}

    var k=0;
	
    for(k=0;k<selOUListIndex;k++)
    {
        document.ChartGenerationForm.orgUnitListCB.options[k].selected = true;
    }

    var sWidth = 850;
    var sHeight = 650;
    var LeftPosition=(screen.width)?(screen.width-sWidth)/2:100;
    var TopPosition=(screen.height)?(screen.height-sHeight)/2:100;

    window.open('','chartWindow1','width=' + sWidth + ', height=' + sHeight + ', ' + 'left=' + LeftPosition + ', top=' + TopPosition + ', ' + 'location=no, menubar=no, ' +  'status=no, toolbar=no, scrollbars=yes, resizable=yes');
  	
  	 	
    return true;

} // formValidations Function End	


//DataStatus Form Validations
function formValidationsForOUwiseDataStatus()
{
    var selOUListLength = document.ChartGenerationForm.orgUnitListCB.options.length;
    var selOUListIndex = document.ChartGenerationForm.orgUnitListCB.selectedIndex;
    var selDSListSize  = document.ChartGenerationForm.selectedDataSets.options.length;
    var orgunitIdValue = document.ChartGenerationForm.ouIDTB.value;
    sDateIndex    = document.ChartGenerationForm.sDateLB.selectedIndex;
    eDateIndex    = document.ChartGenerationForm.eDateLB.selectedIndex;
    sDateTxt = document.ChartGenerationForm.sDateLB.options[sDateIndex].text;
    sDate = formatDate(new Date(getDateFromFormat(sDateTxt,"MMM-y")),"yyyy-MM-dd");
    eDateTxt = document.ChartGenerationForm.eDateLB.options[eDateIndex].text;
    eDate = formatDate(new Date(getDateFromFormat(eDateTxt,"MMM-y")),"yyyy-MM-dd");
	
    if(selOUListLength <= 0 || selOUListIndex < 0 ) {
        alert("Please Select OrganisationUnitGroup"); return false;
    }
    else if(selDSListSize <= 0) {
        alert("Please Select DataSet(s)"); return false;
    }
    else if(sDateIndex < 0) {
        alert("Please Select Starting Period"); return false;
    }
    else if(eDateIndex < 0) {
        alert("Please Select Ending Period"); return false;
    }
    else if(sDate > eDate) {
        alert("Starting Date is Greater"); return false;
    }
    else if(orgunitIdValue == null || orgunitIdValue == "") {
        alert("Please Select OrganisationUnit"); return false;
    }

    /*
	var k=0;
	
	 for(k=0;k<selOUListIndex;k++)
	 {
	 	document.ChartGenerationForm.orgUnitListCB.options[k].selected = true;
	 }
	 */
    var sWidth = 850;
    var sHeight = 650;
    var LeftPosition=(screen.width)?(screen.width-sWidth)/2:100;
    var TopPosition=(screen.height)?(screen.height-sHeight)/2:100;
	
    window.open('','chartWindow1','width=' + sWidth + ', height=' + sHeight + ', ' + 'left=' + LeftPosition + ', top=' + TopPosition + ', ' + 'location=no, menubar=no, ' +  'status=no, toolbar=no, scrollbars=yes, resizable=yes');
		
    return true;

} // formValidations Function End	


// 
//
//  Getting corresponding Period List for Datasets. 
function getdSetPeriods()
{
    var dataSetList = document.getElementById("selectedDataSets");
    var dataSetId = dataSetList.options[ dataSetList.selectedIndex].value;

  
    //var url = "getDataSetPeriods.action?id=" + dataSetId;
    
    var request = new Request();
    request.setResponseTypeXML( 'period' );
    request.setCallbackSuccess( getdSetPeriodsReceived );
    //request.send( url );

    var requestString = "getDataSetPeriods.action";
    var params = "id=" + dataSetId;
    request.sendAsPost( params );
    request.send( requestString );
 
}	 



function getdSetPeriodsReceived( xmlObject )
{	
    var sDateLB = document.getElementById( "sDateLB" );
    var eDateLB = document.getElementById( "eDateLB" );
		
    var periods = xmlObject.getElementsByTagName( "period" );
	
    for ( var i = 0; i < periods.length; i++)
    {
        var periodType = periods[ i ].getElementsByTagName( "periodtype" )[0].firstChild.nodeValue;
		
        if(i ==0 )
        {
            if( periodType == curPeriodType )
            {
                break;
            }
            else
            {
                curPeriodType = periodType;
                clearList( sDateLB );
                clearList( eDateLB );
            }
        }
				
        var id = periods[ i ].getElementsByTagName( "id" )[0].firstChild.nodeValue;
        var periodName = periods[ i ].getElementsByTagName( "periodname" )[0].firstChild.nodeValue;
							
        var option1 = document.createElement( "option" );
        option1.value = id;
        option1.text = periodName;
        sDateLB.add( option1, null );
			
        var option2 = document.createElement( "option" );
        option2.value = id;
        option2.text = periodName;
        eDateLB.add( option2, null);
				
    }
		
}

function getOrgUDetails(orgUnitIds)
{
    //var url = "getOrgUnitDetails.action?orgUnitId=" + orgUnitIds;
	
    var request = new Request();
    request.setResponseTypeXML( 'orgunit' );
    request.setCallbackSuccess( getOrgUDetailsRecevied );
    //request.send( url );

    var requestString = "getOrgUnitDetails.action";
    var params = "orgUnitId=" + orgUnitIds;
    request.sendAsPost( params );
    request.send( requestString );

//getReports();
}

function getOrgUDetailsRecevied(xmlObject)
{
		
    var orgUnits = xmlObject.getElementsByTagName("orgunit");

    for ( var i = 0; i < orgUnits.length; i++ )
    {
        var id = orgUnits[ i ].getElementsByTagName("id")[0].firstChild.nodeValue;
        var orgUnitName = orgUnits[ i ].getElementsByTagName("name")[0].firstChild.nodeValue;
        var level = orgUnits[ i ].getElementsByTagName("level")[0].firstChild.nodeValue;
		
		
        document.ChartGenerationForm.ouNameTB.value = orgUnitName;
    //document.reportForm.ouLevelTB.value = level;
    }    		
}