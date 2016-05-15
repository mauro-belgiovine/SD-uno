package overlay;


import java.util.*;

import net.RemoteGame;
import net.Server;

import java.net.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class Node {
    String uuid;
    Node prevNode, nextNode;
    String ip;

    public Node() {
        this.uuid = UUID.randomUUID().toString();
        //net = new HashMap <String,Node>  () ;

        try {
            ip = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    public String getUuid() {
        return this.uuid;
    }

    public String getIp() {
        return this.ip;
    }

    public void registry() {

    }

    public void bind() {

    }

    public void setNext(Node node) {
        this.nextNode = node;
    }

    public void setPrev(Node node) {
        this.prevNode = node;
    }
}
