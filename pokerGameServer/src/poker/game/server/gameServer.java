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
	Socket client;
	Boolean connecting = true;
	ObjectInputStream []in;
	ObjectOutputStream []out;
	Socket[] clientArray = new Socket[6];
	int playerCount, smallBlind, bigBlind, currentBet,minStake,pot,playersLeft,indexOfLastRaise,betUntil;
	//double minStake;
	int[] playerBet;
	ArrayList <Player> playerList;
	ArrayList <Boolean> PlayerList,hasBet,foldGroup;

	//Player
	//Players
	
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

	public void start() throws IOException, ClassNotFoundException{
		while(true){
		int i = 0;

		playerList = new ArrayList<Player>();
		playerCount = 0;
		DeckOfCards cardDeck = new DeckOfCards();
		cardDeck.setupPack();
		cardDeck.shuffle();
		//cardDeck.showDeck();
		smallBlind = 100;
		bigBlind = smallBlind*2;
		PlayerList = new ArrayList<Boolean>();
		hasBet = new ArrayList<Boolean>();
		foldGroup = new ArrayList<Boolean>();
		out = new ObjectOutputStream[6];
		in = new ObjectInputStream[6];
		playersLeft=0;
		connecting = true;
		while(connecting){
			client= socket.accept();
			clientArray[i] = client;
			System.out.println("Connected to client : " + clientArray[i].getInetAddress());				
			in[i] =new ObjectInputStream(clientArray[i].getInputStream());
			out[i] =new ObjectOutputStream(clientArray[i].getOutputStream());	

			String name = "Player " + i;
			String addy = "" + clientArray[i].getInetAddress().toString();
			playerList.add(new Player(name,addy,10000)); 
			
			i++;
			PlayerList.add(true);
			hasBet.add(false);
			foldGroup.add(false);
			playersLeft++;
			playerCount++;
			
			
			
			if(i == 1)
				connecting = false;
		}
		
		

		//String input = (String )in[i].readObject();

		minStake = bigBlind;
		playerBet = new int[6];
		System.out.println("Finished taking in Players\nNow going to send cards");
		for(int a = 0; a < i; a++){

			//String input=(String) in.readObject();				 
			//System.out.println("Client says : ");
			//input.showCard()
			String wait = (String) in[a].readObject();
			out[a].writeObject("Take Hands");
			out[a].writeInt(smallBlind);
			out[a].writeInt(bigBlind);
			//out[a].writeObject("Player " + (a+1));
			out[a].writeInt(a+1);
			out[a].writeObject(cardDeck.deck[a]);
			playerList.get(a).setCardOne(cardDeck.deck[a]);
			System.out.println("\nSending " + cardDeck.deck[a].toString() + " to " + clientArray[a].getInetAddress());

			//out[a] =new ObjectOutputStream(clientArray[a].getOutputStream());				 

			out[a].writeObject(cardDeck.deck[a+i]);
			playerList.get(a).setCardTwo(cardDeck.deck[a+i]);
			System.out.println("Sending " + cardDeck.deck[a + i].toString() + " to " + clientArray[a].getInetAddress());
			String confirm  = (String) in[a].readObject();
			System.out.println(confirm);
			//out[a].writeInt(minStake);
		}

		System.out.println("Finished Sending cards\nGoing to take bets now");

		for(int c = 0; c < playerCount; c++){
			String confirm = (String) in[c].readObject();
			out[c].writeObject("Bet time");
			//System.out.println(confirm);
		}
		
		//Start at first player who is still in game
		indexOfLastRaise = PlayerList.indexOf(true);
		betUntil = (indexOfLastRaise + (playerCount -1)) % playerCount;
		
		int keepTrack = 0;
		
		while(!checkForAll(hasBet)){
			//check is player is still in game
			if(PlayerList.get(keepTrack) == true){
				
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
					PlayerList.set(keepTrack, false);
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
		
		
		//Check if everyone has folded
		//If so end game and show winner

		//System.out.println("\nHasBet :" + hasBet.toString());
		
		System.out.println("\nPot is " + pot);
		//System.out.println(PlayerList.toString());
		//System.out.println("Last person to bet was " + indexOfLastRaise);
		
		

		//System.out.println(PlayerList.toString());
		System.out.println("Finished taking bets going to send Flop");

		//Makes sure players are ready to take in flop cards
		for(int c = 0; c < playerCount; c++){
			String confirm = (String) in[c].readObject();
			out[c].writeObject("Sending Flop");
			//System.out.println(confirm);
		}
		
		//sending flop cards
		for(int c = 0; c < i; c++){
			System.out.println("\nSending Flop to " + clientArray[c].getInetAddress());
			out[c].writeObject(cardDeck.deck[(playerCount*2)]);
			System.out.println("Player " + (c+1) + " Sending Card " + cardDeck.deck[(playerCount*2)].toString());
			out[c].writeObject(cardDeck.deck[(playerCount*2)+1]);
			System.out.println("Player " + (c+1) + " Sending Card " + cardDeck.deck[(playerCount*2+1)].toString());
			out[c].writeObject(cardDeck.deck[(playerCount*2)+2]);
			System.out.println("Player " + (c+1) + " Sending Card " + cardDeck.deck[(playerCount*2+2)].toString());

		}
		
		for(int c = 0; c < playerCount; c++){
			String confirm = (String) in[c].readObject();
			out[c].writeObject("Bet time");
			//System.out.println(confirm);
		}
		
		//Start at first player who is still in game
		indexOfLastRaise = PlayerList.indexOf(true);
		betUntil = (indexOfLastRaise + (playerCount -1)) % playerCount;
		
		keepTrack = 0;
		reset(hasBet);
		minStake = bigBlind;
		while(!checkForAll(hasBet)){
			//check is player is still in game
			if(PlayerList.get(keepTrack) == true){
				
				String confirm = (String) in[keepTrack].readObject();
				////System.out.println("\nConfirmation : " + confirm);
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
					PlayerList.set(keepTrack, false);
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
		
		//Sent flop
		//Take in bets
		//If 1 person left then game is over
		//Else send River		
		
		
		for(int c = 0; c < playerCount; c++){
			String confirm = (String) in[c].readObject();
			out[c].writeObject("Sending River");
			//System.out.println(confirm);
		}
		
		//Send River card
		for(int c = 0; c < i; c++){
			System.out.println("\nSending River to Player " + c);
			out[c].writeObject(cardDeck.deck[(playerCount*2)+3]);
		}
		
		for(int c = 0; c < playerCount; c++){
			String confirm = (String) in[c].readObject();
			out[c].writeObject("Bet time");
			//System.out.println(confirm);
		}
		
		//Start at first player who is still in game
		indexOfLastRaise = PlayerList.indexOf(true);
		betUntil = (indexOfLastRaise + (playerCount -1)) % playerCount;
		
		keepTrack = 0;
		reset(hasBet);
		minStake = bigBlind;
		while(!checkForAll(hasBet)){
			//check is player is still in game
			if(PlayerList.get(keepTrack) == true){
				
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
					PlayerList.set(keepTrack, false);
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
		
		//River has been sent
		//take in Bets
		//If 1 person left then game is over
		
		for(int c = 0; c < playerCount; c++){
			String confirm = (String) in[c].readObject();
			out[c].writeObject("Sending Run");
			//System.out.println(confirm);
		}
		
		//Send Run card
		for(int c = 0; c < i; c++){
			System.out.println("\nSending Run to Player " + c);
			
			out[c].writeObject(cardDeck.deck[(playerCount*2)+4]);
		}
		for(int c = 0; c < playerCount; c++){
			String confirm = (String) in[c].readObject();
			out[c].writeObject("Bet time");
			//System.out.println(confirm);
		}
		
		//Start at first player who is still in game
		indexOfLastRaise = PlayerList.indexOf(true);
		betUntil = (indexOfLastRaise + (playerCount -1)) % playerCount;
		
		keepTrack = 0;
		reset(hasBet);
		minStake = bigBlind;
		while(!checkForAll(hasBet)){
			//check is player is still in game
			if(PlayerList.get(keepTrack) == true){
				
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
					PlayerList.set(keepTrack, false);
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
		
		
		for(int c = 0; c < playerCount; c++){
			String confirm = (String) in[c].readObject();
			out[c].writeObject("Showing Winner");
			//System.out.println(confirm);
		}
		
		for(int c = 0; c < playerCount; c++){
			String confirm = (String) in[c].readObject();
			out[c].writeObject("Winner is ");
			
			System.out.println("\n" + playerList.get(c).toString());
		}
		
		
		
		System.out.println("\n-----------END OF GAME-----------\n");
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
			}

		} catch (IOException e) {

			// TODO Auto-generated catch block

			e.printStackTrace();

		}

	}	 
}