package org.example;

import java.io.*;
import java.util.Arrays;

public class BTree {
  public static void main(String... args) {
    try (var file = new RandomAccessFile("btree.bin", "rw")) {
      if (false) {
        try {
          var f = new File("btree.bin");
          f.delete();
          System.out.println("file deleted");
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
      var tree = new BTree(file);
      if (false) {
        tree.put(1, "Rafael");
        tree.put(2, "Carmen");
        tree.put(3, "Ricardo");
        tree.put(4, "Mi");
        tree.put(5, "Alvaro");
        tree.put(6, "Debora");
        tree.put(7, "John");
        tree.put(8, "Jane");
      }
      for (int i = 0; i < 10; i++) {
        System.out.println(i + ": " + tree.get(i));
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  static final class Node {
    private static final int PAGE_SIZE = 4 * 1024;
    private static final int MAX = 4;

    public static Node load(RandomAccessFile file, Long id) throws IOException {
      var arr = new byte[PAGE_SIZE];
      file.seek(id);
      file.read(arr);
      var buf = new ByteArrayInputStream(arr);
      var in = new DataInputStream(buf);
      var res = new Node(0);
      res.count = in.readInt();
      for (int i = 0; i < res.count; i++) {
        res.entries[i] = Entry.load(in);
      }
      return res;
    }

    private final Entry[] entries = new Entry[MAX];
    private int count;
    private Long id;

    public Node(int count) {
      this.count = count;
    }

    public long save(RandomAccessFile file) throws IOException {
      class X extends ByteArrayOutputStream {
        public X(int size) {
          super(size);
        }
        public byte[] buffer() { return buf; }
      }
      var buf = new X(PAGE_SIZE);
      var out = new DataOutputStream(buf);
      out.writeInt(count);
      for (int i = 0; i < count; i++) {
        entries[i].save(out);
      }
      if (id == null) {
        id = file.length();
      }
      file.seek(id);
      var arr = buf.buffer();
      file.write(arr);
      return id;
    }

    @Override
    public String toString() {
      return id + ":" + Arrays.toString(entries);
    }
  }
  static final class Entry {
    private static Entry load(DataInputStream in) throws IOException {
      var res = new Entry();
      res.key = in.readInt();
      var len = in.readInt();
      if (len > 0) {
        var val = new byte[len];
        in.read(val);
        res.value = new String(val);
      } else {
        res.value = null;
      }
      var nextId = in.readLong();
      if (nextId != -1) {
        res.nextId = nextId;
      } else {
        res.nextId = null;
      }
      return res;
    }

    private Integer key;
    private String value;
    private Node next;
    private Long nextId; // used for persistence

    private Entry() {

    }

    public Entry(Integer key, String value, Node next) {
      this.key = key;
      this.value = value;
      this.next = next;
    }

    public void save(DataOutputStream out) throws IOException {
      out.writeInt(key);
      if (value != null) {
        out.writeInt(value.getBytes().length);
        out.write(value.getBytes());
      } else {
        out.writeInt(0);
      }
      if (next != null) {
        out.writeLong(next.id);
      } else {
        out.writeLong(-1);
      }
    }

    public void loadNext(RandomAccessFile file) throws IOException {
      next = Node.load(file, nextId);
    }

    @Override
    public String toString() {
      var buf = new StringBuilder();
      buf.append(key + ":" + value + ":" + nextId);
      return buf.toString();
    }
  }

  private Node root;
  private int height;
  private int count;
  private RandomAccessFile file;

  public BTree(RandomAccessFile f) throws IOException {
    file = f;
    if (file.length() == 0) {
      file.writeInt(count);
      file.writeInt(height);
      root = new Node(0);
      root.save(file);
    } else {
      count = file.readInt();
      height = file.readInt();
      root = Node.load(file, file.getFilePointer());
    }
  }

  public void put(Integer key, String value) throws IOException {
    if (key == null) {
      throw new IllegalArgumentException("key is null");
    }
    if (value == null) {
      throw new IllegalArgumentException("value is null");
    }

    var newNode = insert(root, key, value, height);
    count++;
    if (newNode == null) {
      return;
    }
    newNode.save(file);

    var newRoot = new Node(2);
    newRoot.entries[0] = new Entry(root.entries[0].key, null, root);
    newRoot.entries[1] = new Entry(newNode.entries[0].key, null, newNode);
    newRoot.entries[1].nextId = newNode.id;
    newRoot.save(file);
    newRoot.entries[0].nextId = newRoot.id;

    // swap the ids
    var t = root.id;
    root.id = newRoot.id;
    newRoot.id = t;

    root.save(file);
    root = newRoot;
    height++;
    file.seek(0L);
    file.writeInt(count);
    file.writeInt(height);
  }

  private Node insert(Node node, Integer key, String value, int height) throws IOException {
    var ins = 0;
    var newEntry = new Entry(key, value, null);
    if (height == 0) {
      for (ins = 0; ins < node.count; ins++) {
        if (key < node.entries[ins].key) {
          break;
        }
      }
    } else {
      for (ins = 0; ins < node.count; ins++) {
        if (ins + 1 == node.count || key < node.entries[ins + 1].key) {
          var newNode = insert(node.entries[ins++].next, key, value, height - 1);
          if (newNode == null) {
            return null;
          }
          newNode.save(file);
          newEntry = new Entry(newNode.entries[0].key, null, newNode);
          newEntry.nextId = newNode.id;
          break;
        }
      }
    }
    for (int i = node.count; i > ins; i--) {
      node.entries[i] = node.entries[i - 1];
    }
    node.entries[ins] = newEntry;
    node.count++;
    node.save(file);
    if (node.count == Node.MAX) {
      return split(node);
    }
    return null;
  }

  private Node split(Node full) {
    var half = (int) Node.MAX / 2;
    full.count = half;
    var newNode = new Node(half);
    for (int i = 0; i < half; i++) {
      newNode.entries[i] = full.entries[half + i];
      full.entries[half + i] = null;
    }
    return newNode;
  }

  public String get(Integer key) throws IOException {
    return search(root, key, height);
  }

  private String search(Node node, Integer key, int height) throws IOException {
    if (height == 0){
      for (int i = 0; i < node.count; i++) {
        if (key != null && key.intValue() == node.entries[i].key.intValue()) {
          return node.entries[i].value;
        }
      }
    } else {
      for (int i = 0; i < node.count; i++) {
        if (i + 1 == node.count || key < node.entries[i + 1].key) {
          node.entries[i].loadNext(file);
          return search(node.entries[i].next, key, height - 1);
        }
      }
    }
    return null;
  }
}
