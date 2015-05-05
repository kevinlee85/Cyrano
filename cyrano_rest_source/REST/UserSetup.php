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

    function get($token = null, $userId) {
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
            $commandQuery = "SELECT * FROM $dbname.user_settings;";
            $stmt = $cnx->prepare($commandQuery);
            $stmt->execute();
            $stmt->bind_result(
                $terseMode,
                $tsAudio,
                $graphicalMode,
                $friendFinder,
                $friendAudio,
                $textSize,
                $gpsTimeDelay,
                $maxFriends,
                $autoDisplayFriends,
                $nearbyTimeout);

            $settings = array();
            while ($stmt->fetch()) {
                $setting = array(
                    'terseMode' => $terseMode,
                    'tsAudio' => $tsAudio,
                    'graphicalMode' => $graphicalMode,
                    'friendFinder' => $friendFinder,
                    'friendAudio' => $friendAudio,
                    'textSize' => $textSize,
                    'gpsTimeDelay' => $gpsTimeDelay,
                    'maxFriends' => $maxFriends,
                    'autoDisplayFriends' => $autoDisplayFriends
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
