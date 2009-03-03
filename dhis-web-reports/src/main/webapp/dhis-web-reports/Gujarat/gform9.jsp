
<%@ page import="java.sql.*" %>
<%@ page import="com.opensymphony.xwork.util.OgnlValueStack" %>


<%
      Connection con=null;
      
      // for Performance in the Reporting Month
      Statement st=null;
      ResultSet rs=null;
      
      // for selected OrgUnit Name and Population
      Statement st1=null;
      ResultSet rs1=null;
      
      // for Performance in Corresponding month Last Year
      Statement st2=null;
      ResultSet rs2=null;
      
      // for Cumulative Performance till Current Month
      Statement st4=null;
      ResultSet rs4=null;
     
      // for Cumulative Performance till corresponding month of Last Year
      Statement st3=null;
      ResultSet rs3=null;
 
      // for Taluk Name and Id
      Statement st5=null;
      ResultSet rs5=null;

      // for District Name and Id
      Statement st6=null;
      ResultSet rs6=null;

     // for PHC Name and Id
      Statement st8=null;
      ResultSet rs8=null;

     // for CHC Name and Id
      Statement st9=null;
      ResultSet rs9=null;

     // for PHC Population Estimates
      Statement st10=null;
      ResultSet rs10=null;

     // for Data Period Start Date and End Date
      Statement st11=null;
      ResultSet rs11=null;
      
     // for DataElement ids based on DataElement code
      Statement st12=null;
      ResultSet rs12=null;
     
      
      String userName = "dhis";           
      String password = "";           
      String urlForConnection = "jdbc:mysql://localhost/gj_dhis2";
          
      int talukID = 0;
      String talukName = "";
      int districtID = 0; 
      String districtName = ""; 
      int CHCID = 0;
      String CHCName ="";
      int PHCID = 0;
      String PHCName ="";          
      int totPHCPopulation = -1;
      int totSCPopulation = -1;

	  OgnlValueStack stack = (OgnlValueStack)request.getAttribute("webwork.valueStack");
	  String selectedId = (String) stack.findValue( "orgUnitId" );
	  int selectedOrgUnitID =    Integer.parseInt( selectedId );
	

 	  String startingDate  = 	  (String) stack.findValue( "startingPeriod" );
	  String endingDate  =   (String) stack.findValue( "endingPeriod" );

      
	  String monthlyPeriodId = (String) stack.findValue( "monthlyPeriodTypeId" );
	  int periodTypeID = 	  Integer.parseInt( monthlyPeriodId );
	       
   	  int lastYear = 0;
      
	  String selectedOrgUnitName = "";
	  String selectedDataPeriodStartDate = "";
      	  String selectedDataPeriodEndDate = "";
	  String lastDataPeriodStartDate = "";
	  
	   String dataElementCodes[] = {
										"' '",
										 
										"'Form6_DE1'", "'Form6_DE2'", 
										
										"' '","' '","' '",
										
										"'Form6_DE3'", "'Form6_DE4'", "'Form6_DE5'", "'Form6_DE6'", "'Form6_DE7'", "'Form6_DE8'", 
										"'Form6_DE9'", "'Form6_DE10'", "'Form6_DE11'", "'Form6_DE12'", "'Form6_DE13'", "'Form6_DE14'", "'Form6_DE15'", "'Form6_DE16'", 
										"'Form6_DE17'", 
										"'Form6_DE18'", "'Form6_DE19'", "'Form6_DE20'", "'Form6_DE21'", "'Form6_DE22'", "'Form6_DE23'", "'Form6_DE24'", "'Form6_DE25'", 
										"'Form6_DE26'",
										"'Form6_DE27'", "'Form6_DE28'", "'Form6_DE29'", "'Form6_DE30'", "'Form6_DE31'", "'Form6_DE32'", "'Form6_DE33'", "'Form6_DE34'", "'Form6_DE35'", 
										"'Form6_DE36'", "'Form6_DE37'", "'Form6_DE38'", 
										
										// 2.7
										" 'Form6_DE39','Form6_DE42','Form6_DE45' ",
										" 'Form6_DE40','Form6_DE43','Form6_DE46' ",
										" 'Form6_DE41','Form6_DE44','Form6_DE47' ",
										
										"'Form6_DE39'", "'Form6_DE40'", "'Form6_DE41'", "'Form6_DE42'", "'Form6_DE43'",
										"'Form6_DE44'", "'Form6_DE45'", "'Form6_DE46'", "'Form6_DE47'",	"'Form6_DE48'", "'Form6_DE49'", "'Form6_DE50'", "'Form6_DE51'", 
										"'Form6_DE52'",
										"'Form6_DE53'", "'Form6_DE54'", "'Form6_DE55'", "'Form6_DE56'", 
										
										
										"' '","' '","' '",
										
										//3
										" 'Form6_DE60','Form6_DE63','Form6_DE66','Form6_DE69','Form8_DE1','Form8_DE4','Form8_DE7','Form8_DE10','Form8_DE13','Form8_DE16','Form6_DE78','Form6_DE81' ",
										" 'Form6_DE61','Form6_DE64','Form6_DE67','Form6_DE70','Form8_DE2','Form8_DE5','Form8_DE8','Form8_DE11','Form8_DE14','Form8_DE17','Form6_DE79','Form6_DE82' ",
										" 'Form6_DE62','Form6_DE65','Form6_DE68','Form6_DE71','Form8_DE3','Form8_DE6','Form8_DE9','Form8_DE12','Form8_DE15','Form8_DE18','Form6_DE80','Form6_DE83' ",

										
										//3.1
										" 'Form6_DE60','Form6_DE63','Form6_DE66','Form6_DE69' ",
										" 'Form6_DE61','Form6_DE64','Form6_DE67','Form6_DE70' ",
										" 'Form6_DE62','Form6_DE65','Form6_DE68','Form6_DE71' ",

										
										"'Form6_DE60'", "'Form6_DE61'", "'Form6_DE62'",	"'Form6_DE63'", "'Form6_DE64'", "'Form6_DE65'",
										"'Form6_DE66'", "'Form6_DE67'", "'Form6_DE68'", "'Form6_DE69'", "'Form6_DE70'", "'Form6_DE71'", 
										
										//3.2
										" 'Form8_DE1','Form8_DE4','Form8_DE7','Form8_DE10','Form8_DE13','Form8_DE16' ",
										" 'Form8_DE2','Form8_DE5','Form8_DE8','Form8_DE11','Form8_DE14','Form8_DE17' ",
										" 'Form8_DE3','Form8_DE6','Form8_DE9','Form8_DE12','Form8_DE15','Form8_DE18' ",
										
										"'Form8_DE1'", "'Form8_DE2'", "'Form8_DE3'", "'Form8_DE4'", "'Form8_DE5'", "'Form8_DE6'", "'Form8_DE7'", "'Form8_DE8'", "'Form8_DE9'", 
										"'Form8_DE10'", "'Form8_DE11'", "'Form8_DE12'", "'Form8_DE13'", "'Form8_DE14'", "'Form8_DE15'", "'Form8_DE16'", "'Form8_DE17'", 										
										"'Form8_DE18'",
										
										//3.3
										" 'Form6_DE78','Form6_DE81' ",
										" 'Form6_DE79','Form6_DE82' ",
										" 'Form6_DE80','Form6_DE83' ",
										 
										"'Form6_DE78'", "'Form6_DE79'", "'Form6_DE80'", "'Form6_DE81'",	"'Form6_DE82'", 
										"'Form6_DE83'", 
										
										"' '","' '","' '","' '","' '","' '",
										
										// 4.1
										"'Form6_DE84'", 
										"'Form6_DE85'", 
										"'Form6_DE86'", 
										"'Form6_DE87'", 
										"'Form6_DE88'", 
										"'Form6_DE89'",
										
										"'Form6_DE84'", "'Form6_DE85'", "'Form6_DE86'", "'Form6_DE87'", 
										"'Form6_DE88'", "'Form6_DE89'", "'Form6_DE90'", "'Form6_DE91'", "'Form6_DE92'", "'Form6_DE93'", "'Form6_DE94'", "'Form6_DE95'", 
										
										"' '", "' '","' '","' '","' '","' '", 
										
										"'Form6_DE96'", "'Form6_DE97'", "'Form6_DE98'", "'Form6_DE99'", "'Form6_DE100'", "'Form6_DE101'", "'Form6_DE102'","'Form6_DE103'", 
										"'Form6_DE104'", 
										"'Form6_DE105'", "'Form6_DE106'", "'Form6_DE107'", "'Form6_DE108'", "'Form6_DE109'","'Form6_DE110'", "'Form6_DE111'",
										"'Form6_DE112'", "'Form6_DE113'", 
										
										//4.3
										" 'Form6_DE117','Form6_DE123' ",
										" 'Form6_DE118','Form6_DE124' ",
										" 'Form6_DE119','Form6_DE125' ",
										" 'Form6_DE120','Form6_DE126' ",
										" 'Form6_DE121','Form6_DE127' ",
										" 'Form6_DE122','Form6_DE128' ",
																				
										
										"'Form6_DE117'", "'Form6_DE118'", "'Form6_DE119'", "'Form6_DE120'", "'Form6_DE121'", "'Form6_DE122'", 
										"'Form6_DE123'", "'Form6_DE124'", "'Form6_DE125'", "'Form6_DE126'", "'Form6_DE127'", "'Form6_DE128'", 
										
										"' '","' '","' '",
										
										"'Form8_DE19'", "'Form8_DE20'",  "'Form8_DE21'", "'Form8_DE22'", "'Form8_DE23'", "'Form8_DE24'", "'Form7_DE10'", "'Form7_DE11'", "'Form7_DE12'", 
										"'Form7_DE13'", "'Form7_DE14'", "'Form7_DE15'",
										 
										"' '","' '","' '",
										
										//5.1
										" 'Form6_DE135','Form6_DE138','Form6_DE141','Form6_DE144','Form6_DE147' ",
										" 'Form6_DE136','Form6_DE139','Form6_DE142','Form6_DE145','Form6_DE148' ",
										" 'Form6_DE137','Form6_DE140','Form6_DE143','Form6_DE146','Form6_DE149' ",

										
										"'Form6_DE135'", "'Form6_DE136'", "'Form6_DE137'", "'Form6_DE138'", "'Form6_DE139'", 
										"'Form6_DE140'", "'Form6_DE141'", "'Form6_DE142'", "'Form6_DE143'", "'Form6_DE144'", "'Form6_DE145'", "'Form6_DE146'", "'Form6_DE147'", 
										"'Form6_DE148'",
										"'Form6_DE149'", 
										
										//6
										" 'Form8_DE25','Form8_DE28','Form7_DE16','Form7_DE19' ",
										" 'Form8_DE26','Form8_DE29','Form7_DE17','Form7_DE20' ",																	
										" 'Form8_DE27','Form8_DE30','Form7_DE18','Form7_DE21' ",

										
										"'Form8_DE25'", "'Form8_DE26'", "'Form8_DE27'", "'Form8_DE28'", "'Form8_DE29'", "'Form8_DE30'", 
										"'Form7_DE16'", "'Form7_DE17'", "'Form7_DE18'", "'Form7_DE19'", "'Form7_DE20'", "'Form7_DE21'", 
										
										//6.5
										" 'Form6_DE1500','Form6_DE171' ",
										" 'Form6_DE180','Form6_DE172' ",
										" 'Form6_DE1536','Form6_DE173' ",																				
										
										"'Form6_DE1500'", "'Form6_DE180'", "'Form6_DE1536'", "'Form6_DE171'", "'Form6_DE172'", 
										"'Form6_DE173'", 
										
										"' '","' '","' '",
										
										//7.1
										" 'Form8_DE34','Form8_DE37','Form8_DE40','Form8_DE43','Form8_DE46','Form8_DE52' ",
										" 'Form8_DE35','Form8_DE38','Form8_DE41','Form8_DE44','Form8_DE47','Form8_DE50' ",
										" 'Form8_DE36','Form8_DE39','Form8_DE42','Form8_DE45','Form8_DE48','Form8_DE51' ",
										
										"'Form8_DE34'", "'Form8_DE35'", "'Form8_DE36'", "'Form8_DE37'", "'Form8_DE38'", "'Form8_DE39'", "'Form8_DE40'", "'Form8_DE41'", "'Form8_DE42'", 
										"'Form8_DE43'", 
										"'Form8_DE44'", "'Form8_DE45'", "'Form8_DE46'", "'Form8_DE47'", "'Form8_DE48'", "'Form8_DE49'", "'Form8_DE50'", "'Form8_DE51'", 
										
										//7.2
										" 'Form6_DE1537','Form6_DE1540' ",
										" 'Form6_DE1538','Form6_DE1541' ",
										" 'Form6_DE1539','Form6_DE188' ",

										
										"'Form6_DE1537'", "'Form6_DE1538'", "'Form6_DE1539'", "'Form6_DE1540'", "'Form6_DE1541'", "'Form6_DE1542'", 
										
										"' '",
										
										"'Form8_DE53'", "'Form8_DE54'", "'Form8_DE55'",
										
										"'Form6_DE192'",
										"'Form6_DE193'", "'Form6_DE194'", "'Form6_DE195'", "'Form6_DE196'", "'Form6_DE197'", "'Form6_DE198'", "'Form6_DE199'", "'Form6_DE200'", 
										
										"' '", "' '",
										
										"'Form7_DE25'", "'Form7_DE26'", 
										
										"' '","' '","' '",
										
										//9.1
										" 'Form6_DE207','Form6_DE210','Form6_DE213','Form8_DE56' ",
										" 'Form6_DE208','Form6_DE211','Form6_DE214','Form8_DE57' ",
										" 'Form6_DE209','Form6_DE212','Form6_DE215','Form8_DE58' ",

										
										"'Form6_DE207'", "'Form6_DE208'", "'Form6_DE209'", "'Form6_DE210'",
										"'Form6_DE211'", "'Form6_DE212'", "'Form6_DE213'", "'Form6_DE214'", "'Form6_DE215'", 
										
										"'Form8_DE56'", "'Form8_DE57'", "'Form8_DE58'",
										
										//9.2
										" 'Form6_DE222','Form6_DE225','Form8_DE59' ",
										" 'Form6_DE223','Form6_DE226','Form8_DE60' ",																	
										" 'Form6_DE224','Form6_DE227','Form8_DE61' ",

										
										"'Form6_DE222'", "'Form6_DE223'", "'Form6_DE224'", "'Form6_DE225'", "'Form6_DE226'", "'Form6_DE227'", "'Form8_DE59'", "'Form8_DE60'",
										"'Form8_DE61'", 
										
										//9.3
										" 'Form6_DE234','Form8_DE62' ",
										" 'Form6_DE235','Form8_DE63' ",
										" 'Form6_DE236','Form8_DE64' ",

										
										"'Form6_DE234'", "'Form6_DE235'", "'Form6_DE236'", 
										"'Form8_DE62'", "'Form8_DE63'", "'Form8_DE64'",
										
										"' '","' '","' '",
										
										"'Form8_DE65'", "'Form8_DE66'", "'Form8_DE67'", "'Form8_DE68'", "'Form8_DE69'", "'Form8_DE70'",
										"'Form7_DE30'", "'Form7_DE31'", "'Form7_DE32'",
										
										"'Form8_DE71'", "'Form8_DE72'", "'Form8_DE73'", 
										
										//9.5
										" 'Form6_DE246','Form6_DE249','Form6_DE252' ",
										" 'Form6_DE247','Form6_DE250','Form6_DE253' ",																	
										" 'Form6_DE248','Form6_DE251','Form6_DE254' ",

										
										"'Form6_DE246'", "'Form6_DE247'", "'Form6_DE248'", 
										"'Form6_DE249'", "'Form6_DE250'", "'Form6_DE251'", "'Form6_DE252'", "'Form6_DE253'", "'Form6_DE254'", 
										
										"' '","' '","' '",
										
										//10.0
										" 'Form8_DE77','Form8_DE80' ",
										" 'Form8_DE78','Form8_DE81' ",
										" 'Form8_DE79','Form8_DE82' ",

										
										"'Form8_DE77'", "'Form8_DE78'", "'Form8_DE79'", "'Form8_DE80'", "'Form8_DE81'", "'Form8_DE82'",
										
										// 10.3
										" 'Form8_DE83','Form8_DE86' ",
										" 'Form8_DE84','Form8_DE87' ",
										" 'Form8_DE85','Form8_DE88' ",

										
										"'Form8_DE83'", "'Form8_DE84'", "'Form8_DE85'", "'Form8_DE86'", "'Form8_DE87'", "'Form8_DE88'", "'Form8_DE89'", "'Form8_DE90'", "'Form8_DE91'",
										
										"' '","' '","' '","' '","' '","' '",
										
										// 11.0
										" 'Form6_DE261','Form8_DE92' ",
										" 'Form6_DE262','Form8_DE93' ",
										" 'Form6_DE263','Form8_DE94' ",
										" 'Form6_DE264','Form8_DE95' ",
										" 'Form6_DE265','Form8_DE96' ",
										" 'Form6_DE266','Form8_DE97' ",

										
										"'Form6_DE261'", "'Form6_DE262'", "'Form6_DE263'", "'Form6_DE264'", "'Form6_DE265'",
										"'Form6_DE266'", 
										
										
										"'Form8_DE92'", "'Form8_DE93'", "'Form8_DE94'", "'Form8_DE95'", "'Form8_DE96'", "'Form8_DE97'",
										"'Form6_DE267'", "'Form6_DE268'", "'Form6_DE269'", "'Form6_DE270'", "'Form6_DE271'", "'Form6_DE272'", "'Form6_DE273'", "'Form6_DE274'",
										"'Form6_DE275'", "'Form6_DE276'", "'Form6_DE277'", "'Form6_DE278'", 
										
										"'Form8_DE98'", "'Form8_DE99'", "'Form8_DE100'", "'Form8_DE101'", "'Form8_DE102'", "'Form8_DE103'", 
										
										"' '","' '","' '",
										
										"'Form6_DE279'", "'Form6_DE280'", "'Form6_DE281'", "'Form6_DE282'", 
										"'Form6_DE283'", "'Form6_DE284'", "'Form6_DE285'", "'Form6_DE286'", "'Form6_DE287'", "'Form6_DE288'", "'Form6_DE289'", "'Form6_DE290'",
										"'Form6_DE291'", "'Form6_DE292'", "'Form6_DE293'", "'Form6_DE294'", "'Form6_DE295'", 
										
										"' '","' '","' '",
										
										"'Form6_DE296'", "'Form6_DE297'", "'Form6_DE298'",
										
										//13.2
										" 'Form6_DE302','Form6_DE305','Form6_DE308' ",
										" 'Form6_DE303','Form6_DE306','Form6_DE309' ",																	
										" 'Form6_DE304','Form6_DE307','Form6_DE310' ",

										
										"'Form6_DE302'", "'Form6_DE303'", "'Form6_DE304'", "'Form6_DE305'", "'Form6_DE306'", "'Form6_DE307'",
										"'Form6_DE308'", "'Form6_DE309'", "'Form6_DE310'", 
										
										
										"' '",
										
										"'Form6_DE313'", "'Form6_DE314'", "'Form6_DE315'", 
										
										// 15
										" 'Form6_DE317','Form6_DE318' ",									

										
										"'Form6_DE317'", "'Form6_DE318'", 
										
										"' '", "' '", "' '", "' '", "' '", "' '",
										
										"'Form6_DE319'", "'Form6_DE320'", "'Form6_DE321'", "'Form6_DE322'", "'Form6_DE323'", "'Form6_DE324'", "'Form6_DE325'", "'Form6_DE326'", 
										"'Form6_DE327'", 
										"'Form6_DE328'", "'Form6_DE329'", "'Form6_DE330'", 
										
										//17.12
										" 'Form6_DE337','Form6_DE343','Form6_DE349' ",
										" 'Form6_DE338','Form6_DE344','Form6_DE350' ",																	
										" 'Form6_DE339','Form6_DE345','Form6_DE351' ",
										" 'Form6_DE340','Form6_DE346','Form6_DE352' ",
										" 'Form6_DE341','Form6_DE347','Form6_DE353' ",																	
										" 'Form6_DE342','Form6_DE348','Form6_DE354' ",

										
										
										"'Form6_DE337'", "'Form6_DE338'", "'Form6_DE339'", "'Form6_DE340'", "'Form6_DE341'", "'Form6_DE342'", "'Form6_DE343'", "'Form6_DE344'", 
										"'Form6_DE345'",
										"'Form6_DE346'", "'Form6_DE347'", "'Form6_DE348'", "'Form6_DE349'", "'Form6_DE350'", "'Form6_DE351'", "'Form6_DE352'", "'Form6_DE353'", 
										"'Form6_DE354'",
										
										//17.13
										" 'Form6_DE361','Form6_DE367','Form6_DE373','Form6_DE379' ",
										" 'Form6_DE362','Form6_DE368','Form6_DE374','Form6_DE380' ",																	
										" 'Form6_DE363','Form6_DE369','Form6_DE375','Form6_DE381' ",
										" 'Form6_DE364','Form6_DE370','Form6_DE376','Form6_DE382' ",
										" 'Form6_DE365','Form6_DE371','Form6_DE377','Form6_DE383' ",																	
										" 'Form6_DE366','Form6_DE372','Form6_DE378','Form6_DE384' ",
										
										"'Form6_DE361'", "'Form6_DE362'", "'Form6_DE363'",
										"'Form6_DE364'", "'Form6_DE365'", "'Form6_DE366'", "'Form6_DE367'", "'Form6_DE368'", "'Form6_DE369'", "'Form6_DE370'", "'Form6_DE371'", 
										"'Form6_DE372'",
										"'Form6_DE373'", "'Form6_DE374'", "'Form6_DE375'", "'Form6_DE376'", "'Form6_DE377'", "'Form6_DE378'", "'Form6_DE379'", "'Form6_DE380'", 
										"'Form6_DE381'",
										"'Form6_DE382'", "'Form6_DE383'", "'Form6_DE384'", 
										
										//17.14
										" 'Form6_DE391','Form6_DE397','Form6_DE403' ",
										" 'Form6_DE392','Form6_DE398','Form6_DE404' ",																	
										" 'Form6_DE393','Form6_DE399','Form6_DE405' ",
										" 'Form6_DE394','Form6_DE400','Form6_DE406' ",
										" 'Form6_DE395','Form6_DE401','Form6_DE407' ",																	
										" 'Form6_DE396','Form6_DE402','Form6_DE408' ",

										
										
										"'Form6_DE391'", "'Form6_DE392'", "'Form6_DE393'", "'Form6_DE394'", "'Form6_DE395'", "'Form6_DE396'", "'Form6_DE397'", "'Form6_DE398'", 
										"'Form6_DE399'",
										"'Form6_DE400'", "'Form6_DE401'", "'Form6_DE402'", "'Form6_DE403'", "'Form6_DE404'", "'Form6_DE405'", "'Form6_DE406'", "'Form6_DE407'", 
										"'Form6_DE408'",
										"'Form6_DE409'", "'Form6_DE410'", "'Form6_DE411'", "'Form6_DE412'", "'Form6_DE413'",	"'Form6_DE414'", "'Form6_DE415'", "'Form6_DE416'", 
										"'Form6_DE417'",
										"'Form6_DE448'", "'Form6_DE449'", "'Form6_DE450'", 
										
										//17.2
										" 'Form6_DE457','Form6_DE463' ",
										" 'Form6_DE458','Form6_DE464' ",
										" 'Form6_DE459','Form6_DE465' ",
										" 'Form6_DE460','Form6_DE466' ",
										" 'Form6_DE461','Form6_DE467' ",
										" 'Form6_DE462','Form6_DE468' ",

										
										"'Form6_DE457'",
										"'Form6_DE458'", "'Form6_DE459'", "'Form6_DE460'", "'Form6_DE461'", "'Form6_DE462'", "'Form6_DE463'", "'Form6_DE464'", "'Form6_DE465'", 
										"'Form6_DE466'",
										"'Form6_DE467'", "'Form6_DE468'", 
										
										"' '","' '","' '","' '","' '","' '",
										
										"'Form6_DE475'",
										"'Form6_DE476'", "'Form6_DE477'", "'Form6_DE478'", "'Form6_DE479'", "'Form6_DE480'", "'Form6_DE481'", "'Form6_DE482'", "'Form6_DE483'", 
										"'Form6_DE484'",
										"'Form6_DE485'", "'Form6_DE486'", "'Form6_DE487'", "'Form6_DE488'", "'Form6_DE489'", "'Form6_DE490'", "'Form6_DE491'", "'Form6_DE492'", 
										"'Form6_DE493'",
										"'Form6_DE494'", "'Form6_DE495'", "'Form6_DE496'", "'Form6_DE497'", "'Form6_DE498'", 
										
										//17.4
										" 'Form6_DE505','Form6_DE511' ",
										" 'Form6_DE506','Form6_DE512' ",
										" 'Form6_DE507','Form6_DE513' ",
										" 'Form6_DE508','Form6_DE514' ",
										" 'Form6_DE509','Form6_DE515' ",
										" 'Form6_DE510','Form6_DE516' ",

										
										"'Form6_DE505'", "'Form6_DE506'", "'Form6_DE507'", "'Form6_DE508'", "'Form6_DE509'", "'Form6_DE510'", "'Form6_DE511'",
										"'Form6_DE512'", "'Form6_DE513'", "'Form6_DE514'", "'Form6_DE515'", "'Form6_DE516'", "'Form6_DE517'", "'Form6_DE518'", 
										"'Form6_DE519'", "'Form6_DE520'",
										"'Form6_DE521'", "'Form6_DE522'", "'Form6_DE523'", "'Form6_DE524'", "'Form6_DE525'", "'Form6_DE526'", "'Form6_DE527'", "'Form6_DE528'", 
										"'Form6_DE529'",
										"'Form6_DE530'", "'Form6_DE531'", "'Form6_DE532'", "'Form6_DE533'", "'Form6_DE534'", "'Form6_DE535'", "'Form6_DE536'", "'Form6_DE537'", 
										"'Form6_DE538'", 
										"'Form6_DE539'", "'Form6_DE540'", 
										
										
										"'Form6_DE541'", "'Form6_DE542'", "'Form6_DE543'", "'Form6_DE544'", "'Form6_DE545'", "'Form6_DE546'", "'Form6_DE547'", "'Form6_DE548'", 
										"'Form6_DE549'",
										"'Form6_DE550'", "'Form6_DE551'", "'Form6_DE552'", "'Form6_DE553'", "'Form6_DE554'", "'Form6_DE555'", "'Form6_DE556'", "'Form6_DE557'", 
										"'Form6_DE558'",   
										"'Form6_DE559'", "'Form6_DE560'", "'Form6_DE561'", "'Form6_DE562'", "'Form6_DE563'", "'Form6_DE564'", "'Form6_DE565'", "'Form6_DE566'", 
										"'Form6_DE567'",
										"'Form6_DE568'", "'Form6_DE569'", "'Form6_DE570'", "'Form6_DE571'", "'Form6_DE572'", "'Form6_DE573'", "'Form6_DE574'", "'Form6_DE575'", 
										"'Form6_DE576'",
										"'Form6_DE577'", "'Form6_DE578'", "'Form6_DE579'", "'Form6_DE580'", "'Form6_DE581'", "'Form6_DE582'", "'Form6_DE583'", "'Form6_DE584'", 
										"'Form6_DE585'",
										"'Form6_DE586'", "'Form6_DE587'", "'Form6_DE588'", "'Form6_DE589'", "'Form6_DE590'", "'Form6_DE591'", "'Form6_DE592'", "'Form6_DE593'", 
										"'Form6_DE594'",
										"'Form6_DE595'", "'Form6_DE596'", "'Form6_DE597'", "'Form6_DE598'", "'Form6_DE599'", "'Form6_DE600'", "'Form6_DE601'", "'Form6_DE602'", 
										"'Form6_DE603'",
										"'Form6_DE604'", "'Form6_DE605'", "'Form6_DE606'", "'Form6_DE607'", "'Form6_DE608'", "'Form6_DE609'", "'Form6_DE610'", "'Form6_DE611'", 
										"'Form6_DE612'",
										"'Form6_DE613'", "'Form6_DE614'", "'Form6_DE615'", "'Form6_DE616'", "'Form6_DE617'", "'Form6_DE618'", "'Form6_DE619'", "'Form6_DE620'", 
										
										
										"' '","' '","' '","' '",
										
										"'Form7_DE57'", "'Form7_DE58'", "'Form7_DE59'", "'Form7_DE60'", "'Form7_DE61'", "'Form7_DE62'", "'Form7_DE63'", "'Form7_DE64'", 
										"'Form7_DE65'", "'Form7_DE66'", "'Form7_DE67'", "'Form7_DE68'",
										
										"' '","' '","' '","' '","' '","' '","' '","' '","' '",										
 
										"'Form6_DE621'", "'Form6_DE622'", "'Form6_DE623'", "'Form6_DE624'", "'Form6_DE625'", "'Form6_DE626'", "'Form6_DE627'",
										"'Form6_DE628'", "'Form6_DE629'", "'Form6_DE630'", "'Form6_DE631'", "'Form6_DE632'", "'Form6_DE633'", "'Form6_DE634'", 
										"'Form6_DE635'", "'Form6_DE636'",
										"'Form6_DE637'", "'Form6_DE638'", "'Form6_DE639'", "'Form6_DE640'", "'Form6_DE641'", "'Form6_DE642'", "'Form6_DE643'", 
										"'Form6_DE644'", "'Form6_DE645'",
										"'Form6_DE646'", "'Form6_DE647'", "'Form6_DE648'", "'Form6_DE649'", "'Form6_DE650'", 
										
										
										"' '","' '",
										
										"' '","' '",
										
										"'Form7_DE69'", "'Form7_DE70'", "'Form7_DE71'", "'Form7_DE72'", "'Form7_DE73'", "'Form7_DE74'",
										
										"' '","' '","' '",
										
										"'Form6_DE651'", "'Form6_DE652'", "'Form6_DE653'", 
										
										//22.2
										" 'Form7_DE75','Form6_DE726' ",
										" 'Form7_DE76','Form6_DE727' ",
										" 'Form7_DE77','Form6_DE728' ",

										
										"'Form7_DE75'", "'Form7_DE76'", "'Form7_DE77'", 
										"'Form6_DE726'", "'Form6_DE727'", "'Form6_DE728'", 
										
										//22.3
										" 'Form6_DE729','Form6_DE732','Form6_DE735' ",
										" 'Form6_DE730','Form6_DE733','Form6_DE736' ",																	
										" 'Form6_DE731','Form6_DE734','Form6_DE737' ",

										
										"'Form6_DE729'", "'Form6_DE730'", "'Form6_DE731'", "'Form6_DE732'", "'Form6_DE733'", "'Form6_DE734'", "'Form6_DE735'", "'Form6_DE736'", 
										"'Form6_DE737'",
										
										"'Form6_DE666'", "'Form6_DE667'", "'Form6_DE1529'",
										 "'Form6_DE669'", "'Form6_DE670'", "'Form6_DE671'", "'Form6_DE672'", "'Form6_DE673'", "'Form6_DE674'", "'Form6_DE675'", "'Form6_DE676'",
										"'Form6_DE677'", "'Form6_DE678'", "'Form6_DE679'", "'Form6_DE680'", "'Form6_DE681'", "'Form6_DE682'", "'Form6_DE683'", "'Form6_DE684'", 
										"'Form6_DE685'",
										"'Form6_DE686'", "'Form6_DE687'", "'Form6_DE688'", "'Form6_DE689'", "'Form6_DE690'", "'Form6_DE691'", "'Form6_DE692'", "'Form6_DE693'", 
										"'Form6_DE694'",
										"'Form6_DE695'", "'Form6_DE696'", "'Form6_DE697'", "'Form6_DE698'", "'Form6_DE699'", "'Form6_DE700'", "'Form6_DE701'", "'Form6_DE702'", 
										"'Form6_DE703'",
										"'Form6_DE704'", "'Form6_DE705'", "'Form6_DE706'", "'Form6_DE707'", "'Form6_DE708'", "'Form6_DE709'", "'Form6_DE710'", "'Form6_DE711'", 
										"'Form6_DE712'",
										"'Form6_DE713'", "'Form6_DE714'", "'Form6_DE715'", "'Form6_DE716'", "'Form6_DE717'", "'Form6_DE718'", "'Form6_DE719'", 
										
										"' '","' '","' '",
										
										//22.81
										" 'Form6_DE723','Form6_DE726' ",
										" 'Form6_DE724','Form6_DE727' ",
										" 'Form6_DE725','Form6_DE728' ",

										
										"'Form6_DE723'", "'Form6_DE724'", "'Form6_DE725'", "'Form6_DE726'", "'Form6_DE727'", 
										"'Form6_DE728'", 
										
										//21.82
										" 'Form6_DE729','Form6_DE732','Form6_DE735' ",
										" 'Form6_DE730','Form6_DE733','Form6_DE736' ",																	
										" 'Form6_DE731','Form6_DE734','Form6_DE737' ",

										
										"'Form6_DE729'", "'Form6_DE730'", "'Form6_DE731'", "'Form6_DE732'", "'Form6_DE733'", "'Form6_DE734'", "'Form6_DE735'",
										"'Form6_DE736'", "'Form6_DE737'", "'Form6_DE738'", "'Form6_DE739'", "'Form6_DE740'", 
										
										"' '","' '","' '","' '","' '","' '",
										
										"'Form6_DE741'", "'Form6_DE742'", 
										"'Form6_DE743'", "'Form6_DE744'", "'Form6_DE745'", "'Form6_DE746'", "'Form6_DE747'", "'Form6_DE748'", "'Form6_DE749'",
																			
										"' '","' '","' '","' '","' '","' '","' '","' '","' '","' '","' '","' '","' '","' '","' '","' '","' '","' '",

										"' '","' '","' '","' '","' '","' '",
										"' '","' '","' '","' '","' '","' '",
										"' '","' '","' '","' '","' '","' '",
										"' '","' '","' '","' '","' '","' '",
										"' '","' '","' '","' '","' '","' '",
										"' '","' '","' '","' '","' '","' '",
										"' '","' '","' '","' '","' '","' '",

										"' '","' '","' '","' '","' '","' '",
										"' '","' '","' '","' '","' '","' '",
										"' '","' '","' '","' '","' '","' '",
										"' '","' '","' '","' '","' '","' '",
										"' '","' '","' '","' '","' '","' '",
										"' '","' '","' '","' '","' '","' '",
										"' '","' '","' '","' '","' '","' '",

	
	  	  							   };

                 
 	int dataElementIDs[] = new int[dataElementCodes.length+5];  
	int entryNumberValues[]=  new int[dataElementCodes.length+5];	  
      	int entryValuesForLastYear[]= new int[dataElementCodes.length+5];      
      	int cumentryValuesForCurYear[]= new int[dataElementCodes.length+5];      
      	int cumentryValuesForLastYear[]= new int[dataElementCodes.length+5];      
      	
   String monthNames[] = { " ", "January", "February", "March", "April", "May", "June", "July", "August", "September", "October'", "November", "December" };    
%>

<%
     try
      {
        Class.forName ("com.mysql.jdbc.Driver").newInstance ();
        con = DriverManager.getConnection (urlForConnection, userName, password);
        
        st=con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_READ_ONLY); 
        st1=con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_READ_ONLY);
        st2=con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_READ_ONLY);
        st3=con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_READ_ONLY);
        st4=con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_READ_ONLY);
        st5=con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_READ_ONLY); 
        st6=con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_READ_ONLY);
        st8=con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_READ_ONLY);
        st9=con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_READ_ONLY);
        st10=con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_READ_ONLY);
        st11=con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_READ_ONLY);
        st12=con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_READ_ONLY);

     
        //rs1 = st1.executeQuery("SELECT organisationunit.shortname FROM organisationunit WHERE id = "+selectedOrgUnitID);
        rs1 = st1.executeQuery("SELECT organisationunit.shortname FROM organisationunit WHERE organisationunitid = "+selectedOrgUnitID);        
        if(rs1.next())  {   selectedOrgUnitName = rs1.getString(1);                  }
        else   {     	selectedOrgUnitName = "";                      }  

  
  		selectedDataPeriodStartDate = startingDate;
 

    } // try block end
     catch(Exception e)  { out.println(e.getMessage());  }
     finally
       {
			try
				{
  					if(rs1!=null)  rs1.close();			if(st1!=null)  st1.close();
					if(rs11!=null)  rs11.close();		if(st11!=null)  st11.close();
				}
			catch(Exception e)   {  out.println(e.getMessage());   }
       } // finally block end		

	String partsOfDataPeriodStartDate[]  =  selectedDataPeriodStartDate.split("-");
	lastYear  = Integer.parseInt(partsOfDataPeriodStartDate[0]) - 1;
	lastDataPeriodStartDate = lastYear+"-"+partsOfDataPeriodStartDate[1]+"-"+partsOfDataPeriodStartDate[2];
     	
	int tempForMonth1 = Integer.parseInt(partsOfDataPeriodStartDate[1]);
	int tempForYear = 0;
	 	
	if(tempForMonth1 < 4)   	{   tempForYear = lastYear;  }
 	else  {   tempForYear = lastYear + 1;   	}
 	 
 	 String curYearStart = tempForYear+"-04-01";
 	 String lastYearStart = (tempForYear-1)+"-04-01";
 	 String lastYearEnd = lastYear+"-"+partsOfDataPeriodStartDate[1]+"-"+partsOfDataPeriodStartDate[2];
	 
     //for district, taluk, CHC names
     try
      {
	        //rs8=st8.executeQuery("select organisationunit.id, organisationunit.name FROM organisationunit WHERE organisationunit.id in ( select organisationunit.parent from organisationunit where organisationunit.id = "+selectedOrgUnitID+")");
	        rs8=st8.executeQuery("select organisationunit.organisationunitid, organisationunit.name FROM organisationunit WHERE organisationunit.organisationunitid in ( select organisationunit.parentid from organisationunit where organisationunit.organisationunitid = "+selectedOrgUnitID+")");        
			if(rs8.next())  { PHCID = rs8.getInt(1);PHCName = rs8.getString(2);  } 
			else  {  PHCID = 0; PHCName = "";  } 

			//rs9=st9.executeQuery("select organisationunit.id, organisationunit.name FROM organisationunit WHERE organisationunit.id in ( select organisationunit.parent from organisationunit where organisationunit.id = "+PHCID+")");
			rs9=st9.executeQuery("select organisationunit.organisationunitid, organisationunit.name FROM organisationunit WHERE organisationunit.organisationunitid in ( select organisationunit.parentid from organisationunit where organisationunit.organisationunitid = "+PHCID+")");	
			if(rs9.next())  { CHCID = rs9.getInt(1);CHCName = rs9.getString(2);  } 
			else  {  CHCID = 0; CHCName = "";  } 

			//rs5=st5.executeQuery("select organisationunit.id, organisationunit.name FROM organisationunit WHERE organisationunit.id in ( select organisationunit.parent from organisationunit where organisationunit.id = "+CHCID+")");
			rs5=st5.executeQuery("select organisationunit.organisationunitid, organisationunit.name FROM organisationunit WHERE organisationunit.organisationunitid in ( select organisationunit.parentid from organisationunit where organisationunit.organisationunitid = "+CHCID+")");	
			if(rs5.next())  { talukID = rs5.getInt(1); talukName = rs5.getString(2);  } 
			else  {  talukID = 0; talukName = "";  } 
        
		    //rs6=st6.executeQuery("select organisationunit.id, organisationunit.name FROM organisationunit WHERE organisationunit.id in ( select organisationunit.parent from organisationunit where organisationunit.id = "+talukID+")");
		    rs6=st6.executeQuery("select organisationunit.organisationunitid, organisationunit.name FROM organisationunit WHERE organisationunit.organisationunitid in ( select organisationunit.parentid from organisationunit where organisationunit.organisationunitid = "+talukID+")"); 
		    if(rs6.next()) {  districtID = rs6.getInt(1); districtName = rs6.getString(2);}
			else {districtID = 0; districtName = "";}      

			//rs10=st10.executeQuery("SELECT sum(datavalue.value) FROM organisationunit INNER JOIN (dataelement INNER JOIN datavalue ON dataelement.id = datavalue.dataElement) ON organisationunit.id = datavalue.source WHERE organisationunit.parent = "+PHCID+" AND dataelement.name like 'Total Population'");
			rs10=st10.executeQuery("SELECT sum(datavalue.value) FROM organisationunit INNER JOIN (dataelement INNER JOIN datavalue ON dataelement.dataelementid = datavalue.dataelementid) ON organisationunit.organisationunitid = datavalue.sourceid WHERE organisationunit.parentid = "+PHCID+" AND dataelement.name like 'Total Population'");
			if(rs10.next()) { totPHCPopulation = rs10.getInt(1);}
			else {totPHCPopulation = 0;}      
       
       }   // try block end		 
     catch(Exception e)  { out.println("Exception1 : "+e.getMessage());  }
     finally
       {
		 try
			  {
			    if(rs5!=null)  rs5.close();			if(st5!=null)  st5.close();
			    if(rs6!=null)  rs6.close();			if(st6!=null)  st6.close();
			    if(rs8!=null)  rs8.close();			if(st8!=null)  st8.close();   
			    if(rs9!=null)  rs9.close();			if(st9!=null)  st9.close();
			    if(rs10!=null)  rs10.close();			if(st10!=null)  st10.close();                                                                                                                
			  }
		catch(Exception e)   {  out.println("Exception2 : "+e.getMessage());   }
       }  // finally block end
    
     try
      {
		int i=0;    
		int j= dataElementCodes.length;
		String query = "";	
		while(i!=j)
			{		
				 entryNumberValues[i]	 = -1;
				 entryValuesForLastYear[i] = -1;
				 cumentryValuesForCurYear[i] = -1;
				 cumentryValuesForLastYear[i] = -1;
										
				// for Performance in the reporting month				
				//	query = "SELECT sum(datavalue.value) FROM datavalue INNER JOIN dataelement ON datavalue.dataElement = dataelement.id WHERE datavalue.period in (select id from period where startdate between '"+startingDate+"' and '"+startingDate+"') AND datavalue.source ="+selectedOrgUnitID+" AND dataelement.code in ("+dataElementCodes[i]+")"; 
				//	query = "SELECT sum(datavalue.value) FROM datavalue INNER JOIN dataelement ON datavalue.dataElement = dataelement.id WHERE datavalue.period  in (select id from period where startdate between '2006-04-01' and '2006-04-01') AND datavalue.source in (select id from organisationunit where id = "+selectedOrgUnitID+" OR id  in (select id from organisationunit where parent = "+selectedOrgUnitID+") OR parent in (select id from organisationunit where parent = "+selectedOrgUnitID+")) AND dataelement.code in ("+dataElementCodes[i]+")";
				query = "SELECT sum(datavalue.value) FROM datavalue INNER JOIN dataelement ON datavalue.dataelementid = dataelement.dataelementid WHERE datavalue.periodid  in (select periodid from period where startdate between '2006-04-01' and '2006-04-01') AND datavalue.sourceid in (select organisationunitid from organisationunit where organisationunitid = "+selectedOrgUnitID+" OR organisationunitid  in (select organisationunitid from organisationunit where parentid = "+selectedOrgUnitID+") OR parentid in (select organisationunitid from organisationunit where parentid = "+selectedOrgUnitID+")) AND dataelement.code in ("+dataElementCodes[i]+")";
				rs=st.executeQuery(query);
				if(rs.next())  {  entryNumberValues[i] = rs.getInt(1);  } 
				else  {  entryNumberValues[i] = 0;  } 
						
			                
			    // for Cumulative Performance till Current Month
			    //query = "SELECT sum(datavalue.value) FROM datavalue INNER JOIN dataelement ON datavalue.dataElement = dataelement.id WHERE datavalue.period in (select id from period where startdate between '"+curYearStart+"' and '"+selectedDataPeriodStartDate+"' and periodType = "+periodTypeID+")  AND datavalue.source in (select id from organisationunit where id = "+selectedOrgUnitID+" OR id  in (select id from organisationunit where parent = "+selectedOrgUnitID+") OR parent in (select id from organisationunit where parent = "+selectedOrgUnitID+")) AND dataelement.code in ("+dataElementCodes[i]+")";
			    query = "SELECT sum(datavalue.value) FROM datavalue INNER JOIN dataelement ON datavalue.dataelementid = dataelement.dataelementid WHERE datavalue.periodid in (select periodid from period where startdate between '"+curYearStart+"' and '"+selectedDataPeriodStartDate+"' and periodtypeid = "+periodTypeID+")  AND datavalue.sourceid in (select organisationunitid from organisationunit where organisationunitid = "+selectedOrgUnitID+" OR organisationunitid  in (select organisationunitid from organisationunit where parentid = "+selectedOrgUnitID+") OR parentid in (select organisationunitid from organisationunit where parentid = "+selectedOrgUnitID+")) AND dataelement.code in ("+dataElementCodes[i]+")";	
			    rs4=st4.executeQuery(query);
     			if(rs4.next())  {  cumentryValuesForCurYear[i]= rs4.getInt(1);  } 
			    else  {  cumentryValuesForCurYear[i] = 0;  } 
			            
				i++;
			}  // while loop end
      }  // try block end   		 
     catch(Exception e)  { out.println(e.getMessage());  }
     finally
       {
			 try
				{
				    if(rs!=null)  rs.close();		if(st!=null)  st.close();      							
					if(rs2!=null)  rs2.close();		if(st2!=null)  st2.close();    
					if(rs3!=null)  rs3.close();		if(st3!=null)  st3.close();					
					if(rs4!=null)  rs4.close();		if(st4!=null)  st4.close();
					if(rs12!=null)  rs12.close();		if(st12!=null)  st12.close();
										
					if(con!=null) con.close();					
		        }	 
			catch(Exception e)   {  out.println("Exception3 : "+e.getMessage());   }
       }  // finally block end	                     	        
%>

<HTML>
<HEAD>
   <TITLE>Form - 9</TITLE>
   <script src="../dhis-web-reports/Gujarat/GDENamesForForm9.js" type="text/javascript" language="Javascript"></script> 	
   <script>
   		function fun1()
        	{ 
        	    
        		var start=0;
        		var end = 28;    
        		var j=1;
        		var k=0;             	
        		var id="";        		
        	
        		while(j<=11)
        		 {        		    
        		   	
        		   	if(j==1) end = 22;
					else if(j==2) end = 34;
					else if(j==3) end = 29;
					else if(j==4) end = 31;
					else if(j==5) end = 33;
					else if(j==6) end = 24;
					else if(j==7) end = 15;
					else if(j==8) end = 28;
        		   	else if(j==9) end = 20;
        		   	else if(j==10) end = 19;
        		   	else if(j==11) end = 27;
        		   	
	   	 			for(start=0;start<=end;start++)
	   	 			 {	
					  	 id="cell1"+k;   	 			  
	   	 			  	 document.getElementById(id).innerHTML = slnoForForm9[k];
					  	id="cell2"+k;   	 			  
	   	 			  	document.getElementById(id).innerHTML = denamesForForm9[k];
	   	 			  	k++;
	   	 			 }
	   	 			j++; 
	   	 		 }	   	 			   	 		
	  		}
  	</script>   		    
</HEAD>
<BODY BGCOLOR="#FFFFFF" onload="fun1()">
	<br><br>
	<center>
		<font face="Arial" size="3">
			<b>&#2731;&#2763;&#2736;&#2765;&#2734; &#2728;&#2690;. &#2799;<BR>&#2730;&#2765;&#2736;&#2716;&#2750;&#2728;&#2728; &#2693;&#2728;&#2759; &#2732;&#2750;&#2739; &#2694;&#2736;&#2763;&#2711;&#2765;&#2735; - ||<BR>&#2744;&#2690;&#2744;&#2765;&#2725;&#2750;&#2709;&#2752;&#2735; &#2734;&#2750;&#2745;&#2751;&#2724;&#2752; &#2730;&#2727;&#2765;&#2727;&#2724;&#2751;<BR>&#2716;&#2751;&#2738;&#2765;&#2738;&#2750; &#2726;&#2765;&#2741;&#2736;&#2750; &#2736;&#2750;&#2716;&#2765;&#2735; &#2709;&#2709;&#2765;&#2743;&#2750;&#2703; &#2734;&#2763;&#2709;&#2738;&#2741;&#2750;&#2728;&#2763; &#2734;&#2750;&#2744;&#2752;&#2709; &#2734;&#2763;&#2728;&#2752;&#2719;&#2736;&#2752;&#2690;&#2711; &#2736;&#2752;&#2730;&#2763;&#2736;&#2765;&#2719;</b>
		</font>
	</center>

	<table cellpadding="0" cellspacing="0" bgcolor="ffffff" align="center" style="border-style:solid; border-width:0; border-collapse: collapse; padding: 0; position:relative; top:10" bordercolor="#ffffff" width="100%"  border="0">
  		<tr style="border-collapse: collapse; border-left-style: solid; border-left-width: 0; border-right-style: solid; border-right-width: 0; padding: 0" bordercolor="#111111" width="100%">
    		<td width="70%" style="border-collapse: collapse; border-left-style: solid; border-left-width: 0; border-right-style: solid; border-right-width: 0; padding-left:15; padding-right:0; padding-top:0; padding-bottom:0" bordercolor="#111111"  height="23">
       			<font face="Arial" size="2">&#2716;&#2751;&#2738;&#2765;&#2738;&#2750;&#2728;&#2753;&#2690; &#2728;&#2750;&#2734; :&nbsp;&nbsp;<%=selectedOrgUnitName%></font>
    	</td>
    		<td width="30%" style="border-collapse: collapse; border-left-style: solid; border-left-width: 0; border-right-style: solid; border-right-width: 0; padding-left:15; padding-right:0; padding-top:0; padding-bottom:0" bordercolor="#111111"  height="23">
       			<font face="Arial" size="2">&#2693;&#2745;&#2759;&#2741;&#2750;&#2738;&#2728;&#2763; &#2734;&#2750;&#2744; :&nbsp;&nbsp;<%=monthNames[Integer.parseInt(partsOfDataPeriodStartDate[1])]%></font>
    	</td>   
  	</tr>
 	<tr>
    		<td width="70%" style="border-collapse: collapse; border-left-style: solid; border-left-width: 0; border-right-style: solid; border-right-width: 0; padding-left:15; padding-right:0; padding-top:0; padding-bottom:0" bordercolor="#111111" height="19">
       			<font face="Arial" size="2">&#2716;&#2751;&#2738;&#2765;&#2738;&#2750;&#2728;&#2763; &#2709;&#2763;&#2721; (&#2741;&#2744;&#2724;&#2751; &#2711;&#2723;&#2724;&#2736;&#2752; &#2792;&#2790;&#2790;&#2791; &#2734;&#2753;&#2716;&#2732;)</font>
    	</td>
    		<td width="30%" style="border-collapse: collapse; border-left-style: solid; border-left-width: 0; border-right-style: solid; border-right-width: 0; padding-left:15; padding-right:0; padding-top:0; padding-bottom:0" bordercolor="#111111"  height="19">
       			<font face="Arial" size="2">&#2693;&#2745;&#2759;&#2741;&#2750;&#2738;&#2728;&#2753; &#2741;&#2736;&#2765;&#2743; :&nbsp;&nbsp;<%=partsOfDataPeriodStartDate[0]%></font>
    	</td>
  	</tr>		
	</table>  
<br>

<%
 	int i;
 	int j = 1;
 	int k = 0;
 	int l = 0;
 	int endcount = 28;
 	int flag = 0;
	String tempForentryNumberValues[] = new String[12];
	String tempForcumentryValuesForCurYear[] = new String[12];
	
 	 	 	
 	String temp1 = "";
 	
 	String temp2 = "";
 	
 	int temp = 0;
int bt = 0;
 	while(j<=11)
 	 { 
 	   
 	    if(j==1) 
 	      { %>
 	       	<table cellpadding="0" cellspacing="0" style="border-collapse: collapse; border-left-style: solid; border-left-width: 1; border-right-style: solid; border-right-width: 1; border-top-style: solid; border-top-width: 1; border-bottom-style: solid; border-bottom-width: 1;padding: 0" bordercolor="#111111" width="100%">
  				<tr>
    				<td width="100%" style="border-collapse: collapse; border-left-style: solid; border-left-width: 1; border-right-style: solid; border-right-width: 1; border-top-style: solid; border-top-width: 1; border-bottom-style: solid; border-bottom-width: 1;padding: 0" bordercolor="#111111" width="100%" align="center" colspan="14"><font face="Arial" size="2"><b>&#2733;&#2750;&#2711;-&#2693; &#2734;&#2750;&#2724;&#2750; &#2693;&#2728;&#2759; &#2732;&#2750;&#2710; &#2694;&#2736;&#2763;&#2711;&#2765;&#2735;</b></font></td>
				</tr> 
		   <%	 	      
 	       }
 	    else if(j==8)
 	       { %>
 	        <br><br><br><br><br><br><br><br><br><br><br><br><br><br><br>
			<table cellpadding="0" cellspacing="0" style="border-collapse: collapse; border-left-style: solid; border-left-width: 1; border-right-style: solid; border-right-width: 1; border-top-style: solid; border-top-width: 1; border-bottom-style: solid; border-bottom-width: 1;padding: 0" bordercolor="#111111" width="100%"> 	       
 	         <tr>
    			<td width="2%" style="border-collapse: collapse; border-top-style: solid; border-top-width: 1; border-bottom-style: solid; border-bottom-width: 1; border-left-style: solid; border-left-width: 1; border-right-style: solid; border-right-width: 1; " bordercolor="#111111" width="100%" align="left" rowspan="3"><b><font face="Arial" size="2">&#2791;&#2798;</b></font></td>
    			<td width="34%" style="border-collapse: collapse; border-top-style: solid; border-top-width: 1; border-bottom-style: solid; border-bottom-width: 1; border-left-style: solid; border-left-width: 1; border-right-style: solid; border-right-width: 1; padding-left:15; padding-right:1; " bordercolor="#111111" width="100%" rowspan="3"><b><font face="Arial" size="2">&#2732;&#2750;&#2739;&#2709;&#2763;&#2728;&#2750; &#2736;&#2763;&#2711;</b></font></td>
    			<td width="32%" style="border-collapse: collapse; border-top-style: solid; border-top-width: 1; border-bottom-style: solid; border-bottom-width: 1; border-left-style: solid; border-left-width: 1; border-right-style: solid; border-right-width: 1; padding-left:3; padding-right:1; " bordercolor="#111111" width="100%" align="center" colspan="8"><b><font face="Arial" size="2">&#2714;&#2750;&#2738;&#2753; &#2734;&#2750;&#2744; &#2727;&#2736;&#2734;&#2765;&#2735;&#2750;&#2728;</b></font></td>
    			<td width="32%" style="border-collapse: collapse; border-top-style: solid; border-top-width: 1; border-bottom-style: solid; border-bottom-width: 1; border-left-style: solid; border-left-width: 1; border-right-style: solid; border-right-width: 1; padding-left:3; padding-right:1; " bordercolor="#111111" width="100%" align="center" colspan="8"><b><font face="Arial" size="2">&#2703;&#2730;&#2765;&#2736;&#2752;&#2738;&#2735;&#2752; &#2741;&#2727;&#2724;&#2752; &#2716;&#2724;&#2752; &#2709;&#2750;&#2734;&#2711;&#2752;&#2736;&#2752;</font></b></td>
		  	</tr>
			<tr>
    			<td width="8%" style="border-collapse: collapse; border-top-style: solid; border-top-width: 1; border-bottom-style: solid; border-bottom-width: 1; border-left-style: solid; border-left-width: 1; border-right-style: solid; border-right-width: 1; padding-left:3; padding-right:1; " bordercolor="#111111" width="100%" align="center" colspan="2"><b><font face="Arial" size="2">&#2742;&#2763;&#2727;&#2759;&#2738;</font></b></td>
    			<td width="8%" style="border-collapse: collapse; border-top-style: solid; border-top-width: 1; border-bottom-style: solid; border-bottom-width: 1; border-left-style: solid; border-left-width: 1; border-right-style: solid; border-right-width: 1; padding-left:3; padding-right:1; " bordercolor="#111111" width="100%" align="center" colspan="2"><b><font face="Arial" size="2">&#2744;&#2750;&#2736;&#2741;&#2750;&#2736; &#2694;&#2730;&#2759;&#2738;</font></b></td>
    			<td width="8%" style="border-collapse: collapse; border-top-style: solid; border-top-width: 1; border-bottom-style: solid; border-bottom-width: 1; border-left-style: solid; border-left-width: 1; border-right-style: solid; border-right-width: 1; padding-left:3; padding-right:1; " bordercolor="#111111" width="100%" align="center" colspan="2"><b><font face="Arial" size="2">&#2736;&#2752;&#2731;&#2736; &#2709;&#2736;&#2759;&#2738;</font></b></td>
    			<td width="8%" style="border-collapse: collapse; border-top-style: solid; border-top-width: 1; border-bottom-style: solid; border-bottom-width: 1; border-left-style: solid; border-left-width: 1; border-right-style: solid; border-right-width: 1; padding-left:3; padding-right:1; " bordercolor="#111111" width="100%" align="center" colspan="2"><b><font face="Arial" size="2">&#2734;&#2755;&#2724;&#2765;&#2735;&#2753;</font></b></td>
    			<td width="8%" style="border-collapse: collapse; border-top-style: solid; border-top-width: 1; border-bottom-style: solid; border-bottom-width: 1; border-left-style: solid; border-left-width: 1; border-right-style: solid; border-right-width: 1; padding-left:3; padding-right:1; " bordercolor="#111111" width="100%" align="center" colspan="2"><b><font face="Arial" size="2">&#2742;&#2763;&#2727;&#2759;&#2738;</font></b></td>
    			<td width="8%" style="border-collapse: collapse; border-top-style: solid; border-top-width: 1; border-bottom-style: solid; border-bottom-width: 1; border-left-style: solid; border-left-width: 1; border-right-style: solid; border-right-width: 1; padding-left:3; padding-right:1; " bordercolor="#111111" width="100%" align="center" colspan="2"><b><font face="Arial" size="2">&#2744;&#2750;&#2736;&#2741;&#2750;&#2736; &#2694;&#2730;&#2759;&#2738;</font></b></td>
    			<td width="8%" style="border-collapse: collapse; border-top-style: solid; border-top-width: 1; border-bottom-style: solid; border-bottom-width: 1; border-left-style: solid; border-left-width: 1; border-right-style: solid; border-right-width: 1; padding-left:3; padding-right:1; " bordercolor="#111111" width="100%" align="center" colspan="2"><b><font face="Arial" size="2">&#2736;&#2752;&#2731;&#2736; &#2709;&#2736;&#2759;&#2738;</font></b></td>
    			<td width="8%" style="border-collapse: collapse; border-top-style: solid; border-top-width: 1; border-bottom-style: solid; border-bottom-width: 1; border-left-style: solid; border-left-width: 1; border-right-style: solid; border-right-width: 1; padding-left:3; padding-right:1; " bordercolor="#111111" width="100%" align="center" colspan="2"><b><font face="Arial" size="2">&#2734;&#2755;&#2724;&#2765;&#2735;&#2753;</font></b></td>
		  	</tr>
			<tr>
    			<td width="4%" style="border-collapse: collapse; border-top-style: solid; border-top-width: 1; border-bottom-style: solid; border-bottom-width: 1; border-left-style: solid; border-left-width: 1; border-right-style: solid; border-right-width: 1; padding-left:3; padding-right:1; " bordercolor="#111111" width="100%" align="center"><b><font face="Arial" size="2">&#2730;&#2753;&#2736;&#2754;&#2743;</font></b></td>
    			<td width="4%" style="border-collapse: collapse; border-top-style: solid; border-top-width: 1; border-bottom-style: solid; border-bottom-width: 1; border-left-style: solid; border-left-width: 1; border-right-style: solid; border-right-width: 1; padding-left:3; padding-right:1; " bordercolor="#111111" width="100%" align="center"><b><font face="Arial" size="2">&#2744;&#2765;&#2724;&#2765;&#2736;&#2752;</font></b></td>
    			<td width="4%" style="border-collapse: collapse; border-top-style: solid; border-top-width: 1; border-bottom-style: solid; border-bottom-width: 1; border-left-style: solid; border-left-width: 1; border-right-style: solid; border-right-width: 1; padding-left:3; padding-right:1; " bordercolor="#111111" width="100%" align="center"><b><font face="Arial" size="2">&#2730;&#2753;&#2736;&#2754;&#2743;</font></b></td>
    			<td width="4%" style="border-collapse: collapse; border-top-style: solid; border-top-width: 1; border-bottom-style: solid; border-bottom-width: 1; border-left-style: solid; border-left-width: 1; border-right-style: solid; border-right-width: 1; padding-left:3; padding-right:1; " bordercolor="#111111" width="100%" align="center"><b><font face="Arial" size="2">&#2744;&#2765;&#2724;&#2765;&#2736;&#2752;</font></b></td>
    			<td width="4%" style="border-collapse: collapse; border-top-style: solid; border-top-width: 1; border-bottom-style: solid; border-bottom-width: 1; border-left-style: solid; border-left-width: 1; border-right-style: solid; border-right-width: 1; padding-left:3; padding-right:1; " bordercolor="#111111" width="100%" align="center"><b><font face="Arial" size="2">&#2730;&#2753;&#2736;&#2754;&#2743;</font></b></td>
    			<td width="4%" style="border-collapse: collapse; border-top-style: solid; border-top-width: 1; border-bottom-style: solid; border-bottom-width: 1; border-left-style: solid; border-left-width: 1; border-right-style: solid; border-right-width: 1; padding-left:3; padding-right:1; " bordercolor="#111111" width="100%" align="center"><b><font face="Arial" size="2">&#2744;&#2765;&#2724;&#2765;&#2736;&#2752;</font></b></td>
    			<td width="4%" style="border-collapse: collapse; border-top-style: solid; border-top-width: 1; border-bottom-style: solid; border-bottom-width: 1; border-left-style: solid; border-left-width: 1; border-right-style: solid; border-right-width: 1; padding-left:3; padding-right:1; " bordercolor="#111111" width="100%" align="center"><b><font face="Arial" size="2">&#2730;&#2753;&#2736;&#2754;&#2743;</font></b></td>
    			<td width="4%" style="border-collapse: collapse; border-top-style: solid; border-top-width: 1; border-bottom-style: solid; border-bottom-width: 1; border-left-style: solid; border-left-width: 1; border-right-style: solid; border-right-width: 1; padding-left:3; padding-right:1; " bordercolor="#111111" width="100%" align="center"><b><font face="Arial" size="2">&#2744;&#2765;&#2724;&#2765;&#2736;&#2752;</font></b></td>
    			<td width="4%" style="border-collapse: collapse; border-top-style: solid; border-top-width: 1; border-bottom-style: solid; border-bottom-width: 1; border-left-style: solid; border-left-width: 1; border-right-style: solid; border-right-width: 1; padding-left:3; padding-right:1; " bordercolor="#111111" width="100%" align="center"><b><font face="Arial" size="2">&#2730;&#2753;&#2736;&#2754;&#2743;</font></b></td>
    			<td width="4%" style="border-collapse: collapse; border-top-style: solid; border-top-width: 1; border-bottom-style: solid; border-bottom-width: 1; border-left-style: solid; border-left-width: 1; border-right-style: solid; border-right-width: 1; padding-left:3; padding-right:1; " bordercolor="#111111" width="100%" align="center"><b><font face="Arial" size="2">&#2744;&#2765;&#2724;&#2765;&#2736;&#2752;</font></b></td>
    			<td width="4%" style="border-collapse: collapse; border-top-style: solid; border-top-width: 1; border-bottom-style: solid; border-bottom-width: 1; border-left-style: solid; border-left-width: 1; border-right-style: solid; border-right-width: 1; padding-left:3; padding-right:1; " bordercolor="#111111" width="100%" align="center"><b><font face="Arial" size="2">&#2730;&#2753;&#2736;&#2754;&#2743;</font></b></td>
    			<td width="4%" style="border-collapse: collapse; border-top-style: solid; border-top-width: 1; border-bottom-style: solid; border-bottom-width: 1; border-left-style: solid; border-left-width: 1; border-right-style: solid; border-right-width: 1; padding-left:3; padding-right:1; " bordercolor="#111111" width="100%" align="center"><b><font face="Arial" size="2">&#2744;&#2765;&#2724;&#2765;&#2736;&#2752;</font></b></td>
    			<td width="4%" style="border-collapse: collapse; border-top-style: solid; border-top-width: 1; border-bottom-style: solid; border-bottom-width: 1; border-left-style: solid; border-left-width: 1; border-right-style: solid; border-right-width: 1; padding-left:3; padding-right:1; " bordercolor="#111111" width="100%" align="center"><b><font face="Arial" size="2">&#2730;&#2753;&#2736;&#2754;&#2743;</font></b></td>
    			<td width="4%" style="border-collapse: collapse; border-top-style: solid; border-top-width: 1; border-bottom-style: solid; border-bottom-width: 1; border-left-style: solid; border-left-width: 1; border-right-style: solid; border-right-width: 1; padding-left:3; padding-right:1; " bordercolor="#111111" width="100%" align="center"><b><font face="Arial" size="2">&#2744;&#2765;&#2724;&#2765;&#2736;&#2752;</font></b></td>
    			<td width="4%" style="border-collapse: collapse; border-top-style: solid; border-top-width: 1; border-bottom-style: solid; border-bottom-width: 1; border-left-style: solid; border-left-width: 1; border-right-style: solid; border-right-width: 1; padding-left:3; padding-right:1; " bordercolor="#111111" width="100%" align="center"><b><font face="Arial" size="2">&#2730;&#2753;&#2736;&#2754;&#2743;</font></b></td>
    			<td width="4%" style="border-collapse: collapse; border-top-style: solid; border-top-width: 1; border-bottom-style: solid; border-bottom-width: 1; border-left-style: solid; border-left-width: 1; border-right-style: solid; border-right-width: 1; padding-left:3; padding-right:1; " bordercolor="#111111" width="100%" align="center"><b><font face="Arial" size="2">&#2744;&#2765;&#2724;&#2765;&#2736;&#2752;</font></b></td>
		  	</tr> 	
 	       <%
 	       }
 	    else if(j==2)
 	      { %>
			<br><br>
 	    	<table cellpadding="0" cellspacing="0" style="border-collapse: collapse; border-left-style: solid; border-left-width: 1; border-right-style: solid; border-right-width: 1; border-top-style: solid; border-top-width: 1; border-bottom-style: solid; border-bottom-width: 1;padding: 0" bordercolor="#111111" width="100%">   	         
 	      <% 
			}
        else if(j==3)
 	      { %>
			<br><br><br><br><br><br><br><br><br><br>
 	    	<table cellpadding="0" cellspacing="0" style="border-collapse: collapse; border-left-style: solid; border-left-width: 1; border-right-style: solid; border-right-width: 1; border-top-style: solid; border-top-width: 1; border-bottom-style: solid; border-bottom-width: 1;padding: 0" bordercolor="#111111" width="100%">   	         
 	      <% 
			}
 		else if(j==4)
 	      { %>
			<br><br><br><br><br><br><br><br><br><br><br><br><br><br>
 	    	<table cellpadding="0" cellspacing="0" style="border-collapse: collapse; border-left-style: solid; border-left-width: 1; border-right-style: solid; border-right-width: 1; border-top-style: solid; border-top-width: 1; border-bottom-style: solid; border-bottom-width: 1;padding: 0" bordercolor="#111111" width="100%">   	         
 	      <% 
			}
		else if(j==5)
 	      { %>
			<br><br><br><br>
 	    	<table cellpadding="0" cellspacing="0" style="border-collapse: collapse; border-left-style: solid; border-left-width: 1; border-right-style: solid; border-right-width: 1; border-top-style: solid; border-top-width: 1; border-bottom-style: solid; border-bottom-width: 1;padding: 0" bordercolor="#111111" width="100%">   	         
 	      <% 
			}
		else if(j==6)
 	      { %>
			<br><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br>
 	    	<table cellpadding="0" cellspacing="0" style="border-collapse: collapse; border-left-style: solid; border-left-width: 1; border-right-style: solid; border-right-width: 1; border-top-style: solid; border-top-width: 1; border-bottom-style: solid; border-bottom-width: 1;padding: 0" bordercolor="#111111" width="100%"> 													  	         
 	      <% 
			}
		else if(j==7)
 	      { %>
			<br><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br>
 	    	<table cellpadding="0" cellspacing="0" style="border-collapse: collapse; border-left-style: solid; border-left-width: 1; border-right-style: solid; border-right-width: 1; border-top-style: solid; border-top-width: 1; border-bottom-style: solid; border-bottom-width: 1;padding: 0" bordercolor="#111111" width="100%">   	         
 	      <% 
			}
		else if(j==8)
 	      { %>
			<br><br><br><br><br><br><br><br><br><br><br><br><br><br><br>
 	    	<table cellpadding="0" cellspacing="0" style="border-collapse: collapse; border-left-style: solid; border-left-width: 1; border-right-style: solid; border-right-width: 1; border-top-style: solid; border-top-width: 1; border-bottom-style: solid; border-bottom-width: 1;padding: 0" bordercolor="#111111" width="100%">   	         
 	      <% 
			}
		else if(j==9)
 	      { %>
			<br><br><br><br><br><br><br><br><br><br><br><br>
 	    	<table cellpadding="0" cellspacing="0" style="border-collapse: collapse; border-left-style: solid; border-left-width: 1; border-right-style: solid; border-right-width: 1; border-top-style: solid; border-top-width: 1; border-bottom-style: solid; border-bottom-width: 1;padding: 0" bordercolor="#111111" width="100%">   	         
 	      <% 
			}
		else if(j==10)
 	      { %>
			<br><br><br><br><br><br><br><br>
 	    	<table cellpadding="0" cellspacing="0" style="border-collapse: collapse; border-left-style: solid; border-left-width: 1; border-right-style: solid; border-right-width: 1; border-top-style: solid; border-top-width: 1; border-bottom-style: solid; border-bottom-width: 1;padding: 0" bordercolor="#111111" width="100%">   	         
 	      <% 
			}
		else if(j==11)
 	      { %>
			<br><br><br><br><br><br><br><br><br><br><br><br><br><br><br>
 	    	<table cellpadding="0" cellspacing="0" style="border-collapse: collapse; border-left-style: solid; border-left-width: 1; border-right-style: solid; border-right-width: 1; border-top-style: solid; border-top-width: 1; border-bottom-style: solid; border-bottom-width: 1;padding: 0" bordercolor="#111111" width="100%">   	         
 	      <% 
			}

 	    else
 	      { %>
			<br>
 	    	<table cellpadding="0" cellspacing="0" style="border-collapse: collapse; border-left-style: solid; border-left-width: 1; border-right-style: solid; border-right-width: 1; border-top-style: solid; border-top-width: 1; border-bottom-style: solid; border-bottom-width: 1;padding: 0" bordercolor="#111111" width="100%">   	         
 	      <% 
			}
		if(j==1) endcount = 22; 
		else if(j==2) endcount = 34; 
		else if(j==3) endcount = 29;
		else if(j==4) endcount = 31;
		else if(j==5) endcount = 33;
		else if(j==6) endcount = 24;
		else if(j==7) endcount = 15;
		else if(j==8) endcount = 28;
		else if(j==9) endcount = 20;
		else if(j==10) endcount = 19;
		else if(j==11) endcount = 27;
												
 		for(i=0;i<=endcount;i++)
 	 	  {  	
 	     	String id1="cell1"+k;
 	     	String id2="cell2"+k;
 	    
 	     	if((k>=3 && k<=42) || (k>=46 && k<=47) || (k>=55 && k<=96) || (k>=99 && k<=119) || (k>=137 && k<=142) || (k>=145 && k<=150) || (k>=224 && k<=264) || (k>=280 && k<=292))
 	      	{ 
               if(k==3 || k==23 || k==55 || k==58 ||  k==88 || k==99 || k==137 || k==135 || k==145 || k==224 || k==245 || k==252 || k==261 || k==280) 
 	            {
 	              tempForentryNumberValues[0]= "<b>&#2709;&#2753;&#2738;</b>";
 	              tempForentryNumberValues[1]= "<b>&#2693;.&#2716;&#2750;.</b>";
				  tempForentryNumberValues[2]= "<b>&#2693;.&#2716;.&#2716;&#2750;</b>";
 
 	              tempForcumentryValuesForCurYear[0] = "<b>&#2709;&#2753;&#2738;</b>";
 	              tempForcumentryValuesForCurYear[1] = "<b>&#2693;.&#2716;&#2750;.</b>";
 	              tempForcumentryValuesForCurYear[2] = "<b>&#2693;.&#2716;.&#2716;&#2750;</b>"; 	              
 	              
 	              l= l+3;
 	            }
 	           else 
 	            {

					temp =  entryNumberValues[l];
 	         		if(temp==-1) tempForentryNumberValues[0] = "";
 	         		else tempForentryNumberValues[0] = ""+temp;
 					
 					temp = cumentryValuesForCurYear[l];
			 		if(temp==-1) tempForcumentryValuesForCurYear[0] = "";
			 		else tempForcumentryValuesForCurYear[0] = ""+temp;
//tempForcumentryValuesForCurYear[0] = dataElementCodes[l];

					l++;
						         
 	         		temp =  entryNumberValues[l];
 	         		if(temp==-1) tempForentryNumberValues[1] = "";
 	         		else tempForentryNumberValues[1] = ""+temp;

					temp = cumentryValuesForCurYear[l];
			 		if(temp==-1) tempForcumentryValuesForCurYear[1] = "";
			 		else tempForcumentryValuesForCurYear[1] = ""+temp;
//tempForcumentryValuesForCurYear[1] = dataElementCodes[l];
 	         		
 	         		l++;
 	         		
			 		temp =  entryNumberValues[l];
 	         		if(temp==-1) tempForentryNumberValues[2] = "";
 	         		else tempForentryNumberValues[2] = ""+temp;
 	         		
 	         		temp = cumentryValuesForCurYear[l];
			 		if(temp==-1) tempForcumentryValuesForCurYear[2] = "";
			 		else tempForcumentryValuesForCurYear[2] = ""+temp;
//tempForcumentryValuesForCurYear[2] = dataElementCodes[l];

					l++;
 			 		
   	            } 
 	        %>
 	        
 	   
 
 	        
 	        
 	        
 	      	<tr>
    			<td id="<%=id1%>" name="<%=id1%>" width="2%" center style="border-collapse: collapse; border-top-style: solid; border-top-width: 1; border-bottom-style: solid; border-bottom-width: 1; border-left-style: solid; border-left-width: 1; border-right-style: solid; border-right-width: 1; padding-bottom:0; " bordercolor="#111111" align="left">&nbsp;</td>
    			<td id="<%=id2%>" name="<%=id2%>" width="34%" center style="border-collapse: collapse; border-top-style: solid; border-top-width: 1; border-bottom-style: solid; border-bottom-width: 1; border-left-style: solid; border-left-width: 1; border-right-style: solid; border-right-width: 1; padding-bottom:0; padding-left:15; padding-right:1; " bordercolor="#111111" ></td>
    			<td width="12%" center style="border-collapse: collapse; border-top-style: solid; border-top-width: 1; border-bottom-style: solid; border-bottom-width: 1; border-left-style: solid; border-left-width: 1; border-right-style: solid; border-right-width: 1; padding-bottom:0; padding-left:3; padding-right:1; " bordercolor="#111111" align="center" colspan="2"><font face="Arial" size="2"><%=tempForentryNumberValues[0]%></font></td>
    			<td width="10%" center style="border-collapse: collapse; border-top-style: solid; border-top-width: 1; border-bottom-style: solid; border-bottom-width: 1; border-left-style: solid; border-left-width: 1; border-right-style: solid; border-right-width: 1; padding-bottom:0; padding-left:3; padding-right:1; " bordercolor="#111111" align="center" colspan="2"><font face="Arial" size="2"><%=tempForentryNumberValues[1]%></font></td>
    			<td width="10%" center style="border-collapse: collapse; border-top-style: solid; border-top-width: 1; border-bottom-style: solid; border-bottom-width: 1; border-left-style: solid; border-left-width: 1; border-right-style: solid; border-right-width: 1; padding-bottom:0; padding-left:3; padding-right:1; " bordercolor="#111111" align="center" colspan="2"><font face="Arial" size="2"><%=tempForentryNumberValues[2]%></font></td>
    			<td width="12%" center style="border-collapse: collapse; border-top-style: solid; border-top-width: 1; border-bottom-style: solid; border-bottom-width: 1; border-left-style: solid; border-left-width: 1; border-right-style: solid; border-right-width: 1; padding-bottom:0; padding-left:3; padding-right:1; " bordercolor="#111111" align="center" colspan="2"><font face="Arial" size="2"><%=tempForcumentryValuesForCurYear[0]%></font></td>
    			<td width="10%" center style="border-collapse: collapse; border-top-style: solid; border-top-width: 1; border-bottom-style: solid; border-bottom-width: 1; border-left-style: solid; border-left-width: 1; border-right-style: solid; border-right-width: 1; padding-bottom:0; padding-left:3; padding-right:1; " bordercolor="#111111" align="center" colspan="2"><font face="Arial" size="2"><%=tempForcumentryValuesForCurYear[1]%></font></td>
    			<td width="10%" center style="border-collapse: collapse; border-top-style: solid; border-top-width: 1; border-bottom-style: solid; border-bottom-width: 1; border-left-style: solid; border-left-width: 1; border-right-style: solid; border-right-width: 1; padding-bottom:0; padding-left:3; padding-right:1; " bordercolor="#111111" align="center" colspan="2"><font face="Arial" size="2"><%=tempForcumentryValuesForCurYear[2]%></font></td>
			</tr>
 	   <%   }


 	    else if( (k>=43 && k<=45) || (k>=48 && k<=54) ||  (k>=120 && k<=124) || (k>=134 && k<=136) || (k>=161 && k<=194) || (k>=213 && k<=218))
 	      { 
                if(k==43 || k==48 || k==94 || k==120 || k==134 || k==161 || k==179) 
 	            {
 	              tempForentryNumberValues[0]= "<b>&#2730;&#2753;&#2736;&#2753;&#2743;</b>";
 	              tempForentryNumberValues[1]= "<b>&#2744;&#2765;&#2724;&#2765;&#2736;&#2752;</b>";
				  tempForentryNumberValues[2]= "<b>&#2730;&#2753;&#2736;&#2753;&#2743;</b>";
   	              tempForentryNumberValues[3]= "<b>&#2744;&#2765;&#2724;&#2765;&#2736;&#2752;</b>";
   	              tempForentryNumberValues[4]= "<b>&#2730;&#2753;&#2736;&#2753;&#2743;</b>";
 	              tempForentryNumberValues[5]= "<b>&#2744;&#2765;&#2724;&#2765;&#2736;&#2752;</b>";

 	              tempForcumentryValuesForCurYear[0] = "<b>&#2730;&#2753;&#2736;&#2753;&#2743;</b>";
 	              tempForcumentryValuesForCurYear[1] = "<b>&#2744;&#2765;&#2724;&#2765;&#2736;&#2752;</b>";
 	              tempForcumentryValuesForCurYear[2] = "<b>&#2730;&#2753;&#2736;&#2753;&#2743;</b>";
   	              tempForcumentryValuesForCurYear[3] = "<b>&#2744;&#2765;&#2724;&#2765;&#2736;&#2752;</b>";
   	              tempForcumentryValuesForCurYear[4] = "<b>&#2730;&#2753;&#2736;&#2753;&#2743;</b>";
 	              tempForcumentryValuesForCurYear[5] = "<b>&#2744;&#2765;&#2724;&#2765;&#2736;&#2752;</b>"; 	
 	              
				  l = l+6;	               	              
 	            }
 	           else 
 	            {
				temp =  entryNumberValues[l];
         		if(temp==-1) tempForentryNumberValues[0] = "";
         		else tempForentryNumberValues[0] = ""+temp;
 					
				temp = cumentryValuesForCurYear[l];
		 		if(temp==-1) tempForcumentryValuesForCurYear[0] = "";
		 		else tempForcumentryValuesForCurYear[0] = ""+temp;
//tempForcumentryValuesForCurYear[0] = dataElementCodes[l];

				l++;
				
				temp =  entryNumberValues[l];
         		if(temp==-1) tempForentryNumberValues[1] = "";
         		else tempForentryNumberValues[1] = ""+temp;
 					
				temp = cumentryValuesForCurYear[l];
		 		if(temp==-1) tempForcumentryValuesForCurYear[1] = "";
		 		else tempForcumentryValuesForCurYear[1] = ""+temp;
//tempForcumentryValuesForCurYear[1] = dataElementCodes[l];

				l++;

				temp =  entryNumberValues[l];
         		if(temp==-1) tempForentryNumberValues[2] = "";
         		else tempForentryNumberValues[2] = ""+temp;
 					
				temp = cumentryValuesForCurYear[l];
		 		if(temp==-1) tempForcumentryValuesForCurYear[2] = "";
		 		else tempForcumentryValuesForCurYear[2] = ""+temp;
//tempForcumentryValuesForCurYear[2] = dataElementCodes[l];

				l++;

				temp =  entryNumberValues[l];
         		if(temp==-1) tempForentryNumberValues[3] = "";
         		else tempForentryNumberValues[3] = ""+temp;
 					
				temp = cumentryValuesForCurYear[l];
		 		if(temp==-1) tempForcumentryValuesForCurYear[3] = "";
		 		else tempForcumentryValuesForCurYear[3] = ""+temp;
//tempForcumentryValuesForCurYear[3] = dataElementCodes[l];

				l++;

				temp =  entryNumberValues[l];
         		if(temp==-1) tempForentryNumberValues[4] = "";
         		else tempForentryNumberValues[4] = ""+temp;
 					
				temp = cumentryValuesForCurYear[l];
		 		if(temp==-1) tempForcumentryValuesForCurYear[4] = "";
		 		else tempForcumentryValuesForCurYear[4] = ""+temp;
//tempForcumentryValuesForCurYear[4] = dataElementCodes[l];

				l++;

				temp =  entryNumberValues[l];
         		if(temp==-1) tempForentryNumberValues[5] = "";
         		else tempForentryNumberValues[5] = ""+temp;
 					
				temp = cumentryValuesForCurYear[l];
		 		if(temp==-1) tempForcumentryValuesForCurYear[5] = "";
		 		else tempForcumentryValuesForCurYear[5] = ""+temp;
//tempForcumentryValuesForCurYear[5] = dataElementCodes[l];

				l++;
 	            } 

 	        %>
 	      	<tr>
    			<td id="<%=id1%>" name="<%=id1%>" width="2%" center style="border-collapse: collapse; border-top-style: solid; border-top-width: 1; border-bottom-style: solid; border-bottom-width: 1; border-left-style: solid; border-left-width: 1; border-right-style: solid; border-right-width: 1; padding-bottom:0; " bordercolor="#111111" align="left">&nbsp;</td>
    			<td id="<%=id2%>" name="<%=id2%>" width="34%" center style="border-collapse: collapse; border-top-style: solid; border-top-width: 1; border-bottom-style: solid; border-bottom-width: 1; border-left-style: solid; border-left-width: 1; border-right-style: solid; border-right-width: 1; padding-bottom:0; padding-left:15; padding-right:1; " bordercolor="#111111" ></td>
    			<td width="6%" center style="border-collapse: collapse; border-top-style: solid; border-top-width: 1; border-bottom-style: solid; border-bottom-width: 1; border-left-style: solid; border-left-width: 1; border-right-style: solid; border-right-width: 1; padding-bottom:0; padding-left:3; padding-right:1; " bordercolor="#111111" align="center"><font face="Arial" size="2"><%=tempForentryNumberValues[0]%></font></td>
    			<td width="6%" center style="border-collapse: collapse; border-top-style: solid; border-top-width: 1; border-bottom-style: solid; border-bottom-width: 1; border-left-style: solid; border-left-width: 1; border-right-style: solid; border-right-width: 1; padding-bottom:0; padding-left:3; padding-right:1; " bordercolor="#111111" align="center"><font face="Arial" size="2"><%=tempForentryNumberValues[1]%></font></td>
    			<td width="5%" center style="border-collapse: collapse; border-top-style: solid; border-top-width: 1; border-bottom-style: solid; border-bottom-width: 1; border-left-style: solid; border-left-width: 1; border-right-style: solid; border-right-width: 1; padding-bottom:0; padding-left:3; padding-right:1; " bordercolor="#111111" align="center"><font face="Arial" size="2"><%=tempForentryNumberValues[2]%></font></td>
    			<td width="5%" center style="border-collapse: collapse; border-top-style: solid; border-top-width: 1; border-bottom-style: solid; border-bottom-width: 1; border-left-style: solid; border-left-width: 1; border-right-style: solid; border-right-width: 1; padding-bottom:0; padding-left:3; padding-right:1; " bordercolor="#111111" align="center"><font face="Arial" size="2"><%=tempForentryNumberValues[3]%></font></td>
    			<td width="5%" center style="border-collapse: collapse; border-top-style: solid; border-top-width: 1; border-bottom-style: solid; border-bottom-width: 1; border-left-style: solid; border-left-width: 1; border-right-style: solid; border-right-width: 1; padding-bottom:0; padding-left:3; padding-right:1; " bordercolor="#111111" align="center"><font face="Arial" size="2"><%=tempForentryNumberValues[4]%></font></td>
    			<td width="5%" center style="border-collapse: collapse; border-top-style: solid; border-top-width: 1; border-bottom-style: solid; border-bottom-width: 1; border-left-style: solid; border-left-width: 1; border-right-style: solid; border-right-width: 1; padding-bottom:0; padding-left:3; padding-right:1; " bordercolor="#111111" align="center"><font face="Arial" size="2"><%=tempForentryNumberValues[5]%></font></td>
    			<td width="6%" center style="border-collapse: collapse; border-top-style: solid; border-top-width: 1; border-bottom-style: solid; border-bottom-width: 1; border-left-style: solid; border-left-width: 1; border-right-style: solid; border-right-width: 1; padding-bottom:0; padding-left:3; padding-right:1; " bordercolor="#111111" align="center"><font face="Arial" size="2"><%=tempForcumentryValuesForCurYear[0]%></font></td>
    			<td width="6%" center style="border-collapse: collapse; border-top-style: solid; border-top-width: 1; border-bottom-style: solid; border-bottom-width: 1; border-left-style: solid; border-left-width: 1; border-right-style: solid; border-right-width: 1; padding-bottom:0; padding-left:3; padding-right:1; " bordercolor="#111111" align="center"><font face="Arial" size="2"><%=tempForcumentryValuesForCurYear[1]%></font></td>
    			<td width="5%" center style="border-collapse: collapse; border-top-style: solid; border-top-width: 1; border-bottom-style: solid; border-bottom-width: 1; border-left-style: solid; border-left-width: 1; border-right-style: solid; border-right-width: 1; padding-bottom:0; padding-left:3; padding-right:1; " bordercolor="#111111" align="center"><font face="Arial" size="2"><%=tempForcumentryValuesForCurYear[2]%></font></td>
    			<td width="5%" center style="border-collapse: collapse; border-top-style: solid; border-top-width: 1; border-bottom-style: solid; border-bottom-width: 1; border-left-style: solid; border-left-width: 1; border-right-style: solid; border-right-width: 1; padding-bottom:0; padding-left:3; padding-right:1; " bordercolor="#111111" align="center"><font face="Arial" size="2"><%=tempForcumentryValuesForCurYear[3]%></font></td>
    			<td width="5%" center style="border-collapse: collapse; border-top-style: solid; border-top-width: 1; border-bottom-style: solid; border-bottom-width: 1; border-left-style: solid; border-left-width: 1; border-right-style: solid; border-right-width: 1; padding-bottom:0; padding-left:3; padding-right:1; " bordercolor="#111111" align="center"><font face="Arial" size="2"><%=tempForcumentryValuesForCurYear[4]%></font></td>
    			<td width="5%" center style="border-collapse: collapse; border-top-style: solid; border-top-width: 1; border-bottom-style: solid; border-bottom-width: 1; border-left-style: solid; border-left-width: 1; border-right-style: solid; border-right-width: 1; padding-bottom:0; padding-left:3; padding-right:1; " bordercolor="#111111" align="center"><font face="Arial" size="2"><%=tempForcumentryValuesForCurYear[5]%></font></td>
			</tr>
 	   <%   }



 else if( (k>=195 && k<=204))
 	      { 
                
				temp =  entryNumberValues[l];
         		if(temp==-1) tempForentryNumberValues[0] = "";
         		else tempForentryNumberValues[0] = ""+temp;
 					
				temp = cumentryValuesForCurYear[l];
		 		if(temp==-1) tempForcumentryValuesForCurYear[0] = "";
		 		else tempForcumentryValuesForCurYear[0] = ""+temp;
//tempForcumentryValuesForCurYear[0] = dataElementCodes[l];		 		

				l++;
				
				temp =  entryNumberValues[l];
         		if(temp==-1) tempForentryNumberValues[1] = "";
         		else tempForentryNumberValues[1] = ""+temp;
 					
				temp = cumentryValuesForCurYear[l];
		 		if(temp==-1) tempForcumentryValuesForCurYear[1] = "";
		 		else tempForcumentryValuesForCurYear[1] = ""+temp;
//tempForcumentryValuesForCurYear[1] = dataElementCodes[l];

				l++;

				temp =  entryNumberValues[l];
         		if(temp==-1) tempForentryNumberValues[2] = "";
         		else tempForentryNumberValues[2] = ""+temp;
 					
				temp = cumentryValuesForCurYear[l];
		 		if(temp==-1) tempForcumentryValuesForCurYear[2] = "";
		 		else tempForcumentryValuesForCurYear[2] = ""+temp;
//tempForcumentryValuesForCurYear[2] = dataElementCodes[l];

				l++;

				temp =  entryNumberValues[l];
         		if(temp==-1) tempForentryNumberValues[3] = "";
         		else tempForentryNumberValues[3] = ""+temp;
 					
				temp = cumentryValuesForCurYear[l];
		 		if(temp==-1) tempForcumentryValuesForCurYear[3] = "";
		 		else tempForcumentryValuesForCurYear[3] = ""+temp;
//tempForcumentryValuesForCurYear[3] = dataElementCodes[l];

				l++;

				temp =  entryNumberValues[l];
 	         	if(temp==-1) tempForentryNumberValues[4] = "";
 	         	else tempForentryNumberValues[4] = ""+temp;
 					
 				temp = cumentryValuesForCurYear[l];
			 	if(temp==-1) tempForcumentryValuesForCurYear[4] = "";
			 	else tempForcumentryValuesForCurYear[4] = ""+temp;
//tempForcumentryValuesForCurYear[4] = dataElementCodes[l];

				l++;

				temp =  entryNumberValues[l];
 	         	if(temp==-1) tempForentryNumberValues[5] = "";
 	         	else tempForentryNumberValues[5] = ""+temp;
 					
 				temp = cumentryValuesForCurYear[l];
			 	if(temp==-1) tempForcumentryValuesForCurYear[5] = "";
			 	else tempForcumentryValuesForCurYear[5] = ""+temp;
//tempForcumentryValuesForCurYear[5] = dataElementCodes[l];

				l++;

				temp =  entryNumberValues[l];
 	         	if(temp==-1) tempForentryNumberValues[6] = "";
 	         	else tempForentryNumberValues[6] = ""+temp;
 					
 				temp = cumentryValuesForCurYear[l];
			 	if(temp==-1) tempForcumentryValuesForCurYear[6] = "";
			 	else tempForcumentryValuesForCurYear[6] = ""+temp;
//tempForcumentryValuesForCurYear[6] = dataElementCodes[l];

				l++;
					
 				temp =  entryNumberValues[l];
 	         	if(temp==-1) tempForentryNumberValues[7] = "";
 	         	else tempForentryNumberValues[7] = ""+temp;
 					
 				temp = cumentryValuesForCurYear[l];
			 	if(temp==-1) tempForcumentryValuesForCurYear[7] = "";
			 	else tempForcumentryValuesForCurYear[7] = ""+temp;
//tempForcumentryValuesForCurYear[7] = dataElementCodes[l];

				l++;



 	        %>
 	      	<tr>
    			<td id="<%=id1%>" name="<%=id1%>" width="2%" style="border-collapse: collapse; border-top-style: solid; border-top-width: 1; border-bottom-style: solid; border-bottom-width: 1; border-left-style: solid; border-left-width: 1; border-right-style: solid; border-right-width: 1; " bordercolor="#111111" width="100%" align="left">&nbsp;</td>
    			<td id="<%=id2%>" name="<%=id2%>" width="34%" style="border-collapse: collapse; border-top-style: solid; border-top-width: 1; border-bottom-style: solid; border-bottom-width: 1; border-left-style: solid; border-left-width: 1; border-right-style: solid; border-right-width: 1; padding-left:15; padding-right:1; " bordercolor="#111111" width="100%">&nbsp;</td>
    			<td width="4%" style="border-collapse: collapse; border-top-style: solid; border-top-width: 1; border-bottom-style: solid; border-bottom-width: 1; border-left-style: solid; border-left-width: 1; border-right-style: solid; border-right-width: 1; padding-left:3; padding-right:1; " bordercolor="#111111" width="100%" align="center"><font face="Arial" size="2"><%=tempForentryNumberValues[0]%></font></td>
    			<td width="4%" style="border-collapse: collapse; border-top-style: solid; border-top-width: 1; border-bottom-style: solid; border-bottom-width: 1; border-left-style: solid; border-left-width: 1; border-right-style: solid; border-right-width: 1; padding-left:3; padding-right:1; " bordercolor="#111111" width="100%" align="center"><font face="Arial" size="2"><%=tempForentryNumberValues[1]%></font></td>
    			<td width="4%" style="border-collapse: collapse; border-top-style: solid; border-top-width: 1; border-bottom-style: solid; border-bottom-width: 1; border-left-style: solid; border-left-width: 1; border-right-style: solid; border-right-width: 1; padding-left:3; padding-right:1; " bordercolor="#111111" width="100%" align="center"><font face="Arial" size="2"><%=tempForentryNumberValues[2]%></font></td>
    			<td width="4%" style="border-collapse: collapse; border-top-style: solid; border-top-width: 1; border-bottom-style: solid; border-bottom-width: 1; border-left-style: solid; border-left-width: 1; border-right-style: solid; border-right-width: 1; padding-left:3; padding-right:1; " bordercolor="#111111" width="100%" align="center"><font face="Arial" size="2"><%=tempForentryNumberValues[3]%></font></td>
    			<td width="4%" style="border-collapse: collapse; border-top-style: solid; border-top-width: 1; border-bottom-style: solid; border-bottom-width: 1; border-left-style: solid; border-left-width: 1; border-right-style: solid; border-right-width: 1; padding-left:3; padding-right:1; " bordercolor="#111111" width="100%" align="center"><font face="Arial" size="2"><%=tempForentryNumberValues[4]%></font></td>
    			<td width="4%" style="border-collapse: collapse; border-top-style: solid; border-top-width: 1; border-bottom-style: solid; border-bottom-width: 1; border-left-style: solid; border-left-width: 1; border-right-style: solid; border-right-width: 1; padding-left:3; padding-right:1; " bordercolor="#111111" width="100%" align="center"><font face="Arial" size="2"><%=tempForentryNumberValues[5]%></font></td>
    			<td width="4%" style="border-collapse: collapse; border-top-style: solid; border-top-width: 1; border-bottom-style: solid; border-bottom-width: 1; border-left-style: solid; border-left-width: 1; border-right-style: solid; border-right-width: 1; padding-left:3; padding-right:1; " bordercolor="#111111" width="100%" align="center"><font face="Arial" size="2"><%=tempForentryNumberValues[6]%></font></td>
    			<td width="4%" style="border-collapse: collapse; border-top-style: solid; border-top-width: 1; border-bottom-style: solid; border-bottom-width: 1; border-left-style: solid; border-left-width: 1; border-right-style: solid; border-right-width: 1; padding-left:3; padding-right:1; " bordercolor="#111111" width="100%" align="center"><font face="Arial" size="2"><%=tempForentryNumberValues[7]%></font></td>
    			<td width="4%" style="border-collapse: collapse; border-top-style: solid; border-top-width: 1; border-bottom-style: solid; border-bottom-width: 1; border-left-style: solid; border-left-width: 1; border-right-style: solid; border-right-width: 1; padding-left:3; padding-right:1; " bordercolor="#111111" width="100%" align="center"><font face="Arial" size="2"><%=tempForcumentryValuesForCurYear[0]%></font></td>
    			<td width="4%" style="border-collapse: collapse; border-top-style: solid; border-top-width: 1; border-bottom-style: solid; border-bottom-width: 1; border-left-style: solid; border-left-width: 1; border-right-style: solid; border-right-width: 1; padding-left:3; padding-right:1; " bordercolor="#111111" width="100%" align="center"><font face="Arial" size="2"><%=tempForcumentryValuesForCurYear[1]%></font></td>
    			<td width="4%" style="border-collapse: collapse; border-top-style: solid; border-top-width: 1; border-bottom-style: solid; border-bottom-width: 1; border-left-style: solid; border-left-width: 1; border-right-style: solid; border-right-width: 1; padding-left:3; padding-right:1; " bordercolor="#111111" width="100%" align="center"><font face="Arial" size="2"><%=tempForcumentryValuesForCurYear[2]%></font></td>
    			<td width="4%" style="border-collapse: collapse; border-top-style: solid; border-top-width: 1; border-bottom-style: solid; border-bottom-width: 1; border-left-style: solid; border-left-width: 1; border-right-style: solid; border-right-width: 1; padding-left:3; padding-right:1; " bordercolor="#111111" width="100%" align="center"><font face="Arial" size="2"><%=tempForcumentryValuesForCurYear[3]%></font></td>
    			<td width="4%" style="border-collapse: collapse; border-top-style: solid; border-top-width: 1; border-bottom-style: solid; border-bottom-width: 1; border-left-style: solid; border-left-width: 1; border-right-style: solid; border-right-width: 1; padding-left:3; padding-right:1; " bordercolor="#111111" width="100%" align="center"><font face="Arial" size="2"><%=tempForcumentryValuesForCurYear[4]%></font></td>
    			<td width="4%" style="border-collapse: collapse; border-top-style: solid; border-top-width: 1; border-bottom-style: solid; border-bottom-width: 1; border-left-style: solid; border-left-width: 1; border-right-style: solid; border-right-width: 1; padding-left:3; padding-right:1; " bordercolor="#111111" width="100%" align="center"><font face="Arial" size="2"><%=tempForcumentryValuesForCurYear[5]%></font></td>
    			<td width="4%" style="border-collapse: collapse; border-top-style: solid; border-top-width: 1; border-bottom-style: solid; border-bottom-width: 1; border-left-style: solid; border-left-width: 1; border-right-style: solid; border-right-width: 1; padding-left:3; padding-right:1; " bordercolor="#111111" width="100%" align="center"><font face="Arial" size="2"><%=tempForcumentryValuesForCurYear[6]%></font></td>
    			<td width="4%" style="border-collapse: collapse; border-top-style: solid; border-top-width: 1; border-bottom-style: solid; border-bottom-width: 1; border-left-style: solid; border-left-width: 1; border-right-style: solid; border-right-width: 1; padding-left:3; padding-right:1; " bordercolor="#111111" width="100%" align="center"><font face="Arial" size="2"><%=tempForcumentryValuesForCurYear[7]%></font></td>
		  	</tr>
 	   <%   }

		else if((k>=125 && k<=132) || (k>=265 && k<=278))
		 { 
		 

		  	if(k==125 || k==130) 
 	            {
 	              	tempForentryNumberValues[0]= "<B>&#2709;&#2753;&#2738;</B>";
					tempForentryNumberValues[1]= "<B>&#2716;&#2751;&#2738;&#2765;&#2738;&#2750;&#2728;&#2750; &#2693;&#2727;&#2751;&#2709;&#2750;&#2736;&#2752; &#2727;&#2765;&#2741;&#2750;&#2736;&#2750;</B>";
					tempForentryNumberValues[2]= "<B>&#2716;&#2751;&#2738;&#2765;&#2738;&#2750;&#2728;&#2750; &#2693;&#2727;&#2751;&#2709;&#2750;&#2736;&#2752; &#2727;&#2765;&#2741;&#2750;&#2736;&#2750;</B>";

 	              	tempForcumentryValuesForCurYear[0] = "<B>&#2709;&#2753;&#2738;</B>";
					tempForcumentryValuesForCurYear[1] = "<B>&#2716;&#2751;&#2738;&#2765;&#2738;&#2750;&#2728;&#2750; &#2693;&#2727;&#2751;&#2709;&#2750;&#2736;&#2752; &#2727;&#2765;&#2741;&#2750;&#2736;&#2750;</B>";
					tempForcumentryValuesForCurYear[2] = "<B>&#2716;&#2751;&#2738;&#2765;&#2738;&#2750;&#2728;&#2750; &#2693;&#2727;&#2751;&#2709;&#2750;&#2736;&#2752; &#2727;&#2765;&#2741;&#2750;&#2736;&#2750;</B>";

 	                l= l+2;
 	             }
 	       	else  
 	        	{

					temp =  entryNumberValues[l];
 	         		if(temp==-1) tempForentryNumberValues[0] = "";
 	         		else tempForentryNumberValues[0] = ""+temp;
 					
 					temp = cumentryValuesForCurYear[l];
			 		if(temp==-1) tempForcumentryValuesForCurYear[0] = "";
			 		else tempForcumentryValuesForCurYear[0] = ""+temp;
//tempForcumentryValuesForCurYear[0] = dataElementCodes[l];

					
					temp =  entryNumberValues[l];
 	         		if(temp==-1) tempForentryNumberValues[1] = "";
 	         		else tempForentryNumberValues[1] = ""+temp;

					l++;

					temp =  entryNumberValues[l];
         			if(temp==-1) tempForentryNumberValues[2] = "";
         			else tempForentryNumberValues[2] = ""+temp;
 					
					temp = cumentryValuesForCurYear[l];
		 			if(temp==-1) tempForcumentryValuesForCurYear[1] = "";
		 			else tempForcumentryValuesForCurYear[1] = ""+temp;
//tempForcumentryValuesForCurYear[1] = dataElementCodes[l];

					temp =  entryNumberValues[l];
         			if(temp==-1) tempForentryNumberValues[3] = "";
         			else tempForentryNumberValues[3] = ""+temp;

					l++;
 	         		
 	        	}  

		 
		 %>
 	       	<tr>
    			<td id="<%=id1%>" name="<%=id1%>" width="2%" center style="border-collapse: collapse; border-top-style: solid; border-top-width: 1; border-bottom-style: solid; border-bottom-width: 1; border-left-style: solid; border-left-width: 1; border-right-style: solid; border-right-width: 1;  padding-bottom:0; " bordercolor="#111111" align="left">&nbsp;</td>
    			<td id="<%=id2%>" name="<%=id2%>" width="34%" center style="border-collapse: collapse; border-top-style: solid; border-top-width: 1; border-bottom-style: solid; border-bottom-width: 1; border-left-style: solid; border-left-width: 1; border-right-style: solid; border-right-width: 1;  padding-bottom:0; padding-left:15; padding-right:1; " bordercolor="#111111" ></td>
    			<td width="22%" center style="border-collapse: collapse; border-top-style: solid; border-top-width: 1; border-bottom-style: solid; border-bottom-width: 1; border-left-style: solid; border-left-width: 1; border-right-style: solid; border-right-width: 1; padding-left:3; padding-right:1;  padding-bottom:0;" bordercolor="#111111" align="center" colspan="4"><font face="Arial" size="2"><%=tempForentryNumberValues[0]%></font></td>
    			<td width="21%" center style="border-collapse: collapse; border-top-style: solid; border-top-width: 1; border-bottom-style: solid; border-bottom-width: 1; border-left-style: solid; border-left-width: 1; border-right-style: solid; border-right-width: 1; padding-left:3; padding-right:1;  padding-bottom:0;" bordercolor="#111111" align="center" colspan="4">&nbsp;</td>
    			<td width="21%" center style="border-collapse: collapse; border-top-style: solid; border-top-width: 1; border-bottom-style: solid; border-bottom-width: 1; border-left-style: solid; border-left-width: 1; border-right-style: solid; border-right-width: 1; padding-left:3; padding-right:1;  padding-bottom:0;" bordercolor="#111111" align="center" colspan="4">&nbsp;</td>
			</tr>   	       	

 	   <%   }

		else if( (k>=97 && k<=98) || (k>=206 && k<=212) || (k>=220 && k<=223))
		 { 
		 

		  	if(k==97 || k==192 || k==206 || k==220) 
 	            {
 	              	tempForentryNumberValues[0]= "<B>&#2709;&#2753;&#2738;</B>";
					tempForentryNumberValues[1]= "<B>&#2716;&#2751;&#2738;&#2765;&#2738;&#2750;&#2728;&#2750; &#2693;&#2727;&#2751;&#2709;&#2750;&#2736;&#2752; &#2727;&#2765;&#2741;&#2750;&#2736;&#2750;</B>";

 	              	tempForcumentryValuesForCurYear[0] = "<B>&#2709;&#2753;&#2738;</B>";
					tempForcumentryValuesForCurYear[1] = "<B>&#2716;&#2751;&#2738;&#2765;&#2738;&#2750;&#2728;&#2750; &#2693;&#2727;&#2751;&#2709;&#2750;&#2736;&#2752; &#2727;&#2765;&#2741;&#2750;&#2736;&#2750;</B>";

 	                l= l+2;
 	             }
 	       	else  
 	        	{

					temp =  entryNumberValues[l];
 	         		if(temp==-1) tempForentryNumberValues[0] = "";
 	         		else tempForentryNumberValues[0] = ""+temp;
 					
 					temp = cumentryValuesForCurYear[l];
			 		if(temp==-1) tempForcumentryValuesForCurYear[0] = "";
			 		else tempForcumentryValuesForCurYear[0] = ""+temp;
//tempForcumentryValuesForCurYear[0] = dataElementCodes[l];

					l++;

					temp =  entryNumberValues[l];
         			if(temp==-1) tempForentryNumberValues[1] = "";
         			else tempForentryNumberValues[1] = ""+temp;
 					
					temp = cumentryValuesForCurYear[l];
		 			if(temp==-1) tempForcumentryValuesForCurYear[1] = "";
		 			else tempForcumentryValuesForCurYear[1] = ""+temp;
//tempForcumentryValuesForCurYear[1] = dataElementCodes[l];

					l++;
 	         		
 	        	}  

		 
		 %>
   	       	
   	       	<tr>
    			<td id="<%=id1%>" name="<%=id1%>" width="2%" center style="border-collapse: collapse; border-top-style: solid; border-top-width: 1; border-bottom-style: solid; border-bottom-width: 1; border-left-style: solid; border-left-width: 1; border-right-style: solid; border-right-width: 1;  padding-bottom:0; " bordercolor="#111111" align="left">&nbsp;</td>
    			<td id="<%=id2%>" name="<%=id2%>" width="34%" center style="border-collapse: collapse; border-top-style: solid; border-top-width: 1; border-bottom-style: solid; border-bottom-width: 1; border-left-style: solid; border-left-width: 1; border-right-style: solid; border-right-width: 1;  padding-bottom:0; padding-left:15; padding-right:1; " bordercolor="#111111" ></td>
    			<td width="16%" center style="border-collapse: collapse; border-top-style: solid; border-top-width: 1; border-bottom-style: solid; border-bottom-width: 1; border-left-style: solid; border-left-width: 1; border-right-style: solid; border-right-width: 1; padding-left:3; padding-right:1;  padding-bottom:0;" bordercolor="#111111" align="center" colspan="3"><font face="Arial" size="2"><%=tempForentryNumberValues[0]%></font></td>
    			<td width="16%" center style="border-collapse: collapse; border-top-style: solid; border-top-width: 1; border-bottom-style: solid; border-bottom-width: 1; border-left-style: solid; border-left-width: 1; border-right-style: solid; border-right-width: 1; padding-left:3; padding-right:1;  padding-bottom:0;" bordercolor="#111111" align="center" colspan="3"><font face="Arial" size="2"><%=tempForentryNumberValues[1]%></font></td>
    			<td width="16%" center style="border-collapse: collapse; border-top-style: solid; border-top-width: 1; border-bottom-style: solid; border-bottom-width: 1; border-left-style: solid; border-left-width: 1; border-right-style: solid; border-right-width: 1; padding-left:3; padding-right:1;  padding-bottom:0;" bordercolor="#111111" align="center" colspan="3"><font face="Arial" size="2"><%=tempForcumentryValuesForCurYear[0]%></font></td>
    			<td width="16%" center style="border-collapse: collapse; border-top-style: solid; border-top-width: 1; border-bottom-style: solid; border-bottom-width: 1; border-left-style: solid; border-left-width: 1; border-right-style: solid; border-right-width: 1; padding-left:3; padding-right:1;  padding-bottom:0;" bordercolor="#111111" align="center" colspan="3"><font face="Arial" size="2"><%=tempForcumentryValuesForCurYear[1]%></font></td>
			</tr>

	<%	 }
 	     else 
 	      {   	       

 			if(k==205) { %>
							</table> 	    
 	    					<br>
 	    					<table cellpadding="0" cellspacing="0" style="border-collapse: collapse; border-left-style: solid; border-left-width: 1; border-right-style: solid; border-right-width: 1; border-top-style: solid; border-top-width: 1; border-bottom-style: solid; border-bottom-width: 1;padding: 0" bordercolor="#111111" width="100%">
 	   			 		<% }

 	         if(k==0 || k==83 || k==112 || k==115 || k==141 || k==154 || k==205 || k==219) 
 	            {
 	              tempForentryNumberValues[0]= "<B>&#2714;&#2750;&#2738;&#2753; &#2734;&#2750;&#2744; &#2727;&#2736;&#2734;&#2765;&#2735;&#2750;&#2728;</B>";
 	              tempForcumentryValuesForCurYear[0] = "<B>&#2703;&#2730;&#2765;&#2736;&#2752;&#2738;&#2735;&#2752; &#2741;&#2727;&#2724;&#2752; &#2716;&#2724;&#2752; &#2709;&#2750;&#2734;&#2711;&#2752;&#2736;&#2752;</B>";
 	              
 	              l= l+1;
 	             }
 	       	  if(k==279)
 	       	    {
 	              tempForentryNumberValues[0]= "<B>&#2724;&#2690;&#2711;&#2752;&#2741;&#2750;&#2739;&#2750; &#2730;&#2765;&#2736;&#2750;.&#2694;.&#2709;&#2759;/&#2744;&#2750;.&#2694;.&#2709;&#2759;.&#2728;&#2752; &#2744;&#2690;&#2710;&#2765;&#2735;&#2750;</B>";
 	              tempForcumentryValuesForCurYear[0] = "<B>&#2724;&#2690;&#2711;&#2752;&#2728;&#2752; &#2719;&#2709;&#2750;&#2741;&#2750;&#2736;&#2752; (&#2709;&#2738; &#2744;&#2690;&#2732;&#2727;&#2751;&#2724; &#2709;&#2759;&#2728;&#2765;&#2726;&#2765;&#2736;&#2763; &#2744;&#2750;&#2734;&#2759;)</B>";
 	       	     
 	       	    }
 	       	  else  
 	        	{

					temp =  entryNumberValues[l];
 	         		if(temp==-1) tempForentryNumberValues[0] = "";
 	         		else tempForentryNumberValues[0] = ""+temp;
 					
 					temp = cumentryValuesForCurYear[l];
			 		if(temp==-1) tempForcumentryValuesForCurYear[0] = "";
			 		else tempForcumentryValuesForCurYear[0] = ""+temp;
//tempForcumentryValuesForCurYear[0] = dataElementCodes[l];

					l++;
 	         		
 	        	}  
 	       %>
 	       	<tr>
    			<td id="<%=id1%>" name="<%=id1%>" width="2%" center style="border-collapse: collapse; border-top-style: solid; border-top-width: 1; border-bottom-style: solid; border-bottom-width: 1; border-left-style: solid; border-left-width: 1; border-right-style: solid; border-right-width: 1;  padding-bottom:0; " bordercolor="#111111" align="left">&nbsp;</td>
    			<td id="<%=id2%>" name="<%=id2%>" width="34%" center style="border-collapse: collapse; border-top-style: solid; border-top-width: 1; border-bottom-style: solid; border-bottom-width: 1; border-left-style: solid; border-left-width: 1; border-right-style: solid; border-right-width: 1;  padding-bottom:0; padding-left:15; padding-right:1; " bordercolor="#111111" ></td>
    			<td width="32%" center style="border-collapse: collapse; border-top-style: solid; border-top-width: 1; border-bottom-style: solid; border-bottom-width: 1; border-left-style: solid; border-left-width: 1; border-right-style: solid; border-right-width: 1; padding-left:3; padding-right:1;  padding-bottom:0;" bordercolor="#111111" align="center" colspan="6"><font face="Arial" size="2"><%=tempForentryNumberValues[0]%></font></td>
    			<td width="32%" center style="border-collapse: collapse; border-top-style: solid; border-top-width: 1; border-bottom-style: solid; border-bottom-width: 1; border-left-style: solid; border-left-width: 1; border-right-style: solid; border-right-width: 1; padding-left:3; padding-right:1;  padding-bottom:0;" bordercolor="#111111" align="center" colspan="6"><font face="Arial" size="2"><%=tempForcumentryValuesForCurYear[0]%></font></td>
			</tr>
 	     	<% 
 	     }    	  	   
		 k++;
		}		
		 j++;
		%>
		</table>
		<%
	 }	
 %>
</BODY>
</HTML>