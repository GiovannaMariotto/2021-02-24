package it.polito.tdp.PremierLeague.model;

public class GiocatoreMigliore {

	private Player p1;
	double delta;
	
	public GiocatoreMigliore(Player p1, double peso) {
		super();
		this.p1 = p1;
		this.delta = peso; 
	}
	@Override
	public String toString() {
		return "GiocatoreMigliore : \n" + p1 + ", delta=" + delta + "\n";
	}
	public Player getP1() {
		return p1;
	}
	public void setP1(Player p1) {
		this.p1 = p1;
	}
	public double getPeso() {
		return delta;
	}
	public void setPeso(double peso) {
		this.delta = peso;
	}
	
	
	
	
	
	
}
