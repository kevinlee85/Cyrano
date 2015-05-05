<?php



require_once "DB_Connector.php";



class GetScripts {

	/*get Scripts*/
	
	public function get($token, $userId,$scriptID)

	{

		

		if (! isset($token)) 

		{

            $response = new Response(false, "You must be logged in to access this feature.", null, 403);

            return $response->getJSONData();

		}

		

		if (! isset($userId))

		{

			$response = new Response(false, "You must provide a User ID.", null, 400);

            return $response->getJSONData();

		}
		if (! isset($scriptID))

		{

			$response = new Response(false, "You must provide a ScriptID.", null, 400);

            return $response->getJSONData();

		}

		

		

		$db = new DB_Connector();

		$cnx = $db->getConnection();

		$dbname=$GLOBALS['db'];

       

		if ($db->verifyToken($token, $userId, $scriptID) || $GLOBALS['opencall']==1 )

		{	

			

			

	       $prepSQL = "SELECT script_id,user_id,script_type,script_name,script_desc,privacy_level,sharing_types,script_image,

            create_date,update_date,preload_flag FROM $dbname.scripts

			WHERE script_id = $scriptID";

			

			$rs = $cnx->query($prepSQL);

            


            $scripts = array();

			if($rs->field_count){

			while ($rows=$rs->fetch_assoc())

            {



				array_push(

                    $scripts,

                    array(

                        "script_id"=>$rows['script_id'],

                        "user_id"=>$rows['user_id'],

                        "script_type"=>$rows['script_type'],

                        "script_name"=>$rows['script_name'],

                        "script_desc"=>$rows['script_desc'],

                        "privacy_level"=>$rows['privacy_level'],

                        "sharing_types"=>$rows['sharing_types'],

                        "script_image"=>$rows['script_image'],

                        "create_date"=>$rows['create_date'],

                        "update_date"=>$rows['update_date'],

                        "preload_flag"=>$rows['preload_flag']

                        )

                );

				

            }

            }else{

                array_push(

                    $scripts,'No Scripts found for this user');

            }

          

            $response = new Response(true, 'Success!', $scripts, 200);

        }else {

            $message = "Your token is incorrect or has expired. Please login again.";

            $response = new Response(false, $message, null, 401);

        }

        $cnx->close();

       



		return $response->getJSONData();

	}

}

