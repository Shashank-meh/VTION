
<?php 
     error_reporting(E_ALL);
    //post Data
    $name=$_POST['name'];
    $email=$_POST['email'];
    $phoneNumber=$_POST['phoneNumber'];
    $country=$_POST['country'];
    $query=$_POST['query'];
    if($name=='' && $email ==''&& $phoneNumber=='' && $country=='' && $query==''){
        $response= array('response'=> 'Parameter is Empty. Please check','status'=>false);
        echo json_encode($response);
    }else{
        echo 'HI';
        die;
        $msg='<p> Hello Manoj, This is the New Customer</p><p>
        Name : '.$name.'</p><p>email : '.$email.'</p><p>country : '.$country.'</p><p>phone : '.$phoneNumber.'</p><p>Query : '.$query.'</p>';
        $subject="IMPORTANT : New Customer Query --  Website ---";
        $api_key="key-2d3f9d68f56245b0c063d4edc9a919a5";/* Api Key got from https://mailgun.com/cp/my_account */
        $domain ="edunetwork.in";/* Domain Name you given to Mailgun */
        $ch = curl_init();
        curl_setopt($ch, CURLOPT_HTTPAUTH, CURLAUTH_BASIC);
        curl_setopt($ch, CURLOPT_USERPWD, 'api:'.$api_key);
        curl_setopt($ch, CURLOPT_RETURNTRANSFER, 1);
        curl_setopt($ch, CURLOPT_CUSTOMREQUEST, 'POST');
        curl_setopt($ch, CURLOPT_URL, 'https://api.mailgun.net/v2/'.$domain.'/messages');
        curl_setopt($ch, CURLOPT_POSTFIELDS, array(
            'from' => $email,
            'to' => 'Saurabh.singh@semusi.com',
            'subject' => $subject,
            'html' => $msg
        ));
        $result = curl_exec($ch);
        $responseData= array('response'=> 'Query Posted Successfully','status'=>true);
        echo json_encode($responseData);
    }

?>