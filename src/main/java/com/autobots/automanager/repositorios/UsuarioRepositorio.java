package com.autobots.automanager.repositorios;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.autobots.automanager.entidades.Usuario;

public interface UsuarioRepositorio extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findOneByTelefonesId(Long telefoneId);
    Optional<Usuario> findOneByDocumentosId(Long documentoId);
    Optional<Usuario> findOneByEnderecoId(Long documentoId);
}