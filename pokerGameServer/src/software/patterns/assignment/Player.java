package software.patterns.assignment;
import java.io.Serializable;

public class Player implements Observer, Cloneable, Serializable {
	//A Player is an object with a name and 2 cards
	String name;
	String address;
	
	Card card1;
	Card card2;
	int chipCount;
	Card[] cards = new Card[7];
	int index = 0;
	private int [] rank;
	
	public Player(){
		card1 = new Card();
		card2 = new Card();
		cards = new Card[7];
		index = 0;
	}
	
	public Player(String a){
		name = a;
		
		card1 = new Card();
		card2 = new Card();
		cards = new Card[7];
		for(int i = 0; i < 7; i++)
			cards[i] = new Card(0,"Diamonds");
		
		index = 0;
	}
	
	public Player(String a, String b, int c){
		name = a;
		address = b;
		card1 = new Card();
		card2 = new Card();
		chipCount = c;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		int [] rank = HandEvaluator.evaluate7(cards);
		String rankString = HandEvaluator.getRank(rank);
		String s = name + " at " + address + "\nChip Count:" + chipCount +"\n"+
				cards[0].toString() + "\n" +
				cards[1].toString() + "\n" +
				cards[2].toString() + "\n" +
				cards[3].toString() + "\n" +
				cards[4].toString() + "\n" +
				cards[5].toString() + "\n" +
				cards[6].toString() + "\n" + rankString;
		
		return s;
	}

	//Displaying the Player and their cards
	public void display() {
		System.out.print(name + "'s cards are: \n");
		card1.showCard();
		card2.showCard();
		System.out.println();
	}

	//Simple method for checking if a hand is "good"
	//If a player has a card that is an Ace or Jack or better then we say its "good"
	public Boolean isGoodHand() {
		if(card1.Number == 1 || card1.Number >= 10 || card2.Number == 1 || card2.Number >= 10)
			return true;
		else return false;
	}
		
	//Implementing the clone method
	public Object clone(){
		Player theClone = null;
		try{
			theClone = (Player) super.clone();
		}
		catch (CloneNotSupportedException e) {
            // Should never happen
            throw new InternalError(e.toString());
        }
		
		// Clone mutable members
		theClone.card1 = (Card) card1.clone();
		theClone.card2 = (Card) card2.clone();
		theClone.name = this.name;
        return theClone;
	}
	
	//implementing the equals method
	public boolean equals(Object o){
		if(o == null)
			return false;
		
		Player p;
		try{
			p = (Player) o;
		}
		catch (ClassCastException e) {
            return false;
        }
		
		if (name.equals(p.name) && card1.equals(p.card1) && card2.equals(p.card2)) {
            return true;
        }
		
		return false;
	}

	//reseting the cards the player had
	public void foldOut() {
		card1= null;
		card2 = null;
		System.out.println(name + " has Folded");
	}

	//Taking their first card from the deck
	public void dealFirstCard(DeckOfCards d) {
		card1 = (Card) d.deck[d.cardsDealt].clone();
		d.cardsDealt++;
	}

	//Taking their second card from the deck
	public void dealSecondCard(DeckOfCards d) {
		card2 = (Card) d.deck[d.cardsDealt].clone();
		d.cardsDealt++;
	}

	public void setCardOne(Card card) {
		card1 = (Card) card.clone();
	}
	
	public void setCardTwo(Card card) {
		card2 = (Card) card.clone();
	}
	
	public void addCard(Card card){
		Card temp = (Card) card.clone();
		cards[index] = new Card();
		cards[index] = temp;
		index++;
		//System.out.println("Index is " +index);
	}
	
	public void addFlop(Card card1, Card card2, Card card3){
		addCard(card1);
		addCard(card2);
		addCard(card3);

		//System.out.println("Index is " +index);
	}
	
	public void calculateRank(){
		setRank(HandEvaluator.evaluate7(cards));
	}

	public int [] getRank() {
		return rank;
	}

	public void setRank(int [] rank) {
		this.rank = rank;
	}

	public void resetCards() {
		// TODO Auto-generated method stub
		for(int i = 0; i < 7; i++)
			cards[i] = null;
		
		index = 0;
		
	}
		
}
