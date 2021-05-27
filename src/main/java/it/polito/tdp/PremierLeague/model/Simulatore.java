package it.polito.tdp.PremierLeague.model;

import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;

import org.jgrapht.Graph;import org.jgrapht.graph.DefaultWeightedEdge;

import it.polito.tdp.PremierLeague.model.Event.EventType;

public class Simulatore {

	//Coda Prioritaria
	private PriorityQueue queue;
	
	//Modello del Mondo
	private Integer nAzioni;//imposte dall'utente
	private Integer azioniID;
	private Double probabilita = Math.random();// [0,1]
	//private Graph grafo;
	private Model model;
	private Match match;
	private Map<Integer,Action> azioni; //K:MatchID V:Action
	Player giocatoreMigliore;
	
	//Parametri di Output
	private Integer golS1;//Squadra1
	private Integer golS2;//Squadra2
	private Integer espulsioneS1;
	private Integer espulsioneS2;
	
	
	
	public Simulatore() {
		queue = new PriorityQueue<Event>();
		//model = new Model();
		azioni = new HashMap<Integer,Action>();
		
	}
	
	
	public void setAzioni(Map<Integer, Action> azioni) {
		this.azioni = azioni;
	}


	public void init(Integer n, Match m) {
		//inizializzazione dei parametri 
		this.nAzioni=n;
		this.match=m;
		giocatoreMigliore = this.model.getMigliore().getP1();
		
		
		//Output:
		this.golS1=0;
		this.golS2=0;
		this.espulsioneS1=0;
		this.espulsioneS2=0;
		azioniID=0;
		
		//inizializazzione dei eventi:
		for(int i=0;i<this.nAzioni;i++) {
			if(match==null) { //Evitare eccezioni
				return;
			}
			queue.add(new Event(azioniID++,getEventType()));
			
			}//fine for
		}
		
		
	
	
	public EventType getEventType() {
		Double prob = Math.random();
		if(prob>=0.5) {//50%
			//GOL
			return EventType.GOL;
		} else if(prob>=0.7) { //30%
		//Espulsione
			return EventType.ESPULSIONE;
			
		} else //infortunio --> crea 2 oppure 3 nuovi eventi
		return EventType.INFORTUNIO;
	}
	
	
	
	
	//Processare i Eventi:
 	public void run() { 
 		
 		
 		//Mentre Queue non vuota: prosseguo
 		while(!this.queue.isEmpty()) {
 			Event e = (Event) this.queue.poll();
 			
 			switch (e.getType()) {
 				
 			case GOL:
 					if(azioni.containsKey(giocatoreMigliore.getPlayerID())) {
 						
 						if(this.match.teamHomeFormation>this.match.teamAwayFormation) { //2 interi
 							
 							this.golS1++;
 							break;
 						}else if(this.match.teamAwayFormation>this.match.teamHomeFormation) {
 							
 							this.golS2++;
 							break;
 						}else {//2. GiocatoreMigliore
 							 giocatoreMigliore = model.getMigliore().getP1();
 							if(azioni.containsKey(giocatoreMigliore.getPlayerID())) {
 								Action a = azioni.get(giocatoreMigliore.getPlayerID());
 								if(a.teamID==this.match.getTeamHomeID()) {
 									this.golS1++;
 									break;
 								}else //if(a.teamID==this.match.getTeamAwayID()) 
 									{
 									this.golS2++;
 									break;
 								}
 								}
 						}//fine 2
 						
 					}else {
 						System.out.println("Errore nel evento GOL: azioni non contiene matchID");
 						return;
 					}
 				
 			case ESPULSIONE:
 					giocatoreMigliore = model.getMigliore().getP1();
					Action a = azioni.get(giocatoreMigliore.getPlayerID());
 				if(azioni.containsKey(giocatoreMigliore.getPlayerID())) {
 					Double probEspulsione = Math.random();
 					if(probEspulsione<0.6) {
 						if(a.playerID==giocatoreMigliore.playerID && this.match.teamHomeID==a.teamID) {
 							this.espulsioneS1++;
 							break;
 						}else { //se non è della squadra 'home'(1), sarà della squara 'away'(2)
 							this.espulsioneS2++;
 							break;
 						}
 					} else {
 						if(a.playerID==giocatoreMigliore.playerID && this.match.teamHomeID==a.teamID) {
 							//giocatoreMigliore della Squara 1
 							this.espulsioneS2++;
 							break;
 						}else {//giocatoreMigliore della Squadra 2
 							this.espulsioneS1++;
 							break;
 						}		
 						
 					}
 					
 				}else {
						System.out.println("Errore nel evento ESPULSIONE: azioni non contiene playerID");
						return;
					}
 				
 			case INFORTUNIO:	
 			Double probInfortunio = Math.random();
 			if(probInfortunio>=0.5) { //crea 2 eventi
 				for( int i=0;i<2;i++) {
 					queue.add(new Event(azioniID++,getEventType()));
 				}
 				break;
 			}else { //crea 3 eventi
 				for( int i=0;i<3;i++) {
 					queue.add(new Event(azioniID++,getEventType()));
 				}
 				break;
 			}
 				
 			
 			}
			
 		
 		
 		}
 		 //end while
 		
 		
 		
 		
 	
 	}	 //end Method
 			
 				
	
 	
 	public String getEspulsioni() {
		String espulsioni = "ESPULSIONI:\n Squadra 1: "+this.espulsioneS1+" X Squadra 2: "+this.espulsioneS2+"\n";  
 		return espulsioni;
 		
 	}
 	
 	
 	public String getGols() {
 		String gols = "GOLS:\nSquadra 1: "+this.golS1+" X Squadra 2: "+this.golS2+"\n";
 		return gols;
 	}
 	
 	public void setModel(Model m) {
 		this.model=m;
 	}
	
}
