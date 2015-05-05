<?php

require_once "DB_Connector.php";

class UserInfoBtList {

    public function get($token=null, $userId=null,$btlist=null)
    {
        //First verify parameters were specified (just checks for NULL, does NOT correct input.

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
        if (! isset($btlist))
        {
            $response = new Response(false, "You must provide a BT List.", null, 400);
            return $response->getJSONData();
        }

        /* Connect to the database*/
        $db = new DB_Connector();
        $cnx = $db->getConnection();
        $dbname=$GLOBALS['db'];

        if ($db->verifyToken($token, $userId) || $GLOBALS['opencall']==1 )
        {
            //Creates a result for all lats, and another one with all lons

            /*******************************************************************************************************************/
            /*This uses ulat and ulon compared against u.phone_mac_addr to identify the current user,                */
            /*allowing us to ignore the current user as within the phone_mac_addr.                                                  */
            /*Be sure to always call a put before a get for this class together to make sure we have up-to-date identification.*/
            /*******************************************************************************************************************/

            $prepSQL = "SELECT fb_auth_fields, firstname, lastname, email, phone_mac_addr FROM $dbname.users WHERE phone_mac_addr in ($btlist)";

            $stmt = $cnx->prepare($prepSQL);
            //$stmt->bind_param('s', $userId);
            $stmt->execute();
            /** @var $lastname TYPE_NAME */
            $stmt->bind_result($user_id, $firstname, $lastname, $email, $phone_mac_addr);
            $usernames = array();

            while ($stmt->fetch()){

                array_push(
                    $usernames,
                    array(
                        "id" => $user_id,
                        "first_name" => $firstname,
                        "last_name" => $lastname,
                        "email" => $email,
                        "phone_mac_addr" => $phone_mac_addr
                    )
                );

            }
            $stmt->close();
            $response = new Response(true, 'Success!', $usernames, 200);
        }else {
            $message = "Your token is incorrect or has expired. Please login again.";
            $response = new Response(false, $message, null, 401);
        }
        $cnx->close();
        //Client is not currently handling our null case - consider adding a 'no users found' message

        return $response->getJSONData();
    }
}
