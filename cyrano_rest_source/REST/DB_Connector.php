<?php
include("rest_config.php");
class DB_Connector {

    private $connection;
	
	const TIMEOUT = 28800;
    
    function __construct() {
        /* Parse configuration file */
        $ini = parse_ini_file("db_config.ini");

        $this->connection = new mysqli($ini["host"], $ini["user"], $ini["password"], $ini["database"]);

        /* Check connection */
        if ($this->connection->connect_errno) {
            printf("Connect failed: %s\n", $this->connection->connect_error);
            exit();
        }
    }

    public function getConnection() {
        return $this->connection;
    }

    /* Hit Facebook to verify that this token is still valid */
    public function verifyToken($token, $userId) {
        
        $requestString = "https://graph.facebook.com/me?fields=id&access_token=%s";
        $json = json_decode(file_get_contents(sprintf($requestString, $token)));
        return $userId == $json->{'id'};
    }

    /* Register upon login if the facebookId is not already in the database */
    public function loginOrRegister($token, $userId,$bluetooth_address) {
	$dbname=$GLOBALS['db'];
        if ($this->verifyToken($token, $userId) || $GLOBALS['opencall']==1) {

            if ($this->isRegistered($userId)!=0) {
                //echo "IF";
                //$local_user_id=$this->isRegistered($userId);
                $requestString = "https://graph.facebook.com/me?fields=id,email,first_name,last_name&access_token=%s";
                $json = json_decode(file_get_contents(sprintf($requestString, $token)));

                $facebookId = $json->{'id'};
                $email = $json->{'email'};
                $firstName = $json->{'first_name'};
                $lastName  = $json->{'last_name'};
                //$date=new Datetime();
                $time = date("Y-m-d H:i:s");

                $stmt = $this->connection->prepare("update $dbname.users set  firstname='$firstName', lastname='$lastName', last_update='$time', phone_mac_addr='$bluetooth_address' where fb_auth_fields='$facebookId'");
                //$stmt->bind_param('ssss',$firstName, $lastName);
                $stmt->execute();
                $stmt->close();

            }
			else
			{
               // echo "ELSE";
                $requestString = "https://graph.facebook.com/me?fields=id,email,first_name,last_name&access_token=%s";
                $json = json_decode(file_get_contents(sprintf($requestString, $token)));

                $facebookId = $json->{'id'};
                $email = $json->{'email'};
                $firstName = $json->{'first_name'};
                $lastName  = $json->{'last_name'};
                //echo "INSERT INTO $dbname.users (fb_auth_fields, firstname, lastname, latitude, longitude, email, phone_mac_addr) VALUES ($facebookId, $firstName, $lastName, NULL, NULL, $email,$bluetooth_address)";
                $this->connection->query("INSERT INTO $dbname.users (fb_auth_fields, firstname, lastname, latitude, longitude, email, phone_mac_addr) VALUES ('$facebookId','$firstName' ,'$lastName', NULL, NULL,'$email','$bluetooth_address')");
               // $stmt->bind_param('sssss', $facebookId, $firstName, $lastName, $email, $bluetooth_address);
                //$stmt->execute();
                //$stmt->close();


            }
            return true;
        }

        return false;
    }

    private function isRegistered($userId) {
	$dbname=$GLOBALS['db'];
       // echo "SELECT COUNT(*) FROM $dbname.users WHERE fb_auth_fields='$userId'";
        $stmt = $this->connection->prepare("SELECT COUNT(*),user_id FROM $dbname.users WHERE fb_auth_fields=?;");
        $stmt->bind_param('s', $userId);
        $stmt->execute();
        $stmt->bind_result($count,$local_user_id);
        $stmt->fetch();
        $stmt->close();
        //echo "$local_user_id";

        return $count!=0;
    }

}
