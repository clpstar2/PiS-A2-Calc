import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.lang.Math.*;

interface UpnCalcRechner {
     void push(Double item);
     void pop();
     Double top();
     void add();
     void sub();
     void mul();
     void div();
     void inv();
     boolean isEmpty();
     int size();
 }

 class UpnCalc extends App implements UpnCalcRechner {
        UpnCalc next;
        Double value;
        int layer = 0;

        UpnCalc() {
            this.next = null;
            this.value = null;
        }

        UpnCalc(Double value, UpnCalc next) {
            this.value = value;
            this.next = next;
        }

        UpnCalc ini(){
            return new UpnCalc();
        }

        public int size() {
            if (next == null) return 0;
            return 1 + next.size();
        }

        public boolean isEmpty() {
            if (next == null) return true;
            return false;
        }

        public void push(Double value) {
            if(size() >= 4) throw new UnsupportedOperationException("Stack size limited to 4");
            UpnCalc tmp = new UpnCalc(this.value, next);
            this.value = value;
            next = tmp;
            layer++;
        }

        public void pop() {
            if(isEmpty()) throw new UnsupportedOperationException("Stack underflow");
            value = next.value;
            next = next.next;
            layer--;
        }

        public Double top() {
            if(isEmpty()) return -1.0;
            return value;
        }


        public void add(){
            Double x = this.top();
            this.pop();
            Double y = this.top();
            this.pop();
            this.push(x+y);
        }

        public void sub(){
            Double x = this.top();
            this.pop();
            Double y = this.top();
            this.pop();
            this.push(x-y);
        }

        public void mul(){
            Double x = this.top();
            this.pop();
            Double y = this.top();
            this.pop();
            this.push(x*y);
        }

        public void div() {
            Double x = this.top();
            this.pop();
            Double y = this.top();
            this.pop();
            this.push(x / y);
        }

        public void inv(){
            this.value = value*(-1);
        }

        String show() {
            if (next == null) return "]";
            return value + " " + next.show();
        }

        public String toString() {
            return "[" + show();
        }
}

public class App extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    TextField inputArea = new TextField();
    // BorderPane to organize the GUI
    BorderPane root = new BorderPane();
    // Pane containing the NumberPad + Functional Buttons
    GridPane calcPane = new GridPane();
    // Vertical Box containing the Stack-Layers
    VBox LayerBox = new VBox();
    // InputArea displaying current Input
    TextField[] layerArray = new TextField[4];
    // fill grid-pane containing the interactive elements of the calculator
    // btns-array for saving the freshly created buttons
    Button[] btns = new Button[18];

    public void start(Stage primaryStage) {
        String[] btnNames =
                {
                        "1", "2", "3", "+",
                        "4", "5", "6", "-",
                        "7", "8", "9", "*",
                        "CE", "0", ",", "/",
                        "Enter", "C"
                };
        for (int i = 0; i < btnNames.length; i++) {
            Button btn = new Button(btnNames[i]);
            btns[i] = btn;
            btn.setPrefSize(50, 50);
            calcPane.add(btn, i % 4, (int) Math.ceil(i / 4));
        }


        btns[0].setOnAction((ev) -> Control.write("1"));
        btns[1].setOnAction((ev) -> Control.write("2"));
        btns[2].setOnAction((ev) -> Control.write("3"));
        btns[3].setOnAction((ev) -> Control.stakk.add());
        btns[4].setOnAction((ev) -> Control.write("4"));
        btns[5].setOnAction((ev) -> Control.write("5"));
        btns[6].setOnAction((ev) -> Control.write("6"));
        btns[7].setOnAction((ev) -> Control.stakk.sub());
        btns[8].setOnAction((ev) -> Control.write("7"));
        btns[9].setOnAction((ev) -> Control.write("8"));
        btns[10].setOnAction((ev) -> Control.write(" "));
        btns[11].setOnAction((ev) -> Control.stakk.mul());
        btns[12].setOnAction((ev) -> Control.write("CE"));
        btns[13].setOnAction((ev) -> Control.write("0"));
        btns[14].setOnAction((ev) -> Control.write(","));
        btns[15].setOnAction((ev) -> Control.stakk.div());
        btns[16].setOnAction((ev) -> Control.parseInput(Control.input));
        btns[17].setOnAction((ev) -> Control.clear());


        // create 4 text-boxes and labels showcasing the currently saved stack elements

        for (int i = 4; i > 0; i--) {
            // labels assigning the displayed layer to a textfield
            Label layerNumber = new Label("Layer" + i);
            layerNumber.setStyle("-fx-font-size: 16, -fx-bold");
            // Horizontal Box containing Layer Elements
            HBox hLayerBox = new HBox();
            TextField layerText = new TextField();
            layerText.setDisable(true);
            layerArray[i - 1] = layerText;
            hLayerBox.getChildren().addAll(layerText, layerNumber);
            LayerBox.getChildren().add(hLayerBox);
        }


        // Put everything together
        root.setTop(inputArea);
        root.setBottom(LayerBox);
        root.setRight(calcPane);

        primaryStage.setScene(new Scene(root, 400, 400));
        primaryStage.show();
    }
}

    class Control extends UpnCalc {

    static String input = "";
    UpnCalc stakk = new UpnCalc();

        static void parseInput(String s) {
            double d1;
            double d2;
            String[] parser = s.split(" ");
            if (parser.length == 2) {
                try {
                    d1 = Double.parseDouble(parser[0]);
                    d2 = Double.parseDouble(parser[1]);
                    stakk.push(d1);
                    stakk.push(d2);
                } catch (NumberFormatException nfe) {
                    System.out.println("Bad Input");
                }
            } else if (parser.length == 1) {
                try {
                    d1 = Double.parseDouble(parser[0]);
                    stakk.push(d1);
                } catch (NumberFormatException nfe) {
                    System.out.println("Bad Input");
                }
            } else throw new IllegalArgumentException("Bad User Input, Syntax: Double [optional]Double ");
        }

        void clear() {
            stakk = new UpnCalc();
            stakk.layer = 0;
            input = "";
        }

        static void write(String input) {
            input += input;
        }
    }

