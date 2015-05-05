<?php

require_once "DB_Connector.php";
require_once "Response.php";

class Commands {

    /* If you are running XAMPP or WAMPP locally, you can
     * hit this function with: 
     * localhost/REST/commands/{group_id}/{token}
     * OR
     * curl -X GET http://localhost/REST/commands/{group_id}/{token} */

    function get($token = null, $userId, $groupID) {

        if (! isset($token)) {
            $message = "You must be logged in to access this feature.";
            $response = new Response(false, $message, null, 403);
            return $response->getJSONData();
        }
		
		if (! isset($groupID)) {
			$message = "You must provide a group id and instruction number.";
            $response = new Response(false, $message, null, 400);
            return $response->getJSONData();
		}

        /* Connect to the database */
        $db = new DB_Connector();
        $cnx = $db->getConnection();
		 $dbname=$GLOBALS['db'];
        /* Verify the token provided */
        if ($db->verifyToken($token, $userId)) {

			/* Get the commands in the database */
			$commandQuery = "SELECT * FROM cyranodatabase.commands WHERE group_id=? ORDER BY instruction_number ASC;";
			$stmt = $cnx->prepare($commandQuery);
			$stmt->bind_param('i', $groupID);
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
    
    public static function parseCommand(&$stmt) {
        $stmt->bind_result($commandID,
                           $groupID,
                           $instructionNumber,
                           $name,
                           $description,
                           $type,
                           $filename,
                           $url,
                           $delay,
                           $trigger,
                           $cfStop,
                           $cfPause,
                           $cfNext,
                           $cfPrevious,
                           $br1,
                           $bl1,
                           $br2,
                           $bl2,
                           $br3,
                           $bl3,
                           $br4,
                           $bl4);
						
			$commands = array();
			while ($stmt->fetch()) {
				$command = array();
                $command['commandID'] = $commandID;
				$command['groupID'] = $groupID;
				$command['instructionNumber'] = $instructionNumber;
				$command['name'] = $name;
				$command['description'] = $description;
				$command['type'] = $type;
				$command['filename'] = $filename;
				$command['url'] = $url;
				$command['delay'] = $delay;
				$command['cfStop'] = $cfStop;
				$command['cfPause'] = $cfPause;
				$command['cfNext'] = $cfNext;
				$command['cfPrevious'] = $cfPrevious;
                $branch = array();
                $branch[] = array('record' => $br1, 'label' => $bl1);
                $branch[] = array('record' => $br2, 'label' => $bl2);
                $branch[] = array('record' => $br3, 'label' => $bl3);
                $branch[] = array('record' => $br4, 'label' => $bl4);
                $command['branching'] = $branch;
				array_push($commands, $command);
			}
			
            /* free result set */
            $stmt->close();
            return $commands;
    }
}
