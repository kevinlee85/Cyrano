<?php
require_once "DB_Connector.php";
require_once "Response.php";
require_once "Commands.php";

class BranchCommand {

    /* If you are running XAMPP or WAMPP locally, you can
     * hit this function with: 
     * localhost/REST/branchcommand/{instruction_id}/{token}
     * OR
     * curl -X GET http://localhost/REST/branchcommand/{instruction_id}/{token} */

    function get($token = null, $userId, $instructionId) {

        if (! isset($token)) {
            $message = "You must be logged in to access this feature.";
            $response = new Response(false, $message, null, 403);
            return $response->getJSONData();
        }
		
		if (! isset($instructionId)) {
			$message = "You must provide a command ID.";
            $response = new Response(false, $message, null, 400);
            return $response->getJSONData();
		}

        /* Connect to the database */
        $db = new DB_Connector();
        $cnx = $db->getConnection();
		 $dbname=$GLOBALS['db'];
        /* Verify the token provided */
        if ($db->verifyToken($token, $userId)|| $GLOBALS['opencall']==1) {

			/* Get the commands in the database */
			 $commandQuery = "SELECT command_id,script_id,command_num,command_name,command_desc,command_type,command_filename,command_url,command_delay,command_trigger_flag,command_stop_enabled,command_pause_enabled,command_next_enabled,command_prev_enabled,command_branchto_rec_1,command_branchto_label_1,command_branchto_rec_2,command_branchto_label_2,command_branchto_rec_3,command_branchto_label_3,command_branchto_rec_4,command_branchto_label_4 FROM $dbname.script_commands WHERE script_id=(" .
				"SELECT script_id FROM $dbname.script_commands WHERE command_id=?" .
				") ORDER BY command_num ASC;";
			$stmt = $cnx->prepare($commandQuery);
			$stmt->bind_param('i', $instructionId);
			$stmt->execute();
			$commands = Commands::parseCommand($stmt);
            $response = new Response(true, 'Success!', $commands, 200);

        } else {
            $message = "Your token is incorrect or has expired. Please login again.";
            $response = new Response(false, $message, null, 401);
        }

        $cnx->close();
        return $response->getJSONData();
    }
}
