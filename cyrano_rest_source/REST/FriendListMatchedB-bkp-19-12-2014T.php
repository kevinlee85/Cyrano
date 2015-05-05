<?php

require_once "DB_Connector.php";

class FriendListMatchedBT {
	
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
	 public function get($token, $userId,$btlist)
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
            $fieldRSParam="";
            $r="SELECT uf.friend_id FROM $dbname.user_friends as uf
			WHERE uf.user_id ='$userId'";
            $stmt1 = $cnx->query($r);
            $e=0;
            while($row=$stmt1->fetch_array()){
                $fieldRSParam=$fieldRSParam."'".$row[0]."',";
                $e++;
            }

            $r="SELECT uf.user_id FROM $dbname.user_friends as uf
			WHERE uf.friend_id ='$userId'";
            $stmt2 = $cnx->query($r);
            $e=0;
            while($row=$stmt2->fetch_array()){
                $fieldRSParam=$fieldRSParam."'".$row[0]."',";
                $e++;
            }
            $fieldRSParam=rtrim($fieldRSParam,",");
            $prepSQL = "SELECT us.user_id as user_id,us.phone_mac_addr as phone_mac_addr FROM $dbname.users as us WHERE (us.phone_mac_addr in ($btlist) or us.earpiece_bt_id in ($btlist)) and us.user_id in ($fieldRSParam)";
			$stmt = $cnx->query($prepSQL);
			//$stmt->bind_param('s', $userId);
            $scripts=array();
            $e=0;
             if($stmt->num_rows>0){
                while($row=$stmt->fetch_assoc()){
                    $scripts[$e]['user_id']= $row['user_id'];
                    $scripts[$e]['bt_mac_address']= $row['phone_mac_addr'];
                    $e++;
                }
                $response = new Response(true, 'Success!', $scripts, 200);
            }else{
                $response = new Response(true, 'No nearby friends found', null, '400');
            }

        }else {
            $message = "Your token is incorrect or has expired. Please login again.";
            $response = new Response(false, $message, null, 401);
        }
        $cnx->close();
        //Client is not currently handling our null case - consider adding a 'no users found' message

		return $response->getJSONData();
	}
}
