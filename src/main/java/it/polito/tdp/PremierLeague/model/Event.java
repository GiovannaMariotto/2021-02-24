package it.polito.tdp.PremierLeague.model;

public class Event implements Comparable<Event>{

	private Integer eventID;
	private Player player; //if player = null : INFORTUNIO
	private EventType type;
	
	public Event(Integer eventID, EventType type) {
		super();
		this.eventID = eventID;
		this.type=type;
	}


	public EventType getType() {
		return type;
	}




	public void setType(EventType type) {
		this.type = type;
	}


	public enum EventType{
		GOL,
		ESPULSIONE,
		INFORTUNIO
	}

	
	

	public Integer getEventID() {
		return eventID;
	}




	public void setEventID(Integer eventID) {
		this.eventID = eventID;
	}




	public Player getPlayer() {
		return player;
	}




	public void setPlayer(Player player) {
		this.player = player;
	}




	@Override
	public int compareTo(Event o) {
		return o.getEventID().compareTo(this.eventID) ;
	}




	@Override
	public String toString() {
		return "Event: " + eventID + ", player=" + player + "\n";
	}
	
	
	
	
}
