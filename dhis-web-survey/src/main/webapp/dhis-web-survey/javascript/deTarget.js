
// -----------------------------------------------------------------------------
// Delete Survey
// -----------------------------------------------------------------------------

var tmpSurveyId;

var tmpSource;

function removeSurvey( surveyId, surveyName )
{
  var result = window.confirm( i18n_confirm_delete + '\n\n' + " Survey Id =" + surveyId + '\n\n' + " Survey Name ="  + surveyName );

  if ( result )
  {
	$.post("delSurvey.action",
		{
			surveyId : surveyId
		},
		function (data)
		{
			removeSurveyCompleted(data);
		},'xml');
		
  }
}

function removeSurveyCompleted( messageElement )
{
	messageElement = messageElement.getElementsByTagName( "message" )[0];
	var type = messageElement.getAttribute( "type" );
  
    if ( type == 'error' )
   {
    	var message = messageElement.firstChild.nodeValue;
    	//alert( message );
		//setFieldValue( 'warningField', message );
		setInnerHTML( 'warningField', message );
		showWarning();
   }
   else
   {
		window.location.href = 'index.action';
   }
}

//validation for adding New DeTarget
function validateAddDeTarget()
{
	$.post("validateDeTarget.action",
		{
			name :  byId( 'name' ).value,
			shortName : byId( 'shortName' ).value
		},
		function (data)
		{
			addSurveyValidationCompleted(data);
		},'xml');
		
  return false;
}

function addSurveyValidationCompleted( messageElement )
{
  	messageElement = messageElement.getElementsByTagName( "message" )[0];
	var type = messageElement.getAttribute( "type" );
	var message = messageElement.firstChild.nodeValue;

	if ( type == 'success' )
	{
		var selectedList = document.getElementById( 'selectedList' );
		for(var k=0;k<selectedList.length;k++)
		{
			selectedList.options[k].selected = "true";
		}  
		document.forms['addDeTargetForm'].submit();
	} 
    else if ( type == 'input' )
    {
		setMessage( message );
	}
}


function validateEditSurvey()
{
  $.post("validateSurvey.action",
		{
			name :  byId( 'name' ).value,
			shortName : byId( 'shortName' ).value,
			url : byId( 'url' ).value,
			surveyId : byId( 'surveyId' ).value
		},
		function (data)
		{
			editSurveyValidationCompleted(data);
		},'xml');

  return false;
}
function editSurveyValidationCompleted( messageElement )
{
  	messageElement = messageElement.getElementsByTagName( "message" )[0];
	var type = messageElement.getAttribute( "type" );
	var message = messageElement.firstChild.nodeValue;

	if ( type == 'success' )
	{
		// Both edit and add form has id='dataSetForm'
		var selectedList = document.getElementById( 'selectedList' );
		for(var k=0;k<selectedList.length;k++)
		{
			selectedList.options[k].selected = "true";
		} 
		  
		document.forms['editSurveyForm'].submit();
	}
	else if ( type == 'input' )
	{
		setMessage(message);
	}
}

// ----------------------------------------------------------------------
// List
// ----------------------------------------------------------------------
function filterDeTargetMembers()
{
	var filter = document.getElementById( 'deTargetMembersFilter' ).value;
    var list = document.getElementById( 'selectedList' );
    
    list.options.length = 0;
    
    for ( var id in deTargetMembers )
    {
        var value = deTargetMembers[id];
        
        if ( value.toLowerCase().indexOf( filter.toLowerCase() ) != -1 )
        {
            list.add( new Option( value, id ), null );
        }
    }
}

//complette
function filterAvailableDataElements()
{
	var filter = document.getElementById( 'availableDataElementFilter' ).value;
    var list = document.getElementById( 'availableList' );
    
    list.options.length = 0;
    
    for ( var id in availableDataElements )
    {
        var value = availableDataElements[id];
        
        if ( value.toLowerCase().indexOf( filter.toLowerCase() ) != -1 )
        {
            list.add( new Option( value, id ), null );
        }
    }
}

function addDeTargetMembers()
{
	var list = document.getElementById( 'availableList' );

    while ( list.selectedIndex != -1 )
    {
        var id = list.options[list.selectedIndex].value;

        list.options[list.selectedIndex].selected = false;

        deTargetMembers[id] = availableDataElements[id];
        
        delete availableDataElements[id];        
    }
    
    filterDeTargetMembers();
    filterAvailableDataElements();
}

function removeDeTargetMembers()
{
	var list = document.getElementById( 'selectedList' );

    while ( list.selectedIndex != -1 )
    {
        var id = list.options[list.selectedIndex].value;

        list.options[list.selectedIndex].selected = false;

        availableIndicators[id] = surveyMembers[id];
        
        delete surveyMembers[id];        
    }
    
    filterSurveyMembers();
    filterAvailableIndicators();
}

function filterByDataElementGroup( selectedDataElementGroup )
{
  var selectedList = document.getElementById( 'selectedList' );

  
  var params = 'dataElementGroupId=' + selectedDataElementGroup;
  
  for ( var i = 0; i < selectedList.options.length; ++i)
  {
  	params += '&selectedDataElements=' + selectedList.options[i].value;
  }
  // Clear the list
  var availableList = document.getElementById( 'availableList' );

  availableList.options.length = 0;
  
  
  var request = new Request();
  request.setResponseTypeXML( 'indicatorgroup' );
  request.setCallbackSuccess( filterByDataElementGroupCompleted );

  var requestString = "filterAvailableDataElementsByDataElementGroup.action";
  request.sendAsPost( params );
  request.send( requestString ); 
  
}

function filterByDataElementGroupCompleted( xmlObject )
{
  var dataElements = xmlObject.getElementsByTagName("dataElement");
  alert( "DataElement Group Received lent of Group member " + dataElements.length );
  for ( var i = 0; i < dataElements.length; i++ )
  {
      var id = dataElements[ i ].getElementsByTagName("id")[0].firstChild.nodeValue;
      var dataElementName = dataElements[ i ].getElementsByTagName("name")[0].firstChild.nodeValue;
     
      var option = document.createElement("option");
      option.value = id;
      option.text = dataElementName;
      option.title = dataElementName;
      availableList.add( option, null );
 
  }
}

function showDeTargetDetails( deTargetId )
{
	$.get("getDeTargetDetails.action",
		{
			deTargetId : deTargetId
		},
		function (data)
		{
			deTargetRecieved(data);
		},'xml');
}

function deTargetRecieved( deTargetElement )
{
 
  setInnerHTML( 'idField', getElementValue( deTargetElement, 'id' ) );
  setInnerHTML( 'nameField', getElementValue( deTargetElement, 'name' ) );
  setInnerHTML( 'dataElementSizeField', getElementValue( deTargetElement, 'dataElementSize' ) );
  
  var desCription = getElementValue( deTargetElement, 'description' );
  setInnerHTML( 'descriptionField', desCription ? desCription : '[' + i18n_none + ']' );
  
  var url = getElementValue( deTargetElement, 'url' );
  setInnerHTML( 'urlField', url ? '<a href="' + url + '">' + url + '</a>' : '[' + i18n_none + ']' );
  
   showDetails();
}

