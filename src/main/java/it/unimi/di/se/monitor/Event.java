package it.unimi.di.se.monitor;

public class Event {
	
	private String name;
	private long time;
	
	public static final String NONE = "NONE";
	private static final String STOP = "STOP";
	private static final long DEFAULT = -1L;
	
	public Event(String name, long time){
		this.name = name;
		this.time = time;
	}
	
	public static Event StopEvent(){
		return new Event(STOP, DEFAULT);
	}
	
	public boolean isStop(){
		return name.equals(STOP) && time == DEFAULT;
	}
	
	public String getName(){
		return name;
	}
	
	public long getTime(){
		return time;
	}
}
