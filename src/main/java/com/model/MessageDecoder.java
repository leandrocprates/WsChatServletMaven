/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.model;

import com.google.gson.Gson;
import javax.websocket.DecodeException;
import javax.websocket.Decoder;
import javax.websocket.EndpointConfig;

/**
 *  Faz o decoder de mensagem recebida 
 * 
 * http://javabeat.net/decoder-websocket-javaee-7/
 * 
 * @author leandro.prates
 */
public class MessageDecoder implements Decoder.Text<Mensagem>{

    @Override
    public Mensagem decode(String message) throws DecodeException {
        
        Gson gson = new Gson();
        Mensagem mensagem = gson.fromJson(message, Mensagem.class);
        return mensagem ; 
    }

    @Override
    public boolean willDecode(String arg0) {
        return true;
    }

    @Override
    public void init(EndpointConfig config) {
        
    }

    @Override
    public void destroy() {

    }
    
    
    
    
    
}
