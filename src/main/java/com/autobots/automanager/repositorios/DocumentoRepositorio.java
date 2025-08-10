package com.autobots.automanager.repositorios;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.autobots.automanager.entidades.Documento;

public interface DocumentoRepositorio extends JpaRepository<Documento, Long> {
    Optional<Documento> findByNumero(String numero);
}