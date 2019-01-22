import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class GraphicInterface extends JFrame {

    private JLabel name, surname, schema_name, username, password;
    private JTextField id, data, schema, user, psw;
    private JButton search, update, connect, disconnect;
    private JTextArea answer;

    public GraphicInterface() {
        super("MySQL database searching/updating interface");
        setLayout(null);
        schema_name = new JLabel("Имя схемы jdbc:mysql://localhost:3306/");
        username = new JLabel("Пользователь: ");
        password = new JLabel("Пароль: ");
        schema_name.setBounds(40, 20, 250, 40);
        schema = new JTextField("account");
        schema.setBounds(270, 20, 70, 40);
        username.setBounds(350, 20, 100, 40);
        user = new JTextField("root");
        user.setBounds(450, 20, 70, 40);
        password.setBounds(530, 20, 100, 40);
        psw = new JTextField("кщще");
        psw.setBounds(590, 20, 70, 40);
        connect = new JButton("Подключиться");
        connect.setBounds(670, 20, 130, 40);
        connect.addActionListener(new connectLictener());
        disconnect = new JButton("Отключиться");
        disconnect.setBounds(670, 20, 130, 40);
        disconnect.setVisible(false);
        disconnect.addActionListener(new disconnectListener());
        name = new JLabel("Введите искомый ключ:");
        name.setBounds(40, 80, 250, 40);
        surname = new JLabel("Введите новую фамилию:");
        surname.setBounds(40, 140, 250, 40);
        id = new JTextField();
        id.setBounds(300, 80, 200, 40);
        data = new JTextField();
        data.setBounds(300, 140, 200, 40);
        search = new JButton("Поиск");
        search.setBounds(670, 80, 130, 40);
        search.addActionListener(new searchListener());
        update = new JButton("Изменить");
        update.setBounds(670, 140, 130, 40);
        update.addActionListener(new updateListener());
        answer = new JTextArea();
        JScrollPane jsp = new JScrollPane();
        jsp.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        jsp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        jsp.getViewport().add(answer);
        jsp.getViewport().setPreferredSize(answer.getSize());
        jsp.setBounds(40, 200, 760, 100);

        add(schema_name);
        add(schema);
        add(username);
        add(user);
        add(name);
        add(password);
        add(psw);
        add(connect);
        add(disconnect);
        add(id);
        add(search);
        add(surname);
        add(data);
        add(update);
        add(jsp);
        setSize(850, 400);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setVisible(true);
    }

    public static void main(String[] args) {
        GraphicInterface gf = new GraphicInterface();
    }

    class connectLictener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/" + schema.getText() + "?verifyServerCertificate=false&useSSL=false&requireSSL=false&useLegacyDatetimeCode=false&amp&serverTimezone=UTC", user.getText(), psw.getText());
                MyConnect.setConnection(con);
                answer.setText("Подключён к базе jdbc:mysql://localhost:3306/" + schema.getText() + " под пользователем " + user.getText() + "\n");
                connect.setVisible(false);
                disconnect.setVisible(true);
                schema.setEditable(false);
                user.setEditable(false);
                psw.setEditable(false);
            } catch (SQLException e1) {
                answer.append("Не удалось подключиться к указанной базе данных");
                e1.printStackTrace();
            }
        }
    }

    class disconnectListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                if (!MyConnect.getConnection().isClosed()) {
                    MyConnect.getConnection().close();
                    answer.append("Отключен от jdbc:mysql://localhost:3306/" + schema.getText() + "\n");
                }
                connect.setVisible(true);
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
            disconnect.setVisible(false);
            schema.setEditable(true);
            user.setEditable(true);
            psw.setEditable(true);
        }
    }

    class searchListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                if (MyConnect.getConnection() == null || MyConnect.getConnection().isClosed()) {
                    answer.setText("Вы не подключены к базе!\n");
                } else {
                    if ("".equals(id.getText())) {
                        answer.setText("Не введена строка для поиска\n");
                    } else {
                        PreparedStatement head = MyConnect.getConnection().prepareStatement("SELECT `COLUMN_NAME` FROM `INFORMATION_SCHEMA`.`COLUMNS` WHERE `TABLE_SCHEMA`='" + schema.getText() + "'AND `TABLE_NAME`='acctable';");
                        ResultSet headrs = head.executeQuery();
                        answer.setText("");
                        while (headrs.next()) {
                            answer.append(headrs.getString(1) + "\t|\t");
                        }
                        answer.append("\n");
                        head.close();
                        PreparedStatement preparedStatement = MyConnect.getConnection().prepareStatement("select * from account.acctable where name=\"" + id.getText() + "\";");
                        ResultSet rs = preparedStatement.executeQuery();
                        while (rs.next()) {
                            DatabaseRow row = new DatabaseRow(rs.getString(1), rs.getString(2));
                            answer.append(row + "\n");
                        }
                        rs.last();
                        if (rs.getRow() == 0) {
                            answer.append("\t no rows\n");
                        }
                        preparedStatement.close();
                    }
                }
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
        }
    }

    class updateListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                if (MyConnect.getConnection() == null || MyConnect.getConnection().isClosed()) {
                    answer.setText("Вы не подключены к базе!\n");
                } else {
                    if ("".equals(id.getText()) || "".equals(data.getText())) {
                        answer.setText("Не введена строка для поиска/модификации\n");
                    } else {
                        PreparedStatement update = MyConnect.getConnection().prepareStatement("update account.acctable set surname=? where name = ?;");
                        update.setString(1, data.getText());
                        update.setString(2, id.getText());
                        int howMuch = update.executeUpdate();
                        answer.setText("Изменено " + howMuch + " строк(а)");
                        update.close();
                    }
                }

            } catch (SQLException e1) {
                e1.printStackTrace();
            }
        }
    }
}


