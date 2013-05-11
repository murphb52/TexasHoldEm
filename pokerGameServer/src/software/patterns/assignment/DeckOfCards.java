package software.patterns.assignment;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class DeckOfCards implements Cloneable, Serializable{
	//Deck of cards is an array of 52 Cards
	public Card [] deck = new Card[52];
	//Integer to keep track of where the dealer is in the pack
	//Dont want to delete cards as dealer should know every card that has past come out to avoid cheating
	int cardsDealt = 0;
	
	public DeckOfCards(){}
	
	//Setting up the pack by creating 52 cards of every number from 1-13 with each suite
	public void setupPack() {
		for(int i = 0; i < 52; i++){
			this.deck[i] = new Card();
			this.deck[i].Number = (i%13)+1;
			if(i < 13)
				this.deck[i].suite = "Diamonds";
			else if(i < 26)
				this.deck[i].suite = "Hearts";
			else if(i < 39)
				this.deck[i].suite = "Spades";
			else this.deck[i].suite = "Clubs";
		}
	}

	//Converting the array to an Arraylist to shuffle
	//Once shuffled change ArrayList back to array
	public void shuffle() {
		List<Card> list = Arrays.asList(deck);
		Collections.shuffle(list);
		deck = (Card[]) list.toArray();	
	}

	//Test method to print out all the cards in the deck
	public void showDeck() {
		for(int i = 0; i < 52; i++){
			deck[i].showCard();
		}	
	}
	
	//Overwriting the clone() method
	public Object clone(){
		DeckOfCards theClone = null;
		try{
			theClone = (DeckOfCards) super.clone();
		}
		catch (CloneNotSupportedException e) {
            // Should never happen
            throw new InternalError(e.toString());
        }
		
		// Clone mutable members
		theClone.cardsDealt = this.cardsDealt;
		for(int i = 0; i < deck.length; i++){
			theClone.deck[i] = this.deck[i];
		}

        return theClone;
	}
	
	//Overwriting the equals method
	public boolean equals(Object o){
		if(o == null)
			return false;
		
		DeckOfCards c;
		try{
			c = (DeckOfCards) o;
		}
		catch (ClassCastException e) {
            return false;
        }
		
		if (deck.equals(c.deck) && cardsDealt == c.cardsDealt) {
            return true;
        }
		
		return false;
	}
	
}
