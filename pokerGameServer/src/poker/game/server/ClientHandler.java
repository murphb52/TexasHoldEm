package poker.game.server;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.net.Socket;

import software.patterns.assignment.Card;
import software.patterns.assignment.DeckOfCards;

public class ClientHandler implements Runnable {
	Socket socket;
	PrintStream out;
	DeckOfCards d;
	int ii;

	ClientHandler(Socket s) {
		socket = s;
	}

	public ClientHandler(Socket client, DeckOfCards cardDeck, int i) throws IOException {
		socket = client;
		d = cardDeck;
		ii = i;
			
	}

	@Override
	public void run() {

		// TODO Auto-generated constructor stub
				//ObjectInputStream in=new ObjectInputStream(client.getInputStream());				 
				ObjectOutputStream out;
				try {
					out = new ObjectOutputStream(socket.getOutputStream());
						 
					//String input=(String) in.readObject();				 
					//System.out.println("Client says : ");
					//input.showCard();
					
					Card abc = new Card(1,"Spades");
	
					out.writeObject(d.deck[ii]);
					ii++;
					out.flush();				 
					//in.close();				 
					out.close();				 
					socket.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}	
	}

}