package org.encalmo.tagstats;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;


/**
 * GenericTagSet maintains in-memory doubly-linked ordered list of tags and current TopN list.
 * Instances of this class are NOT thread-safe.
 *
 * @param <T> type of tags
 * @see TagSetActor
 * @see GenericTagStatsService
 */
public final class GenericTagSet<T> implements TagSet<T> {

    private final int topListSize;
    private final int topListUpdateAccuracy;

    /**
     * @param topListSize           the size of the maintained top list
     * @param topListUpdateAccuracy at how many increment steps should we update the top list
     */
    GenericTagSet(int topListSize, int topListUpdateAccuracy) {
        this.topListSize = topListSize;
        this.topListUpdateAccuracy = topListUpdateAccuracy;
        this.steps = topListUpdateAccuracy;
    }

    private class Element<T> {
        final T tag;
        int count = 1;
        Element<T> previous;
        Element<T> next;

        public Element(T tag) {
            this.tag = tag;
        }

    }

    private Map<T, Element<T>> tags = new HashMap<>();
    private Element<T> first;
    private Element<T> last;
    private int total;
    private int steps;

    private AtomicReference<ArrayList<T>> top = new AtomicReference<>(new ArrayList<T>());

    @Override
    public void increment(T tag) {
        if (tag == null) {
            throw new AssertionError("tag must not be null");
        }

        Element<T> elem = tags.get(tag);
        if (elem == null) {
            elem = new Element<>(tag);
            tags.put(tag, elem);
            append(last, elem);
        } else {
            elem.count++;
            while (adjust(elem)) ;
        }
        total++;
        steps--;
        if (steps == 0) {
            updateTopList();
            steps = topListUpdateAccuracy;
        }
    }

    @Override
    public Iterable<T> top() {
        return top.get();
    }

    @Override
    public int size() {
        return total;
    }

    public double shareOf(T tag) {
        Element<T> elem = tags.get(tag);
        if (elem != null) {
            return elem.count / (double) total;
        } else {
            return 0d;
        }
    }

    private void append(Element<T> front, Element<T> back) {
        Element<T> moved = null;
        if (front != null) {
            moved = front.next;
            front.next = back;
        }
        back.previous = front;
        back.next = moved;
        if (moved != null) {
            moved.previous = back;
        }
        check(back);
    }

    private boolean adjust(Element<T> elem) {
        if (elem.previous == null) return false;
        if (elem.previous.count >= elem.count) return false;
        swap(elem);
        return true;
    }

    private void swap(Element<T> elem) {
        Element<T> front = elem.previous;
        if (front != null) {
            // 2nd element front change
            elem.previous = front.previous;
            if (front.previous != null) {
                front.previous.next = elem;
            }

            // 2nd element back and 1st element front swap
            Element<T> back = elem.next;
            elem.next = front;
            front.previous = elem;

            // 1st element back change
            front.next = back;
            if (back != null) {
                back.previous = front;
            }
            check(front);
        }
        check(elem);
    }

    private void check(Element<T> elem) {
        if (elem.next == null) {
            last = elem;
        }
        if (elem.previous == null) {
            first = elem;
        }
    }

    private void updateTopList() {
        ArrayList<T> list = new ArrayList<>(topListSize);
        Element<T> elem = first;
        while ((elem != null) && (list.size() < topListSize)) {
            list.add(elem.tag);
            elem = elem.next;
        }
        top.set(list);
    }

}
