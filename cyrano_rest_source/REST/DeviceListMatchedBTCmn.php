<?php

require_once "DB_Connector.php";

class DeviceListMatchedBTCmn {
	
	public function get($token, $userId,$btlist,$resultType=null,$mode=null,$limit=null)
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
        if (! isset($resultType))
        {
            $resultType='R'; // C- count or R - result
        }
        if (! isset($mode))
        {
            $resultType='L'; // L - Light or F - full mode
        }
        if(! isset($limit)){
            $limit=""; // blank for all otherwise value of count
        }else{
            $limit="limit ".$limit;
        }
        if (! isset($btlist))
        {
            $response = new Response(false, "You must provide a BT List.", null, 400);
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
            if($mode=='F'){ // If full result is necessary
              $commandQuery = "SELECT * FROM $dbname.devices WHERE device_mac_address in ($btlist) and user_id ='$userId' ORDER BY device_id ASC $limit;";
            }else{
              $commandQuery = "SELECT user_id,device_mac_address FROM $dbname.devices WHERE device_mac_address in ($btlist) and user_id ='$userId' ORDER BY device_id ASC $limit;";
            }
            $stmt = $cnx->prepare($commandQuery);
            $stmt->execute();
            $devices=array();
            $devices=getDynArrayStmt($stmt);
            $device_count=count($devices);
            $matchedArrray=array();
            if($resultType=='C'){ // IF only to count
              $matchedArrray["Matched-Device-Count"]=$device_count;
            }else{
              $matchedArrray["Matched-Devices"]=$devices;
            }
            $stmt->close();
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
