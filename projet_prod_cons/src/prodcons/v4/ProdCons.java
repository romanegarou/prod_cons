package prodcons.v4;



import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import jus.poc.prodcons.*;


public class ProdCons implements Tampon {
	
	private int num; //numero du message TODO peut être à supprimer
	private List<Message> l_messages; //TODO peut être à supprimer 
	private int taille;// taille du buffer
	private Message tampon[];// buffer
	private int index_lecture;// indice de la zone du tampon ou il faut lire
	private int index_ecriture;// indice de la zone du tamtpon ou il faut ecrire
	private int enAttente;// nombre de message present dans le tampon
	private Semaphore notFull;
	private Semaphore notEmpty;
	private Semaphore mutex;
	private Semaphore synchrone;
	private int nb_prod_alive = 0;

	//private Condition notEmpty, notFull;
	
	
	// constructeur 
	public ProdCons(int taille){
		this.taille=taille;
		this.tampon=new Message[taille];
		this.index_lecture=0;
		this.index_ecriture=0;
		this.enAttente=0;
		notFull= new Semaphore(taille);
		notEmpty= new Semaphore(0);
		mutex= new Semaphore(1);
		synchrone = new Semaphore(0);
		num = 0;
		l_messages = new LinkedList<Message>();
	}

	public synchronized void nv_prod (){
		nb_prod_alive ++;
	}
	
	public synchronized void fin_prod(){
		nb_prod_alive --;
		if (cons_should_die()) notifyAll();
	}
	public synchronized boolean  cons_should_die (){
		return (nb_prod_alive==0) && (enAttente==0);
	}


	@Override
	public int enAttente() {
		return this.enAttente;
	}

	@Override
	public Message get(_Consommateur arg0) throws Exception, InterruptedException {
		Consommateur conso = ((Consommateur) arg0); //TODO à retirer. je le garde pour le moment pour faire je_parle
		conso.je_parle("j'arrive dans le get");
		notEmpty.P();
		conso.je_parle("j'ai passé notEmpty.P()");
		Message m = tampon[index_lecture];
		int index_temp = index_lecture; //TODO il me semble que j'avais trouvé une meilleure solution pour n'incrémenter qu'une seule fois mais j'ai oublié. Si ça me revient faut changer du coup

		if(!((MessageX) m).retrait_possible()) //retrait_possible incrémente le compteur de consommateurs dans le message. ensuite cela retourne false si il n'y a pas autant de consommateurs que d'exemplaires
		{
			synchrone.P();	//Si le consommateur n'est pas le dernier attendu, il faut qu'il se bloque en attendant l(es) autre(s)
			((Consommateur) arg0).je_parle("J'ai passé synchrone.P()");
		}
		conso.je_parle("je suis sorti du premier if");
		
		
		if(((MessageX) m).reveil_necessaire()) //reveil_necessaire décrémente le compteur de consommateurs sur le message puis vérifie qu'il reste des consommateurs à réveiller (compteur > 0)
		{ //TODO ajouter un cas où il n'y a qu'un seul exemplaire
			
			synchrone.V(); // Le dernier consommateur ne passe pas dans le P() (voir plus haut). Il va ensuite passer dans V() pour réveiller le producteur (plus ancien thread endormi ici)
			//en passant ici, les autres consommateurs se réveillent successivement.
			//L'un des consommateur n'a pas besoin de passer ici (quand tous les autres sont réveillés). d'où le if
			
			conso.je_parle("J'ai passé le synchrone.V()");
		}
		conso.je_parle("J'ai passé le deuxième if");
		mutex.P();
		
		index_lecture= (index_temp+1)%taille; //tous les consommateurs ont mémorisé l'index du message dans index_temp. du coup ça revient à ne l'incrémenter qu'une seule fois.
		enAttente --;
		((MessageX) m).set_date_retrait(new Date());
		((Consommateur) arg0).je_parle("Je get le message " +(((MessageX) m).get_numero()+1));//TODO probleme avec le numero du message
		
		mutex.V();
		notFull.V();
		return m;
		
	}

	@Override
	public void put(_Producteur arg0, Message arg1) throws Exception, InterruptedException {
		Producteur pro = ((Producteur) arg0); //TODO à retirer
		pro.je_parle("J'essaye de mettre un message\n");
		notFull.P();
		mutex.P();
		num++;
		((MessageX)arg1).set_num(num);
		tampon[index_ecriture]= arg1;
		index_ecriture = (index_ecriture +1)%taille;
		enAttente++;
		l_messages.add(arg1);
		((MessageX) arg1).set_date_envoi(new Date());
		((Producteur) arg0).je_parle("Je put le message "+num);

		mutex.V();
		for(int i = 0; i < ((MessageX) arg1).get_nbExemplaires(); i++)
		{
			notEmpty.V();
		}
		pro.je_parle("j'ai passé notEmpty.V()");
		synchrone.P(); /*Cette ligne bloque le producteur.
		C'est important de le faire après le mutex.V() et le notEmpty.V() car ça permet de laisser d'autres acteurs passer.
		Le producteur a donc déposé un message et attend que les consommateurs soient assez nombreux pour pouvoir continuer 
		---
		Ce producteur sera débloqué par le dernier consommateur à arriver dans le get (voir la méthode get)
		*/
		
		synchrone.V(); //peut être vérifier que le nbExemplaires est > 1. sinon .V() n'a personne à réveiller
		/* Quand le producteur est réveillé,
		 *  ça veut dire que tous les consommateurs nécessaires sont réunis pour consommer le message
		 *  Il faut que le producteur réveille un consommateur endormi (ici ce sera le premier consommateur arrivé dans le get)
		 */
	}

	@Override
	public int taille() {
		return this.taille;
	}

	
	@Override
	public String toString()
	{
		String res = "[";
		char occupe = 'X';
		char libre = ' ';
		if (index_lecture > index_ecriture)
		{
			occupe = ' ';
			libre = 'X';
		}
		
		for(int i = 0; i < index_lecture ; i++)
		{
			res += libre + ",";
		}
		for(int i = index_lecture; i < index_ecriture ; i++)
		{
			res += occupe +",";
		}
		for (int i = index_ecriture ; i < taille ; i++)
		{
			res += libre + ",";
		}
		return res += "]";
	}
	
	
	
	//tests à lancer à la fin du processus
	

	public void tests_temporels(Date date_arret)
	{
		//Booleens vérifiant les différents tests.
		boolean test_fifo_valide = true;
		boolean test_messages_consommes = true;
		boolean test_depot_avant_retrait = true;
		
		
		//Dates de dépôt et retrait
		Date dated = null; 
		Date dater = null;  
		Date datec = null;
		
		System.out.println("Test : On affiche les temps de dépôt et retrait de chaque message :");
		for(Message m : l_messages)
		{
	
			//on vérifie si le test sur l'ordre de retrait des messages est valide en regardant si le message est retiré après le précédent.
			if(m != l_messages.get(0) && dater.after(((MessageX) m).get_date_retrait().get(0)))
			{
					test_fifo_valide = false;
					System.out.println("*");
			}

			//Si les messages ne sont pas déposés selon le même ordre que la liste, ce n'est pas normal
			if(m != l_messages.get(0) && !dated.before(((MessageX) m).get_date_envoi()))
			{
				System.out.println("La liste de messages n'est pas construite comme prévu !");
			}

				
			dated = ((MessageX) m).get_date_envoi();			
			dater = ((MessageX) m).get_date_retrait().get(0);
			datec = ((MessageX) m).get_date_consommation().get(0);
			
			
			//On vérifie que chaque message a été déposé avant d'être retiré
			if(dated.after(dater))
			{
				test_depot_avant_retrait = false;
				System.out.println("***");
			}
			
			
			//On vérifie que chaque message est consommé avant la fin du programme
			if(datec.after(date_arret))
			{
				test_messages_consommes = false;
				System.out.println("**  Date arret = " + MessageX.Format_HeureMinuteSeconde(date_arret));
			}
			
			
			String s_dated = MessageX.Format_HeureMinuteSeconde(dated);
			String s_dater = MessageX.Format_HeureMinuteSeconde(dater);

			System.out.println("  dépôt : " + s_dated+ "    retrait : " + s_dater);
		}
		// le delai entre chaque message d'un producteur respecte une loi proba
		//TODO
		//pareil pour les consos
		//TODO
		System.out.println("fifo : Chaque message a-t-il été retiré dans l'ordre où il a été déposé ?");
		if(test_fifo_valide) 
		{
			System.out.println("Le test a bien été validé.\n\n");
		}
		else System.out.println("Le test n'a pas été validé.\n\n");
		
		System.out.println("Exécution complète : Chaque message produit a-t-il été consommé avant la fin du programme ?");
		if(test_messages_consommes)
		{
			System.out.println("Le test a bien été validé.\n\n");
		}
		else
		{
			System.out.println("Le test n'a pas été validé.\n\n");
		}
		
		System.out.println("Dépot avant retrait : Chaque message produit a-t-il été retiré par un consommateur ?");
		if(test_depot_avant_retrait)
		{
			System.out.println("Le test a bien été validé.\n\n");
		}
		else
		{
			System.out.println("Le test n'a pas été validé.\n\n");
		}
	}
	
	

}