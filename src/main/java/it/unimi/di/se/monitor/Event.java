package it.unimi.di.se.monitor;

public class Event {
	
	private String name;
	private long time;
	
	public static final String NONE = "NONE";
	private static final String STOP = "STOP";
	private static final String RESET = "RESET";
	private static final String READ_STATE = "READ_STATE";
	private static final long DEFAULT = -1L;
	
	public Event(String name, long time){
		this.name = name;
		this.time = time;
	}
	
	public static Event readStateEvent(){
		return new Event(READ_STATE, DEFAULT);
	}
	
	public static Event resetEvent(){
		return new Event(RESET, DEFAULT);
	}
	
	public static Event stopEvent(){
		return new Event(STOP, DEFAULT);
	}
	
	public boolean isStop(){
		return name.equals(STOP) && time == DEFAULT;
	}
	
	public boolean isReset(){
		return name.equals(RESET) && time == DEFAULT;
	}
	
	public boolean isReadState(){
		return name.equals(READ_STATE) && time == DEFAULT;
	}
	
	public String getName(){
		return name;
	}
	
	public long getTime(){
		return time;
	}
}
