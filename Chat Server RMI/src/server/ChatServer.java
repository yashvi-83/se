package server;

import java.rmi.RemoteException;


import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import client.InterfaceClient;
import java.util.List;
import java.util.Vector;


public class ChatServer extends UnicastRemoteObject implements InterfaceServer{
    private final ArrayList<InterfaceClient> clients; //list of connected clients 
    private final ArrayList<InterfaceClient> blockedClients; //list of blocked clients
    
    //constructor
    public ChatServer() throws RemoteException{
        super();
        this.clients = new ArrayList<>();
        blockedClients = new ArrayList<>();
    }
    
    //Function to broadcast message to all connected clients or direct message to particular client
    @Override
    public synchronized void broadcastMessage(String message,List<String> list) throws RemoteException {
        if(list.isEmpty()){
            int i= 0;
            while (i < clients.size()){
                clients.get(i++).retrieveMessage(message);
            }
        }else{
            for (InterfaceClient client : clients) {
                for(int i=0;i<list.size();i++){
                    if(client.getName().equals(list.get(i))){
                        client.retrieveMessage(message);
                    }
                }
            }
        }
    }
    
    //Function to broadcast message to all connected clients or direct message to particular client
    @Override
    public synchronized void broadcastMessage(ArrayList<Integer> inc, List<String> list,String filename) throws RemoteException {
        if(list.isEmpty()){
            int i= 0;
            while (i < clients.size()){
                clients.get(i++).retrieveMessage(filename,inc);
            }
        }else{
            for (InterfaceClient client : clients) {
                for(int i=0;i<list.size();i++){
                    if(client.getName().equals(list.get(i))){
                        client.retrieveMessage(filename,inc);
                    }
                }
            }
        }
    }
        
    //Function to add new client to list of clients on server
    @Override
    public synchronized void addClient(InterfaceClient client) throws RemoteException {
        this.clients.add(client);
    }
    
    //Function to get connected clients' names
    @Override
    public synchronized Vector<String> getListClientByName(String name) throws RemoteException {
        Vector<String> list = new Vector<>();
        for (InterfaceClient client : clients) {
            if(!client.getName().equals(name)){
                list.add(client.getName());
            }
        }
        return list;
    }
    
    //Function to block a client from sending message, but can receive messages
    @Override
    public synchronized void blockClient(List<String> clients){
        for(int j=0;j<this.clients.size();j++){
            for(int i=0;i<clients.size();i++){
                try {
                    if(this.clients.get(j).getName().equals(clients.get(i))){
                        this.clients.get(j).closeChat(clients + " you are blocked by admin");
                        blockedClients.add(this.clients.get(j));
                    }
                } catch (RemoteException ex) {
                    System.out.println("Error: " + ex.getMessage());
                }
            }
        }
    }
    
    //Function to remove a list of clients
    @Override
    public synchronized void removeClient(List<String> clients){
        for(int j=0;j<this.clients.size();j++){
            for(int i=0;i<clients.size();i++){
                try {
                    if(this.clients.get(j).getName().equals(clients.get(i))){
                        this.clients.get(j).closeChat(clients.get(i) + " you are removed from the chat");
                        this.clients.remove(j);
                    }
                } catch (RemoteException ex) {
                    System.out.println("Error: " + ex.getMessage());
                }
            }
        }
    }
    
    //Function to remove one client only
    @Override
    public synchronized void removeClient(String clients){
        for(int j=0;j<this.clients.size();j++){
            try {
                if(this.clients.get(j).getName().equals(clients)){
                    this.clients.remove(j);
                }
            } catch (RemoteException ex) {
                System.out.println("Error: " + ex.getMessage());
            }
        }
    }

    //Function to activate a client after being "blocked"
    @Override
    public synchronized void reactiveClient(List<String> clients) throws RemoteException {
        for(int j=0;j<this.blockedClients.size();j++){
            for(int i=0;i<clients.size();i++){
                try {
                    if(this.blockedClients.get(j).getName().equals(clients.get(i))){
                        this.blockedClients.get(j).openChat();
                        this.blockedClients.remove(j);
                    }
                } catch (RemoteException ex) {
                    System.out.println("Error: " + ex.getMessage());
                }
            }
        }
    }
    //Function to check if a username already exists on the server
    @Override
    public boolean checkUsername(String username) throws RemoteException {
        boolean exist = false;
        for(int i=0;i<clients.size();i++){
            if(clients.get(i).getName().equals(username)){
                exist = true;
            }
        }
        for(int i=0;i<blockedClients.size();i++){
            if(blockedClients.get(i).getName().equals(username)){
                exist = true;
            }
        }
        return exist;
    }
}
