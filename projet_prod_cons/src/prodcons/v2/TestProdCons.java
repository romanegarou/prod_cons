package prodcons.v2;

import java.util.ArrayList;

import jus.poc.prodcons.*;

public class TestProdCons  extends Simulateur {
	private Observateur obs;
	private int taille_tampon=3;
	int nb_conso=2;
	int nb_prod=2;
	Tampon tampon;
	ArrayList <Producteur> p;
	ArrayList <Consommateur> c;
	
	public TestProdCons(Observateur observateur) {
		super(observateur);
		this.obs=observateur;
		
		
		// TODO Auto-generated method stub
		//corps du programme principal
		//TODO recuperer l nombre de producteur 
		
		//TODO recuperer nombre conso
		
		
	//TODO recuperer taille du tampon 
		
		tampon = new ProdCons(taille_tampon);
		
		
		Acteur prod;
		p= new ArrayList<Producteur>();// posse possiblement probleme comme initialisation
		int moyenneTempsDeTraitement= 10;//TODO a changer
		int deviationTempsDeTraitement =5;//TODO a changer
		for (int i=0;i<=nb_prod;i++){
		
			try {
				prod=new Producteur(Producteur.Prod(), obs, moyenneTempsDeTraitement, deviationTempsDeTraitement, tampon);
				p.add((Producteur)prod); 
			} catch (ControlException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
		
			}
		
			
		
		}
		
		c= new  ArrayList<Consommateur>();// posse possiblement probleme comme initialisation
		moyenneTempsDeTraitement= 10;//TODO a changer
		deviationTempsDeTraitement =5;//TODO a changer
		for (int i=0;i<=nb_prod;i++){
			try {
				c.add(new Consommateur(Consommateur.Cons(), obs, moyenneTempsDeTraitement, deviationTempsDeTraitement, tampon));
			} catch (ControlException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
		}
		
	}

	

	@Override
	protected void run() throws Exception {

		boolean b= true;
		for (int i=0;i<nb_prod;i++){
			if (p.get(i)==null) System.out.println("petit probleme\n");
			else System.out.println(p.get(i).toString());
			p.get(i).start();
			//b=b && p.get(i).isAlive();
		}
		for (int i=0;i<nb_conso;i++){
			System.out.println(c.get(i).toString());
			c.get(i).start();
		}
	}

	public static void main(String[] args) {
		new TestProdCons(new Observateur()).start();
	}
	
	
	



}