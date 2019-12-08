package org.jsoup.select;
import java.util.*;

public class ProducerConsumer
{
  public static void main(String[] args) throws InterruptedException
  {
    Buffer buffer = new Buffer(BUFSIZE);
    Consumer consumer = new Consumer(buffer);
    consumer.start();
    Producer producer = new Producer(buffer);
    producer.start();
    consumer.join();
  }

  final static int BUFSIZE = 5;
  final static int MAXDATA = 100;
  static Random random = new Random();
}

class Producer extends Thread
{
  public Producer(Buffer buffer) {
    this.buffer = buffer;
  }

  public void run() {
    try {
      for (int value = 1; value <= ProducerConsumer.MAXDATA; value++) {
        Thread.sleep(ProducerConsumer.random.nextInt(100));
        System.out.println("Producer> " + value);
        buffer.deposit(value);
        System.out.println("Producer< " + value);
      }
    } catch (InterruptedException e) {}
  }

  private final Buffer buffer;
}

class Consumer extends Thread
{
  public Consumer(Buffer buffer) {
    this.buffer = buffer;
  }

  public void run() {
    try {
      while (true) {
        Thread.sleep(ProducerConsumer.random.nextInt(100));
        int value = buffer.fetch();
        System.out.println("Consumer= " + value);
        if (value == ProducerConsumer.MAXDATA)
          return;
      }
    } catch (InterruptedException e) {}
  }

  private final Buffer buffer;
}

class Buffer
{
  private Queue<Integer> buffer;
  private int size;

  public Buffer(int size) {
    this.buffer = new LinkedList<Integer>();
    this.size = size;
  }

  public synchronized void deposit(int value) throws InterruptedException {

    while (buffer.size() >= size)
      wait();

    buffer.add(value);
    notifyAll();
  }

  public synchronized int fetch() throws InterruptedException {
    while (buffer.isEmpty())
      wait();
    int value = buffer.remove();
    notifyAll();
    return value;
  }
}