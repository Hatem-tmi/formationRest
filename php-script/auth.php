<?php

  $login = "";
  if(isset($_POST['login']))
    $login = $_POST['login'];

  $password = "";
  if(isset($_POST['password']))
    $password = $_POST['password'];

  $result_array;
  if($login != "" && $password != "") {
    $connection = mysql_connect('localhost','root','') or die('Cannot connect to the DB');
    mysql_select_db('webservice_test', $connection) or die('Cannot select the DB');
    
    $query = "SELECT * FROM user WHERE login LIKE '$login' AND password LIKE '$password';";

    $result = mysql_query($query, $connection) or die('Errant query:  '.$query);
    
    if(mysql_num_rows($result)) {
      $row = mysql_fetch_assoc($result);

      $result_array = array('success'=>true, 'user'=>$row);
    }
    else {
      $result_array = array('success'=>false);
    }

    mysql_close($connection);
  }
  else {
    $result_array = array('success'=>false);
  }

  header('Content-type: application/json');
  echo json_encode($result_array); 
?>