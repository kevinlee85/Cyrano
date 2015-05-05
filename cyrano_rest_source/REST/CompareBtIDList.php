<?php
require_once "DB_Connector.php";
require_once "Response.php";

class CompareBtIDList {
    
    /* If you are running XAMPP or WAMPP locally, you can
     * hit this function with: 
     * localhost/REST/commandgroups/{token}/{userId}/
     * OR
     * curl -X GET http://localhost/REST/commandgroups/{token}/{userId}/ */
    function get($token = null, $userId, $btlist) {
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
           /* Get the commands in the database */
            $commandQuery = "SELECT * FROM $dbname.users WHERE phone_mac_addr in ($btlist) ORDER BY user_id ASC;";
            $stmt = $cnx->prepare($commandQuery);
            $stmt->execute();
            $users=array();
            $users=getDynArrayStmt($stmt);
            $user_count=count($users);
            $commandQuery = "SELECT * FROM $dbname.devices WHERE device_mac_address in ($btlist) ORDER BY device_id ASC;";
            $stmt = $cnx->prepare($commandQuery);
            $stmt->execute();
            $devices=array();
            $devices=getDynArrayStmt($stmt);
            $device_count=count($devices);
            $matchedArrray=array();
            $matchedArrray['user_match_count']=$user_count;
            $matchedArrray['user_matched']=$users;
            $matchedArrray['device_match_count']=$device_count;
            $matchedArrray['device_matched']=$devices;
            $stmt->close();
            $response = new Response(true, 'Success!', $matchedArrray, 200);
        } else {
            $message = "Your token is incorrect or has expired. Please login again.";
            $response = new Response(false, $message, null, 401);
        }
        
        $cnx->close();
        return $response->getJSONData();
    }
}
