/*
AUTORES:
        JHON STIVEN MAFLA HENAO 202160295
        DANIEL CAMELO CASTRO 202159908
        SANTIAGO PEREZ PINO 201968143
 */

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;

// Clase que representa un nodo en el árbol de Huffman
class HuffmanNode implements Comparable<HuffmanNode> {
    char data;
    int frequency;
    HuffmanNode left, right;

    public HuffmanNode(char data, int frequency) {
        this.data = data;
        this.frequency = frequency;
    }

    @Override
    public int compareTo(HuffmanNode other) {
        return this.frequency - other.frequency;
    }
}

// Clase principal que implementa la interfaz gráfica de usuario (GUI)
class HuffmanCodingGUI extends JFrame {
    private JTextArea inputTextArea;
    private JTextArea outputTextArea;
    private JTable treeTable;
    private JTable codesTable;

    private Map<Character, String> huffmanCodes = new HashMap<>();

    // Constructor de la GUI
    public HuffmanCodingGUI() {
        super("Huffman Coding");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Componentes de la GUI
        inputTextArea = new JTextArea(5, 20);
        JButton encodeButton = new JButton("Encode");
        JButton decodeButton = new JButton("Decode");
        JButton loadFileButton = new JButton("Load File");
        outputTextArea = new JTextArea(5, 20);

        // Modelos de tabla para el árbol y los códigos de Huffman
        DefaultTableModel treeTableModel = new DefaultTableModel(new Object[]{"Character", "Frequency"}, 0);
        treeTable = new JTable(treeTableModel);
        treeTable.setPreferredScrollableViewportSize(new Dimension(250, 150));

        DefaultTableModel codesTableModel = new DefaultTableModel(new Object[]{"Character", "Huffman Code"}, 0);
        codesTable = new JTable(codesTableModel);
        codesTable.setPreferredScrollableViewportSize(new Dimension(250, 150));

        JScrollPane treeScrollPane = new JScrollPane(treeTable);
        JScrollPane codesScrollPane = new JScrollPane(codesTable);

        // Eventos de los botones
        encodeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String inputText = inputTextArea.getText();
                Map<Character, Integer> frequencyMap = buildFrequencyMap(inputText);
                HuffmanNode root = buildHuffmanTree(frequencyMap);
                generateHuffmanCodes(root, new StringBuilder());
                updateTreeTable(frequencyMap);
                updateCodesTable();
                String encodedText = encode(inputText);
                outputTextArea.setText("Texto Codificado: " + encodedText);
            }
        });

        decodeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String encodedText = outputTextArea.getText().replace("Texto Codificado: ", "");
                String decodedText = decode(encodedText, buildHuffmanTree(buildFrequencyMap(inputTextArea.getText())));
                outputTextArea.setText("Texto Decodificado: " + decodedText);
            }
        });

        loadFileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                int result = fileChooser.showOpenDialog(HuffmanCodingGUI.this);

                if (result == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = fileChooser.getSelectedFile();
                    try {
                        String fileContent = readFile(selectedFile);
                        inputTextArea.setText(fileContent);
                        updateTreeTable(buildFrequencyMap(fileContent));
                        updateCodesTable();
                    } catch (IOException ex) {
                        JOptionPane.showMessageDialog(HuffmanCodingGUI.this, "Error al leer el archivo.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });

        // Diseño de la interfaz gráfica
        JPanel panel = new JPanel();
        panel.add(new JLabel("Texto de Entrada:"));
        panel.add(new JScrollPane(inputTextArea));
        panel.add(loadFileButton);
        panel.add(encodeButton);
        panel.add(decodeButton);
        panel.add(new JLabel("Texto de Salida:"));
        panel.add(new JScrollPane(outputTextArea));

        JPanel treePanel = new JPanel();
        treePanel.add(new JLabel("Árbol de Huffman:"));
        treePanel.add(treeScrollPane);

        JPanel codesPanel = new JPanel();
        codesPanel.add(new JLabel("Códigos de Huffman:"));
        codesPanel.add(codesScrollPane);

        Container container = getContentPane();
        container.setLayout(new BorderLayout());
        container.add(panel, BorderLayout.NORTH);

        JPanel tablesPanel = new JPanel(new GridLayout(1, 2));
        tablesPanel.add(treePanel);
        tablesPanel.add(codesPanel);
        container.add(tablesPanel, BorderLayout.CENTER);

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private Map<Character, Integer> buildFrequencyMap(String text) {
        Map<Character, Integer> frequencyMap = new HashMap<>();
        for (char c : text.toCharArray()) {
            frequencyMap.put(c, frequencyMap.getOrDefault(c, 0) + 1);
        }
        return frequencyMap;
    }

    private HuffmanNode buildHuffmanTree(Map<Character, Integer> frequencyMap) {
        PriorityQueue<HuffmanNode> priorityQueue = new PriorityQueue<>();
        for (Map.Entry<Character, Integer> entry : frequencyMap.entrySet()) {
            priorityQueue.offer(new HuffmanNode(entry.getKey(), entry.getValue()));
        }

        while (priorityQueue.size() > 1) {
            HuffmanNode left = priorityQueue.poll();
            HuffmanNode right = priorityQueue.poll();

            HuffmanNode parent = new HuffmanNode('\0', left.frequency + right.frequency);
            parent.left = left;
            parent.right = right;

            priorityQueue.offer(parent);
        }

        return priorityQueue.poll();
    }

    private void generateHuffmanCodes(HuffmanNode root, StringBuilder code) {
        if (root != null) {
            if (root.data != '\0') {
                huffmanCodes.put(root.data, code.toString());
            }

            generateHuffmanCodes(root.left, code.append('0'));
            code.deleteCharAt(code.length() - 1);

            generateHuffmanCodes(root.right, code.append('1'));
            code.deleteCharAt(code.length() - 1);
        }
    }

    private String encode(String text) {
        StringBuilder encodedText = new StringBuilder();
        for (char c : text.toCharArray()) {
            encodedText.append(huffmanCodes.get(c));
        }
        return encodedText.toString();
    }

    private String decode(String encodedText, HuffmanNode root) {
        StringBuilder decodedText = new StringBuilder();
        HuffmanNode current = root;
        for (char bit : encodedText.toCharArray()) {
            if (bit == '0') {
                current = current.left;
            } else {
                current = current.right;
            }

            if (current.data != '\0') {
                decodedText.append(current.data);
                current = root;
            }
        }
        return decodedText.toString();
    }

    private String readFile(File file) throws IOException {
        StringBuilder content = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
        }
        return content.toString();
    }

    private void updateTreeTable(Map<Character, Integer> frequencyMap) {
        DefaultTableModel model = (DefaultTableModel) treeTable.getModel();
        model.setRowCount(0);

        for (Map.Entry<Character, Integer> entry : frequencyMap.entrySet()) {
            model.addRow(new Object[]{entry.getKey(), entry.getValue()});
        }
    }

    private void updateCodesTable() {
        DefaultTableModel model = (DefaultTableModel) codesTable.getModel();
        model.setRowCount(0);

        for (Map.Entry<Character, String> entry : huffmanCodes.entrySet()) {
            model.addRow(new Object[]{entry.getKey(), entry.getValue()});
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new HuffmanCodingGUI());
    }
}
