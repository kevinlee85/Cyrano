<?php

class Command {
	
	private $data = array("commandID",
                          "groupID",
                          "instructionNumber",
						  "name",
						  "description",
						  "type",
						  "filename", 
						  "url",
						  "delay");

    function __construct($commandID, $groupID, $instructionNumber, $name, $description, $type, $filename, $url, $delay) {
        $this->data['commandID'] = $commandID;
        $this->data['groupID'] = $groupID;
        $this->data['instructionNumber'] = $instructionNumber;
		$this->data['name'] = $name;
		$this->data['description'] = $description;
		$this->data['type'] = $type;
		$this->data['filename'] = $filename;
		$this->data['url'] = $url;
		$this->data['delay'] = $delay;
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
        return array("commandID" => $this->commandID,
                     "groupID" => $this->groupID,
					 "instructionNumber" => $this->instructionNumber,
					 "name" => $this->name,
					 "description" => $this->description,
					 "type" => $this->type,
					 "filename" => $this->filename,
					 "url" => $this->url,
					 "delay" => $this->delay); 
    }
}