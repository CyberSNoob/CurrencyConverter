import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;
import org.json.simple.JSONObject;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class UserInt extends JFrame {

    private JPanel mainPanel;
    private JTextField amountFrom;
    private JComboBox<String> currencyFrom;
    private JComboBox<String> currencyTo;
    private JButton convertButton;
    private JButton switchButton;
    private JLabel result;

    public UserInt() {
        setUpFrame();
        setUpComboboxes();
        setActionListeners();
        repaint();
    }

    public void setUpFrame(){
        setTitle("Currency converter");
        setContentPane(mainPanel);
        setMinimumSize(new Dimension(500,300));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    private void setUpComboboxes(){
        JsonElement jsonEl = FileHandler.readJsonFile("currencies.json").get("data");
//        String[] currencies = new Gson().fromJson(jsonEl, String[].class);
//        Alternative COMPLICATED WAY
        ArrayList<String> currencyList = new Gson().fromJson(jsonEl, new TypeToken<ArrayList<String>>(){}.getType());
        String[] currencies = currencyList.toArray(new String[0]);
        Arrays.sort(currencies);
        currencyFrom.setModel(new DefaultComboBoxModel<>(currencies));
        currencyFrom.setSelectedItem("EUR");
        currencyTo.setModel(new DefaultComboBoxModel<>(currencies));
        currencyTo.setSelectedItem("USD");
    }

    public void switchCurrencies(){
        String temp = String.valueOf(currencyFrom.getSelectedItem());
        currencyFrom.setSelectedItem(currencyTo.getSelectedItem());
        currencyTo.setSelectedItem(temp);
    }

    public void setActionListeners(){
        getRootPane().setDefaultButton(convertButton);
        convertButton.addActionListener(e -> { convert(); });
        switchButton.addActionListener(e -> { switchCurrencies(); });
    }

    public void convert(){
        if(!amountFrom.getText().isBlank() && amountFrom.getText().matches("^\\d*\\.?\\d+$|^\\d+\\.?\\d*$")){
            try {
                double amount = Double.parseDouble(amountFrom.getText());
                double currencyValue = getCurrencyValue();
                double converted = roundValue(amount*currencyValue);
                result.setText(String.valueOf(converted).formatted("%.2f"));
            }catch (NumberFormatException | IOException nfe) {
                nfe.printStackTrace();
            }
        }
    }

    public double getCurrencyValue() throws IOException {
        HttpRequestProcessor req = new HttpRequestProcessor();
        String url = req.buildCurrencyApiUrl(String.valueOf(currencyFrom.getSelectedItem()),
                List.of(String.valueOf(currencyTo.getSelectedItem())));
        if(url.isBlank()) {
            System.out.println("CurrencyApi url is invalid");
            return 0;
        }
        JSONObject response = req.request(url);
        JSONObject data = (JSONObject) response.get("data");
        JSONObject currency = (JSONObject) data.get(currencyTo.getSelectedItem());
        return (double) currency.get("value");
    }

    public double roundValue(double v){
        BigDecimal bd = BigDecimal.valueOf(v);
        bd = bd.setScale(2, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
}
