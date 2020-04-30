package sample;

import javafx.animation.AnimationTimer;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.input.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.ArcType;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.util.Duration;

//
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public class Main extends Application
{
    Scene scene;
    ArrayList<Task> taskMaster;
    ArrayList<Text> taskToDo;
    Color myColour;
    String[] taskStrings = {
            "Charge phone",
            "Send Email",
            "Send Fax",
            "Reset Password",
            "Request Authorisation",
            "Supply Key",
            "Lock Doors",
            "Feed Püpperdoggens",
            "Refund Warcraft III",
            "Win Lotto",
            "Commit Pi to Memory",
            "Update Software",
            "Fetch Master Branch",
            "Do a Flip",
            "World Domination",
            "Java Assignment",
            "Start Blasting"
    };

    Random rand;

    int r = 127;
    int g = 127;
    int b = 127;

    int lineWidth;
    int spread = 0;

    boolean growing = false;

    VBox vb;
    GraphicsContext gc;
    Group root;
    Canvas canv;

    @Override
    public void start(Stage primaryStage) throws Exception {
        rand = new Random();

        // Set stage and scene
        primaryStage.setTitle("JMC Dragger N Dropper");

        // Create menu bar + items
        MenuBar mb = CreateMenus(primaryStage);

        vb = new VBox(mb);
        root = new Group(vb);
        scene = new Scene(root, 1200, 750);
        scene.setFill(Color.WHITE);

        // Create canvas
        canv = new Canvas(scene.getWidth(), scene.getHeight() - mb.getHeight());
        gc = canv.getGraphicsContext2D();

        vb.getChildren().add(canv);

        // Initialise Tasks
        taskMaster = new ArrayList<Task>();
        taskToDo = new ArrayList<Text>();

        // Populate task master list
        for (String task: taskStrings)
        {
            taskMaster.add(new Task((task)));
        }

        // Print TO DO items
        ArrayList<Text> toDoTexts = populateTaskTexts();

        // Set various event handlers for each to do item
        addTextDragHandlers(toDoTexts, root);

        // Place labels
        placeLabels(root);

        animatePolygon(gc, Color.rgb(rand.nextInt(255),rand.nextInt(255),rand.nextInt(255)), 50, 3);

        primaryStage.setScene(scene);
        primaryStage.show();
    }
    public MenuBar CreateMenus(Stage primaryStage) {
        // FILE
        Menu menuFile = new Menu("File");
        MenuItem menuFile1 = new MenuItem("Reset List");
        menuFile1.setOnAction(event -> {
            taskToDo.clear();
            try {
                start(primaryStage);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        MenuItem menuFile3 = new MenuItem("Reset Background");
        menuFile3.setOnAction(event -> {
            gc.setFill(Color.WHITE);
            gc.fillRect(0, 0, scene.getWidth(), scene.getHeight());
        });
        MenuItem menuFile2 = new MenuItem("Exit");
        menuFile2.setOnAction(event -> { System.exit(0);});
        menuFile.getItems().add(menuFile1);
        menuFile.getItems().add(menuFile3);
        menuFile.getItems().add(menuFile2);

        // HELP
        Menu menuHelp = new Menu("Help");
        MenuItem menuHelp1 = new MenuItem("Help File");
        menuHelp.getItems().add(menuHelp1);
        menuHelp1.setOnAction(event -> openHelpFile());

        // Create and Add menu bar
        MenuBar mb = new MenuBar();
        mb.getMenus().addAll(menuFile, menuHelp);
        return mb;
    }
    public static void main(String[] args) { launch(args); }
    public void addPanelDragHandlers(Scene scene, Text target) {
        target.setOnDragOver(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                /* data is dragged over the target */
//                System.out.println("onDragOver");
                /* accept it only if it is not dragged from the same node and if it has a string data */
                if (event.getGestureSource() != target && event.getDragboard().hasString())
                {
                    /* allow for moving */
                    event.acceptTransferModes(TransferMode.MOVE);

//                    scene.setFill(Color.ORANGE);
                }
                event.consume();
            }
        });

        target.setOnDragEntered(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent event)
            {
                /* the drag-and-drop gesture entered the target */
//                System.out.println("onDragEntered");

                /* show to the user that it is an actual gesture target */
                if (event.getGestureSource() != target && event.getDragboard().hasString())
                {
                    target.setFill(Color.GREEN);

//                    scene.setFill(Color.YELLOW);
                }
                event.consume();
            }
        });

        target.setOnDragExited(new EventHandler<DragEvent>()
        {
            @Override
            public void handle(DragEvent event)
            {
                /* mouse moved away, remove the graphical cues */
                target.setFill(Color.BLACK);
                event.consume();
            }
        });

        target.setOnDragDropped(new EventHandler<DragEvent>()
        {
            @Override
            public void handle(DragEvent event)
            {
                /* data dropped */
//                System.out.println("onDragDropped");

                /* if there is a string data on dragboard, read it and use it */
                Dragboard db = event.getDragboard();
                boolean success = false;
                if (db.hasString())
                {
//                    target.setText(db.getString());

                    success = true;
                }
                /* let the source know whether the string was successfully transferred and used */
                event.setDropCompleted(success);
                event.consume();

                scene.setFill(Color.WHITE);
            }
        });
    }
    public void addTextDragHandlers(ArrayList<Text> textObjs, Group _group) {
        for (Text item: textObjs) {
            item.setOnDragDetected(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    /* drag was detected, start drag-and-drop gesture*/
//                    System.out.println("onDragDetected");

                    /* allow MOVE transfer mode */
                    Dragboard db = item.startDragAndDrop(TransferMode.MOVE);

                    /* put a string on dragboard */
                    ClipboardContent content = new ClipboardContent();
                    content.putString(item.getText());
                    db.setContent(content);
                    event.consume();
                }
            });

            item.setOnDragDone(new EventHandler<DragEvent>() {
                @Override
                public void handle(DragEvent event) {
                    /* the drag-and-drop gesture ended */
//                    System.out.println("onDragDone");

                    /* if the data was successfully moved, clear it */
                    if (event.getTransferMode() == TransferMode.MOVE)
                    {
                        // Move text to To Do list zone
                        Text selectedTextObject = (Text)event.getSource();
                        addToList(selectedTextObject);

//                        item.setText("");
                    }
                    event.consume();

                    // Change background colour
                    scene.setFill(Color.LIGHTBLUE);
                }
            });

            item.setOnMouseEntered(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    // Triggered a Mouse over event
//                    System.out.println("onMouseEntered");

                    // Change background colour
                    Text currentText = (Text) event.getSource();
                    currentText.setFill(Color.GREEN);

                    // Yummy yummy
                    event.consume();
                }
            });

            item.setOnMouseExited(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    // Triggered a Mouse over event
//                    System.out.println("onMouseExited");

                    // Change background colour
                    Text currentText = (Text) event.getSource();
                    currentText.setFill(Color.WHITE);

                    // Event salad, Yummy yummy
                    event.consume();
                }
            });

            // Add the item to the scene
            _group.getChildren().add(item);
        }
    }
    public ArrayList<Text> populateTaskTexts() {
        ArrayList<Text> myTexts = new ArrayList<Text>();
        int yPosition;
        for (int i = 0; i < taskMaster.size(); i++) {
            // Determine Y Position
            yPosition = 150 + (i * 30);

            // Add new text
            myTexts.add(new Text(150, yPosition, taskMaster.get(i).getTask()));

            // Set size
            myTexts.get(i).setFill(Color.WHITE);
            myTexts.get(i).setScaleX(2.0);
            myTexts.get(i).setScaleY(2.0);

            // Text align to left for neatness
            myTexts.get(i).setWrappingWidth(150);
            myTexts.get(i).setTextAlignment(TextAlignment.LEFT);
        }

        // Return list of Text objects
        return myTexts;
    }
    public void placeLabels(Group root) {
        // Available tasks list
        final Text labelMaster = new Text(140, 75, "Available Tasks");
        labelMaster.setScaleX(2.5);
        labelMaster.setScaleY(2.5);
        labelMaster.setFill(Color.ORANGE);
        root.getChildren().add(labelMaster);

        // My to do list
        final Text labelToDo = new Text(scene.getWidth() - 230, 75, "My ToDo List");
        labelToDo.setScaleX(2.5);
        labelToDo.setScaleY(2.5);
        labelToDo.setFill(Color.GREEN);
        root.getChildren().add(labelToDo);

        // 'Add to list' drop zone
        int xPos = (int) scene.getWidth() / 2 - 50;
        int yPos = (int) scene.getHeight() / 2 - 25;
        final Text target = new Text(xPos, yPos, "   DROP HERE \nTO ADD TO LIST");
        target.setFill(Color.BLACK);
        target.setScaleX(2.0);
        target.setScaleY(2.0);

        // Add Panel Drag Events
        addPanelDragHandlers(scene, target);

        // Add to scene
        root.getChildren().add(target);
    }
    public void addToList(Text taskObject) {
        if (taskToDo.size() < 18) {
            // Add to list
            taskToDo.add(taskObject);

            // Display text in list
            int xPos = (int) scene.getWidth() - 300;
            int yPos = 150 + ((taskToDo.size() - 1) * 30);

            final Text newTask = new Text(xPos, yPos, taskObject.getText());
            newTask.setFill(Color.WHITE);
            newTask.setScaleX(2);
            newTask.setScaleY(2);

            // Text align to left for neatness
            newTask.setWrappingWidth(150);
            newTask.setTextAlignment(TextAlignment.RIGHT);

            // Add the item to the scene
            Group curRoot = (Group) scene.getRoot();
            curRoot.getChildren().add(newTask);
        }
        else
        {
            JOptionPane.showMessageDialog(null, "List is full. Start a new list.", "List is full", JOptionPane.ERROR_MESSAGE);
        }
    }
    public void animatePolygon(GraphicsContext gc, Color polyColor, int pointCount, int polygonCount) throws InterruptedException {
        // Get random x points
        int windowWidth = (int)scene.getWidth();
        double[] xPoints = new double[pointCount];
        for (int i = 0; i < xPoints.length; i++) {
            xPoints[i] = rand.nextInt(windowWidth);
        }

        // Get random y points
        int windowHeight = (int)scene.getHeight();
        double[] yPoints = new double[pointCount];
        for (int i = 0; i < yPoints.length; i++) {
            yPoints[i] = rand.nextInt(windowHeight);
        }

        DoubleProperty x  = new SimpleDoubleProperty();
        DoubleProperty y  = new SimpleDoubleProperty();

        Timeline timeline = new Timeline(
            new KeyFrame(Duration.seconds(0),
                    new KeyValue(x, 0),
                    new KeyValue(y, 0)
            ),
            new KeyFrame(Duration.seconds(1),
                    new KeyValue(x, scene.getWidth() - 20),
                    new KeyValue(y, scene.getHeight() - 20)
            ));

        timeline.setAutoReverse(false);
        timeline.setCycleCount(Timeline.INDEFINITE);

        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long l) {

            }
        };

        // Set background
        gc.setFill(Color.WHITE);
        gc.fillRect(0, 0, scene.getWidth(), scene.getHeight());

        // Place boxes
        int yMargin = 20;
        int boxWidth = 350;
        gc.setFill(Color.rgb(0, 0, 0, 0.5f));

        timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
            double xLower = scene.getWidth() * rand.nextDouble() - spread;
            double yLower = scene.getHeight() * rand.nextDouble() - spread;

            double xUpper = xLower + (rand.nextDouble() * (spread));
            double yUpper = yLower + (rand.nextDouble() * (spread));

            evolvePoints(xPoints, xLower, xUpper);
            evolvePoints(yPoints, yLower, yUpper);

            r = evolveColour(r);
            g = evolveColour(g);
            b = evolveColour(b);

            clampValue(r, 0, 255);
            clampValue(g, 0, 255);
            clampValue(b, 0, 255);

            myColour = Color.rgb(r, g, b, 0.5f);

            // Create polygon
            lineWidth = evolvePointCount(lineWidth);
            lineWidth = rand.nextInt(rand.nextInt(200) + 10);
            gc.setLineWidth(lineWidth);
            gc.setStroke(myColour);
            gc.strokePolyline(xPoints, yPoints, pointCount);

            // Task list backgrounds
            gc.setFill(Color.rgb(0, 0, 0, 0.1f));
            gc.fillRoundRect(yMargin, yMargin, boxWidth, scene.getHeight() - (4 * yMargin), 50, 50);
            gc.fillRoundRect(scene.getWidth() - yMargin - boxWidth, yMargin, boxWidth, scene.getHeight() - (4 * yMargin), 50, 50);

            int boxHeight = 90;
            int boxWidth = 200;

            // Drop here box background
            gc.setFill(Color.rgb(255, 255, 255, 0.1));
            gc.fillRoundRect((scene.getWidth()/2) - (boxWidth / 2) - 5, (scene.getHeight()/2) - (boxHeight/2) - 45, boxWidth, boxHeight, 50, 50);
            }
        };

        timer.start();
        timeline.play();
    }
    public int evolvePointCount(int count) {

        if (count < 1)
        {
            growing = true;
        }
        else if (count > 50)
        {
            growing = false;
        }
        if (growing)
        {
            count++;
        }
        else
        {
            count--;
        }
        return count;
    }
    public int evolveColour(int col) {
        int evolveRate = 50;

        // Flip coin
        if (coinFlip())
        {
            col += rand.nextInt(evolveRate);
        }
        else
        {
            col -= rand.nextInt(evolveRate);
        }

        if (col < 0)
            col = 0;
        else if (col > 255)
            col = 255;

        return col;
    }
    public boolean coinFlip() {
        if (rand.nextDouble() > 0.5f)
        {
            return true;
        }
        else
        {
            return false;
        }
    }
    public void clampValue(double value, double min, double max) {
        if (value < min)
            value = min;
        else if (value > max)
            value = max;
    }
    public void evolvePoints(double[] points, double minValue, double maxValue) {
        double evolveRate = 50;

        for (int i = 0; i < points.length; i++)
        {
            double coinFlip = rand.nextDouble();

            if (coinFlip >= 0.5f) {
                points[i] += evolveRate;

                // Clamp to window bounds
                if (points[i] > maxValue) {
                    points[i] = maxValue;
                }
                if (points[i] < minValue) {
                    points[i] = minValue;
                }
            } else {
                points[i] -= evolveRate;

                // Clamp to window bounds
                if (points[i] > maxValue) {
                    points[i] = maxValue;
                }
                if (points[i] < minValue) {
                    points[i] = minValue;
                }
            }
        }
    }
    public void openHelpFile() {
        System.out.println("Opening help file");
        String url = "Source\\help\\help.html";
        File htmlFile = new File(url);
        try
        {
            Desktop.getDesktop().browse(htmlFile.toURI());
        }
        catch (IOException ex)
        {
            System.out.println("IOException occurs: " + ex);
        }
    }
}


