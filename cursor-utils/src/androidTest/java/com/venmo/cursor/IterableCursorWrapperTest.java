package com.venmo.cursor;

import android.database.Cursor;
import android.test.AndroidTestCase;

import com.venmo.cursor.test.Pojo;
import com.venmo.cursor.test.PojoCursor;
import com.venmo.cursor.test.TestDb;

import java.util.Arrays;

public class IterableCursorWrapperTest extends AndroidTestCase {

    public void testRetrievalHelpers() {
        TestDb db = new TestDb(getContext());

        db.insertRow(1, 1l, 1.1f, 1.2d, (short) 1, true, new byte[]{1, 2, 3}, "a");
        IterableCursorWrapper<?> cursor = new IterableCursorWrapper<Object>(db.query()) {
            @Override
            public Object peek() {
                return null;
            }
        };

        String strFound = cursor.getStringHelper("some_str", "not_found");
        assertEquals("a", strFound);
        String strNotFound = cursor.getStringHelper("other", "not_found");
        assertEquals("not_found", strNotFound);

        int intFound = cursor.getIntegerHelper("some_int", -1);
        assertEquals(1, intFound);
        int intNotFound = cursor.getIntegerHelper("other", -1);
        assertEquals(-1, intNotFound);

        long longFound = cursor.getLongHelper("some_long", -1l);
        assertEquals(1l, longFound);
        long longNotFound = cursor.getLongHelper("other", -1l);
        assertEquals(-1l, longNotFound);

        boolean booleanFound = cursor.getBooleanHelper("some_boolean", false);
        assertEquals(true, booleanFound);
        boolean booleanNotFound = cursor.getBooleanHelper("other", false);
        assertEquals(false, booleanNotFound);

        float floatFound = cursor.getFloatHelper("some_float", 2.0f);
        assertEquals(1.1f, floatFound);
        float floatNotFound = cursor.getFloatHelper("other", 2.0f);
        assertEquals(2.0f, floatNotFound);

        double doubleFound = cursor.getDoubleHelper("some_double", 3.0d);
        assertEquals(1.2d, doubleFound);
        double doubleNotFound = cursor.getDoubleHelper("other", 3.0d);
        assertEquals(3.0d, doubleNotFound);

        short shortFound = cursor.getShortHelper("some_short", (short) 4);
        assertEquals((short) 1, shortFound);
        short shortNotFound = cursor.getShortHelper("other", (short) 4);
        assertEquals((short) 4, shortNotFound);

        byte[] blobFound = cursor.getBlobHelper("some_byte_array", new byte[]{4, 5, 6});
        assertTrue(Arrays.equals(new byte[]{1, 2, 3}, blobFound));
        byte[] blobNotFound = cursor.getBlobHelper("other", new byte[]{4, 5, 6});
        assertTrue(Arrays.equals(new byte[]{4, 5, 6}, blobNotFound));
    }

    public void testIterating() {
        TestDb db = new TestDb(getContext());

        db.insertRow(0, 0l, 0f, 0d, (short) 0, true, new byte[]{0, 0}, "0");
        db.insertRow(1, 1l, 1f, 1d, (short) 1, true, new byte[]{1, 1}, "1");

        IterableCursor<Pojo> cursor = new PojoCursor(db.query());

        Pojo[] samples = new Pojo[]{
                new Pojo(0, 0l, 0f, 0d, (short) 0, true, new byte[]{0, 0}, "0"),
                new Pojo(1, 1l, 1f, 1d, (short) 1, true, new byte[]{1, 1}, "1"),
        };

        iterationHelper(cursor, samples);
    }

    public void testMerging() {
        TestDb db0 = new TestDb(getContext());
        TestDb db1 = new TestDb(getContext());

        db0.insertRow(0, 0l, 0f, 0d, (short) 0, true, new byte[]{0, 0}, "0");
        db0.insertRow(1, 1l, 1f, 1d, (short) 1, true, new byte[]{1, 1}, "1");
        db1.insertRow(2, 2l, 2f, 2d, (short) 2, true, new byte[]{2, 2}, "2");
        db1.insertRow(3, 3l, 3f, 3d, (short) 3, true, new byte[]{3, 3}, "3");

        IterableCursor<Pojo> cursor = new IterableMergeCursor<Pojo>(
                new PojoCursor(db0.query()), new PojoCursor(db1.query()));

        Pojo[] samples = new Pojo[]{
                new Pojo(0, 0l, 0f, 0d, (short) 0, true, new byte[]{0, 0}, "0"),
                new Pojo(1, 1l, 1f, 1d, (short) 1, true, new byte[]{1, 1}, "1"),
                new Pojo(2, 2l, 2f, 2d, (short) 2, true, new byte[]{2, 2}, "2"),
                new Pojo(3, 3l, 3f, 3d, (short) 3, true, new byte[]{3, 3}, "3")
        };

        iterationHelper(cursor, samples);
    }

    public void testIsEmpty() {
        TestDb db0 = new TestDb(getContext());
        TestDb db1 = new TestDb(getContext());

        db0.insertRow(0, 0l, 0f, 0d, (short) 0, true, new byte[]{0, 0}, "0");

        IterableCursorWrapper<Pojo> cursor0 = new PojoCursor(db0.query());
        IterableCursorWrapper<Pojo> cursor1 = new PojoCursor(db1.query());

        assertFalse(cursor0.isEmpty());
        assertTrue(cursor1.isEmpty());
    }

    public void testMovingAround() {
        TestDb db = new TestDb(getContext());

        db.insertRow(0, 0l, 0f, 0d, (short) 0, true, new byte[]{0, 0}, "0");
        db.insertRow(1, 1l, 1f, 1d, (short) 1, true, new byte[]{1, 1}, "1");
        db.insertRow(2, 2l, 2f, 2d, (short) 2, true, new byte[]{2, 2}, "2");
        db.insertRow(3, 3l, 3f, 3d, (short) 3, true, new byte[]{3, 3}, "3");

        IterableCursorWrapper<Pojo> cursor = new PojoCursor(db.query());

        Pojo p0 = new Pojo(0, 0l, 0f, 0d, (short) 0, true, new byte[]{0, 0}, "0");
        Pojo p1 = new Pojo(1, 1l, 1f, 1d, (short) 1, true, new byte[]{1, 1}, "1");
        Pojo p2 = new Pojo(2, 2l, 2f, 2d, (short) 2, true, new byte[]{2, 2}, "2");
        Pojo p3 = new Pojo(3, 3l, 3f, 3d, (short) 3, true, new byte[]{3, 3}, "3");

        assertEquals(p0, cursor.nextDocument());
        assertEquals(p0, cursor.previousDocument());

        assertEquals(p0, cursor.nextDocument());
        assertEquals(p1, cursor.nextDocument());
        assertEquals(p2, cursor.nextDocument());
        assertEquals(p3, cursor.nextDocument());
        assertEquals(p3, cursor.previousDocument());
        assertEquals(p2, cursor.previousDocument());
        assertEquals(p1, cursor.previousDocument());
        assertEquals(p0, cursor.previousDocument());

        assertEquals(p0, cursor.nextDocument());
        assertEquals(p1, cursor.nextDocument());
        assertEquals(p2, cursor.nextDocument());
        assertEquals(p3, cursor.nextDocument());
    }

    public void testMovingAroundWithOnlyOneItem() {
        TestDb db = new TestDb(getContext());

        db.insertRow(0, 0l, 0f, 0d, (short) 0, true, new byte[]{0, 0}, "0");

        IterableCursorWrapper<Pojo> cursor = new PojoCursor(db.query());

        Pojo p0 = new Pojo(0, 0l, 0f, 0d, (short) 0, true, new byte[]{0, 0}, "0");

        assertEquals(p0, cursor.nextDocument());
        assertEquals(p0, cursor.previousDocument());
    }

    private void iterationHelper(IterableCursor<Pojo> cursor, Pojo[] samples) {
        int i = 0;
        for (Pojo pojo : cursor) {
            Pojo expected = samples[i];
            assertEquals(expected, pojo);
            i++;
        }
        assertEquals(samples.length, i);
    }

}