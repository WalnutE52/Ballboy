package ballboy.model.entities.utilities;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.GregorianCalendar;
import java.util.List;
import java.util.ArrayList;
import java.util.Calendar;
import java.sql.*;
import static org.junit.jupiter.api.Assertions.*;

public class Vector2DTest {

    Vector2D vector;
    Vector2D vector2;

    @BeforeEach
    public void setup() {
        vector = new Vector2D(3, 4);
        vector2 = new Vector2D(2, 1);
    }

    @Test
    public void getXTest() {
        assertEquals(3, vector.getX());
    }

    @Test
    public void getYTest() {
        assertEquals(4, vector.getY());
    }

    @Test
    public void setXTest() {
        Vector2D newV = vector.setX(5);
        assertEquals(5, newV.getX());
    }

    @Test
    public void setYTest() {
        Vector2D newV = vector.setY(6);
        assertEquals(6, newV.getY());
    }

    @Test
    public void reflectXTest() {
        Vector2D newV = vector.reflectX();
        assertEquals(-3, newV.getX());
    }

    @Test
    public void addTest() {
        Vector2D newV = vector.add(vector2);
        assertEquals(5, newV.getX());
        assertEquals(5, newV.getY());
    }

    @Test
    public void addXTest() {
        Vector2D newV = vector.addX(3);
        assertEquals(6, newV.getX());
    }

    @Test
    public void addYTest() {
        Vector2D newV = vector.addY(2);
        assertEquals(6, newV.getY());
    }

    @Test
    public void scaleTest() {
        Vector2D newV = vector.scale(2);
        assertEquals(6, newV.getX());
        assertEquals(8, newV.getY());
    }

    @Test
    public void isLeftOfTest() {
        assertTrue(vector.isLeftOf(5));
        assertFalse(vector.isLeftOf(1));
    }

    @Test
    public void isRightOfTest() {
        assertTrue(vector.isRightOf(1));
        assertFalse(vector.isRightOf(5));
    }

    @Test
    public void isAboveTest() {
        assertTrue(vector.isAbove(9));
        assertFalse(vector.isAbove(1));
    }

    @Test
    public void isBelowTest() {
        assertTrue(vector.isBelow(1));
        assertFalse(vector.isBelow(9));
    }

}
