/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.wschatservletmaven;

/**
 *
 * @author lprates
 */

import com.google.gson.Gson;
import com.model.Mensagem;
import com.model.MessageDecoder;
import com.model.MessageEncoder;
import java.io.IOException;
import java.util.HashMap;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.AtomicInteger;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;


import util.HtmlFilter;

/**
 * 
 * Exemplo de uso : 
 * 
 * ws://localhost:8080/WsChatServletMaven/wschat/WsChatServlet?usuario={idUsuarioLogado} 
 * 
 * ws://localhost:8080/WsChatServletMaven/wschat/WsChatServlet?usuario=1
 * ws://localhost:8080/WsChatServletMaven/wschat/WsChatServlet?usuario=2
 * 
 * 
 * { "fromIdUsuario" : "1" , "toIdUsuario" : "2" , "mensagem" : "Usuario 1 : Mensagem text ponto a ponto" , "tipo": "MensagemDeTexto" }
 * { "fromIdUsuario" : "2" , "toIdUsuario" : "1" , "mensagem" : "Usuario 2 : Mensagem text ponto a ponto" , "tipo": "MensagemDeTexto" }
 * 
 * 
 * @author leandro.prates
 */





@ServerEndpoint(value = "/wschat/WsChatServlet" , encoders = {MessageEncoder.class} , 
        decoders = {MessageDecoder.class}   )
public class ChatAnnotation {

    

    private static final String GUEST_PREFIX = "Guest";
    private static final AtomicInteger connectionIds = new AtomicInteger(1);
    private static final Set<ChatAnnotation> connections = new CopyOnWriteArraySet<>();
    
    private static final HashMap<String,ChatAnnotation> mapConnections = new HashMap<String,ChatAnnotation>();
    
    //usuario logado 
    //private Usuario usuario;
    private String idUsuario;
    
    
    private final String nickname;
    private Session session;

    public ChatAnnotation() {
        nickname = GUEST_PREFIX + connectionIds.getAndIncrement();
        System.out.println("Construtor ChatAnnotation - NickName : " + nickname);
    }


    @OnOpen
    public void start(Session session) {
        
        this.session = session;
        connections.add(this);
        String message = String.format("* %s %s", nickname, "has joined.");
        
        idUsuario = getIdUsuarioConectado(session.getQueryString());
        //busco usuario no banco de dados e adiciona no mapa
        mapConnections.put(idUsuario, this);

        
        System.out.println("Id Usuario : " + idUsuario);
        System.out.println("Funcao Start : " + message);
        
        //broadcast(message);
        
        Mensagem mensagem = new Mensagem();
        mensagem.setMensagem(message);
        
        broadcastObject(mensagem);
        
    }


    @OnClose
    public void end() {
        
        connections.remove(this);
        mapConnections.remove(this.idUsuario);
        
        
        String message = String.format("* %s %s", nickname, "has disconnected.");
        
        System.out.println("Funcao End : " + message);
        
        //broadcast(message);
        
        Mensagem mensagem = new Mensagem();
        mensagem.setMensagem(message);
        broadcastObject(mensagem);
        
        
    }


    /**
     * Funcao que recebe texto 
     * @param message 
     */
    /*
    @OnMessage
    public void incoming(String message) {
        String filteredMessage = String.format("%s: %s",  nickname, HtmlFilter.filter(message.toString()));
        System.out.println("Funcao Incoming : " + filteredMessage);
        Gson gson = new Gson();
        Mensagem mensagem = gson.fromJson(message, Mensagem.class);
        sendMessageToUser(mensagem);
        //broadcast(filteredMessage);
    }
    */

    /**
     * 
     * @param mensagem 
     */
    @OnMessage
    public void incoming(Mensagem mensagem) {
        String filteredMessage = String.format("%s: %s",  nickname, HtmlFilter.filter( mensagem.getMensagem() ));
        System.out.println("Funcao Incoming : " + filteredMessage);
        sendMessageToUser(mensagem);
    }
    
    

    @OnError
    public void onError(Throwable t) throws Throwable {
        
        System.out.println("Funcao onError : " + t);
                
        System.out.println("Chat Error: " + t.toString() );
    }


    /**
     * Envia mensagem de texto simples 
     * @param msg 
     */
    private static void broadcast(String msg) {
        for (ChatAnnotation client : connections) {
            try {
                synchronized (client) {
                    client.session.getBasicRemote().sendText(msg);
                }
            } catch (IOException e) {
                System.out.println("Chat Error: Failed to send message to client" + e );
                connections.remove(client);
                try {
                    client.session.close();
                } catch (IOException e1) {
                    // Ignore
                }
                String message = String.format("* %s %s",
                        client.nickname, "has been disconnected.");
                broadcast(message);
            }
        }
    }
    
    
    /**
     * Envia mensagem de texto por mei ode objeto
     * @param mensagem 
     */
    private static void broadcastObject(Mensagem mensagem) {
        for (ChatAnnotation client : connections) {
            try {
                synchronized (client) {
                    client.session.getBasicRemote().sendObject(mensagem);
                }
            } catch (IOException e) {
                System.out.println("Chat Error: Failed to send message to client" + e );
                connections.remove(client);
                try {
                    client.session.close();
                } catch (IOException e1) {
                    // Ignore
                }
                String message = String.format("* %s %s",
                        client.nickname, "has been disconnected.");
                broadcast(message);
            } catch (Exception ex){
                
            }
        }
    }
        
    
    
    public void sendMessageToUser(Mensagem mensagem){
        
        try{
            ChatAnnotation chatAnnotation = mapConnections.get(mensagem.getToIdUsuario());
            
            if ( chatAnnotation!= null  ) {
                //chatAnnotation.session.getBasicRemote().sendText(mensagem.getMensagem());
                chatAnnotation.session.getBasicRemote().sendObject(mensagem);
                
            } else {
                System.out.println("Usuario :" +mensagem.getToIdUsuario()+ " esta desconectado.") ; 
            }
            
        }catch (Exception ex){
            System.out.println(ex);
        }
    }
    
    public String getIdUsuarioConectado(String queryString){
        String query=queryString; 
        String queryArray[]=query.split("&");
        String usuario[]=queryArray[0].split("=");
        return usuario[1];
    }
    
    
}