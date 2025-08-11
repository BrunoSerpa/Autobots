package com.autobots.automanager.configuracao;

import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition; 
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.servers.Server;

@Configuration
@OpenAPIDefinition(
  info = @Info(
    title       = "Rotas API",
    version     = "Atividade 1",
    description = "Projeto de Programação Web III",
    contact     = @Contact(
      name  = "Bruno Serpa Pereira Carvalho",
      email = "brunospc2005@gmail.com",
      url   = "http://brunoserpa.vercel.app/"
    ),
    license = @License(
      name = "MIT License",
      url  = "https://opensource.org/license/mit/"
    )
  ),
  servers = {
    @Server(
      description = "Local",
      url         = "http://localhost:${server.port}"
    )
  }
)
public class ApiDefinicoesConfiguracao { }
