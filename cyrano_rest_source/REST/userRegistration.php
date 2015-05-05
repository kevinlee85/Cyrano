<?php


require_once "DB_Connector.php";
require_once "Response.php";
require_once "Settings.php";

class userRegistration {

    /* If you are running XAMPP or WAMPP locally, you can
     * hit this function with:
     * localhost/REST/commands/{token}/{userId}/
     * OR
     * curl -X GET http://localhost/REST/defaultsettings/{token}/{userId}/ */

    public function put($token = null, $firstname,$lastname,$email,$password,$btMacAdd)
     {
        if (!isset($token)) {
            $response = new Response(false, "You must be logged in to access this feature.", null, 403);
            return $response->getJSONData();
        }

        if (! isset($firstname)) {
            $response = new Response(false, "You must provide a First Name.", null, 400);
            return $response->getJSONData();
        }
         if (! isset($lastname)) {
         $response = new Response(false, "You must provide a Last Name.", null, 400);
         return $response->getJSONData();
        }
         if (! isset($email)) {
             $response = new Response(false, "You must provide a Email.", null, 400);
             return $response->getJSONData();
         }
         if (! isset($password)) {
             $response = new Response(false, "You must provide a Password.", null, 400);
             return $response->getJSONData();
         }
         if (! isset($btMacAdd)) {
             $response = new Response(false, "You must provide a BT MAC ID.", null, 400);
             return $response->getJSONData();
         }
         $flag=0;
        /* Connect to the database */
        $db = new DB_Connector();
        $cnx = $db->getConnection();
        $dbname=$GLOBALS['db'];

         $rsrt=$cnx->query("select * from users where email='$email'");
         $numEmail=$rsrt->num_rows;
         if($numEmail>0){
             $response = new Response(true, "You must provide another Email ID. This Email is already in use.", null, 400);
             return $response->getJSONData();
         }else{
             $flag=1;
             $commandQuery = "INSERT INTO  $dbname.users(`firstname`,`lastname`,`email`,`password`,`phone_mac_addr`,`date_added`) values ('$firstname','$lastname','$email','$password','$btMacAdd',now())";
              $stmt1 =$cnx->query($commandQuery);
             $local_user_ida=array();
             $rsrt=$cnx->query("select user_id,fb_auth_fields,firstname,lastname,email,phone_mac_addr from users where email='$email' and password='$password'");

             //$local_user_ida=getDynArrayResult($rsrt);
             $meta = $rsrt->fetch_fields();
             $fields = $results = array();
             // This is the tricky bit dynamically creating an array of variables to use
             // to bind the results
             foreach($meta as $k=>$v){
                 $var = $v->name;
                 $$var = null;
                 $fields[$var] = &$$var;
             }
             $dyResult = array();
             $i=0;
             $results=array();
             while ($rw=$rsrt->fetch_assoc()) {
                 //$results[$i] = array();
                 foreach($rw as $k => $v){
                     $results[$k] = $v;
                     //$results[$i]['token']=$token;
                 }
                /// array_push($dyResult, $results);
                 $i++;
             }
             //array_push($local_user_ida,$dyResult);
             $response = new Response(true, 'Success!', $results, 200);
         }

        $cnx->close();
        return $response->getJSONData();
 }
 }

