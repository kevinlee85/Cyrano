<?php

require_once "DB_Connector.php";

class UserScripts {
	
	/*
	* Update the user's current Bluetooth address
	*/
	/*public function put($token = null, $userId) {
		if (! isset($token)) {
            $response = new Response(false, "You must be logged in to access this feature.", null, 403);
            return $response->getJSONData();
		}
		
		if (! isset($userId)) {
			$response = new Response(false, "You must provide a User ID.", null, 400);
            return $response->getJSONData();
		}

		
		/* Connect to the database
		$db = new DB_Connector();
		$cnx = $db->getConnection();
		$dbname=$GLOBALS['db'];
		if ($db->verifyToken($token, $userId) || $GLOBALS['opencall']==1) {
			
			/* Update the user's bluetooth mac address in the DB
		   echo $userUpdateQuery = "UPDATE $dbname.users u SET u.earpiece_bt_id = ? , u.earpiece_device_id = ? WHERE u.user_id = ?;";
			$stmt = $cnx->prepare($userUpdateQuery);
			$stmt->bind_param('sss', $btid,$devid,$userId);
			$stmt->execute();
			$response = new Response(true, 'Success!', NULL, 200);
			$stmt->close();
		}
		
		$cnx->close();
		return $response->getJSONData();
	}*/
	
        //This function will check given Bluethooth mac address
        //to the current listed bluetooth mac address for each user in the DB
        //we stop scanning and return TRUE
        //else return FALSE
	 public function get($token, $userId)
	{
		//First verify parameters were specified (just checks for NULL, does NOT correct input.
		if (! isset($token)) 
		{
            $response = new Response(false, "You must be logged in to access this feature.", null, 403);
            return $response->getJSONData();
		}
		
		if (! isset($userId))
		{
			$response = new Response(false, "You must provide a User ID.", null, 400);
            return $response->getJSONData();
		}
		
		/* Connect to the database*/
		$db = new DB_Connector();
		$cnx = $db->getConnection();
		$dbname=$GLOBALS['db'];
       // echo $db->verifyToken($token, $userId);
		if ($db->verifyToken($token, $userId) || $GLOBALS['opencall']==1 )
		{	
			//Creates a result for all lats, and another one with all lons
			
			/*******************************************************************************************************************/
			/*This uses ulat and ulon compared against u.phone_mac_addr to identify the current user,                */
			/*allowing us to ignore the current user as within the phone_mac_addr.                                                  */
			/*Be sure to always call a put before a get for this class together to make sure we have up-to-date identification.*/
			/*******************************************************************************************************************/
			
	       $prepSQL = "SELECT  s.script_id as script_id,user_id,script_type,script_name,script_desc,privacy_level,sharing_types,script_image,
            create_date,update_date,preload_flag FROM $dbname.scripts as s
			WHERE s.user_id = $userId";
			
			$rs = $cnx->query($prepSQL);
            //$rows=$stmt->fetch_assoc();
			//$stmt->bind_param('s', $userId);
			//$stmt->query();

            $scripts = array();
			if($rs->field_count){
			while ($rows=$rs->fetch_assoc())
            {

				array_push(
                    $scripts,
                    array(
                        "script_id"=>$rows['script_id'],
                        "user_id"=>$rows['user_id'],
                        "script_type"=>$rows['script_type'],
                        "script_name"=>$rows['script_name'],
                        "script_desc"=>$rows['script_desc'],
                        "privacy_level"=>$rows['privacy_level'],
                        "sharing_types"=>$rows['sharing_types'],
                        "script_image"=>$rows['script_image'],
                        "create_date"=>$rows['create_date'],
                        "update_date"=>$rows['update_date'],
                        "preload_flag"=>$rows['preload_flag']
                        )
                );
				
            }
            }else{
                array_push(
                    $scripts,'No Scripts found for this user');
            }
            //$stmt->close();
            $response = new Response(true, 'Success!', $scripts, 200);
        }else {
            $message = "Your token is incorrect or has expired. Please login again.";
            $response = new Response(false, $message, null, 401);
        }
        $cnx->close();
        //Client is not currently handling our null case - consider adding a 'no users found' message

		return $response->getJSONData();
	}		/*get Scripts*/	/*	public function get($token, $userId,$scriptID)	{				if (! isset($token)) 		{            $response = new Response(false, "You must be logged in to access this feature.", null, 403);            return $response->getJSONData();		}				if (! isset($userId))		{			$response = new Response(false, "You must provide a User ID.", null, 400);            return $response->getJSONData();		}		if (! isset($scriptID))		{			$response = new Response(false, "You must provide a ScriptID.", null, 400);            return $response->getJSONData();		}						$db = new DB_Connector();		$cnx = $db->getConnection();		$dbname=$GLOBALS['db'];       		if ($db->verifyToken($token, $userId,scriptID) || $GLOBALS['opencall']==1 )		{								       $prepSQL = "SELECT  s.script_id as script_id,user_id,script_type,script_name,script_desc,privacy_level,sharing_types,script_image,            create_date,update_date,preload_flag FROM $dbname.scripts as s			WHERE s.script_id = $scriptID";						$rs = $cnx->query($prepSQL);                        $scripts = array();			if($rs->field_count){			while ($rows=$rs->fetch_assoc())            {				array_push(                    $scripts,                    array(                        "script_id"=>$rows['script_id'],                        "user_id"=>$rows['user_id'],                        "script_type"=>$rows['script_type'],                        "script_name"=>$rows['script_name'],                        "script_desc"=>$rows['script_desc'],                        "privacy_level"=>$rows['privacy_level'],                        "sharing_types"=>$rows['sharing_types'],                        "script_image"=>$rows['script_image'],                        "create_date"=>$rows['create_date'],                        "update_date"=>$rows['update_date'],                        "preload_flag"=>$rows['preload_flag']                        )                );				            }            }else{                array_push(                    $scripts,'No Scripts found for this user');            }                      $response = new Response(true, 'Success!', $scripts, 200);        }else {            $message = "Your token is incorrect or has expired. Please login again.";            $response = new Response(false, $message, null, 401);        }        $cnx->close();       		return $response->getJSONData();	}*/
}
