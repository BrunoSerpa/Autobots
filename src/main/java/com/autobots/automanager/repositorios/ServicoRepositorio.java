package com.autobots.automanager.repositorios;

import org.springframework.data.jpa.repository.JpaRepository;

import com.autobots.automanager.entidades.Veiculo;

public interface ServicoRepositorio extends JpaRepository<Veiculo, Long> {
}