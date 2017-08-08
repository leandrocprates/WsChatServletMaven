/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.model;

import lombok.Data;

/**
 *
 * @author lprates
 */
@Data
public class Mensagem {
    
    private String fromIdUsuario;
    private String toIdUsuario;
    private String mensagem; 
    private String tipo;
    
}
