import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.text.DecimalFormat;
import java.util.Stack;


public class App extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    //DecimalFormat df = new DecimalFormat(".####");
    private int layer = 0;
    private String input = "";
    private Stack<Double> stack = new Stack<>();

    private VBox LayerBox = new VBox();
    private TextField [] layerAr = new TextField[4];
    private Label [] labelAr = new Label [4];
    //private Label consoleL = new Label("console");
    //private TextField console = new TextField();

    private VBox IOBox = new VBox();
    private TextField inputArea = new TextField();
    private TextField outputArea = new TextField();
    private Label inputLabel = new Label("Input");
    private Label outputLabel = new Label("Output");

    private BorderPane root = new BorderPane();
    private GridPane calcPane = new GridPane();
    private Button[] btns = new Button[20];

    public void start(Stage primaryStage) {

        primaryStage.setTitle("Upn Calculator");
        String[] btnNames =
                {
                        "1", "2", "3", "+",
                        "4", "5", "6", "-",
                        "7", "8", "9", "*",
                        "\u23CE", "0", ",", "/",
                        "\u2423", "CE", "\u2190","INV"
                };
        for (int i = 0; i < btnNames.length; i++) {
            Button btn = new Button(btnNames[i]);
            btns[i] = btn;
            btn.setPrefSize(50, 50);
            calcPane.add(btn, i % 4, (int) Math.ceil(i / 4));
        }


        // Put everything together

        for (int i = 0; i < layerAr.length; i++) {
            layerAr[i] = new TextField();
        }

        for (int i = 3; i >= 0; i--) {
            labelAr[i] = new Label("Layer" + (i+1));
        }

        IOBox.getChildren().addAll(inputLabel, inputArea, outputLabel, outputArea);
        IOBox.setSpacing(5.0);

        LayerBox.getChildren().addAll(labelAr[3], layerAr[3], labelAr[2], layerAr[2], labelAr[1], layerAr[1], labelAr[0], layerAr[0]);
        LayerBox.setSpacing(5.0);


        root.setTop(IOBox);
        root.setMargin(IOBox, new Insets(5,5,5,5));
        root.setLeft(calcPane);
        root.setMargin(calcPane, new Insets(5,5,5,5));
        root.setRight(LayerBox);
        root.setMargin(LayerBox, new Insets(5,5,5,5));

        primaryStage.setScene(new Scene(root, 400, 370));
        primaryStage.show();

        //TextButtons
        btns[0].setOnAction((ev) -> write("1"));
        btns[1].setOnAction((ev) -> write("2"));
        btns[2].setOnAction((ev) -> write("3"));
        btns[3].setOnAction((ev) -> write("+"));
        btns[7].setOnAction((ev) -> write("-"));
        btns[11].setOnAction((ev) ->write("*"));
        btns[4].setOnAction((ev) -> write("4"));
        btns[5].setOnAction((ev) -> write("5"));
        btns[6].setOnAction((ev) -> write("6"));
        btns[8].setOnAction((ev) -> write("7"));
        btns[9].setOnAction((ev) -> write("8"));
        btns[10].setOnAction((ev) -> write("9"));
        btns[13].setOnAction((ev) -> write("0"));
        btns[15].setOnAction((ev) -> write("/"));
        btns[14].setOnAction((ev) -> write("."));
        btns[16].setOnAction((ev) -> write(" "));
        btns[19].setOnAction((ev) -> write("INV"));


        //Action Buttons
        btns[12].setOnAction((ev) -> parse());
        btns[17].setOnAction((ev) -> clearAll());
        btns[18].setOnAction((ev) -> backspace());

    }

    void clearAll(){
        this.input = "";
        this.stack = new Stack<Double>();
        inputArea.setText("");
        outputArea.setText("");
        for(int i = 0; i < layerAr.length; i++){
            layerAr[i].setText("");
        }
    }

    void backspace(){
        this.input = input.substring(0, input.length()-1);
        inputArea.setText(this.input);
    }

    void write(String input) {
        this.input += input;
        inputArea.setText(this.input);
    }

    void parse() {
        if(this.input.equals("")){
            this.input = inputArea.getText();
        }
        System.out.println(this.input);
        String [] tmp = this.input.split(" ");

        try {
            for (int i = 0; i < tmp.length; i++) {
                if(tmp[i].equals("+")) add();
                else if(tmp[i].equals("-")) sub();
                else if(tmp[i].equals("/")) div();
                else if(tmp[i].equals("*")) mul();
                else if(tmp[i].equals("INV")) inv();
                else {
                    if(stack.size()>=4)throw new StackOverflowError("Clear Stack!");
                    stack.push(Double.parseDouble(tmp[i]));
                    layerAr[layer++].setText(""+stack.peek());
                }
            }
        }
        catch (NumberFormatException nfe) {
            System.out.println("Bad Input at length = 2, Syntax: Double*SPACE*...Double");
        }

        this.input = "";
        inputArea.setText("");
    }

    void inv(){
        double x1 = stack.peek();
        stack.pop();
        stack.push(x1*(-1));
        outputArea.setText(""+stack.peek());
        layerAr[layer-1].setText(""+stack.peek());
    }

    void add(){
        if(stack.size() < 2) throw new UnsupportedOperationException("Stack Underflow");
        Double x1 = stack.peek();
        stack.pop();
        Double x2 = stack.peek();
        stack.pop();
        stack.push(x1+x2);
        outputArea.setText(""+stack.peek());
        layerAr[--layer].setText(" ");
        layerAr[layer-1].setText(""+stack.peek());
    }

    void sub(){
        if(stack.size() < 2) throw new UnsupportedOperationException("Stack Underflow");
        Double x1 = stack.peek();
        stack.pop();
        Double x2 = stack.peek();
        stack.pop();
        stack.push(x1-x2);
        outputArea.setText(""+stack.peek());
        layerAr[--layer].setText(" ");
        layerAr[layer-1].setText(""+stack.peek());
    }

    void mul(){
        if(stack.size() < 2) throw new UnsupportedOperationException("Stack Underflow");
        Double x1 = stack.peek();
        stack.pop();
        Double x2 = stack.peek();
        stack.pop();
        stack.push(x1*x2);
        outputArea.setText(""+stack.peek());
        layerAr[--layer].setText(" ");
        layerAr[layer-1].setText(""+stack.peek());
    }

    void div(){
        if(stack.size() < 2) throw new UnsupportedOperationException("Stack Underflow");
        Double x1 = stack.peek();
        stack.pop();
        Double x2 = stack.peek();
        stack.pop();
        stack.push(x1/x2);
        outputArea.setText(""+stack.peek());
        layerAr[--layer].setText(" ");
        layerAr[layer-1].setText(""+stack.peek());
    }
}
