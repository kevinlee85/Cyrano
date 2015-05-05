<?php
require_once "DB_Connector.php";
require_once "Response.php";

class login {
    
    /* If you are running XAMPP or WAMPP locally, you can
     * hit this function with: 
     * localhost/REST/commandgroups/{token}/{userId}/
     * OR
     * curl -X GET http://localhost/REST/commandgroups/{token}/{userId}/ */
    function get($token = null, $email,$password,$btMacAdd) {
        if (!isset($token)) {
            $message = "You must be logged in to access this feature.";
            $response = new Response(false, $message, null, 403);
            return $response->getJSONData();
        }
        if (!isset($email)) {
            $message = "You must provide Email address.";
            $response = new Response(false, $message, null, 403);
            return $response->getJSONData();
        }
        if (!isset($password)) {
            $message = "You must provide Password.";
            $response = new Response(false, $message, null, 403);
            return $response->getJSONData();
        }
        if (!isset($btMacAdd)) {
            $message = "You must provide BT MAC address.";
            $response = new Response(false, $message, null, 403);
            return $response->getJSONData();
        }

        /* Connect to the database */
        $db = new DB_Connector();
        $cnx = $db->getConnection();
        $dbname=$GLOBALS['db'];
        /* Verify the token provided */

        $rsrt=$cnx->query("select user_id,fb_auth_fields,firstname,lastname,email,phone_mac_addr,about_text,textspeech_style from users where email='$email' and password='$password'");
        $numEmail=$rsrt->num_rows;

        $ID_local=$rsrt->fetch_array();
        $user_ID_local= $ID_local[0];
        $fname=$ID_local[2];
        $lname=$ID_local[3];
        $email1=$ID_local[4];
        $mac=$ID_local[5]; 
        $about_text=$ID_local[6];
        $textspeech_style=$ID_local[7];
        if($numEmail>0){
            // Success login
            $commandQuery = "UPDATE $dbname.users SET `phone_mac_addr`='$btMacAdd' where email='$email' and password='$password'";
            $stmt1 =$cnx->query($commandQuery);

            $user_ID_localA=array();
            $user_ID_localA['user_id']=$user_ID_local;
            $user_ID_localA['token']=$token;
            $user_ID_localA['firstname']=$fname;
            $user_ID_localA['lastname']=$lname;
            $user_ID_localA['email']=$email1;
            $user_ID_localA['phone_mac_addr']=$mac;
            $user_ID_localA['about_text']=$about_text;
	        $user_ID_localA['textspeech_style']=$textspeech_style;

            $response = new Response(true, 'Success!', $user_ID_localA, 200);

        }else{
            $response = new Response(true, "Provided email or password couldn't be authenticated. ", null, 400);
            return $response->getJSONData();
        }
        $cnx->close();
        return $response->getJSONData();
    }
}
