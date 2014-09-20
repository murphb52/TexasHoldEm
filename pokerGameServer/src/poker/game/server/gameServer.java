package poker.game.server;
import java.io.IOException;

import java.io.ObjectInputStream;

import java.io.ObjectOutputStream;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;

import java.net.Socket;
import java.util.ArrayList;
import java.util.Enumeration;

import software.patterns.assignment.*;

public class gameServer {

	private ServerSocket socket;
	private Socket client;
	private Boolean connecting = true, firstHand = true;
	private ObjectInputStream []in;
	private ObjectOutputStream []out;
	private Socket[] clientArray = new Socket[6];
	private int playerCount, smallBlind, bigBlind, currentBet,minStake,pot,playersLeft,indexOfLastRaise,betUntil;
	private int[] playerBet;
	private ArrayList <Player> playerList;
	private ArrayList <Boolean> stillInHand,hasBet,foldGroup;
	private DeckOfCards cardDeck;
	private int numOfPlayers = 2;
	private int indexOfWinner;
	private Player winner;

	public gameServer(int port) throws IOException{
		InetAddress ip = null;
		int countercounter = 0;
		try {

			Enumeration e = NetworkInterface.getNetworkInterfaces();

			while(e.hasMoreElements()) {
				NetworkInterface ni = (NetworkInterface) e.nextElement();
				//System.out.println("Net interface: "+ni.getName());

				Enumeration e2 = ni.getInetAddresses();

				while (e2.hasMoreElements()){
					InetAddress temp = (InetAddress) e2.nextElement();
					if(countercounter==3)
						ip = temp;
					countercounter++;
					//System.out.println("IP address: "+ ip.toString());

				}
			}

		} catch (Exception e) {

			e.printStackTrace();

		}
		InetAddress ia = InetAddress.getByName(ip.toString().substring(1));	
		socket=new ServerSocket(53535,0,ia);
		System.out.println("Server running on " + ip.toString().substring(1) + " on port " + socket.getLocalPort());

	}

	public void start() throws IOException, ClassNotFoundException, InterruptedException{
		
			
			//Program when someone is all in
			//bigBlind/smallBling

			initilizeVariables();
			takeInPlayers();
			//		System.out.println("Finished taking in Players\nNow going to send cards");
		while(true){
			createNewDeck();
			sendHandCardsToPlayers();
			//		System.out.println("Finished Sending cards\nGoing to take bets now");
			betTime();
			//		System.out.println("\nPot is " + pot);
			//		System.out.println("Finished taking bets going to send Flop");
			writeToAll("Sending Flop");
			sendFlopCards();
			betTime();
			writeToAll("Sending River");

			//Send River card
			for(int c = 0; c < playerCount; c++){
				System.out.println("\nSending River to Player " + c);
				out[c].writeObject(cardDeck.deck[(playerCount*2)+3]);
				playerList.get(c).addCard(cardDeck.deck[(playerCount*2)+3]);
			}

			betTime();
			writeToAll("Sending Run");

			//Send Run card
			for(int c = 0; c < playerCount; c++){
				System.out.println("\nSending Run to Player " + c);

				out[c].writeObject(cardDeck.deck[(playerCount*2)+4]);
				playerList.get(c).addCard(cardDeck.deck[(playerCount*2)+4]);
			}

			betTime();
			writeToAll("Showing Winner");

			calculateWinner();
			System.out.println("Winner is " + HandEvaluator.getRank(winner.getRank()) +"\n"+ winner.toString());

			for(int c = 0; c < playerCount; c++){
				//System.out.println(playerList.get(c).toString());
				String confirm = (String) in[c].readObject();
				if(c == indexOfWinner){
					out[c].writeObject(1);
					out[c].writeObject("You win with " + HandEvaluator.getRank(winner.getRank()));
					out[c].writeObject(pot);
				}
				else {
					out[c].writeObject(0);
					out[c].writeObject("You lost, Winner was Player " + (c+1) + " with " + HandEvaluator.getRank(winner.getRank()));
				}
			}

			/*for(int c = 0; c < playerCount ; c++){
				System.out.println("\n" + playerList.get(c).toString());
			}*/
			
			System.out.println(cardDeck.deck[0].toString() + "\n"+
					cardDeck.deck[1].toString() + "\n"+
					cardDeck.deck[2].toString() + "\n"+
					cardDeck.deck[3].toString() + "\n"+
					cardDeck.deck[4].toString() + "\n"+
					cardDeck.deck[5].toString() + "\n"+
					cardDeck.deck[6].toString() + "\n");

			System.out.println("\n-----------END OF GAME-----------\n");
			Thread.sleep(9000);
			System.out.println("\n-----------NEW GAME-----------\n");
		}




		//All cards have been sent
		//take in bets
		//If 1 person left then game is over


		//Game is over
		//Check who wins
		//Send who has won and with what to everyone
		//Increment the values on whoever has won


		//shuffle deck
		//Go back to start
		//Check if someone has no more chips left 
		//	if so tell them and then kick them out of the game?


	}

	private void createNewDeck() {
		// TODO Auto-generated method stub
		cardDeck = new DeckOfCards();
		cardDeck.setupPack();
		cardDeck.shuffle();
		pot = 0;
	}

	private void calculateWinner() {
		// TODO Auto-generated method stub
		winner = new Player();
		Player temp = new Player();
		winner = playerList.get((stillInHand.indexOf(true)));
		for(int i = 0; i < playerCount; i++){
			if(stillInHand.get(i)){
				temp = playerList.get(i);
				temp.calculateRank();
				if(HandEvaluator.compareTo(temp.getRank(),winner.getRank()) == 1){
					winner = playerList.get(i);
					indexOfWinner = i;
				}
			}
			
		}
		
		if(countOf(stillInHand) == 1){
			winner = playerList.get(stillInHand.indexOf(true));
			indexOfWinner = stillInHand.indexOf(true);
		}
	}

	private int countOf(ArrayList<Boolean> stillInHand2) {
		// TODO Auto-generated method stub
		int count =0;
		for(int i = 0; i < stillInHand2.size(); i++){
			if(stillInHand2.get(i))
				count++;
		}
		return count;
	}

	private void sendFlopCards() throws IOException {
		// TODO Auto-generated method stub
		//sending flop cards
		for(int c = 0; c < playerCount; c++){
			System.out.println("\nSending Flop to " + clientArray[c].getInetAddress());
			out[c].writeObject(cardDeck.deck[(playerCount*2)]);
			System.out.println("Player " + (c+1) + " Sending Card " + cardDeck.deck[(playerCount*2)].toString());
			out[c].writeObject(cardDeck.deck[(playerCount*2)+1]);
			System.out.println("Player " + (c+1) + " Sending Card " + cardDeck.deck[(playerCount*2+1)].toString());
			out[c].writeObject(cardDeck.deck[(playerCount*2)+2]);
			System.out.println("Player " + (c+1) + " Sending Card " + cardDeck.deck[(playerCount*2+2)].toString());

			playerList.get(c).addFlop(cardDeck.deck[(playerCount*2)], cardDeck.deck[(playerCount*2)+1], cardDeck.deck[(playerCount*2)+2]);
		}
	}

	private void initilizeVariables() {
		// TODO Auto-generated method stub
		playerList = new ArrayList<Player>();
		playerCount = 0;
		
		//cardDeck.showDeck();
		smallBlind = 100;
		bigBlind = smallBlind*2;
		stillInHand = new ArrayList<Boolean>();
		hasBet = new ArrayList<Boolean>();
		foldGroup = new ArrayList<Boolean>();
		out = new ObjectOutputStream[6];
		in = new ObjectInputStream[6];
		playersLeft=0;
		connecting = true;
		minStake = bigBlind;
		playerBet = new int[6];
		
	}

	private void takeInPlayers() throws IOException {
		// TODO Auto-generated method stub
		while(connecting){
			client= socket.accept();
			clientArray[playerCount] = client;
			System.out.println("Connected to client : " + clientArray[playerCount].getInetAddress());				
			in[playerCount] =new ObjectInputStream(clientArray[playerCount].getInputStream());
			out[playerCount] =new ObjectOutputStream(clientArray[playerCount].getOutputStream());	

			String name = "Player " + playerCount;
			String addy = "" + clientArray[playerCount].getInetAddress().toString();
			playerList.add(new Player(name,addy,10000)); 

			stillInHand.add(true);
			hasBet.add(false);
			foldGroup.add(false);
			playersLeft++;
			playerCount++;



			if(playerCount == numOfPlayers)
				connecting = false;
		}
	}

	private void sendHandCardsToPlayers() throws ClassNotFoundException, IOException {
		// TODO Auto-generated method stub
		for(int a = 0; a < playerCount; a++){
			if(firstHand){
				String wait = (String) in[a].readObject();
				out[a].writeObject("Take Hands");
			}
			stillInHand.set(a, true);
			hasBet.set(a,false);
			foldGroup.set(a,false);
			out[a].writeInt(smallBlind);
			out[a].writeInt(bigBlind);		
			out[a].writeInt(a+1);

			//Card1
			out[a].writeObject(cardDeck.deck[a]);
			playerList.get(a).resetCards();
			playerList.get(a).addCard(cardDeck.deck[a]);
			System.out.println("\nSending " + cardDeck.deck[a].toString() + " to " + clientArray[a].getInetAddress());

			//Card2
			out[a].writeObject(cardDeck.deck[a+playerCount]);
			playerList.get(a).addCard(cardDeck.deck[a+playerCount]);
			System.out.println("Sending " + cardDeck.deck[a + playerCount].toString() + " to " + clientArray[a].getInetAddress());


			System.out.println("waiting");
			String confirm  = (String) in[a].readObject();

			System.out.println("confirmed");
			System.out.println(confirm);

		}

		firstHand = false;
	}

	private void writeToAll(String string) throws IOException, ClassNotFoundException {
		// TODO Auto-generated method stub
		for(int c = 0; c < playerCount; c++){
			String confirm = (String) in[c].readObject();
			out[c].writeObject(string);
			//System.out.println(confirm);
		}
	}

	private void betTime() throws IOException, ClassNotFoundException {
		// TODO Auto-generated method stub
		for(int c = 0; c < playerCount; c++){
			String confirm = (String) in[c].readObject();
			out[c].writeObject("Bet time");
			//System.out.println(confirm);
		}

		minStake = bigBlind;

		//Start at first player who is still in game
		indexOfLastRaise = stillInHand.indexOf(true);
		betUntil = (indexOfLastRaise + (playerCount -1)) % playerCount;

		int keepTrack = 0;
		reset(hasBet);
		while(!checkForAll(hasBet) && moreThanOne(stillInHand)){
			//check is player is still in game
			if(stillInHand.get(keepTrack) == true){

				String confirm = (String) in[keepTrack].readObject();
				//System.out.println("\nConfirmation : " + confirm);
				out[keepTrack].writeObject(minStake);
				//System.out.println("Sending how much to bet: " + minStake + " to Player " + keepTrack);

				int bet = (int) in[keepTrack].readObject();

				//Player has raised
				if(bet > minStake){
					System.out.println("Player " + keepTrack + " Raised");
					minStake = bet;
					pot += bet;
					indexOfLastRaise = keepTrack;
					betUntil = (indexOfLastRaise + (playerCount -1)) % playerCount;
					//System.out.println("Keep betting until Player " + betUntil);

					//Reset arrayList that keeps track of who has to bet
					reset(hasBet);
				}
				else if ( bet == minStake) //Player has called
				{
					System.out.println("Player " + keepTrack + " Called");
					pot += bet;
				}
				else if (bet == -1) //Player has folded
				{
					System.out.println("Player " + keepTrack + " Folded");
					stillInHand.set(keepTrack, false);
				}//Player is all in

				hasBet.set(keepTrack, true);

				//Keep this player looking for a new bet
				out[keepTrack].writeObject("Bet time");
				confirm = (String) in[keepTrack].readObject();
				//System.out.println("Confirm 2 " + confirm);
			}
			//loop back to next player
			keepTrack = (keepTrack+1)%playerCount;

		}
		//Basically telling each user they are no longer betting and to send in when they are ready for next cards
		for(int c = 0; c < playerCount; c++){
			String confirm = (String) in[c].readObject();
			out[c].writeObject(-1);
			//System.out.println(confirm);
		}
	}

	private boolean moreThanOne(ArrayList<Boolean> hasBet2) {
		// TODO Auto-generated method stub
		int count = 0;
		for(int i = 0; i < playerCount; i++){
			if(stillInHand.get(i))
				count++;
		}
		if(count > 1)
			return true;
		else return false;
	}

	private void reset(ArrayList<Boolean> list) {
		// TODO Auto-generated method stub
		for(int i = 0; i < list.size(); i++){
			list.set(i, false);
		}
	}

	public boolean checkForAll(ArrayList<Boolean> list){

		for(int i = 0; i < list.size(); i++){
			if(list.get(i) == false)
				return false;
		}

		return true;
	}

	public static void main(String[] args){

		try {

			gameServer s=new gameServer(7878); 

			try {
				s.start();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} catch (IOException e) {

			// TODO Auto-generated catch block

			e.printStackTrace();

		}
	}	 

}