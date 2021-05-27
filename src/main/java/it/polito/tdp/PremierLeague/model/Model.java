package it.polito.tdp.PremierLeague.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;

import it.polito.tdp.PremierLeague.db.PremierLeagueDAO;

public class Model {
	
	//grafo semplice, pesato e orientato
	
	PremierLeagueDAO dao;
	private Graph<Player, DefaultWeightedEdge> grafo;
	private Map<Integer,Player> idMap;
	private Simulatore sim;
	private Map<Integer,Action> azioni; //playedID and Action
	private List<Match> matches;
	private Match match;
	
	public Model() {
		this.dao=new PremierLeagueDAO();
		this.idMap= new HashMap<Integer, Player>();
		this.dao.listAllPlayers(idMap);
		
		
	}
	public Match getMatch() {
		return match;
	}
	public void setMatch(Match m) {
		this.match=m;
	}
	
	public void creaGrafo(Match m) {
		grafo= new SimpleDirectedWeightedGraph<>(DefaultWeightedEdge.class);
		this.match=m;
		
		//aggiungere i vertici
		Graphs.addAllVertices(this.grafo, this.dao.getVertici(m,idMap));
		
		//aggiungo gli archi
		for(Adiacenza a: dao.getAdiacenze(m, idMap)) { //Migliore verso Peggiore
			if(a.getPeso()>=0) {
				//p1 meglio di p2
				if(grafo.containsVertex(a.getP1()) && grafo.containsVertex(a.getP2())) {
					Graphs.addEdgeWithVertices(this.grafo, a.getP1(), a.getP2(),a.getPeso());
				}
			}else {
				//p2 meglio di p1
				if(grafo.containsVertex(a.getP1()) && grafo.containsVertex(a.getP2())) {
					Graphs.addEdgeWithVertices(this.grafo, a.getP2(), a.getP1(),(-1)*a.getPeso());	
				}
				
			}
			
			
		}
		
	}
	
	public int nVertici() {
		return this.grafo.vertexSet().size();
	}
	
	public int nArchi() {
		return this.grafo.edgeSet().size();
	}
	
	public List<Match> getAllMatches(){
		List<Match> matches = dao.listAllMatches();
		Collections.sort(matches,new Comparator<Match>() {
			@Override
			public int compare(Match o1,Match o2) {
				return o1.getMatchID().compareTo(o2.getMatchID());
			}
		});
		return matches;
	}
	
	public GiocatoreMigliore getMigliore() {
		if(this.grafo==null) {
			return null;
		}
		Player best = null;
		Double maxDelta = (double) Integer.MIN_VALUE;
		
		for(Player p : this.grafo.vertexSet()) {
			//Delta : somma dei pesi dei archi uscenti - somma dei pesi dei archi entranti
			double pesoUscente =0;
			//Calcolo la somma dei pesi degli archi uscenti
			for(DefaultWeightedEdge e : this.grafo.outgoingEdgesOf(p)) {
				pesoUscente+=this.grafo.getEdgeWeight(e);
			}
			//Calcolo la somma dei pesi degli archi entranti
			double pesoEntrante=0;
			for(DefaultWeightedEdge edge : this.grafo.incomingEdgesOf(p)) {
				pesoEntrante+=this.grafo.getEdgeWeight(edge);
			}
			
			double delta = pesoUscente-pesoEntrante;
			if(delta>maxDelta) {
				best=p;
				maxDelta=delta;
			}
			
		}//fine for
		
		return new GiocatoreMigliore(best,maxDelta);
	}
	

	public Graph getGrafo() {
		return this.grafo;
	}
	
	public void Simula(Integer n, Match m, Model model) {
		//inizializazzione 
		sim = new Simulatore();
		sim.setModel(model);
		this.match=m;
		azioni=getMapaAzioni(m);
		sim.setAzioni(azioni);
		
		sim.init(n,m); // mettere i parametri di input
		sim.run();
	
	}
	public Map<Integer,Action> getMapaAzioni(Match m){
		List<Action> listaAzioni = new LinkedList<Action>(this.dao.listAllActionsOfMatch(m));
		azioni= new HashMap<Integer,Action>();
		for(Action a2 : listaAzioni) {
			if(a2!=null && !(a2.getPlayerID()==null)) {
				azioni.put(a2.playerID , a2);
			}else {
				System.out.println("ERRORE: azione null");
			}
		}
		if(azioni==null) {
			System.out.println("ERRORE: mapa azioni null");
			return null;
		}
		
		return azioni;
	}

	
	public String getSimulationResult() {
		if(sim!=null) {
			String result = sim.getGols()+"\n"+sim.getEspulsioni();
			return result;
		}
		
		return null;
	}
	
	public void setSimModel(Model m) {
		this.sim.setModel(m);
	}
	
}
