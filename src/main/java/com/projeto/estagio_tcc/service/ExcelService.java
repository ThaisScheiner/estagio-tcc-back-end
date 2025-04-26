package com.projeto.estagio_tcc.service;

import com.projeto.estagio_tcc.model.Aluno;
import com.projeto.estagio_tcc.repository.AlunoRepository;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.awt.Color;
import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ExcelService {
    private final AlunoRepository alunoRepository;

    public ExcelService(AlunoRepository alunoRepository) {
        this.alunoRepository = alunoRepository;
    }

    public void importarExcel(MultipartFile file) throws Exception {
        try (InputStream is = file.getInputStream();
             Workbook workbook = new XSSFWorkbook(is)) {

            alunoRepository.deleteAll();

            Sheet sheet = workbook.getSheetAt(0);
            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue;

                Aluno aluno = new Aluno();
                String ra = getCellValue(row.getCell(0));
                aluno.setRa(ra);
                aluno.setAno(extrairAnoDoRa(ra));
                aluno.setNome(getCellValue(row.getCell(2)));
                aluno.setEquipe(getCellValue(row.getCell(3)));
                aluno.setOrientador(getCellValue(row.getCell(4)));
                aluno.setTema(getCellValue(row.getCell(5)));
                aluno.setObservacoes(getCellValue(row.getCell(6)));

                alunoRepository.save(aluno);
            }
        } catch (Exception e) {
            throw new Exception("Erro ao importar: " + e.getMessage(), e);
        }
    }

    public byte[] gerarArquivoExcel() throws IOException {
        List<Aluno> alunos = alunoRepository.findAll();
        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("alunos_tcc_exemplo");

            Row header = sheet.createRow(0);
            String[] colunas = {"RA", "Ano", "Nome", "Equipe", "Orientador", "Tema", "Observações"};
            for (int i = 0; i < colunas.length; i++) {
                header.createCell(i).setCellValue(colunas[i]);
            }

            Map<String, CellStyle> estilosPorEquipe = new HashMap<>();
            String[] cores = {"#D9EAD3", "#FCE5CD", "#CFE2F3", "#F4CCCC", "#D9D2E9", "#FFF2CC", "#EAD1DC"};
            int corIndex = 0;

            int rowIdx = 1;
            for (Aluno aluno : alunos) {
                String equipe = aluno.getEquipe();

                if (!estilosPorEquipe.containsKey(equipe)) {
                    XSSFCellStyle style = (XSSFCellStyle) workbook.createCellStyle();
                    XSSFColor cor = new XSSFColor(Color.decode(cores[corIndex % cores.length]), null);
                    style.setFillForegroundColor(cor);
                    style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                    estilosPorEquipe.put(equipe, style);
                    corIndex++;
                }

                CellStyle estilo = estilosPorEquipe.get(equipe);

                Row row = sheet.createRow(rowIdx++);
                Cell cell0 = row.createCell(0); cell0.setCellValue(aluno.getRa()); cell0.setCellStyle(estilo);
                Cell cell1 = row.createCell(1); cell1.setCellValue(aluno.getAno()); cell1.setCellStyle(estilo);
                Cell cell2 = row.createCell(2); cell2.setCellValue(aluno.getNome()); cell2.setCellStyle(estilo);
                Cell cell3 = row.createCell(3); cell3.setCellValue(aluno.getEquipe()); cell3.setCellStyle(estilo);
                Cell cell4 = row.createCell(4); cell4.setCellValue(aluno.getOrientador()); cell4.setCellStyle(estilo);
                Cell cell5 = row.createCell(5); cell5.setCellValue(aluno.getTema()); cell5.setCellStyle(estilo);
                Cell cell6 = row.createCell(6); cell6.setCellValue(aluno.getObservacoes()); cell6.setCellStyle(estilo);
            }

            workbook.write(out);
            return out.toByteArray();
        }
    }

    public void exportarParaExcel() throws IOException {
        byte[] dados = gerarArquivoExcel();

        File pasta = new File("arquivos");
        if (!pasta.exists()) {
            pasta.mkdirs();
        }

        try (FileOutputStream fos = new FileOutputStream("arquivos/alunos_tcc_exemplo.xlsx")) {
            fos.write(dados);
        }
    }

    private String getCellValue(Cell cell) {
        if (cell == null) return "";
        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue().trim();
            case NUMERIC -> String.valueOf((int) cell.getNumericCellValue());
            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
            case FORMULA -> cell.getCellFormula();
            default -> "";
        };
    }

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
}
