package com.apm.expenses.service;

import com.apm.expenses.dao.StatementDetailsDao;
import com.apm.expenses.dto.BankStatementDetailsDto;
import com.apm.expenses.dto.TotalDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.itextpdf.text.Document;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.PdfWriter;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ExpensesService {

    @Autowired
    StatementDetailsDao statementDetailsDao;

    @Autowired
    StatementService statementService;

    public List<TotalDto> calculateExpenses(String userId, LocalDate fromDate, LocalDate toDate) {
        //TODO
        //1. Fetch statements
        //2. Calculate total for category and subcategory
        try{
            List<BankStatementDetailsDto> bankStatementDetailsDtoList = statementService.getStatement(userId,fromDate,toDate);
            Map<String,List<TotalDto>> totalMap = new HashMap<>();
            List<TotalDto> totalDtoList = new ArrayList<>();
            for (BankStatementDetailsDto bankStatementDetailsDto : bankStatementDetailsDtoList) {
                addOrUpdateTotal(totalMap, bankStatementDetailsDto.getCategory(), bankStatementDetailsDto.getDebitAmount(), "Category",bankStatementDetailsDto.getExpenseMonth());
                addOrUpdateTotal(totalMap, bankStatementDetailsDto.getSubCategory(), bankStatementDetailsDto.getDebitAmount(), "SubCategory", bankStatementDetailsDto.getExpenseMonth());
            }
            ObjectMapper objectMapper  = new ObjectMapper();
            String jsonString = objectMapper.writeValueAsString(totalDtoList);
            FileOutputStream fileOutputStream = new FileOutputStream("expenses.json");
            fileOutputStream.write(jsonString.getBytes());
            fileOutputStream.close();
            System.out.println(totalDtoList);
            createPdf(totalDtoList);
            return totalDtoList;
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    private void createPdf(List<TotalDto> totalDtoList) {
        //TODO
        //1. Create Category Chart
        JFreeChart chart1 = createChart(totalDtoList,"Category");
        JFreeChart chart2 = createChart(totalDtoList,"SubCategory");
        //2. Create SubCategory Chart
        // 3. Export them in PDF.

        Document document = new Document();
        try{
            PdfWriter.getInstance(document, new FileOutputStream("Expenses.pdf"));
            document.open();
            addChartToPDF(chart1,document);
            addChartToPDF(chart2,document);
            document.close();
            System.out.println("PDF Created");
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    private void addChartToPDF(JFreeChart chart, Document document) {
        int width = 700;
        int height = 500;
        try{
            BufferedImage chartImage = chart.createBufferedImage(width, height);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ImageIO.write(chartImage, "png", byteArrayOutputStream);

            Image image = Image.getInstance(byteArrayOutputStream.toByteArray());
            document.add(image);
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    private JFreeChart createChart(List<TotalDto> totalDtoList, String name) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (TotalDto totalDto : totalDtoList) {
            if (totalDto.getType().equals(name)){
                dataset.addValue(totalDto.getAmount(),totalDto.getName(),totalDto.getName());
            }
        }
        JFreeChart chart =  ChartFactory.createBarChart(name,name,"Amount",dataset, PlotOrientation.VERTICAL,true,true,true);
        CategoryPlot plot = chart.getCategoryPlot();
        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        renderer.setDefaultItemLabelGenerator( new StandardCategoryItemLabelGenerator());
        renderer.setDefaultItemLabelsVisible(true);
        return chart;
    }

    private void addOrUpdateTotal(Map<String,List<TotalDto>> totalDtoMap, String name, double amount, String type, String expenseMonth) {
        if (totalDtoMap.containsKey(expenseMonth)) {
            List<TotalDto> totalDtoList = totalDtoMap.get(expenseMonth);
            for (TotalDto totalDto : totalDtoList) {
                if (totalDto.getName().equals(name)) {
                    totalDto.setAmount(totalDto.getAmount()+amount);
                    return;
                }
            }
            totalDtoList.add(TotalDto.builder().name(name).amount(amount).type(type).build());
        }
        else{
            totalDtoMap.put(expenseMonth,List.of(TotalDto.builder().name(name).amount(amount).type(type).build()));
        }

    }
}

