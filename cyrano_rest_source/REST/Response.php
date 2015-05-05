<?php 

class Response {

    private $data = array('success', 'error', 'body');

    function __construct($success, $message, $body, $code) {
        if ($success)
            $this->data['success'] = array('code' => $code, 'message' => $message);
        else
            $this->data['error'] = array('code' => $code, 'message' => $message);
        if (! is_null($body))
          $this->data['body'] = $body;
        header('X-PHP-Response-Code: {$code}', true, $code);
    }

    public function __get($name) {
        if (array_key_exists($name, $this->data)) {
            return $this->data[$name];
        }

        $trace = debug_backtrace();
        trigger_error(
            'Undefined property via __get(): ' . $name .
            ' in ' . $trace[0]['file'] .
            ' on line ' . $trace[0]['line'],
            E_USER_NOTICE);
        return null;
    }

    public function __set($name, $value) {
        if (array_key_exists($name, $this->data)) {
            $this->data[$name] = $value;
        }

        $trace = debug_backtrace();
        trigger_error(
            'Undefined property via __set(): ' . $name .
            ' in ' . $trace[0]['file'] .  
            ' on line ' . $trace[0]['line'],
            E_USER_NOTICE);
        return null;
    }

    public function getJSONData() {
        $json = array();
        if (isset($this->data['success']))
            $json['success'] = $this->data['success'];
        else
            $json['error'] = $this->data['error'];
        if (isset($this->data['body']))
            $json['body'] = $this->data['body'];
        return $json;
    }
}
