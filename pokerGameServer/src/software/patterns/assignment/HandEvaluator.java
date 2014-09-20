package software.patterns.assignment;



public class HandEvaluator {
	
	public HandEvaluator(){

	}
	
	public static boolean isFlush(Card[] p1){
		if(p1[0].suite.compareTo(p1[1].suite) == 0 &&
				p1[1].suite.compareTo(p1[2].suite) == 0 &&
				p1[2].suite.compareTo(p1[3].suite) == 0 &&
				p1[3].suite.compareTo(p1[4].suite) == 0)
			return true;
		else return false;
	}
	
	public static int[] evaluate(Card[] p1){
		int[] p1values = new int[6]; //[0] for value and rest for cards
		
		int handValue = 0;
		int firstGroup = 0;
		int secondGroup;
		boolean isStraight = false;
		boolean isFlush = isFlush(p1);
		boolean pair= false;
		boolean three = false;
		boolean ace = false;
		int highCard = 0;
		
		int pairValue = 0;
		int pairValue2;
		//Check for flush
		
		
		int[] orderedNums = new int[5];
		for(int i = 0; i < 5; i++){
			orderedNums[i] = p1[i].Number;
		}
		
		//Check for straight
		
		//Order numbers
		int temp;
		for(int i = 0; i < 5; i++){
			for(int j = 0; j < 5; j++ ){
				if(orderedNums[j] > orderedNums[i]){
					temp = orderedNums[i];
					orderedNums[i] = orderedNums[j];
					orderedNums[j] = temp;
				}
					 
			}
		}
		
		
		
		//Check for Straight
		if(orderedNums[0] == (orderedNums[1]-1) &&
				orderedNums[1] == (orderedNums[2]-1) &&
				orderedNums[2] == (orderedNums[3]-1) &&
				orderedNums[3] == (orderedNums[4]-1)){
			isStraight = true;
		}
		//Special Case for ACE
		if(orderedNums[0] == 1 &&
				orderedNums[1] == 10 &&
				orderedNums[2] == 11 &&
				orderedNums[3] == 12 &&
				orderedNums[4] == 13){
			isStraight = true;
		}
		
		//set all counts of cards to 0
		int[] countOfCards = new int[14];
		for(int i = 0; i < 13; i++){
			countOfCards[i] = 0;
			
		}
		
		p1values[0]=1;
		p1values[1]=orderedNums[4];
		
		//increment index when card is seen	
		//counting cards basically
		for(int i = 0; i < 5; i++){
			countOfCards[p1[i].Number]++;
			
			if(p1[i].Number > highCard){
				highCard = p1[i].Number;
				p1values[1] = highCard;
			}
			
			if(p1[i].Number == 1)
				ace = true;
		}
		
		
		
		if(ace){
			p1values[1] = 14;
		}
		
		//go back through counts to see how many of each card we have
		//check for counts of 2,3 and 4
		for(int i = 0; i < 14; i++){
			if(countOfCards[i] == 2){
				if(!pair){
					//Pair
					handValue = countOfCards[i];
					p1values[0] = 2;
					p1values[1] = i;
					pairValue = i;					;
					pair = true;
				}
				else if (pair){
					//Two Pair
					handValue = countOfCards[i];
					pairValue2 = i;
					p1values[0] = 3;
					if(i > pairValue){
						p1values[1] = i;
						p1values[2] = pairValue;
					}else{
						p1values[1] = pairValue;
						p1values[2] = i;
					}
				}
				
			}
			else if(countOfCards[i] == 3){
				//Three of a kind
				handValue = countOfCards[i];
				p1values[0] = 4;
				p1values[1] = i;
				three = true;
			}
			else if(countOfCards[i] == 4){
				//4 Of a kind
				p1values[0] = 8;
				p1values[1] = i;
			}
		}
		
		if(isStraight && isFlush){
			p1values[0] = 9;
		}else if(isFlush && !isStraight){
			//Flush
			p1values[0] = 6;
			p1values[1] = highCard;
		}else if(isStraight && !isFlush){
			p1values[0] = 5;
		}else if(pair && three){
			//FullHouse
			p1values[0] = 7;
			p1values[2] = pairValue;
		}
		
		return p1values;
	}

	public static int[] evaluate7(Card[] p1){	
		
		int[] e0 = evaluate(new Card[]{p1[0],p1[1],p1[2],p1[3],p1[4]});
		int[] e1 = evaluate(new Card[]{p1[0],p1[1],p1[2],p1[3],p1[5]});
		int[] e2 = evaluate(new Card[]{p1[0],p1[1],p1[2],p1[3],p1[6]});
		int[] e3 = evaluate(new Card[]{p1[0],p1[1],p1[2],p1[4],p1[5]});
		int[] e4 = evaluate(new Card[]{p1[0],p1[1],p1[2],p1[4],p1[6]});
		int[] e5 = evaluate(new Card[]{p1[0],p1[1],p1[2],p1[5],p1[6]});
		int[] e6 = evaluate(new Card[]{p1[0],p1[1],p1[3],p1[4],p1[5]});
		int[] e7 = evaluate(new Card[]{p1[0],p1[1],p1[3],p1[4],p1[6]});
		int[] e8 = evaluate(new Card[]{p1[0],p1[1],p1[3],p1[5],p1[6]});
		int[] e9 = evaluate(new Card[]{p1[0],p1[1],p1[4],p1[5],p1[6]});
		int[] e10 = evaluate(new Card[]{p1[0],p1[2],p1[3],p1[4],p1[5]});
		int[] e11 = evaluate(new Card[]{p1[0],p1[2],p1[4],p1[5],p1[6]});
		int[] e12 = evaluate(new Card[]{p1[0],p1[2],p1[3],p1[5],p1[6]});
		int[] e13 = evaluate(new Card[]{p1[0],p1[2],p1[3],p1[4],p1[6]});
		int[] e14 = evaluate(new Card[]{p1[1],p1[2],p1[3],p1[4],p1[5]});
		int[] e15 = evaluate(new Card[]{p1[1],p1[2],p1[3],p1[4],p1[6]});
		int[] e16 = evaluate(new Card[]{p1[1],p1[2],p1[3],p1[5],p1[6]});
		int[] e17 = evaluate(new Card[]{p1[1],p1[2],p1[4],p1[5],p1[6]});
		int[] e18 = evaluate(new Card[]{p1[1],p1[3],p1[4],p1[5],p1[6]});
		int[] e19 = evaluate(new Card[]{p1[2],p1[3],p1[4],p1[5],p1[6]});
		int[] e20 = evaluate(new Card[]{p1[0],p1[3],p1[4],p1[5],p1[6]});
		
		int[] winner = compareToReturnArray(e0,e1);
		winner = compareToReturnArray(winner,e2);
		winner = compareToReturnArray(winner,e3);
		winner = compareToReturnArray(winner,e4);
		winner = compareToReturnArray(winner,e5);
		winner = compareToReturnArray(winner,e6);
		winner = compareToReturnArray(winner,e7);
		winner = compareToReturnArray(winner,e8);
		winner = compareToReturnArray(winner,e9);
		winner = compareToReturnArray(winner,e10);
		winner = compareToReturnArray(winner,e11);
		winner = compareToReturnArray(winner,e12);
		winner = compareToReturnArray(winner,e13);
		winner = compareToReturnArray(winner,e14);
		winner = compareToReturnArray(winner,e15);
		winner = compareToReturnArray(winner,e16);
		winner = compareToReturnArray(winner,e17);
		winner = compareToReturnArray(winner,e18);
		winner = compareToReturnArray(winner,e19);
		winner = compareToReturnArray(winner,e20);

		return winner;
	}
	
	private static int[] compareToReturnArray(int[] e0, int[] e1) {
		// TODO Auto-generated method stub
		for (int x=0; x<6; x++)
        {
            if (e0[x]>e1[x])
                return e0;
            else if (e0[x]<e1[x])
                return e1;
        }
        return e0; //if hands are equal
	}

	public static void displayRank(int i[]){
		String[] ranks = {"High Card","Pair","2 Pair","3 of a kind","Straight","Flush","Full House","4 of a kind","Straight Flush"};
		String[] Numbers = {"Null" ,"Ace","Two","Three","Four","Five","Six","Seven","Eight","Nine","Ten","Jack","Queen","King","Ace"};
		String s;
		switch( i[0] )
        {
			
            case 1:
                s="high card " + Numbers[i[1]];
                break;
            case 2:
                s="pair of " + Numbers[i[1]] + "\'s";
                break;
            case 3:
                s="two pair " + Numbers[i[1]] + " " + 
                		Numbers[i[2]];
                break;
            case 4:
                s="three of a kind " + Numbers[i[1]] + "\'s";
                break;
            case 5:
                s=Numbers[i[1]] + " high straight";
                break;
            case 6:
                s="flush";
                break;
            case 7:
                s="full house " + Numbers[i[1]] + " over " + 
                		Numbers[i[2]];
                break;
            case 8:
                s="four of a kind " + Numbers[i[1]];
                break;
            case 9:
                s="straight flush " + Numbers[i[1]] + " high";
                break;
            default:
                s="error in Hand.display: value[0] contains invalid value";
        }
		s = "" + s;
        System.out.println(s);
	}
	
	public static int compareTo(int[] p1, int[] p2)
    {
        for (int x=0; x<6; x++)
        {
            if (p1[x]>p2[x])
                return 1;
            else if (p1[x]<p2[x])
                return -1;
        }
        return 0; //if hands are equal
    }
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		//long time = System.currentTimeMillis();
		//System.out.println(System.nanoTime());
		//for(int i = 1; i < 2000000; i++){
		DeckOfCards deck = new DeckOfCards();
		deck.setupPack();
		deck.shuffle();
		
		
		
		Card[] cards = new Card[7];
		cards[0] = deck.deck[0];
		cards[1] = deck.deck[1];
		cards[2] = deck.deck[4];
		cards[3] = deck.deck[5];
		cards[4] = deck.deck[6];
		cards[5] = deck.deck[7];
		cards[6] = deck.deck[8];
		
		
		Card[] cards2 = new Card[7];
		cards2[0] = deck.deck[2];
		cards2[1] = deck.deck[3];
		cards2[2] = deck.deck[4];
		cards2[3] = deck.deck[5];
		cards2[4] = deck.deck[6];
		cards2[5] = deck.deck[7];
		cards2[6] = deck.deck[8];
		
		//int test [] = evaluate(cards);
		//int test2 [] = evaluate(cards2);
		//System.out.println(ranks[test[0]] + " " + Numbers[test[1]]);
		//displayRank(test);
		//displayRank(test2);
		
		
		//System.out.println(compareTo(test,test2));
		System.out.println(cards[0].toString() + "\n" +
				cards[1].toString() + "\n" +
				cards[2].toString() + "\n" +
				cards[3].toString() + "\n" +
				cards[4].toString() + "\n" +
				cards[5].toString() + "\n" +
				cards[6].toString() + "\n");
		System.out.println(cards2[0].toString() + "\n" +
				cards2[1].toString() + "\n" +
				cards2[2].toString() + "\n" +
				cards2[3].toString() + "\n" +
				cards2[4].toString() + "\n" +
				cards2[5].toString() + "\n" +
				cards2[6].toString() + "\n");
		
		
		
		int[] rankOf1 = HandEvaluator.evaluate7(cards);
		int[] rankOf2 = HandEvaluator.evaluate7(cards2);
		HandEvaluator.displayRank(evaluate7(cards));
		HandEvaluator.displayRank(evaluate7(cards2));
		
		System.out.println();
		if(compareTo(rankOf1,rankOf2) == 1)
			HandEvaluator.displayRank(rankOf1);
		else if(compareTo(rankOf1,rankOf2) == -1)
			HandEvaluator.displayRank(rankOf2);
		else System.out.println("Draw");
		//System.out.println("--------------------------------------------");
		//}
		
		//long time2 = System.currentTimeMillis() - time;
		//System.out.println("Done in " + time2/1000);
	}

	public static String getRank(int[] i) {
		// TODO Auto-generated method stub
		String[] ranks = {"High Card","Pair","2 Pair","3 of a kind","Straight","Flush","Full House","4 of a kind","Straight Flush"};
		String[] Numbers = {"Null" ,"Ace","Two","Three","Four","Five","Six","Seven","Eight","Nine","Ten","Jack","Queen","King","Ace"};
		String s;
		switch( i[0] )
        {
			
            case 1:
                s="high card " + Numbers[i[1]];
                break;
            case 2:
                s="pair of " + Numbers[i[1]] + "\'s";
                break;
            case 3:
                s="two pair " + Numbers[i[1]] + " " + 
                		Numbers[i[2]];
                break;
            case 4:
                s="three of a kind " + Numbers[i[1]] + "\'s";
                break;
            case 5:
                s=Numbers[i[1]] + " high straight";
                break;
            case 6:
                s="flush";
                break;
            case 7:
                s="full house " + Numbers[i[1]] + " over " + 
                		Numbers[i[2]];
                break;
            case 8:
                s="four of a kind " + Numbers[i[1]];
                break;
            case 9:
                s="straight flush " + Numbers[i[1]] + " high";
                break;
            default:
                s="error in Hand.display: value[0] contains invalid value";
        }
		s = "" + s;
		return s;
	}
	
	

}
