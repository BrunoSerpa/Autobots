package com.autobots.automanager.controles;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "Inicio", description = "Tela Inicial com HTML enriquecido")
public class InicioControle {
    @Value("${server.port}")
    private String port;

    @Value("${app.version:1.0.0}")
    private String appVersion;

    @Operation(summary = "Página inicial", description = "Página com link para o Swagger e a versão.")
    @GetMapping(path = "/", produces = MediaType.TEXT_HTML_VALUE)
    public ResponseEntity<String> getInicioHtml() {
        String baseUrl = String.format("http://localhost:%s", port);

        String html = """
                    <!DOCTYPE html>
                    <html lang="pt-BR">
                        <head>
                        <meta charset="UTF-8">
                        <meta name="viewport" content="width=device-width, initial-scale=1.0">
                        <title>API - Tela Inicial</title>
                        <style>
                            body { font-family: Arial, sans-serif; margin: 2em; }
                            h1 { color: #2C3E50; }
                            ul { list-style: none; padding: 0; }
                            li { margin: 0.5em 0; }
                            a { color: #1F618D; text-decoration: none; }
                            a:hover { text-decoration: underline; }
                            footer     { margin-top: 2em; font-size: 0.9em; color: #7F8C8D; }
                        </style>
                        </head>
                        <body>
                        <h1>Bem-vindo às APIs operacionais!</h1>
                        <ul>
                            <li><a href="%s/swagger">🔗 Swagger</a></li>
                        </ul>
                        <footer>
                            Versão: %s<br/>
                        </footer>
                        </body>
                    </html>
                """.formatted(baseUrl, appVersion);

        return ResponseEntity
                .ok()
                .contentType(MediaType.TEXT_HTML)
                .body(html);
    }
}
