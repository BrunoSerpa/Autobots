package com.autobots.automanager.controles;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.autobots.automanager.entidades.Empresa;
import com.autobots.automanager.entidades.Telefone;
import com.autobots.automanager.entidades.Usuario;
import com.autobots.automanager.modelos.AdicionadorLinkTelefone;
import com.autobots.automanager.repositorios.EmpresaRepositorio;
import com.autobots.automanager.repositorios.TelefoneRepositorio;
import com.autobots.automanager.repositorios.UsuarioRepositorio;

@RestController
@RequestMapping("/telefone")
public class TelefoneControle {
	
	@Autowired
	private TelefoneRepositorio repositorio;
	@Autowired
	private UsuarioRepositorio UsuarioRepositorio;
	@Autowired
	private EmpresaRepositorio EmpresaRepositorio;
	@Autowired
	private AdicionadorLinkTelefone adicionarLink;
	
	@GetMapping("/buscar")
	public ResponseEntity<List<Telefone>> buscarTelefones(){
		List<Telefone> telefones = repositorio.findAll();
		adicionarLink.adicionarLink(telefones);
		if(!telefones.isEmpty()) {
			for(Telefone telefone: telefones) {
				adicionarLink.adicionarLinkUpdate(telefone);
				adicionarLink.adicionarLinkDelete(telefone);
			}
		}
		return new ResponseEntity<List<Telefone>>(telefones,HttpStatus.FOUND);
	}
	
	@GetMapping("/buscar/{id}")
	public ResponseEntity<Telefone> buscarTelefone(@PathVariable Long id){
		Telefone telefone = repositorio.findById(id).orElse(null);
		HttpStatus status = null;
		if(telefone == null) {
			status = HttpStatus.NOT_FOUND;
		}else {
			adicionarLink.adicionarLink(telefone);
			adicionarLink.adicionarLinkUpdate(telefone);
			adicionarLink.adicionarLinkDelete(telefone);
			status = HttpStatus.FOUND;
		}
		return new ResponseEntity<Telefone>(telefone,status);
	}
	
	@PutMapping("/atualizar/{idTelefone}")
	public ResponseEntity<?> atualizarTelefone(@PathVariable Long idTelefone, @RequestBody Telefone dados){
		Telefone telefone = repositorio.findById(idTelefone).orElse(null);
		if(telefone == null) {
			return new ResponseEntity<>("Telefone não econtrado...", HttpStatus.NOT_FOUND);
		}else {
			if(dados != null) {
				if(dados.getDdd() != null) {
					telefone.setDdd(dados.getDdd());
				}
				if(dados.getNumero() != null) {
					telefone.setNumero(dados.getNumero());
				}
				repositorio.save(telefone);
			}
			return new ResponseEntity<>(telefone, HttpStatus.ACCEPTED);
		}
	}
	
	@DeleteMapping("/excluir/{idTelefone}")
	public ResponseEntity<?> excluirTelefone(@PathVariable Long idTelefone){
		Telefone verificacao = repositorio.findById(idTelefone).orElse(null);
		if(verificacao == null) {
			return new ResponseEntity<>("Telefone não econtrado...", HttpStatus.NOT_FOUND);
		}else {
			
			//usuario
			for(Usuario usuario: UsuarioRepositorio.findAll()) {
				if(!usuario.getTelefones().isEmpty()) {
					for(Telefone telefone: usuario.getTelefones()) {
						if(telefone.getId() == idTelefone) {
							usuario.getTelefones().remove(telefone);
							UsuarioRepositorio.save(usuario);
						}
						break;
					}
				}
			}
			
			//empresa
			for(Empresa empresa: EmpresaRepositorio.findAll()) {
				if(!empresa.getTelefones().isEmpty()) {
					for(Telefone telefone: empresa.getTelefones()) {
						if(telefone.getId() == idTelefone) {
							empresa.getTelefones().remove(telefone);
							EmpresaRepositorio.save(empresa);
						}
						break;
					}
				}
			}
			
			return new ResponseEntity<>("Telefone excluido com sucesso...", HttpStatus.ACCEPTED);
		}
	}

}
