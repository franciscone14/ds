package pt.ipb.sd;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class PeerGui extends Peer {

    JLabel lblReq;
    JFrame jFrame;
    JPanel panel;
    JButton button;
    JLabel lblState;
    JTextField txtState;
    JLabel lblReplying;
    JTextField txtReplying;
    JLabel lblQueue;
    JLabel lblClock;
    JTextField txtClock;

    JTextArea AckList;
    JTextArea ReqList;
    GridBagConstraints gbc;

    public PeerGui(){
        run();
        gbc = new GridBagConstraints();

        panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("Agrawala Algorithm"),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));

        lblState = new JLabel("Current State: ");
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(5, 1, 5, 1);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(lblState, gbc);

        txtState = new JTextField();
        txtState.setText(String.valueOf(this.getPeerState()));
        txtState.setEnabled(false);
        txtState.setDisabledTextColor(new Color(0,0,0));
        txtState.setColumns(30);
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.insets = new Insets(5, 1, 5, 1);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(txtState, gbc);

        lblReplying = new JLabel("Last replyed to: ");
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.insets = new Insets(5, 1, 5, 1);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(lblReplying, gbc);

        txtReplying = new JTextField();
        txtReplying.setText(getReplying());
        txtReplying.setEnabled(false);
        txtReplying.setDisabledTextColor(new Color(0,0,0));
        txtReplying.setColumns(30);
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.insets = new Insets(5, 1, 5, 1);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(txtReplying, gbc);


        lblQueue = new JLabel("Ack Queue: ");
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.insets = new Insets(5, 1, 5, 1);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(lblQueue, gbc);

        AckList = new JTextArea();
        AckList.setEnabled(false);
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(5, 1, 5, 1);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(AckList, gbc);

        lblReq = new JLabel("Req Queue: ");
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(lblReq, gbc);

        ReqList = new JTextArea();
        ReqList.setEnabled(false);
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(ReqList, gbc);

        lblClock = new JLabel("Logical Clock: ");
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(lblClock, gbc);

        txtClock = new JTextField();
        txtClock.setEnabled(false);
        gbc.gridx = 0;
        gbc.gridy = 7;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(txtClock, gbc);


        button = new JButton("Request");
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                request();
                txtState.setText(String.valueOf(getPeerState()));
            }
        });
        gbc.gridx = 0;
        gbc.gridy = 8;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(5, 5, 5, 5);
        panel.add(button, gbc);

        jFrame = new JFrame("Peer Info:" + getAddress());
        jFrame.setContentPane(panel);
        jFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        jFrame.setVisible(true);
        jFrame.pack();
        setArrays();
    }

    public void setArrays(){
        String txt = "";
        for (int i = 0; i < AckQueue.size(); i++){
            txt += AckQueue.get(i).toString() + "\n";
        }
        AckList.setText(txt);

        txt = "";
        for (int i = 0; i < ReqQueue.size(); i++){
            txt += ReqQueue.get(i).toString() + "\n";
        }
        ReqList.setText(txt);
    }

    @Override
    public void update() {
        setArrays();
        txtClock.setText(String.valueOf(this.getPeerData().getTimestamp()));
        txtState.setText(String.valueOf(this.getPeerState()));
        txtReplying.setText(getReplying());

        if(getPeerState() == State.inCriticalSection){
            button.setEnabled(false);
            txtState.setDisabledTextColor(new Color(255,0,0));
        }
        else if(getPeerState() == State.ready){
            button.setEnabled(true);
            txtState.setDisabledTextColor(new Color(0,255,0));
        }
        else if(getPeerState() == State.waiting){
            button.setEnabled(true);
            txtState.setDisabledTextColor(new Color(0,0,255));
        }
        jFrame.repaint();
    }

    public static void main(String[] args) {
        new PeerGui();
    }

}
