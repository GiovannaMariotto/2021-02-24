package it.polito.tdp.PremierLeague.model;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
	
	public Model() {
		this.dao=new PremierLeagueDAO();
		this.idMap= new HashMap<Integer, Player>();
		this.dao.listAllPlayers(idMap);
		
	}
	
	
	public void creaGrafo(Match m) {
		grafo= new SimpleDirectedWeightedGraph<>(DefaultWeightedEdge.class);
		
		
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
}
