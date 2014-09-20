package software.patterns.assignment;
interface Observer
{
	//Observer interface with defined methods
	public void display();
	public Boolean isGoodHand();
	public void foldOut();
	public void dealFirstCard(DeckOfCards deck);
	public void dealSecondCard(DeckOfCards deck);
}
