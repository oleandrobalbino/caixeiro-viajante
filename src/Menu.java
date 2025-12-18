import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

public class Menu extends JFrame {
    private JTextField filePathField;
    private JButton browseButton;
    private JTextField populationSizeField;
    private JTextField numParentsField;
    private JTextField numGenerationsField;
    private JTextField mutationRateField;
    private JButton runGeneticButton;
    private JButton runBranchBoundButton;
    private JTextArea resultArea;
    private JComboBox<String> mutationTypeComboBox;
    private JCheckBox enable2OptCheckBox;
    private JTextField numCidadesField;

    public Menu() {
        setTitle("Caixeiro Viajante");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JPanel inputPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        filePathField = new JTextField(30);
        browseButton = new JButton("Procurar Arquivo");
        populationSizeField = new JTextField(10);
        numParentsField = new JTextField(10);
        numGenerationsField = new JTextField(10);
        mutationRateField = new JTextField(10);
        runGeneticButton = new JButton("Executar Algoritmo Genético");
        runBranchBoundButton = new JButton("Executar Algoritmo Ótimo");

        String[] tiposMutacao = {"Mutação de Troca", "Mutação de Inversão", "Mutação de Mistura", "Mutação de Inserção"};
        mutationTypeComboBox = new JComboBox<>(tiposMutacao);

        enable2OptCheckBox = new JCheckBox("Habilitar 2-OPT");

        gbc.gridx = 0;
        gbc.gridy = 0;
        inputPanel.add(new JLabel("Caminho do Arquivo:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        inputPanel.add(filePathField, gbc);

        gbc.gridx = 2;
        gbc.gridy = 0;
        inputPanel.add(browseButton, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        inputPanel.add(new JLabel("Tamanho da População:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        inputPanel.add(populationSizeField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        inputPanel.add(new JLabel("Número de Pais:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 2;
        inputPanel.add(numParentsField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        inputPanel.add(new JLabel("Número de Gerações:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 3;
        inputPanel.add(numGenerationsField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        inputPanel.add(new JLabel("Taxa de Mutação (%):"), gbc);

        numCidadesField = new JTextField(10);
        gbc.gridx = 0;
        gbc.gridy = 5;
        inputPanel.add(new JLabel("Quantidade de Cidades:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 5;
        inputPanel.add(numCidadesField, gbc);

        gbc.gridx = 1;
        gbc.gridy = 4;
        inputPanel.add(mutationRateField, gbc);

        gbc.gridx = 2;
        gbc.gridy = 4;
        inputPanel.add(mutationTypeComboBox, gbc);

        gbc.gridx = 0;
        gbc.gridy = 6;
        inputPanel.add(enable2OptCheckBox, gbc);

        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 3;
        gbc.anchor = GridBagConstraints.CENTER;
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.add(runGeneticButton);
        buttonPanel.add(runBranchBoundButton);
        inputPanel.add(buttonPanel, gbc);

        resultArea = new JTextArea();
        resultArea.setEditable(false);
        resultArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        JScrollPane scrollPane = new JScrollPane(resultArea);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Resultados"));

        mainPanel.add(inputPanel, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        add(mainPanel, BorderLayout.CENTER);

        browseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                int returnValue = fileChooser.showOpenDialog(null);
                if (returnValue == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = fileChooser.getSelectedFile();
                    filePathField.setText(selectedFile.getAbsolutePath());
                }
            }
        });

        runGeneticButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String filePath = filePathField.getText();
                int populationSize = Integer.parseInt(populationSizeField.getText());
                int numParents = Integer.parseInt(numParentsField.getText());
                int numGenerations = Integer.parseInt(numGenerationsField.getText());
                double mutationRate = Double.parseDouble(mutationRateField.getText()) / 100.0;
        
                String selectedMutationType = (String) mutationTypeComboBox.getSelectedItem();
                int tipoMutacao = 3;
                switch (selectedMutationType) {
                    case "Mutação de Inversão":
                        tipoMutacao = 4;
                        break;
                    case "Mutação de Mistura":
                        tipoMutacao = 5;
                        break;
                    case "Mutação de Inserção":
                        tipoMutacao = 6;
                        break;
                }
        
                boolean habilitar2Opt = enable2OptCheckBox.isSelected();

                int numCidadesDesejadas = Integer.parseInt(numCidadesField.getText());
        
                FileManager fileManager = new FileManager();
                ArrayList<String> lines = fileManager.stringReader(filePath);
                if (lines == null) {
                    resultArea.setText("Erro ao ler o arquivo.");
                    return;
                }
        
                int totalCidades = Integer.parseInt(lines.get(0).trim());
                if (numCidadesDesejadas > totalCidades || numCidadesDesejadas <= 0) {
                    resultArea.setText("Quantidade de cidades inválida.");
                    return;
                }
        
                int[][] grafo = new int[numCidadesDesejadas][numCidadesDesejadas];
        
                for (int i = 1; i <= numCidadesDesejadas; i++) {
                    String line = lines.get(i).trim();
                    String[] parts = line.split(" ");
                    int origem = Integer.parseInt(parts[0]);
                    for (int j = 1; j < parts.length; j++) {
                        String[] dist = parts[j].split("-");
                        int destino = Integer.parseInt(dist[0]);
                        double distancia = Double.parseDouble(dist[1].replace(";", ""));
                        if (destino < numCidadesDesejadas) {
                            grafo[origem][destino] = (int) distancia;
                        }
                    }
                }
        
                long startTime = System.nanoTime();
        
                AlgoritmoGenetico ag = new AlgoritmoGenetico(grafo);
                ag.setTipoMutacao(tipoMutacao);
                ag.setHabilitar2Opt(habilitar2Opt);
        
                StringBuilder geracoesInfo = new StringBuilder();
        
                final int[] melhorCustoGlobal = {Integer.MAX_VALUE};
        
                ag.setProgressListener(new ProgressaoEmTempoReal() {
                    @Override
                    public void onGenerationComplete(int generation, double mediaCusto, int globalBestCost) {
                        geracoesInfo.append(String.format(
                                "Geração %d - Média de Custo = %.2f, Melhor Custo Global = %d\n",
                                generation, mediaCusto, globalBestCost));
        
                        if (globalBestCost < melhorCustoGlobal[0]) {
                            melhorCustoGlobal[0] = globalBestCost;
                        }
                    }
                });
        
                int[] melhorRota = ag.resolver(populationSize, numGenerations, mutationRate, numParents);
        
                long endTime = System.nanoTime();
        
                long durationMs = (endTime - startTime) / 1_000_000;
        
                long minutos = durationMs / 60000;
                long segundos = (durationMs % 60000) / 1000;
                long milissegundos = durationMs % 1000;

                resultArea.append("\n--- Progresso das Gerações ---\n");
                resultArea.append(geracoesInfo.toString());
        
                resultArea.append("\n--- Informações Importantes ---\n");
                resultArea.append("Quantidade de Cidades: " + numCidadesDesejadas + "\n");
                resultArea.append("Melhor Rota Encontrada:\n");
                resultArea.append(Arrays.toString(melhorRota).replaceAll("[\\[\\],]", "") + "\n");
                resultArea.append("Custo Final: " + ag.calcularCustoRota(melhorRota) + "\n");
                resultArea.append("Tempo de Execução: " + minutos + " minutos, " + segundos + " segundos e " + milissegundos + " milissegundos\n");
            }
        });

        runBranchBoundButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String filePath = filePathField.getText();
                int numCidadesDesejadas = Integer.parseInt(numCidadesField.getText());
        
                FileManager fileManager = new FileManager();
                ArrayList<String> lines = fileManager.stringReader(filePath);
                if (lines == null) {
                    resultArea.setText("Erro ao ler o arquivo.");
                    return;
                }
        
                int totalCidades = Integer.parseInt(lines.get(0).trim());
                if (numCidadesDesejadas > totalCidades || numCidadesDesejadas <= 0) {
                    resultArea.setText("Quantidade de cidades inválida.");
                    return;
                }
        
                double[][] matriz = new double[numCidadesDesejadas][numCidadesDesejadas];
        
                for (int i = 1; i <= numCidadesDesejadas; i++) {
                    String line = lines.get(i).trim();
                    String[] parts = line.split(" ");
                    int origem = Integer.parseInt(parts[0]);
                    for (int j = 1; j < parts.length; j++) {
                        String[] dist = parts[j].split("-");
                        int destino = Integer.parseInt(dist[0]);
                        double distancia = Double.parseDouble(dist[1].replace(";", ""));
                        if (destino < numCidadesDesejadas) {
                            matriz[origem][destino] = distancia;
                        }
                    }
                }
        
                long startTime = System.nanoTime();
        
                AlgoritmoOtimo caixeiro = new AlgoritmoOtimo(matriz);
                AlgoritmoOtimo.Resultado resultado = caixeiro.resolver();
        
                long endTime = System.nanoTime();
        
                long durationMs = (endTime - startTime) / 1_000_000;
        
                long minutos = durationMs / 60000;
                long segundos = (durationMs % 60000) / 1000;
                long milissegundos = durationMs % 1000;
        
                resultArea.append("\n--- Resultados do Algoritmo Ótimo ---\n");
                resultArea.append("Quantidade de Cidades: " + numCidadesDesejadas + "\n");
                resultArea.append("Rota Encontrada:\n");
                resultArea.append(Arrays.toString(resultado.getRota()).replaceAll("[\\[\\],]", "") + "\n");
                resultArea.append("Custo Mínimo: " + resultado.getCustoMinimo() + "\n");
                resultArea.append("Tempo de Execução: " + minutos + " minutos, " + segundos + " segundos e " + milissegundos + " milissegundos\n");
            }
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new Menu().setVisible(true);
            }
        });
    }
}