package com.projeto.estagio_tcc.controller;

import com.projeto.estagio_tcc.model.Aluno;
import com.projeto.estagio_tcc.repository.AlunoRepository;
import com.projeto.estagio_tcc.service.ExcelService;
import jakarta.transaction.Transactional;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

@RestController
@RequestMapping("/alunos")
@CrossOrigin(origins = "*")
public class AlunoController {

    @Autowired
    private AlunoRepository alunoRepository;

    @Autowired
    private ExcelService excelService;

    // GET /alunos - listar todos
    @GetMapping
    public List<Aluno> listarAlunos() {
        return alunoRepository.findAll();
    }

    // GET /alunos/percentual-orientados - percentual com orientador e tema
    @GetMapping("/percentual-orientados")
    public Map<String, Object> percentualOrientados() {
        long total = alunoRepository.count();
        long orientados = alunoRepository.countByOrientadorIsNotNullAndOrientadorNotAndTemaIsNotNullAndTemaNot("", "");

        double percentual = total > 0 ? ((double) orientados / total) * 100 : 0;

        Map<String, Object> resultado = new HashMap<>();
        resultado.put("total", total);
        resultado.put("orientados", orientados);
        resultado.put("percentual", percentual);
        return resultado;
    }

    // POST /alunos - inserir novo aluno
    @PostMapping
    public ResponseEntity<Aluno> inserirAluno(@RequestBody Aluno aluno) {
        if (alunoRepository.existsById(aluno.getRa())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }

        aluno.setAno(extrairAnoDoRa(aluno.getRa())); // Define o ano a partir do RA
        Aluno salvo = alunoRepository.save(aluno);

        try {
            excelService.exportarParaExcel();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(salvo);
    }

    //Método auxiliar dentro do controller
    private int extrairAnoDoRa(String ra) {
        if (ra != null && ra.length() >= 8) {
            try {
                return Integer.parseInt(ra.substring(6, 8));
            } catch (NumberFormatException e) {
                return 0;
            }
        }
        return 0;
    }


    // PUT /alunos/{ra} - atualizar aluno existente
    @PutMapping("/{ra}")
    public ResponseEntity<Aluno> atualizarAluno(@PathVariable String ra, @RequestBody Aluno alunoAtualizado) {
        Optional<Aluno> alunoExistente = alunoRepository.findById(ra);
        if (alunoExistente.isPresent()) {
            Aluno aluno = alunoExistente.get();
            aluno.setAno(alunoAtualizado.getAno());
            aluno.setNome(alunoAtualizado.getNome());
            aluno.setEquipe(alunoAtualizado.getEquipe());
            aluno.setOrientador(alunoAtualizado.getOrientador());
            aluno.setTema(alunoAtualizado.getTema());
            aluno.setObservacoes(alunoAtualizado.getObservacoes());

            Aluno salvo = alunoRepository.save(aluno);

            try {
                excelService.exportarParaExcel();
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }

            return ResponseEntity.ok(salvo);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/buscar-por-nome")
    public List<Aluno> buscarPorNome(@RequestParam String nome) {
        return alunoRepository.findByNomeContainingIgnoreCase(nome);
    }

    @GetMapping("/buscar-por-equipe")
    public List<Aluno> buscarPorEquipe(@RequestParam String equipe) {
        return alunoRepository.findByEquipeContainingIgnoreCase(equipe);
    }

    @GetMapping("/buscar-por-ano")
    public ResponseEntity<List<Aluno>> buscarPorAno(@RequestParam int ano) {
        List<Aluno> alunos = alunoRepository.findByAno(ano);
        return ResponseEntity.ok(alunos);
    }
    
    // DELETE /alunos/{ra} - deletar aluno
    @DeleteMapping("/{ra}")
    public ResponseEntity<Void> deletarAluno(@PathVariable String ra) {
        if (alunoRepository.existsById(ra)) {
            alunoRepository.deleteById(ra);

            try {
                excelService.exportarParaExcel();
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }

            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // POST /alunos/importar - upload da planilha
    @PostMapping("/importar")
    public ResponseEntity<String> importarExcel(@RequestParam("arquivo") MultipartFile arquivo) {
        try {
            excelService.importarExcel(arquivo);
            return ResponseEntity.ok("Arquivo importado com sucesso.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Erro ao importar: " + e.getMessage());
        }
    }

    // GET /alunos/exportar - download da planilha
    @GetMapping("/exportar")
    public ResponseEntity<byte[]> exportarExcel() {
        try {
            byte[] arquivo = excelService.gerarArquivoExcel();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentDisposition(ContentDisposition.attachment().filename("alunos_tcc_exemplo.xlsx").build());
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);

            return new ResponseEntity<>(arquivo, headers, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}