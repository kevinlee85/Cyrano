<?php

require_once "DB_Connector.php";

class otherDeviceUpdate {

    /*
    * Update the user's current Bluetooth address
    */
    public function put($token = null, $userId, $btid = null,$devmac = null) {
        if (! isset($token)) {
            $response = new Response(false, "You must be logged in to access this feature.", null, 403);
            return $response->getJSONData();
        }

        if (! isset($btid)) {
            $response = new Response(false, "You must provide a bluetooth ID.", null, 400);
            return $response->getJSONData();
        }
        if (! isset($devmac)) {
            $response = new Response(false, "You must provide a bluetooth Device address.", null, 400);
            return $response->getJSONData();
        }

        /* Connect to the database */
        $db = new DB_Connector();
        $cnx = $db->getConnection();
        $dbname=$GLOBALS['db'];
        //echo "dd";
        if ($db->verifyToken($token, $userId) || $GLOBALS['opencall']==1) {
            $query="select `user_id`,`device_bt_id`,`device_mac_address` from $dbname.devices as d where d.user_id = $userId
             and d.device_mac_address='$devmac'";
            $rs = $cnx->query($query);
            $count=$rs->num_rows;
           // echo $count;
            $result=array();
            if($count>0){
                while($row=$rs->fetch_assoc()){
                    $result['user_id']=$row['user_id'];
                    $result['device_bt_id']=$row['device_bt_id'];
                    $result['device_mac_address']=$row['device_mac_address'];
                }

            }else{
                $query="insert into $dbname.devices(`user_id`,`device_bt_id`,`device_mac_address`) values
                ('$userId','$btid','$devmac')";
                $cnx->query($query);
                $result['user_id']=$userId;
                $result['device_bt_id']=$btid;
                $result['device_mac_address']=$devmac;
            }
            //echo "bbb";
            /* Update the user's bluetooth mac address in the DB */
            /*$userUpdateQuery = "UPDATE $dbname.users u SET u.earpiece_bt_id = ? , u.earpiece_device_id = ? WHERE u.user_id = ?;";
            $stmt = $cnx->prepare($userUpdateQuery);
            $stmt->bind_param('sss', $btid,$devid,$userId);
            $stmt->execute();

            $stmt->close();*/
            $response = new Response(true, 'Success!', $result, 200);
        }else {
            $message = "Your token is incorrect or has expired. Please login again.";
            $response = new Response(false, $message, null, 401);
        }

        $cnx->close();
        //return "";
        return $response->getJSONData();
    }


}
