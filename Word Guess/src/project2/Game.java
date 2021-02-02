package project2;

import java.util.HashSet;

public class Game {
    String firstPlayerName="",secondPlayerName="";
    int ID;
    String fixedWord="",currentWord="";
    int round=0,guessCounter=0,numberOfRounds;
    String turn;
    HashSet<Character> letters=new HashSet<>();
    

    public Game(String first,String second,int ID,int numberOfRounds){
        this.firstPlayerName=first;
        this.secondPlayerName=second;
        this.ID=ID;
        this.turn=firstPlayerName;
        this.numberOfRounds=numberOfRounds;
    }
    
    public void initialize(){
        fixedWord="";
        guessCounter=0;
    }
    
    public void swapPlayers(){
    	if(turn.equals(firstPlayerName))
    		turn=secondPlayerName;
    	else
    		turn=firstPlayerName;
    }
    
    public boolean isOver() {
    	if(guessCounter==fixedWordSize())
    		return true;
    	return false;
    }
    
    public boolean hasThisLetter(char letter){
    	return letters.contains((Character)letter);
        //return letters.contains(String.valueOf(letter.charAt(0)));
    }
    
    public void setWord(String word){
        fixedWord=word;
    }
    
    public int fixedWordSize(){
        return fixedWord.length();
    }
    
    public String getOpponent(String name){
        if(name.compareTo(firstPlayerName)==0)
            return secondPlayerName;
        else
            return firstPlayerName;
    }
    
    public String getFixedWord(){
        return fixedWord;
    }
    
    public String getCurrentWord(){
        return currentWord;
    }
    
    public void setFixedWord(String word){
        fixedWord=word;
    }
    
    public void setLetter(String letters){
        char letter=letters.charAt(0);
        this.letters.add(letter);
        this.guessCounter++;
        updateCurrentWord();
    }
    
    private void updateCurrentWord(){
        currentWord="";
        for(int i=0;i<fixedWord.length();i++){
            if(hasThisLetter(fixedWord.charAt(i))){
                currentWord+=fixedWord.charAt(i);
            }
            else{
                currentWord+='-';
            }
        }
    }
}