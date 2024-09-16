package com.astier.bts.client_tcp_prof;

import com.astier.bts.client_tcp_prof.tcp.TCP;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.TextArea;
import javafx.scene.shape.Circle;
import javafx.stage.WindowEvent;

import java.io.IOException;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ResourceBundle;

import static javafx.scene.paint.Color.*;

public class HelloController implements Initializable {
    public Button button;
    public Button connecter;
    public Button deconnecter;

    public TextField TextFieldIP;
    public TextField TextFieldPort;
    public TextField TextFieldRequette;
    public Circle voyant;
    public TextArea TextAreaReponses;
    static public TCP tcp;
    static boolean enRun = false;
    String adresse, port;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        voyant.setFill(RED);



        connecter.setOnAction(event -> {
            try {
                connecter();
            } catch (UnknownHostException e) {
                TextAreaReponses.appendText("Erreur de connexion : " + e.getMessage() + "\n");
            }
        });

        deconnecter.setOnAction(event -> {
            try {
                deconnecter();
            } catch (InterruptedException e) {
                TextAreaReponses.appendText("Erreur lors de la déconnexion : " + e.getMessage() + "\n");
            }
            fermerServer();

        });



        button.setOnAction(event -> {
            try {
                envoyer();
            } catch (InterruptedException e) {
                TextAreaReponses.appendText("Erreur lors de l'envoi : " + e.getMessage() + "\n");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

    }




    private void envoyer() throws InterruptedException, IOException {
        String requette = TextFieldRequette.getText();
        if (enRun && !requette.isEmpty()) {
            tcp.requette(requette);
            TextAreaReponses.appendText("Requette envoyee : "+requette+"\n");
        } else {
            TextAreaReponses.appendText("Connexion non établie ou requête vide.\n");
        }
    }


    private void deconnecter() throws InterruptedException {
        if (enRun) {
            try {
                tcp.deconnection();
                enRun = false;
                voyant.setFill(RED);
                TextAreaReponses.appendText("Déconnecté du serveur.\n");
            } catch (InterruptedException e) {
                TextAreaReponses.appendText("Erreur lors de la déconnexion : " + e.getMessage() + "\n");
            }
        }
    }


    private void connecter() throws UnknownHostException {
         adresse = TextFieldIP.getText();
         port = TextFieldPort.getText();
        if (!adresse.isEmpty() && !port.isEmpty()) {
            try {
                tcp = new TCP(InetAddress.getByName(adresse), Integer.parseInt(port), this);
                tcp.connection();
                enRun = true;
                voyant.setFill(GREEN);
                TextAreaReponses.appendText("Connecté à " + adresse + ":" + port + "\n");
            } catch (Exception e) {
                TextAreaReponses.appendText("Erreur lors de la connexion : " + e.getMessage() + "\n");
                voyant.setFill(RED);
            }
        } else {
            TextAreaReponses.appendText("Veuillez saisir une adresse IP et un port.\n");
        }
    }
    private void fermerServer(){
        if (enRun){
            try {
                tcp.deconnection();
                enRun =false;
                voyant.setFill(RED);
                TextAreaReponses.appendText("Deconnecter du Serveur."+"\n");

            } catch (InterruptedException e) {
                TextAreaReponses.appendText("Erreur de la deconnexion"+"\n");
            }
        }
    }
}
