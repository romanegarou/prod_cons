package prodcons.v6;

import java.util.ArrayList;
import java.util.Date;

import jus.poc.prodcons.*;

public class Observations {
	ArrayList<Message> l;
	int init_nb_prod;
	int init_nb_conso;
	int init_nb_buffers;
	int nb_conso;
	int nb_prod;
	ArrayList<Producteur> p;
	ArrayList<Consommateur> c;
	ArrayList<Message> produit;
	ArrayList<Message> depose;
	ArrayList<Message> retrait;
	ArrayList<Message> consomme;
	
	public  Observations () {
		l= new ArrayList<Message>();
		p= new ArrayList<Producteur>();
		c= new ArrayList<Consommateur>();
		produit= new ArrayList<Message>();
		depose= new ArrayList<Message>();
		retrait= new ArrayList<Message>();
		consomme= new ArrayList<Message>();
	}
	//a l'initialisation du systeme.
	public  void init(int nbProducteurs, int nbConsommateurs, int nbBuffers){
		this.init_nb_buffers=nbBuffers;
		this.init_nb_conso=nbConsommateurs;
		this.init_nb_prod=nbProducteurs;
	}
	//lorsqu'un nouveau producteur P est créé,
	public void newProducteur(Producteur P){
		this.p.add(P);
		//TODO verifier que le prod est valide
	}
	// lorsqu'un nouveau consommateur C est créé,
	public void newConsommateur(Consommateur C) {
		this.c.add(C);
		//TODO verifier que le conso est valide
	}

	//lorsqu'un producteur P produit un nouveau message M avec un délai de production de T,
	public void productionMessage(Producteur P, Message M, int T){
		if (P==null) System.out.println("Probleme\n");
		if (p.indexOf(P)==-1)System.out.println("Probleme\n");
		if (M==null)System.out.println("Probleme\n");
		if (!(T<temp_moyen -deviation && T>temp_moyen +deviation))System.out.println("Probleme\n");
		produit.add(M);
		//recuperer le delai de prod 
	}

	//TODO verifier que tout les message sont produit
	

	//lorsqu'un message M est déposé dans le tampon par le producteur P,
	public void depotMessage(Producteur P, Message M){
		// TODO verifier qu'on as au moins attendu le temp.
		if (P==null) System.out.println("Probleme\n");
		if (p.indexOf(P)==-1)System.out.println("Probleme\n");
		if (M==null)System.out.println("Probleme\n");
		int i = produit.indexOf(M);
		if (i==-1){ //le message n'a pas ete produit
			System.out.println("Probleme\n");
		}
		depose.add(M);
		produit.remove(i);
	}
	
	
	
	
	//lorsqu'un message M est retiré du tampon par le consommateur C.
	public void retraitMessage(Consommateur C, Message M){
		if (C==null) System.out.println("Probleme\n");
		if (c.indexOf(C)==-1)System.out.println("Probleme\n");
		if (M==null)System.out.println("Probleme\n");
		int i = depose.indexOf(M);
		if (i==-1){ //le message n'a pas ete depose
			System.out.println("Probleme\n");
		}
		if (i!=0){// on essaie pas de retire le premier message du tampon
			System.out.println("Probleme\n");
		}
		depose.remove(i);
		retrait.add(M);
	}
	
	
	
	//lorsqu'un consommateur C consomme un message M avec un délai de T,
		public void consommationMessage(Consommateur C, Message M, int T){
			if (C==null) System.out.println("Probleme\n");
			if (c.indexOf(C)==-1)System.out.println("Probleme\n");
			if (M==null)System.out.println("Probleme\n");
			int i = retrait.indexOf(M);
			if (i==-1){ //le message n'a pas ete depose
				System.out.println("Probleme\n");
			}
			if (!(T<temp_moyen -deviation && T>temp_moyen +deviation))System.out.println("Probleme\n");
			retrait.remove(i);
			consomme.add(M);
		}
	
	public boolean bon_nombre_conso(){
		return this.c.size()==this.init_nb_conso;
	}
	
	public boolean bon_nombre_prod(){
		return this.p.size()==this.init_nb_prod;
	}
	
	//TODO
	//tout les messages creer sont deposes 
	//tout les messages lu sont traites
	//tout les messages deposes sont lu
	//respect des loi temporel
	//les messages sont lu dans le meme ordre qu'ils deposer
	//Les messages sont creer avant d'etre deposer
	//Les messages sont deposer avant d'etre lu
	//les messages sont lu avant d'etre traiter
	//verifier que toute les liste sauf consomme sont vide 
	//regarder que les prod ont produit a peut pret le bon nombre de message 
	// regarder le temp entre la prod et le depot
}
