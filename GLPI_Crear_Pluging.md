NOTA:  Esta página es una traducción automática (ligeramente revisada) de la correspondiente información en francés en la documentación de GLPI.
Esta traducción se hizo en Septiembre de 2008 por lo que puede estar desactualizada.

A plugin is an add code, which allows features to enrich GLPI without having to add patches to the main code of the application.
All plugins are stored in the subversion of GLPI, at[https://dev.indepnet.net/plugins/browser/].

There is a plugin example, having all
features [https://dev.indepnet.net/plugins/browser/example].
The plugin room is a simple case also fairly complete [https://dev.indepnet.net/plugins/browser/room]

# Functionality of a plugin GLPI #
The plug-in is in the form of a file to add to the plugins directory tree GLPI.
This folder will contain all the php files (and images for example).

'' 'This directory should understand that alpahnumÃ©riques characters (no - _or other)'''_

A plugin never modify tables GLPI. It merely add tables in the database, to manage its own data.

# How to write a plugin for GLPI ? #

## Programming Conventions ##

1 - Names of tables

  * Your table names must respect this type glpi\_plugin

<plugin\_name>

_XXXX
  * If you use dropdown, please use the table: glpi\_dropdown\_plugin_

<plugin\_name>

_XXXX_

2 - Although adhere architecture creation plugins:

  * A directory of "local": dictionaries
    * The dictionaries will read fr\_FR.php / en\_GB.php
  * A directory "docs":
    * Readme.txt, Readme.txt, Roadmap.txt, Changelog.txt
  * A directory "inc": contains classes and functions
    * The file names will be tagged:  example plugin

<plugin\_name>

.functions\_db.php
    * The classes will be created: class plugin

<plugin\_name>


    * The functions will be tagged: function plugin

<plugin\_name>

_showform
  * A directory "front": contains the forms
    * The file names will be tagged: example plugin_

<plugin\_name>

.form.php
  * A directory "pics": contains images
  * At the root:
    * At least one index.php and setup.php

  * If your plugin must create files, they must be created in the files under GLPI files\_plugins/plugin\_name.

This avoid management writing rights  for each release of your plugin.
The rights when installing or updating GLPI will apply.
All this in order not to interfere with the core GLPI.


3 - Look and Appearance of plugin:

For that fits best in GLPI the best is to use class layout already defined and used in GLPI.
You find all these definitions in the CSS file GLPI.

Thank you to respect in order to harmonize the plugins and their future developments.

## Structure file a plugin ##
The minimum structure is as follows:
`glpi/plugins/PLUGIN_NAME` : directory plugin
`glpi/plugins/PLUGIN_NAME/setup.php` : file that contains the code of the plugin.

You can also add as many files as you wish.

The file setup.php == ==

This file must contain at least 2 functions:

(((plugin\_init\_PLUGIN\_NAME()}}}: defines the configuration plugin, what it does ... This function will be launched at each call page GLPI.
(((plugin\_version\_PLUGIN\_NAME()}}}: defines the name and version of the plugin

The function plugin\_init\_PLUGIN\_NAME will identify all behaviors plugin which may be of different type:
  * Integration of visual plugin in GLPI
  * Hooks / actions performed on specific event
  * Location
  * Management of dropdown
  * Using the search engine
  * Interactions range planning, redirect, cron, ...


### Visual integration ###

The existence of the plugin is done by defining first plugin\_version\_PLUGIN\_NAME function like this:
```
function plugin_version_PLUGIN_NAME(){
	return array( 'name'    => 'Name of my plugin',
			'version' => '0.0.1');
}
```

With in the function plugin\_init\_PLUGIN\_NAME may be added instructions to activate certain features of the plugin.

- `$PLUGIN_HOOKS['helpdesk_menu_entry']['PLUGIN_NAME'] = true;` : indicates whether the plugin should be displayed in the list of plugins menu interface helpdesk.

- `$PLUGIN_HOOKS['menu_entry']['PLUGIN_NAME'] = true;` : indicates whether the plug should be displayed in the main menu interface list of plugins.

- `$PLUGIN_HOOKS['submenu_entry']['PLUGIN_NAME'][ACTION] = LINK;` : Specify if necessary sub-menus to the main interface.
`ACTION` defines the element displayed in the submenu. Actions are defined by default in GLPI (add, search, template, and showall summary).
For these actions standard icons will be used. But you can also choose to display a text or a personal icon (see example plugin).

or:

- `$PLUGIN_HOOKS['submenu_entry']['PLUGIN_NAME'][ACTION][OPTION] = LINK;` : Specify if necessary sub-menus to the central interface.
You can display a custom link according to the Options settings. This allows to change the link depending on the sub-section where you are.

- `$PLUGIN_HOOKS['config_page']['PLUGIN_NAME'] = 'config.php';` : Page tree plugin appearing in Configuration / Plugins.

- 0.71 : `$PLUGIN_HOOKS['central_action']['PLUGIN_NAME'] = 'plugin_central_action_PLUGIN_NAME';` : Definition of a specific page for the instrument panel center.
plugin\_central\_action\_PLUGIN\_NAME is a function without setting :
```
// Hook to be launch on central
function plugin_central_action_PLUGIN_NAME(){
	echo "<div align='center'>";
	echo "Plugin central action";
	echo "</div>";
}
```
'''en 0.72 mÃªme systÃ¨me que les headings classiques avec comme type ''central'' '''


- `$PLUGIN_HOOKS['headings']['PLUGIN_NAME'] = 'plugin_get_headings_PLUGIN_NAME';` :
Function defining tabs in entries of different types of GLPI.
0.72: 2 types can be used: central and prefs for display on the homepage of the central and user preferences.
This function, which is defined in the setup.php, then takes the following form:
```
function plugin_get_headings_example($type,$withtemplate){
	switch ($type){
		case COMPUTER_TYPE :
			// template case
			if ($withtemplate){
				return array();
			} else { // Non template case
				return array(1 => "Test Plugin");
                        }
			break;
		case ENTERPRISE_TYPE :
			return array(
					1 => "Test Plugin",
					2 => "Test Plugin 2",
				    );
			break;
	}
	return false;
}
```

- `$PLUGIN_HOOKS['headings_action']['PLUGIN_NAME'] = 'plugin_headings_actions_PLUGIN_NAME';` : Function defines the actions taken by the tabs for different types of GLPI
This function, defined in the setup.php, then takes the following form (each definition is the name of the function to be performed) :
```
// Define headings actions added by the plugin	 
function plugin_headings_actions_example($type){

	switch ($type){
		case COMPUTER_TYPE :
			return array(1 => "plugin_example_function1");

			break;
		case ENTERPRISE_TYPE :
			return array(
					1 => "plugin_example_function2",
					2 => "plugin_example_function3",
				    );
			break;
	}
	return false;
}
```

The display functions has the following prototype :
```
// Example of an action heading
function plugin_example_function2($type,$ID,$withtemplate=0){
	if (!$withtemplate){
		echo "<div align='center'>";
		echo "Plugin function with headings TYPE=".$type." ID=".$ID;
		echo "</div>";
	}
}
```


### Hooks on actions of GLPI ###

A plugin can be grafted on each action carried out in GLPI to carry out their own treatments.
> For that shows the names of functions to be executed at that time.
```
  $PLUGIN_HOOKS['item_update']['PLUGIN_NAME'] = 'plugin_item_update_PLUGIN_NAME'; 
  $PLUGIN_HOOKS['item_add']['PLUGIN_NAME'] = 'plugin_item_add_PLUGIN_NAME';
  $PLUGIN_HOOKS['item_delete']['PLUGIN_NAME'] = 'plugin_item_delete_PLUGIN_NAME';
  $PLUGIN_HOOKS['item_purge']['PLUGIN_NAME'] = 'plugin_item_purge_PLUGIN_NAME';
  $PLUGIN_HOOKS['item_restore']['PLUGIN_NAME'] = 'plugin_item_restore_PLUGIN_NAME';
```
> These functions use the following prototype ($parm fields containing 2 'type' and 'ID' defining the type and ID of the element on which was directed action):
```
// Hook done on add item case
function plugin_item_add_PLUGIN_NAME($parm){

	if (isset($parm["type"]))
		switch ($parm["type"]){
			case COMPUTER_TYPE :
				if (!empty($_SESSION["MESSAGE_AFTER_REDIRECT"]))
                                   S_SESSION["MESSAGE_AFTER_REDIRECT"].="<br>";
				$_SESSION["MESSAGE_AFTER_REDIRECT"].=
                                     "Add Computer Hook ".$parm["ID"];
				return true;
				break;
		}
	return false;
}
```
[[Information sur les hooks](https://dev.indepnet.net/plugins/wiki/HowtoHook)]

### Localization ###

All of the plugin texts to be displayed in GLPI should be outsourced to a file so that they can easily translate the interface (just need to translate a file).
The language files are in the local /
The Convention for the naming of a language is the same as GLPI and uses the standard i18n:
  * En\_US for France,
  * En\_GB for England

Within the file, the labels are all stored in a table from which the following example:
```
  $LANGMYPLUGIN["title"][0] = "mon titre";
  $LANGMYPLUGIN["myplugin"][1] = "libellÃ© 1";
  $LANGMYPLUGIN["myplugin"][2] = "libellÃ© 2";
```

The language file corresponding to the language of the user will dynamically loaded.
If no exists, GLPI attempts to load successively the default language, English and French.

### Various interactions ###

#### Adding Javascript and CSS ####

You can add JavaScript to your own needs as well as css files using the following definitions:
{{{$PLUGIN\_HOOKS['add\_javascript']['PLUGIN\_NAME']="FILENAME.js";}}
$PLUGIN\_HOOKS['add\_css']['PLUGIN\_NAME']="FILENAME.css";}}}


#### Planning ####

Two functions can be defined and used to populate the planning that will be displayed and how the items will be displayed:
> - To populate the schedule:
```
$PLUGIN_HOOKS['planning_populate']['PLUGIN_NAME']="plugin_planning_populate_PLUGIN_NAME";
```
The function sets and fills a table whose elements can be sorted according to their keys.
For example:
```
// Parm contains 'begin', 'end' and 'who'
// Create data to be displayed in the planning of $parm["who"] between $parm["begin"] and $parm["end"] 
function plugin_planning_populate_example($parm){

	// Add items in the items fields of the parm array
	// Items need to have an unique index beginning by the begin date of the item to display
	// needed to be correcly displayed

	list($date,$time)=split(" ",$parm["begin"]);
	$end=$date." 13:33:00";

	$parm["items"][$parm["begin"]."$$$"."plugin_example1"]["plugin"]="example";
	$parm["items"][$parm["begin"]."$$$"."plugin_example1"]["begin"]=$parm["begin"];
	$parm["items"][$parm["begin"]."$$$"."plugin_example1"]["end"]=$end;
	$parm["items"][$parm["begin"]."$$$"."plugin_example1"]["name"]="test planning example 1 ";

	return $parm;
}
```

> - To set the display in the planning:
```
$PLUGIN_HOOKS['display_planning']['example']="plugin_display_planning_PLUGIN_NAME";
```
```
// Display the planning item
function plugin_display_planning_PLUGIN_NAME($parm){
	// $parm["type"] say begin end in or from type
	// Add items in the items fields of the parm array
	global $LANG;
	switch ($parm["type"]){
		case "in":
			echo date("H:i",strtotime($parm["begin"]))." -> ".date("H:i",strtotime($parm["end"])).": ";
			break;
		case "from":
			break;
		case "begin";
			echo $LANG["planning"][19]." ".date("H:i",strtotime($parm["begin"])).": ";
			break;
		case "end";
			echo $LANG["planning"][20]." ".date("H:i",strtotime($parm["end"])).": ";
			break;
	}
	echo $parm["name"];
}
```


#### Cron ####

You can add a scheduled task by plugin.
To do this simply define:
`$PLUGIN_HOOKS['cron']['PLUGIN_NAME'] = DAY_TIMESTAMP;`
This directive will launch function `cron_plugin_PLUGIN_NAME ((()))` on a regular basis in the background (here all day).

#### Redirecting ####

The call to the login page allows for automatic redirection (eg for access when sending a link via email).
You can perform an automatic redirection to a page on your plugin by defining:
`$PLUGIN_HOOKS['redirect_page']['PLUGIN_NAME']="PAGE_TO_REDIRECT.php";`
call to `index.php?redirect=plugin_PLUGINNAME_IDTOREDIRECT`  redirect to  `plugins/PLUGINNAME/PAGE_TO_REDIRECT.php?ID=IDTOREDIRECT`

## Using the API GLPI ##

### Management dropdown ###

To use as dropdown.function.php as dropdownValue, simply select the function that defines plugin\_PLUGIN\_NAME\_getDropdown plugins:

```
// Define Dropdown tables to be manage in GLPI :
function plugin_example_getDropdown(){
	// Table => Name
	return array("glpi_dropdown_plugin_example"=>"Plugin Example Dropdown");
}
```

For a simple dropdown enough is enough. For a dropdown tree you must specify that the table is like this:

` array_push($CFG_GLPI["dropdowntree_tables"],"glpi_dropdown_plugin_PLUGIN_NAME");`

If it is a dropdown for each separate entity:
` array_push($CFG_GLPI["specif_entities_tables"],"glpi_dropdown_plugin_PLUGIN_NAME");`

To manage automatically publishing elements of the dropdown with replacement or removal of values in removing you can describe the relationship between the tables of your plugins adding function:
```
// Define dropdown relations
function plugin_example_getDatabaseRelations(){
	// 
	return array("glpi_dropdown_plugin_example"=>array("glpi_plugin_example"=>"FK_dropdown"));
}
```
Here the field FK\_dropdown of glpi\_plugin\_example is linked to the table glpi\_dropdown\_plugin\_example.

### Definition of a new type of object ###

For this you can use the pluginNewType:
`pluginNewType('example',"PLUGIN_EXAMPLE_TYPE",1001,"pluginExample","glpi_plugin_example","example.form.php","Example",false);`

It takes as a parameter: the name of the plugin / string defining the type / number of type / associated class / the associated table / page form associated if it exists / type name / if the type is recursive.

The numbers of the types of plugins are booked per 100 here: PluginTypesReservation. They start from 1000.

To use the haveTypeRight GLPI you must define how the rights are managed on this new object by redefining the function:
```
// Define rights for the plugin types
function plugin_example_haveTypeRight($type,$right){
	switch ($type){
		case PLUGIN_EXAMPLE_TYPE :
			// 1 - All rights for all users
			// return true;
			// 2 - Similarity right : same right of computer
			return haveRight("computer",$right);
                        // 3 - Specific right management
			break;
	}
}
```
The definition of the rights is needed to use the mass action system of the search system (only available for users who have 'w' right on the defined type).

### Utiliser CommonDBTM ###

If you define a new type you must also define the class associated with this new type.
It must inherit the class CommonDBTM that allows abstract of all direct access to the database.
Its minimal definition is like:
```
class pluginExample extends CommonDBTM {
	function pluginExample () {
		$this->table="glpi_plugin_example";
		$this->type=PLUGIN_EXAMPLE_TYPE;
	}
};
```


For more information on the functions you can override the documentation for this class is here:
https://dev.indepnet.net/glpidoc/classCommonDBTM.html

### Using the search engine ###

You must first define a new type (see above).
You can define the elements displayed in your search engine by defining the function plugin\_PLUGINNAME\_getSearchOption:

```
function plugin_example_getSearchOption(){
	global $LANGEXAMPLE;
	$sopt=array();

        // Part header
	$sopt[PLUGIN_EXAMPLE_TYPE]['common']="Header Needed";


	$sopt[PLUGIN_EXAMPLE_TYPE][1]['table']='glpi_plugin_example';
	$sopt[PLUGIN_EXAMPLE_TYPE][1]['field']='name';
	$sopt[PLUGIN_EXAMPLE_TYPE][1]['linkfield']='name';
	$sopt[PLUGIN_EXAMPLE_TYPE][1]['name']=$LANGEXAMPLE["name"];

	$sopt[PLUGIN_EXAMPLE_TYPE][2]['table']='glpi_dropdown_plugin_example';
	$sopt[PLUGIN_EXAMPLE_TYPE][2]['field']='name';
	$sopt[PLUGIN_EXAMPLE_TYPE][2]['linkfield']='FK_dropdown';
	$sopt[PLUGIN_EXAMPLE_TYPE][2]['name']='Dropdown';
	
	return $sopt;
}
```


The search engine then called simply:
```
manageGetValuesInSearch(PLUGIN_EXAMPLE_TYPE);

searchForm(PLUGIN_EXAMPLE_TYPE,$_SERVER['PHP_SELF'],$_GET["field"],$_GET["contains"],$_GET["sort"],
$_GET["deleted"],$_GET["link"],$_GET["distinct"],$_GET["link2"],$_GET["contains2"],$_GET["field2"],
$_GET["type2"]);

showList(PLUGIN_EXAMPLE_TYPE,$_SERVER['PHP_SELF'],$_GET["field"],$_GET["contains"],$_GET["sort"],
$_GET["order"],$_GET["start"],$_GET["deleted"],$_GET["link"],$_GET["distinct"],$_GET["link2"],
$_GET["contains2"],$_GET["field2"],$_GET["type2"]);
```


You can also define specific cases of research by defining one or more of the following if necessary:

```

function plugin_example_addLeftJoin($type,$ref_table,$new_table,$linkfield,&$already_link_tables){
	switch ($new_table){
		case "glpi_dropdown_plugin_example" :
			// Standard LEFT JOIN for the example but use it for specific jointures
			return " LEFT JOIN $new_table ON ($ref_table.$linkfield = $new_table.ID) ";
			break;
	}
	return "";
}


function plugin_example_forceGroupBy($type){
	switch ($type){
		case PLUGIN_EXAMPLE_TYPE :
                        // Force add GROUP BY IN REQUEST
			return true;
			break;
	}
	return false;
}

function plugin_example_giveItem($type,$field,$data,$num,$linkfield=""){
	global $CFG_GLPI, $INFOFORM_PAGES;

	switch ($field){
		case "glpi_plugin_example.name" :
			$out= "<a href=\"".$CFG_GLPI["root_doc"]."/".$INFOFORM_PAGES[$type]."?ID=".$data['ID']."\">";
			$out.= $data["ITEM_$num"];
			if ($CFG_GLPI["view_ID"]||empty($data["ITEM_$num"])) $out.= " (".$data["ID"].")";
			$out.= "</a>";
			return $out;
			break;
	}
	return "";
}

function plugin_example_addWhere($link,$nott,$type,$ID,$val){
	global $SEARCH_OPTION;

	$table=$SEARCH_OPTION[$type][$ID]["table"];
	$field=$SEARCH_OPTION[$type][$ID]["field"];
	
	$SEARCH=makeTextSearch($val,$nott);

	switch ($table.".".$field){
		case "glpi_plugin_example.name" :
			// Standard Where clause for the example but use it for specific jointures
			$ADD="";	
			if ($nott&&$val!="NULL") {
				$ADD=" OR $table.$field IS NULL";
			}
			
			return $link." ($table.$field $SEARCH ".$ADD." ) ";
			break;
	}
	return "";
}

function plugin_example_addSelect($type,$ID,$num){
	global $SEARCH_OPTION;

	$table=$SEARCH_OPTION[$type][$ID]["table"];
	$field=$SEARCH_OPTION[$type][$ID]["field"];

	switch ($table.".".$field){
		case "glpi_plugin_example.name" :
			// Standard Select clause for the example but use it for specific selection
			return $table.".".$field." AS ITEM_$num, ";
			break;
	}
	return "";
}

function plugin_example_addOrderBy($type,$ID,$order,$key=0){
	global $SEARCH_OPTION;

	$table=$SEARCH_OPTION[$type][$ID]["table"];
	$field=$SEARCH_OPTION[$type][$ID]["field"];

	switch ($table.".".$field){
		case "glpi_plugin_example.name" :
			// Standard Order By clause for the example but use it for specific selection
			return " ORDER BY $table.$field $order ";
			break;
	}
	return "";
}

```


### Defining actions modified mass ###

By default type action: modify / delete / purge / restore are accessible from the lists.

To add specific actions we must first add:
$PLUGIN\_HOOKS['use\_massive\_action']['PLUGIN\_NAME']=1;


In the case change: for fields with specific patterns of seizures you can define in defining the function: plugin\_PLUGINNAME\_MassiveActionsFieldsDisplay ($type, $table, $field, $linkfield).
It must return true if the element is displayed and false otherwise (in this case the display default will be applied).
An example:
```
// How to display specific update fields ?

function plugin_example_MassiveActionsFieldsDisplay($type,$table,$field,$linkfield){
	global $LINK_ID_TABLE;
	if ($table==$LINK_ID_TABLE[$type]){
		// Table fields
		switch ($table.".".$field){
			case 'glpi_plugin_example.serial':
				echo "Not really specific - Just for example&nbsp;";
				autocompletionTextField($linkfield,$table,$field);
				// dropdownYesNo($linkfield);
				// Need to return true if specific display
				return true;
			break;
		}

	} else {
		// Linked Fields
		switch ($table.".".$field){
			case "glpi_dropdown_plugin_example.name" :
				echo "Not really specific - Just for example&nbsp;";
				dropdown($table,$linkfield,1,$_SESSION["glpiactive_entity"]);
				//dropdownUsers($linkfield,0,"own_ticket",0,1,$_SESSION["glpiactive_entity"]);
 				// Need to return true if specific display
				return true;
				break;
		}
	}
	// Need to return false on non display item
	return false;
}
```


You can also define new actions massive changes in defining the following functions:
```
// Define actions : 

function plugin_example_MassiveActions($type){
	global $LANG;
	switch ($type){
                // New action for core type : name = plugin_PLUGINNAME_actionname
		case COMPUTER_TYPE :
			return array(
				"plugin_example_DoIt"=>"plugin_example_DoIt",
			);
			break;
		case PLUGIN_EXAMPLE_TYPE:
			return array(
				// GLPI core one
				"add_document"=>$LANG["document"][16],
				// Specific one
				"do_nothing"=>'Do Nothing - just for fun'
				);
		break;
	}
	return array();
}

// How to display specific actions ?

function plugin_example_MassiveActionsDisplay($type,$action){
	global $LANG;
	switch ($type){
		case COMPUTER_TYPE:
			switch ($action){
				case "plugin_example_DoIt":
				echo "&nbsp;<input type=\"submit\" name=\"massiveaction\" class=\"submit\" value=\"".$LANG["buttons"][2]."\" >&nbsp;but do nothing :)";
				break;
			}
			break;
		case PLUGIN_EXAMPLE_TYPE:
			switch ($action){
				// No case for add_document : use GLPI core one
				case "do_nothing":
					echo "&nbsp;<input type=\"submit\" name=\"massiveaction\" class=\"submit\" value=\"".$LANG["buttons"][2]."\" >&nbsp;but do nothing :)";
				break;
			}
		break;
	}
	return "";
}

// How to process specific actions ?

function plugin_example_MassiveActionsProcess($data){
	global $LANG;

	if (!isset($_SESSION["MESSAGE_AFTER_REDIRECT"])) $_SESSION["MESSAGE_AFTER_REDIRECT"]="";

	switch ($data['action']){
		case 'plugin_example_DoIt':
			if ($data['device_type']==COMPUTER_TYPE){
				$ci =new CommonItem();
				$_SESSION["MESSAGE_AFTER_REDIRECT"].= "Right it is the type I want...<br>";
				$_SESSION["MESSAGE_AFTER_REDIRECT"].= "But... I say I will do nothing for :<br>";
				foreach ($data['item'] as $key => $val){
					if ($val==1) {
						if ($ci->getFromDB($data["device_type"],$key)){
						$_SESSION["MESSAGE_AFTER_REDIRECT"].= "- ".$ci->getField("name")."<br>";
						}
					}
				}
			}
			break;
		case 'do_nothing':
			if ($data['device_type']==PLUGIN_EXAMPLE_TYPE){
				$ci =new CommonItem();
				$_SESSION["MESSAGE_AFTER_REDIRECT"].= "Right it is the type I want...<br>";
				$_SESSION["MESSAGE_AFTER_REDIRECT"].= "But... I say I will do nothing for :<br>";
				foreach ($data['item'] as $key => $val){
					if ($val==1) {
						if ($ci->getFromDB($data["device_type"],$key)){
							$_SESSION["MESSAGE_AFTER_REDIRECT"].= "- ".$ci->getField("name")."<br>";
						}
					}
				}
			}
		break;
	}

}
```

### Customizing the dynamic export ###

It is possible if the dynamic export standard must be customized to do so by defining the function plugin\_PLUGINNAME\_dynamicReport ($parm).
$parm parameters being awarded $_GET from Pager (printPager function)._

```
// Do special actions for dynamic report

function plugin_example_dynamicReport($parm){
	if ($parm["item_type"]==PLUGIN_EXAMPLE_TYPE){
		// Do all what you want for export depending on $parm 
		echo "Personalized export for type ".$parm["display_type"];
		// Return true if personalized display is done
		return true;
	}
	// Return false if no specific display is done, then use standard display
	return false;
}
```


We can define data in addition to taking the dynamic export function by defining the function: plugin\_PLUGINNAME\_addParamFordynamicReport.
The search parameters are accessible via session variables.

```
// Add parameters to printPager in search system

function plugin_example_addParamFordynamicReport($device_type){
	if ($device_type==PLUGIN_EXAMPLE_TYPE){
		// Return array data containing all params to add : may be single data or array data
		// Search config are available from session variable
		return array(
			'add1' => $_SESSION['glpisearch'][$device_type]['order'],
			'add2' => array('tutu'=>'Second Add','Other Data'));
	}
	// Return false or a non array data if not needed
	return false;
}
```