package com.example.androidclient;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OptionalDataException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import software.patterns.assignment.*;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.graphics.Color;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements OnSeekBarChangeListener{

	TextView t, card1,card2,chipsTextView, flop1Text ,flop2Text ,flop3Text, infoText,riverText,runText,pickBet;
	ImageView cardImage1,cardImage2,cardBack1,cardBack2, flopImage1, flopImage2, flopImage3,riverImage,runImage, flopback1,flopback2,flopback3,riverBack,runBack;
	Card a,b, flop1,flop2,flop3, river, run;
	Socket socket;
	int chipCount, smallBlind,bigBlind,minStake,PlayerNo,PlayerToGo;
	//Double minStake;
	ObjectOutputStream oos;
	ObjectInputStream ois;
	SeekBar bar;
	String gameStatus,Player,whosGo, address;
	Boolean inGame;
	Button startButton;
	@Override

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		//address = "192.168.1.16";
		inGame = false;
		address = "136.206.18.44";
		gameStatus= "No Server";
		bar = (SeekBar)findViewById(R.id.seekBar1); // make seekbar object
		bar.setOnSeekBarChangeListener(this);

		t = (TextView) findViewById(R.id.text);
		t.setMovementMethod(new ScrollingMovementMethod());
		chipCount = 10000;
		chipsTextView = (TextView) findViewById(R.id.chips);
		chipsTextView.setText("Chips: " + chipCount);
		cardBack1 = (ImageView) findViewById(R.id.imageView1);
		cardBack2 = (ImageView) findViewById(R.id.imageView2);
		card1 = (TextView) findViewById(R.id.card1);
		card1.setVisibility(View.INVISIBLE);
		card2 = (TextView) findViewById(R.id.card2);
		card2.setVisibility(View.INVISIBLE);
		cardImage1 = (ImageView) findViewById(R.id.imageView8);
		cardImage1.setVisibility(View.INVISIBLE);
		cardImage2 = (ImageView) findViewById(R.id.imageView9);
		cardImage2.setVisibility(View.INVISIBLE);

		infoText = (TextView) findViewById(R.id.textView1);

		flopImage1 = (ImageView) findViewById(R.id.flopimage1);
		flopImage2 = (ImageView) findViewById(R.id.flopimage2);
		flopImage3= (ImageView) findViewById(R.id.flopimage3);
		riverImage = (ImageView) findViewById(R.id.riverCardImage);
		runImage = (ImageView) findViewById(R.id.runCardImage);
		
		flopImage1.setVisibility(View.INVISIBLE);
		flopImage2.setVisibility(View.INVISIBLE);
		flopImage3.setVisibility(View.INVISIBLE);
		riverImage.setVisibility(View.INVISIBLE);
		runImage.setVisibility(View.INVISIBLE);

		flop1Text = (TextView) findViewById(R.id.flop1);
		flop2Text = (TextView) findViewById(R.id.flop2);
		flop3Text = (TextView) findViewById(R.id.flop3);
		riverText = (TextView) findViewById(R.id.riverCardText);
		runText = (TextView) findViewById(R.id.runCardText);
		
		flop1Text.setVisibility(View.INVISIBLE);
		flop2Text.setVisibility(View.INVISIBLE);
		flop3Text.setVisibility(View.INVISIBLE);
		riverText.setVisibility(View.INVISIBLE);
		runText.setVisibility(View.INVISIBLE);

		flopback1 = (ImageView) findViewById(R.id.imageView3);
		flopback2 = (ImageView) findViewById(R.id.imageView4);
		flopback3 = (ImageView) findViewById(R.id.imageView5);
		riverBack = (ImageView) findViewById(R.id.imageView6);
		runBack = (ImageView) findViewById(R.id.imageView7);

		minStake = 1;

		a = new Card();
		b = new Card();
		flop1 = new Card(0,"Clubs");
		flop2 = new Card(0,"Clubs");
		flop3 = new Card(0,"Clubs");
		river = new Card(0, "Clubs");
		run = new Card(0, "Clubs");
		
		pickBet = (TextView) findViewById(R.id.textView2);
		pickBet.setText(""+ (chipCount));
		
		startButton =(Button) findViewById(R.id.send);
		//t.append("\nStart");
		startButton.setOnClickListener(new OnClickListener() {		 
			@Override
			public void onClick(View v) {
				if(!inGame){
					new waitForServer().execute("");
				}
				else startButton.setText("Connected");
			} 
		});

		Button raiseBet = (Button) findViewById(R.id.button2);
		Button lowerBet = (Button) findViewById(R.id.button4);

		raiseBet.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				bar.setProgress(bar.getProgress()+5);
			}
		});
		lowerBet.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				bar.setProgress(bar.getProgress()-5);
			}
		});

		Button betButton = (Button) findViewById(R.id.betbutton);
		betButton.setOnClickListener(new OnClickListener(){
			public void onClick(View arg0) {
				try {
					if(((bar.getProgress()*chipCount)/100) >= minStake){
						oos.writeObject((bar.getProgress()*chipCount)/100);
						chipCount = chipCount - ((bar.getProgress()*chipCount)/100);
						chipsTextView.setText("Chips: " + chipCount);
					}else Toast.makeText(getApplicationContext(), "Bet must be larger than minimum stake:" + minStake, Toast.LENGTH_LONG).show();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});

		Button callButton = (Button) findViewById(R.id.callButton);
		callButton.setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
				try {
					//if(((bar.getProgress()*chipCount)/100) == minStake){
						oos.writeObject(minStake);
						chipCount = chipCount - minStake;
						chipsTextView.setText("Chips: " + chipCount);
					//}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});

		Button foldButton = (Button) findViewById(R.id.foldButton);
		foldButton.setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
				try {
					oos.writeObject(-1);
				} catch (IOException e) {
					e.printStackTrace();
				}				
			}

		});
	}

	private class retrieveBet extends AsyncTask<String,Void,String>{

		protected String doInBackground(String... params) {
			try {
				oos.writeObject("Send Min Stake");
				int temp = (Integer) ois.readObject();
				
				if(temp == -1)
					gameStatus = "After Bet";
				else minStake = temp;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
			return "Executed";
		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			//Toast.makeText(getBaseContext(), (minStake + " to call" ), Toast.LENGTH_LONG).show();
			//infoText.setText("Current chips to call is :" + minStake);
			t.append("Current chips to call is :" + minStake +"\n");
			
			if(gameStatus.compareTo("After Bet") != 0){
				gameStatus = "Sent Bet waiting for server";
				Toast.makeText(getBaseContext(), ("Time to bet\n" + minStake + " to call" ), Toast.LENGTH_SHORT).show();
				
			}
			new waitForServer().execute();
		}
	}

	private class retrieveFlop extends AsyncTask<String, Void, String>{

		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			try {
				System.out.println("IM HERE");
				flop1 = (Card) ois.readObject();
				flop2 = (Card) ois.readObject();
				flop3 = (Card) ois.readObject();
				System.out.println(flop1.toString());
				System.out.println(flop2.toString());
				System.out.println(flop3.toString());
				//ois.close();
			} catch (OptionalDataException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}			
			return "Executed";
		}

		protected void onPostExecute(String result) {
			TextView txt = (TextView) findViewById(R.id.text);
			System.out.println(flop1.Number + " of " + flop1.suite);
			t.append("\n"+flop1.toString() + flop2.toString() + flop3.toString());

			flopback1.setImageResource(R.drawable.card_small);
			flopback2.setImageResource(R.drawable.card_small);
			flopback3.setImageResource(R.drawable.card_small);


			flopImage1.setVisibility(View.VISIBLE);
			flopImage2.setVisibility(View.VISIBLE);
			flopImage3.setVisibility(View.VISIBLE);

			flop1Text.setVisibility(View.VISIBLE);
			flop2Text.setVisibility(View.VISIBLE);
			flop3Text.setVisibility(View.VISIBLE);

			flop1Text.setText(flop1.getCardValue() + "");
			flop2Text.setText(flop2.getCardValue() + "");
			flop3Text.setText(flop3.getCardValue() + "");

			assignSuitImage(flopImage1, flop1, "Small");
			assignSuitImage(flopImage2, flop2, "Small");
			assignSuitImage(flopImage3, flop3, "Small");

			checkCardColor(flop1Text, flop1);
			checkCardColor(flop2Text, flop2);
			checkCardColor(flop3Text, flop3);
			gameStatus = "Ready For Betting";
			
			new waitForServer().execute();
		}


	}

	private class retrieveCard extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... params) {
			try {

				smallBlind = ois.readInt();
				bigBlind = ois.readInt();
				PlayerNo = ois.readInt();

				a = (Card) ois.readObject();
				b = (Card) ois.readObject();
				// minStake = ois.readInt();

			} catch (UnknownHostException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return "Executed";
		}      

		@Override
		protected void onPostExecute(String result) {
			TextView txt = (TextView) findViewById(R.id.text);
			System.out.println(a.Number + " of " + a.suite);
			card1.setText(a.getCardValue() + "");
			card1.setVisibility(View.VISIBLE);
			cardBack1.setImageResource(R.drawable.card);

			card2.setText(b.getCardValue() + "");
			card2.setVisibility(View.VISIBLE);
			cardBack2.setImageResource(R.drawable.card);
			checkCardColor(card1,a);
			checkCardColor(card2,b);

			//assignSuiteImage();
			assignSuitImage(cardImage1,a, "Large");
			assignSuitImage(cardImage2,b, "Large");

			//t.append("\n"+ " " + " " + bigBlind + " " + smallBlind);// txt.setText(result);
			bar.setProgress((100*bigBlind)/chipCount);
			gameStatus = "Ready For Betting";

			if(!(a.equals(null)) && !(b.equals(null))){
				try {
					oos.writeObject("Not Null");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			//Toast.makeText(getBaseContext(), (minStake + "?" + (bar.getProgress()*chipCount)/100) + "!" + PlayerNo, Toast.LENGTH_LONG).show();
			new waitForServer().execute();
			//new retrieveFlop().execute("");
		}

		@Override
		protected void onPreExecute() {
		}

		@Override
		protected void onProgressUpdate(Void... values) {
		}
	}

	private class waitForServer extends AsyncTask<String,Void, String>{

		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			try {
				if(gameStatus.compareTo("No Server") == 0){

					try {
						inGame = true;
						InetAddress ia = InetAddress.getByName("192.168.1.21");
						socket = new Socket(ia, 53535);
						// Send a message to the client application
						oos = new ObjectOutputStream(socket.getOutputStream());

						// Read and display the response message sent by server application
						ois= new ObjectInputStream(socket.getInputStream());

						oos.writeObject("Setup");
						gameStatus = (String) ois.readObject();
					} catch (UnknownHostException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}else if(gameStatus.compareTo("Ready For Cards") == 0){
					oos.writeObject("Im ready for Cards Baby");
					gameStatus = (String) ois.readObject();
				}else if(gameStatus.compareTo("Ready For Betting") == 0){
					oos.writeObject("Start Betting");
					gameStatus = (String) ois.readObject();
				}else if(gameStatus.compareTo("Sent Bet waiting for server") == 0){
					gameStatus = (String) ois.readObject();
					oos.writeObject("What Do I Do Now?");
				}

			} catch (OptionalDataException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub

			if(gameStatus.compareTo("Take Hands") == 0){

				resetCardUI();
				new retrieveCard().execute();
			}else if(gameStatus.compareTo("Sending Flop") == 0){
				new retrieveFlop().execute();
			}else if(gameStatus.compareTo("Sending River") == 0){
				new retrieveRiver().execute();
			}else if(gameStatus.compareTo("Sending Run") == 0){
				new retrieveRun().execute();
			}else if(gameStatus.compareTo("Showing Winner") == 0){
				new showWinner().execute();
			}else if(gameStatus.compareTo("Bet time") == 0){
				//Toast.makeText(getBaseContext(), gameStatus, Toast.LENGTH_LONG).show();
				new retrieveBet().execute();
				//Take in stake and send over bet
				//gameStatus = "No Server";
			}else if(gameStatus.compareTo("Finished Betting") == 0){
				//move onto nextpiece of game ie taking in new cards or retrieving winner
			}else if(gameStatus.compareTo("After Bet") == 0){
				//Toast.makeText(getBaseContext(), gameStatus, Toast.LENGTH_LONG).show();
				gameStatus = "Ready For Cards";
				new waitForServer().execute();
			}
			super.onPostExecute(result);
		}

	}

	private class retrieveRiver extends AsyncTask<String, Void, String>{

		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			try {
				river = (Card) ois.readObject();
				System.out.println(flop3.toString());
				//ois.close();
			} catch (OptionalDataException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}			
			return "Executed";
		}

		protected void onPostExecute(String result) {
			TextView txt = (TextView) findViewById(R.id.text);
			System.out.println(flop1.Number + " of11 " + flop1.suite);
			t.append("\n"+river.toString());

			riverBack.setImageResource(R.drawable.card_small);

			riverImage.setVisibility(View.VISIBLE);
	
			riverText.setVisibility(View.VISIBLE);
			
			riverText.setText(river.getCardValue() + "");
			
			assignSuitImage(riverImage, river, "Small");
			
			checkCardColor(riverText, river);
			gameStatus = "Ready For Betting";
			
			new waitForServer().execute();
		}


	}

	private class retrieveRun extends AsyncTask<String, Void, String>{

		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			try {
				run = (Card) ois.readObject();
				System.out.println(flop3.toString());
				//ois.close();
			} catch (OptionalDataException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}			
			return "Executed";
		}

		protected void onPostExecute(String result) {
			TextView txt = (TextView) findViewById(R.id.text);
			System.out.println(flop1.Number + " of11 " + flop1.suite);
			t.append("\n"+run.toString());

			runBack.setImageResource(R.drawable.card_small);

			runImage.setVisibility(View.VISIBLE);
	
			runText.setVisibility(View.VISIBLE);
			
			runText.setText(run.getCardValue() + "");
			
			assignSuitImage(runImage, run, "Small");
			
			checkCardColor(runText, run);
			gameStatus = "Ready For Betting";

			new waitForServer().execute();
		}


	}
	
	private class showWinner extends AsyncTask<String,Void,String>{
		String winner;
		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			try {
				oos.writeObject("Ready for Winner");
				winner = (String) ois.readObject();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			Toast.makeText(getBaseContext(), winner, Toast.LENGTH_SHORT).show();
			inGame = false;
			gameStatus = "No Server";
			new waitForServer().execute();
			super.onPostExecute(result);
		}
		
	}
	
	@Override
	public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
		// TODO Auto-generated method stub
		//t.append("Textasd\n");
		pickBet.setText(""+ (progress * chipCount)/100);

	}

	public void resetCardUI() {
		// TODO Auto-generated method stub
		card1.setVisibility(View.INVISIBLE);
		card2.setVisibility(View.INVISIBLE);
		cardImage1.setVisibility(View.INVISIBLE);
		cardImage2.setVisibility(View.INVISIBLE);
		flopImage1.setVisibility(View.INVISIBLE);
		flopImage2.setVisibility(View.INVISIBLE);
		flopImage3.setVisibility(View.INVISIBLE);
		riverImage.setVisibility(View.INVISIBLE);
		runImage.setVisibility(View.INVISIBLE);
		flop1Text.setVisibility(View.INVISIBLE);
		flop2Text.setVisibility(View.INVISIBLE);
		flop3Text.setVisibility(View.INVISIBLE);
		riverText.setVisibility(View.INVISIBLE);
		runText.setVisibility(View.INVISIBLE);

		
		riverBack.setImageResource(R.drawable.cardbacksmall);
		runBack.setImageResource(R.drawable.cardbacksmall);
		flopback1.setImageResource(R.drawable.cardbacksmall);
		flopback2.setImageResource(R.drawable.cardbacksmall);
		flopback3.setImageResource(R.drawable.cardbacksmall);

		
		a = new Card();
		b = new Card();
		flop1 = new Card(0,"Clubs");
		flop2 = new Card(0,"Clubs");
		flop3 = new Card(0,"Clubs");
		river = new Card(0, "Clubs");
		run = new Card(0, "Clubs");
	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub

	}

	private void checkCardColor(TextView text, Card card) {
		// TODO Auto-generated method stub
		if(card.suite.compareTo("Hearts") == 0 || card.suite.compareTo("Diamonds") == 0)
			text.setTextColor(Color.RED);
		else text.setTextColor(Color.BLACK);
	}

	private void assignSuitImage(ImageView image, Card card, String string) {
		// TODO Auto-generated method stub
		image.setVisibility(View.VISIBLE);
		if(string.compareTo("Large") == 0){
			if(card.suite.compareTo("Diamonds") == 0)
				image.setImageResource(R.drawable.diamond);
			if(card.suite.compareTo("Hearts") == 0)
				image.setImageResource(R.drawable.heart);
			if(card.suite.compareTo("Clubs") == 0)
				image.setImageResource(R.drawable.club);
			if(card.suite.compareTo("Spades") == 0)
				image.setImageResource(R.drawable.spade);
		} else if(string.compareTo("Small") == 0){
			if(card.suite.compareTo("Diamonds") == 0)
				image.setImageResource(R.drawable.diamondsmall);
			if(card.suite.compareTo("Hearts") == 0)
				image.setImageResource(R.drawable.heartsmall);
			if(card.suite.compareTo("Clubs") == 0)
				image.setImageResource(R.drawable.clubsmall);
			if(card.suite.compareTo("Spades") == 0)
				image.setImageResource(R.drawable.spadesmall);
		}
	}
}
