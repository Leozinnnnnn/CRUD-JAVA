import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.ArrayList;

public class TelaFuncionarios extends JFrame {
    private JTextField tfNome, tfCargo, tfIdade;
    private JTextArea taFuncionarios;
    private JButton btnAdicionar, btnEditar, btnDeletar;

    private int idSelecionado = -1;
    private java.util.List<Integer> listaIds = new ArrayList<>();

    public TelaFuncionarios() {
        setTitle("Cadastro de Funcionários");
        setSize(400, 450);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        tfNome = new JTextField(15);
        tfCargo = new JTextField(15);
        tfIdade = new JTextField(15);
        taFuncionarios = new JTextArea(10, 30);
        taFuncionarios.setEditable(false);

        btnAdicionar = new JButton("Adicionar");
        btnEditar = new JButton("Editar");
        btnDeletar = new JButton("Deletar");

        // Painel principal com BorderLayout
        JPanel painelPrincipal = new JPanel(new BorderLayout());
        painelPrincipal.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Painel do formulário com GridBagLayout
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("Nome:"), gbc);
        gbc.gridx = 1;
        formPanel.add(tfNome, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(new JLabel("Cargo:"), gbc);
        gbc.gridx = 1;
        formPanel.add(tfCargo, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(new JLabel("Idade:"), gbc);
        gbc.gridx = 1;
        formPanel.add(tfIdade, gbc);

        // Painel dos botões
        JPanel botoesPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        botoesPanel.add(btnAdicionar);
        botoesPanel.add(btnEditar);
        botoesPanel.add(btnDeletar);

        JScrollPane scrollArea = new JScrollPane(taFuncionarios);

        painelPrincipal.add(formPanel, BorderLayout.NORTH);
        painelPrincipal.add(botoesPanel, BorderLayout.CENTER);
        painelPrincipal.add(scrollArea, BorderLayout.SOUTH);

        add(painelPrincipal);

        // Listeners
        btnAdicionar.addActionListener(e -> {
            adicionarFuncionario(tfNome.getText(), tfCargo.getText(), tfIdade.getText());
            limparCampos();
            carregarFuncionarios();
        });

        btnEditar.addActionListener(e -> {
            if (idSelecionado != -1) {
                editarFuncionario(tfNome.getText(), tfCargo.getText(), tfIdade.getText());
                limparCampos();
                carregarFuncionarios();
            } else {
                JOptionPane.showMessageDialog(this, "Selecione um funcionário da lista para editar.");
            }
        });

        btnDeletar.addActionListener(e -> {
            if (idSelecionado != -1) {
                deletarFuncionario();
                limparCampos();
                carregarFuncionarios();
            } else {
                JOptionPane.showMessageDialog(this, "Selecione um funcionário da lista para deletar.");
            }
        });

        taFuncionarios.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int linha = taFuncionarios.getCaretPosition();
                int linhaIndex = taFuncionarios.getText().substring(0, linha).split("\n").length - 1;
                if (linhaIndex >= 0 && linhaIndex < listaIds.size()) {
                    carregarFuncionarioPorId(listaIds.get(linhaIndex));
                }
            }
        });

        carregarFuncionarios();
        setVisible(true);
    }

    private void carregarFuncionarios() {
        taFuncionarios.setText("");
        listaIds.clear();
        try (Connection con = ConexãoBD.getConnection();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT id, nome, cargo, idade FROM funcionarios")) {
            while (rs.next()) {
                int id = rs.getInt("id");
                listaIds.add(id);
                // Mostra apenas nome, cargo e idade (sem o ID)
                String linha = rs.getString("nome") + " - " + 
                               rs.getString("cargo") + " - " + rs.getInt("idade");
                taFuncionarios.append(linha + "\n");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro: " + e.getMessage());
        }
    }

    private void carregarFuncionarioPorId(int id) {
        try (Connection con = ConexãoBD.getConnection();
             PreparedStatement ps = con.prepareStatement("SELECT * FROM funcionarios WHERE id = ?")) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                tfNome.setText(rs.getString("nome"));
                tfCargo.setText(rs.getString("cargo"));
                tfIdade.setText(String.valueOf(rs.getInt("idade")));
                idSelecionado = id;
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro: " + e.getMessage());
        }
    }

    private void adicionarFuncionario(String nome, String cargo, String idadeStr) {
        if (nome.isEmpty() || cargo.isEmpty() || idadeStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Preencha todos os campos!");
            return;
        }
        try {
            int idade = Integer.parseInt(idadeStr);
            try (Connection con = ConexãoBD.getConnection();
                 PreparedStatement ps = con.prepareStatement("INSERT INTO funcionarios (nome, cargo, idade) VALUES (?, ?, ?)")) {
                ps.setString(1, nome);
                ps.setString(2, cargo);
                ps.setInt(3, idade);
                ps.executeUpdate();
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Idade deve ser um número válido!");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao adicionar: " + e.getMessage());
        }
    }

    private void editarFuncionario(String nome, String cargo, String idadeStr) {
        if (nome.isEmpty() || cargo.isEmpty() || idadeStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Preencha todos os campos!");
            return;
        }
        try {
            int idade = Integer.parseInt(idadeStr);
            try (Connection con = ConexãoBD.getConnection();
                 PreparedStatement ps = con.prepareStatement("UPDATE funcionarios SET nome=?, cargo=?, idade=? WHERE id=?")) {
                ps.setString(1, nome);
                ps.setString(2, cargo);
                ps.setInt(3, idade);
                ps.setInt(4, idSelecionado);
                ps.executeUpdate();
                idSelecionado = -1;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Idade deve ser um número válido!");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao editar: " + e.getMessage());
        }
    }

    private void deletarFuncionario() {
        try (Connection con = ConexãoBD.getConnection();
             PreparedStatement ps = con.prepareStatement("DELETE FROM funcionarios WHERE id=?")) {
            ps.setInt(1, idSelecionado);
            ps.executeUpdate();
            idSelecionado = -1;
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao deletar: " + e.getMessage());
        }
    }

    private void limparCampos() {
        tfNome.setText("");
        tfCargo.setText("");
        tfIdade.setText("");
        idSelecionado = -1;
    }
}