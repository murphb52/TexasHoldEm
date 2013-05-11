package software.patterns.assignment;
import java.util.ArrayList;

public class PokerGame implements Subject {
	//A PokerGame is a Singleton meaning only one instance can be created at a time
	private static PokerGame INSTANCE = null;
	//The game has an ArrayList of Players (Observers)
	private ArrayList<Observer> observers;
	//A Game has a DeckOfCards
	DeckOfCards deck;
	
	PokerGame(){
		//Initializing the Observer ArrayList
		observers = new ArrayList<Observer>();
	}
	
	public static synchronized PokerGame getInstance() 
	   {
	     //Initializing the instance of a PokerGame
		 if(INSTANCE==null)
			INSTANCE=new PokerGame();
		
	     return INSTANCE;
	   }

	void drawTableCards() {
		//Taking the 5 cards from the deck after the player cards have been dealt
		System.out.println("\nTable Cards : ");
		for(int i = 0; i < 5; i++){
			
		deck.deck[deck.cardsDealt].showCard();
		
		deck.cardsDealt++;
		}
	}

	void setup() {
		//Setting up everything that is needed in the game and pack of cards
		getInstance();
		deck = new DeckOfCards();
		deck.setupPack();
		deck.shuffle();
	}

	public void registerObserver(Observer o) {
		//Adding an Observer
		observers.add(o);
	}

	//Removing and Observer and the Observer folding
	public void removeObserver(Observer o) {
		int j = observers.indexOf(o);
		if (j >= 0) {
			Observer observer = (Observer)observers.get(j);
			observer.foldOut();
			observers.remove(j);
		}
	}

	//Dealing Cards 1 and 2 to the Observers
	//Must be dealt in rounds
	//Each Observer must get first card then and only then can each observer get their second card
	public void dealCardsToObservers() {
		for (int j = 0; j < observers.size(); j++) {
			Observer observer = (Observer)observers.get(j);
			observer.dealFirstCard(deck);
		}
		for (int j = 0; j < observers.size(); j++) {
			Observer observer = (Observer)observers.get(j);
			observer.dealSecondCard(deck);
		}
	}

	//Showing the cards of each observer
	public void displayCardsOfPlayers() {
		for (int j = 0; j < observers.size(); j++){
			Observer observer = (Observer) observers.get(j);
			observer.display();
		}
	}

	//Getting each Observer to check their hand and if its a bad hand they fold by being removed
	public void checkHands() {
		for (int j = 0; j < observers.size(); j++){
			Observer observer = (Observer) observers.get(j);
			if(observer.isGoodHand() == false){
				removeObserver(observer);
			}
		}
	}
	
}
