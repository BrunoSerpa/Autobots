package com.autobots.automanager.controles;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.autobots.automanager.entidades.Credencial;
import com.autobots.automanager.entidades.CredencialCodigoBarra;
import com.autobots.automanager.entidades.CredencialUsuarioSenha;
import com.autobots.automanager.entidades.Usuario;
import com.autobots.automanager.modelos.AdicionadorLinkCredencialCodigoDeBarra;
import com.autobots.automanager.modelos.AdicionarLinkCredencialUsuarioSenha;
import com.autobots.automanager.repositorios.CredencialCodigoBarraRepositorio;
import com.autobots.automanager.repositorios.CredencialUsuarioSenhaRepositorio;
import com.autobots.automanager.repositorios.UsuarioRepositorio;

@RestController
@RequestMapping("/credencial")
public class CredencialControle {
	
	@Autowired
	private CredencialUsuarioSenhaRepositorio CredencialUsuarioSenhaRepositorio;
	@Autowired
	private CredencialCodigoBarraRepositorio CredencialCodigoBarraRepositorio;
	@Autowired
	private UsuarioRepositorio UsuarioRepositorio;
	@Autowired
	private AdicionarLinkCredencialUsuarioSenha adicionarLinkCredencialUserSenha;
	@Autowired
	private AdicionadorLinkCredencialCodigoDeBarra adicionarLinkCredencialCodigoDeBarra;
	
	@GetMapping("/buscar-credencial-user-senha")
	public ResponseEntity<?> buscarCredenciaisUsuariosSenhas(){
		List<CredencialUsuarioSenha> credenciais = CredencialUsuarioSenhaRepositorio.findAll();
		if(!credenciais.isEmpty()) {
			adicionarLinkCredencialUserSenha.adicionarLink(credenciais);
			for(CredencialUsuarioSenha credencial: credenciais) {
				adicionarLinkCredencialUserSenha.adicionarLinkUpdate(credencial);
				adicionarLinkCredencialUserSenha.adicionarLinkDelete(credencial);
			}
			return new ResponseEntity<List<CredencialUsuarioSenha>>(credenciais, HttpStatus.FOUND);
		}else {
			return new ResponseEntity<String>("Nenhuma credencial encontrada...", HttpStatus.NOT_FOUND);
		}
	}
	
	@GetMapping("/buscar-credencial-user-senha/{id}")
	public ResponseEntity<?> buscarCredencialUsuarioSenhaPorId(@PathVariable Long id){
		CredencialUsuarioSenha credencial = CredencialUsuarioSenhaRepositorio.findById(id).orElse(null);
		if(credencial == null) {
			return new ResponseEntity<String>("credencial não encontrada...", HttpStatus.NOT_FOUND);
		}else {
			adicionarLinkCredencialUserSenha.adicionarLink(credencial);
			adicionarLinkCredencialUserSenha.adicionarLinkUpdate(credencial);
			adicionarLinkCredencialUserSenha.adicionarLinkDelete(credencial);
			return new ResponseEntity<CredencialUsuarioSenha>(credencial, HttpStatus.FOUND);
		}
	}
	
	@GetMapping("/buscar-username")
	public ResponseEntity<?> buscarCredencialUsuarioSenhaPorNomeUsuario(@RequestBody CredencialUsuarioSenha dados){
		List<CredencialUsuarioSenha> credenciais = CredencialUsuarioSenhaRepositorio.findAll();
		CredencialUsuarioSenha credencial = null;
		for( CredencialUsuarioSenha c:credenciais) {
			if(c.getNomeUsuario().equals(dados.getNomeUsuario())) {
				credencial = c;
			}
		}
		if(credencial == null) {
			return new ResponseEntity<String>("credencial não encontrada...", HttpStatus.NOT_FOUND);
		}else {
			adicionarLinkCredencialUserSenha.adicionarLink(credencial);
			adicionarLinkCredencialUserSenha.adicionarLinkUpdate(credencial);
			adicionarLinkCredencialUserSenha.adicionarLinkDelete(credencial);
			return new ResponseEntity<CredencialUsuarioSenha>(credencial, HttpStatus.FOUND);
		}
	}
	
	@PostMapping("/cadastrar/{idUsuario}")
	public ResponseEntity<?> cadastrarCredencialUserSenha(@RequestBody CredencialUsuarioSenha dados, @PathVariable Long idUsuario){
		Usuario usuario = UsuarioRepositorio.findById(idUsuario).orElse(null);
		if(usuario == null) {
			return new ResponseEntity<String>("Usuario não encontrado...",HttpStatus.NOT_FOUND);
		}else {
			List<CredencialUsuarioSenha> credenciais = CredencialUsuarioSenhaRepositorio.findAll();
			Boolean verificador = false;
			for(CredencialUsuarioSenha credencial: credenciais) {
				if(dados.getNomeUsuario().equals(credencial.getNomeUsuario())) {
					verificador = true;
				}
			}
			if (verificador == true) {
				return new ResponseEntity<String>("Credencial ja existente...",HttpStatus.CONFLICT);
			}else {
				dados.setCriacao(new Date());
				usuario.getCredenciais().add(dados);
				UsuarioRepositorio.save(usuario);
				return new ResponseEntity<Usuario>(usuario,HttpStatus.CREATED);
			}
		}
	}
	
	@PutMapping("/atualizar/{idCredencial}")
	public ResponseEntity<?> atualizarCredencialUserSenha(@PathVariable Long idCredencial, @RequestBody CredencialUsuarioSenha dados){
		CredencialUsuarioSenha credencial = CredencialUsuarioSenhaRepositorio.findById(idCredencial).orElse(null);
		if(credencial == null) {
			return new ResponseEntity<String>("credencial não encontrada...", HttpStatus.NOT_FOUND);
		}else {
			if(dados != null) {
				if(dados.getNomeUsuario() != null) {
					credencial.setNomeUsuario(dados.getNomeUsuario());
				}
				if(dados.getSenha() != null) {
					credencial.setSenha(dados.getSenha());
				}
				CredencialUsuarioSenhaRepositorio.save(credencial);
			}
			return new ResponseEntity<>(credencial, HttpStatus.ACCEPTED);
		}
	}
	
	@DeleteMapping("/excluir/{idCredencial}")
	public ResponseEntity<?> excluirCredencialUserSenha(@PathVariable Long idCredencial){
		CredencialUsuarioSenha verificacao = CredencialUsuarioSenhaRepositorio.findById(idCredencial).orElse(null);
		if(verificacao == null) {
			return new ResponseEntity<String>("credencial não encontrada...", HttpStatus.NOT_FOUND);
		}else {
			
			for(Usuario usuario:UsuarioRepositorio.findAll()) {
				if(!usuario.getCredenciais().isEmpty()) {
					for(Credencial credencial: usuario.getCredenciais()) {
						if(credencial.getId() == idCredencial) {
							usuario.getCredenciais().remove(credencial);
							UsuarioRepositorio.save(usuario);
							break;
						}
					}
				}
			}
			
			return new ResponseEntity<>("Credencial excluida com sucesso...", HttpStatus.ACCEPTED);
		}
	}
	
	
	//CREDENCIAL CODIGO DE BARRAS
	
	@GetMapping("/buscar-codigo-barra")
	public ResponseEntity<?> buscarCredenciaisCodigoBarras(){
		List<CredencialCodigoBarra> credenciais = CredencialCodigoBarraRepositorio.findAll();
		adicionarLinkCredencialCodigoDeBarra.adicionarLink(credenciais);
		for(CredencialCodigoBarra credencial: credenciais) {
			adicionarLinkCredencialCodigoDeBarra.adicionarLinkUpdate(credencial);
			adicionarLinkCredencialCodigoDeBarra.adicionarLinkDelete(credencial);
		}
		return new ResponseEntity<List<CredencialCodigoBarra>>(credenciais, HttpStatus.FOUND);
	}

	@GetMapping("/buscar-codigo-barra/{id}")
	public ResponseEntity<?> buscarCredencialCodigoBarraPorId(@PathVariable Long id){
		CredencialCodigoBarra credencial = CredencialCodigoBarraRepositorio.findById(id).orElse(null);
		if(credencial == null) {
			return new ResponseEntity<String>("credencial não encontrada...", HttpStatus.NOT_FOUND);
		}else {
			adicionarLinkCredencialCodigoDeBarra.adicionarLink(credencial);
			adicionarLinkCredencialCodigoDeBarra.adicionarLinkUpdate(credencial);
			adicionarLinkCredencialCodigoDeBarra.adicionarLinkDelete(credencial);
			return new ResponseEntity<CredencialCodigoBarra>(credencial, HttpStatus.FOUND);
		}
	}
	
	@PostMapping("/cadastrar-codigo-barra/{idUsuario}")
	public ResponseEntity<?> cadastrarCredencialCodigoBarra(@RequestBody CredencialCodigoBarra dados, @PathVariable Long idUsuario){
		Usuario usuario = UsuarioRepositorio.findById(idUsuario).orElse(null);
		if(usuario == null) {
			return new ResponseEntity<String>("credencial não encontrada...", HttpStatus.NOT_FOUND);
		}else {
			List<CredencialCodigoBarra> credenciais = CredencialCodigoBarraRepositorio.findAll();
			Boolean verificador = false;
			for(CredencialCodigoBarra credencial: credenciais) {
				if(dados.getCodigo() == credencial.getCodigo()) {
					verificador = true;
				}
			}
			if (verificador == true) {
				return new ResponseEntity<String>("Credencial ja existente...",HttpStatus.CONFLICT);
			}else {
				double randomNumero = Math.random();
				dados.setCodigo(randomNumero);
				dados.setCriacao(new Date());
				usuario.getCredenciais().add(dados);
				UsuarioRepositorio.save(usuario);
				return new ResponseEntity<Usuario>(usuario,HttpStatus.CREATED);
			}
		}
	}
	
	@PutMapping("/atualizar-codigo-barra/{idCredencial}")
	public ResponseEntity<?> atualizarCredencialCodigoBarra(@PathVariable Long idCredencial, @RequestBody CredencialCodigoBarra dados){
		CredencialCodigoBarra credencial = CredencialCodigoBarraRepositorio.findById(idCredencial).orElse(null);
		if(credencial == null) {
			return new ResponseEntity<String>("credencial não encontrada...", HttpStatus.NOT_FOUND);
		}else {
			if(dados != null) {
				
				credencial.setCodigo(dados.getCodigo());
				CredencialCodigoBarraRepositorio.save(credencial);
			}
			return new ResponseEntity<>(credencial, HttpStatus.ACCEPTED);
		}
	}
	
	@DeleteMapping("/excluir-codigo-barra/{idCredencial}")
	public ResponseEntity<?> excluirCredencialCodigoBarra(@PathVariable Long idCredencial){
		CredencialCodigoBarra verificacao = CredencialCodigoBarraRepositorio.findById(idCredencial).orElse(null);
		if(verificacao == null) {
			return new ResponseEntity<String>("credencial não encontrada...", HttpStatus.NOT_FOUND);
		}else {
			for(Usuario usuario:UsuarioRepositorio.findAll()) {
				if(!usuario.getCredenciais().isEmpty()) {
					for(Credencial credencial: usuario.getCredenciais()) {
						if(credencial.getId() == idCredencial) {
							usuario.getCredenciais().remove(credencial);
							UsuarioRepositorio.save(usuario);
							break;
						}
					}
				}
			}
			
			return new ResponseEntity<>("Credencial excluida com sucesso...", HttpStatus.ACCEPTED);
		}
	}
}
