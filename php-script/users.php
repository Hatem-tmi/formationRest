<?php
  
  $number_of_records = isset($_GET['num']) ? intval($_GET['num']) : 10; //10 is the default


  $format = (isset($_GET['format']) && strtolower($_GET['format']) == 'xml') ? 'xml' : 'json'; //json is the default

  $user_id = -1;
  if(isset($_GET['id']) && intval($_GET['id']))
    $user_id = intval($_GET['id']);

  /* connect to the db */
  $link = mysql_connect('localhost','root','') or die('Cannot connect to the DB');
  mysql_select_db('webservice_test',$link) or die('Cannot select the DB');

  /* fetch the users from the db */
  $query = "";
  if($user_id != -1)
    $query = "SELECT * FROM `webservice_test`.`user` WHERE ID = $user_id ORDER BY ID DESC LIMIT $number_of_records;";
  else
    $query = "SELECT * FROM `webservice_test`.`user` ORDER BY ID DESC LIMIT $number_of_records;";
  
  $result = mysql_query($query,$link) or die('Errant query:  '.$query);

  /* create one master array of the records */
  $users = array();
  if(mysql_num_rows($result)) {
    while($user = mysql_fetch_assoc($result)) {
      array_push($users, $user);
    }
  }

  /* output in necessary format */
  if($format == 'json') {
    header('Content-type: application/json');
    echo json_encode(array('users'=>$users));
  }
  else {
    header('Content-type: text/xml');
    echo '';
    
    foreach($user as $index => $user) {
      if(is_array($user)) {
        foreach($user as $key => $value) {
          echo '<',$key,'>';
          if(is_array($value)) {
            foreach($value as $tag => $val) {
              echo '<',$tag,'>',htmlentities($val),'</',$tag,'>';
            }
          }
          echo '</',$key,'>';
        }
      }
    }
    echo '';
  }

  /* disconnect from the db */
  @mysql_close($link);

?> 
