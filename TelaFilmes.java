import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.ArrayList;

public class TelaFilmes extends JFrame {
    private JTextField tfTitulo;
    private JTextField tfGenero;
    private JTextField tfAno;
    private JTextArea taFilmes;
    private JButton btnAdicionar, btnEditar, btnDeletar;

    private int idSelecionado = -1; // ID do filme selecionado
    private java.util.List<Integer> listaIds = new ArrayList<>();

    public TelaFilmes() {
        setTitle("Cadastro de Filmes");
        setSize(400, 450);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        tfTitulo = new JTextField(15);  // tamanho menor
        tfGenero = new JTextField(15);
        tfAno = new JTextField(15);
        taFilmes = new JTextArea(10, 30);
        taFilmes.setEditable(false);

        btnAdicionar = new JButton("Adicionar");
        btnEditar = new JButton("Editar");
        btnDeletar = new JButton("Deletar");

        // Painel principal com BorderLayout para centralizar o form
        JPanel painelPrincipal = new JPanel(new BorderLayout());
        painelPrincipal.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Painel do formulário centralizado usando FlowLayout alinhado ao centro
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5,5,5,5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("Título:"), gbc);
        gbc.gridx = 1;
        formPanel.add(tfTitulo, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(new JLabel("Gênero:"), gbc);
        gbc.gridx = 1;
        formPanel.add(tfGenero, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(new JLabel("Ano:"), gbc);
        gbc.gridx = 1;
        formPanel.add(tfAno, gbc);

        // Painel dos botões abaixo do formulário
        JPanel botoesPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        botoesPanel.add(btnAdicionar);
        botoesPanel.add(btnEditar);
        botoesPanel.add(btnDeletar);

        // Área de texto com lista de filmes, com scroll
        JScrollPane scrollArea = new JScrollPane(taFilmes);

        painelPrincipal.add(formPanel, BorderLayout.NORTH);
        painelPrincipal.add(botoesPanel, BorderLayout.CENTER);
        painelPrincipal.add(scrollArea, BorderLayout.SOUTH);

        add(painelPrincipal);

        btnAdicionar.addActionListener(e -> {
            adicionarFilme(tfTitulo.getText(), tfGenero.getText(), tfAno.getText());
            limparCampos();
            carregarFilmes();
        });

        btnEditar.addActionListener(e -> {
            if (idSelecionado != -1) {
                editarFilme(idSelecionado, tfTitulo.getText(), tfGenero.getText(), tfAno.getText());
                limparCampos();
                carregarFilmes();
            } else {
                JOptionPane.showMessageDialog(this, "Selecione um item da lista para editar.");
            }
        });

        btnDeletar.addActionListener(e -> {
            if (idSelecionado != -1) {
                deletarFilme(idSelecionado);
                limparCampos();
                carregarFilmes();
            } else {
                JOptionPane.showMessageDialog(this, "Selecione um item da lista para deletar.");
            }
        });

        taFilmes.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int linha = taFilmes.getCaretPosition();
                int linhaIndex = taFilmes.getText().substring(0, linha).split("\n").length - 1;
                if (linhaIndex >= 0 && linhaIndex < listaIds.size()) {
                    int id = listaIds.get(linhaIndex);
                    carregarFilmePorId(id);
                }
            }
        });

        carregarFilmes();
        setVisible(true);
    }

    private void carregarFilmes() {
        taFilmes.setText("");
        listaIds.clear();
        try (Connection con = ConexãoBD.getConnection();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT id, titulo, genero, ano FROM filmes")) {
            while (rs.next()) {
                int id = rs.getInt("id");
                listaIds.add(id);
                String linha = rs.getString("titulo") + " - " + rs.getString("genero") + " - " + rs.getInt("ano");
                taFilmes.append(linha + "\n");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro: " + e.getMessage());
        }
    }

    private void carregarFilmePorId(int id) {
        try (Connection con = ConexãoBD.getConnection();
             PreparedStatement ps = con.prepareStatement("SELECT * FROM filmes WHERE id = ?")) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                tfTitulo.setText(rs.getString("titulo"));
                tfGenero.setText(rs.getString("genero"));
                tfAno.setText(String.valueOf(rs.getInt("ano")));
                idSelecionado = id;
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro: " + e.getMessage());
        }
    }

    private void adicionarFilme(String titulo, String genero, String anoStr) {
        if (titulo.isEmpty() || genero.isEmpty() || anoStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Preencha todos os campos!");
            return;
        }
        try {
            int ano = Integer.parseInt(anoStr);
            Connection con = ConexãoBD.getConnection();
            PreparedStatement ps = con.prepareStatement("INSERT INTO filmes (titulo, genero, ano) VALUES (?, ?, ?)");
            ps.setString(1, titulo);
            ps.setString(2, genero);
            ps.setInt(3, ano);
            ps.executeUpdate();
            con.close();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao adicionar: " + e.getMessage());
        }
    }

    private void editarFilme(int id, String titulo, String genero, String anoStr) {
        if (titulo.isEmpty() || genero.isEmpty() || anoStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Preencha todos os campos!");
            return;
        }
        try {
            int ano = Integer.parseInt(anoStr);
            Connection con = ConexãoBD.getConnection();
            PreparedStatement ps = con.prepareStatement("UPDATE filmes SET titulo = ?, genero = ?, ano = ? WHERE id = ?");
            ps.setString(1, titulo);
            ps.setString(2, genero);
            ps.setInt(3, ano);
            ps.setInt(4, id);
            ps.executeUpdate();
            con.close();
            idSelecionado = -1;
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao editar: " + e.getMessage());
        }
    }

    private void deletarFilme(int id) {
        try {
            Connection con = ConexãoBD.getConnection();
            PreparedStatement ps = con.prepareStatement("DELETE FROM filmes WHERE id = ?");
            ps.setInt(1, id);
            ps.executeUpdate();
            con.close();
            idSelecionado = -1;
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao deletar: " + e.getMessage());
        }
    }

    private void limparCampos() {
        tfTitulo.setText("");
        tfGenero.setText("");
        tfAno.setText("");
        idSelecionado = -1;
    }
}