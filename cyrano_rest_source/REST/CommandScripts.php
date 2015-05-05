<?php
require_once "DB_Connector.php";
require_once "Response.php";

class CommandScripts {
    
    /* If you are running XAMPP or WAMPP locally, you can
     * hit this function with: 
     * localhost/REST/commandgroups/{token}/{userId}/
     * OR
     * curl -X GET http://localhost/REST/commandgroups/{token}/{userId}/ */
    function get($token = null, $userId, $scriptId) {
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
            $commandQuery = "SELECT * FROM $dbname.script_commands WHERE script_id = '$scriptId' ORDER BY command_id ASC;";
            $stmt = $cnx->prepare($commandQuery);
            $stmt->execute();
            $stmt->bind_result(
                    $command_id,
                    $script_id,
                    $command_num,
                    $command_name,
                    $command_desc,
                    $command_type,
                    $command_image,
                    $command_filename,
                    $command_url,
                    $command_delay,
                    $command_trigger_flag,
                    $command_stop_enabled,
                    $command_pause_enabled,
                    $cfNext,
                    $cfPrevious,
                    /* Branching */
                    $command_branchto_rec_1,
                    $command_branchto_label_1,
                    $command_branchto_rec_2,
                    $command_branchto_label_2,
                    $command_branchto_rec_3,
                    $command_branchto_label_3,
                    $command_branchto_rec_4,
                    $command_branchto_label_4,
                    $command_label_orientation,
                    $other
            );
            
            $commands = array();
            while ($stmt->fetch()) {
                $command = array(
                    'script_id'=>$script_id,
                    'command_id'=>$command_id,
                    'command_num'=>$command_num,
                    'command_name'=>$command_name,
                    'command_desc'=>$command_desc,
                    'command_type'=>$command_type,
                    'command_image'=>$command_image,
                    'command_filename'=>$command_filename,
                    'command_url'=>$command_url,
                    'command_delay'=>$command_delay,
                    'command_trigger_flag'=>$command_trigger_flag,
                    'command_stop_enabled'=>$command_stop_enabled,
                    'command_pause_enabled'=>$command_pause_enabled,
                    'cfNext'=>$cfNext,
                    'cfPrevious'=>$cfPrevious,
                    /* Branching */
                    'command_branchto_rec_1'=>$command_branchto_rec_1,
                    'command_branchto_label_1'=>$command_branchto_label_1,
                    'command_branchto_rec_2'=>$command_branchto_rec_2,
                    'command_branchto_label_2'=>$command_branchto_label_2,
                    'command_branchto_rec_3'=>$command_branchto_rec_3,
                    'command_branchto_label_3'=>$command_branchto_label_3,
                    'command_branchto_rec_4'=>$command_branchto_rec_4,
                    'command_branchto_label_4'=>$command_branchto_label_4,
                    'command_label_orientation'=>$command_label_orientation,
                    'other'=>$other

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
