package curso.springboot.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import curso.springboot.model.Pessoa;
import curso.springboot.model.Telefone;
import curso.springboot.repository.PessoaRepository;
import curso.springboot.repository.TelefoneRepository;

@Controller
public class PessoaController {
	
	@Autowired
	private PessoaRepository pessoaRepository;
	
	@Autowired
	private TelefoneRepository telefoneRepository;
	
	@RequestMapping(method = RequestMethod.GET, value = "/cadastropessoa")
	public ModelAndView inicio() {
		
		ModelAndView andView = new ModelAndView("cadastro/cadastropessoa");
		andView.addObject("pessoaobj", new Pessoa());
		
		Iterable<Pessoa> pessoaIt = pessoaRepository.findAll();
		andView.addObject("pessoas", pessoaIt);
		
		
		return andView;
	}
	
	@RequestMapping(method = RequestMethod.POST, value = "**/salvarpessoa")
	public ModelAndView salvar(@Valid Pessoa pessoa, BindingResult bindingResult) {
		
		pessoa.setTelefones(telefoneRepository.getTelefones(pessoa.getId()));
		
		if(bindingResult.hasErrors()) {
			ModelAndView andView = new ModelAndView("cadastro/cadastropessoa");
			Iterable<Pessoa> pessoaIt = pessoaRepository.findAll();
			andView.addObject("pessoas", pessoaIt);
			andView.addObject("pessoaobj", pessoa);
			
			List<String> msg = new ArrayList<String>();
			
			for (ObjectError objetoError : bindingResult.getAllErrors()) {
				msg.add(objetoError.getDefaultMessage()); // vem das anotações da classe @NotNull etc
			}
			
			andView.addObject("msg", msg);
			
			return andView;
		}
		
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
	
	@GetMapping("/telefones/{idpessoa}")
	public ModelAndView telefones(@PathVariable("idpessoa") Long idpessoa) {
		ModelAndView mv = new ModelAndView("cadastro/telefone");
		Optional<Pessoa> p = pessoaRepository.findById(idpessoa);
		mv.addObject("pessoaobj", p.get());
		mv.addObject("telefones", telefoneRepository.getTelefones(idpessoa));
		return mv;
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
	
	@PostMapping("**/addfonePessoa/{pessoaid}")
	public ModelAndView addfonePessoa(Telefone telefone, @PathVariable("pessoaid") Long pessoaid) {
		ModelAndView mv = new ModelAndView("cadastro/telefone");
		
		Pessoa pessoa = pessoaRepository.findById(pessoaid).get();
		
		if(telefone != null && telefone.getNumero().isEmpty() || telefone.getTipo().isEmpty()) {
			
			List<Telefone> listaTelefones = telefoneRepository.getTelefones(pessoaid);
			
			mv.addObject("pessoaobj", pessoa);
			mv.addObject("telefones", listaTelefones);
			
			List<String> msg = new ArrayList<>();
			
			if(telefone.getNumero().isEmpty()) {
				msg.add("O número deve ser informado!");
			}
			if(telefone.getTipo().isEmpty()) {
				msg.add("O tipo deve ser informado!");
			}
			
			
			
			mv.addObject("msg", msg);
			
			return mv;
			
			
		}
		
		telefone.setPessoa(pessoa);
		telefoneRepository.save(telefone);
		List<Telefone> listaTelefones = telefoneRepository.getTelefones(pessoaid);
		
		mv.addObject("pessoaobj", pessoa);
		mv.addObject("telefones", listaTelefones);
		
		return mv;
	}
	
	@GetMapping("**/excluirTelefone/{idtelefone}")
	public ModelAndView excluirTelefone(@PathVariable("idtelefone") Long idtelefone) {
		
		Pessoa p = telefoneRepository.findById(idtelefone).get().getPessoa();
		telefoneRepository.deleteById(idtelefone);
		ModelAndView mv = new ModelAndView("cadastro/telefone");
		mv.addObject("pessoaobj", p);
		mv.addObject("telefones",telefoneRepository.getTelefones(p.getId()));
		
		return mv;
		
	}
}
