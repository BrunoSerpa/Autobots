package com.autobots.automanager.repositorios;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.autobots.automanager.entidades.Cliente;

public interface ClienteRepositorio extends JpaRepository<Cliente, Long> {
    Optional<Cliente> findOneByTelefonesId(Long telefoneId);
    Optional<Cliente> findOneByDocumentosId(Long documentoId);
    Optional<Cliente> findOneByEnderecoId(Long documentoId);
}