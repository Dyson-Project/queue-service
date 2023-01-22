package vn.unicloud.genericqueue.server.algorithm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.function.Consumer;

public class CircularQueue<E> extends AbstractQueue<E> {
    final Logger logger = LoggerFactory.getLogger(CircularQueue.class);

    private Node<E> head = null;
    private Node<E> tail = null;
    private int size = 0;

    public E getHead() {
        return head.value;
    }

    @Override
    public boolean offer(E e) {
        if (e == null)
            throw new NullPointerException();

        Node<E> newNode = new Node<>(e);
        if (head == null) {
            head = newNode;
        } else {
            tail.nextNode = newNode;
        }

        tail = newNode;
        tail.nextNode = head;
        size++;
        return true;
    }

    @Override
    public E poll() {
        Node<E> currentNode = head;
        if (head == null) {
            // Truong hop list 0 phan tu
            return null;
        }
        if (tail == head) {
            // Truong hop list 1 phan tu
            head = null;
            tail = null;
        } else {
            head = currentNode.nextNode;
            tail.nextNode = head;
        }
        size--;
        return currentNode.value;
    }

    @Override
    public E peek() {
        return null;
    }

    public void traverse() {

        Node<E> currentNode = head;

        if (head != null) {
            do {
                logger.info(currentNode.value + " ");
                currentNode = currentNode.nextNode;
            } while (currentNode != head);
        }
    }

    @Override
    public Iterator<E> iterator() {
        return new Itr();
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return Objects.isNull(head);
    }

    public boolean contains(Object o) {
        Node<E> currentNode = head;

        if (head == null) {
            return false;
        } else {
            do {
                if (currentNode.value == o) {
                    return true;
                }
                currentNode = currentNode.nextNode;
            } while (currentNode != head);
            return false;
        }

    }

    public void delete(E valueToDelete) {
        Node<E> currentNode = head;
        if (head == null) {
            return;
        }
        do {
            Node<E> nextNode = currentNode.nextNode;
            if (nextNode.value == valueToDelete) {
                if (tail == head) {
                    head = null;
                    tail = null;
                } else {
                    currentNode.nextNode = nextNode.nextNode;
                    if (head == nextNode) {
                        head = head.nextNode;
                    }
                    if (tail == nextNode) {
                        tail = currentNode;
                    }
                }
                size--;
                break;
            }
            currentNode = nextNode;
        } while (currentNode != head);
    }

    public static class Node<E> {

        E value;
        Node<E> nextNode;

        public Node(E value) {
            this.value = value;
        }

    }

    private class Itr implements Iterator<E> {
        Node<E> cursor;

        Itr(){
            cursor = CircularQueue.this.head;
        }

        public boolean hasNext() {
            return this.cursor != null && this.cursor.nextNode!=null && this.cursor != CircularQueue.this.tail;
        }

        public E next() {
            Node<E> i = this.cursor;
            if (i==null) {
                throw new NoSuchElementException();
            } else {
                E v = this.cursor.value;
                this.cursor = this.cursor.nextNode;
                return v;
            }
        }
    }
}