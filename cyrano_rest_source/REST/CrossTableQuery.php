<?php
require_once "DB_Connector.php";
require_once "Response.php";

class CrossTableQuery {
    
    /* If you are running XAMPP or WAMPP locally, you can
     * hit this function with: 
     * localhost/REST/commandgroups/{token}/{userId}/
     * OR
     * curl -X GET http://localhost/REST/commandgroups/{token}/{userId}/ */
    function get($token = null, $userId, $tableName=null,$select=null,$whereparam=null,$extra= null) {
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
        if (!isset($select)) {
            $select=" * ";
        }
        if (!isset($whereparam)) {
            $whereparam ="";
        }else if($whereparam=='-'){
            $whereparam ="";
        }else{
            $whereparam =" Where $whereparam";
        }
        if (!isset($extra)) {
            $extra ="";
        }
        
        /* Connect to the database */
        $db = new DB_Connector();
        $cnx = $db->getConnection();
        $dbname=$GLOBALS['db'];
        /* Verify the token provided */
        if ($db->verifyToken($token, $userId) || $GLOBALS['opencall']==1) {
           /* Get the commands in the database */
            $commandQuery = "SELECT $select FROM $dbname.$tableName $whereparam $extra";
            $stmt = $cnx->prepare($commandQuery);
            $stmt->execute();
            $matchedArrrayName=$tableName;
            $$tableName=array();
            $$tableName=getDynArrayStmt($stmt);
            //$user_count=count($users);

            $matchedArrray=array();
            $matchedArrray["$matchedArrrayName"]=$$tableName;

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
