/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.model;

import com.google.gson.Gson;
import javax.websocket.EncodeException;
import javax.websocket.Encoder;
import javax.websocket.EndpointConfig;

/**
 *  Transforma objecto em String para enviar objeto pelo socket 
 * 
 * http://javabeat.net/encoder-websocket-in-javaee-7/
 * 
 * @author leandro.prates
 */
public class MessageEncoder implements Encoder.Text<Mensagem>{

    @Override
    public String encode(Mensagem mensagem) throws EncodeException {
        Gson gson = new Gson();
        return gson.toJson(mensagem);
    }

    @Override
    public void init(EndpointConfig config) {
    }

    @Override
    public void destroy() {
    }
    
    
    
}
