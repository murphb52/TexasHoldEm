package software.patterns.assignment;
/*Simple program to test the classes
- PokerGame
- Card
- DeckOfCards
- Observer
- Player
- Subject
*/
public class GameExample {

	public static void main(String [] args){
		//Create the instance of a PokerGame
		PokerGame game = new PokerGame();
		//Setup the game at the start
		game.setup();
		
		//Create and register 3 players into the game
		Player p1 = new Player("Alice");
		Player p2 = new Player("Bob");
		Player p3 = new Player("Charlie");
		game.registerObserver(p1);
		game.registerObserver(p2);
		game.registerObserver(p3);
		
		//Dealing cards to all observers
		game.dealCardsToObservers();
		
		//Displaying all the cards of attached observers
		game.displayCardsOfPlayers();
		
		//All Observers are notified to check their hands and fold out if they dont have a decent hand
		game.checkHands();
		
		//Table Cards are drawn
		game.drawTableCards();
		
		//Full deck is shown to just check that all the cards were given out properly
		System.out.println("\nFull deck");
		game.deck.showDeck();
	}
}
