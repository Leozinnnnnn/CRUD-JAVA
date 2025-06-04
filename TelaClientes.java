import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.ArrayList;

public class TelaClientes extends JFrame {
    private JTextField tfNome;
    private JTextField tfEmail;
    private JTextField tfCPF;
    private JTextArea taClientes;
    private JButton btnAdicionar, btnEditar, btnDeletar;

    private String cpfSelecionado = null;
    private java.util.List<String> listaCPFs = new ArrayList<>();

    public TelaClientes() {
        setTitle("Cadastro de Clientes");
        setSize(400, 450);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        tfNome = new JTextField(15);
        tfEmail = new JTextField(15);
        tfCPF = new JTextField(15);
        taClientes = new JTextArea(10, 30);
        taClientes.setEditable(false);

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
        formPanel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1;
        formPanel.add(tfEmail, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(new JLabel("CPF:"), gbc);
        gbc.gridx = 1;
        formPanel.add(tfCPF, gbc);

        // Painel dos botões
        JPanel botoesPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        botoesPanel.add(btnAdicionar);
        botoesPanel.add(btnEditar);
        botoesPanel.add(btnDeletar);

        JScrollPane scrollArea = new JScrollPane(taClientes);

        painelPrincipal.add(formPanel, BorderLayout.NORTH);
        painelPrincipal.add(botoesPanel, BorderLayout.CENTER);
        painelPrincipal.add(scrollArea, BorderLayout.SOUTH);

        add(painelPrincipal);

        // Listeners
        btnAdicionar.addActionListener(e -> {
            adicionarCliente(tfNome.getText(), tfEmail.getText(), tfCPF.getText());
            limparCampos();
            carregarClientes();
        });

        btnEditar.addActionListener(e -> {
            if (cpfSelecionado != null) {
                editarCliente(tfNome.getText(), tfEmail.getText(), tfCPF.getText());
                limparCampos();
                carregarClientes();
            } else {
                JOptionPane.showMessageDialog(this, "Selecione um cliente da lista para editar.");
            }
        });

        btnDeletar.addActionListener(e -> {
            if (cpfSelecionado != null) {
                deletarCliente(cpfSelecionado);
                limparCampos();
                carregarClientes();
            } else {
                JOptionPane.showMessageDialog(this, "Selecione um cliente da lista para deletar.");
            }
        });

        taClientes.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int linha = taClientes.getCaretPosition();
                int linhaIndex = taClientes.getText().substring(0, linha).split("\n").length - 1;
                if (linhaIndex >= 0 && linhaIndex < listaCPFs.size()) {
                    carregarClientePorCPF(listaCPFs.get(linhaIndex));
                }
            }
        });

        carregarClientes();
        setVisible(true);
    }

    private void carregarClientes() {
        taClientes.setText("");
        listaCPFs.clear();
        try (Connection con = ConexãoBD.getConnection();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT nome, email, cpf FROM clientes")) {
            while (rs.next()) {
                String nome = rs.getString("nome");
                String email = rs.getString("email");
                String cpf = rs.getString("cpf");
                listaCPFs.add(cpf);
                taClientes.append(nome + " - " + email + " - " + cpf + "\n");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro: " + e.getMessage());
        }
    }

    private void carregarClientePorCPF(String cpf) {
        try (Connection con = ConexãoBD.getConnection();
             PreparedStatement ps = con.prepareStatement("SELECT * FROM clientes WHERE cpf = ?")) {
            ps.setString(1, cpf);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                tfNome.setText(rs.getString("nome"));
                tfEmail.setText(rs.getString("email"));
                tfCPF.setText(rs.getString("cpf"));
                cpfSelecionado = cpf;
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro: " + e.getMessage());
        }
    }

    private void adicionarCliente(String nome, String email, String cpf) {
        if (nome.isEmpty() || email.isEmpty() || cpf.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Preencha todos os campos!");
            return;
        }
        try (Connection con = ConexãoBD.getConnection();
             PreparedStatement ps = con.prepareStatement("INSERT INTO clientes (nome, email, cpf) VALUES (?, ?, ?)")) {
            ps.setString(1, nome);
            ps.setString(2, email);
            ps.setString(3, cpf);
            ps.executeUpdate();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao adicionar: " + e.getMessage());
        }
    }

    private void editarCliente(String nome, String email, String cpf) {
        if (nome.isEmpty() || email.isEmpty() || cpf.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Preencha todos os campos!");
            return;
        }
        try (Connection con = ConexãoBD.getConnection();
             PreparedStatement ps = con.prepareStatement("UPDATE clientes SET nome=?, email=?, cpf=? WHERE cpf=?")) {
            ps.setString(1, nome);
            ps.setString(2, email);
            ps.setString(3, cpf);
            ps.setString(4, cpfSelecionado);
            ps.executeUpdate();
            cpfSelecionado = null;
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao editar: " + e.getMessage());
        }
    }

    private void deletarCliente(String cpf) {
        try (Connection con = ConexãoBD.getConnection();
             PreparedStatement ps = con.prepareStatement("DELETE FROM clientes WHERE cpf=?")) {
            ps.setString(1, cpf);
            ps.executeUpdate();
            cpfSelecionado = null;
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao deletar: " + e.getMessage());
        }
    }

    private void limparCampos() {
        tfNome.setText("");
        tfEmail.setText("");
        tfCPF.setText("");
        cpfSelecionado = null;
    }
}