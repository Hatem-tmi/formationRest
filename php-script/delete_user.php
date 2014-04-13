<?php

  $user_id = -1;
  if(isset($_GET['id']) && intval($_GET['id']))
    $user_id = intval($_GET['id']);

  $success = false;
  if($user_id != -1) {
    //Delete
    $connection = mysql_connect('localhost','root','') or die('Cannot connect to the DB');
    mysql_select_db('webservice_test', $connection) or die('Cannot select the DB');
    
    $query = "DELETE FROM `webservice_test`.`user` WHERE ID = $user_id;";

    $success = mysql_query($query);

    mysql_close($connection);
  }

  header('Content-type: application/json');
  echo json_encode(array('success'=>$success)); 
?>