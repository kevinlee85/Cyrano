<?php

error_reporting(E_ALL);
ini_set('display_errors', '1');

require_once 'restler/restler.php';
require_once 'Auth.php';
require_once 'Commands.php';
require_once 'CommandGroups.php';
require_once 'BranchCommand.php';
require_once 'Location.php';
require_once 'Bluetooth.php';
require_once 'Settings.php';
require_once 'DefaultSettings.php';
require_once 'CommandScripts.php';
require_once 'AllScripts.php';
require_once 'BtEarpieceUpdate.php';
require_once 'UserInfo.php';
require_once 'UserScripts.php';
require_once 'UserListMatchedBT.php';
require_once 'UserInfoBtList.php';
require_once 'CompareBtIDList.php';
require_once 'CommandsUserID.php';
require_once 'CrossTableQuery.php';
require_once 'CrossTableUpdate.php';
require_once 'DeviceListMatchedBT.php';
require_once 'TriggerListMatchedBT.php';
require_once 'FriendListMatchedBT.php';
require_once 'DeviceListMatchedBTAllUsers.php';
require_once 'DeviceListMatchedBTCmn.php';
require_once 'TriggerListMatchedBTCmn.php';
require_once 'FriendListMatchedBTCmn.php';
require_once 'ScrComMatchUSRDEV.php';
require_once 'userRegistration.php';
require_once 'otherDeviceUpdate.php';
require_once 'login.php';require_once 'GetScripts.php';
$r = new Restler();

$r->addAPIClass('Auth');
$r->addAPIClass('Commands');
$r->addAPIClass('CommandGroups');
$r->addAPIClass('BranchCommand');
$r->addAPIClass('Location');
$r->addAPIClass('Bluetooth');
$r->addAPIClass('Settings');
$r->addAPIClass('DefaultSettings');
$r->addAPIClass('CommandScripts');
$r->addAPIClass('AllScripts');
$r->addAPIClass('BtEarpieceUpdate');
$r->addAPIClass('UserInfo');
$r->addAPIClass('UserScripts');
$r->addAPIClass('UserListMatchedBT');
$r->addAPIClass('UserInfoBtList');
$r->addAPIClass('CompareBtIDList');
$r->addAPIClass('CommandsUserID');
$r->addAPIClass('CrossTableQuery');
$r->addAPIClass('CrossTableUpdate');
$r->addAPIClass('DeviceListMatchedBT');
$r->addAPIClass('TriggerListMatchedBT');
$r->addAPIClass('FriendListMatchedBT');
$r->addAPIClass('DeviceListMatchedBTAllUsers');
$r->addAPIClass('DeviceListMatchedBTCmn');
$r->addAPIClass('TriggerListMatchedBTCmn');
$r->addAPIClass('FriendListMatchedBTCmn');
$r->addAPIClass('ScrComMatchUSRDEV');
$r->addAPIClass('userRegistration');
$r->addAPIClass('login');
$r->addAPIClass('otherDeviceUpdate');$r->addAPIClass('GetScripts');
$r->handle();