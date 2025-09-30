package com.duel.logic;

import com.duel.model.Card;
import java.util.*;

public class Duel {
    private List<Card> playerCards;
    private List<Card> aiCards;
    private int playerScore;
    private int aiScore;
    private com.duel.logic.BattleListener listener;

    public Duel(List<Card> playerCards, List<Card> aiCards, com.duel.logic.BattleListener listener) {
        this.playerCards = new ArrayList<>(playerCards);
        this.aiCards = new ArrayList<>(aiCards);
        this.listener = listener;
    }

    public void playTurn(Card playerChoice) {
        Random rand = new Random();
        Card aiChoice = aiCards.remove(rand.nextInt(aiCards.size()));

        String winner;
        if (playerChoice.getAtk() > aiChoice.getAtk()) {
            playerScore++;
            winner = "Jugador";
        } else if (playerChoice.getAtk() < aiChoice.getAtk()) {
            aiScore++;
            winner = "IA";
        } else {
            winner = "Empate";
        }

        listener.onTurn(playerChoice.toString(), aiChoice.toString(), winner);
        listener.onScoreChanged(playerScore, aiScore);

        if (playerScore == 2 || aiScore == 2) {
            listener.onDuelEnded(playerScore > aiScore ? "Jugador" : "IA");
        }
    }
}
