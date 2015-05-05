<?php

require_once "DB_Connector.php";

class ScrComMatchUSRDEV {
	
	/*
	* Update the user's current Bluetooth address
	*/

	 public function get($token, $userId,$devId)
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
        if (! isset($devId))
        {
            $response = new Response(false, "You must provide a Device ID.", null, 400);
            return $response->getJSONData();
        }
		
		/* Connect to the database*/
		$db = new DB_Connector();
		$cnx = $db->getConnection();
		$dbname=$GLOBALS['db'];
		if ($db->verifyToken($token, $userId) || $GLOBALS['opencall']==1 )
		{	
			//Creates a result for all lats, and another one with all lons
			
			/*******************************************************************************************************************/
			/*This uses ulat and ulon compared against u.phone_mac_addr to identify the current user,                */
			/*allowing us to ignore the current user as within the phone_mac_addr.                                                  */
			/*Be sure to always call a put before a get for this class together to make sure we have up-to-date identification.*/
			/*******************************************************************************************************************/
           // echo "<pre>";
            $commandQuery = "SELECT t.script_id as script_id,t.command_id as command_id FROM $dbname.triggers_alarms as t,$dbname.devices as d  WHERE d.device_mac_address in ($devId) and t.user_id='$userId' and d.device_id =t.device_id
            ORDER BY t.priority ASC;";
            $result = $cnx->query($commandQuery);
            if( $result->num_rows >0){
                //var_dump($state->fetch());
            $devices=array();
            $devices=getDynArrayResult($result);
            //print_r($devices);
            $matchedArrray=array();
            $matchedArrray["Matched-Scripts-Commands"]=$devices;
            }else{
                $matchedArrray=array();
                $matchedArrray["Matched-Scripts-Commands"]="No Script or Command found.";
            }
            $response = new Response(true, 'Success!', $matchedArrray, 200);
        }else {
            $message = "Your token is incorrect or has expired. Please login again.";
            $response = new Response(false, $message, null, 401);
        }
        $cnx->close();
        //Client is not currently handling our null case - consider adding a 'no users found' message

		return $response->getJSONData();
	}
}
