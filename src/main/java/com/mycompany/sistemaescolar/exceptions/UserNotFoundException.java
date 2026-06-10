/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.sistemaescolar.exceptions;

/**
 *
 * @author rafae
 */
public class UserNotFoundException extends Exception {
    public UserNotFoundException(String mensagem) {
       super(mensagem);
    }
}
