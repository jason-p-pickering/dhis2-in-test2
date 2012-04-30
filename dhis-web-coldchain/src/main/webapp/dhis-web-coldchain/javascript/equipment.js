
// ----------------------------------------------------------------
// On InventoryType Change - Loading InventoryTypeAttributes
// ----------------------------------------------------------------
function inventoryTypeChange( inventoryTypeId )
{
	if( inventoryTypeId == "0" )
		return;
	
	showById('selectDiv');
    disable('listAllEquipmentBtn');
    
    hideById('searchEquipmentDiv');
    hideById('listEquipmentDiv');
    hideById('editEquipmentDiv');
	hideById('resultSearchDiv');
	hideById('editEquipmentStatusDiv');
	
	jQuery('#loaderDiv').show();
	
	$.post("getInventoryTypeAttributes.action",
			{
				id:inventoryTypeId
			},
			function(data)
			{
				showById('searchEquipmentDiv');
				enable('listAllEquipmentBtn');
				jQuery('#loaderDiv').hide();
				populateInventoryTypeAttributes( data );
			},'xml');	
}

function populateInventoryTypeAttributes( data )
{
	var searchingAttributeId = document.getElementById("searchingAttributeId");
	clearList( searchingAttributeId );
	
	var invenTypeAttribs = data.getElementsByTagName("inventory-type-attribute");
    for ( var i = 0; i < invenTypeAttribs.length; i++ )
    {
        var id = invenTypeAttribs[ i ].getElementsByTagName("id")[0].firstChild.nodeValue;
        var name = invenTypeAttribs[ i ].getElementsByTagName("name")[0].firstChild.nodeValue;
		
        var option = document.createElement("option");
        option.value = id;
        option.text = name;
        option.title = name;
        searchingAttributeId.add(option, null);
    }    	
}

//----------------------------------------------------------------
//On LoadAllEquipments
//----------------------------------------------------------------

function loadAllEquipments()
{
	var orgUnitId = document.getElementById('selectedOrgunitID').value;
	var inventoryType = document.getElementById('inventoryType');
	var inventoryTypeId = inventoryType.options[ inventoryType.selectedIndex ].value;
	
	if( inventoryTypeId == 0 )
	{	
		alert("Plese select Inventorytype");
		return;
	}
	
    hideById('editEquipmentDiv');
	hideById('resultSearchDiv');
	hideById('editEquipmentStatusDiv');
	
	showById('selectDiv');
	showById('searchEquipmentDiv');

	jQuery('#loaderDiv').show();
	contentDiv = 'listEquipmentDiv';

	jQuery('#listEquipmentDiv').load('getEquipmentInstances.action',{
		listAll:true,
		orgUnitId:orgUnitId, 
		InventoryTypeId:inventoryTypeId	
	},
	function(){
		statusSearching = 0;
		showById('listEquipmentDiv');
		jQuery('#loaderDiv').hide();
	});
	hideLoader();
}

//----------------------------------------------------------------
// Load Equipments On Filter by InventoryType Attribute
//----------------------------------------------------------------

function loadEquipmentsByFilter( )
{
	var orgUnitId = document.getElementById('selectedOrgunitID').value;
	var inventoryType = document.getElementById('inventoryType');
	var inventoryTypeId = inventoryType.options[ inventoryType.selectedIndex ].value;
	var searchText = document.getElementById('searchText').value;
	
	if( inventoryTypeId == 0 )
	{	
		alert("Plese select Inventorytype");
		return;
	}
	
	var inventoryTypeAttribute = document.getElementById('searchingAttributeId');
	var inventoryTypeAttributeId = inventoryTypeAttribute.options[ inventoryTypeAttribute.selectedIndex ].value;
	hideById('editEquipmentDiv');
	hideById('resultSearchDiv');
	hideById('editEquipmentStatusDiv');
	showById('selectDiv');
	showById('searchEquipmentDiv');

	jQuery('#loaderDiv').show();
	contentDiv = 'listEquipmentDiv';

	jQuery('#listEquipmentDiv').load('getEquipmentInstances.action',{		
		orgUnitId:orgUnitId, 
		inventoryTypeId:inventoryTypeId,
		inventoryTypeAttributeId:inventoryTypeAttributeId,
		searchText:searchText
	},
	function(){
		statusSearching = 0;
		showById('listEquipmentDiv');
		jQuery('#loaderDiv').hide();
	});
	hideLoader();
}

//----------------------------------------------------------------
//Show Equipment Tracking Form
//----------------------------------------------------------------

function showEquipmentStatusForm( equipmentInstanceId )
{
	hideById('listEquipmentDiv');
	hideById('editEquipmentStatusDiv');
	hideById('selectDiv');
	hideById('searchEquipmentDiv');
	
	setInnerHTML('editEquipmentDiv', '');
	
	jQuery('#loaderDiv').show();
	jQuery('#editEquipmentStatusDiv').load('showEquipmentStatusForm.action',
		{
			equipmentInstanceId:equipmentInstanceId
		}, function()
		{
			showById('editEquipmentStatusDiv');
			jQuery('#searchEquipmentDiv').dialog('close');
			jQuery('#loaderDiv').hide();
		});
		
	jQuery('#resultSearchDiv').dialog('close');
}

function updateEquipmentStatus()
{
	$.ajax({
      type: "POST",
      url: 'updateEquipmentStatus.action',
      data: getParamsForDiv('editEquipmentStatusDiv'),
      success: function( json ) {
		loadAllEquipments();
      }
     });
}


//----------------------------------------------------------------
//Add Equipment
//----------------------------------------------------------------

function showAddEquipmentForm()
{
	var orgUnitId = document.getElementById('selectedOrgunitID').value;
	var inventoryType = document.getElementById('inventoryType');
	var inventoryTypeId = inventoryType.options[ inventoryType.selectedIndex ].value;
	if( inventoryTypeId == 0 )
	{	
		alert("Plese select inventorytype");
		return;
	}

	hideById('listEquipmentDiv');
	hideById('selectDiv');
	hideById('searchEquipmentDiv');
	hideById('editEquipmentStatusDiv');
	
	jQuery('#loaderDiv').show();
	jQuery('#editEquipmentDiv').load('showAddEquipmentForm.action',{
		orgUnitId:orgUnitId, 
		inventoryTypeId:inventoryTypeId
		}, 
		function()
		{
			showById('editEquipmentDiv');
			jQuery('#loaderDiv').hide();
		});	
}

function addEquipment()
{
	$.ajax({
      type: "POST",
      url: 'addEquipment.action',
      data: getParamsForDiv('editEquipmentDiv'),
      success: function(json) {
		var type = json.response;
		jQuery('#resultSearchDiv').dialog('close');
		loadAllEquipments();
      }
     });
    return false;
}

//----------------------------------------------------------------
//Update Equipment
//----------------------------------------------------------------

function showUpdateEquipmentForm( equipmentInstanceId )
{
	hideById('listEquipmentDiv');
	hideById('selectDiv');
	hideById('searchEquipmentDiv');
	hideById('editEquipmentStatusDiv');
	
	setInnerHTML('editEquipmentDiv', '');
	
	jQuery('#loaderDiv').show();
	jQuery('#editEquipmentDiv').load('showUpdateEquipmentForm.action',
		{
			equipmentInstanceId:equipmentInstanceId
		}, function()
		{
			showById('editEquipmentDiv');
			jQuery('#searchEquipmentDiv').dialog('close');
			jQuery('#loaderDiv').hide();
		});
		
	jQuery('#resultSearchDiv').dialog('close');
}

function updateEquipment()
{
	$.ajax({
      type: "POST",
      url: 'updateEquipment.action',
      data: getParamsForDiv('editEquipmentDiv'),
      success: function( json ) {
		loadAllEquipments();
      }
     });
}
//----------------------------------------------------------------
//Get Params form Div
//----------------------------------------------------------------

function getParamsForDiv( equipmentDiv )
{
	var params = '';
	
	jQuery("#" + equipmentDiv + " :input").each(function()
		{
			var elementId = $(this).attr('id');
			
			if( $(this).attr('type') == 'checkbox' )
			{
				var checked = jQuery(this).attr('checked') ? true : false;
				params += elementId + "=" + checked + "&";
			}
			else if( $(this).attr('type') != 'button' )
			{
				var value = "";
				if( jQuery(this).val() != '' )
				{
					value = htmlEncode(jQuery(this).val());
				}
				params += elementId + "="+ value + "&";
			}
			
		});
	
	alert( params );
	
	return params;
}

