/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.astier.bts.client_tcp_prof.tcp;


import com.astier.bts.client_tcp_prof.HelloController;
import javafx.application.Platform;
import javafx.scene.paint.Color;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;


import static javafx.scene.paint.Color.RED;

/**
 * @author Michael
 */
public class TCP extends Thread {
    int port;
    InetAddress serveur;
    Socket socket;
    boolean marche = false;
    boolean connection = false;
    PrintStream out;
    BufferedReader in;

    HelloController fxmlCont;

    public TCP() {
    }

    public TCP(InetAddress serveur, int port, HelloController fxmlCont) {
        this.port = port;
        this.serveur = serveur;
        this.fxmlCont = fxmlCont;
        System.out.println("@ serveur: " + serveur + " port: " + port);
    }

    public void connection() {
       try {
           socket = new Socket(serveur,port);
           out =new PrintStream(socket.getOutputStream());
           in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
           marche = true;
           connection = true;

           fxmlCont.voyant.setFill(Color.GREEN);
           fxmlCont.TextAreaReponses.appendText("Connecter au Serveur"+serveur.getHostAddress()+"sur le port "+port+"\n");
           this.start();

       } catch (IOException e) {
           throw new RuntimeException(e);
       }
    }

    public void deconnection() throws InterruptedException {
        marche = false;
        try {
            if (socket !=null && socket.isClosed()) {
                socket.close();
                fxmlCont.voyant.setFill(Color.RED);
                fxmlCont.TextAreaReponses.appendText("Déconecter du Serveur");
            }
        } catch (IOException e) {
            fxmlCont.TextAreaReponses.appendText("Erreur lors de la déconnexion : " + e.getMessage() + "\n");
        }
    }

    public void requette(String laRequette) throws IOException {
        if (connection){
        out.println(laRequette);  // envoi reseau
        System.out.println("la requette " + laRequette);
        }else {
            fxmlCont.TextAreaReponses.appendText("Pas de connextion Serveur:\n");
        }
    }

    public void run() {
        try{
            String messageServer;
        while (marche) {
            messageServer =in.readLine();
            if (messageServer != null) {
                updateMessage(messageServer);

            }else {
                marche = false;

            }

        }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    /*
    Pour déclencher une opération graphique en dehors du thread graphique  utiliser
    javafx.application.Platform.runLater(java.lang.Runnable)
    Cette méthode permet d'éxécuter le code du runnable par le thread graphique de JavaFX.
    */
    protected void updateMessage(String message) {
        Platform.runLater(() -> fxmlCont.TextAreaReponses.appendText("    MESSAGE SERVEUR >  \n      " + message + "\n"));
    }
}