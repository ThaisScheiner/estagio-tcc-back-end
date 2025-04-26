package com.projeto.estagio_tcc.repository;

import com.projeto.estagio_tcc.model.Aluno;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AlunoRepository extends JpaRepository<Aluno, String> {

    long countByOrientadorIsNotNullAndOrientadorNotAndTemaIsNotNullAndTemaNot(String orientador, String tema);
    List<Aluno> findByNomeContainingIgnoreCase(String nome);
    List<Aluno> findByEquipeContainingIgnoreCase(String equipe);
    List<Aluno> findByAno(int ano);


}
