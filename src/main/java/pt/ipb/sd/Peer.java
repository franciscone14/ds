package pt.ipb.sd;

import org.jgroups.*;
import org.jgroups.util.UUID;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Random;


public class Peer extends ReceiverAdapter {
    public enum State {
        ready, waiting, inCriticalSection
    }

    private JChannel channel;
    private PeerData peerData;
    private State peerState;
    private String replying;

    LinkedList<PeerData> ReqQueue;
    ArrayList<PeerData> AckQueue;
    HashMap<PeerData, Address> addrMap;

    public Peer() {
        ReqQueue = new LinkedList<PeerData>();
        AckQueue = new ArrayList<PeerData>();
        addrMap = new HashMap<PeerData, Address>();
        peerState = State.ready;
        replying = "";
    }

    public void run(){
        try{
            channel = new JChannel();
            channel.setReceiver(this);
            channel.connect("Cluster");
            String uid = ((UUID)channel.getAddress()).toStringLong();
//            System.out.println("Erro tv aqui: " + uid);
            peerData = new PeerData(uid);
            addrMap.put(peerData, channel.getAddress());
            //Set the ts according to the node entering in the cluster
            peerData.setTimestamp(channel.getView().getMembers().size() + 1);
        }
        catch (Exception ex){
            System.err.println("Error conecting to cluster");
        }
    }

    public void request(){
        Message msg = new Message(null, this.peerData);
        try{
            channel.send(msg);
            peerData.incrementTimestamp();
        }catch (Exception ex){
            System.err.println("Error sending request message to cluster");;
        }
    }

    private void setState(State state) {
        this.peerState = state;
    }

    public State getPeerState() {
        return peerState;
    }

    public PeerData getPeerData() {
        return peerData;
    }

    public String getReplying() {
        return replying;
    }

    public Address getAddress(){
        return channel.getAddress();
    }

    @Override
    public void viewAccepted(View new_view) {
        System.out.println("** view: " + new_view);
    }

    public void receive(Message msg) {
        peerData.incrementTimestamp();
        PeerData data = (PeerData) msg.getObject();
        addrMap.put(data, msg.getSrc());
        // Synchronize logicalClock
        if (this.peerData.getTimestamp() < data.getTimestamp()) {
            this.peerData.setTimestamp(data.getTimestamp());
        }

        /***
         * If peer is in Critical Section just adds the request data into the Queue
         */
        if(this.peerState == State.inCriticalSection){
            System.out.println("** In critical Section **");
            this.ReqQueue.add(data);
            System.out.println("Add To Req Queue");
        }
        else if(this.peerState == State.ready){
            /**
             * Checks if the msg is a Req or a Ack type.
             * If there's no destination then its a Req
             */
            if(msg.getDest() != null){
                if (!AckQueue.contains(data)) {
                    AckQueue.add(data);
                }
                setState(State.waiting);
            }
            else {
                System.out.println("Replying.....");
                // If is self Peer, change state to waiting and Reply to itself
                if (data.getUid().equals(this.peerData.getUid())) {
                    setState(State.waiting);
                    reply(msg.getSrc());
                } else {
                    // If is another Peer, reply
                    reply(msg.getSrc());
                }
                replying = String.valueOf(msg.getSrc());
            }
        }
        /***
         * Otherwise the state is waiting
         */
        else{
            int numberOfPeers = channel.getView().getMembers().size();
            /***
             * If the first condition is true, then the msg is a request type
             * while the peer is waiting for others reponses
             */
            if(msg.getDest() == null){
                ReqQueue.add(data);
            }
            else{
                if(!AckQueue.contains(data)){
                    AckQueue.add(data);
                }
            }

            /***
             * Check wether the number of peers in the cluster is equal
             * to the AckQueue size, if so, the peer can execute the critical section
             */
            if(numberOfPeers == AckQueue.size()){
                AckQueue.clear();
                setState(State.inCriticalSection);
                update();
                try{
                    Thread.sleep(5000);
                }catch (InterruptedException e){
                    System.err.println("Program interruped while executing thread");
                }
                setState(State.ready);
                update();

                if(!ReqQueue.isEmpty()){
                    while(!ReqQueue.isEmpty()){
                        data = ReqQueue.getFirst();
                        // Obtains Peer with lowest logicalClock
                        for (PeerData item : ReqQueue) {
                            if (item.getTimestamp() < data.getTimestamp()){
                                data = item;
                            }
                        }
                        this.ReqQueue.remove(data);
                        reply(addrMap.get(data));
                    }

                }
            }
        }
        update();
    }

    public void reply(Address sourceAddress){
        try{
            Message message = new Message(sourceAddress, this.peerData);
            peerData.incrementTimestamp();
            channel.send(message);
            update();
        } catch (Exception ex){
            System.err.println("Error in the replying process");
        }
    }

    public void update(){
        System.out.println(
                this.peerData.getUid() + ": " + this.peerState + ", Clock: " + this.peerData.getTimestamp());
    }
}
