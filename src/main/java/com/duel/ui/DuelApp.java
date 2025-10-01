package com.duel.ui;
import com.duel.api.YgoApiClient;
import com.duel.logic.Duel;
import com.duel.logic.BattleListener;
import com.duel.model.Card;
import javax.swing.*;
import java.awt.*;
import java.net.URL;  // <-- Importa esta clase
import java.util.ArrayList;
import java.util.List;


import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

public class DuelApp extends JFrame implements BattleListener {
    private YgoApiClient apiClient = new YgoApiClient();
    private Duel duel;

    private List<Card> playerCards = new ArrayList<>();
    private List<Card> aiCards = new ArrayList<>();

    private JPanel playerCardsPanel = new JPanel();
    private JTextArea battleLog = new JTextArea(10, 40);
    private JButton startButton = new JButton("Iniciar Duelo");

    public DuelApp() {
        setTitle("Yu-Gi-Oh! Duel");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(800, 600);
        setLayout(new BorderLayout());

        battleLog.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(battleLog);

        playerCardsPanel.setLayout(new FlowLayout());

        add(playerCardsPanel, BorderLayout.SOUTH);
        add(scrollPane, BorderLayout.CENTER);
        add(startButton, BorderLayout.NORTH);

        startButton.addActionListener(e -> startDuel());

        setLocationRelativeTo(null);
    }

    private void startDuel() {
        startButton.setEnabled(false);
        battleLog.setText("Cargando cartas...\n");

        // Cargar cartas en hilo separado para no bloquear UI
        new Thread(() -> {
            try {
                playerCards.clear();
                aiCards.clear();

                for (int i = 0; i < 3; i++) {
                    playerCards.add(apiClient.getRandomMonsterCard());
                    aiCards.add(apiClient.getRandomMonsterCard());
                }

                duel = new Duel(playerCards, aiCards, this);

                SwingUtilities.invokeLater(() -> {
                    battleLog.append("Cartas cargadas. Selecciona una carta para jugar.\n");
                    showPlayerCards();
                });
            } catch (Exception ex) {
                SwingUtilities.invokeLater(() -> {
                    battleLog.append("Error cargando cartas: " + ex.getMessage() + "\n");
                    startButton.setEnabled(true);
                });
            }
        }).start();
    }

    private void showPlayerCards() {
        playerCardsPanel.removeAll();
        for (Card card : playerCards) {
            JButton btn = new JButton("<html>" + card.getName() + "<br>ATK: " + card.getAtk() + " DEF: " + card.getDef() + "</html>");
            try {
                ImageIcon icon = new ImageIcon(new URL(card.getImageUrl()));
                Image img = icon.getImage().getScaledInstance(100, 145, Image.SCALE_SMOOTH);
                btn.setIcon(new ImageIcon(img));
                btn.setHorizontalTextPosition(SwingConstants.CENTER);
                btn.setVerticalTextPosition(SwingConstants.BOTTOM);
            } catch (Exception e) {
                // Ignorar error de imagen
            }
            btn.addActionListener(e -> {
                duel.playTurn(card);
                btn.setEnabled(false);
            });
            playerCardsPanel.add(btn);
        }
        playerCardsPanel.revalidate();
        playerCardsPanel.repaint();
    }

    // Métodos BattleListener

    @Override
    public void onTurn(String playerCard, String aiCard, String winner) {
        String msg = String.format("Turno: Jugador usó %s, Máquina usó %s. ", playerCard, aiCard);
        if ("player".equals(winner)) msg += "Ganaste el turno.";
        else if ("ai".equals(winner)) msg += "La máquina ganó el turno.";
        else msg += "Empate en el turno.";
        battleLog.append(msg + "\n");
    }

    @Override
    public void onScoreChanged(int playerScore, int aiScore) {
        battleLog.append(String.format("Marcador - Jugador: %d, Máquina: %d\n", playerScore, aiScore));
    }

    @Override
    public void onDuelEnded(String winner) {
        battleLog.append("El duelo terminó. Ganador: " + winner + "\n");
        JOptionPane.showMessageDialog(this, "El duelo terminó. Ganador: " + winner);
        startButton.setEnabled(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new DuelApp().setVisible(true);
        });
    }
}
