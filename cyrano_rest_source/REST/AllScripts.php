<?php
require_once "DB_Connector.php";
require_once "Response.php";

class AllScripts {
    
    /* If you are running XAMPP or WAMPP locally, you can
     * hit this function with: 
     * localhost/REST/commandgroups/{token}/{userId}/
     * OR
     * curl -X GET http://localhost/REST/commandgroups/{token}/{userId}/ */
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
            
            /* Get the commands in the database */
            $commandQuery = "SELECT * FROM $dbname.scripts ORDER BY script_id ASC;";
            $stmt = $cnx->prepare($commandQuery);
            $stmt->execute();
            $stmt->bind_result(
                $script_id,
                $script_type,
                $script_name,
                $script_desc,
                $privacy_level,
                $sharing_types,
                $script_image,
                $create_date,
                $update_date,
                $preload_flag
            );
            
            $commands = array();
            while ($stmt->fetch()) {
                $command = array(
                    'script_id'=>$script_id,
                    'script_type'=>$script_type,
                    'script_name'=>$script_name,
                    'script_desc'=>$script_desc,
                    'privacy_level'=>$privacy_level,
                    'sharing_types'=>$sharing_types,
                    'script_image'=>$script_image,
                    'create_date'=>$create_date,
                    'update_date'=>$update_date,
                    'preload_flag'=>$preload_flag


                );
                array_push($commands, $command);
            }
            
            /* free result set */
            $stmt->close();
            $response = new Response(true, 'Success!', $commands, 200);
        } else {
            $message = "Your token is incorrect or has expired. Please login again.";
            $response = new Response(false, $message, null, 401);
        }
        
        $cnx->close();
        return $response->getJSONData();
    }
}
