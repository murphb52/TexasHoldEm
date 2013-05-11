package software.patterns.assignment;

import java.io.Serializable;


public class Card implements Cloneable, Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 6315140196116483159L;
	//A Card is a simple object with a number and suite
	public int Number;
	public String suite;
	
	public Card(){}
	
	public Card(int a, String b){
		this.Number = a;
		this.suite = b;
	}
	
	//Ace, King, Queen and Jack are represented using numbers
	//So when printing just have a few cases to print out the Strings rather than the number
	public void showCard(){
		if(this.Number == 1)
			System.out.println("Ace of " + this.suite + " ");
		else if(this.Number == 11)
			System.out.println("Jack of " + this.suite  + " ");
		else if(this.Number == 12)
			System.out.println("Queen of " + this.suite  + " ");
		else if(this.Number == 13)
			System.out.println("King of " + this.suite  + " ");
		else System.out.println(this.Number + " of " + this.suite  + " ");
	}
	
	//Overwriting the clone method
	public Object clone(){
		Card theClone = null;
		try{
			theClone = (Card) super.clone();
		}
		catch (CloneNotSupportedException e) {
            // Should never happen
            throw new InternalError(e.toString());
        }
		
		// Clone mutable members
		theClone.Number = this.Number;
		theClone.suite = this.suite;
        return theClone;
	}
	
	//Overwriting the equals method
	public boolean equals(Object o){
		if(o == null)
			return false;
		
		Card c;
		try{
			c = (Card) o;
		}
		catch (ClassCastException e) {
            return false;
        }
		
		if (Number == c.Number && suite.equals(c.suite)) {
            return true;
        }
		
		return false;
	}
	
	public String toString(){
		if(this.Number == 1)
			return ("Ace of " + this.suite + " ");
		else if(this.Number == 11)
			return ("Jack of " + this.suite  + " ");
		else if(this.Number == 12)
			return ("Queen of " + this.suite  + " ");
		else if(this.Number == 13)
			return ("King of " + this.suite  + " ");
		else return(this.Number + " of " + this.suite  + " ");
		
	}

	public String getCardValue() {
		// TODO Auto-generated method stub
		if(this.Number == 1)
			return ("A");
		else if(this.Number == 11)
			return ("J");
		else if(this.Number == 12)
			return ("Q");
		else if(this.Number == 13)
			return ("K");
		else return(this.Number + "");
	}
}
