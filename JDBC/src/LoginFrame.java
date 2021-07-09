import javax.swing.*;
import java.awt.*;
import java.io.FileInputStream;
import java.util.Properties;

public class LoginFrame {
    private final String PROP_FILE = "mysql.ini";
    private String driver;
    private String url;
    private String user;
    private String pass;
    private JFrame jf = new JFrame("登录");
    private JTextField userField = new JTextField(20);
    private JTextField passField = new JTextField(20);
    private JButton loginButton = new JButton("登录");

    public void init() throws Exception{
        Properties connProp = new Properties();
        connProp.load(new FileInputStream(PROP_FILE));
        driver = connProp.getProperty("driver");
        url = connProp.getProperty("url");
        user = connProp.getProperty("user");
        pass = connProp.getProperty("pass");
        Class.forName(driver);
        loginButton.addActionListener(e -> {
            if (validate(userField.getText(),passField.getText())){
                JOptionPane.showMessageDialog(jf,"登陆成功");
            }else {
                JOptionPane.showMessageDialog(jf,"登陆失败");
            }
        });
        jf.add(userField, BorderLayout.NORTH);
        jf.add(passField);
        jf.add(loginButton,BorderLayout.SOUTH);
        jf.pack();
        jf.setVisible(true);
    }

    private boolean validate(String userName, String userPass){
        return false;
    }

    public static void main(String[] args) throws Exception {
        new LoginFrame().init();
    }
}
