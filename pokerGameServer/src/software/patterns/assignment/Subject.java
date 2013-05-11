package software.patterns.assignment;
interface Subject
{
	//Interface for Subject with its defined methods
	public void registerObserver(Observer o);
	public void removeObserver(Observer o);
	public void dealCardsToObservers();
	public void displayCardsOfPlayers();
	public void checkHands();
}