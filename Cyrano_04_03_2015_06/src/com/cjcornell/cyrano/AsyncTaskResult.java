package com.cjcornell.cyrano;

public class AsyncTaskResult<T> 
{
	private T result;
	
	private Exception error;
	
	public T getResult() 
	{
	    return result;
	}
	public Exception getError() 
	{
	    return error;
	}
	
	
	public AsyncTaskResult(T result) 
	{
	    this.result = result;
	}
	
	public AsyncTaskResult(Exception error) 
	{
	    this.error = error;
	}
}