package com.metchevn.util.interval;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;

import static java.util.Collections.singletonList;
import static org.junit.jupiter.api.Assertions.*;

public class TestInterval {

    @Test
    public void testInvalidIntervals() {
        assertThrows(IllegalArgumentException.class, () -> new Interval<Integer>(10, 10));
    }

    @Test
    public void testInvalidIntervals2() {
        assertThrows(IllegalArgumentException.class, () -> new Interval<Integer>(10, 9));
    }

    @Test
    public void testAfter() {
        Interval<Integer> interval = new Interval<>(0, 10);
        assertTrue(interval.isAfter(2, 1, true));
        assertTrue(interval.isAfter(2, 1, false));
        assertTrue(interval.isAfter(2, 2, true));
        assertFalse(interval.isAfter(2, 2, false));
        assertFalse(interval.isAfter(2, 3, false));
        assertFalse(interval.isAfter(2, 3, true));
    }

    @Test
    public void testBefore() {
        Interval<Integer> interval = new Interval<>(0, 10);
        assertFalse(interval.isBefore(2, 1, true));
        assertFalse(interval.isBefore(2, 1, false));
        assertTrue(interval.isBefore(2, 2, true));
        assertFalse(interval.isBefore(2, 2, false));
        assertTrue(interval.isBefore(2, 3, false));
        assertTrue(interval.isBefore(2, 3, true));
    }

    @Test
    public void testWithin() {
        Interval<Integer> interval = new Interval<>(2, 7);
        assertTrue(interval.isWithin(3));
        assertTrue(interval.isWithin(2));
        assertFalse(interval.isWithin(7));
        assertFalse(interval.isWithin(1));

        assertFalse(interval.overlaps(0, 1));
        assertFalse(interval.overlaps(1, true, 1, true));
        assertFalse(interval.overlaps(1, 2));

        assertTrue(interval.overlaps(1, 3));
        assertTrue(interval.overlaps(2, true, 2, true));
        assertTrue(interval.overlaps(2, 3));
        assertTrue(interval.overlaps(3, true, 3, true));
        assertTrue(interval.overlaps(3, 7));
        assertTrue(interval.overlaps(3, 10));
        assertTrue(interval.overlaps(1, 8));

        assertFalse(interval.overlaps(7, true, 7, true));
        assertFalse(interval.overlaps(7, 10));
        assertFalse(interval.overlaps(10, true, 10, true));
        assertFalse(interval.overlaps(10, 15));
    }

    @Test
    public void testIntersection() throws Exception {
        Interval<Integer> interval1 = new Interval<>(2, 7);
        Interval<Integer> interval2 = new Interval<>(3, 4);
        assertEquals(interval1.intersection(3, 4), interval2);
        assertEquals(interval1.intersection(3, 10), new Interval<>(3, 7));
        assertNull(interval1.intersection(0, 1));
    }

    @Test
    public void testMinusNonOverlap() throws Exception {
        Interval<Integer> interval1 = new Interval<>(2, 7);
        Interval<Integer> interval2 = new Interval<>(8, 10);

        assertEquals(interval1.minus(interval2), singletonList(interval1));
    }

    @Test
    public void testMinusCase1() throws Exception {
        Interval<Integer> interval1 = new Interval<>(2, 7);
        Interval<Integer> interval2 = new Interval<>(1, 4);

        assertEquals(interval1.minus(interval2), singletonList(new Interval<>(4, true, 7, false)));
    }

    @Test
    public void testMinusCase2() throws Exception {
        Interval<Integer> interval1 = new Interval<>(2, 7);
        Interval<Integer> interval2 = new Interval<>(1, 8);

        assertEquals(interval1.minus(interval2), Collections.emptyList());
    }

    @Test
    public void testMinusCase3() throws Exception {
        Interval<Integer> interval1 = new Interval<>(2, 7);
        Interval<Integer> interval2 = new Interval<>(4, 8);

        assertEquals(interval1.minus(interval2), singletonList(new Interval<>(2, true, 4, false)));
    }

    @Test
    public void testMinusCase4() throws Exception {
        Interval<Integer> interval1 = new Interval<>(2, 7);
        Interval<Integer> interval2 = new Interval<>(4, 6);

        assertEquals(interval1.minus(interval2), Arrays.asList(
                new Interval<>(2, true, 4, false),
                new Interval<>(6, true, 7, false)
        ));
    }
}
