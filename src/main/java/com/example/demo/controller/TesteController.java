package com.example.demo.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Endpoint simples para confirmar que o Spring Security + JWT está
 * funcionando: só responde 200 quando a requisição traz um token válido
 * (ver "anyRequest().authenticated()" no SecurityConfig). Sem token, ou com
 * token inválido/expirado, retorna 401.
 */
@RestController
@RequestMapping("/test")
public class TesteController {

    @GetMapping
    public String test() {
        return "Testando segurança do CarboFlix!";
    }
}
