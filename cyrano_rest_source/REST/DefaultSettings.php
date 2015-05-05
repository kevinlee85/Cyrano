<?php


require_once "DB_Connector.php";
require_once "Response.php";
require_once "Settings.php";

class DefaultSettings {

    /* If you are running XAMPP or WAMPP locally, you can
     * hit this function with:
     * localhost/REST/commands/{token}/{userId}/
     * OR
     * curl -X GET http://localhost/REST/defaultsettings/{token}/{userId}/ */

    public function put($token = null, $user_id,$terseMode,$tsAudio,$graphicalMode,$friendFinder,$friendAudio,$textSize,$gpsTimeDelay,$maxFriends,$autoDisplayFriends,$nearbyTimeout ) {
        if (!isset($token)) {
            $response = new Response(false, "You must be logged in to access this feature.", null, 403);
            return $response->getJSONData();
        }

        if (! isset($user_id)) {
            $response = new Response(false, "You must provide a User ID.", null, 400);
            return $response->getJSONData();
        }

        /* Connect to the database */
        $db = new DB_Connector();
        $cnx = $db->getConnection();
        $dbname=$GLOBALS['db'];
        if ($db->verifyToken($token, $user_id) || $GLOBALS['opencall']== 1) {
            /* Insert if not exists*/
            $commandQuery = "SELECT * FROM $dbname.user_settings where user_id=$user_id";
            $stmt1 = $cnx->query($commandQuery);
            /*$stmt1->bind_param('i',$user_id);
            $stmt1->execute();
            */
            $num=$stmt1->num_rows;
            $stmt1->close();
           // echo ">>".$num."<<";
            if($num>0){

            /* Update the user's bluetooth mac address in the DB */
            $userUpdateQuery = "UPDATE $dbname.user_settings u SET u.terse_mode = ?,u.ts_audio =?, u.graphical_mode = ?,
            u.friend_finder =?, u.friend_audio =?,u.text_size =?, u.gps_time_delay =?, u.max_friends =?,
            u.auto_display_friends = ?, u.nearby_timeout=? where user_id=?;";
            $stmt = $cnx->prepare($userUpdateQuery);
            $stmt->bind_param('iiiiiidiiii',$terseMode,$tsAudio,$graphicalMode,$friendFinder,$friendAudio,$textSize,$gpsTimeDelay,$maxFriends,$autoDisplayFriends,$nearbyTimeout,$user_id);
            $stmt->execute();
                $stmt->close();
            }else{
                $userUpdateQuery = "INSERT INTO $dbname.user_settings(`user_id`,`terse_mode`,`ts_audio`,`graphical_mode`,`friend_finder`,`friend_audio`,`text_size`,`gps_time_delay`,`max_friends`,`auto_display_friends`,`nearby_timeout`) values(?,?,?,?,?,?,?,?,?,?,?)";
                $stmt = $cnx->prepare($userUpdateQuery);
                $stmt->bind_param('iiiiiiidiii', $user_id,$terseMode,$tsAudio,$graphicalMode,$friendFinder,$friendAudio,$textSize,$gpsTimeDelay,$maxFriends,$autoDisplayFriends,$nearbyTimeout);
                $stmt->execute();
                $stmt->close();
            }
            ///$stmt->field_count
            $response = new Response(true, 'Success!', NULL, 200);

            //$stmt->close();
        }else{
            $message = "Your token is incorrect or has expired. Please login again.";
            $response = new Response(false, $message, null, 401);
        }

        $cnx->close();
        return $response->getJSONData();
    }
	public function get($token = null, $userId) {
		if (!isset($token)) {
			$message = "You must be logged in to access this feature.";
			$response = new Response(false, $message, null, 403);
			return $response->getJSONData();
		}

		/* Connect to the database */
		$db = new DB_Connector();
		$cnx = $db->getConnection();
        $dbname=$GLOBALS['db'];
		/* Verify the token provided */
		if ($db->verifyToken($token, $userId) || $GLOBALS['opencall']==1) {
			/* Get the settings in the database */
			$commandQuery = "SELECT * FROM $dbname.user_settings where user_id='$userId';";
		
			$stmt = $cnx->prepare($commandQuery);
			$stmt->execute();
		
           // print_r($stmt->fetch());
			$stmt->bind_result(
                    $user_id,
					$terseMode,
					$tsAudio,
					$graphicalMode,
					$friendFinder,
					$friendAudio,
					$textSize,
					$gpsTimeDelay,
					$maxFriends,
                    $autoDisplayFriends,
                    $nearbyTimeout,
                    $TTS_style,
                    $pre_tone_style,
					$pre_tone_volume,
					$vibrate);

			$settings = array();
			while ($stmt->fetch()) {
			
				$setting = array(
                    'userId' => $user_id,
					'terseMode' => $terseMode,
					'tsAudio' => $tsAudio,
					'graphicalMode' => $graphicalMode,
					'friendFinder' => $friendFinder,
					'friendAudio' => $friendAudio,
					'textSize' => $textSize,
					'gpsTimeDelay' => $gpsTimeDelay,
					'maxFriends' => $maxFriends,
					'autoDisplayFriends' => $autoDisplayFriends,
					'TTS_style' => $TTS_style
				);
				array_push($settings, $setting);
			}

			/* free result set */
			$stmt->close();
			$response = new Response(true, 'Success!', $settings, 200);
		} else {
			$message = "Your token is incorrect or has expired. Please login again.";
			$response = new Response(false, $message, null, 401);
		}

		$cnx->close();
		return $response->getJSONData();
    }
}