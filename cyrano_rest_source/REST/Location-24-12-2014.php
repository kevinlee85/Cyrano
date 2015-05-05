<?php

require_once "DB_Connector.php";

class Location {
	
	/*
	* Update the user's current GPS location
	*/
	public function put($token = null, $userId, $latitude = null, $longitude = null) {
		if (! isset($token)) {
            $response = new Response(false, "You must be logged in to access this feature.", null, 403);
            return $response->getJSONData();
		}
		
		if (! isset($latitude) || ! isset($longitude)) {
			$response = new Response(false, "You must provide a latitude and a longitude.", null, 400);
            return $response->getJSONData();
		}
		
		/* Connect to the database */
		$db = new DB_Connector();
		$cnx = $db->getConnection();
		$dbname=$GLOBALS['db'];
		if ($db->verifyToken($token, $userId) || $GLOBALS['opencall']==1 ) {
			
			/* Update the user's latitude and longitude in the DB */
			$userUpdateQuery = "UPDATE $dbname.users u SET u.latitude = ?, u.longitude = ? WHERE u.user_id = ?;";
			$stmt = $cnx->prepare($userUpdateQuery);
			$stmt->bind_param('dds', $latitude, $longitude, $userId);
			$stmt->execute();
			$response = new Response(true, 'Success!', NULL, 200);
			$stmt->close();
		}
		
		$cnx->close();
		return $response->getJSONData();
	}
	
	//This function will compare given GPS coordinates (ulat and ulon)
	//to the current listed GPS coords for each user in the DB
	//If ANY user is found within a certain distance threshold,
	//we stop scanning and return TRUE
	//else return FALSE
	public function get($token, $userId, $ulat, $ulon, $threshold) //NOTE: Dist function returns miles/km. Make sure threshold is the same unit.
	{
		//First verify parameters were specified (just checks for NULL, does NOT correct input.
		if (! isset($token)) 
		{
            $response = new Response(false, "You must be logged in to access this feature.", null, 403);
            return $response->getJSONData();
		}
		
		if (! isset($ulat) || ! isset($ulon)) 
		{
			$response = new Response(false, "You must provide a latitude and a longitude.", null, 400);
            return $response->getJSONData();
		}
		
		if (! isset($threshold)) 
		{
			$response = new Response(false, "You must provide a maximum search radius.", null, 400);
            return $response->getJSONData();
		}
		
		/* Connect to the database */
		$db = new DB_Connector();
		$cnx = $db->getConnection();
		$dbname=$GLOBALS['db'];
		if ($db->verifyToken($token, $userId) || $GLOBALS['opencall']==1 )
		{	
			//Creates a result for all lats, and another one with all lons
			
			/*******************************************************************************************************************/
			/*This uses ulat and ulon compared against u.latitude and u.longitude to identify the current user,                */
			/*allowing us to ignore the current user as within the threshold.                                                  */
			/*Be sure to always call a put before a get for this class together to make sure we have up-to-date identification.*/
			/*******************************************************************************************************************/
			
			$prepSQL = "SELECT user_id, firstname, lastname, email, latitude, longitude, '', '','', phone_mac_addr
			FROM $dbname.users WHERE user_id != ? AND last_update > DATE_SUB(NOW(),
			INTERVAL (SELECT nearby_timeout FROM user_settings) MINUTE)";
			$stmt = $cnx->prepare($prepSQL);
			$stmt->bind_param('s', $userId);
			$stmt->execute();
			$stmt->bind_result($user_id, $first_name, $last_name, $email, $arr_lat, $arr_lon, $details1, $details2, $details3, $bluetooth_address);
			$usernames = array();
			
			while ($stmt->fetch()) 
            {
            	// Distance can return miles, kilometers or nautical miles
				$kilos = $this->_distance($ulat, $ulon, $arr_lat, $arr_lon, "K"); 
				$meters = $kilos * 1000; // Client GPS expects meters; may change this later

				//If the distance is within the threshold, add to array DON'T return TRUE
				if($meters <= $threshold)
				{
					array_push($usernames, array("id" => $user_id, "first_name" => $first_name, "last_name" => $last_name, "email" => $email, "distance" => $meters, "latitude" => $arr_lat, "longitude" => $arr_lon, 
						"details1" => $details1, "details2" => $details2, "details3" => $details3, "bluetooth_address"=> $bluetooth_address));
				}
            }
            $stmt->close();
        }else {
            $message = "Your token is incorrect or has expired. Please login again.";
            $response = new Response(false, $message, null, 401);
        }
        $cnx->close();
        //Client is not currently handling our null case - consider adding a 'no users found' message
		$response = new Response(true, 'Success!', $usernames, 200);
		return $response->getJSONData();
	}
	
	/*::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
	/*::                                                                         :*/
	/*::  This routine calculates the distance between two points (given the     :*/
	/*::  latitude/longitude of those points). It is being used to calculate     :*/
	/*::  the distance between two locations using GeoDataSource(TM) Products    :*/
	/*::                                                                         :*/
	/*::  Definitions:                                                           :*/
	/*::    South latitudes are negative, east longitudes are positive           :*/
	/*::                                                                         :*/
	/*::  Passed to function:                                                    :*/
	/*::    lat1, lon1 = Latitude and Longitude of point 1 (in decimal degrees)  :*/
	/*::    lat2, lon2 = Latitude and Longitude of point 2 (in decimal degrees)  :*/
	/*::    unit = the unit you desire for results                               :*/
	/*::           where: 'M' is statute miles                                   :*/
	/*::                  'K' is kilometers (default)                            :*/
	/*::                  'N' is nautical miles                                  :*/
	/*::  Worldwide cities and other features databases with latitude longitude  :*/
	/*::  are available at http://www.geodatasource.com                          :*/
	/*::                                                                         :*/
	/*::  For enquiries, please contact sales@geodatasource.com                  :*/
	/*::                                                                         :*/
	/*::  Official Web site: http://www.geodatasource.com                        :*/
	/*::                                                                         :*/
	/*::         GeoDataSource.com (C) All Rights Reserved 2014                  :*/
	/*::                                                                         :*/
	/*::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
	
	//Many types of GPS out there. 
	//Fairly certain this one is built for decimal degrees, 
	//which is the format of our storage.
	//If we have problems with this, check the GPS typing first.
	//There are functions out there to convert between GPS types. - J.T.
	
	private function _distance($lat1, $lon1, $lat2, $lon2, $unit) 
	{
		$theta = $lon1 - $lon2;
		$dist = sin(deg2rad($lat1)) * sin(deg2rad($lat2)) +  cos(deg2rad($lat1)) * cos(deg2rad($lat2)) * cos(deg2rad($theta));
		$dist = acos($dist);
		$dist = rad2deg($dist);
		$miles = $dist * 60 * 1.1515;
		$unit = strtoupper($unit);
 
		if ($unit == "K") 
		{
			return ($miles * 1.609344);
		}
		else if ($unit == "N") 
		{
			return ($miles * 0.8684);
		} 
		else 
		{
			return $miles;
		}
	}
}
