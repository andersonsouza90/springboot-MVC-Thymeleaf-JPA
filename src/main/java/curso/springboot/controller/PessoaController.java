package curso.springboot.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import curso.springboot.model.Pessoa;
import curso.springboot.repository.PessoaRepository;

@Controller
public class PessoaController {
	
	@Autowired
	private PessoaRepository pessoaRepository;
	
	@RequestMapping(method = RequestMethod.GET, value = "/cadastropessoa")
	public ModelAndView inicio() {
		
		ModelAndView andView = new ModelAndView("cadastro/cadastropessoa");
		andView.addObject("pessoaobj", new Pessoa());
		
		Iterable<Pessoa> pessoaIt = pessoaRepository.findAll();
		andView.addObject("pessoas", pessoaIt);
		
		
		return andView;
	}
	
	@RequestMapping(method = RequestMethod.POST, value = "**/salvarpessoa")
	public ModelAndView salvar(Pessoa pessoa) {
		pessoaRepository.save(pessoa);
		
		ModelAndView andView = new ModelAndView("cadastro/cadastropessoa");
		Iterable<Pessoa> pessoaIt = pessoaRepository.findAll();
		andView.addObject("pessoas", pessoaIt);
		andView.addObject("pessoaobj", new Pessoa());
		
		return andView;
	}
	
	@RequestMapping(method = RequestMethod.GET, value = "/listapessoas")
	public ModelAndView pessoas() {
		ModelAndView andView = new ModelAndView("cadastro/cadastropessoa");
		Iterable<Pessoa> pessoaIt = pessoaRepository.findAll();
		andView.addObject("pessoas", pessoaIt);
		andView.addObject("pessoaobj", new Pessoa());
		return andView;
	}
	
	@GetMapping("/editarpessoa/{idpessoa}")
	public ModelAndView editar(@PathVariable("idpessoa") Long idpessoa) {
		
		ModelAndView modelAndView = new ModelAndView("cadastro/cadastropessoa");
		Optional<Pessoa> p = pessoaRepository.findById(idpessoa);
		modelAndView.addObject("pessoaobj", p.get());
		return modelAndView;
		
	}
	
	@GetMapping("/excluirpessoa/{idpessoa}")
	public ModelAndView excluir(@PathVariable("idpessoa") Long idpessoa) {
		
		ModelAndView MV = new ModelAndView("cadastro/cadastropessoa");
		
		pessoaRepository.deleteById(idpessoa);	
		
		Iterable<Pessoa> p = pessoaRepository.findAll();
		MV.addObject("pessoas", p);
		MV.addObject("pessoaobj", new Pessoa());
		
		return MV;
	}
	
	@PostMapping("**/pesquisarpessoa")
	public ModelAndView pesquisarPorNome(@RequestParam("nomepesquisa") String nomepessoa) {
		ModelAndView MV = new ModelAndView("cadastro/cadastropessoa");
		Iterable<Pessoa> p = pessoaRepository.findPessoaByNome(nomepessoa);
		MV.addObject("pessoas", p);
		MV.addObject("pessoaobj", new Pessoa());
		
		return MV;
	}
	
}
