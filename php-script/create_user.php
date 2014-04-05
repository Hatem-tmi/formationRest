<?php

  $name = "";
  if(isset($_POST['name']))
    $name = $_POST['name'];

  $age = 0;
  if(isset($_POST['age']) && intval($_POST['age']))
    $age = intval($_POST['age']);

  $login = "";
  if(isset($_POST['login']))
    $login = $_POST['login'];

  $password = "";
  if(isset($_POST['password']))
    $password = $_POST['password'];

  $success = false;
  if($name != "" && $age > 0 && $login != "" && $password != "") {
    //Save
    $connection = mysql_connect('localhost','root','') or die('Cannot connect to the DB');
    mysql_select_db('webservice_test',$connection);
    
    mysql_query("INSERT INTO `webservice_test`.`user` (name, age, login, password)
                VALUES ('" . $name . "', '" . $age . "', '" . $login . "', '" . $password . "')");
    mysql_close($connection);

    $success = true;
  }

  header('Content-type: application/json');
  echo json_encode(array('success'=>$success)); 
?>