import javax.swing.*;
import java.awt.*;

public class LocadoraApp {
    public static void main(String[] args) {
        JFrame menu = new JFrame("Locadora de Filmes");
        menu.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        menu.setSize(400, 200);
        menu.setLocationRelativeTo(null);

        JButton btnFilmes = new JButton("Cadastro de Filmes");
        JButton btnClientes = new JButton("Cadastro de Clientes");
        JButton btnFuncionarios = new JButton("Cadastro de Funcionários");

        btnFilmes.addActionListener(e -> new TelaFilmes());
        btnClientes.addActionListener(e -> new TelaClientes());
        btnFuncionarios.addActionListener(e -> new TelaFuncionarios());

        JPanel painel = new JPanel(new GridLayout(3, 1, 10, 10));
        painel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));
        painel.add(btnFilmes);
        painel.add(btnClientes);
        painel.add(btnFuncionarios);

        menu.add(painel);
        menu.setVisible(true);
    }
}