package com.duel.ui;

import com.duel.api.YgoApiClient;
import com.duel.logic.BattleListener;
import com.duel.logic.Duel;
import com.duel.model.Card;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class DuelApp extends JFrame implements BattleListener {
    private JTextArea battleLog;
    private List<Card> playerCards = new ArrayList<>();
    private List<Card> aiCards = new ArrayList<>();
    private Duel duel;

    public DuelApp() {
        setTitle("Yu-Gi-Oh! Duel Lite");
        setSize(800, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        battleLog = new JTextArea();
        battleLog.setEditable(false);
        add(new JScrollPane(battleLog), BorderLayout.CENTER);

        JButton startBtn = new JButton("Iniciar duelo");
        startBtn.addActionListener(e -> loadCards());
        add(startBtn, BorderLayout.SOUTH);
    }

    private void loadCards() {
        SwingWorker<Void, String> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() throws Exception {
                playerCards.clear();
                aiCards.clear();

                for (int i = 0; i < 3; i++) {
                    playerCards.add(YgoApiClient.getRandomMonsterCard());
                    aiCards.add(YgoApiClient.getRandomMonsterCard());
                }
                return null;
            }

            @Override
            protected void done() {
                duel = new Duel(playerCards, aiCards, DuelApp.this);
                showPlayerCards();
            }
        };
        worker.execute();
    }

    private void showPlayerCards() {
        JPanel panel = new JPanel();
        panel.removeAll(); // limpia si había algo antes

        for (Card card : playerCards) {
            JButton cardBtn = new JButton(card.toString());
            cardBtn.addActionListener(e -> duel.playTurn(card));
            panel.add(cardBtn);
        }

        getContentPane().add(panel, BorderLayout.NORTH);
        revalidate();   // fuerza refresco
        repaint();      // fuerza redibujado
    }


    @Override
    public void onTurn(String playerCard, String aiCard, String winner) {
        battleLog.append("Jugador: " + playerCard + "\n");
        battleLog.append("IA: " + aiCard + "\n");
        battleLog.append("Ganador: " + winner + "\n\n");
    }

    @Override
    public void onScoreChanged(int playerScore, int aiScore) {
        battleLog.append("Marcador -> Jugador: " + playerScore + " | IA: " + aiScore + "\n\n");
    }

    @Override
    public void onDuelEnded(String winner) {
        JOptionPane.showMessageDialog(this, "¡Ganador final: " + winner + "!");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new DuelApp().setVisible(true));
    }
}
