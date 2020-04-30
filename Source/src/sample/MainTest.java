package sample;

import javafx.scene.text.Text;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class MainTest
{
    Main m;

    @Test
    void testStart() {
    }

    @Test
    void testMain() {

    }

    @Test
    void testMasterList() {
        // Task master
        m.taskMaster = new ArrayList<Task>();
        assertEquals(0, m.taskMaster.size());

        // Populate task master list
        for (String task: m.taskStrings)
        {
            m.taskMaster.add(new Task((task)));
        }
        assertEquals(17, m.taskMaster.size());
    }

    @Test
    void testTaskList() {
        // Populate task master list
        m.taskMaster = new ArrayList<Task>();
        for (String task: m.taskStrings) {
            m.taskMaster.add(new Task((task)));
        }

        // Populate Tasks to do
        m.taskToDo = new ArrayList<Text>();
        assertEquals(0, m.taskToDo.size());
        m.taskToDo = m.populateTaskTexts();
        assertEquals(17, m.taskToDo.size());
    }

    @BeforeEach
    void setUp() {
        m = new Main();
    }

    @AfterEach
    void tearDown() {
        m = null;
    }
}