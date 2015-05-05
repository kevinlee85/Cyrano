<?php

require_once 'DB_Connector.php';
require_once 'Response.php';

class Auth {

    /* Logs user into Cyrano with a Facebook access token. */
    function get($accessToken, $userId,$bluetooth_address) {
        if (! isset($accessToken) || ! isset($userId)) {
            $message = "No Facebook access token and/or user ID provided.";
            $response = new Response(false, $message, null, 400);
            return $response->getJSONData();
        }

        $db = new DB_Connector();
        $cnx = $db->getConnection();
        $dbname=$GLOBALS['db'];
       // $local_user_id=;

        if ($db->loginOrRegister($accessToken, $userId,$bluetooth_address) || $GLOBALS['opencall']==1) {
            //$local_user_id_array=array();
            //array_push($local_user_id_array,$local_user_id);

            $stmt1 = $cnx->query("SELECT `user_id` FROM $dbname.users WHERE fb_auth_fields='$userId' limit 1");
            //echo "SELECT user_id FROM $dbname.users WHERE fb_auth_fields='$facebookId'";
            //$stmt1->execute();
            //$local_user_ida=array();
            //$local_user_ida=getDynArrayStmt($stmt1);
           // $local_user_ida=$stmt1->fetch_row();
            while($row=$stmt1->fetch_assoc()){
                $local_user_ida['user_id']=$row['user_id'];
            }
           // echo $local_user_ida."[][][";
            //echo $local_user_id=$Luser_id;
            //$stmt1->close();
            if($local_user_ida!=""){
                $response = new Response(true, 'Success!', $local_user_ida, 200);
            }else{
                $response = new Response(false, 'No user exists with this FB User ID !! ', null, 401);
            }


        } else {
            $response = new Response(false, 'No user exists with this FB User ID !! ', null, 401);
        }

        return $response->getJSONData();
    }
}

