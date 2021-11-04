package server;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.beans.property.FloatProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleFloatProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class ChartStage extends Stage {
    
    String admin;
    Label loggedUser = new Label();

    ChartStage chartStage;
    
    private TableView<Chart> tableview = new TableView();
    private TableView<Chart> tableview_persons = new TableView();
    
    private ObservableList data;
    private ObservableList data_persons;
    
    final PieChart chart = new PieChart();
    final PieChart chart_persons = new PieChart();
    
    Label celkemAgencies = new Label();
    Label celkemPrivat = new Label();
    
    DatePicker selectStart = new DatePicker(LocalDate.now());
    DatePicker selectEnd = new DatePicker(LocalDate.now());    

    Text actiontarget;
    int id = 0;
    double count;
    
    AutoReload ar = new AutoReload(id,count,actiontarget); 
    
    public void start(Stage primaryStage) throws ClassNotFoundException, SQLException {
        chartStage = new ChartStage();
    }    
    
    ChartStage() throws ClassNotFoundException, SQLException {
        
        Locale.setDefault(Locale.forLanguageTag("cs-CZ"));
        
        tableview.setPrefWidth(300);
        tableview.setPrefHeight(300);
        
        tableview_persons.setPrefSize(300, 300);
        
        tableview.setId("agency");
        tableview_persons.setId("agency");

                        long months = ChronoUnit.MONTHS.between(
                        selectStart.getValue().withDayOfMonth(1),
                        selectEnd.getValue().withDayOfMonth(1));
        
        buildData(months+1);
        buildTable();
        
        buildData_privatePersons(months+1);
        buildTable_privatePersons();
         

        selectStart.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                
                        long months = ChronoUnit.MONTHS.between(
                        selectStart.getValue().withDayOfMonth(1),
                        selectEnd.getValue().withDayOfMonth(1));
                        System.out.println(months+1);       
                
                try {
                    
                    buildData(months+1);
                    buildData_privatePersons(months+1);
                    buildTable();
                    buildTable_privatePersons();
                    
                    totalPrices_refresh();
                    
                } catch (ClassNotFoundException ex) {
                    Logger.getLogger(ChartStage.class.getName()).log(Level.SEVERE, null, ex);
                } catch (SQLException ex) {
                    Logger.getLogger(ChartStage.class.getName()).log(Level.SEVERE, null, ex);
                }
            }            
        });
        
        selectEnd.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                
                        long months = ChronoUnit.MONTHS.between(
                        selectStart.getValue().withDayOfMonth(1),
                        selectEnd.getValue().withDayOfMonth(1));
                        System.out.println(months+1);       
                
                try {
                    buildData(months+1);
                    buildData_privatePersons(months+1);
                    buildTable();
                    buildTable_privatePersons();
                    
                    totalPrices_refresh();
                    
                } catch (ClassNotFoundException ex) {
                    Logger.getLogger(ChartStage.class.getName()).log(Level.SEVERE, null, ex);
                } catch (SQLException ex) {
                    Logger.getLogger(ChartStage.class.getName()).log(Level.SEVERE, null, ex);
                }
            }            
        });        
        
        this.setTitle(" UBYTOVNA");
        this.getIcons().add(new Image("http://vikinet.cz/4Adam/building.png"));
        this.setOnCloseRequest(new EventHandler<WindowEvent>() {
            public void handle(WindowEvent we) {

                    //ar.stop();
                    Platform.exit();
                    System.exit(0);

            }
        }); 
        
        String tableName = null;        
        tableName = "chartemp";    

        BigDecimal totalPriceCurr = totalPrice(tableName); 
        String totalPriceStr = NumberFormat.getCurrencyInstance().format(totalPriceCurr).toString();

        celkemAgencies.setText(" "+totalPriceStr+" ");
        celkemAgencies.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
        celkemAgencies.setStyle("-fx-background-color: white");
        
        tableName = "chartemp_privatepersons"; 
        
        totalPriceCurr = totalPrice(tableName);           
        totalPriceStr = NumberFormat.getCurrencyInstance().format(totalPriceCurr).toString();        
        
        celkemPrivat.setText(" "+totalPriceStr+" ");
        celkemPrivat.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
        celkemPrivat.setStyle("-fx-background-color: white");
        
        GridPane scene = new GridPane();
        scene.setStyle("-fx-background-color: #e0e0e0;");
        scene.getStylesheets().add("/styles/test.css");

        chart.setTitle("Agentury příjmy");    
        chart_persons.setTitle("Privát příjmy");
        
        chart.setPrefWidth(600);
        chart.setPrefHeight(600);
        
        chart_persons.setPrefSize(600,600);
        
        final HBox logus = new HBox();
        logus.setPadding(new Insets(0,10,0,10));
        logus.getChildren().add(loggedUser);


        final HBox spacing = new HBox();
        spacing.setPadding(new Insets(50,50,50,150));
        spacing.getChildren().add(chart);
        
        
        final HBox selector = new HBox();
        selector.setSpacing(10);
        selector.getChildren().addAll(selectStart,selectEnd);
        
        final HBox chart_table = new HBox();
        chart_table.getChildren().addAll(chart, tableview, chart_persons, tableview_persons);
        chart_table.setPadding(new Insets(55,0,0,0));
        
        final HBox totalPriceAgencies = new HBox();
        totalPriceAgencies.getChildren().add(celkemAgencies);
        totalPriceAgencies.setPadding(new Insets(0,0,0,600));
        
        final HBox totalPricePrivat = new HBox();
        totalPricePrivat.getChildren().add(celkemPrivat);
        totalPricePrivat.setPadding(new Insets(0,0,0,850));
        
        final HBox totalPrices = new HBox();
        totalPrices.getChildren().addAll(totalPriceAgencies,totalPricePrivat);
        
        final VBox FINAL = new VBox();
        FINAL.setPadding(new Insets(10,10,10,10));
        FINAL.setSpacing(10);
        FINAL.getChildren().addAll(selector, chart_table, totalPrices);
        
        scene.add(FINAL, 0, 0);
        scene.setStyle("-fx-background-color: #e0e0e0;");
        
        this.centerOnScreen();
        this.setScene(new Scene(scene, 1800, 900));
        this.show();
        
        ar.start();
   
    }
    
    public void clearData() {
        chart.getData().add(null);
    }
    
    private void clearData_persons() {
        chart_persons.getData().add(null);
    }
    
    private void totalPrices_refresh() throws SQLException {
        String tableName = null;        
        tableName = "chartemp";    

        BigDecimal totalPriceCurr = totalPrice(tableName); 
        String totalPriceStr = NumberFormat.getCurrencyInstance().format(totalPriceCurr).toString();

        celkemAgencies.setText(" "+totalPriceStr+" ");
        celkemAgencies.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));

        
        tableName = "chartemp_privatepersons"; 
        
        totalPriceCurr = totalPrice(tableName);           
        totalPriceStr = NumberFormat.getCurrencyInstance().format(totalPriceCurr).toString();        
        
        celkemPrivat.setText(" "+totalPriceStr+" ");
        celkemPrivat.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));       
    }
        
    private BigDecimal totalPrice(String tableName) throws SQLException {
        
        String select = "SELECT sum(suma) from "+tableName;
        
        Connection connect;
        connect = DBConnect.connect();
        
        ResultSet rese = connect.createStatement().executeQuery(select);
        BigDecimal celkemPrice = null;
        
        while (rese.next()) {
            
            celkemPrice = rese.getBigDecimal(1);
            System.out.println(celkemPrice);
        }
        
        return celkemPrice;
    }
    
    private void buildData_privatePersons(long months) throws ClassNotFoundException, SQLException {
        
        deleteTemp_persons();
        
        String SELECT = "INSERT INTO chartemp_privatepersons (suma,agentura) "
                            + "SELECT SUM(" +        
            "IF(\n" +                                        
            "agentury_test.toggle='/M' OR agentury_test.toggle='M', agentury_test.celkem*"+months+", \n" +                    
            "IF(\n" +
                    "rezervace.ukonceni<'"+selectStart.getValue()+"', agentury_test.sazba*0,"+
                        "IF(\n" +
                        "rezervace.zahajeni<='"+selectStart.getValue()+"' AND '"+selectEnd.getValue()+"'<=rezervace.ukonceni, agentury_test.sazba*DATEDIFF('"+selectStart.getValue()+"','"+selectEnd.getValue().plusDays(1)+"')*-1, \n" +
                        "	IF(\n" +
                        "   rezervace.zahajeni>'"+selectStart.getValue()+"' AND '"+selectEnd.getValue()+"'<=rezervace.ukonceni, agentury_test.sazba*DATEDIFF(rezervace.zahajeni,'"+selectEnd.getValue().plusDays(1)+"')*-1,\n" +
                        "		IF(\n" +
                        "           rezervace.zahajeni<='"+selectStart.getValue()+"' AND '"+selectEnd.getValue()+"'>rezervace.ukonceni, agentury_test.sazba*DATEDIFF('"+selectStart.getValue().minusDays(1)+"',rezervace.ukonceni)*-1,\n" +                      
                        "		IF(rezervace.zahajeni>'"+selectStart.getValue()+"' AND '"+selectEnd.getValue()+"'>rezervace.ukonceni, agentury_test.sazba*DATEDIFF(rezervace.zahajeni,rezervace.ukonceni)*-1,agentury_test.sazba*0)" +                      
                        "		)\n" +
                        "	)\n" +
                        ")\n"+
                        ")\n"+
                    ")) AS sazba, agentury_test.agentura" +
                " FROM ((rezervace INNER JOIN pokoje ON rezervace.idunit=pokoje.idunit)" +
                " INNER JOIN agentury_test ON rezervace.agency=agentury_test.agentura) WHERE agentury_test.toggle='M' GROUP BY agentury_test.agentura";                    
        
        Connection connect;
        
        connect = DBConnect.connect(); 
        int rese = connect.createStatement().executeUpdate(SELECT);

    // kontrola ulozeni
    
        String minusOut = "UPDATE chartemp_privatepersons SET suma=0 WHERE suma<0";
        
        Connection conn;
        conn = DBConnect.connect();
        
        int resultSet = conn.createStatement().executeUpdate(minusOut);
        
        Connection c;
        data_persons = FXCollections.observableArrayList();
        
        try {
            c = DBConnect.connect();

            String TEST_persons = "SELECT suma, agentura FROM chartemp_privatepersons";
            
            ResultSet rs = c.createStatement().executeQuery(TEST_persons);            
            while (rs.next()) {
                data_persons.add(new PieChart.Data(rs.getString(2),rs.getDouble(1)));   
            }
            
        } catch (SQLException ex) {
            Logger.getLogger(ChartStage.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception e) {
              System.out.println("Error on DB connection");
              return;            
        }  
        chart_persons.getData().clear();
        chart_persons.getData().addAll(data_persons);        
        
    }    
    public void buildData(long months) throws ClassNotFoundException, SQLException{
       
        deleteTemp();
    
        String SELECT = "INSERT INTO chartemp (suma,agentura) "
                    + "SELECT SUM(" +        
    "IF(\n" +                                        
    "agentury_test.toggle='/M' OR agentury_test.toggle='M', agentury_test.celkem*"+months+", \n" +                    
    "IF(\n" +
            "rezervace.ukonceni<'"+selectStart.getValue()+"', agentury_test.sazba*0,"+
                "IF(\n" +
                "rezervace.zahajeni<='"+selectStart.getValue()+"' AND '"+selectEnd.getValue()+"'<=rezervace.ukonceni, agentury_test.sazba*DATEDIFF('"+selectStart.getValue()+"','"+selectEnd.getValue().plusDays(1)+"')*-1, \n" +
                "	IF(\n" +
                "   rezervace.zahajeni>'"+selectStart.getValue()+"' AND '"+selectEnd.getValue()+"'<=rezervace.ukonceni, agentury_test.sazba*DATEDIFF(rezervace.zahajeni,'"+selectEnd.getValue().plusDays(1)+"')*-1,\n" +
                "		IF(\n" +
                "           rezervace.zahajeni<='"+selectStart.getValue()+"' AND '"+selectEnd.getValue()+"'>rezervace.ukonceni, agentury_test.sazba*DATEDIFF('"+selectStart.getValue().minusDays(1)+"',rezervace.ukonceni)*-1,\n" +                      
                "		IF(rezervace.zahajeni>'"+selectStart.getValue()+"' AND '"+selectEnd.getValue()+"'>rezervace.ukonceni, agentury_test.sazba*DATEDIFF(rezervace.zahajeni,rezervace.ukonceni)*-1,agentury_test.sazba*0)" +                      
                "		)\n" +
                "	)\n" +
                ")\n"+
                ")\n"+
            ")) AS sazba, agentury_test.agentura" +
        " FROM ((rezervace INNER JOIN pokoje ON rezervace.idunit=pokoje.idunit)" +
        " INNER JOIN agentury_test ON rezervace.agency=agentury_test.agentura) WHERE agentury_test.toggle='D' GROUP BY agentury_test.agentura";                    

    // vykonat ulozeni
            
        Connection connect;        
        connect = DBConnect.connect();  
        
        int rese = connect.createStatement().executeUpdate(SELECT);
        
    // kontrola ulozeni
    
        String minusOut = "UPDATE chartemp SET suma=0 WHERE suma<0";
        
        Connection conn;
        conn = DBConnect.connect();
        
        int resultSet = conn.createStatement().executeUpdate(minusOut);
        
        Connection c;
        data = FXCollections.observableArrayList();
        
        try {
            c = DBConnect.connect();

            String TEST = "SELECT suma, agentura FROM chartemp";
            
            ResultSet rs = c.createStatement().executeQuery(TEST);            
            while (rs.next()) {
                data.add(new PieChart.Data(rs.getString(2),rs.getDouble(1)));   
            }
            
        } catch (SQLException ex) {
            Logger.getLogger(ChartStage.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception e) {
              System.out.println("Error on DB connection");
              return;            
        }  
        chart.getData().clear();
        chart.getData().addAll(data);
    }
    
    private void buildTable_privatePersons() throws SQLException {
        tableview_persons.getItems().clear();
        tableview_persons.getColumns().clear();
        
        String select = "SELECT idchart, suma, agentura FROM chartemp_privatepersons ORDER BY suma DESC";   
        Connection c;    
        c = DBConnect.connect();
        
        ResultSet resultSet = c.createStatement().executeQuery(select);        
        ObservableList dataTable = FXCollections.observableArrayList(dataBaseArrayList(resultSet));
        
        for(int i=0; i<resultSet.getMetaData().getColumnCount(); i++) {
            TableColumn column = new TableColumn();
            switch (resultSet.getMetaData().getColumnName(i+1)) {
                
                case "idchart":
                    column.setText("idchart");
                    column.setVisible(false);
                    break;
                case "suma":
                    column.setText("suma");
                    column.setStyle("-fx-alignment: CENTER-RIGHT");
                    break;
                case "agentura":
                    column.setText("ubytovaný");
                    break;
                    
                default: column.setText(resultSet.getMetaData().getColumnName(i+1));
                    break;
            }
            column.setCellValueFactory(new PropertyValueFactory<>(resultSet.getMetaData().getColumnName(i+1)));
            tableview_persons.getColumns().add(column);
        }
        tableview_persons.setItems(dataTable);        
        
    }     
    public void buildTable() throws ClassNotFoundException, SQLException {
        
        tableview.getItems().clear();
        tableview.getColumns().clear();
        
        String select = "SELECT idchart, suma, agentura FROM chartemp ORDER BY suma DESC";
     
        Connection c;       
        c = DBConnect.connect();
        
        ResultSet resultSet = c.createStatement().executeQuery(select);
        
        ObservableList dataTable = FXCollections.observableArrayList(dataBaseArrayList(resultSet));
        
        for(int i=0; i<resultSet.getMetaData().getColumnCount(); i++) {
            TableColumn column = new TableColumn();
            switch (resultSet.getMetaData().getColumnName(i+1)) {
                
                case "idchart":
                    column.setText("idchart");
                    column.setVisible(false);
                    break;
                case "suma":
                    column.setText("suma");
                    column.setStyle("-fx-alignment: CENTER-RIGHT");
                    break;
                case "agentura":
                    column.setText("agentura");
                    break;
                    
                default: column.setText(resultSet.getMetaData().getColumnName(i+1));
                    break;
            }
            column.setCellValueFactory(new PropertyValueFactory<>(resultSet.getMetaData().getColumnName(i+1)));
            tableview.getColumns().add(column);
        }
        tableview.setItems(dataTable);
    }
    
    public void LoggedUser(String userName) {
        
        loggedUser.setText(userName);
        admin = userName;                
    }    
    
    private void deleteTemp_persons() throws SQLException {
        String delTemp_persons = "DELETE FROM chartemp_privatepersons";
        
        Connection c;
        c = DBConnect.connect();
        
        int rest_persons = c.createStatement().executeUpdate(delTemp_persons);        
    }

    public void deleteTemp() throws ClassNotFoundException, SQLException {
        String delTemp = "DELETE FROM chartemp";       
        
        Connection con;
        con = DBConnect.connect();
        
        int rest = con.createStatement().executeUpdate(delTemp);

    }
    
    public class Chart {
        IntegerProperty idchart = new SimpleIntegerProperty();
        FloatProperty suma = new SimpleFloatProperty();
        StringProperty agentura = new SimpleStringProperty();
        
        public IntegerProperty idchartProperty() {
            return idchart;
        }
        public final int getIdchart() {
            return idchartProperty().get();
        }
        public final void setIdchart(int val) {
            idchartProperty().set(val);
        }        
        
        public FloatProperty sumaProperty() {
            return suma;
        }
        public final float getSuma() {
            return sumaProperty().get();
        }
        public final void setSuma(int val) {
            sumaProperty().set(val);
        }
        
        public StringProperty agenturaProperty() {
            return agentura;
        }
        public final String getAgentura() {
            return agenturaProperty().get();
        } 
        public final void setAgentura(String value) {
            agenturaProperty().set(value);
        }
        
        public Chart(int idchartVal, float sumaVal, String agenturaValue) {
            idchart.set(idchartVal);
            suma.set(sumaVal);
            agentura.set(agenturaValue);
        }
        Chart(){}
    }
    private ArrayList dataBaseArrayList(ResultSet rs) throws SQLException {
        ArrayList<Chart> data4table = new ArrayList<>();
        while (rs.next()) {
            Chart chart = new Chart();
            
            chart.idchart.set(rs.getInt("idchart"));
            chart.suma.set(rs.getFloat("suma"));
            chart.agentura.set(rs.getString("agentura"));
            
            data4table.add(chart);
        }
        return data4table; 
    }

}
