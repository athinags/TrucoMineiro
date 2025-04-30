import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;
import javax.swing.*;

public class TrucoGUI extends JFrame implements GameObserver {
    private JPanel tablePanel;
    private JPanel playerHandPanel;
    private JPanel gameInfoPanel;
    private JTextArea gameLog;
    private JButton trucoButton;
    private JLabel team1ScoreLabel;
    private JLabel team2ScoreLabel;
    private JLabel currentPointsLabel;
    private JLabel currentRoundLabel;
    private JPanel team1RoundsPanel;
    private JPanel team2RoundsPanel;

    private Map<Integer, JButton> cardButtons = new HashMap<>();
    private TrucoGameFacade game;

    public TrucoGUI() {
        super("Truco Mineiro");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);

        initializeComponents();
        layoutComponents();

        setVisible(true);

        startGame();
    }

    private void initializeComponents() {
        // Painel da mesa
        tablePanel = new JPanel();
        tablePanel.setBorder(BorderFactory.createTitledBorder("Mesa"));
        tablePanel.setPreferredSize(new Dimension(600, 200));
        tablePanel.setLayout(new GridLayout(2, 2, 10, 10));

        // Painel da mão do jogador
        playerHandPanel = new JPanel();
        playerHandPanel.setBorder(BorderFactory.createTitledBorder("Sua Mão"));
        playerHandPanel.setPreferredSize(new Dimension(600, 150));

        // Painel de informações do jogo
        gameInfoPanel = new JPanel();
        gameInfoPanel.setBorder(BorderFactory.createTitledBorder("Informações do Jogo"));
        gameInfoPanel.setPreferredSize(new Dimension(600, 100));
        gameInfoPanel.setLayout(new GridLayout(2, 3));

        // Componentes de informação
        team1ScoreLabel = new JLabel("Nós: 0");
        team2ScoreLabel = new JLabel("Eles: 0");
        currentPointsLabel = new JLabel("Pontos da mão: 1");
        currentRoundLabel = new JLabel("Rodada: 1");

        // Painéis para mostrar rodadas ganhas
        team1RoundsPanel = new JPanel();
        team1RoundsPanel.setBorder(BorderFactory.createTitledBorder("Rodadas Nós"));
        team1RoundsPanel.setLayout(new FlowLayout(FlowLayout.LEFT));

        team2RoundsPanel = new JPanel();
        team2RoundsPanel.setBorder(BorderFactory.createTitledBorder("Rodadas Eles"));
        team2RoundsPanel.setLayout(new FlowLayout(FlowLayout.LEFT));

        // Botão de truco
        trucoButton = new JButton("Pedir Truco");
        trucoButton.addActionListener(e -> requestTruco());

        // Log do jogo
        gameLog = new JTextArea(10, 30);
        gameLog.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(gameLog);
        scrollPane.setPreferredSize(new Dimension(200, 400));
    }

    private void layoutComponents() {
        setLayout(new BorderLayout());

        // Painel principal central
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));

        // Adiciona componentes ao painel de informações
        gameInfoPanel.add(team1ScoreLabel);
        gameInfoPanel.add(currentRoundLabel);
        gameInfoPanel.add(team2ScoreLabel);
        gameInfoPanel.add(team1RoundsPanel);
        gameInfoPanel.add(currentPointsLabel);
        gameInfoPanel.add(team2RoundsPanel);

        // Adiciona componentes ao painel central
        centerPanel.add(gameInfoPanel);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        centerPanel.add(tablePanel);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        centerPanel.add(playerHandPanel);

        // Adiciona o botão de truco
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(trucoButton);
        centerPanel.add(buttonPanel);

        // Adiciona ao frame
        add(centerPanel, BorderLayout.CENTER);
        add(new JScrollPane(gameLog), BorderLayout.EAST);
    }

    private void startGame() {
        game = new TrucoGameFacade();
        game.addObserver(this);

        // Substituímos o HumanPlayer padrão por um GUI Player
        setupGUIGame();

        game.startGame();
    }

    private void setupGUIGame() {
        // Cria times
        Team team1 = new Team("Nós");
        Team team2 = new Team("Eles");

        // Cria um jogador GUI que usa esta interface
        GUIPlayer humanPlayer = new GUIPlayer("Jogador", this);
        team1.addPlayer(humanPlayer);
        team1.addPlayer(PlayerFactory.createMineiroAIPlayer("Parceiro"));
        team2.addPlayer(PlayerFactory.createRandomAIPlayer("Oponente 1"));
        team2.addPlayer(PlayerFactory.createRandomAIPlayer("Oponente 2"));

        // Adiciona times ao jogo
        game.addTeam(team1);
        game.addTeam(team2);
    }

    public Card getUserCardChoice(List<Card> hand) {
        updatePlayerHand(hand);
        
        // Retorna null aqui, o controle real acontece no CardActionListener
        // que vai notificar o GUIPlayer quando uma carta for selecionada
        return null;
    }

    private void updatePlayerHand(List<Card> hand) {
        playerHandPanel.removeAll();
        cardButtons.clear();

        for (int i = 0; i < hand.size(); i++) {
            Card card = hand.get(i);
            JButton cardButton = createCardButton(card, i);
            playerHandPanel.add(cardButton);
            cardButtons.put(i, cardButton);
        }

        playerHandPanel.revalidate();
        playerHandPanel.repaint();
    }

    private JButton createCardButton(Card card, int index) {
        JButton cardButton = new JButton(getCardLabel(card));
        cardButton.setPreferredSize(new Dimension(120, 120));
        cardButton.setBackground(getCardColor(card.getSuit()));
        cardButton.addActionListener(new CardActionListener(card, index));
        return cardButton;
    }

    private String getCardLabel(Card card) {
        return "<html><center>" + card.getRank() + "<br>de<br>" + card.getSuit() + 
               "<br>Valor: " + card.getMineiroValue() + "</center></html>";
    }

    private Color getCardColor(Suit suit) {
        switch (suit) {
            case COPAS: return new Color(255, 200, 200); // Rosa claro
            case OUROS: return new Color(255, 230, 200); // Laranja claro
            case PAUS: return new Color(200, 255, 200);  // Verde claro
            case ESPADAS: return new Color(200, 200, 255); // Azul claro
            default: return Color.WHITE;
        }
    }

    private void requestTruco() {
        // Esta funcionalidade será implementada pelo controlador do jogo
        appendToLog("Você pediu TRUCO!");
        // Aqui você chamaria a função do jogo para processar o pedido de truco
    }

    public boolean getUserTrucoDecision() {
        // Exibe um diálogo para o usuário decidir se aceita o truco
        int response = JOptionPane.showConfirmDialog(
            this,
            "Oponente pediu TRUCO. Aceitar?",
            "Pedido de Truco",
            JOptionPane.YES_NO_OPTION
        );

        return response == JOptionPane.YES_OPTION;
    }

    private void updateTableView(List<Card> cardsOnTable, List<Player> players) {
        tablePanel.removeAll();

        // Posicionamento relativo dos jogadores na mesa
        // 0: Jogador (posição inferior)
        // 1: Adversário à direita
        // 2: Parceiro (posição superior)
        // 3: Adversário à esquerda

        // Preenchemos os painéis vazios ou com cartas jogadas
        for (int i = 0; i < 4; i++) {
            JPanel cardPanel = new JPanel();
            cardPanel.setBorder(BorderFactory.createEtchedBorder());

            // Verificamos se existe uma carta jogada para esta posição
            boolean cardFound = false;
            for (int j = 0; j < cardsOnTable.size() && j < players.size(); j++) {
                Player player = players.get(j);
                // Simplificação - assumindo ordem fixa de jogadores
                if (player.getName().equals(getPlayerNameForPosition(i))) {
                    Card card = cardsOnTable.get(j);
                    JLabel cardLabel = new JLabel(getCardLabel(card));
                    cardLabel.setOpaque(true);
                    cardLabel.setBackground(getCardColor(card.getSuit()));
                    cardPanel.add(cardLabel);
                    cardFound = true;
                    break;
                }
            }

            if (!cardFound) {
                cardPanel.add(new JLabel(getPlayerNameForPosition(i)));
            }

            tablePanel.add(cardPanel);
        }

        tablePanel.revalidate();
        tablePanel.repaint();
    }

    private String getPlayerNameForPosition(int position) {
        switch (position) {
            case 0: return "Jogador";
            case 1: return "Oponente 1";
            case 2: return "Parceiro";
            case 3: return "Oponente 2";
            default: return "";
        }
    }

    private void updateRoundsDisplay(int team1Rounds, int team2Rounds) {
        team1RoundsPanel.removeAll();
        team2RoundsPanel.removeAll();

        for (int i = 0; i < team1Rounds; i++) {
            JLabel roundMarker = new JLabel("●");
            roundMarker.setForeground(Color.GREEN);
            team1RoundsPanel.add(roundMarker);
        }

        for (int i = 0; i < team2Rounds; i++) {
            JLabel roundMarker = new JLabel("●");
            roundMarker.setForeground(Color.RED);
            team2RoundsPanel.add(roundMarker);
        }

        team1RoundsPanel.revalidate();
        team1RoundsPanel.repaint();
        team2RoundsPanel.revalidate();
        team2RoundsPanel.repaint();
    }

    private void appendToLog(String message) {
        gameLog.append(message + "\n");
        gameLog.setCaretPosition(gameLog.getDocument().getLength());
    }

    // Implementação dos métodos da interface GameObserver
    @Override
    public void onGameStateChanged(GameState state, Table table) {
        if (table != null) {
            updateTableView(table.getCardsOnTable(), table.getCurrentPlayers());
            currentRoundLabel.setText("Rodada: " + table.getCurrentRound());
            currentPointsLabel.setText("Pontos da mão: " + table.getCurrentPoints());

            if (table.getTeams() != null && table.getTeams().size() >= 2) {
                updateRoundsDisplay(table.getRoundsWonByTeam(0), table.getRoundsWonByTeam(1));
            }
        }

        appendToLog("Estado do jogo: " + state);
    }

    @Override
    public void onCardPlayed(Player player, Card card) {
        appendToLog(player.getName() + " jogou " + card);
    }

    @Override
    public void onRoundFinished(Team winnerTeam) {
        appendToLog(winnerTeam.getName() + " venceu a rodada!");
    }

    @Override
    public void onHandFinished(Team winnerTeam, int points) {
        appendToLog(winnerTeam.getName() + " ganhou " + points + " pontos!");

        // Atualiza as pontuações
        if (winnerTeam.getName().equals("Nós")) {
            team1ScoreLabel.setText("Nós: " + winnerTeam.getScore());
        } else {
            team2ScoreLabel.setText("Eles: " + winnerTeam.getScore());
        }
    }

    @Override
    public void onTrucoRequested(Player requestingPlayer) {
        appendToLog(requestingPlayer.getName() + " pediu TRUCO!!!");
    }

    @Override
    public void onTrucoResponse(boolean accepted) {
        appendToLog("Truco " + (accepted ? "aceito!" : "recusado!"));
    }

    // Classe interna para lidar com eventos de clique nas cartas
    private class CardActionListener implements ActionListener {
        private Card card;
        private int index;

        public CardActionListener(Card card, int index) {
            this.card = card;
            this.index = index;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            // Desabilita todos os botões após a escolha
            for (JButton button : cardButtons.values()) {
                button.setEnabled(false);
            }
            
            appendToLog("Você selecionou: " + card);

            // Notifica que uma carta foi escolhida
            synchronized(GUIPlayer.getLock()) {
                GUIPlayer.setSelectedCard(card);
                GUIPlayer.setSelectedIndex(index);
                GUIPlayer.getLock().notifyAll();
            }
        }
    }

    // Método principal para execução
    public static void main(String[] args) {
        // Configura o look and feel para corresponder à plataforma
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> new TrucoGUI());
    }
}

// Classe para implementar o jogador GUI
class GUIPlayer extends Player {
    private static final Object lock = new Object();
    private static Card selectedCard;
    private static int selectedIndex;
    private TrucoGUI gui;

    public GUIPlayer(String name, TrucoGUI gui) {
        super(name);
        this.gui = gui;
    }

    @Override
    public Card playCard(Table table) {
        // Solicita a escolha do usuário através da GUI
        synchronized(lock) {
            // Limpa a seleção anterior
            selectedCard = null;
            selectedIndex = -1;

            // Mostra as cartas e aguarda o usuário escolher
            gui.getUserCardChoice(hand);

            try {
                // Espera a escolha
                lock.wait();

                // Remove a carta selecionada da mão
                if (selectedIndex >= 0 && selectedIndex < hand.size()) {
                    return hand.remove(selectedIndex);
                }
                // Fallback se algo der errado
                return hand.remove(0);
            } catch (InterruptedException e) {
                e.printStackTrace();
                return hand.remove(0);
            }
        }
    }

    @Override
    public boolean decideTruco(Table table) {
        // Pergunta ao usuário através da GUI
        return gui.getUserTrucoDecision();
    }

    public static Object getLock() {
        return lock;
    }

    public static void setSelectedCard(Card card) {
        selectedCard = card;
    }

    public static void setSelectedIndex(int index) {
        selectedIndex = index;
    }
}