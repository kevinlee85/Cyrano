<?php
/**
 * Created by PhpStorm.
 * User: work
 * Date: 9/20/14
 * Time: 2:39 AM
 */
 include("rest_functions.php");
 error_reporting(E_ALL);
 ini_set('display_errors', '0');
 $GLOBALS['opencall']=1; // Set '1' for Open Rest Call without token check set '0' to Strict mode
 $GLOBALS['db']='CyranoProduction';
