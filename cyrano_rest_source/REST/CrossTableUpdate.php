<?php
require_once "DB_Connector.php";
require_once "Response.php";

class CrossTableUpdate {
    
    /* If you are running XAMPP or WAMPP locally, you can
     * hit this function with: 
     * localhost/REST/commandgroups/{token}/{userId}/
     * OR
     * curl -X GET http://localhost/REST/commandgroups/{token}/{userId}/ */
    function put($token = null, $userId, $tableName=null,$setparam=null,$whereparam=null) {
        if (!isset($token)) {
            $message = "You must be logged in to access this feature.";
            $response = new Response(false, $message, null, 403);
            return $response->getJSONData();
        }
        if (!isset($userId)) {
            $message = "You must be logged in to access this feature.";
            $response = new Response(false, $message, null, 403);
            return $response->getJSONData();
        }
        if (!isset($tableName)) {
            $message = "You must be logged in to access this feature.";
            $response = new Response(false, $message, null, 403);
            return $response->getJSONData();
        }
        if (!isset($setparam)) {
            $message = "You must Set some parameters on update.";
            $response = new Response(false, $message, null, 403);
            return $response->getJSONData();
        }
        if (!isset($whereparam)) {
            $whereparam ="";
        }else if($whereparam=='-'){
            $whereparam ="";
        }else{
            $whereparam =" Where $whereparam";
        }

        
        /* Connect to the database */
        $db = new DB_Connector();
        $cnx = $db->getConnection();
        $dbname=$GLOBALS['db'];
        /* Verify the token provided */
        if ($db->verifyToken($token, $userId) || $GLOBALS['opencall']==1) {
           /* Get the commands in the database */
            $commandQuery = "Update $dbname.$tableName set $setparam $whereparam";
            $stmt = $cnx->prepare($commandQuery);
            $stmt->bind_param('ss', $macaddress, $userId);
            $stmt->execute();
            $response = new Response(true, 'Success!', NULL, 200);

            $stmt->close();
           // $response = new Response(true, 'Success!', $matchedArrray, 200);
        } else {
            $message = "Your token is incorrect or has expired. Please login again.";
            $response = new Response(false, $message, null, 401);
        }
        
        $cnx->close();
        return $response->getJSONData();
    }
}
