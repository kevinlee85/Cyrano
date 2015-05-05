<?php

class Settings {
	private $data = array("terseMode",
				 "tsAudio",
				 "graphicalMode",
				 "friendFinder",
				 "friendAudio",
			 	 "textSize",
			 	 "gpsTimeDelay");

	function __construct($terseMode, $tsAudio, $graphicalMode, $friendFinder, $friendAudio, $textSize, $gpsTimeDelay) {
		$this->data['terseMode'] = $terseMode;
		$this->data['tsAudio'] = $tsAudio;
		$this->data['graphicalMode'] = $graphicalModee;
		$this->data['friendFinder'] = $friendFinder;
		$this->data['friendAudio'] = $friendAudio;
		$this->data['textSize'] = $textSize;
		$this->data['gpsTimeDelay'] = $gpsTimeDelay;
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
		return array("terseMode" => $this->terseMode,
					 "tsAudio" => $this->tsAudio,
					 "graphicalMode" => $this->graphicalMode,
					 "friendFinder" => $this->friendFinder,
					 "friendAudio" => $this->friendAudio,
					 "textSize" => $this->textSize,
					 "gpsTimeDelay" => $this->gpsTimeDelay);
	}

}