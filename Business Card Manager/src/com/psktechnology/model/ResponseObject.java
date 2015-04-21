package com.psktechnology.model;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ResponseObject implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private String status, msg, id;
	
	// for security questions
//	private ArrayList<QuestionsOne> questionOneObj;
	

	@JsonProperty("status")
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	
	@JsonProperty("msg")
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	
	@JsonProperty("id")
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}	
	
	// for security questions
//	@JsonProperty("securityquestionsone")
//	public ArrayList<QuestionsOne> getQuestionOneObj() {
//		return questionOneObj;
//	}
//	public void setQuestionOneObj(ArrayList<QuestionsOne> questionOneObj) {
//		this.questionOneObj = questionOneObj;
//	}

}