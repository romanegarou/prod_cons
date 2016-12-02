package projet_prod_cons;

import jus.poc.prodcons.*;

public class MessageX implements Message{
	private Producteur  source;
	private int numero;
	private int numero_bis;
	private String contennu;
	private int date;
	
	public String toString (){
		
		return "Source"+source.toString()+"Numero"+String.valueOf(numero)+"Contennu"+contennu+"Date"+String.valueOf(date)+ String.valueOf(numero_bis);
		//TODO 
	}
	public MessageX(Producteur p , int n, String contennu,int date ){
		this.source=p;
		this.numero=n;
		this.contennu=contennu;
		this.date=date;
		this.numero_bis=0;
	}
	
	public void set_num (int n){
		numero_bis=n;
	}
	
	
}
