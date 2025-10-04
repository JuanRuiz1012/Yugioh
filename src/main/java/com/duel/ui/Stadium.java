package com.duel.ui;

import com.duel.api.YgoApiClient;
import com.duel.logic.Duel;
import com.duel.logic.BattleListener;
import com.duel.model.Card;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class Stadium extends JFrame implements BattleListener {
    private JPanel panel1;
    private JTextArea textArea;
    private JButton botonInicio;
    private JButton carta1;
    private JButton carta2;
    private JButton carta3;
    private JPanel panelInicial;
    private JPanel panelCartas;
    private JScrollPane panelText;
    private JButton cartaRival1;
    private JButton cartaRival2;
    private JButton cartaRival3;
    private JPanel panelRival;
    private JPanel panelTitulo;
    private JLabel labelTitulo;

    // Atributos para la lógica del duelo
    private YgoApiClient apiClient = new YgoApiClient();
    private Duel duel;
    private List<Card> playerCards = new ArrayList<>();
    private List<Card> aiCards = new ArrayList<>();
    private List<Boolean> playerCardsUsed = new ArrayList<>();  // Para rastrear cartas usadas

    public Stadium() {
        setContentPane(panel1);
        setTitle("Yu-Gi-Oh A Pelear Durisimo");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 550);
        setLocationRelativeTo(null);

        initializeUI();  // Configura listeners y UI inicial
    }

    private void initializeUI() {
        // Configurar textArea
        textArea.setEditable(false);
        textArea.setText("Bienvenido al Duelo de Yu-Gi-Oh!. Presiona 'Iniciar Duelo' para comenzar.\n");

        // Listener para botonInicio
        botonInicio.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                startDuel();
            }
        });

        // Listeners para botones de cartas del jugador (inicialmente deshabilitados)
        carta1.setEnabled(false);
        carta2.setEnabled(false);
        carta3.setEnabled(false);
        carta1.addActionListener(e -> selectPlayerCard(0));  // Pasar índice en lugar de botón para simplicidad
        carta2.addActionListener(e -> selectPlayerCard(1));
        carta3.addActionListener(e -> selectPlayerCard(2));

        // Botones de rival (inicialmente vacíos y deshabilitados, se mostrarán después)
        cartaRival1.setEnabled(false);
        cartaRival2.setEnabled(false);
        cartaRival3.setEnabled(false);
        panelRival.setVisible(false);  // Ocultar panel rival inicialmente
    }

    private void startDuel() {
        botonInicio.setEnabled(false);
        textArea.append("Cargando cartas...\n");
        textArea.setCaretPosition(textArea.getDocument().getLength());  // Auto-scroll

        // Limpiar paneles de cartas y estado
        clearPlayerCards();
        clearAiCards();
        playerCardsUsed.clear();

        // Cargar cartas en hilo separado para no bloquear UI
        new Thread(() -> {
            try {
                playerCards.clear();
                aiCards.clear();

                for (int i = 0; i < 3; i++) {
                    playerCards.add(apiClient.getRandomMonsterCard());
                    aiCards.add(apiClient.getRandomMonsterCard());
                    playerCardsUsed.add(false);  // Inicializar como no usadas
                }

                duel = new Duel(playerCards, aiCards, this);

                SwingUtilities.invokeLater(() -> {
                    textArea.append("Cartas cargadas. Selecciona una carta para jugar (puedes jugar múltiples turnos).\n");
                    textArea.setCaretPosition(textArea.getDocument().getLength());
                    showPlayerCards();
                    showAiCards();  // Mostrar cartas de AI en panelRival
                });
            } catch (Exception ex) {
                SwingUtilities.invokeLater(() -> {
                    textArea.append("Error cargando cartas: " + ex.getMessage() + "\n");
                    textArea.setCaretPosition(textArea.getDocument().getLength());
                    botonInicio.setEnabled(true);
                });
            }
        }).start();
    }

    private void showPlayerCards() {
        // Asignar cartas a botones específicos
        if (playerCards.size() >= 3) {
            setupCardButton(carta1, playerCards.get(0), 0);
            setupCardButton(carta2, playerCards.get(1), 1);
            setupCardButton(carta3, playerCards.get(2), 2);
        }
        panelCartas.revalidate();
        panelCartas.repaint();
    }

    private void showAiCards() {
        // Mostrar cartas de AI en botones rivales
        if (aiCards.size() >= 3) {
            setupAiCardButton(cartaRival1, aiCards.get(0));
            setupAiCardButton(cartaRival2, aiCards.get(1));
            setupAiCardButton(cartaRival3, aiCards.get(2));
        }
        panelRival.setVisible(true);  // Mostrar panel rival
        panelRival.revalidate();
        panelRival.repaint();
    }

    private void setupCardButton(JButton button, Card card, int index) {
        boolean isUsed = playerCardsUsed.get(index);
        button.setEnabled(!isUsed);
        button.setText("<html>" + card.getName() + "<br>ATK: " + card.getAtk() + " DEF: " + card.getDef() + (isUsed ? " (Usada)" : "") + "</html>");
        try {
            ImageIcon icon = new ImageIcon(new URL(card.getImageUrl()));
            Image img = icon.getImage().getScaledInstance(100, 145, Image.SCALE_SMOOTH);
            button.setIcon(new ImageIcon(img));
            button.setHorizontalTextPosition(SwingConstants.CENTER);
            button.setVerticalTextPosition(SwingConstants.BOTTOM);
            button.setPreferredSize(new Dimension(120, 180));  // Tamaño fijo para mejor visualización
        } catch (Exception e) {
            // Ignorar error de imagen
            System.err.println("Error cargando imagen para carta: " + card.getName());
        }
    }

    private void setupAiCardButton(JButton button, Card card) {
        button.setEnabled(false);
        button.setText("<html>" + card.getName() + "<br>ATK: " + card.getAtk() + " DEF: " + card.getDef() + "</html>");
        try {
            ImageIcon icon = new ImageIcon(new URL(card.getImageUrl()));
            Image img = icon.getImage().getScaledInstance(100, 145, Image.SCALE_SMOOTH);
            button.setIcon(new ImageIcon(img));
            button.setHorizontalTextPosition(SwingConstants.CENTER);
            button.setVerticalTextPosition(SwingConstants.BOTTOM);
            button.setPreferredSize(new Dimension(120, 180));
        } catch (Exception e) {
            // Ignorar error de imagen
            System.err.println("Error cargando imagen para carta AI: " + card.getName());
        }
    }

    private void selectPlayerCard(int index) {
        if (index < 0 || index >= playerCards.size() || playerCardsUsed.get(index)) {
            textArea.append("Esta carta ya fue usada o no está disponible.\n");
            textArea.setCaretPosition(textArea.getDocument().getLength());
            return;
        }

        Card selectedCard = playerCards.get(index);
        playerCardsUsed.set(index, true);  // Marcar como  carta usada
        duel.playTurn(selectedCard);

        // Se deshabilita este botón ( para permitir más turnos)
        switch (index) {
            case 0 -> carta1.setEnabled(false);
            case 1 -> carta2.setEnabled(false);
            case 2 -> carta3.setEnabled(false);
        }

        // Actualizar visual de todas las cartas (para mostrar "(Usada)")
        showPlayerCards();  // Re-dibuja para reflejar estado usado

        textArea.append("Carta seleccionada: " + selectedCard.getName() + ". Esperando resultado del turno...\n");
        textArea.setCaretPosition(textArea.getDocument().getLength());

        // Verifica si todas las cartas del jugador están usadas
        if (playerCardsUsed.stream().allMatch(Boolean::booleanValue)) {
            textArea.append("¡Has usado todas tus cartas! El duelo continúa con la IA.\n");
        }
    }

    private void clearPlayerCards() {
        carta1.setText("");
        carta1.setIcon(null);
        carta1.setEnabled(false);
        carta2.setText("");
        carta2.setIcon(null);
        carta2.setEnabled(false);
        carta3.setText("");
        carta3.setIcon(null);
        carta3.setEnabled(false);
        panelCartas.revalidate();
        panelCartas.repaint();
    }

    private void clearAiCards() {
        cartaRival1.setText("");
        cartaRival1.setIcon(null);
        cartaRival2.setText("");
        cartaRival2.setIcon(null);
        cartaRival3.setText("");
        cartaRival3.setIcon(null);
        panelRival.setVisible(false);
        panelRival.revalidate();
        panelRival.repaint();
    }

    // Métodos de BattleListener
    @Override
    public void onTurn(String playerCard, String aiCard, String winner) {
        String resultado;
        if ("player".equals(winner)) {
            resultado = " ¡Ganaste el turno!";
        } else if ("ai".equals(winner)) {
            resultado = " La máquina ganó el turno.";
        } else {
            resultado = " Empate en el turno.";
        }

        textArea.append(String.format(
                " %-10s | %-25s | %-25s | %s%n",
                "Turno", "Jugador: " + playerCard, "Máquina: " + aiCard, resultado
        ));
    }


    @Override
    public void onScoreChanged(int playerScore, int aiScore) {
        textArea.append(String.format("Marcador - Jugador: %d, Máquina: %d\n", playerScore, aiScore));
        textArea.setCaretPosition(textArea.getDocument().getLength());
    }

    @Override
    public void onDuelEnded(String winner) {
        textArea.append("El duelo terminó. Ganador: " + winner + "\n");
        textArea.setCaretPosition(textArea.getDocument().getLength());
        JOptionPane.showMessageDialog(this, "El duelo terminó. Ganador: " + winner, "Duelo Finalizado", JOptionPane.INFORMATION_MESSAGE);
        botonInicio.setEnabled(true);
        // se desabilita todos los botones y se limpia para nuevo duelo
        carta1.setEnabled(false);
        carta2.setEnabled(false);
        carta3.setEnabled(false);
        clearPlayerCards();
        clearAiCards();
        playerCardsUsed.clear();
    }

    // Método público para reiniciar
    public void resetDuel() {
        playerCards.clear();
        aiCards.clear();
        duel = null;
        playerCardsUsed.clear();
        textArea.setText("Duelo reiniciado. Presiona 'Iniciar Duelo' para comenzar de nuevo.\n");
        textArea.setCaretPosition(textArea.getDocument().getLength());
        clearPlayerCards();
        clearAiCards();
        botonInicio.setEnabled(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Stadium view = new Stadium();
            view.setVisible(true);
        });
    }
}
