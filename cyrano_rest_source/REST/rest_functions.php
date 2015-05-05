<?php

/**

 * Created by PhpStorm.

 * User: work

 * Date: 10/9/14

 * Time: 3:02 AM

 */

function getDynArrayStmt($stmt){
    $meta = $stmt->result_metadata();
    $fields = $results = array();
    // This is the tricky bit dynamically creating an array of variables to use
    // to bind the results
    while ($field = $meta->fetch_field()) {
        $var = $field->name;
        $$var = null;
        $fields[$var] = &$$var;
    }
    count($fields);
    call_user_func_array(array($stmt,'bind_result'),$fields);
    $dyResult = array();
    $i=0;
    while ($stmt->fetch()) {
        $results[$i] = array();
        foreach($fields as $k => $v){
            //echo $k;
            $results[$i][$k] = $v;
        }
        array_push($dyResult, $results[$i]);
        $i++;
    }
    return $dyResult;
}
function getDynArrayResult($result){
    $meta = $result->fetch_fields();
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
    while ($rw=$result->fetch_assoc()) {
        $results[$i] = array();
        foreach($rw as $k => $v){
            $results[$i][$k] = $v;
        }
        array_push($dyResult, $results[$i]);
        $i++;
    }
    //print_r($dyResult);
    return $dyResult;
}
