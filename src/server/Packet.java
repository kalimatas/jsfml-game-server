package server;

import java.io.Serializable;
import java.util.LinkedList;

public class Packet implements Serializable {
    private LinkedList data = new LinkedList();

    public int size() {
        return data.size();
    }

    @SuppressWarnings("unchecked")
    public void append(Object obj) {
        data.add(obj);
    }

    public Object get() {
        return data.removeFirst();
    }
}
