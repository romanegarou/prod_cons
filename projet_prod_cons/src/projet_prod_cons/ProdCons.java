package projet_prod_cons;

import java.util.concurrent.locks.Condition;

import jus.poc.prodcons.*;
public class ProdCons implements Tampon {
	
	private int taille;// taille du buffer
	private Message tampon[];// buffer
	private int index_lecture;// indice de la zone du tampon ou il faut lire
	private int index_ecriture;// indice de la zone du tamtpon ou il faut ecrire
	private int enAttente;// nombre de message present dans le tampon
	//private Condition notEmpty, notFull;
	
	
	// constructeur 
	public ProdCons(int taille){
		this.taille=taille;
		this.tampon=new Message[taille];
		this.index_lecture=0;
		this.index_ecriture=0;
		this.enAttente=0;
	}

	@Override
	public int enAttente() {
		return this.enAttente;
	}

	@Override
	public synchronized Message get(_Consommateur arg0) throws Exception, InterruptedException {
		while (enAttente==0){
			/*notEmpty.*/wait();
		}
		Message m = tampon[index_lecture];
		index_lecture= (index_lecture+1)%taille;
		enAttente --;
		/*notFull.signal()*/ notifyAll();
		return m;
	}

	@Override
	public synchronized void put(_Producteur arg0, Message arg1) throws Exception, InterruptedException {
		while (enAttente==taille){
			/*notFull.*/wait();
		}
		
		tampon[index_ecriture]= arg1;
		index_ecriture= (index_ecriture+1)%taille;
		enAttente++;
		/*notEmpty.signal()*/ notifyAll();
	}

	@Override
	public int taille() {
		return this.taille;
	}

}
